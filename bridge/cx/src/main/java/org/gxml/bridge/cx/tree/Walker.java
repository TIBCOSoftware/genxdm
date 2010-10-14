/**
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
package org.gxml.bridge.cx.tree;

import org.genxdm.base.io.ContentHandler;

public final class Walker
{
    private Walker() {} // not instantiable.
    
    // this is here so that we can sidestep method calls in Cursor.write() and Model.stream().
    public static final void walk(final XmlNode node, final ContentHandler handler)
    {
        walk(node, true, handler);
    }
    
    public static final void walk(final XmlNode node, final boolean namespaces, final ContentHandler handler)
    {
        switch (node.getNodeKind())
        {
            case ELEMENT:
            {
                handler.startElement(node.namespaceURI, node.localName, node.prefixHint);
                try
                {
                    if (namespaces)
                    {
                        XmlNamespaceNode namespace = ((XmlElementNode)node).firstNamespace;
                        while (namespace != null)
                        {
                            walk(namespace, handler);
                            namespace = (XmlNamespaceNode)namespace.nextSibling;
                        }
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
                }
                finally
                {
                    handler.endElement();
                }
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
                try
                {
                    XmlNode child = ((XmlContainerNode)node).firstChild;
                    while (child != null)
                    {
                        walk(child, handler);
                        child = child.nextSibling;
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
                if (namespaces)
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
