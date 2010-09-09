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
package org.gxml.bridgekit.tree;

import java.io.IOException;

import org.gxml.NodeKind;
import org.gxml.base.Cursor;
import org.gxml.base.Model;
import org.gxml.base.io.ContentHandler;
import org.gxml.exceptions.PreCondition;
import org.gxml.nodes.Bookmark;

public class CursorOnModel<N>
    extends InformerOnModel<N>
    implements Cursor<N>
{
    
    public CursorOnModel(final N node, final Model<N> model)
    {
        super(node, model);
        setCurrentNode(node);
    }
    
    public Bookmark<N> bookmark()
    {
        return new BookmarkOnModel<N>(node, model);
    }
    
    public int compareTo(final Cursor<N> other)
    {
        return model.compare(node, other.bookmark().getNode());
    }

    public void moveTo(final N bookmark)
    {
        setCurrentNode(PreCondition.assertArgumentNotNull(bookmark, "bookmark"));
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
        model.stream(node, true, writer);
    }

    protected boolean moveToNode(final N position)
    {
        if (null != position)
        {
            setCurrentNode(position);
            return true;
        }
        else
        {
            return false;
        }
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
}
