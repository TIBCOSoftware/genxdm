/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.gxml.bridge.cx.base;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.gxml.bridgekit.axes.IterableAncestorAxis;
import org.gxml.bridgekit.axes.IterableAncestorOrSelfAxis;
import org.gxml.bridgekit.axes.IterableChildAxis;
import org.gxml.bridgekit.axes.IterableChildAxisElements;
import org.gxml.bridgekit.axes.IterableChildAxisElementsByName;
import org.gxml.bridgekit.axes.IterableDescendantAxis;
import org.gxml.bridgekit.axes.IterableDescendantOrSelfAxis;
import org.gxml.bridgekit.axes.IterableFollowingAxis;
import org.gxml.bridgekit.axes.IterableFollowingSiblingAxis;
import org.gxml.bridgekit.axes.IterablePrecedingAxis;
import org.gxml.bridgekit.axes.IterablePrecedingSiblingAxis;
import org.gxml.bridgekit.misc.UnaryIterable;
import org.gxml.bridgekit.tree.Ordering;
import org.gxml.NodeKind;
import org.gxml.base.Model;
import org.gxml.base.io.ContentHandler;
import org.gxml.bridge.cx.tree.NodeIterable;
import org.gxml.bridge.cx.tree.Walker;
import org.gxml.bridge.cx.tree.XmlAttributeNode;
import org.gxml.bridge.cx.tree.XmlContainerNode;
import org.gxml.bridge.cx.tree.XmlElementNode;
import org.gxml.bridge.cx.tree.XmlNamespaceNode;
import org.gxml.bridge.cx.tree.XmlNode;
import org.gxml.bridge.cx.tree.XmlNodeFactory;
import org.gxml.bridge.cx.tree.XmlNodeMutator;
import org.gxml.exceptions.GxmlException;
import org.gxml.names.NamespaceBinding;

public class XmlNodeModel
    implements Model<XmlNode>
{

    public void stream(final XmlNode node, final boolean copyNamespaces, final ContentHandler handler)
        throws GxmlException
    {
        Walker.walk(node, copyNamespaces, handler);
    }

    public int compare(final XmlNode n1, final XmlNode n2)
    {
        return Ordering.compareNodes(n1, n2, this);
    }

    public Iterable<QName> getAttributeNames(XmlNode node, boolean orderCanonical)
    {
        return node.getAttributeNames(orderCanonical);
    }

    public String getAttributeStringValue(XmlNode parent, String namespaceURI, String localName)
    {
        return parent.getAttributeStringValue(namespaceURI, localName);
   }

    public URI getBaseURI(XmlNode node)
    {
        return node.getBaseURI();
    }

    public URI getDocumentURI(XmlNode node)
    {
        return node.getDocumentURI();
    }

    public String getLocalName(XmlNode node)
    {
        return node.getLocalName();
    }

    public Iterable<NamespaceBinding> getNamespaceBindings(final XmlNode node)
    {
        return node.getNamespaceBindings();
    }
    
    public String getNamespaceForPrefix(final XmlNode node, final String prefix)
    {
        return node.getNamespaceForPrefix(prefix);
    }

    public Iterable<String> getNamespaceNames(final XmlNode node, final boolean orderCanonical)
    {
        return node.getNamespaceNames(orderCanonical);
    }

    public String getNamespaceURI(final XmlNode node)
    {
        return node.getNamespaceURI();
    }
    
    public XmlNode getNodeId(final XmlNode node)
    {
        return node.getNodeId();
    }

    public NodeKind getNodeKind(final XmlNode node)
    {
        return node.getNodeKind();
    }

    public String getPrefix(XmlNode node)
    {
        return node.getPrefix();
    }

    public String getStringValue(XmlNode node)
    {
        return node.getStringValue();
    }

    public boolean hasAttributes(XmlNode node)
    {
        return node.hasAttributes();
    }

    public boolean hasChildren(XmlNode node)
    {
        return node.hasChildren();
    }

    public boolean hasNamespaces(XmlNode node)
    {
        return node.hasNamespaces();
    }

    public boolean hasNextSibling(XmlNode node)
    {
        return node.hasNextSibling();
    }

    public boolean hasParent(XmlNode node)
    {
        return node.hasParent();
    }

    public boolean hasPreviousSibling(XmlNode node)
    {
        return node.hasPreviousSibling();
    }

    public boolean isAttribute(XmlNode node)
    {
        return node.isAttribute();
    }

    public boolean isElement(XmlNode node)
    {
        return node.isElement();
    }

    public boolean isId(XmlNode node)
    {
        return node.isId();
    }

    public boolean isIdRefs(XmlNode node)
    {
        return node.isIdRefs();
    }

    public boolean isNamespace(XmlNode node)
    {
        return node.isNamespace();
    }

    public boolean isSameNode(XmlNode one, XmlNode two)
    {
        return one.isSameNode(two);
    }

    public boolean isText(XmlNode node)
    {
        return node.isText();
    }

    public boolean matches(XmlNode node, NodeKind nodeKind, String namespaceURI, String localName)
    {
        return node.matches(nodeKind, namespaceURI, localName);
    }

    public boolean matches(XmlNode node, String namespaceURI, String localName)
    {
        return node.matches(namespaceURI, localName);
    }

    public XmlNode getAttribute(XmlNode node, String namespaceURI, String localName)
    {
        return node.getAttribute(namespaceURI, localName);
    }

    public XmlNode getElementById(XmlNode context, String id)
    {
        return context.getElementById(id);
    }

    public XmlNode getFirstChild(XmlNode origin)
    {
        return origin.getFirstChild();
    }

    public XmlNode getFirstChildElement(XmlNode node)
    {
        return node.getFirstChildElement();
    }

    public XmlNode getFirstChildElementByName(XmlNode node, String namespaceURI, String localName)
    {
        return node.getFirstChildElementByName(namespaceURI, localName);
    }

    public XmlNode getLastChild(XmlNode node)
    {
        return node.getLastChild();
    }

    public XmlNode getNextSibling(XmlNode node)
    {
        if (node.isAttribute() || node.isNamespace())
            return null;
        return node.getNextSibling();
    }

    public XmlNode getNextSiblingElement(XmlNode node)
    {
        return node.getNextSiblingElement();
    }

    public XmlNode getNextSiblingElementByName(XmlNode node, String namespaceURI, String localName)
    {
        return node.getNextSiblingElementByName(namespaceURI, localName);
    }

    public XmlNode getParent(XmlNode origin)
    {
        return origin.getParent();
    }

    public XmlNode getPreviousSibling(XmlNode node)
    {
        if (node.isAttribute() || node.isNamespace())
            return null;
        return node.getPreviousSibling();
    }

    public XmlNode getRoot(XmlNode node)
    {
        return node.getRoot();
    }

    public Iterable<XmlNode> getAncestorAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableAncestorAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getAncestorOrSelfAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableAncestorOrSelfAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getAttributeAxis(XmlNode node, boolean inherit)
    {
        if ( (node != null) && node.isElement())
        {
            if (!inherit)
                return new NodeIterable((XmlElementNode)node, false);
            else
            {
                // first, create an ordered list of scopes (root-order)
                final LinkedList<XmlNode> ancestors = new LinkedList<XmlNode>();
                XmlNode parent = node.getParent();
                while (parent != null)
                {
                    if (parent.hasAttributes())
                        ancestors.addFirst(parent);
                    parent = parent.getParent();
                }
                // the strings are there for the inherited attributes, all in the xml namespace
                final Map<String, XmlNode> attributeMap = new HashMap<String, XmlNode>();
                final XmlNodeFactory factory = new XmlNodeFactory();
                final XmlNodeMutator agency = new XmlNodeMutator();
                // iterate over the collection of ancestors, from the root to the current node.
                Iterable<XmlNode> attributes = null;
                for (final XmlNode scope : ancestors)
                {
                    // we know that every scope is an XmlElementNode, because only
                    // elements can have attributes (which is what gets them into the list).
                    attributes = new NodeIterable((XmlElementNode)scope, false);
                    for (final XmlNode attribute : attributes)
                    {
                        if (attribute.getNamespaceURI().equals(XMLConstants.XML_NS_URI))
                        {
                            // create a fake attribute with node as the parent.
                            XmlAttributeNode attr = factory.createAttribute(node.getRoot(), attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix(), attribute.getStringValue());
                            agency.setParent(attr, (XmlContainerNode)node);
                            attributeMap.put(attribute.getLocalName(), attr);
                        }
                    }
                }
                // now, add the current node--all attributes, not just heritable ones
                attributes = new NodeIterable((XmlElementNode)node, false);
                for (final XmlNode attribute : attributes)
                {
                    attributeMap.put(attribute.getLocalName(), attribute);
                }
                // the trick in all of the above is that each subsequent put of the
                // same string is going to overwrite previous ones.  now ... whether
                // this is correct for all the heritable attributes, i'm not quite certain.
                return attributeMap.values();
            }
        }
        return new UnaryIterable<XmlNode>(null);
    }

    public Iterable<XmlNode> getChildAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableChildAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getChildElements(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableChildAxisElements<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getChildElementsByName(final XmlNode node, final String namespaceURI, final String localName)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableChildAxisElementsByName<XmlNode>(node, namespaceURI, localName, this);
    }

    public Iterable<XmlNode> getDescendantAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableDescendantAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getDescendantOrSelfAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableDescendantOrSelfAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getFollowingAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableFollowingAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getFollowingSiblingAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterableFollowingSiblingAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getNamespaceAxis(XmlNode node, boolean inherit)
    {
        if ( (node != null) && node.isElement() )
        {
            if (!inherit)
                return new NodeIterable((XmlElementNode)node, true);
            else
            {
                final LinkedList<XmlNode> ancestors = new LinkedList<XmlNode>();
                XmlNode parent = node.getParent();
                while (parent != null)
                {
                    if (parent.hasNamespaces())
                        ancestors.addFirst(parent);
                    parent = parent.getParent();
                }
                final Map<String, XmlNode> namespaceMap = new HashMap<String, XmlNode>();
                final XmlNodeFactory factory = new XmlNodeFactory();
                final XmlNodeMutator agency = new XmlNodeMutator();
                Iterable<XmlNode> localNamespaces = null;
                for (final XmlNode ancestor : ancestors)
                {
                    localNamespaces = new NodeIterable((XmlElementNode)ancestor, true);
                    for (final XmlNode namespace : localNamespaces)
                    {
                        final String prefix = namespace.getLocalName();
                        final String uri = namespace.getStringValue();
                        if (uri.trim().length() == 0) // a cancellation
                            namespaceMap.remove(prefix);
                        else
                        {
                            // create a fake namespace node with parent set to the current node
                            XmlNamespaceNode ns = factory.createNamespace(node.getRoot(), prefix, uri);
                            agency.setParent(ns, (XmlContainerNode)node);
                            namespaceMap.put(prefix, ns);
                        }
                    }
                }
                localNamespaces = new NodeIterable((XmlElementNode)node, true);
                for (XmlNode namespace : localNamespaces)
                {
                    final String prefix = namespace.getLocalName();
                    final String uri = namespace.getStringValue();
                    if (uri.trim().length() == 0) // a cancellation
                        namespaceMap.remove(prefix);
                    else
                        namespaceMap.put(prefix, namespace);
                }
                // Add a namespace node for the xml prefix that is implicitly
                // declared by the XML Namespaces Recommendation.
                XmlNamespaceNode xmlns = factory.createNamespace(node.getRoot(), XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
                agency.setParent(xmlns, (XmlContainerNode)node);
                namespaceMap.put(XMLConstants.XML_NS_PREFIX, xmlns);
                return namespaceMap.values();
            }
        }
        return new UnaryIterable<XmlNode>(null);
    }

    public Iterable<XmlNode> getPrecedingAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterablePrecedingAxis<XmlNode>(node, this);
    }

    public Iterable<XmlNode> getPrecedingSiblingAxis(final XmlNode node)
    {
        if (node == null)
            return new UnaryIterable<XmlNode>(null);
        return new IterablePrecedingSiblingAxis<XmlNode>(node, this);
    }

}
