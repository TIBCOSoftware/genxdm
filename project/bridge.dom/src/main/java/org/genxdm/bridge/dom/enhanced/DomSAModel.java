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
import org.genxdm.bridgekit.atoms.XsiNil;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.NativeType;
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

    @Override
    public List<XmlAtom> getAttributeValue(final Node parent, final String namespaceURI, final String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        return getValue(getAttribute(parent, namespaceURI, localName));
    }

    @Override
    public QName getAttributeTypeName(final Node parent, final String namespaceURI, final String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        return getTypeName(getAttribute(parent, namespaceURI, localName));
    }
    
    @Override
    public final List<XmlAtom> getValue(final Node node)
    {
        PreCondition.assertNotNull(node, "node");
        switch (getNodeKind(node))
        {
            case ELEMENT:
            {
                if (XsiNil.isNilledElement(this, node, atomBridge))
                    return (List<XmlAtom>)atomBridge.emptySequence();
                // otherwise, fall through to the next section, which is both
                // elements and attributes. clear?
            }
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
            case TEXT:
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

    @SuppressWarnings("incomplete-switch")
    @Override
    public final QName getTypeName(final Node node)
    {
        PreCondition.assertNotNull(node, "node");
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
        return null;
    }

    @Override
    public final void stream(final Node origin, final SequenceHandler<XmlAtom> handler, boolean bogus) throws GenXDMException
    {
        PreCondition.assertNotNull(origin, "node");
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
                                    stream(namespace, handler, false);
                                }
                            }

                            // The real attributes.
                            for (int i = 0; i < length; i++)
                            {
                                final Node attribute = mixed.item(i);
                                if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI()))
                                {
                                    stream(attribute, handler, false);
                                }
                            }
                        }
                    }
                    Node child = origin.getFirstChild();
                    while (null != child)
                    {
                        stream(child, handler, false);
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
                final String prefix = getAttributePrefix(origin);
                handler.attribute(getNamespaceURI(origin), getLocalName(origin), prefix, getValue(origin), getTypeName(origin));
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
                    Node child = origin.getFirstChild();
                    while (null != child)
                    {
                        stream(child, handler, false);
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
                final String prefix = DomSupport.getLocalNameAsString(origin);
                final String uri = origin.getNodeValue();
                handler.namespace(prefix, uri);
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

    private QName getAnnotationType(final Node node, final SchemaComponentCache metaBridge)
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


    private final AtomBridge<XmlAtom> atomBridge;
    private final SchemaComponentCache schema;
    private final ComponentProvider provider;

    private static final QName xsUntyped = NativeType.UNTYPED.toQName();
    private static final QName xsUntypedAtomic = NativeType.UNTYPED_ATOMIC.toQName();
}
