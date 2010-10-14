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
package org.gxml.bridge.dom.enhanced;

import java.net.URI;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.base.io.ContentHandler;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;
import org.gxml.bridge.dom.DomModel;
import org.gxml.bridge.dom.DomNID;
import org.gxml.bridge.dom.DomSupport;
import org.gxml.bridgekit.atoms.XmlAtom;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * {@link TypedModel} implementation for W3C Document Object Model.
 */
class DomSAModel implements TypedModel<Node, XmlAtom>
{
	public DomSAModel(final TypedContext<Node, XmlAtom> pcx)
	{
		this.pcx = PreCondition.assertArgumentNotNull(pcx, "pcx");
		this.baseModel = new org.gxml.bridge.dom.DomModel();
		this.m_metaBridge = PreCondition.assertArgumentNotNull(pcx.getMetaBridge(), "metaBridge");
		this.m_atomBridge = m_metaBridge.getAtomBridge();
		this.nameBridge = m_atomBridge.getNameBridge();
	}

	public int compare(final Node one, final Node two)
	{
	    return baseModel.compare(one, two);
	}

	public final Iterable<Node> getAncestorAxis(final Node node)
	{
	    return baseModel.getAncestorAxis(node);
	}

	public final Iterable<Node> getAncestorOrSelfAxis(final Node node)
	{
	    return baseModel.getAncestorOrSelfAxis(node);
	}

	public final Node getAttribute(final Node node, final String namespaceURI, final String localName)
	{
	    return baseModel.getAttribute(node, namespaceURI, localName);
	}

	public final Iterable<Node> getAttributeAxis(final Node origin, final boolean inherit)
	{
	    return baseModel.getAttributeAxis(origin, inherit);
	}

	public Iterable<QName> getAttributeNames(final Node node, final boolean orderCanonical)
	{
	    return baseModel.getAttributeNames(node, orderCanonical);
	}

	public String getAttributeStringValue(final Node parent, final String namespaceURI, final String localName)
	{
	    return baseModel.getAttributeStringValue(parent, namespaceURI, localName);
	}

	public List<XmlAtom> getAttributeValue(final Node parent, final String namespaceURI, final String localName)
	{
        return getValue(getAttribute(parent, namespaceURI, localName));
	}

	public QName getAttributeTypeName(final Node parent, final String namespaceURI, final String localName)
	{
		return getTypeName(getAttribute(parent, namespaceURI, localName));
	}
	
	public URI getBaseURI(final Node node)
	{
	    return baseModel.getBaseURI(node);
	}

	public final Iterable<Node> getChildAxis(final Node node)
	{
	    return baseModel.getChildAxis(node);
	}

	public final Iterable<Node> getChildElements(final Node origin)
	{
	    return baseModel.getChildElements(origin);
	}

	public final Iterable<Node> getChildElementsByName(final Node origin, final String namespaceURI, final String localName)
	{
	    return baseModel.getChildElementsByName(origin, namespaceURI, localName);
	}

	public final Iterable<Node> getDescendantAxis(final Node node)
	{
	    return baseModel.getDescendantAxis(node);
	}

	public final Iterable<Node> getDescendantOrSelfAxis(final Node node)
	{
	    return baseModel.getDescendantOrSelfAxis(node);
	}

	public final URI getDocumentURI(final Node node)
	{
	    return baseModel.getDocumentURI(node);
	}
	
	public final Node getElementById(final Node context, final String id)
	{
	    return baseModel.getElementById(context, id);
	}

	public final Node getFirstChild(final Node origin)
	{
	    return baseModel.getFirstChild(origin);
	}

	public final Node getFirstChildElement(final Node origin)
	{
	    return baseModel.getFirstChildElement(origin);
	}

	public final Node getFirstChildElementByName(final Node origin, final String namespaceURI, final String localName)
	{
	    return baseModel.getFirstChildElementByName(origin, namespaceURI, localName);
	}

	public final Iterable<Node> getFollowingAxis(final Node node)
	{
	    return baseModel.getFollowingAxis(node);
	}

	public final Iterable<Node> getFollowingSiblingAxis(final Node node)
	{
	    return baseModel.getFollowingSiblingAxis(node);
	}

	public final Node getLastChild(final Node origin)
	{
	    return baseModel.getLastChild(origin);
	}

	public final String getLocalName(final Node node)
	{
	    return baseModel.getLocalName(node);
	}

	public final Iterable<Node> getNamespaceAxis(final Node node, final boolean inherit)
	{
	    return baseModel.getNamespaceAxis(node, inherit);
	}

	public final Iterable<NamespaceBinding> getNamespaceBindings(final Node origin)
	{
	    return baseModel.getNamespaceBindings(origin);
	}
	
	public final String getNamespaceForPrefix(final Node node, final String prefix)
	{
	    return baseModel.getNamespaceForPrefix(node, prefix);
	}

	public Iterable<String> getNamespaceNames(final Node node, final boolean orderCanonical)
	{
	    return baseModel.getNamespaceNames(node, orderCanonical);
	}

	public final String getNamespaceURI(final Node node)
	{
	    return baseModel.getNamespaceURI(node);
	}

	public final Node getNextSibling(final Node origin)
	{
	    return baseModel.getNextSibling(origin);
	}

	public Node getNextSiblingElement(final Node node)
	{
	    return baseModel.getNextSiblingElement(node);
	}

	public Node getNextSiblingElementByName(final Node node, final String namespaceURI, final String localName)
	{
	    return baseModel.getNextSiblingElementByName(node, namespaceURI, localName);
	}

	public final NodeKind getNodeKind(final Node node)
	{
	    return baseModel.getNodeKind(node);
	}

	public final Node getParent(final Node node)
	{
	    return baseModel.getParent(node);
	}

	public final Iterable<Node> getPrecedingAxis(final Node node)
	{
	    return baseModel.getPrecedingAxis(node);
	}

	public final Iterable<Node> getPrecedingSiblingAxis(final Node node)
	{
	    return baseModel.getPrecedingSiblingAxis(node);
	}

	public final String getPrefix(final Node node)
	{
	    return baseModel.getPrefix(node);
	}

	public final Node getPreviousSibling(final Node origin)
	{
	    return baseModel.getPreviousSibling(origin);
	}

	public final Node getRoot(final Node node)
	{
	    return baseModel.getRoot(node);
	}

	public final String getStringValue(final Node node)
	{
	    return baseModel.getStringValue(node);
	}

	public final List<XmlAtom> getValue(final Node node)
	{
		if (null != node)
		{
			final AtomBridge<XmlAtom> atomBridge = m_metaBridge.getAtomBridge();
			switch (getNodeKind(node))
			{
				case TEXT:
				{
					final String stringValue = getStringValue(node);
					return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
				}
				case ELEMENT:
				case ATTRIBUTE:
				{
					final QName typeName = DomSupport.getAnnotationType(node, m_metaBridge);
					final SmType<XmlAtom> type = pcx.getTypeDefinition(typeName);
					if (type instanceof SmSimpleType<?>)
					{
						final SmSimpleType<XmlAtom> simpleType = (SmSimpleType<XmlAtom>)type;
						final String stringValue = getStringValue(node);
						try
						{
							// return type.validate(stringValue, new PrefixResolverOnNode<Node, A, String>(node, this,
							// m_nameBridge));
							return simpleType.validate(stringValue);
						}
						catch (final SmDatatypeException e)
						{
							throw new GxmlException(e);
						}
					}
					else
					{
						final String stringValue = getStringValue(node);
						return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
					}
				}
				case DOCUMENT:
				{
					return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(getStringValue(node)));
				}
				case NAMESPACE:
				case COMMENT:
				case PROCESSING_INSTRUCTION:
				{
					return atomBridge.wrapAtom(atomBridge.createString(getStringValue(node)));
				}
				default:
				{
					throw new AssertionError(node.getNodeType());
				}
			}
		}
		else
		{
			return null;
		}
	}

	public final QName getTypeName(final Node node)
	{
		if (null != node)
		{
			return DomSupport.getAnnotationType(node, m_metaBridge);
		}
		else
		{
			return null;
		}
	}

//	public List<XmlAtom> getValue(final Node node)
//	{
//		switch (getNodeKind(node))
//		{
//			case ATTRIBUTE:
//			case ELEMENT:
//			{
//				final AtomBridge<XmlAtom> atomBridge = m_metaBridge.getAtomBridge();
//				final QName typeName = DomSupport.getAnnotationType(node, m_metaBridge);
//				final SmType<XmlAtom> type = pcx.getTypeDefinition(typeName);
//				if (type instanceof SmSimpleType<?>)
//				{
//					final SmSimpleType<XmlAtom> simpleType = (SmSimpleType<XmlAtom>)type;
//					final String stringValue = getStringValue(node);
//					try
//					{
//						// return simpleType.validate(stringValue, new PrefixResolverOnNode<Node, A, String>(node, this,
//						// m_nameBridge));
//						return simpleType.validate(stringValue);
//					}
//					catch (final SmDatatypeException e)
//					{
//						throw new GxmlException(e);
//					}
//				}
//				else
//				{
//					final String stringValue = getStringValue(node);
//					return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
//				}
//			}
//			case DOCUMENT:
//			case TEXT:
//			{
//				final AtomBridge<XmlAtom> atomBridge = m_metaBridge.getAtomBridge();
//				final String stringValue = getStringValue(node);
//				return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
//			}
//			case NAMESPACE:
//			case COMMENT:
//			case PROCESSING_INSTRUCTION:
//			{
//				final AtomBridge<XmlAtom> atomBridge = m_metaBridge.getAtomBridge();
//				return atomBridge.wrapAtom(atomBridge.createString(getStringValue(node)));
//			}
//			default:
//			{
//				throw new AssertionError(getNodeKind(node));
//			}
//		}
//	}

	public boolean hasAttributes(final Node node)
	{
	    return baseModel.hasAttributes(node);
	}

	public boolean hasChildren(final Node node)
	{
	    return baseModel.hasChildren(node);
	}

	public boolean hasNamespaces(final Node origin)
	{
	    return baseModel.hasNamespaces(origin);
	}

	public boolean hasNextSibling(final Node node)
	{
	    return baseModel.hasNextSibling(node);
	}

	public boolean hasParent(final Node node)
	{
	    return baseModel.hasParent(node);
	}

	public boolean hasPreviousSibling(final Node node)
	{
	    return baseModel.hasPreviousSibling(node);
	}

	public boolean isAttribute(final Node node)
	{
	    return baseModel.isAttribute(node);
	}

	public boolean isElement(final Node node)
	{
	    return baseModel.isElement(node);
	}
	
	public boolean isId(final Node node)
	{
	    return baseModel.isId(node);
	}
	
	public boolean isIdRefs(final Node node)
	{
	    return baseModel.isIdRefs(node);
	}

	public boolean isNamespace(final Node node)
	{
	    return baseModel.isNamespace(node);
	}

	public final DomNID getNodeId(final Node node)
	{
	    return baseModel.getNodeId(node);
	}

	public boolean isText(final Node node)
	{
	    return baseModel.isText(node);
	}

	public boolean matches(final Node node, final NodeKind kindArg, final String namespaceArg, final String localNameArg)
	{
	    return baseModel.matches(node, kindArg, namespaceArg, localNameArg);
	}

	public boolean matches(final Node node, final String namespaceArg, final String localNameArg)
	{
	    return baseModel.matches(node, namespaceArg, localNameArg);
	}

//	public Node node(Object object)
//	{
//        if (object instanceof Node)
//        {
//            return (Node)object;
//        }
//        else
//        {
//            return null;
//        }
//	}

	public final void stream(final Node origin, final boolean copyNamespaces, final ContentHandler handler) throws GxmlException
	{
	    baseModel.stream(origin, copyNamespaces, handler);
	}

	public final void stream(final Node origin, boolean copyNamespaces, boolean copyTypeAnnotations, final SequenceHandler<XmlAtom> handler) throws GxmlException
	{
		switch (getNodeKind(origin))
		{
			case ELEMENT:
			{
				final QName type = copyTypeAnnotations ? getTypeName(origin) : null;

				handler.startElement(getNamespaceURI(origin), getLocalName(origin), baseModel.getElementPrefix(origin), type);
				try
				{
					if (origin.hasAttributes())
					{
						final NamedNodeMap mixed = origin.getAttributes();
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
										deepCopyNamespace(namespace, handler, nameBridge);
									}
								}
							}

							// The real attributes.
							for (int i = 0; i < length; i++)
							{
								final Node attribute = mixed.item(i);
								if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI()))
								{
									deepCopyAttribute(attribute, copyNamespaces, copyTypeAnnotations, handler);
								}
							}
						}
					}
					deepCopyChildren(origin, copyNamespaces, copyTypeAnnotations, handler);
				}
				finally
				{
					handler.endElement();
				}
			}
			break;
			case ATTRIBUTE:
			{
				deepCopyAttribute(origin, copyNamespaces, copyTypeAnnotations, handler);
			}
			break;
			case TEXT:
			{
				handler.text(origin.getNodeValue());
			}
			break;
			case DOCUMENT:
			{
				handler.startDocument(getDocumentURI(origin), null);
				try
				{
					deepCopyChildren(origin, copyNamespaces, copyTypeAnnotations, handler);
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
					deepCopyNamespace(origin, handler, nameBridge);
				}
			}
			break;
			case COMMENT:
			{
				handler.comment(origin.getNodeValue());
			}
			break;
			case PROCESSING_INSTRUCTION:
			{
				handler.processingInstruction(origin.getNodeName(), origin.getNodeValue());
			}
			break;
			default:
			{
				throw new AssertionError(getNodeKind(origin));
			}
		}
	}
	
//    private String correctBaseURI(final Node node, final TypedModel<Node, XmlAtom> model)
//    {
//        final Node attribute = model.getAttribute(node, XMLConstants.XML_NS_URI, "base");
//        if (null != attribute)
//        {
//            return attribute.getNodeValue();
//        }
//        final Node parent = model.getParent(node);
//        if (null != parent)
//        {
//            return correctBaseURI(parent, model);
//        }
//        else
//        {
//            return model.getDocumentURI(node).toString();
//        }
//    }

    private void deepCopyAttribute(final Node attribute, final boolean copyNamespaces, final boolean copyTypeAnnotations, final SequenceHandler<XmlAtom> handler) throws GxmlException
    {
        final String prefix = copyNamespaces ? baseModel.getAttributePrefix(attribute) : "";
        handler.attribute(getNamespaceURI(attribute), getLocalName(attribute), prefix, attribute.getNodeValue(), null);
    }

    /**
     * An optimized child axis traversal for that avoids intermediate {@link Iterable} creation.
     */
    private void deepCopyChildren(final Node origin, final boolean copyNamespaces, final boolean copyTypeAnnotations, final SequenceHandler<XmlAtom> handler) throws GxmlException
    {
        Node child = origin.getFirstChild();
        while (null != child)
        {
            stream(child, copyNamespaces, copyTypeAnnotations, handler);
            child = child.getNextSibling();
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

    private static void deepCopyNamespace(final Node namespace, final SequenceHandler<XmlAtom> handler, final NameSource nameBridge) throws GxmlException
    {
        final String prefix = DomSupport.getLocalNameAsString(namespace);
        final String uri = namespace.getNodeValue();
        handler.namespace(prefix, uri);
    }

    protected final AtomBridge<XmlAtom> m_atomBridge;

    protected final MetaBridge<XmlAtom> m_metaBridge;
    /**
     * The name bridge is important for ensuring that symbols obey the right semantics. Because the DOM may have been created elsewhere, we must be cautious and ensure that strings are converted to symbols.
     */
    protected final NameSource nameBridge;

    protected final TypedContext<Node, XmlAtom> pcx;
    
    private DomModel baseModel;

}
