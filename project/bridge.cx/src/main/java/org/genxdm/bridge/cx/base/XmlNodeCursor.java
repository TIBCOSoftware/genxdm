/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.bridge.cx.base;



import java.util.ArrayList;
import java.util.List;

import org.genxdm.Cursor;
import org.genxdm.NodeKind;
import org.genxdm.NodeSource;
import org.genxdm.bridge.cx.tree.Walker;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;

public class XmlNodeCursor 
    extends XmlNodeTraversingInformer
    implements Cursor, NodeSource<XmlNode>
{
    public XmlNodeCursor(final XmlNode node)
    {
        super(node);
        this.node = PreCondition.assertNotNull(node, "node");
    }

    public void write(ContentHandler writer)
    {
        Walker.walk(node, writer);
        if ( (node.getNodeKind() != NodeKind.DOCUMENT) &&
              node.hasParent() )
        {
            if (!moveToNextSibling())
                moveToParent();
        }
    }

    public boolean moveToAttribute(String namespaceURI, String localName)
    {
        return moveToNode(node.getAttribute(namespaceURI, localName));
    }

    public boolean moveToElementById(String id)
    {
        return moveToNode(node.getElementById(id));
    }

    public boolean moveToFirstChild()
    {
        return moveToNode(node.getFirstChild());
    }

    public boolean moveToFirstChildElement()
    {
        return moveToNode(node.getFirstChildElement());
    }

    public boolean moveToFirstChildElementByName(String namespaceURI, String localName)
    {
        return moveToNode(node.getFirstChildElementByName(namespaceURI, localName));
    }

    public boolean moveToLastChild()
    {
        return moveToNode(node.getLastChild());
    }

    public boolean moveToNextSibling()
    {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return moveToNode(node.getNextSibling());
    }

    public boolean moveToNextSiblingElement()
    {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return moveToNode(node.getNextSiblingElement());
    }

    public boolean moveToNextSiblingElementByName(String namespaceURI, String localName)
    {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return moveToNode(node.getNextSiblingElementByName(namespaceURI, localName));
    }

    public boolean moveToParent()
    {
        return moveToNode(node.getParent());
    }

    public boolean moveToPreviousSibling()
    {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return moveToNode(node.getPreviousSibling());
    }

    public void moveToRoot()
    {
        moveToNode(node.getRoot());
    }

    @Override
    public List<XmlNode> getNodes()
    {
        List<XmlNode> list = new ArrayList<XmlNode>();
        list.add(node);
        return list;
    }

    @Override
    public XmlNode getNode()
    {
        return node;
    }

    public int compareTo(Cursor arg0)
    {
        XmlNodeModel model = new XmlNodeModel();
        return Ordering.compareNodes(node, ((XmlNodeCursor)arg0).node, model);
    }

    protected boolean moveToNode(final XmlNode position)
    {
        // because this returns false whenever position is null,
        // we get cheap/fast implementation of all moveTo methods
        // based on it.
        if (null != position)
        {
             node = position;
            return true;
        }
        return false;
    }
}
