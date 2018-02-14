/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridge.axiom;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.stream.XMLReporter;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.genxdm.Cursor;
import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.bridge.axiom.enhanced.AxiomSAProcessingContext;
import org.genxdm.bridgekit.tree.CursorOnModel;
import org.genxdm.bridgekit.tree.MutableCursorOnMutableModel;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.io.Resolver;
import org.genxdm.io.SerializationParams;
import org.genxdm.mutable.MutableContext;
import org.genxdm.mutable.MutableCursor;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;
import org.genxdm.processor.io.DefaultDocumentHandler;
import org.genxdm.xs.SchemaComponentCache;

/** ProcessingContext to support abstraction of the AxiOM LLOM tree model.
 *
 * Axiom isn't kind to XDM's notion of nodes.  Axiom's OMNode is the
 * superinterface for OMElement, OMComment, OMProcessingInstruction,
 * and OMText, but not for OMDocument, OMAttribute, or OMNamespace.
 * OMContainer is the superinterface for OMElement and OMDocument. The most abstract unifier
 * is OMSerializable, which is the superinterface of OMNode and OMContainer.
 * Unfortunately, OMAttribute and OMNamespace are top-level interfaces.
 * This is not a big deal for namespaces; we could advertise no support
 * for the namespace axis (and we do, although for a different reason;
 * there are traces here of implementation of the namespace axis, which
 * were ultimately abandoned not because OMNamespace has no notion of
 * parentage (that's fixable), but because something keeps defining extra
 * namespaces for us (maybe us, being clumsy)).
 * 
 * As a consequence, we're forced to use Object as the representation for
 * N, although in fact it's the union of OMContainer, OMNode, OMAttribute
 * (and arguably OMNamespace).  That is, where Object is passed as the N,
 * it <em>must be</em> OMContainer, OMNode, or OMAttribute; where it is
 * returned, the same is true.
 * 
 * This isn't ideal: it means that we have to do a lot of casting.  But
 * the Axiom stance that attributes are not nodes (are not even serializable)
 * leaves us with little choice.  Interestingly, though it produces a certain
 * amount of clumsiness, it isn't all that horrible.
 *
 */
public class AxiomProcessingContext
    implements ProcessingContext<Object>
{

    public AxiomProcessingContext(final OMFactory factory)
    {
        this.omfactory = factory;
    }
    
    @Override
    public DocumentHandler<Object> newDocumentHandler()
    {
        DocumentHandler<Object> handler = new DefaultDocumentHandler<Object>(this);
        // TODO: should the DocumentHandler interface grow these methods?
        if (resolver != null)
            ((DefaultDocumentHandler<Object>)handler).setResolver(resolver);
        if (reporter != null)
            ((DefaultDocumentHandler<Object>)handler).setReporter(reporter);
        return handler;
    }

    @Override
    public DocumentHandler<Object> newDocumentHandler(XMLReporter aReporter, Resolver aResolver)
    {
        DocumentHandler<Object> handler = new DefaultDocumentHandler<Object>(this);
        // TODO: should supplying a null reporter or resolver override a non-null default reporter or resolver?
        if (aResolver != null)
            ((DefaultDocumentHandler<Object>)handler).setResolver(aResolver);
        else if (resolver != null)
            ((DefaultDocumentHandler<Object>)handler).setResolver(resolver);
        if (aReporter != null)
            ((DefaultDocumentHandler<Object>)handler).setReporter(aReporter);
        else if (reporter != null)
            ((DefaultDocumentHandler<Object>)handler).setReporter(reporter);
        return handler;
    }
    
    @Override
    public XMLReporter getDefaultReporter()
    {
        return reporter;
    }
    
    @Override
    public Resolver getDefaultResolver()
    {
        return resolver;
    }

    @Override
    public void setDefaultReporter(XMLReporter reporter)
    {
        this.reporter = reporter;
    }
    
    @Override
    public void setDefaultResolver(Resolver resolver)
    {
        this.resolver = resolver;
    }

    @Override
    public Model<Object> getModel()
    {
        return model;
    }

    @Override
    public MutableContext<Object> getMutableContext()
    {
        if (mutableContext == null)
            mutableContext = new AxioMutableContext();
        return mutableContext;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public AxiomSAProcessingContext getTypedContext(SchemaComponentCache cache)
    {
        AxiomSAProcessingContext tc;
        if (cache != null)
            tc = typedContexts.get(cache);
        else
        {
            if (defaultCache != null)
                tc = typedContexts.get(defaultCache);
            else
            {
                tc = new AxiomSAProcessingContext(this, null);
                defaultCache = tc.getSchema();
                typedContexts.put(defaultCache, tc);
            }
        }
        if (tc == null) // only happens when cache != null, first time seen.
        {
            tc = new AxiomSAProcessingContext(this, cache);
            typedContexts.put(cache, tc);
        }
        return tc;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public AxiomSAProcessingContext getTempTypedContext(SchemaComponentCache cache)
    {
        // if argument is null, use the default
        if (cache == null)
            return getTypedContext(null);
        // create a new one; do not query or update the typedContexts map.
        return new AxiomSAProcessingContext(this, cache);
    }

    @Override
    public boolean isNode(Object item)
    {
        if (null != item)
        {
            return AxiomSupport.getNodeKind(item) != null;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean isSupported(final String feature)
    {
        PreCondition.assertNotNull(feature, "feature");
//System.out.println("Feature : " + feature);
        if (feature.startsWith(Feature.PREFIX))
        {
            // Axiom does not support document uri retrieval or xml:base.
            // disable namespace axis until we can figure out if we can
            // make it work; axis doesn't support it, apparently.
            if (feature.equals(Feature.DOCUMENT_URI) ||
                feature.equals(Feature.BASE_URI) ||
                feature.equals(Feature.NAMESPACE_AXIS) ||
                feature.equals(Feature.IN_TREE_VALIDATION) )
                return false;
            if (feature.equals(Feature.ATTRIBUTE_AXIS_INHERIT) ||
                feature.equals(Feature.MUTABILITY) ||
                feature.equals(Feature.TYPED) ||
                feature.equals(Feature.TYPE_ANNOTATION) ||
                feature.equals(Feature.TYPED_VALUE) )
                return true;
        }
        return false;
    }

    @Override
    public Cursor newCursor(Object node)
    {
        return new CursorOnModel<Object>(node, model);
    }

    @Override
    public FragmentBuilder<Object> newFragmentBuilder()
    {
        // doesn't currently work, but when we implement namespace fixup
        // in the other override, we won't have to change this, just remove the comment
        return newFragmentBuilder(true);
    }

    @Override
    public FragmentBuilder<Object> newFragmentBuilder(boolean fixupNamespaces)
    {
        // TODO: we've disabled namespace fixups (for axiom) in order to enable typing.
        // the issue here is that namespace fixups delays the firing of events to the
        // underlying fragment builder, which means that we don't have the node identity and
        // the type name for attributes at the same time. bad. the long term solution is to
        // do something a good deal smaller; the namespace fixup mess is a kludge anyway.
        return new AxiomFragmentBuilder(omfactory, false);
//        return new FilteredFragmentBuilder<Object>(new NamespaceFixupFilter(), new AxiomFragmentBuilder(omfactory, false));
    }

    @Override
    public Object node(Object item)
    {
        if (isNode(item))
            return item;
        return null;
    }

    @Override
    public Object[] nodeArray(int size)
    {
        if (size < 0)
        {
            throw new NegativeArraySizeException("Illegal size: " + size);
        }
        return new Object[size];
    }

    @Override
    public String getRegisteredPrefix(String namespace)
    {
        // TODO : implement properly; placeholder for now
        return null;
    }
    
    @Override
    public SerializationParams getDefaultParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDefaultParameters(SerializationParams config)
    {
        // TODO Auto-generated method stub
        
    }

    public OMFactory getOMFactory()
    {
        return omfactory;
    }
    
    public static Map<String, Integer> getIdMap(OMDocument document)
    {
        synchronized(idMaps)
        {
            Map<String, Integer> map = idMaps.get(document);
            if (map == null)
            {
                map = new HashMap<String, Integer>();
                idMaps.put(document, map);
            }
            return Collections.synchronizedMap(map);
        }
    }
    
    private class AxioMutableContext implements MutableContext<Object>
    {
        AxioMutableContext()
        {
            this.factory = new AxiomFactory(PreCondition.assertNotNull(omfactory, "omfactory"));
            this.mmodel = new AxiomMutableModel(factory);
        }
        
        public AxiomProcessingContext getProcessingContext()
        {
            return AxiomProcessingContext.this;
        }
        
        public MutableModel<Object> getModel()
        {
            return mmodel;
        }

        public NodeFactory<Object> getNodeFactory()
        {
            return factory;
        }

        public MutableCursor<Object> newCursor(Object node)
        {
            return new MutableCursorOnMutableModel<Object>(node, mmodel);
        }
        private final AxiomFactory factory;
        private final AxiomMutableModel mmodel;
    }
    
    static Map<OMDocument, URI> docURIs = new WeakHashMap<OMDocument, URI>();
    static final Map<OMDocument, Map<String, Integer>> idMaps = new WeakHashMap<OMDocument, Map<String, Integer>>();
    
    private final AxiomModel model = new AxiomModel();
    private final OMFactory omfactory;
    private MutableContext<Object> mutableContext;
    private Resolver resolver;
    private XMLReporter reporter;
    private SchemaComponentCache defaultCache;
    private Map<SchemaComponentCache, AxiomSAProcessingContext> typedContexts = new HashMap<SchemaComponentCache, AxiomSAProcessingContext>();
}
