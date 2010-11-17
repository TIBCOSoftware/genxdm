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
package org.genxdm.bridge.cx.tree;

import org.genxdm.NodeKind;
import org.genxdm.base.mutable.MutableModel;
import org.genxdm.bridge.cx.base.XmlNodeBuilder;
import org.genxdm.bridge.cx.base.XmlNodeModel;
import org.genxdm.exceptions.PreCondition;

public class XmlNodeMutator
    extends XmlNodeModel
    implements MutableModel<XmlNode>
{

    public XmlNode adoptNode(final XmlNode target, final XmlNode source)
    {
        if (source.parent != null)
        {
            XmlContainerNode parentNode = (XmlContainerNode)source.parent;
            if (source.getNodeKind() == NodeKind.ATTRIBUTE)
                ((XmlElementNode)parentNode).removeAttribute((XmlAttributeNode)source);
            else if (source.getNodeKind() == NodeKind.NAMESPACE)
                ((XmlElementNode)parentNode).removeNamespace((XmlNamespaceNode)source);
            else
                parentNode.removeChild(source);
        }
        source.parent = null;
        source.setDocument((XmlRootNode)target);
        return source;
    }

    public XmlNode appendChild(XmlNode parent, XmlNode newChild)
    {
        ((XmlContainerNode)parent).appendChild(newChild);
        return newChild;
    }

    public XmlNode cloneNode(XmlNode source, boolean deep) {
    	return cloneNodeInto(source, deep, source.getRoot());
    }
    
    public XmlNode cloneNodeInto(XmlNode source, boolean deep, XmlRootNode document)
    {
        if (deep)
        {
            XmlNodeBuilder builder = new XmlNodeBuilder(document);
            Walker.walk(source, builder);
            return builder.getNode();
        }
        switch (source.getNodeKind())
        {
            case ELEMENT:
            {
                return factory.createElement(document, source.namespaceURI, source.localName, source.prefixHint);
            }
            case ATTRIBUTE:
            {
                return factory.createAttribute(document, source.namespaceURI, source.localName, source.prefixHint, source.getStringValue());
            }
            case NAMESPACE:
            {
                return factory.createNamespace(document, source.localName, source.getStringValue());
            }
            case TEXT:
            {
                return factory.createText(document, source.getStringValue());
            }
            case COMMENT:
            {
                return factory.createComment(document, source.getStringValue());
            }
            case DOCUMENT:
            {
                return factory.createDocument(source.getDocumentURI(), ((XmlRootNode)source).docTypeDecl);
            }
            case PROCESSING_INSTRUCTION:
            {
                return factory.createProcessingInstruction(document, source.localName, source.getStringValue());
            }
            default:
                throw new AssertionError(source.getNodeKind());
        }
    }

    public XmlNode getOwner(final XmlNode node)
    {
        // don't you hate dom-isms?
        // contract says "never null".  horsefeathers.
        return node.getRoot();
    }

    public XmlNode importNode(final XmlNode target, final XmlNode source, final boolean deep)
    {
        // the difference, in theory, is changing the owner document.
        // put it in a different document, and voici: different owner document.
        XmlNode node = cloneNodeInto(source, deep, (XmlRootNode)target);
        return node;
    }

    public XmlNode insertBefore(final XmlNode parent, final XmlNode newChild, final XmlNode refChild)
    {
        if (refChild == null)
            return appendChild(parent, newChild);
        ((XmlContainerNode)parent).insertChild(newChild, refChild);
        return newChild;
    }

    public void normalize(XmlNode node)
    {
        // Normalization coalesces adjacent text nodes into a single node and removes 
        // any text nodes that have dm:string-value equivalent to a zero-length string.
        if (node.isText() && node.getStringValue().trim().equals(""))
        {
            if (node.parent != null)
                node.parent.removeChild(node);
        }
        if (node.isElement())
        {
            XmlNode child = ((XmlContainerNode)node).firstChild;
            while (child != null)
            {
                if (child.isText())
                {
                    XmlNode next = child.nextSibling;
                    if ( (next != null) &&  next.isText() )
                    {
                        // replace the second text node with a text node containing both strings
                        XmlTextNode newNode = factory.createText(node.getRoot(), child.getStringValue() + next.getStringValue());
                        replaceChild(node, newNode, next);
                        // remove the first text node.
                        ((XmlContainerNode)node).removeChild(child);
                        next = newNode;
                    }
                    else
                    {
                        normalize(child);
                    }
                    child.nextSibling = next; // restore it, for iterating ....
                }
                else if (child.isElement())
                    normalize(child);
                child = child.nextSibling;
            }
        }
        if (node.getNodeKind() == NodeKind.DOCUMENT)
        {
            XmlElementNode child = node.getFirstChildElement();
            if (child != null)
                normalize(child);
        }
    }

    public void removeAttribute(XmlNode element, String namespaceURI, String localName)
    {
        // cheap and sleazy:
        XmlAttributeNode attribute = factory.createAttribute(element.getRoot(), namespaceURI, localName, "", "");
        ((XmlElementNode)element).removeAttribute(attribute);
    }

    public XmlNode removeChild(XmlNode parent, XmlNode oldChild)
    {
        ((XmlContainerNode)parent).removeChild(oldChild);
        return oldChild;
    }

    public void removeNamespace(XmlNode element, String prefix)
    {
        // cheap and sleazy:
        XmlNamespaceNode toRemove = factory.createNamespace(element.getRoot(), prefix, "");
        ((XmlElementNode)element).removeNamespace(toRemove);
    }

    public XmlNode replaceChild(XmlNode parent, XmlNode newChild, XmlNode oldChild)
    {
        ((XmlContainerNode)parent).insertChild(newChild, oldChild);
        ((XmlContainerNode)parent).removeChild(oldChild);
        return oldChild;
    }

    public void setAttribute(XmlNode e, XmlNode attribute)
    {
        XmlElementNode element = (XmlElementNode)PreCondition.assertNotNull(e, "element");
        element.setAttribute((XmlAttributeNode)attribute);
    }

    public XmlNode setAttribute(XmlNode element, String namespaceURI, String localName, String prefix, String value)
    {
        PreCondition.assertNotNull(namespaceURI, "namespaceURI");
        PreCondition.assertNotNull(localName, "localName");
        PreCondition.assertNotNull(value, "value");
        XmlAttributeNode attr = factory.createAttribute(element, namespaceURI, localName, prefix, value);
        setAttribute(element, attr);
        return attr;
    }

    public void setNamespace(XmlNode e, XmlNode namespace)
    {
        XmlElementNode element = (XmlElementNode)PreCondition.assertNotNull(e, "element");
        element.setNamespace((XmlNamespaceNode)namespace);
    }

    public XmlNamespaceNode setNamespace(XmlNode element, String prefixString, String uriSymbol)
    {
        PreCondition.assertNotNull(prefixString, "prefix");
        PreCondition.assertNotNull(uriSymbol, "namespaceURI");
        XmlNamespaceNode result = factory.createNamespace(element, prefixString, uriSymbol);
        setNamespace(element, result);
        return result;
    }
    
    public void setParent(final XmlNode child, final XmlContainerNode parent)
    {
        if (child != null)
            child.setParent(parent);
    }

    private XmlNodeFactory factory = new XmlNodeFactory();
}
