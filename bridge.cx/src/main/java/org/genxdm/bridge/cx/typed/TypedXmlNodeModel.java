/*
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
package org.genxdm.bridge.cx.typed;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridge.cx.base.XmlNodeModel;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridge.cx.tree.XmlRootNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

public class TypedXmlNodeModel
    extends XmlNodeModel
    implements TypedModel<XmlNode, XmlAtom>
{
    TypedXmlNodeModel(final AtomBridge<XmlAtom> bridge)
    {
        this.bridge = PreCondition.assertNotNull(bridge, "atomBridge");
    }

    @Override
    public QName getAttributeTypeName(XmlNode parent, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        return parent.getAttributeTypeName(namespaceURI, localName);
    }

    @Override
    public Iterable<? extends XmlAtom> getAttributeValue(XmlNode parent, String namespaceURI, String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        return parent.getAttributeValue(namespaceURI, localName);
    }

    @Override
    public QName getTypeName(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        return node.getTypeName();
    }

    @Override
    public Iterable<? extends XmlAtom> getValue(XmlNode node)
    {
        PreCondition.assertNotNull(node, "node");
        switch (node.getNodeKind())
        {
            case ATTRIBUTE :
            {
                return node.getValue();
            }
            case TEXT :
            case NAMESPACE :
            case COMMENT :
            case PROCESSING_INSTRUCTION :
            case DOCUMENT :
            {
                return bridge.wrapAtom(bridge.createString(node.getStringValue()));
            }
            case ELEMENT :
            {
                if (node.hasChildren())
                {
                    XmlNode firstChild = node.getFirstChild();
                    if ( (firstChild == node.getLastChild()) && firstChild.isText() ) // simple content
                        return firstChild.getValue();
                }
                return bridge.wrapAtom(bridge.createString(node.getStringValue())); // complex or empty content
            }
            default :
            {
                throw new AssertionError(node.getNodeKind());
            }
        }
    }

    @Override
    public void stream(XmlNode node, SequenceHandler<XmlAtom> handler, boolean bogus)
        throws GenXDMException
    {
        PreCondition.assertNotNull(node, "node");
        switch (node.getNodeKind())
        {
            case ELEMENT:
            {
                handler.startElement(node.getNamespaceURI(), node.getLocalName(), node.getPrefix(), node.getTypeName());
                try
                {
                    Iterable<XmlNode> namespaces = getNamespaceAxis(node, false);
                    for (XmlNode namespace : namespaces)
                    {
                        stream(namespace, handler, bogus);
                    }
                    Iterable<XmlNode> attributes = getAttributeAxis(node, false);
                    for (XmlNode attribute : attributes)
                    {
                        stream(attribute, handler, bogus);
                    }
                    Iterable<XmlNode> children = getChildAxis(node);
                    for (XmlNode child : children)
                    {
                        stream(child, handler, bogus);
                    }
                }
                finally
                {
                    handler.endElement();
                }
                break;
            }
            case ATTRIBUTE:
            {
                List<XmlAtom> value = iterableToList(node.getValue());
                handler.attribute(node.getNamespaceURI(), node.getLocalName(), node.getPrefix(), value, node.getTypeName());
                break;
            }
            case TEXT:
            {
                List<XmlAtom> value = iterableToList(node.getValue());
                handler.text(value);
                break;
            }
            case DOCUMENT:
            {
                // TODO: possibly, anyway.  doc type decl?  less important in sa-land.
                handler.startDocument(((XmlRootNode)node).getDocumentURI(), null);
                try
                {
                    Iterable<XmlNode> children = getChildAxis(node);
                    for (XmlNode child : children)
                    {
                        stream(child, handler, bogus);
                    }
                }
                finally
                {
                    handler.endDocument();
                }
                break;
            }
            case NAMESPACE:
            {
                handler.namespace(node.getLocalName(), node.getStringValue());
                break;
            }
            case COMMENT:
            {
                handler.comment(node.getStringValue());
                break;
            }
            case PROCESSING_INSTRUCTION:
            {
                handler.processingInstruction(node.getLocalName(), node.getStringValue());
                break;
            }
            default:
            {
                throw new AssertionError(node.getNodeKind());
            }
        }
    }
    
    private <E> List<E> iterableToList(Iterable<? extends E> iterable)
    {
        List<E> list = new ArrayList<E>();
        for (E e : iterable)
            list.add(e);
        return list;
    }
    
    private final AtomBridge<XmlAtom> bridge;
}
