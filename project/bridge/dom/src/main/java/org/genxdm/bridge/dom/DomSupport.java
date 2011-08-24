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

import java.util.List;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.Emulation;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Conversion functions for integration DOM with other technologies.
 */
public final class DomSupport implements DomConstants
{
    // private static final String UD_DYNAMIC_TYPE = "{http://org.genxdm.bridge.dom}dynamic-type";

    public static Node appendText(final Node parent, final String strval)
    {
        final Node lastChild = parent.getLastChild();
        if (null != lastChild && (lastChild.getNodeType() == Node.TEXT_NODE || lastChild.getNodeType() == Node.CDATA_SECTION_NODE))
        {
            final String existing = lastChild.getNodeValue();
            lastChild.setNodeValue(existing.concat(strval));
            return lastChild;
        }
        else
        {
            final Node text = getOwner(parent).createTextNode(strval);
            try
            {
                parent.appendChild(text);
            }
            catch (final DOMException e)
            {
                throw new UnsupportedOperationException(e);
            }
            return text;
        }
    }

    private static String correctNamespaceURI(final String namespaceURI)
    {
        if (null != namespaceURI)
        {
            return namespaceURI;
        }
        else
        {
            return XMLConstants.NULL_NS_URI;
        }
    }

    public static <A> Attr createAttribute(final Document owner, final String attributeNS, final String attributeLN, final String attributePH, final List<? extends A> data, final Emulation emulation, final AtomBridge<A> atomBridge)
    {
        return createAttributeUntyped(owner, attributeNS, attributeLN, attributePH, emulation.atomsToString(data, atomBridge));
    }

    public static Attr createAttributeUntyped(final Document owner, final String attributeNS, final String attributeLN, final String attributePH, final String data)
    {
        final Attr attribute;
        if (attributeNS.length() > 0)
        {
            final String attributeQN = getQualifiedName(attributeLN, attributePH);
            attribute = owner.createAttributeNS(attributeNS, attributeQN);
        }
        else
        {
            attribute = owner.createAttributeNS(null, attributeLN);
        }
        attribute.setValue(data);
        return attribute;
    }

    /**
     * This is the canonical implementation for element creation.
     */
    public static Element createElement(final Document owner, final String elementNS, final String elementLN, final String elementPH)
    {
        if (elementNS.length() > 0)
        {
            final String elementQN = getQualifiedName(elementLN, elementPH);
            return owner.createElementNS(elementNS, elementQN);
        }
        else
        {
            return owner.createElementNS(null, elementLN);
        }
    }

    public static Attr createNamespace(final Document owner, final String prefix, final String uri)
    {
        final String qualifiedName;
        if (prefix.length() > 0)
        {
            qualifiedName = "xmlns:".concat(prefix);
        }
        else
        {
            qualifiedName = "xmlns";
        }

        final Attr namespace = owner.createAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, qualifiedName);

        // Of the choices setNodeValue, setTextContent and setValue, call the most specific to Attr.
        namespace.setValue(uri);

        return namespace;
    }

    /**
     * XPath-correct implementation for child axis navigation.
     */
    public static Node getFirstChild(final Node origin)
    {
        if (null != origin)
        {
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
                return null;
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

    public static String getLocalNameAsString(final Node node)
    {
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
            {
                return PreCondition.assertArgumentNotNull(getLocalNameRegardlessOfNamespaceAwareness(node));
            }
            case Node.ATTRIBUTE_NODE:
            {
                final String namespaceURI = node.getNamespaceURI();
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                {
                    final String localName = PreCondition.assertArgumentNotNull(node.getLocalName());
                    if (XMLConstants.XMLNS_ATTRIBUTE.equals(localName))
                    {
                        return "";
                    }
                    else
                    {
                        return localName;
                    }
                }
                else
                {
                    return getLocalNameRegardlessOfNamespaceAwareness(node);
                }
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                return node.getNodeName();
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
                throw new AssertionError(Short.toString(node.getNodeType()));
            }
        }
    }

    /**
     * An amusing method that demonstrates the inventiveness of the DOM designers.
     * <p>
     * Prevents untold pain when nobody told you that they created namespace-unware nodes.
     * </p>
     * 
     * @param node
     *            The node for which you want to get the local-name.
     */
    private static String getLocalNameRegardlessOfNamespaceAwareness(final Node node)
    {
        final String localName = node.getLocalName();
        if (null != localName)
        {
            return localName;
        }
        else
        {
            return node.getNodeName();
        }
    }

    public static String getNamespaceURIAsString(final Node node)
    {
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
            {
                return correctNamespaceURI(node.getNamespaceURI());
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
                    return correctNamespaceURI(namespaceURI);
                }
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

    /**
     * Helper function to convert a w3c DOM node type to a Data Model node-kind. Unrecognized node types return null. The DOM {@link Node#ATTRIBUTE_NODE} maps to {@link org.genxdm.NodeKind#ATTRIBUTE} regardless of whether the attribute actually represents a
     * namespace node.
     * 
     * @param nodeType
     *            The w3c DOM node type.
     */
    public static NodeKind getNodeKind(final Node node)
    {
        // Note that this switch statement has been ordered
        // to give the compiler a chance to establish a fast
        // table switch.
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE: // 1
            {
                return NodeKind.ELEMENT;
            }
            case Node.ATTRIBUTE_NODE: // 2
            {
                final String namespaceURI = node.getNamespaceURI();

                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
                {
                    return NodeKind.NAMESPACE;
                }
                else
                {
                    return NodeKind.ATTRIBUTE;
                }
            }
            case Node.TEXT_NODE: // 3
            case Node.CDATA_SECTION_NODE: // 4
            {
                return NodeKind.TEXT;
            }
            case Node.ENTITY_REFERENCE_NODE: // 5
            case Node.ENTITY_NODE: // 6
            {
                return null;
            }
            case Node.PROCESSING_INSTRUCTION_NODE: // 7
            {
                return NodeKind.PROCESSING_INSTRUCTION;
            }
            case Node.COMMENT_NODE: // 8
            {
                return NodeKind.COMMENT;
            }
            case Node.DOCUMENT_NODE: // 9
            {
                return NodeKind.DOCUMENT;
            }
            case Node.DOCUMENT_TYPE_NODE: // 10
            {
                return null;
            }
            case Node.DOCUMENT_FRAGMENT_NODE: // 11
            {
                return NodeKind.DOCUMENT;
            }
            case Node.NOTATION_NODE: // 12
            {
                return null;
            }
            default:
            {
                // What did we miss?
                throw new AssertionError(node.getNodeType());
            }
        }
    }

    /**
     * Returns the owner document. Unlike the DOM API, this works for all node kinds including the document node.
     * 
     * @param node
     *            The node for which the owner is required.
     */
    public static Document getOwner(final Node node)
    {
        if (node.getNodeType() == Node.DOCUMENT_NODE)
            return (Document)node;
        return node.getOwnerDocument();
    }

    /**
     * XPath-correct implementation of parent axis navigation.
     */
    public static Node getParentNode(final Node origin)
    {
        if (origin != null)
        {
            // Faster to call getNodeType() than to do instanceof; that's what it's for!
            if (origin.getNodeType() == Node.ATTRIBUTE_NODE)
            {
                // We don't expect the cast to fail.
                final Attr attribute = (Attr)origin;

                return attribute.getOwnerElement();
            }
            return origin.getParentNode();
        }
        return null;
    }

    /**
     * Computes the lexical qualified name from a local-name and a prefix.
     */
    public static String getQualifiedName(final String localName, final String prefix)
    {
        // Try to make this as efficient as possible because StAX does not retain the
        // qualified name, and DOM needs it. This could make StAX -> DOM slower than
        // SAX -> DOM.
        int prefixLength;
        if (prefix != null && (prefixLength = prefix.length()) > 0)
        {
            final int capacity = prefixLength + 1 + localName.length();
            final StringBuilder sb = new StringBuilder(capacity);
            sb.append(prefix);
            sb.append(":");
            sb.append(localName);

            return sb.toString();
        }
        else
        {
            return localName;
        }
    }

    public static String getStringValue(final Node node, final String separator, final Emulation emulation)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        PreCondition.assertArgumentNotNull(separator, "separator");
        PreCondition.assertArgumentNotNull(emulation, "emulation");
        switch (node.getNodeType())
        {
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
            {
                return PreCondition.assertArgumentNotNull(node.getNodeValue(), "getNodeValue");
            }
            case Node.ELEMENT_NODE:
            {
                // return node.getTextContent();
                return getStringValueOfBranchNode(node, separator, emulation);
            }
            case Node.ATTRIBUTE_NODE:
            case Node.COMMENT_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                return node.getNodeValue();
            }
            case Node.DOCUMENT_NODE:
            case Node.DOCUMENT_FRAGMENT_NODE:
            {
                // final Document d = (Document) node;

                return getStringValueOfBranchNode(node, separator, emulation);
            }
            default:
            {
                throw new UnsupportedOperationException(Short.toString(node.getNodeType()));
            }
        }
    }

    // we don't use Node.getNodeValue() because that's a stupid and worthless
    // method; it returns null for both document and element, rather than the
    // concatenation of the values of all descendants (which is what this method
    // does)
    private static String getStringValueOfBranchNode(final Node node, final String separator, final Emulation emulation)
    {
        final Node firstChild = node.getFirstChild();
        if (null == firstChild)
        {
            return "";
        }
        else
        {
            // This method is optimized for the simple content case.
            String single = null;
            StringBuilder multiple = null;

            Node child = PreCondition.assertArgumentNotNull(firstChild);
            while (child != null)
            {
                switch (child.getNodeType())
                {
                    case Node.CDATA_SECTION_NODE:
                    case Node.TEXT_NODE:
                    {
                        final String strval = getStringValue(child, separator, emulation);
                        if (null == single)
                        {
                            single = strval;
                        }
                        else
                        {
                            if (null == multiple)
                            {
                                multiple = new StringBuilder(single);
                            }
                            multiple.append(strval);
                        }
                    }
                    break;
                    case Node.ELEMENT_NODE:
                    {
                        final String strval = getStringValueOfBranchNode(child, separator, emulation);
                        if (null == single)
                        {
                            single = strval;
                        }
                        else
                        {
                            if (null == multiple)
                            {
                                multiple = new StringBuilder(single);
                            }
                            multiple.append(strval);
                        }
                    }
                    break;
                    default:
                    {
                        // Ignore
                    }
                }
                child = child.getNextSibling();
            }

            if (null != multiple)
            {
                return multiple.toString();
            }
            else if (null != single)
            {
                return single;
            }
            else
            {
                return "";
            }
        }
    }

    public static boolean hasNamespaces(final Node origin)
    {
        PreCondition.assertArgumentNotNull(origin, "origin");
        if (origin.hasAttributes())
        {
            final NamedNodeMap mixed = origin.getAttributes();
            if (null != mixed)
            {
                return namespaceCount(mixed) > 0;
            }
        }
        return false;
    }

    public static boolean isAttribute(final Node node)
    {
        final short nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE)
        {
            final String namespaceURI = node.getNamespaceURI();
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI) ? false : true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isNamespace(final Node node)
    {
        final short nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE)
        {
            final String namespaceURI = node.getNamespaceURI();
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI) ? true : false;
        }
        else
        {
            return false;
        }
    }

    private static boolean isParentNode(final short nodeType)
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

    public static int namespaceCount(final NamedNodeMap mixed)
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

    public static <A> Attr setAttribute(final Node parent, final String namespaceURI, final String localName, final String prefixHint, final List<? extends A> data, final Emulation emulation, final AtomBridge<A> atomBridge)
    {
        return setAttributeUntyped(parent, namespaceURI, localName, prefixHint, emulation.atomsToString(data, atomBridge));
    }

    public static Attr setAttributeUntyped(final Node parent, final String namespaceURI, final String localName, final String prefixHint, final String data)
    {
        final Element e = (Element)parent;
        final Attr existing = e.getAttributeNodeNS(namespaceURI, localName);
        if (null != existing)
        {
            existing.setValue(data);
            return existing;
        }
        else
        {
            final Attr attribute = createAttributeUntyped(getOwner(parent), namespaceURI, localName, prefixHint, data);
            e.setAttributeNodeNS(attribute);
            return attribute;
        }
    }

    public static Attr setNamespace(final Node parent, final String prefix, final String uri)
    {
        final Element e = (Element)parent;
        final String localName;
        if (prefix.length() > 0)
        {
            localName = prefix;
        }
        else
        {
            localName = "xmlns";
        }

        final Attr existing = e.getAttributeNodeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName);
        if (null != existing)
        {
            existing.setValue(uri);
            return existing;
        }
        else
        {
            final Attr namespace = createNamespace(getOwner(parent), prefix, uri);
            e.setAttributeNodeNS(namespace);
            return namespace;
        }
    }

    public static boolean supportsCoreLevel3(Node node)
    {
        return node.isSupported(CORE, LEVEL_3);
    }
}
