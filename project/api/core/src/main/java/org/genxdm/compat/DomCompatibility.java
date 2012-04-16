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
package org.genxdm.compat;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

/**
 * Methods that provide similar functionality to DOM capability, where that capability
 * should not be part of the generic API.
 */
public class DomCompatibility {

    /**
     * Replaces a particular node with a sequence of nodes - this mirrors the behavior
     * of DOM DocumentFragment behavior.
     * 
     * @param model The tree model bridge
     * @param toReplace Node to be replaced.
     * @param newNodes  Nodes to be inserted.
     */
    public static <N> void replaceNodeSequence(MutableModel<N> model, N toReplace, Iterable<N> newNodes) {

        boolean firstIteration = true;
        N next = model.getNextSibling(toReplace);
        N parent = model.getParent(toReplace);
        
        for (N newNode : newNodes) {
            if (firstIteration) {
                model.replace(toReplace, newNode);
                firstIteration = false;
            }
            else {
                // since we can only replace one node, after the first one, we have to be satisfied with
                // inserting or appending, depending on the target node.
                if (next == null) {
                    model.appendChild(parent, newNode);
                }
                else {
                    model.insertBefore(next, newNode);
                }
            }
        }
    }
    
    /**
     * Finds the first descendant or self element that has the given namespace or name.  
     * @param <N>
     * @param model  What's the model that we're traversing?
     * @param node  What node are we starting with?
     * @param namespaceURI  What namespace are we searching for?  <code>null</code> implies any namespace
     * @param localName What local name are we searching for?  <code>null</code> implies any local name.
     * @return The first descendent or self that matches the given name and local name.
     */
    public static <N> N getFirstDescendantOrSelfElementByName(Model<N> model, N node, String namespaceURI, 
            String localName) {
        
        for (N possible : model.getDescendantOrSelfAxis(node)) {
            if (model.getNodeKind(possible) == NodeKind.ELEMENT) {
                if ( (namespaceURI == null || namespaceURI.equals(model.getNamespaceURI(possible)) )
                        && (localName == null || localName.equals(model.getLocalName(possible)))) {
                    
                    return possible;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all of the descendant elements matching a given namespace and local name.
     * @param <N>
     * @param model Which model are we traversing.
     * @param node  From which node do we start?
     * @param namespaceURI  What's the namespace to use.  <code>null</code> matches any.
     * @param localName What's the local name to find? <code>null</code> matches any.
     * 
     * @return  A list of the found element nodes.
     * 
     * TODO - This method should be rewritten to do name filtering on any iterator, so that we can do
     * filtering along any axis.  See JIRA issue GXOS-34
     */
    public static <N> Iterable<N> getDescendantOrSelfElementsByName(Model<N> model, N node, String namespaceURI,
            String localName) {
        
        List<N> result = new ArrayList<N>();
        for (N possible : model.getDescendantOrSelfAxis(node)) {
            if (model.getNodeKind(possible) == NodeKind.ELEMENT) {
                if ( (namespaceURI == null || namespaceURI.equals(model.getNamespaceURI(possible)) )
                        && (localName == null || localName.equals(model.getLocalName(possible)))) {
                    
                    result.add(possible);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Utility method to fill a list from an {@link Iterable}
     * @param <N>
     * @param axis
     * @return A {@link List} from the Iterable.
     */
    public static <N> List<N> listFromIterable(Iterable<N> axis) {
        List<N> result = new ArrayList<N>();
        if(axis != null) {
            for (N node : axis) {
                result.add(node);
            }
        }
        return result;
    }
    
    /**
     * Either create or update an attribute on an element.
     * 
     * @param <N>   The node kind.
     * 
     * @param model    The model to use for navigation, modification, and to obtain a factory, if needed.
     * @param element   The element for which the attribute will be updated/added.
     * @param namespace The namespace for the attribute.
     * @param localName The local name of the attribute
     * @param prefix    What prefix to associate with the attribute.
     * @param value     The value of the attribute.
     */
    public static <N> void setAttribute(MutableModel<N> model, N element, String namespace, String localName, String prefix, String value) {
        NodeFactory<N> factory = model.getFactory(element);
        N currAttr = model.getAttribute(element, namespace, localName);
        if (currAttr != null) {
            model.replaceValue(currAttr, value);
        }
        else {
            N newAttr = factory.createAttribute(namespace, localName, prefix, value);
            model.insertAttribute(element, newAttr);
        }
    }
    
    public static <N> N insertBefore(MutableModel<N> model, N parent, N newChild, N refChild) {
        if (refChild == null) {
            model.appendChild(parent, newChild);
        }
        else {
            model.insertBefore(refChild, newChild);
        }
        
        return newChild;
    }
}
