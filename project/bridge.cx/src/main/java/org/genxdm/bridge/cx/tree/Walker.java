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
package org.genxdm.bridge.cx.tree;

import java.util.List;

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.io.ContentHandler;
import org.genxdm.typed.io.SequenceHandler;

public final class Walker
{
    private Walker() {} // not instantiable.
    
    public static final void walk(final XmlNode node, final ContentHandler handler)
    {
        switch (node.getNodeKind())
        {
            case ELEMENT:
            {
                handler.startElement(node.namespaceURI, node.localName, node.prefixHint);
                XmlNamespaceNode namespace = ((XmlElementNode)node).firstNamespace;
                while (namespace != null)
                {
                    walk(namespace, handler);
                    namespace = (XmlNamespaceNode)namespace.nextSibling;
                }
                XmlAttributeNode attribute = ((XmlElementNode)node).firstAttribute;
                while (attribute != null)
                {
                    walk(attribute, handler);
                    attribute = (XmlAttributeNode)attribute.nextSibling;
                }
                XmlNode child = ((XmlContainerNode)node).firstChild;
                while (child != null)
                {
                    walk(child, handler);
                    child = child.nextSibling;
                }
                handler.endElement();
                break;
            }
            case ATTRIBUTE:
            {
                handler.attribute(node.namespaceURI, node.localName, node.prefixHint, node.getStringValue(), null);
                break;
            }
            case TEXT:
            {
                handler.text(node.getStringValue());
                break;
            }
            case DOCUMENT:
            {
                handler.startDocument(((XmlRootNode)node).documentURI, ((XmlRootNode)node).docTypeDecl);
                XmlNode child = ((XmlContainerNode)node).firstChild;
                while (child != null)
                {
                    walk(child, handler);
                    child = child.nextSibling;
                }
                handler.endDocument();
                break;
            }
            case NAMESPACE:
            {
                handler.namespace(node.localName, node.getStringValue());
                break;
            }
            case COMMENT:
            {
                handler.comment(node.getStringValue());
                break;
            }
            case PROCESSING_INSTRUCTION:
            {
                handler.processingInstruction(node.localName, node.getStringValue());
                break;
            }
            default:
            {
                throw new AssertionError(node.getNodeKind());
            }
        }
    }
    
    public static final void walk(final XmlNode node, SequenceHandler<XmlAtom> handler, boolean bogus)
    {
        switch (node.getNodeKind())
        {
            case ELEMENT:
            {
                handler.startElement(node.namespaceURI, node.localName, node.prefixHint, node.getTypeName());
                XmlNamespaceNode namespace = ((XmlElementNode)node).firstNamespace;
                while (namespace != null)
                {
                    walk(namespace, handler, false);
                    namespace = (XmlNamespaceNode)namespace.nextSibling;
                }
                XmlAttributeNode attribute = ((XmlElementNode)node).firstAttribute;
                while (attribute != null)
                {
                    walk(attribute, handler, false);
                    attribute = (XmlAttributeNode)attribute.nextSibling;
                }
                XmlNode child = ((XmlContainerNode)node).firstChild;
                while (child != null)
                {
                    walk(child, handler, false);
                    child = child.nextSibling;
                }
                handler.endElement();
                break;
            }
            case ATTRIBUTE:
            {
                handler.attribute(node.namespaceURI, node.localName, node.prefixHint, (List<? extends XmlAtom>)node.getValue(), node.getTypeName());
                break;
            }
            case TEXT:
            {
                handler.text((List<? extends XmlAtom>)node.getValue());
                break;
            }
            case DOCUMENT:
            {
                handler.startDocument(((XmlRootNode)node).documentURI, ((XmlRootNode)node).docTypeDecl);
                XmlNode child = ((XmlContainerNode)node).firstChild;
                while (child != null)
                {
                    walk(child, handler, false);
                    child = child.nextSibling;
                }
                handler.endDocument();
                break;
            }
            case NAMESPACE:
            {
                handler.namespace(node.localName, node.getStringValue());
                break;
            }
            case COMMENT:
            {
                handler.comment(node.getStringValue());
                break;
            }
            case PROCESSING_INSTRUCTION:
            {
                handler.processingInstruction(node.localName, node.getStringValue());
                break;
            }
            default:
            {
                throw new AssertionError(node.getNodeKind());
            }
        }
    }
}
