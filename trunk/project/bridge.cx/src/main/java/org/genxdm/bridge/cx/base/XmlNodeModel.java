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
import java.util.LinkedList;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.bridge.cx.tree.NodeIterable;
import org.genxdm.bridge.cx.tree.Walker;
import org.genxdm.bridge.cx.tree.XmlAttributeNode;
import org.genxdm.bridge.cx.tree.XmlContainerNode;
import org.genxdm.bridge.cx.tree.XmlElementNode;
import org.genxdm.bridge.cx.tree.XmlNamespaceNode;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridge.cx.tree.XmlNodeFactory;
import org.genxdm.bridge.cx.tree.XmlNodeMutator;
import org.genxdm.bridgekit.axes.IterableAncestorAxis;
import org.genxdm.bridgekit.axes.IterableAncestorOrSelfAxis;
import org.genxdm.bridgekit.axes.IterableChildAxis;
import org.genxdm.bridgekit.axes.IterableChildAxisElements;
import org.genxdm.bridgekit.axes.IterableChildAxisElementsByName;
import org.genxdm.bridgekit.axes.IterableDescendantAxis;
import org.genxdm.bridgekit.axes.IterableDescendantOrSelfAxis;
import org.genxdm.bridgekit.axes.IterableFollowingAxis;
import org.genxdm.bridgekit.axes.IterableFollowingSiblingAxis;
import org.genxdm.bridgekit.axes.IterablePrecedingAxis;
import org.genxdm.bridgekit.axes.IterablePrecedingSiblingAxis;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.NamespaceBinding;

public class XmlNodeModel
    implements Model<XmlNode>
{

    @Override
    public void stream(final XmlNode node, final ContentHandler handler)
        throws GenXDMException
    {
        PreCondition.assertNotNull(node, "node");
        Walker.walk(node, handler);
    }

    @Override
    public int compare(final XmlNode n1, final XmlNode n2)
    {
        return Ordering.compareNodes(n1, n2, this);
    }

    @Override
    public Iterable<QName> getAttributeNames(XmlNode node, boolean orderCanonical)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getAttributeNames(orderCanonical);
    }

    @Override
    public String getAttributeStringValue(XmlNode parent, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        return parent.getAttributeStringValue(namespaceURI, localName);
   }

    @Override
    public URI getBaseURI(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getBaseURI();
    }

    @Override
    public URI getDocumentURI(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getDocumentURI();
    }

    @Override
    public String getLocalName(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getLocalName();
    }

    @Override
    public Iterable<NamespaceBinding> getNamespaceBindings(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNamespaceBindings();
    }
    
    @Override
    public String getNamespaceForPrefix(final XmlNode node, final String prefix)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNamespaceForPrefix(prefix);
    }

    @Override
    public Iterable<String> getNamespaceNames(final XmlNode node, final boolean orderCanonical)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNamespaceNames(orderCanonical);
    }

    @Override
    public String getNamespaceURI(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNamespaceURI();
    }
    
    @Override
    public XmlNode getNodeId(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNodeId();
    }

    @Override
    public NodeKind getNodeKind(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNodeKind();
    }

    @Override
    public String getPrefix(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getPrefix();
    }

    @Override
    public String getStringValue(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getStringValue();
    }

    @Override
    public boolean hasAttributes(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.hasAttributes();
    }

    @Override
    public boolean hasChildren(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.hasChildren();
    }

    @Override
    public boolean hasNamespaces(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.hasNamespaces();
    }

    @Override
    public boolean hasNextSibling(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.hasNextSibling();
    }

    @Override
    public boolean hasParent(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.hasParent();
    }

    @Override
    public boolean hasPreviousSibling(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.hasPreviousSibling();
    }

    @Override
    public boolean isAttribute(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.isAttribute();
    }

    @Override
    public boolean isElement(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.isElement();
    }

    @Override
    public boolean isId(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.isId();
    }

    @Override
    public boolean isIdRefs(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.isIdRefs();
    }

    @Override
    public boolean isNamespace(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.isNamespace();
    }

    @Override
    public boolean isText(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.isText();
    }

    @Override
    public boolean matches(XmlNode node, NodeKind nodeKind, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");
        return node.matches(nodeKind, namespaceURI, localName);
    }

    @Override
    public boolean matches(XmlNode node, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");
        return node.matches(namespaceURI, localName);
    }

    @Override
    public XmlNode getAttribute(XmlNode node, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getAttribute(namespaceURI, localName);
    }

    @Override
    public XmlNode getElementById(XmlNode context, String id)
    {
        PreCondition.assertNotNull(context, "node");
        return context.getElementById(id);
    }

    @Override
    public XmlNode getFirstChild(XmlNode origin)
    {
        PreCondition.assertNotNull(origin, "node");
        return origin.getFirstChild();
    }

    @Override
    public XmlNode getFirstChildElement(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getFirstChildElement();
    }

    @Override
    public XmlNode getFirstChildElementByName(XmlNode node, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getFirstChildElementByName(namespaceURI, localName);
    }

    @Override
    public XmlNode getLastChild(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getLastChild();
    }

    @Override
    public XmlNode getNextSibling(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        if (node.isAttribute() || node.isNamespace())
            return null;
        return node.getNextSibling();
    }

    @Override
    public XmlNode getNextSiblingElement(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNextSiblingElement();
    }

    @Override
    public XmlNode getNextSiblingElementByName(XmlNode node, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNextSiblingElementByName(namespaceURI, localName);
    }

    @Override
    public XmlNode getParent(XmlNode origin)
    {
        PreCondition.assertNotNull(origin, "node");
        return origin.getParent();
    }

    @Override
    public XmlNode getPreviousSibling(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        if (node.isAttribute() || node.isNamespace())
            return null;
        return node.getPreviousSibling();
    }

    @Override
    public XmlNode getRoot(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getRoot();
    }

    @Override
    public Iterable<XmlNode> getAncestorAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableAncestorAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getAncestorOrSelfAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableAncestorOrSelfAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getAttributeAxis(XmlNode node, boolean inherit)
    {
        PreCondition.assertNotNull(node, "node");
        if (node.isElement())
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
                            XmlAttributeNode attr = factory.createAttribute(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix(), attribute.getStringValue());
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

    @Override
    public Iterable<XmlNode> getChildAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableChildAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getChildElements(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableChildAxisElements<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getChildElementsByName(final XmlNode node, final String namespaceURI, final String localName)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableChildAxisElementsByName<XmlNode>(node, namespaceURI, localName, this);
    }

    @Override
    public Iterable<XmlNode> getDescendantAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableDescendantAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getDescendantOrSelfAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableDescendantOrSelfAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getFollowingAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableFollowingAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getFollowingSiblingAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableFollowingSiblingAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getNamespaceAxis(XmlNode node, boolean inherit)
    {
        PreCondition.assertNotNull(node, "node");
        if ( node.isElement() )
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
                            XmlNamespaceNode ns = factory.createNamespace(prefix, uri);
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
                XmlNamespaceNode xmlns = factory.createNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
                agency.setParent(xmlns, (XmlContainerNode)node);
                namespaceMap.put(XMLConstants.XML_NS_PREFIX, xmlns);
                return namespaceMap.values();
            }
        }
        return new UnaryIterable<XmlNode>(null);
    }

    @Override
    public Iterable<XmlNode> getPrecedingAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterablePrecedingAxis<XmlNode>(node, this);
    }

    @Override
    public Iterable<XmlNode> getPrecedingSiblingAxis(final XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterablePrecedingSiblingAxis<XmlNode>(node, this);
    }

}
