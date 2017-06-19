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
package org.genxdm.bridge.dom;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.Cursor;
import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.bridge.dom.enhanced.DomSAProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.filters.FilteredFragmentBuilder;
import org.genxdm.bridgekit.filters.NamespaceFixupFilter;
import org.genxdm.bridgekit.tree.CursorOnModel;
import org.genxdm.bridgekit.tree.MutableCursorOnMutableModel;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableContext;
import org.genxdm.mutable.MutableCursor;
import org.genxdm.mutable.MutableModel;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.SchemaComponentCache;
import org.w3c.dom.Node;

public class DomProcessingContext
    extends DomDocumentHandlerFactory
    implements ProcessingContext<Node>
    
{
    /**
     * Create a DOM processing context.
     * 
     * <p>Note that the preferred way is to call
     * {@link DomProcessingContext#DomProcessingContext(DocumentBuilderFactory)
     * so that the caller can control the set of schemas available during parsing.
     * </p>
     * 
     * <p>The default DocumentBuilderFactory simply sets namespace awareness, but
     * with no schema support</p>
     * 
     */
    public DomProcessingContext()
    {
        this(sm_dbf);
    }

    /**
     * Preferred constructor that lets callers control which {@link DocumentBuilderFactory} will
     * be used during parsing.
     * 
     * @param dbf
     */
    public DomProcessingContext(DocumentBuilderFactory dbf) {
        super(dbf);
        
    }
    
    @Override
    public Model<Node> getModel()
    {
        return model;
    }

    @Override
    public MutableContext<Node> getMutableContext()
    {
        if (mutantContext == null)
            mutantContext = new MutantContext();
        return mutantContext;
    }

    @Override
    public boolean isNode(Object item)
    {
        return (item instanceof Node);
    }

    @Override
    public boolean isSupported(final String feature)
    {
        PreCondition.assertNotNull(feature, "feature");
        if (feature.equals(Feature.BASE_URI) ||
            feature.equals(Feature.IN_TREE_VALIDATION) )
            return false;
        if (feature.startsWith(Feature.PREFIX))
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TypedContext<Node, XmlAtom> getTypedContext(SchemaComponentCache cache)
    {
        DomSAProcessingContext tc = null;
        if (cache != null)
            tc = typedContexts.get(cache);
        else
        {
            if (defaultCache != null)
                tc = typedContexts.get(defaultCache);
            else
            {
                tc = new DomSAProcessingContext(this, null);
                defaultCache = tc.getSchema();
                typedContexts.put(defaultCache, tc);
            }
        }
        if (tc == null) // previous line returned null; first time we've seen this cache
        {
            tc = new DomSAProcessingContext(this, cache);
            typedContexts.put(cache, tc);
        }
        return tc;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypedContext<Node, XmlAtom> getTempTypedContext(SchemaComponentCache cache)
    {
        if (cache == null)
            return getTypedContext(null);
        return new DomSAProcessingContext(this, cache);
    }

    @Override
    public Cursor newCursor(Node node)
    {
        return new CursorOnModel<Node>(node, model);
    }

    @Override
    public DocumentHandler<Node> newDocumentHandler() {
        return new DomDocumentHandler(this);
    }

    @Override
    public FragmentBuilder<Node> newFragmentBuilder()
    {
        return newFragmentBuilder(true);
    }

    @Override
    public FragmentBuilder<Node> newFragmentBuilder(boolean namespaceFixup)
    {
        if (namespaceFixup)
            return new FilteredFragmentBuilder<Node>(new NamespaceFixupFilter(), new DomFragmentBuilder(m_dbf));
        return new DomFragmentBuilder(m_dbf);
    }

    @Override
    public Node node(Object item)
    {
        if (isNode(item))
            return (Node)item;
        return null;
    }

    @Override
    public Node[] nodeArray(int size)
    {
        return new Node[size];
    }

    private class MutantContext implements MutableContext<Node>
    {
        
        @Override
        public MutableModel<Node> getModel()
        {
            return mutant;
        }

        @Override
        public DomNodeFactory getNodeFactory()
        {
            return new DomNodeFactory(m_dbf);
        }

        @Override
        public ProcessingContext<Node> getProcessingContext()
        {
            return DomProcessingContext.this;
        }

        @Override
        public MutableCursor<Node> newCursor(Node node)
        {
            return new MutableCursorOnMutableModel<Node>(node, mutant);
        }

        private final DomModelMutable mutant = new DomModelMutable();
    }

    static DocumentBuilderFactory sm_dbf;
    static DocumentBuilder sm_db;
    static {
        sm_dbf = DocumentBuilderFactory.newInstance();
        sm_dbf.setNamespaceAware(true);
        try {
            sm_dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            sm_db = sm_dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO - this failure really should be logged.
        }
    }
    
    private final DomModel model = new DomModel();
    private MutantContext mutantContext;
    private Map<SchemaComponentCache, DomSAProcessingContext> typedContexts = new HashMap<SchemaComponentCache, DomSAProcessingContext>();
    private SchemaComponentCache defaultCache;
}
