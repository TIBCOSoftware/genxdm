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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.Cursor;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.nodes.Bookmark;

public class XmlNodeMarker
    implements Bookmark<XmlNode>
{
    
    public XmlNodeMarker(final XmlNode node, final XmlNodeModel model)
    {
        this.node = PreCondition.assertNotNull(node, "node");
        this.model = model;
    }

    public Model<XmlNode> getModel()
    {
        if (model == null)
            model = new XmlNodeModel();
        return model;
    }

    public Object getState(String key)
    {
        return state.get(key);
    }

    public Cursor<XmlNode> newCursor()
    {
        return new XmlNodeCursor(node);
    }

    public void putState(String key, Object data)
    {
        state.put(key, data);
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
        return node.hasNextSibling();
    }

    public boolean hasParent()
    {
        return node.hasParent();
    }

    public boolean hasPreviousSibling()
    {
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

    public XmlNode getNode()
    {
        return node;
    }

    public List<XmlNode> getNodes()
    {
        return new UnaryIterable<XmlNode>(node);
    }

    private Map<String, Object> state = new HashMap<String, Object>();
    private final XmlNode node;
    private XmlNodeModel model;
}
