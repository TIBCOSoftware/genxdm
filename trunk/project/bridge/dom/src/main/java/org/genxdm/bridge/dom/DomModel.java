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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.io.ContentHandler;
import org.genxdm.bridge.dom.axes.AxisAncestorIterable;
import org.genxdm.bridge.dom.axes.AxisAncestorOrSelfIterable;
import org.genxdm.bridge.dom.axes.AxisChildElementIterable;
import org.genxdm.bridge.dom.axes.AxisChildIterable;
import org.genxdm.bridge.dom.axes.AxisDescendantOrSelfIterable;
import org.genxdm.bridge.dom.axes.AxisFollowingSiblingIterable;
import org.genxdm.bridge.dom.axes.AxisPrecedingSiblingIterable;
import org.genxdm.bridgekit.axes.IterableDescendantAxis;
import org.genxdm.bridgekit.axes.IterableFollowingAxis;
import org.genxdm.bridgekit.axes.IterablePrecedingAxis;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.bridgekit.names.DefaultNamespaceBinding;
import org.genxdm.bridgekit.names.QNameComparator;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceBinding;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.w3c.dom.TypeInfo;

public class DomModel
    implements Model<Node>
{

    public int compare(Node one, Node two)
    {
        return Ordering.compareNodes(one, two, this);
    }

    public Iterable<Node> getAncestorAxis(Node node)
    {
        if (null != node)
        {
            if (null != DomSupport.getParentNode(node))
            {
                return new AxisAncestorIterable(node);
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getAncestorOrSelfAxis(Node node)
    {
        if (null != node)
        {
            return new AxisAncestorOrSelfIterable(node);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Node getAttribute(Node node, String namespaceURI, String localName)
    {
        PreCondition.assertArgumentNotNull(node, "node");

        // Checking to see if there are any attributes saves the creation of {@link NamedNodeMap}.
        if (node.hasAttributes())
        {
            final NamedNodeMap attributes = node.getAttributes();
            if (null != attributes)
            {
                if (0 == namespaceURI.length())
                {
                    return attributes.getNamedItemNS(null, localName);
                }
                else
                {
                    return attributes.getNamedItemNS(namespaceURI, localName);
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public Iterable<Node> getAttributeAxis(Node origin, boolean inherit)
    {
        if (origin != null && origin instanceof Element)
        {
            final Element element = (Element)origin;
            if (inherit)
            {
                final LinkedList<Node> scopes = new LinkedList<Node>();
                Node currentNode = getParent(element);
                while (null != currentNode)
                {
                    if (currentNode.hasAttributes())
                    {
                        scopes.addFirst(currentNode);
                    }
                    currentNode = getParent(currentNode);
                }
                final HashMap<String, Node> mappings = new HashMap<String, Node>();
                for (final Node scope : scopes)
                {
                    for (final Node attribute : getAttributeAxis(scope, false))
                    {
                        if (attribute.getNamespaceURI().equals(XMLConstants.XML_NS_URI))
                        {
                            // The faux attribute created must be parented by this element.
                            final String localName = getLocalName(attribute);
                            mappings.put(localName, new WrapAttribute((Attr)attribute, element));
                        }
                    }
                }
                for (final Node attribute : getAttributeAxis(element, false))
                {
                    mappings.put(attribute.getLocalName(), attribute);
                }
                return mappings.values();
            }
            else
            {
                if (element.hasAttributes())
                {
                    final NamedNodeMap mixed = element.getAttributes();
                    // We might get lucky and can optimize one attribute.
                    Node attribute = null;
                    if (null != mixed)
                    {
                        final int length = mixed.getLength();
                        int realLength = 0;
                        for (int i = 0; i < length; i++)
                        {
                            final Node node = mixed.item(i);
                            final String namespaceURI = node.getNamespaceURI();
                            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                            {
                                attribute = node;
                                realLength++;
                            }
                        }

                        if (realLength > 0)
                        {
                            if (realLength == 1)
                            {
                                return new UnaryIterable<Node>(PreCondition.assertArgumentNotNull(attribute));
                            }
                            else
                            {
                                final ArrayList<Node> axis = new ArrayList<Node>(realLength);
                                for (int i = 0; i < length; i++)
                                {
                                    final Node node = mixed.item(i);
                                    final String namespaceURI = node.getNamespaceURI();
                                    if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                                    {
                                        axis.add(node);
                                    }
                                }
                                return axis;
                            }
                        }
                        else
                        {
                            return Collections.emptyList();
                        }
                    }
                    else
                    {
                        return Collections.emptyList();
                    }
                }
                else
                {
                    return Collections.emptyList();
                }
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<QName> getAttributeNames(Node node, boolean orderCanonical)
    {
        if (hasAttributes(node))
        {
            final List<QName> names = new LinkedList<QName>();
            for (final Node attribute : getAttributeAxis(node, false))
            {
                // Note: We take care that the arguments to the name are actually symbols by calling the public API.
                final QName name = new QName(getNamespaceURI(attribute), getLocalName(attribute), getPrefix(attribute));
                names.add(name);
            }
            if (orderCanonical)
            {
                Collections.sort(names, new QNameComparator());
            }
            return names;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public String getAttributeStringValue(Node parent, String namespaceURI, String localName)
    {
        return getStringValue(getAttribute(parent, namespaceURI, localName));
    }

    public URI getBaseURI(final Node node)
    {
        // TODO: implement
        return null;//DomSupport.getBaseURI(node);
    }
    
    public Iterable<Node> getChildAxis(Node node)
    {
        if (null != node)
        {
            switch (node.getNodeType())
            {
                case Node.ELEMENT_NODE:
                case Node.DOCUMENT_FRAGMENT_NODE:
                case Node.DOCUMENT_NODE:
                {
                    return new AxisChildIterable(node);
                }
                default:
                {
                    return Collections.emptyList();
                }
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getChildElements(Node origin)
    {
        if (null != origin)
        {
            if (null != DomSupport.getFirstChild(origin))
            {
                return new AxisChildElementIterable(origin);
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getChildElementsByName(Node origin, String namespaceURI, String localName)
    {
        if (origin != null)
        {
            return DomSupport.getChildElementsByName(origin, namespaceURI, localName);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getDescendantAxis(Node node)
    {
        if (null != node)
        {
            return new IterableDescendantAxis<Node>(node, this);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getDescendantOrSelfAxis(Node node)
    {
        if (null != node)
        {
            return new AxisDescendantOrSelfIterable(node);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public URI getDocumentURI(Node node)
    {
        return DomSupport.getDocumentURI(node);
    }

    public Node getFirstChild(Node origin)
    {
        return DomSupport.getFirstChild(origin);
    }

    public Node getFirstChildElement(Node origin)
    {
        return DomSupport.getFirstChildElement(origin);
    }

    public Node getFirstChildElementByName(Node origin, String namespaceURI, String localName)
    {
        final short nodeType = origin.getNodeType();

        if (nodeType == Node.ELEMENT_NODE)
        {
            final Element element = (Element)origin;

            final NodeList elements;

            if (null != namespaceURI)
            {
                if (namespaceURI.length() > 0)
                {
                    if (null != localName)
                    {
                        elements = element.getElementsByTagNameNS(namespaceURI, localName);
                    }
                    else
                    {
                        elements = element.getElementsByTagNameNS(namespaceURI, "*");
                    }
                }
                else
                {
                    if (null != localName)
                    {
                        elements = element.getElementsByTagNameNS(null, localName);
                    }
                    else
                    {
                        elements = element.getElementsByTagNameNS(null, "*");
                    }
                }
            }
            else
            {
                if (null != localName)
                {
                    elements = element.getElementsByTagNameNS("*", localName);
                }
                else
                {
                    elements = element.getElementsByTagNameNS("*", "*");
                }
            }

            final int length = elements.getLength();

            if (length > 0)
            {
                return elements.item(0);
            }
            else
            {
                return null;
            }
        }
        else if (nodeType == Node.DOCUMENT_NODE)
        {
            final Document document = (Document)origin;

            final NodeList elements;
            if (namespaceURI.length() > 0)
            {
                elements = document.getElementsByTagNameNS(namespaceURI, localName);
            }
            else
            {
                elements = document.getElementsByTagNameNS(null, localName);
            }

            final int length = elements.getLength();

            if (length > 0)
            {
                return elements.item(0);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public Iterable<Node> getFollowingAxis(Node node)
    {
        if (null != node)
        {
            return new IterableFollowingAxis<Node>(node, this);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getFollowingSiblingAxis(Node node)
    {
        if (null != node)
        {
            if (null != node.getNextSibling())
            {
                return new AxisFollowingSiblingIterable(node);
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Node getLastChild(Node origin)
    {
        switch (getNodeKind(origin))
        {
            case ATTRIBUTE:
            case NAMESPACE:
            {
                return null;
            }
            default:
            {
                return origin.getLastChild();
            }
        }
    }

    public String getLocalName(Node node)
    {
        return DomSupport.getLocalNameAsString(node);
    }

    public Iterable<Node> getNamespaceAxis(Node node, boolean inherit)
    {
        if (inherit)
        {
            return getNamespacesInScope(node);
        }
        else
        {
            return getNamespaces(node);
        }
    }

    public Iterable<NamespaceBinding> getNamespaceBindings(Node origin)
    {
        PreCondition.assertArgumentNotNull(origin, "origin");
        if (origin.hasAttributes())
        {
            final NamedNodeMap mixed = origin.getAttributes();
            if (null != mixed)
            {
                final int length = mixed.getLength();

                final int realLength = DomSupport.namespaceCount(mixed);

                if (realLength > 0)
                {
                    final ArrayList<NamespaceBinding> bindings = new ArrayList<NamespaceBinding>(realLength);

                    for (int i = 0; i < length; i++)
                    {
                        final Node node = mixed.item(i);

                        final String namespaceURI = node.getNamespaceURI();

                        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                        {
                            // Note how we use the local-name accessor to handle namespaces correctly.
                            bindings.add(new DefaultNamespaceBinding(getLocalName(node), node.getNodeValue()));
                        }
                    }

                    return bindings;
                }
            }
        }
        return Collections.emptyList();
    }
    
    public String getNamespaceForPrefix(final Node node, final String prefix)
    {
        for (NamespaceBinding binding : getNamespaceBindings(node))
        {
            if (binding.getPrefix().equals(prefix))
                return binding.getNamespaceURI();
        }
        return null;
    }

    public Iterable<String> getNamespaceNames(Node node, boolean orderCanonical)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (node.hasAttributes())
        {
            final NamedNodeMap mixed = node.getAttributes();
            if (null != mixed)
            {
                final int length = mixed.getLength();

                final int realLength = DomSupport.namespaceCount(mixed);

                if (realLength > 0)
                {
                    final ArrayList<String> prefixes = new ArrayList<String>(realLength);

                    for (int i = 0; i < length; i++)
                    {
                        final Node candidate = mixed.item(i);

                        final String namespaceURI = candidate.getNamespaceURI();

                        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                        {
                            // Note how we use the local-name accessor to handle namespaces correctly.
                            prefixes.add(getLocalName(candidate));
                        }
                    }
                    if (orderCanonical)
                    {
                        Collections.sort(prefixes);
                    }
                    return prefixes;
                }
            }
        }
        return Collections.emptyList();
    }

    public String getNamespaceURI(Node node)
    {
        return DomSupport.getNamespaceURIAsString(node);
    }

    public Node getNextSibling(Node origin)
    {
        return DomSupport.getNextSibling(origin);
    }

    public Node getNextSiblingElement(Node node)
    {
        return getNextSiblingElementByName(node, null, null);
    }

    public Node getNextSiblingElementByName(Node node, String namespaceURI, String localName)
    {
        final Node current = getNextSibling(node);
        if (null != current)
        {
            if (matches(current, NodeKind.ELEMENT, namespaceURI, localName))
            {
                return current;
            }
            else
            {
                return getNextSiblingElementByName(current, namespaceURI, localName);
            }
        }
        else
        {
            return null;
        }
    }

    public NodeKind getNodeKind(Node node)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        return DomSupport.getNodeKind(node);
    }

    public Node getParent(Node origin)
    {
        // Use DOM Support here because attribute nodes require special handling.
        return DomSupport.getParentNode(origin);
    }

    public Iterable<Node> getPrecedingAxis(Node node)
    {
        if (null != node)
        {
            return new IterablePrecedingAxis<Node>(node, this);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Node> getPrecedingSiblingAxis(Node node)
    {
        if (null != node)
        {
            if (null != node.getPreviousSibling())
            {
                return new AxisPrecedingSiblingIterable(node);
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public String getPrefix(Node node)
    {
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
            {
                return getElementPrefix(node);
            }
            case Node.ATTRIBUTE_NODE:
            {
                return getAttributePrefix(node);
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                return XMLConstants.DEFAULT_NS_PREFIX;
            }
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
            case Node.COMMENT_NODE:
            case Node.DOCUMENT_NODE:
            {
                return null;
            }
            default:
            {
                throw new UnsupportedOperationException(Short.toString(node.getNodeType()));
            }
        }
    }

    public Node getPreviousSibling(Node origin)
    {
        return origin.getPreviousSibling();
    }

    public Node getRoot(Node node)
    {
        Node current = node;
        Node parentNode = DomSupport.getParentNode(current);
        while (null != parentNode)
        {
            current = parentNode;
            parentNode = current.getParentNode();
        }
        return current;
    }

    public String getStringValue(Node node)
    {
        if (null != node)
        {
            return DomSupport.getStringValue(node, DomSupport.DEFAULT_ATOM_SEPARATOR, DomSupport.DEFAULT_EMULATION);
        }
        else
        {
            return null;
        }
    }

    public boolean hasAttributes(Node node)
    {
        if (node.hasAttributes())
        {
            final NamedNodeMap namespacesAndAttributes = node.getAttributes();
            final int length = namespacesAndAttributes.getLength();

            final int nsLength = DomSupport.namespaceCount(namespacesAndAttributes);
            
            if (length > nsLength)
                return true;
        }
        return false;
    }

    public boolean hasChildren(Node node)
    {
        switch (getNodeKind(node))
        {
            case ATTRIBUTE:
            case NAMESPACE:
            {
                return false;
            }
            default:
            {
                return node.hasChildNodes();
            }
        }
    }

    public boolean hasNamespaces(Node origin)
    {
        return DomSupport.hasNamespaces(origin);
    }

    public boolean hasNextSibling(Node node)
    {
        return null != getNextSibling(node);
    }

    public boolean hasParent(Node node)
    {
        return null != getParent(node);
    }

    public boolean hasPreviousSibling(Node node)
    {
        return null != getPreviousSibling(node);
    }

    public boolean isAttribute(Node node)
    {
        if (null != node)
        {
            return DomSupport.isAttribute(node);
        }
        else
        {
            return false;
        }
    }

    public boolean isElement(Node node)
    {
        if (null != node)
        {
            return DomSupport.isElement(node);
        }
        else
        {
            return false;
        }
    }
    
    public boolean isId(Node node)
    {
        if (isAttribute(node))
        {
            Attr attr = (Attr)node;
            // TODO: do we need to special-case xml:id?
            return attr.isId();
        }
        if (isElement(node))
        {
            // TODO: if it's PSVI, it may be an ID if there's a single value derived from xs:ID
        }
        return false;
    }
    
    public boolean isIdRefs(Node node)
    {
        if (isAttribute(node))
        {
            //Attr attr = (Attr)node;
            //TypeInfo ti = attr.getSchemaTypeInfo();
            // TODO: now determine whether it's IDREF or IDREFS or xs:IDREF or xs:IDREFS
            // fall through until that's done
        }
        if (isElement(node))
        {
            // TODO: if it's PSVI, it returns true if any atom of its content is derived
            // from xs:IDREF or xs:IDREFS
        }
        return false;
    }

    public boolean isNamespace(Node node)
    {
        if (null != node)
        {
            return DomSupport.isNamespace(node);
        }
        else
        {
            return false;
        }
    }

    public DomNID getNodeId(Node node)
    {
        return new DomNID(node);
    }

    public boolean isText(Node node)
    {
        if (null != node)
        {
            return DomSupport.isText(node);
        }
        else
        {
            return false;
        }
    }

    public boolean matches(Node node, NodeKind nodeKind, String namespaceURI, String localName)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (null != nodeKind)
        {
            if (DomSupport.getNodeKind(node) != nodeKind)
            {
                return false;
            }
        }
        return matches(node, namespaceURI, localName);
    }

    public boolean matches(Node node, String namespaceURI, String localName)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (namespaceURI != null)
        {
            final String namespace = getNamespaceURI(node);
            // We'd like to compare using ==, but there is no guarantee that we have a symbol!
            if (!namespaceURI.equals(namespace))
            {
                return false;
            }
        }
        if (localName != null)
        {
            final String lName = getLocalName(node);
            if (!localName.equals(lName))
            {
                return false;
            }
        }
        return true;
    }

    public void stream(Node node, boolean copyNamespaces, ContentHandler handler)
        throws GxmlException
    {
        switch (getNodeKind(node))
        {
            case ELEMENT:
                {
                    handler.startElement(getNamespaceURI(node), getLocalName(node), getElementPrefix(node));
                    try
                    {
                        if (node.hasAttributes())
                        {
                            final NamedNodeMap mixed = node.getAttributes();
                            if (null != mixed)
                            {
                                final int length = mixed.getLength();
    
                                if (copyNamespaces)
                                {
                                    // The namespace "attributes" come before the real attributes.
                                    for (int i = 0; i < length; i++)
                                    {
                                        final Node namespace = mixed.item(i);
                                        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespace.getNamespaceURI()))
                                        {
                                            stream(namespace, copyNamespaces, handler);
                                        }
                                    }
                                }
    
                                // The real attributes.
                                for (int i = 0; i < length; i++)
                                {
                                    final Node attribute = mixed.item(i);
                                    if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI()))
                                    {
                                        stream(attribute, copyNamespaces, handler);
                                    }
                                }
                            }
                        }
                        Node child = node.getFirstChild();
                        while (null != child)
                        {
                            stream(child, copyNamespaces, handler);
                            child = child.getNextSibling();
                        }
                    }
                    finally
                    {
                        handler.endElement();
                    }
                }
                break;
            case ATTRIBUTE:
                {
                    final String prefix = copyNamespaces ? getAttributePrefix(node) : "";
                    handler.attribute(getNamespaceURI(node), getLocalName(node), prefix, node.getNodeValue(), null);
                }
                break;
                case TEXT:
                {
                    handler.text(node.getNodeValue());
                }
                break;
                case DOCUMENT:
                {
                    handler.startDocument(getDocumentURI(node), null);
                    try
                    {
                        Node child = node.getFirstChild();
                        while (null != child)
                        {
                            stream(child, copyNamespaces, handler);
                            child = child.getNextSibling();
                        }
                    }
                    finally
                    {
                        handler.endDocument();
                    }
                }
                break;
            case NAMESPACE:
                {
                    if (copyNamespaces)
                    {
                        final String prefix = DomSupport.getLocalNameAsString(node);
                        final String uri = node.getNodeValue();
                        handler.namespace(prefix, uri);
                    }
                }
                break;
            case COMMENT:
                {
                    handler.comment(node.getNodeValue());
                }
                break;
            case PROCESSING_INSTRUCTION:
                {
                    handler.processingInstruction(node.getNodeName(), node.getNodeValue());
                }
                break;
            default:
                {
                    throw new AssertionError(getNodeKind(node));
                }
        }
    }

    public String getAttributePrefix(final Node node)
    {
        final String namespaceURI = node.getNamespaceURI();

        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
        {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
        else
        {
            final String prefix = node.getPrefix();
            if (null != prefix)
            {
                return prefix;
            }
            else
            {
                return XMLConstants.DEFAULT_NS_PREFIX;
            }
        }
    }
    
    public Node getElementById(final Node context, final String id)
    {
    	Document doc = DomSupport.getOwner(context);
    	return doc.getElementById(id);
    }

    public String getElementPrefix(final Node node)
    {
        final String prefix = node.getPrefix();
        if (null != prefix)
        {
            return prefix;
        }
        else
        {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
    }

    private static Iterable<Node> getNamespaces(final Node node)
    {
        if (node instanceof Element)
        {
            final Element element = (Element)node;
            if (element.hasAttributes())
            {
                final Map<String, Node> mappings = new HashMap<String, Node>();
                final NamedNodeMap mixed = element.getAttributes();
                if (null != mixed)
                {
                    final int length = mixed.getLength();

                    final int realLength = DomSupport.namespaceCount(mixed);

                    if (realLength > 0)
                    {
                        for (int i = 0; i < length; i++)
                        {
                            final Node attribute = mixed.item(i);

                            final String namespaceURI = attribute.getNamespaceURI();

                            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                            {
                                final String prefix = DomSupport.getLocalNameAsString(attribute);
                                if (namespaceURI.length() == 0)
                                {
                                    // It's a cancellation.
                                }
                                else
                                {
                                    mappings.put(prefix, attribute);
                                }
                            }
                        }
                    }
                }
                return mappings.values();
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            // The namespace axis will be empty unless the context node is an
            // element.
            return Collections.emptyList();
        }
    }

    private static Iterable<Node> getNamespacesInScope(final Node node)
    {
        if (node instanceof Element)
        {
            final Element element = (Element)node;
            final LinkedList<Node> scopes = new LinkedList<Node>();
            Node currentNode = DomSupport.getParentNode(element);
            while (null != currentNode)
            {
                if (DomSupport.hasNamespaces(currentNode))
                {
                    scopes.addFirst(currentNode);
                }
                currentNode = DomSupport.getParentNode(currentNode);
            }
            final HashMap<String, Node> mappings = new HashMap<String, Node>();
            for (final Node scope : scopes)
            {
                for (final Node namespace : getNamespaces(scope))
                {
                    // The faux namespace created must be parented by this
                    // element.
                    final String prefix = namespace.getLocalName();
                    final String uri = namespace.getNodeValue();
                    if (uri.length() == 0)
                    {
                        mappings.remove(prefix);
                    }
                    else
                    {
                        mappings.put(prefix, new FauxNamespace(prefix, uri, element, element.getOwnerDocument()));
                    }
                }
            }
            for (final Node namespace : getNamespaces(element))
            {
                if (namespace.getNodeValue().length() == 0)
                {
                    // A cancellation.
                    mappings.remove(namespace.getLocalName());
                }
                else
                {
                    mappings.put(namespace.getLocalName(), namespace);
                }
            }
            // Add a namespace node for the xml prefix that is implicitly
            // declared by the XML Namespaces Recommendation.
            mappings.put(XMLConstants.XML_NS_PREFIX, new FauxNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, element, element.getOwnerDocument()));
            return mappings.values();
        }
        else
        {
            // The namespace axis will be empty unless the context node is an
            // element.
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unused")
    private final Node getNamespace(final Node node, final String prefix)
    {
        PreCondition.assertArgumentNotNull(prefix, "prefix");

        if (node.hasAttributes())
        {
            final NamedNodeMap attributes = node.getAttributes();
            if (0 == prefix.length())
            {
                return attributes.getNamedItemNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns");
            }
            else
            {
                return attributes.getNamedItemNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix);
            }
        }
        else
        {
            return null;
        }
    }

    private final NameSource nameBridge = new NameSource();
}
