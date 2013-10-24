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
package org.genxdm.bridge.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.NodeKind;
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
import org.genxdm.bridgekit.names.DefaultNamespaceBinding;
import org.genxdm.bridgekit.names.QNameComparator;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.NamespaceBinding;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

public class DomModel
    implements Model<Node>
{

    @Override
    public int compare(Node one, Node two)
    {
        return Ordering.compareNodes(one, two, this);
    }

    @Override
    public Iterable<Node> getAncestorAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        if (DomSupport.getParentNode(node) != null)
            return new IterableAncestorAxis<Node>(node, this);
        return Collections.emptyList();
    }

    @Override
    public Iterable<Node> getAncestorOrSelfAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableAncestorOrSelfAxis<Node>(node, this);
    }

    @Override
    public Node getAttribute(Node node, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");

        // Checking to see if there are any attributes saves the creation of {@link NamedNodeMap}.
        if (node.hasAttributes())
        {
            // TODO - why does this not simply use "getAttribute/getAttributeNS"?
            final NamedNodeMap attributes = node.getAttributes();
            if (null != attributes)
            {
                if (namespaceURI == null || 0 == namespaceURI.length())
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

    @Override
    public Iterable<Node> getAttributeAxis(Node origin, boolean inherit)
    {
        PreCondition.assertNotNull(origin, "node");
        if (origin instanceof Element)
        {
            final Element element = (Element)origin;
            if (inherit)
            {
                final LinkedList<Node> scopes = new LinkedList<Node>();
                Node currentNode = getParent(element);
                while (null != currentNode)
                {
                    if (currentNode.hasAttributes())
                        scopes.addFirst(currentNode);
                    currentNode = getParent(currentNode);
                }
                final HashMap<String, Node> mappings = new HashMap<String, Node>();
                for (final Node scope : scopes)
                {
                    for (final Node attribute : getAttributeAxis(scope, false))
                    {
                        String ns = attribute.getNamespaceURI();
                        if (ns == null)
                            ns = "";
                        if (ns.equals(XMLConstants.XML_NS_URI))
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
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Iterable<QName> getAttributeNames(Node node, boolean orderCanonical)
    {
        PreCondition.assertNotNull(node, "node");
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
        if (node instanceof Element)
            return Collections.emptyList();
        return null;
    }

    @Override
    public String getAttributeStringValue(Node parent, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        Node att = getAttribute(parent, namespaceURI, localName);
        if (att != null)
            return getStringValue(getAttribute(parent, namespaceURI, localName));
        return null;
    }

    @Override
    public URI getBaseURI(final Node node)
    {
        PreCondition.assertNotNull(node, "node");
        // TODO: implement; currently Feature.BASE_URI marked unsupported.
        return null;//DomSupport.getBaseURI(node);
    }
    
    @Override
    public Iterable<Node> getChildAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
            case Node.DOCUMENT_FRAGMENT_NODE:
            case Node.DOCUMENT_NODE:
            {
                return new IterableChildAxis<Node>(node, this);
            }
            default:
            {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public Iterable<Node> getChildElements(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
        if (getFirstChild(origin) != null)
            return new IterableChildAxisElements<Node>(origin, this);
        return Collections.emptyList();
    }

    @Override
    public Iterable<Node> getChildElementsByName(Node origin, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(origin, "node");
        return new IterableChildAxisElementsByName<Node>(origin, namespaceURI, localName, this);
    }

    @Override
    public Iterable<Node> getDescendantAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableDescendantAxis<Node>(node, this);
    }

    @Override
    public Iterable<Node> getDescendantOrSelfAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableDescendantOrSelfAxis<Node>(node, this);
    }

    @Override
    public URI getDocumentURI(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        final Document owner = DomSupport.getOwner(node);
        if ( (node == owner) || node.isSameNode(owner) )
        {
            final String documentURI;
            try
            {
                if (DomSupport.supportsCoreLevel3(owner))
                {
                    documentURI = owner.getDocumentURI();
                }
                else
                {
                    // TODO: Log something for DOM w/o Level 3 support?
                    // LOG.warn("DOM does not support DOM CORE version 3.0: Document.getDocumentURI");
                    return null;
                }
            }
            catch (final AbstractMethodError e)
            {
                // Thrown by org.apache.xerces.dom.DocumentImpl
                // TODO: Logging
                return null;
            }
            if (documentURI != null)
            {
                try
                {
                    // TODO: use the better parser in bridgekit, and stop throwing the assertion.
                    return new URI(documentURI);
                }
                catch (final URISyntaxException e)
                {
                    throw new AssertionError(e);
                }
            }
        }
        return null;
    }

    @Override
    public Node getFirstChild(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
        if (isParentNode(origin.getNodeType()))
        {
            Node candidate = origin.getFirstChild();
            while (null != candidate)
            {
                if (null != DomSupport.getNodeKind(candidate))
                {
                    return candidate;
                }
                else
                {
                    candidate = candidate.getNextSibling();
                }
            }
        }
        return null;
    }

    @Override
    public Node getFirstChildElement(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
        Node child = getFirstChild(origin);
        while (child != null)
        {
            if (Node.ELEMENT_NODE == child.getNodeType())
                return child;
            child = child.getNextSibling();
        }
        return null;
    }

    @Override
    public Node getFirstChildElementByName(Node origin, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(origin, "node");
        final short nodeType = origin.getNodeType();

        if (nodeType == Node.ELEMENT_NODE || nodeType == Node.DOCUMENT_NODE)
            return NamedSiblingIterator.findNextMatch(origin.getFirstChild(), namespaceURI, localName);
        return null;
    }

    @Override
    public Iterable<Node> getFollowingAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterableFollowingAxis<Node>(node, this);
    }

    @Override
    public Iterable<Node> getFollowingSiblingAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        if (node.getNextSibling() != null)
            return new IterableFollowingSiblingAxis<Node>(node, this);
        return Collections.emptyList();
    }

    @Override
    public Node getLastChild(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
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

    @Override
    public String getLocalName(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return DomSupport.getLocalNameAsString(node);
    }

    @Override
    public Iterable<Node> getNamespaceAxis(Node node, boolean inherit)
    {
        PreCondition.assertNotNull(node, "node");
        if (inherit)
            return getNamespacesInScope(node);
        return getNamespaces(node);
    }

    @Override
    public Iterable<NamespaceBinding> getNamespaceBindings(Node origin)
    {
        PreCondition.assertArgumentNotNull(origin, "origin");
        if (origin.hasAttributes())
        {
            final NamedNodeMap mixed = origin.getAttributes();
            if (null != mixed)
            {
                final int length = mixed.getLength();

                final int realLength = namespaceCount(mixed);

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
    
    @Override
    public String getNamespaceForPrefix(final Node node, final String prefix)
    {
        PreCondition.assertNotNull(node, "node");
        for (NamespaceBinding binding : getNamespaceBindings(node))
        {
            if (binding.getPrefix().equals(prefix))
                return binding.getNamespaceURI();
        }
        return null;
    }

    @Override
    public Iterable<String> getNamespaceNames(Node node, boolean orderCanonical)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (node.hasAttributes())
        {
            final NamedNodeMap mixed = node.getAttributes();
            if (null != mixed)
            {
                final int length = mixed.getLength();

                final int realLength = namespaceCount(mixed);

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

    @Override
    public String getNamespaceURI(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return getNamespaceURIAsString(node);
    }

    @Override
    public Node getNextSibling(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
        Node candidate = origin.getNextSibling();

        while (candidate != null)
        {
            if (DomSupport.getNodeKind(candidate) != null)
                return candidate;
            candidate = candidate.getNextSibling();
        }
        return null;
    }

    @Override
    public Node getNextSiblingElement(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return getNextSiblingElementByName(node, null, null);
    }

    @Override
    public Node getNextSiblingElementByName(Node node, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(node, "node");
        final Node current = getNextSibling(node);
        if (current != null)
        {
            if (matches(current, NodeKind.ELEMENT, namespaceURI, localName))
                return current;
            return getNextSiblingElementByName(current, namespaceURI, localName);
        }
        return null;
    }

    @Override
    public NodeKind getNodeKind(Node node)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        return DomSupport.getNodeKind(node);
    }

    @Override
    public Node getParent(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
        // Use DOM Support here because attribute nodes require special handling.
        return DomSupport.getParentNode(origin);
    }

    @Override
    public Iterable<Node> getPrecedingAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return new IterablePrecedingAxis<Node>(node, this);
    }

    @Override
    public Iterable<Node> getPrecedingSiblingAxis(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        if (null != node.getPreviousSibling())
            return new IterablePrecedingSiblingAxis<Node>(node, this);
        return Collections.emptyList();
    }

    @Override
    public String getPrefix(Node node)
    {
        PreCondition.assertNotNull(node, "node");
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

    @Override
    public Node getPreviousSibling(Node origin)
    {
        PreCondition.assertNotNull(origin, "node");
        return origin.getPreviousSibling();
    }

    @Override
    public Node getRoot(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        Node current = node;
        Node parentNode = DomSupport.getParentNode(current);
        while (null != parentNode)
        {
            current = parentNode;
            parentNode = current.getParentNode();
        }
        return current;
    }

    @Override
    public String getStringValue(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return DomSupport.getStringValue(node, DomSupport.DEFAULT_ATOM_SEPARATOR, DomSupport.DEFAULT_EMULATION);
    }

    @Override
    public boolean hasAttributes(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        if (node.hasAttributes())
        {
            final NamedNodeMap namespacesAndAttributes = node.getAttributes();
            final int length = namespacesAndAttributes.getLength();

            final int nsLength = namespaceCount(namespacesAndAttributes);
            
            if (length > nsLength)
                return true;
        }
        return false;
    }

    @Override
    public boolean hasChildren(Node node)
    {
        PreCondition.assertNotNull(node, "node");
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

    @Override
    public boolean hasNamespaces(Node origin)
    {
        PreCondition.assertArgumentNotNull(origin, "origin");
        if (origin.hasAttributes())
        {
            final NamedNodeMap mixed = origin.getAttributes();
            if (mixed != null)
                return namespaceCount(mixed) > 0;
        }
        return false;
    }

    @Override
    public boolean hasNextSibling(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return getNextSibling(node) != null;
    }

    @Override
    public boolean hasParent(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return getParent(node) != null;
    }

    @Override
    public boolean hasPreviousSibling(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return getPreviousSibling(node) != null;
    }

    @Override
    public boolean isAttribute(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        final short nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE)
        {
            final String namespaceURI = node.getNamespaceURI();
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI) ? false : true;
        }
        return false;
    }

    @Override
    public boolean isElement(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getNodeType() == Node.ELEMENT_NODE;
    }
    
    @Override
    public boolean isId(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        if (isAttribute(node))
        {
            Attr attr = (Attr)node;
            if (attr.isId())
                return true;
            if (XMLConstants.XML_NS_URI.equals(attr.getNamespaceURI()) &&
                    attr.getLocalName().equals("id") )
                return true;
        }
        else if (isElement(node))
        {
            for (Node att : getAttributeAxis(node, false) )
            {
                if (isId(att))
                    return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isIdRefs(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        if (isAttribute(node))
        {
            Attr attr = (Attr)node;
            TypeInfo ti = attr.getSchemaTypeInfo();
            return ti.getTypeName().startsWith("IDREF");
        }
        if (isElement(node))
        {
            for (Node att : getAttributeAxis(node, false) )
            {
                if (isIdRefs(att))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNamespace(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        final short nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE)
        {
            final String namespaceURI = node.getNamespaceURI();
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI) ? true : false;
        }
        return false;
    }

    @Override
    public DomNID getNodeId(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        return new DomNID(node);
    }

    @Override
    public boolean isText(Node node)
    {
        PreCondition.assertNotNull(node, "node");
        final short nodeType = node.getNodeType();
        return nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE;
    }

    @Override
    public boolean matches(Node node, NodeKind nodeKind, String namespaceURI, String localName)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (nodeKind != null)
        {
            if (DomSupport.getNodeKind(node) != nodeKind)
                return false;
        }
        return matches(node, namespaceURI, localName);
    }

    @Override
    public boolean matches(Node node, String namespaceURI, String localName)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (namespaceURI != null)
        {
            final String namespace = getNamespaceURI(node);
            // We'd like to compare using ==, but there is no guarantee that we have a symbol!
            if (!namespaceURI.equals(namespace))
                return false;
        }
        if (localName != null)
        {
            final String lName = getLocalName(node);
            if (!localName.equals(lName))
                return false;
        }
        return true;
    }

    @Override
    public void stream(Node node, ContentHandler handler)
        throws GenXDMException
    {
        PreCondition.assertNotNull(node, "node");
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
    
                                // The namespace "attributes" come before the real attributes.
                                for (int i = 0; i < length; i++)
                                {
                                    final Node namespace = mixed.item(i);
                                    if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespace.getNamespaceURI()))
                                    {
                                        stream(namespace, handler);
                                    }
                                }
    
                                // The real attributes.
                                for (int i = 0; i < length; i++)
                                {
                                    final Node attribute = mixed.item(i);
                                    if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI()))
                                    {
                                        stream(attribute, handler);
                                    }
                                }
                            }
                        }
                        Node child = node.getFirstChild();
                        while (null != child)
                        {
                            stream(child, handler);
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
                    final String prefix = getAttributePrefix(node);
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
                            stream(child, handler);
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
                    final String prefix = DomSupport.getLocalNameAsString(node);
                    final String uri = node.getNodeValue();
                    handler.namespace(prefix, uri);
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
            return (prefix != null) ? prefix : XMLConstants.DEFAULT_NS_PREFIX;
        }
    }
    
    @Override
    public Node getElementById(final Node context, final String id)
    {
        PreCondition.assertNotNull(context, "node");
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

    private Iterable<Node> getNamespaces(final Node node)
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

                    final int realLength = namespaceCount(mixed);

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

    private Iterable<Node> getNamespacesInScope(final Node node)
    {
        if (node instanceof Element)
        {
            final Element element = (Element)node;
            final LinkedList<Node> scopes = new LinkedList<Node>();
            Node currentNode = DomSupport.getParentNode(element);
            while (null != currentNode)
            {
                if (hasNamespaces(currentNode))
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

    // TODO: why suppress warnings? if it's unused, why not get rid of it?
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

    private boolean isParentNode(final short nodeType)
    {
        switch (nodeType)
        {
            case Node.DOCUMENT_NODE:
            case Node.ELEMENT_NODE:
            case Node.DOCUMENT_FRAGMENT_NODE:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    private String getNamespaceURIAsString(final Node node)
    {
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
            {
                String ns = node.getNamespaceURI();
                return (ns != null) ? ns : XMLConstants.NULL_NS_URI;
            }
            case Node.ATTRIBUTE_NODE:
            {
                final String namespaceURI = node.getNamespaceURI();

                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                {
                    return XMLConstants.NULL_NS_URI;
                }
                else
                {
                    return (namespaceURI != null) ? namespaceURI : XMLConstants.NULL_NS_URI;                }
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                return XMLConstants.NULL_NS_URI;
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

    private int namespaceCount(final NamedNodeMap mixed)
    {
        final int length = mixed.getLength();

        int realLength = 0;

        for (int i = 0; i < length; i++)
        {
            final Node node = mixed.item(i);

            final String namespaceURI = node.getNamespaceURI();

            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
            {
                realLength++;
            }
        }

        return realLength;
    }
}
