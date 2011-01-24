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
package org.genxdm.bridge.dom;

import javax.xml.parsers.DocumentBuilderFactory;

import org.genxdm.Feature;
import org.genxdm.base.Cursor;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.base.mutable.MutableContext;
import org.genxdm.base.mutable.MutableCursor;
import org.genxdm.base.mutable.MutableModel;
import org.genxdm.bridge.dom.enhanced.DomSAProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.tree.BookmarkOnModel;
import org.genxdm.bridgekit.tree.CursorOnModel;
import org.genxdm.bridgekit.tree.MutableCursorOnMutableModel;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.Bookmark;
import org.genxdm.typed.TypedContext;
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
    
    public Bookmark<Node> bookmark(Node node)
    {
        return new BookmarkOnModel<Node>(node, model);
    }

    public Model<Node> getModel()
    {
        return model;
    }

    public MutableContext<Node> getMutableContext()
    {
        if (mutantContext == null)
            mutantContext = new MutantContext();
        return mutantContext;
    }

    public boolean isNode(Object item)
    {
        return (item instanceof Node);
    }

    public boolean isSupported(final String feature)
    {
        PreCondition.assertNotNull(feature, "feature");
        if (feature.startsWith(Feature.PREFIX))
        {
            if (!feature.equals(Feature.TYPE_ANNOTATION) && feature.equals(Feature.TYPED_VALUE))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public TypedContext<Node, XmlAtom> getTypedContext()
    {
        if (typedContext == null)
            typedContext = new DomSAProcessingContext(this);
        return typedContext;
    }

    public Cursor<Node> newCursor(Node node)
    {
        return new CursorOnModel<Node>(node, model);
    }

    public FragmentBuilder<Node> newFragmentBuilder()
    {
        return new DomFragmentBuilder(getDocumentBuilderFactory() );
    }

    public Node node(Object item)
    {
        if (isNode(item))
            return (Node)item;
        return null;
    }

    public Node[] nodeArray(int size)
    {
        return new Node[size];
    }

    private class MutantContext implements MutableContext<Node>
    {
        
        public MutableModel<Node> getModel()
        {
            return mutant;
        }

        public DomNodeFactory getNodeFactory()
        {
            return new DomNodeFactory( getDocumentBuilderFactory(), mutant );
        }

        public ProcessingContext<Node> getProcessingContext()
        {
            return DomProcessingContext.this;
        }

        public MutableCursor<Node> newCursor(Node node)
        {
            return new MutableCursorOnMutableModel<Node>(node, mutant);
        }

        private final DomModelMutable mutant = new DomModelMutable();
    }

    // TODO: the DOM bridge has too many document builder factories.
    // we need to review the whole bridge, of course, but this is one
    // of the notable issues in it; if we have a static "default" one somewhere ...
    // well, we should only have *one*, dammit.
    private static DocumentBuilderFactory sm_dbf;
    static {
        sm_dbf = DocumentBuilderFactory.newInstance();
        sm_dbf.setNamespaceAware(true);
    }
    
    private final DomModel model = new DomModel();
    private MutantContext mutantContext;
    private DomSAProcessingContext typedContext;
}
