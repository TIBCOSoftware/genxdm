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
package org.gxml.bridge.axiom;

import javax.xml.stream.XMLReporter;

import org.apache.axiom.om.OMFactory;
import org.gxml.bridgekit.atoms.XmlAtom;
import org.gxml.bridgekit.tree.BookmarkOnModel;
import org.gxml.bridgekit.tree.CursorOnModel;
import org.gxml.bridgekit.tree.MutableCursorOnMutableModel;
import org.gxml.processor.io.DefaultDocumentHandler;
import org.gxml.Feature;
import org.gxml.Resolver;
import org.gxml.base.Cursor;
import org.gxml.base.Model;
import org.gxml.base.ProcessingContext;
import org.gxml.base.io.DocumentHandler;
import org.gxml.base.mutable.MutableContext;
import org.gxml.base.mutable.MutableCursor;
import org.gxml.base.mutable.MutableModel;
import org.gxml.base.mutable.NodeFactory;
import org.gxml.bridge.axiom.enhanced.AxiomSAProcessingContext;
import org.gxml.exceptions.PreCondition;
import org.gxml.names.NameSource;
import org.gxml.nodes.Bookmark;
import org.gxml.typed.TypedContext;

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
        return new DefaultDocumentHandler<Object>(this);
    }

    public DocumentHandler<Object> newDocumentHandler(XMLReporter reporter, Resolver resolver)
    {
        // TODO: implement
        return newDocumentHandler();
    }

    public void setDefaultReporter(XMLReporter reporter)
    {
        // TODO: implement
    }
    
    public void setDefaultResolver(Resolver resolver)
    {
        // TODO: implement
    }

    public Model<Object> getModel()
    {
        return model;
    }

    public NameSource getNameBridge()
    {
        return names;
    }

    public MutableContext<Object> getMutableContext()
    {
        if (mutableContext == null)
            mutableContext = new AxioMutableContext();
        return mutableContext;
    }
    
    @SuppressWarnings("unchecked")
    public TypedContext<Object, XmlAtom> getTypedContext()
    {
        if (saContext == null)
            saContext = new AxiomSAProcessingContext(this);
        return saContext;
    }
    
    public boolean isItem(Object object)
    {
        // TODO: implement
        return false;
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
            if (!feature.equals(Feature.TYPE_ANNOTATION) &&
                !feature.equals(Feature.TYPED_VALUE) &&
                !feature.equals(Feature.DOCUMENT_URI)
                && !feature.equals(Feature.ATTRIBUTE_AXIS_INHERIT))
                return true;
        }
        return false;
    }

    public boolean isTyped()
    {
        return false;
    }

    public Object[] itemArray(int size)
    {
        if (size < 0)
        {
            throw new NegativeArraySizeException("Illegal size: " + size);
        }
        return new Object[size];
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
    
    private class AxioMutableContext implements MutableContext<Object>
    {
        AxioMutableContext()
        {
            this.factory = new AxiomFactory(PreCondition.assertNotNull(omfactory, "omfactory"));
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
        private final AxiomMutableModel mmodel = new AxiomMutableModel();
        private final AxiomFactory factory;
    }
    
    private final AxiomModel model = new AxiomModel();
    private final NameSource names = new NameSource();
    private final OMFactory omfactory;
    private MutableContext<Object> mutableContext;
    private AxiomSAProcessingContext saContext;
}
