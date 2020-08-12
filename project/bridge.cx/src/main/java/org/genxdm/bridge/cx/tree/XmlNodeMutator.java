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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genxdm.NodeKind;
import org.genxdm.bridge.cx.base.XmlNodeBuilder;
import org.genxdm.bridge.cx.base.XmlNodeModel;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

public class XmlNodeMutator
    extends XmlNodeModel
    implements MutableModel<XmlNode>
{

    public void appendChild(XmlNode parent, XmlNode newChild)
    {
        PreCondition.assertNotNull(parent, "parent");
        PreCondition.assertNotNull(newChild, "newChild");
        PreCondition.assertTrue(parent.getNodeKind().isContainer(), "parent is container");
        PreCondition.assertTrue(newChild.getNodeKind().isChild(), "content is child");
        ((XmlContainerNode)parent).appendChild(newChild);
    }

    public void appendChildren(final XmlNode parent, final Iterable<XmlNode> content)
    {
        PreCondition.assertNotNull(content, "content");
        for (XmlNode node : content)
        {
            appendChild(parent, node);
        }
    }

    public XmlNode copyNode(XmlNode source, boolean deep) 
    {
        return cloneNodeInto(source, deep);
    }
    
    public XmlNode cloneNodeInto(XmlNode source, boolean deep)
    {
        if (deep)
        {
            XmlNodeBuilder builder = new XmlNodeBuilder();
            Walker.walk(source, builder);
            return builder.getNode();
        }
        switch (source.getNodeKind())
        {
            case ELEMENT:
            {
                return factory.createElement(source.namespaceURI, source.localName, source.prefixHint);
            }
            case ATTRIBUTE:
            {
                return factory.createAttribute(source.namespaceURI, source.localName, source.prefixHint, source.getStringValue());
            }
            case NAMESPACE:
            {
                return factory.createNamespace(source.localName, source.getStringValue());
            }
            case TEXT:
            {
                return factory.createText(source.getStringValue());
            }
            case COMMENT:
            {
                return factory.createComment(source.getStringValue());
            }
            case DOCUMENT:
            {
                return factory.createDocument(source.getDocumentURI(), ((XmlRootNode)source).docTypeDecl);
            }
            case PROCESSING_INSTRUCTION:
            {
                return factory.createProcessingInstruction(source.localName, source.getStringValue());
            }
            default:
                throw new AssertionError(source.getNodeKind());
        }
    }

    public XmlNode delete(final XmlNode target)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertTrue(target.hasParent(), "target has parent");
        NodeKind kind = target.getNodeKind();
        if (kind == NodeKind.ATTRIBUTE)
            ((XmlElementNode)target.getParent()).removeAttribute((XmlAttributeNode)target);
        else if (kind == NodeKind.NAMESPACE)
            ((XmlElementNode)target.getParent()).removeNamespace((XmlNamespaceNode)target);
        else
            target.getParent().removeChild(target);
        return target;
    }

    public Iterable<XmlNode> deleteChildren(final XmlNode target)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertTrue(target.getNodeKind().isContainer(), "target is container");
        // cheap and sleazy (but probably correct?) implementation:
        if (target.getNodeKind().isContainer())
        {
            XmlContainerNode container = (XmlContainerNode)target;
            if (container.hasChildren())
            {
                List<XmlNode> children = new ArrayList<XmlNode>();
                XmlNode child = container.getFirstChild();
                while (child != null)
                {
                    XmlNode next = child.getNextSibling(); // do it first, because the pointer will be adjusted on delete
                    children.add(delete(child));
                    child = next;
                }
                return children;
            }
        }
        return null;
    }
    
    public NodeFactory<XmlNode> getFactory(XmlNode node)
    {
        return factory;
    }

    public void insertAfter(final XmlNode target, final XmlNode content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        PreCondition.assertTrue(target.hasParent(), "target has parent");
        PreCondition.assertTrue(content.getNodeKind().isChild(), "content is child");
        XmlNode next = target.getNextSibling();
        if (next == null)
        {
            target.getParent().appendChild(content);
        }
        else
        {
            target.getParent().insertChild(content, next);
        }
    }
    
    public void insertAfter(final XmlNode target, final Iterable<XmlNode> content)
    {
        PreCondition.assertNotNull(content, "content");
        List<XmlNode> reversed = new ArrayList<XmlNode>();
        for (XmlNode node : content)
        {
            reversed.add(node);
        }
        Collections.reverse(reversed);
        for (XmlNode node : reversed)
        {
            insertAfter(target, node);
        }
    }

    public void insertAttribute(final XmlNode element, final XmlNode attribute)
    {
// these preconditions are actually correct, but we're not going to use them        
//        PreCondition.assertNotNull(element, "element");
        PreCondition.assertNotNull(attribute, "attribute");
//        PreCondition.assertTrue(element.getNodeKind() == NodeKind.ELEMENT, "target is element");
        PreCondition.assertTrue(attribute.getNodeKind() == NodeKind.ATTRIBUTE, "content is attribute");
        // if it's an element, do something; otherwise, quietly discard the attribute by taking no action
        if (element.isElement())
            ((XmlElementNode)element).setAttribute((XmlAttributeNode)attribute);
    }

    public void insertAttributes(final XmlNode element, final Iterable<XmlNode> attributes)
    {
        PreCondition.assertNotNull(attributes, "attributes");
        for (XmlNode attribute : attributes)
        {
            insertAttribute(element, attribute);
        }
    }

    public void insertBefore(final XmlNode target, final XmlNode content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        PreCondition.assertTrue(target.hasParent(), "target has parent");
        PreCondition.assertTrue(content.getNodeKind().isChild(), "content is child");
        target.getParent().insertChild(content, target);
    }
    
    public void insertBefore(final XmlNode target, final Iterable<XmlNode> content)
    {
        PreCondition.assertNotNull(content, "content");
        for (XmlNode node : content)
        {
            insertBefore(target, node);
        }
    }

    public XmlNode insertNamespace(XmlNode element, String prefix, String uri)
    {
        PreCondition.assertNotNull(element, "element");
        PreCondition.assertNotNull(prefix, "prefix");
        PreCondition.assertNotNull(uri, "namespaceURI");
        PreCondition.assertTrue(element.getNodeKind() == NodeKind.ELEMENT, "target is element");
        XmlNamespaceNode result = factory.createNamespace(prefix, uri);
        ((XmlElementNode)element).setNamespace(result);
        
        return result;
    }
    
    public void prependChild(final XmlNode parent, final XmlNode content)
    {
        PreCondition.assertNotNull(parent, "parent");
        PreCondition.assertNotNull(content, "content");
        PreCondition.assertTrue(parent.getNodeKind().isContainer(), "parent is container");
        PreCondition.assertTrue(content.getNodeKind().isChild(), "content is child");
        XmlContainerNode container = (XmlContainerNode)parent;
        if (container.getFirstChild() == null)
            container.appendChild(content);
        else
            container.insertChild(content, container.getFirstChild());
    }

    public void prependChildren(final XmlNode parent, final Iterable<XmlNode> content)
    {
        PreCondition.assertNotNull(content, "content");
        PreCondition.assertTrue(parent.getNodeKind().isContainer(), "parent is container");
        XmlNode first = ((XmlContainerNode)parent).getFirstChild();
        if (first == null)
        {
            appendChildren(parent, content);
        }
        else
        {
            for (XmlNode child : content)
            {
                insertBefore(first, child);
            }
        }
    }

    public XmlNode replace(XmlNode target, XmlNode content)
    {
        PreCondition.assertNotNull(target);
        PreCondition.assertTrue(target.hasParent(), "target has parent");
        NodeKind kind = target.getNodeKind();
        if (kind == NodeKind.ATTRIBUTE)
            PreCondition.assertTrue(content.getNodeKind() == NodeKind.ATTRIBUTE);
        else if (kind == NodeKind.NAMESPACE)
            PreCondition.assertTrue(content.getNodeKind() == NodeKind.NAMESPACE);
        else
        {
            NodeKind contentKind = content.getNodeKind();
            PreCondition.assertTrue( (contentKind == NodeKind.TEXT) ||
                                     (contentKind == NodeKind.ELEMENT) ||
                                     (contentKind == NodeKind.COMMENT) ||
                                     (contentKind == NodeKind.PROCESSING_INSTRUCTION) );
        }
        
        XmlContainerNode parent = target.getParent();
        if (kind == NodeKind.ATTRIBUTE)
        {
            // preconditions guarantee safe casting.
            ((XmlElementNode)parent).removeAttribute((XmlAttributeNode)target);
            ((XmlElementNode)parent).setAttribute((XmlAttributeNode)content);
        }
        else if (kind == NodeKind.NAMESPACE)
        {
            // preconditions guarantee safe casting.
            ((XmlElementNode)parent).removeNamespace((XmlNamespaceNode)target);
            ((XmlElementNode)parent).setNamespace((XmlNamespaceNode)content);
        }
        else
        {
            parent.insertChild(content, target);
            parent.removeChild(target);
        }
        return target;
    }
    
    public String replaceValue(final XmlNode target, final String value)
    {
        PreCondition.assertNotNull(target);
        NodeKind kind = target.getNodeKind();
        PreCondition.assertTrue( (kind == NodeKind.TEXT) ||
                                 (kind == NodeKind.ATTRIBUTE) ||
                                 (kind == NodeKind.COMMENT) ||
                                 (kind == NodeKind.PROCESSING_INSTRUCTION) );
        return ((XmlLeafNode)target).setValue(value);
    }

    /**
     * This implementation needs to update the document (a.k.a "root") node with the new
     * attribute as an ID attribute, and update the attribute itself as an ID attribute
     * (or, the opposite, depending on the value of the isId parameter).
     */
    @Override
    public void setIsIdAttribute(XmlNode attr, boolean isId) {
        
        // Note that throwing this exception is an explicit part of the contract for this
        // method, thus not using the PreCondition.... methods.
        if (!attr.isAttribute()) {
            throw new IllegalArgumentException("The passed node is not an attribute node.");
        }

        // Do the casts to various bits...
        XmlAttributeNode attrNode = (XmlAttributeNode) attr;
        XmlNode root = getRoot(attr);
        if (root == null) {
            throw new IllegalStateException("Attempting to set an isId true on an attribute, but no root found.");
        }
        XmlRootNode rootNode = (XmlRootNode) root;
        
        // only if the attribute is not already an ID node should we set it as an ID attribute
        if (isId) {
            rootNode.addIdNode(attr);
        }
        else {
            rootNode.removeIdNode(attr);
        }
        attrNode.setIsDtdIdAttribute(isId);
    }

    // this is used when creating fake nodes.  they point to their parents,
    // but the parents don't point to them.
    public void setParent(final XmlNode child, final XmlContainerNode parent)
    {
        if (child != null)
            child.setParent(parent);
    }

    // this is used by the node builder to keep the stack straight; the insertNamespace
    // method unfortunately doesn't return the node that is implicitly created.
    public void setNamespace(final XmlElementNode element, final XmlNamespaceNode namespace)
    {
        PreCondition.assertNotNull(element, "element");
        element.setNamespace(namespace);
    }
    
    private XmlNodeFactory factory = new XmlNodeFactory();
}
