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
package org.genxdm.bridge.axiom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMNodeEx;
import org.genxdm.bridgekit.misc.StringToURIParser;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

public class AxiomMutableModel
    extends AxiomModel
    implements MutableModel<Object>
{
    public AxiomMutableModel(final AxiomFactory factory)
    {
        this.factory = PreCondition.assertNotNull(factory);
    }
    
    /**
     * Appends the specified child to the end of the child axis of the specified parent.
     * 
     * @param parent
     *            The parent to which the child should be added.
     * @param newChild
     *            The child to be added to the parent.
     */
    public void appendChild(Object parent, Object content)
    {
        PreCondition.assertNotNull(parent, "parent");
        PreCondition.assertNotNull(content, "content");
        OMContainer container = AxiomSupport.dynamicDowncastContainer(parent);
        OMNode childNode = AxiomSupport.staticDowncastNode(content);
        container.addChild(childNode);
    }

    public void appendChildren(final Object parent, final Iterable<Object> content)
    {
        PreCondition.assertNotNull(content, "content"); // could be empty, though
        for (Object node : content)
        {
            appendChild(parent, node);
        }
    }

    public Object copyNode(Object source, boolean deep)
    {
        if (deep)
        {
            AxiomFragmentBuilder builder = new AxiomFragmentBuilder(factory.omFactory, false);
            stream(source, builder);
            return builder.getNode();
        }
        else
        {
            switch (getNodeKind(source))
            {
                case DOCUMENT :
                    return factory.createDocument(getDocumentURI(source), null);
                case ELEMENT :
                    return factory.createElement(getNamespaceURI(source), getLocalName(source), getPrefix(source));
                case TEXT :
                    return factory.createText(getStringValue(source));
                case COMMENT :
                    return factory.createComment(getStringValue(source));
                case PROCESSING_INSTRUCTION :
                    return factory.createProcessingInstruction(getLocalName(source), getStringValue(source));
                case ATTRIBUTE :
                    return factory.createAttribute(getNamespaceURI(source), getLocalName(source), getPrefix(source), getStringValue(source));
                case NAMESPACE :
                default :
                    throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Removes a node from the child axis of the parent node.
     * @param target
     *            The child to be removed.
     * 
     * @return The child that has been removed.
     */
    public Object delete(final Object target)
    {
        switch (getNodeKind(target))
        {
            case ATTRIBUTE : 
                OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(target);
                OMElement parent = attribute.getOwner();
                parent.removeAttribute(attribute);
                return attribute;
            case NAMESPACE :
                throw new UnsupportedOperationException();
            case DOCUMENT :
                throw new IllegalArgumentException("cannot delete document");
            default :
                OMNode node = AxiomSupport.dynamicDowncastNode(target);
                //OMContainer container = node.getParent();
                
                node.detach();

                // this no longer works, because you can't null the document element.
                // so it's not actually removed. what fun.
//                if (container instanceof OMDocument && node instanceof OMElement) {
//                  ((OMDocument) container).setOMDocumentElement(null);
//                }
                return node;
        }
    }

    public Iterable<Object> deleteChildren(final Object target)
    {
        PreCondition.assertNotNull(target, "target");
        OMContainer container = AxiomSupport.dynamicDowncastContainer(target);
        OMNode current = container.getFirstOMChild();
        OMNode next = current.getNextOMSibling();
        List<Object> list = new ArrayList<Object>();
        while (current != null)
        {
            list.add(current);
            current.detach();
            current = next;
            if (current != null)
                next = current.getNextOMSibling();
        }
        return list;
    }

    public NodeFactory<Object> getFactory(Object node)
    {
        return factory;
    }

    public void insertAfter(final Object target, final Object content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        OMNode t = AxiomSupport.dynamicDowncastNode(target);
        OMNode c = AxiomSupport.dynamicDowncastNode(content);
        t.insertSiblingAfter(c);
    }

    public void insertAfter(final Object target, final Iterable<Object> content)
    {
        PreCondition.assertNotNull(content, "content");
        Object lastPosition = target;
        for (Object node : content)
        {
            insertAfter(lastPosition, node);
            lastPosition = node;
        }
    }

    /**
     * Sets an attribute node into the attribute axis of an element.
     * 
     * @param element
     *            The element that will hold the attribute.
     * @param attribute
     *            The attribute to be inserted.
     */
    public void insertAttribute(final Object target, final Object content)
    {
        PreCondition.assertNotNull(target, "element");
        PreCondition.assertNotNull(content, "attribute");
        OMElement element = AxiomSupport.dynamicDowncastElement(target);
        OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(content);
        element.addAttribute(attribute);
    }

    public void insertAttributes(final Object element, final Iterable<Object> attributes)
    {
        PreCondition.assertNotNull(attributes, "attributes");
        for (Object attr : attributes)
        {
            insertAttribute(element, attr);
        }
    }

    /**
     * Inserts a new child node before a specified reference node in the child axis of a parent node.
     * <p>
     * Insertion is not expected and not required to result in a normalized tree.
     * </p>
     * @param target
     *            The reference child before which the new node will be added. If no reference child is specified then
     *            the new child is appended to the children of the parent node.
     * @param content
     *            The new child to be added to the parent.
     * 
     * @return The node that was inserted.
     */
    public void insertBefore(final Object target, final Object content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        OMNode t = AxiomSupport.dynamicDowncastNode(target);
        OMNode c = AxiomSupport.dynamicDowncastNode(content);
        insertBeforeWorkAround(t, c);
    }

    public void insertBefore(final Object target, final Iterable<Object> content)
    {
        PreCondition.assertNotNull(content, "content");
        // this does *not* need to be reversed.
        for (Object node : content)
        {
            insertBefore(target, node);
        }
    }

    /**
     * Sets a namespace binding into the namespace axis of an element.
     * 
     * @param element
     *            The element that will hold the namespace binding.
     * @param prefix
     *            The prefix (local-name part of the dm:name) of the namespace node as a <code>String</code>.
     * @param uri
     *            The dm:string-value of the namespace node.
     */
    public Object insertNamespace(final Object element, final String prefix, final String uri)
    {
        OMNamespace ns = factory.omFactory.createOMNamespace(StringToURIParser.parse(uri).toString(), prefix);
        OMElement omElem = AxiomSupport.staticDowncastElement(element);
        omElem.declareNamespace(ns);
        return ns;
    }

    public void prependChild(final Object parent, final Object content)
    {
        PreCondition.assertNotNull(parent, "parent");
        PreCondition.assertNotNull(content, "content");
        OMContainer container = AxiomSupport.dynamicDowncastContainer(parent);
        OMNode node = AxiomSupport.dynamicDowncastNode(content);
        OMNode first = container.getFirstOMChild();
        first.insertSiblingBefore(node);
    }

    public void prependChildren(final Object parent, final Iterable<Object> content)
    {
        PreCondition.assertNotNull(content, "content");
        List<Object> reversed = new ArrayList<Object>();
        for (Object node : content)
        {
            reversed.add(node);
        }
        Collections.reverse(reversed);
        for (Object node : reversed)
        {
            prependChild(parent, node);
        }
    }

    /**
     * Replaces a node in the child axis of a parent node.
     * @param target
     *            The old node to be replaced.
     * @param content
     *            The new node that will replace the old node.
     * 
     * @return The old node that was removed.
     */
    public Object replace(final Object target, final Object content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        switch (getNodeKind(target))
        {
            case ATTRIBUTE :
                if (getNodeKind(content).isAttribute())
                {
                    OMAttribute original = AxiomSupport.dynamicDowncastAttribute(target);
                    OMAttribute replacement = AxiomSupport.dynamicDowncastAttribute(content);
                    OMElement owner = original.getOwner();
                    owner.removeAttribute(original);
                    owner.addAttribute(replacement);
                    return original;
                }
                throw new IllegalArgumentException();
            case ELEMENT :
            case TEXT :
            case COMMENT :
            case PROCESSING_INSTRUCTION :
                if (getNodeKind(content).isChild())
                {
                    OMNode original = AxiomSupport.dynamicDowncastNode(target);
                    OMNode replacement = AxiomSupport.dynamicDowncastNode(content);
                    
                    OMContainer originalParent = insertBeforeWorkAround(
                            original, replacement);

                    original.detach();
                    
                    if (originalParent instanceof OMDocument && replacement instanceof OMElement) {
                        ( (OMDocument) originalParent).setOMDocumentElement( (OMElement) replacement);
                    }
                    
                    return target;
                }
                throw new IllegalArgumentException();
            case NAMESPACE :
                // I don't think that this can be done.
                // the implications are pretty bizarre, too, when you come
                // down to it.  Anyway ... dunno if this will work in axiom,
                // and I think replace is going to allow it not to work for
                // namespaces.  Adding it to the unsupported group.
            case DOCUMENT :
            default :
                throw new UnsupportedOperationException();
        }
    }

    public String replaceValue(final Object target, final String value)
    {
        PreCondition.assertNotNull(target);
        String text = (value == null) ? "" : value;
        String original = null;
        switch (getNodeKind(target))
        {
            case TEXT :
                // can't actually replace a text node's value, in axiom
                OMText node = AxiomSupport.dynamicDowncastText(target);
                insertBeforeWorkAround(node, factory.createText(value));
                node.detach();
                return node.getText();
            case ATTRIBUTE :
                OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(target);
                original = attribute.getAttributeValue();
                attribute.setAttributeValue(text);
                return original;
            case COMMENT :
                OMComment comment = AxiomSupport.dynamicDowncastComment(target);
                original = comment.getValue();
                comment.setValue(text);
                return original;
            case PROCESSING_INSTRUCTION :
                OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(target);
                original = pi.getValue();
                pi.setValue(text);
                return original;
            default :
                throw new UnsupportedOperationException();
        }
    }

    /**
     * This removes the value of the attribute from the ID to element map that
     * we have for the document in question.
     */
    @Override
    public void setIsIdAttribute(Object attr, boolean isId) {
        if (!isAttribute(attr)) {
            throw new IllegalArgumentException("Attempt to setIsIdAttribute on non attribute not allowed.");
        }
        
        OMDocument doc = AxiomSupport.dynamicDowncastDocument(getRoot(attr));
        OMAttribute attrNode = AxiomSupport.staticDowncastAttribute(attr);
        Map<String, OMElement> idMap = AxiomSupport.getIdMap(doc);

        String type = attrNode.getAttributeType(); 
        if ( !DtdAttributeKind.CDATA.toString().equals(type) && !DtdAttributeKind.ID.toString().equals(type)) {
            throw new IllegalStateException("Attempting to set the isId character of an attribute that isn't a CDATA or ID type.");
        }
        String idValue = getStringValue(attr);
        
        if (isId) {
            idMap.put(idValue, attrNode.getOwner());
            attrNode.setAttributeType(DtdAttributeKind.ID.toString());
        }
        else {
            idMap.remove(idValue);
            attrNode.setAttributeType(DtdAttributeKind.CDATA.toString());
        }
    }

    /**
     * This method exists as a work-around for AXIOM-360
     * 
     * @param target
     * @param toInsertBefore
     * @return
     */
    private OMContainer insertBeforeWorkAround(OMNode target,
            OMNode toInsertBefore) {
        // See comment below about AXIOM-360 - note we have to get the
        // parent before detaching.
        OMContainer originalParent = target.getParent();
        
        target.insertSiblingBefore(toInsertBefore);

        // Note that the following three lines are a work-around for bug:
        // https://issues.apache.org/jira/browse/AXIOM-360, in 1.2.11 of AXIOM
        OMNodeEx replacementImpl = (OMNodeEx) toInsertBefore;
        replacementImpl.setParent(originalParent);
        return originalParent;
    }

    private final AxiomFactory factory;
}
