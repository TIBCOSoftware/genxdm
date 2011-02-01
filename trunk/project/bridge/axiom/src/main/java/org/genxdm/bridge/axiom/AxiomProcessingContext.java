/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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

import javax.xml.stream.XMLReporter;

import org.apache.axiom.om.OMFactory;
import org.genxdm.Feature;
import org.genxdm.Resolver;
import org.genxdm.base.Cursor;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.DocumentHandler;
import org.genxdm.base.mutable.MutableContext;
//import org.genxdm.base.mutable.MutableCursor;
//import org.genxdm.base.mutable.MutableModel;
//import org.genxdm.base.mutable.NodeFactory;
//import org.genxdm.bridge.axiom.enhanced.AxiomSAProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.tree.BookmarkOnModel;
import org.genxdm.bridgekit.tree.CursorOnModel;
//import org.genxdm.bridgekit.tree.MutableCursorOnMutableModel;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.Bookmark;
import org.genxdm.processor.io.DefaultDocumentHandler;
import org.genxdm.typed.TypedContext;

public class AxiomProcessingContext
    implements ProcessingContext<Object>
{

    public AxiomProcessingContext(final OMFactory factory)
    {
        this.omfactory = factory;
    }
    
    public Bookmark<Object> bookmark(Object node)
    {
        return new BookmarkOnModel<Object>(node, model);
    }

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

    public void setDefaultReporter(XMLReporter reporter)
    {
        this.reporter = reporter;
    }
    
    public void setDefaultResolver(Resolver resolver)
    {
        this.resolver = resolver;
    }

    public Model<Object> getModel()
    {
        return model;
    }

    public MutableContext<Object> getMutableContext()
    {
        // TODO: disabled temporarily, while completing base and the test suite.
        return null;
//        if (mutableContext == null)
//            mutableContext = new AxioMutableContext();
//        return mutableContext;
    }
    
    @SuppressWarnings("unchecked")
    public TypedContext<Object, XmlAtom> getTypedContext()
    {
        return null; //TODO: implement properly; change this and isSupported. 
//        if (saContext == null)
//            saContext = new AxiomSAProcessingContext(this);
//        return saContext;
    }
    
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

    public boolean isSupported(final String feature)
    {
        PreCondition.assertNotNull(feature, "feature");
        if (feature.startsWith(Feature.PREFIX))
        {
            // at the moment, none of the following are supported.
            // we need to know what "supported" means.
            if (!feature.equals(Feature.TYPE_ANNOTATION) &&
                !feature.equals(Feature.TYPED_VALUE) &&
                !feature.equals(Feature.DOCUMENT_URI) &&
                !feature.equals(Feature.BASE_URI) &&
                !feature.equals(Feature.ATTRIBUTE_AXIS_INHERIT) &&
                !feature.equals(Feature.TYPED) &&
                !feature.equals(Feature.MUTABILITY) )
                return true;
        }
        return false;
    }

    public Cursor<Object> newCursor(Object node)
    {
        return new CursorOnModel<Object>(node, model);
    }

    public AxiomFragmentBuilder newFragmentBuilder()
    {
        return new AxiomFragmentBuilder(omfactory, false);
    }

    public Object node(Object item)
    {
        if (isNode(item))
            return item;
        return null;
    }

    public Object[] nodeArray(int size)
    {
        if (size < 0)
        {
            throw new NegativeArraySizeException("Illegal size: " + size);
        }
        return new Object[size];
    }

    public OMFactory getOMFactory()
    {
        return omfactory;
    }
    
//    private class AxioMutableContext implements MutableContext<Object>
//    {
//        AxioMutableContext()
//        {
//            this.factory = new AxiomFactory(PreCondition.assertNotNull(omfactory, "omfactory"));
//            this.mmodel = new AxiomMutableModel(factory);
//            this.factory.setMutableModel(mmodel);
//        }
//        
//        public AxiomProcessingContext getProcessingContext()
//        {
//            return AxiomProcessingContext.this;
//        }
//        
//        public MutableModel<Object> getModel()
//        {
//            return mmodel;
//        }
//
//        public NodeFactory<Object> getNodeFactory()
//        {
//            return factory;
//        }
//
//        public MutableCursor<Object> newCursor(Object node)
//        {
//            return new MutableCursorOnMutableModel<Object>(node, mmodel);
//        }
//        private final AxiomFactory factory;
//        private final AxiomMutableModel mmodel;
//    }
    
    private final AxiomModel model = new AxiomModel();
    private final OMFactory omfactory;
//    private MutableContext<Object> mutableContext;
    private Resolver resolver;
    private XMLReporter reporter;
//    private AxiomSAProcessingContext saContext;
}
