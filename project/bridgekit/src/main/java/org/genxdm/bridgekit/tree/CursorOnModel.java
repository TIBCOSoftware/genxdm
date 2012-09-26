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
package org.genxdm.bridgekit.tree;

import org.genxdm.Cursor;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.io.ContentHandler;

public class CursorOnModel<N>
    extends TraversingInformerOnModel<N>
    implements Cursor
{
    
    public CursorOnModel(final N node, final Model<N> model)
    {
        super(node, model);
        setCurrentNode(node);
    }
    
    public boolean moveToAttribute(final String namespaceURI, final String localName)
    {
        return moveToNode(model.getAttribute(node, namespaceURI, localName));
    }
    
    public boolean moveToElementById(final String id)
    {
        return moveToNode(model.getElementById(node, id));
    }

    public boolean moveToFirstChild()
    {
        return moveToNode(model.getFirstChild(node));
    }

    public boolean moveToFirstChildElement()
    {
        return moveToNode(model.getFirstChildElement(node));
    }

    public boolean moveToFirstChildElementByName(final String namespaceURI, final String localName)
    {
        return moveToNode(model.getFirstChildElementByName(node, namespaceURI, localName));
    }

    public boolean moveToLastChild()
    {
        return moveToNode(model.getLastChild(node));
    }

    public boolean moveToNextSibling()
    {
        return moveToNode(model.getNextSibling(node));
    }

    public boolean moveToNextSiblingElement()
    {
        return moveToNode(model.getNextSiblingElement(node));
    }


    public boolean moveToNextSiblingElementByName(final String namespaceURI, final String localName)
    {
        return moveToNode(model.getNextSiblingElementByName(node, namespaceURI, localName));
    }

    public boolean moveToParent()
    {
        return moveToNode(model.getParent(node));
    }

    public boolean moveToPreviousSibling()
    {
        return moveToNode(model.getPreviousSibling(node));
    }

    public void moveToRoot()
    {
        moveToNode(model.getRoot(node));
    }

    public void write(final ContentHandler writer)
    {
        model.stream(node, writer);
        if ( (nodeType != NodeKind.DOCUMENT) &&
             hasParent() ) // not at the top level
        {
            if (!moveToNextSibling())
                moveToParent();
        }
    }

    protected boolean moveToNode(final N position)
    {
        if (position != null)
        {
            setCurrentNode(position);
            return true;
        }
        return false;
    }

    /**
     * Changes the current node and synchronizes the node type.
     * 
     * For performance we can't check again that the position is not <code>null</code> so the caller *must* be responsible for this invariant.
     */
    protected void setCurrentNode(final N node)
    {
        this.node = node;
        nodeType = model.getNodeKind(node);
    }
    
    protected NodeKind nodeType;

    @Override
    public Cursor newPrecursor() {
        return new CursorOnModel<N>(node, model);
    }
}
