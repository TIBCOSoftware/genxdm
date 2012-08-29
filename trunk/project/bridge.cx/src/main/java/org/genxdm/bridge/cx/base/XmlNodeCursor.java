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

import java.net.URI;

import javax.xml.namespace.QName;

import org.genxdm.Cursor;
import org.genxdm.NodeKind;
import org.genxdm.bridge.cx.tree.Walker;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.nodes.Bookmark;

public class XmlNodeCursor
    implements Cursor<XmlNode>
{
    public XmlNodeCursor(final XmlNode node)
    {
        this.node = PreCondition.assertNotNull(node, "node");
    }

    public Bookmark<XmlNode> bookmark()
    {
        return new XmlNodeMarker(node, null);
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

    public URI getBaseURI()
    {
        return node.getBaseURI();
    }

    public URI getDocumentURI()
    {
        return node.getDocumentURI();
    }

    public Iterable<NamespaceBinding> getNamespaceBindings()
    {
        return node.getNamespaceBindings();
    }
    
    public XmlNode getNodeId()
    {
        return node.getNodeId();
    }

    public NodeKind getNodeKind()
    {
        return node.getNodeKind();
    }

    public boolean hasChildren()
    {
        return node.hasChildren();
    }

    public boolean hasNextSibling()
    {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return node.hasNextSibling();
    }

    public boolean hasParent()
    {
        return node.hasParent();
    }

    public boolean hasPreviousSibling()
    {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return node.hasPreviousSibling();
    }

    public boolean isAttribute()
    {
        return node.isAttribute();
    }

    public boolean isElement()
    {
        return node.isElement();
    }

    public boolean isId()
    {
        return node.isId();
    }

    public boolean isIdRefs()
    {
        return node.isIdRefs();
    }

    public boolean isNamespace()
    {
        return node.isNamespace();
    }

    public boolean isSameNode(XmlNode other)
    {
        return node.isSameNode(other);
    }

    public boolean isText()
    {
        return node.isText();
    }

    public boolean matches(NodeKind nodeKind, String namespaceURI, String localName)
    {
        return node.matches(nodeKind, namespaceURI, localName);
    }

    public boolean matches(String namespaceURI, String localName)
    {
        return node.matches(namespaceURI, localName);
    }

    public Iterable<QName> getAttributeNames(boolean orderCanonical)
    {
        return node.getAttributeNames(orderCanonical);
    }

    public String getAttributeStringValue(String namespaceURI, String localName)
    {
        return node.getAttributeStringValue(namespaceURI, localName);
    }

    public int getLineNumber()
    {
        return node.getLineNumber();
    }

    public String getLocalName()
    {
        return node.getLocalName();
    }

    public String getNamespaceForPrefix(String prefix)
    {
        return node.getNamespaceForPrefix(prefix);
    }

    public Iterable<String> getNamespaceNames(boolean orderCanonical)
    {
        return node.getNamespaceNames(orderCanonical);
    }

    public String getNamespaceURI()
    {
        return node.getNamespaceURI();
    }

    public String getPrefix()
    {
        return node.getPrefix();
    }

    public String getStringValue()
    {
        return node.getStringValue();
    }

    public boolean hasAttributes()
    {
        return node.hasAttributes();
    }

    public boolean hasNamespaces()
    {
        return node.hasNamespaces();
    }

    public void moveTo(XmlNode bookmark)
    {
        // TODO: we ought not let a cursor move from one document to
        // another, which is possible using this method.  so we ought
        // to verify the precondition that bookmark is in the same document
        // as the current node.
        moveToNode(bookmark);
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

    public int compareTo(Cursor<XmlNode> arg0)
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
    
    protected XmlNode node;
}
