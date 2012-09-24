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
package org.genxdm.bridge.dom.enhanced;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.bridge.dom.DomConstants;
import org.genxdm.bridge.dom.DomModel;
import org.genxdm.bridge.dom.DomSupport;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.Schema;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * {@link TypedModel} implementation for W3C Document Object Model.
 */
class DomSAModel
    extends DomModel
    implements TypedModel<Node, XmlAtom>
{
    public DomSAModel(final TypedContext<Node, XmlAtom> pcx)
    {
        PreCondition.assertArgumentNotNull(pcx, "pcx");
        this.schema = pcx.getSchema();
        this.atomBridge = pcx.getAtomBridge();
        this.provider = schema.getComponentProvider();
    }

    public List<XmlAtom> getAttributeValue(final Node parent, final String namespaceURI, final String localName)
    {
        return getValue(getAttribute(parent, namespaceURI, localName));
    }

    public QName getAttributeTypeName(final Node parent, final String namespaceURI, final String localName)
    {
        return getTypeName(getAttribute(parent, namespaceURI, localName));
    }
    
    public final List<XmlAtom> getValue(final Node node)
    {
        if (null != node)
        {
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
                    final QName typeName = getTypeName(node);
                    final Type type = provider.getTypeDefinition(typeName);
                    if (type instanceof SimpleType)
                    {
                        final SimpleType simpleType = (SimpleType)type;
                        final String stringValue = getStringValue(node);
                        try
                        {
                            return simpleType.validate(stringValue, atomBridge);
                        }
                        catch (final DatatypeException e)
                        {
                            throw new GenXDMException(e);
                        }
                    }
                    final String stringValue = getStringValue(node);
                    return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
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
        if (node != null)
        {
            final QName annotation = getAnnotationType(node, schema); 
            switch (getNodeKind(node))
            {
                case ATTRIBUTE:
                    if (annotation == null)
                        return xsUntypedAtomic;
                    return annotation;
                case ELEMENT:
                    if (annotation == null)
                        return xsUntyped;
                    return annotation;
                case TEXT:
                    return xsUntypedAtomic;
                // for document, namespace, comment, and pi, fall through
//                default : 
//                    return null;
            }
        }
        return null;
    }

    public final void stream(final Node origin, final SequenceHandler<XmlAtom> handler) throws GenXDMException
    {
        switch (getNodeKind(origin))
        {
            case ELEMENT:
            {
                final QName type = getTypeName(origin);

                handler.startElement(getNamespaceURI(origin), getLocalName(origin), getElementPrefix(origin), type);
                try
                {
                    if (origin.hasAttributes())
                    {
                        final NamedNodeMap mixed = origin.getAttributes();
                        if (null != mixed)
                        {
                            final int length = mixed.getLength();

                            // The namespace "attributes" come before the real attributes.
                            for (int i = 0; i < length; i++)
                            {
                                final Node namespace = mixed.item(i);
                                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespace.getNamespaceURI()))
                                {
                                    deepCopyNamespace(namespace, handler);
                                }
                            }

                            // The real attributes.
                            for (int i = 0; i < length; i++)
                            {
                                final Node attribute = mixed.item(i);
                                if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI()))
                                {
                                    deepCopyAttribute(attribute, true, handler);
                                }
                            }
                        }
                    }
                    deepCopyChildren(origin, true, handler);
                }
                finally
                {
                    handler.endElement();
                }
            }
            break;
            case ATTRIBUTE:
            {
                deepCopyAttribute(origin, true, handler);
            }
            break;
            case TEXT:
            {
                boolean typed = false;
                if (hasParent(origin))
                {
                    final QName typeName = getTypeName(getParent(origin));
                    final Type type = provider.getTypeDefinition(typeName);
                    if (type instanceof SimpleType)
                    {
                        handler.text(getValue(getParent(origin)));
                        typed = true;
                    }
                }
                if (!typed)
                    handler.text(origin.getNodeValue());
            }
            break;
            case DOCUMENT:
            {
                handler.startDocument(getDocumentURI(origin), null);
                try
                {
                    deepCopyChildren(origin, true, handler);
                }
                finally
                {
                    handler.endDocument();
                }
            }
            break;
            case NAMESPACE:
            {
                deepCopyNamespace(origin, handler);
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

    private QName getAnnotationType(final Node node, final Schema metaBridge)
    {
        PreCondition.assertArgumentNotNull(node, "node");
        if (DomSupport.supportsCoreLevel3(node))
        {
            try
            {
                return (QName)node.getUserData(DomConstants.UD_ANNOTATION_TYPE);
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

    private void deepCopyAttribute(final Node attribute, final boolean copyTypeAnnotations, final SequenceHandler<XmlAtom> handler) throws GenXDMException
    {
        final String prefix = getAttributePrefix(attribute);
        handler.attribute(getNamespaceURI(attribute), getLocalName(attribute), prefix, attribute.getNodeValue(), null);
    }

    /**
     * An optimized child axis traversal for that avoids intermediate {@link Iterable} creation.
     */
    private void deepCopyChildren(final Node origin, final boolean copyTypeAnnotations, final SequenceHandler<XmlAtom> handler) throws GenXDMException
    {
        Node child = origin.getFirstChild();
        while (null != child)
        {
            stream(child, handler);
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

    private void deepCopyNamespace(final Node namespace, final SequenceHandler<XmlAtom> handler) throws GenXDMException
    {
        final String prefix = DomSupport.getLocalNameAsString(namespace);
        final String uri = namespace.getNodeValue();
        handler.namespace(prefix, uri);
    }

    private final AtomBridge<XmlAtom> atomBridge;

    private final Schema schema;
    
    private final ComponentProvider provider;

    private static final QName xsUntyped = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untyped");
    private static final QName xsUntypedAtomic = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untypedAtomic");
}
