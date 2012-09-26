/*
 * Copyright (c) 2012 TIBCO Software Inc.
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
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.Cursor;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.axes.IteratorAncestorAxis;
import org.genxdm.bridgekit.axes.IteratorAncestorOrSelfAxis;
import org.genxdm.bridgekit.misc.UnaryIterator;
import org.genxdm.bridgekit.tree.TraverserOnIterator;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;

/**
 * Captures the logic of being a {@link TraversingInformer} for the "XmlNode"
 * nodes.
 * 
 * TODO - the axes can likely be implemented more efficiently than they are
 * below.
 */
public class XmlNodeTraversingInformer implements TraversingInformer {

    private static final XmlNodeModel m_model = new XmlNodeModel();
    
    protected XmlNode node;

    public XmlNodeTraversingInformer(XmlNode node) {
        this.node = node;
    }

    public URI getBaseURI() {
        return node.getBaseURI();
    }

    public URI getDocumentURI() {
        return node.getDocumentURI();
    }

    public Iterable<NamespaceBinding> getNamespaceBindings() {
        return node.getNamespaceBindings();
    }

    public XmlNode getNodeId() {
        return node.getNodeId();
    }

    public NodeKind getNodeKind() {
        return node.getNodeKind();
    }

    public boolean hasChildren() {
        return node.hasChildren();
    }

    public boolean hasNextSibling() {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return node.hasNextSibling();
    }

    public boolean hasParent() {
        return node.hasParent();
    }

    public boolean hasPreviousSibling() {
        if (node.isAttribute() || node.isNamespace())
            return false;
        return node.hasPreviousSibling();
    }

    public boolean isAttribute() {
        return node.isAttribute();
    }

    public boolean isElement() {
        return node.isElement();
    }

    public boolean isId() {
        return node.isId();
    }

    public boolean isIdRefs() {
        return node.isIdRefs();
    }

    public boolean isNamespace() {
        return node.isNamespace();
    }

    public boolean isSameNode(XmlNode other) {
        return node.isSameNode(other);
    }

    public boolean isText() {
        return node.isText();
    }

    public boolean matches(NodeKind nodeKind, String namespaceURI, String localName) {
        return node.matches(nodeKind, namespaceURI, localName);
    }

    public boolean matches(String namespaceURI, String localName) {
        return node.matches(namespaceURI, localName);
    }

    public Iterable<QName> getAttributeNames(boolean orderCanonical) {
        return node.getAttributeNames(orderCanonical);
    }

    public String getAttributeStringValue(String namespaceURI, String localName) {
        return node.getAttributeStringValue(namespaceURI, localName);
    }

    public int getLineNumber() {
        return node.getLineNumber();
    }

    public String getLocalName() {
        return node.getLocalName();
    }

    public String getNamespaceForPrefix(String prefix) {
        return node.getNamespaceForPrefix(prefix);
    }

    public Iterable<String> getNamespaceNames(boolean orderCanonical) {
        return node.getNamespaceNames(orderCanonical);
    }

    public String getNamespaceURI() {
        return node.getNamespaceURI();
    }

    public String getPrefix() {
        return node.getPrefix();
    }

    public String getStringValue() {
        return node.getStringValue();
    }

    public boolean hasAttributes() {
        return node.hasAttributes();
    }

    public boolean hasNamespaces() {
        return node.hasNamespaces();
    }

    @Override
    public Traverser traverseAncestorAxis() {
        Iterator<XmlNode> resultIter =
                (node == null) ? new UnaryIterator<XmlNode>(null) :
                    new IteratorAncestorAxis<XmlNode>(node, m_model);
        return new TraverserOnIterator<XmlNode>(m_model, resultIter);
    }

    @Override
    public Traverser traverseAncestorOrSelfAxis() {
        Iterator<XmlNode> resultIter =
                (node == null) ? new UnaryIterator<XmlNode>(null) :
                    new IteratorAncestorOrSelfAxis<XmlNode>(node, m_model);
        return new TraverserOnIterator<XmlNode>(m_model, resultIter);
    }

    @Override
    public Traverser traverseAttributeAxis(boolean inherit) {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getAttributeAxis(node, inherit).iterator());
    }

    @Override
    public Traverser traverseChildAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getChildAxis(node).iterator());
    }

    @Override
    public Traverser traverseChildElements() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getChildElements(node).iterator());
    }

    @Override
    public Traverser traverseChildElementsByName(String namespaceURI, String localName) {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getChildElementsByName(node, namespaceURI, localName).iterator());
    }

    @Override
    public Traverser traverseDescendantAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getDescendantAxis(node).iterator());
    }

    @Override
    public Traverser traverseDescendantOrSelfAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getDescendantOrSelfAxis(node).iterator());
    }

    @Override
    public Traverser traverseFollowingAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getFollowingAxis(node).iterator());
    }

    @Override
    public Traverser traverseFollowingSiblingAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getFollowingSiblingAxis(node).iterator());
    }

    @Override
    public Traverser traverseNamespaceAxis(boolean inherit) {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getNamespaceAxis(node, inherit).iterator());
    }

    @Override
    public Traverser traversePrecedingAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getPrecedingAxis(node).iterator());
    }

    @Override
    public Traverser traversePrecedingSiblingAxis() {
        return new TraverserOnIterator<XmlNode>(m_model, m_model.getPrecedingSiblingAxis(node).iterator());
    }

    @Override
    public int compareTo(TraversingInformer o) {
        if (! (o instanceof XmlNodeTraversingInformer)) {
            throw new IllegalArgumentException("Attempting to compare different classes of TraversingInformer.");
        }
        
        XmlNodeTraversingInformer otherTiom = (XmlNodeTraversingInformer) o;
        
        return m_model.compare(node, otherTiom.node);
    }

    @Override
    public Cursor newCursor() {
        return new XmlNodeCursor(node);
    }

}