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
package org.gxml.bridge.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gxml.NodeKind;
import org.gxml.exceptions.GxmlException;
import org.gxml.exceptions.PreCondition;
import org.gxml.typed.TypedContext;
import org.gxml.typed.types.AtomBridge;
import org.gxml.typed.types.Emulation;
import org.gxml.typed.types.MetaBridge;
import org.gxml.xs.exceptions.SmDatatypeException;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmType;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Conversion functions for integration DOM with other technologies.
 */
public final class DomSupport implements DomConstants
{
    public static final String DEFAULT_ATOM_SEPARATOR = " ";
    public static final Emulation DEFAULT_EMULATION = Emulation.MODERN;
	private static final String UD_ANNOTATION_TYPE = "{http://com.tibco.gxmlsa.xdm.dom}annotation-type";
	// private static final String UD_DYNAMIC_TYPE = "{http://com.tibco.gxmlsa.xdm.dom}dynamic-type";

	/**
	 * The standard "prefix" for xmlns attributes followed by a colon.
	 */
	private static final String XMLNS_COLON = XMLConstants.XMLNS_ATTRIBUTE + ":";

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

	public static <A> Attr createAttribute(final Document owner, final String attributeNS, final String attributeLN, final String attributePH, final A data, final Emulation emulation, final AtomBridge<A> atomBridge)
	{
		return createAttributeUntyped(owner, attributeNS, attributeLN, attributePH, emulation.atomToString(data, atomBridge));
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

	public static Node createComment(final Document owner, final String data)
	{
		return owner.createComment(data);
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

	public static Node createProcessingInstruction(final Document owner, final String target, final String data)
	{
		return owner.createProcessingInstruction(target, data);
	}

	public static <A> Node createText(final Document owner, final A data, final Emulation emulation, final AtomBridge<A> atomBridge)
	{
		final String strval = emulation.atomToString(data, atomBridge);
		return owner.createTextNode(strval);
	}

	public static <A> Node createText(final Document owner, final List<String> data, final Emulation emulation, final AtomBridge<A> atomBridge)
	{
		return owner.createTextNode(emulation.listToString(data));
	}

	public static Node createText(final Document owner, final String strval)
	{
		return owner.createTextNode(strval);
	}

	public static String domConventionNS(final String namespaceURI)
	{
		PreCondition.assertArgumentNotNull(namespaceURI, "namespaceURI");
		if (namespaceURI.length() == 0)
		{
			return null;
		}
		else
		{
			return namespaceURI;
		}
	}

	/**
	 * Converts a standard (SAX) namespace-uri to the DOM convention for searching.
	 */
	public static String domSearchName(final String name)
	{
		if (null != name)
		{
			if (name.length() > 0)
			{
				return name;
			}
			else
			{
				// empty string SAX is null DOM.
				return null;
			}
		}
		else
		{
			// null SAX is wildcard DOM.
			return "*";
		}
	}

	public static QName getAnnotationType(final Node node, final MetaBridge<?> metaBridge)
	{
		PreCondition.assertArgumentNotNull(node, "node");
		if (supportsCoreLevel3(node))
		{
			try
			{
				return (QName)node.getUserData(UD_ANNOTATION_TYPE);
			}
			catch (final AbstractMethodError e)
			{
				// LOG.warn("getAnnotationType", e);
				return null;
			}
		}
		// TODO: Log something for DOM w/o Level 3 support?
		// LOG.warn("DOM does not support DOM CORE version 3.0: Node.getUserData");
		return null;
	}

	public static Iterable<Node> getChildElementsByName(final Node origin, final String namespaceURI, final String localName)
	{
		if (origin.getNodeType() == Node.ELEMENT_NODE)
		{
			final Element element = (Element)origin;

			final NodeList elements = element.getElementsByTagNameNS(domSearchName(namespaceURI), domSearchName(localName));

			final int length = elements.getLength();

			final ArrayList<Node> axis = new ArrayList<Node>(length);

			for (int i = 0; i < length; i++)
			{
				final Node node = elements.item(i);

				axis.add(node);
			}

			return axis;
		}
		else if (origin.getNodeType() == Node.DOCUMENT_NODE)
		{
			final Document document = (Document)origin;

			final NodeList elements = document.getElementsByTagNameNS(domSearchName(namespaceURI), domSearchName(localName));

			final int length = elements.getLength();

			final ArrayList<Node> axis = new ArrayList<Node>(length);

			for (int i = 0; i < length; i++)
			{
				final Node node = elements.item(i);

				axis.add(node);
			}

			return axis;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public static URI getDocumentURI(final Node node)
	{
		final Document owner = DomSupport.getOwner(node);
		if ( (node == owner) || node.isSameNode(owner) )
		{
    		final String documentURI;
    		try
    		{
    			if (supportsCoreLevel3(owner))
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
    		if (null != documentURI)
    		{
    			try
    			{
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

	public static final Node getFirstChildElement(final Node origin)
	{
		if (null != origin)
		{
			Node child = DomSupport.getFirstChild(origin);
			while (null != child)
			{
				if (Node.ELEMENT_NODE == child.getNodeType())
				{
					return child;
				}
				else
				{
					child = child.getNextSibling();
				}
			}
			return null;
		}
		else
		{
			return null;
		}
	}

	public static Node getLastChild(Node origin)
	{
		if (isParentNode(origin.getNodeType()))
		{
			Node candidate = origin.getLastChild();
			while (null != candidate)
			{
				if (null != DomSupport.getNodeKind(candidate))
				{
					return candidate;
				}
				else
				{
					candidate = candidate.getPreviousSibling();
				}
			}
		}

		return null;
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

	public static Node getNextSibling(final Node origin)
	{
		Node candidate = origin.getNextSibling();

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

	/**
	 * Helper function to convert a w3c DOM node type to a Data Model node-kind. Unrecognized node types return null. The DOM {@link Node#ATTRIBUTE_NODE} maps to {@link org.gxml.NodeKind#ATTRIBUTE} regardless of whether the attribute actually represents a
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
		{
			return (Document)node;
		}
		else
		{
			return node.getOwnerDocument();
		}
	}

	/**
	 * XPath-correct implementation of parent axis navigation.
	 */
	public static Node getParentNode(final Node origin)
	{
		if (null != origin)
		{
			// Faster to call getNodeType() than to do instanceof; that's what it's for!
			if (origin.getNodeType() == Node.ATTRIBUTE_NODE)
			{
				// We don't expect the cast to fail.
				final Attr attribute = (Attr)origin;

				return attribute.getOwnerElement();
			}
			else
			{
				return origin.getParentNode();
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Computes the lexical qualified name from an {@link javax.xml.namespace.QName}.
	 * 
	 * @param name
	 *            The expanded-QName.
	 */
	public static String getQualifiedName(final QName name)
	{
		return getQualifiedName(name.getLocalPart(), name.getPrefix());
	}

	/**
	 * Computes the lexical qualified name from a local-name and a prefix.
	 */
	public static String getQualifiedName(final String localName, final String prefix)
	{
		// Try to make this as efficient as possible because StAX does not retain the
		// qualified name, and DOM needs it. This could make StAX -> DOM slower than
		// SAX -> DOM.
		final int prefixLength = prefix.length();
		if (prefixLength > 0)
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

	/**
	 * XPath-correct implementation for child axis navigation.
	 */
	public static Node getTailChild(final Node origin)
	{
		if (null != origin)
		{
			switch (origin.getNodeType())
			{
				case Node.ATTRIBUTE_NODE:
				{
					return null;
				}
				default:
				{
					return origin.getLastChild();
				}
			}
		}
		else
		{
			return null;
		}
	}

	public static <A> List<? extends A> getTypedValue(final Node node, final String separator, final Emulation emulation, final TypedContext<Node, A> pcx)
	{
		final MetaBridge<A> metaBridge = pcx.getMetaBridge();
		final AtomBridge<A> atomBridge = metaBridge.getAtomBridge();
		switch (getNodeKind(node))
		{
			case ELEMENT:
			{
				final QName typeName = getAnnotationType(node, metaBridge);
				final SmType<A> type = pcx.getTypeDefinition(typeName);
				if (type instanceof SmSimpleType<?>)
				{
					final SmSimpleType<A> simpleType = (SmSimpleType<A>)type;
					final String stringValue = getStringValue(node, separator, emulation);
					try
					{
						return simpleType.validate(stringValue);
					}
					catch (final SmDatatypeException e)
					{
						throw new GxmlException(e);
					}
				}
				else
				{
					final String stringValue = getStringValue(node, separator, emulation);
					return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
				}
			}
			case TEXT:
			{
				final String stringValue = getStringValue(node, separator, emulation);
				return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
			}
			case ATTRIBUTE:
			{
				final String strval = getStringValue(node, separator, emulation);
				return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(strval));
			}
			case NAMESPACE:
			{
				return atomBridge.wrapAtom(atomBridge.createString(getStringValue(node, separator, emulation)));
			}
			case DOCUMENT:
			{
				return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(getStringValue(node, separator, emulation)));
			}
			case PROCESSING_INSTRUCTION:
			case COMMENT:
			{
				return atomBridge.wrapAtom(atomBridge.createString(getStringValue(node, separator, emulation)));
			}
			default:
			{
				throw new AssertionError(node.getNodeType());
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

	public static boolean isElement(final Node node)
	{
		return node.getNodeType() == Node.ELEMENT_NODE;
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

	/**
	 * Determines if the attribute is in the "http://www.w3.org/2000/xmlns/" namespace. This is determined by seeing if the attribute qualified name string identically matches "xmlns" or whether it starts with "xmlns" and is followed by a colon. A null attribute
	 * qualified name string is defined to be an illegal argument.
	 * 
	 * @param qname
	 *            The qualified name string of the attribute.
	 * @return <CODE>true</CODE> if the attribute is in this namespace, otherwise <CODE>false</CODE>.
	 * @throws IllegalArgumentException
	 *             If the qname is null.
	 */
	public static boolean isNamespaceAttribute(final String qname) throws IllegalArgumentException
	{
		PreCondition.assertArgumentNotNull(qname, "qname");
		if (XMLConstants.XMLNS_ATTRIBUTE.equals(qname))
		{
			return true;
		}
		else
		{
			return (qname.startsWith(XMLNS_COLON));
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

	public static boolean isText(final Node node)
	{
		final short nodeType = node.getNodeType();
		return nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE;
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

	public static <A> Document newDocumentInternal(final DomNodeFactory fac, final Emulation emulation, final boolean useEnhancedDOM)
	{
		PreCondition.assertFalse(useEnhancedDOM, "useEnhancedDOM");
		final DocumentBuilderFactory dbf = fac.getCachedDocumentBuilderFactory();
		final DocumentBuilder db;
		try
		{
			db = dbf.newDocumentBuilder();
		}
		catch (final ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
		return db.newDocument();
	}

	public static void removeAttribute(final Node parent, final String namespaceURI, final String localName)
	{
		final Element e = (Element)parent;
		e.removeAttributeNS(domConventionNS(namespaceURI), localName);
	}

	public static void removeNamespace(final Node parent, final String prefix)
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
		e.removeAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName);
	}

	/**
	 * Converts a standard (DOM) namespace-uri to the SAX convention.
	 */
	public static String saxName(final String namespaceURI)
	{
		if (null != namespaceURI)
		{
			if (namespaceURI.length() > 0)
			{
				if (namespaceURI.equals("*"))
				{
					// "*" DOM is null SAX.
					return null;
				}
				else
				{
					return namespaceURI;
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			// null DOM is empty string SAX.
			return XMLConstants.NULL_NS_URI;
		}
	}

	public static <T> void setAnnotationType(final Node node, final T type)
	{
	    // TODO: we could, potentially, store DTD types even in untyped API
	    // to do so, though, we have to figure out how to define a QName for the DtdAttributeKind enumeration.
		if (supportsCoreLevel3(node))
		{
			try
			{
				node.setUserData(UD_ANNOTATION_TYPE, type, null);
			}
			catch (final AbstractMethodError e)
			{
				// LOG.warn("setAnnotationType", e);
			}
		}
		// TODO: Log something for DOM w/o Level 3 support?
		// LOG.warn("DOM does not support DOM CORE version 3.0: setUserData");
	}

	public static <A> Attr setAttribute(final Node parent, final String namespaceURI, final String localName, final String prefixHint, final A data, final Emulation emulation, final AtomBridge<A> atomBridge)
	{
		return setAttributeUntyped(parent, namespaceURI, localName, prefixHint, emulation.atomToString(data, atomBridge));
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

	public static <A> void setTypedValue(final Node node, final List<? extends A> value, final Emulation emulation, final MetaBridge<A> metaBridge)
	{
		if (null != value)
		{
			if (supportsCoreLevel3(node))
			{
				switch (node.getNodeType())
				{
					case Node.ATTRIBUTE_NODE:
					case Node.DOCUMENT_NODE:
					case Node.ELEMENT_NODE:
					{
						final AtomBridge<A> atomBridge = metaBridge.getAtomBridge();
						node.setTextContent(emulation.atomsToString(value, atomBridge));
					}
					break;
					case Node.CDATA_SECTION_NODE:
					case Node.COMMENT_NODE:
					case Node.PROCESSING_INSTRUCTION_NODE:
					case Node.TEXT_NODE:
					{
						final AtomBridge<A> atomBridge = metaBridge.getAtomBridge();
						node.setTextContent(emulation.atomsToString(value, atomBridge));
					}
					break;
					default:
					{
						throw new UnsupportedOperationException(Short.toString(node.getNodeType()));
					}
				}
			}
			else
			{
				// TODO: Log something to indicate lack of DOM Level 3 support.
				// LOG.warn("DOM does not support DOM CORE, version 3.0: Node.setTextContent()");
			}
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Deterimines whether the first namespace is a subset of the second. <br/>
	 * The convention in use for this API is SAX (nulls are not allowed, exept for two to indicate wildcard).
	 */
	public static boolean subsetName(final String one, final String two)
	{
		PreCondition.assertArgumentNotNull(one, "one");
		if (one != null)
		{
			if (two != null)
			{
				return one.equals(two);
			}
			else
			{
				// null is the wildcard.
				return true;
			}
		}
		else
		{
			throw new AssertionError();
		}
	}

	public static boolean supportsCoreLevel3(Node node)
	{
		return node.isSupported(CORE, LEVEL_3);
	}
}
