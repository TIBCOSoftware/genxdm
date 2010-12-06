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
package org.genxdm.base.mutable;

import org.genxdm.base.Cursor;

public interface MutableCursor<N>
    extends Cursor<N>
{

	NodeFactory<N> getFactoryForContext();

	/**
     * Appends the specified child to the end of the child axis of the specified parent.
     * 
     * @param newChild
     *            The child to be added to the parent.
     * @return The child added to the parent.
     */
    void appendChild(final N newChild);
    
    void appendChildren(final Iterable<N> content);
    
    void prependChild(final N newChild);
    
    void prependChildren(final Iterable<N> content);

    /**
     * Inserts a new child node before a specified reference node in the child axis of a parent node.
     * <p>
     * Insertion is not expected and not required to result in a normalized tree.
     * </p>
     * 
     * @param newChild
     *            The new child to be added to the parent.
     */
    void insertBefore(final N previous);
    
    void insertBefore(final Iterable<N> content);
    
    void insertAfter(final N next);
    
    void insertAfter(final Iterable<N> content);

    /**
     * Removes a node from the child axis of the parent node.
     * 
     * @return The child that has been removed.
     */
    N delete();
    
    Iterable<N> deleteChildren();

    /**
     * Replaces a node in the child axis of a parent node.
     * 
     * @param newNode
     *            The new node that will replace the old node.
     * @return The old node that was removed.
     */
    N replace(final N newNode);
    
    void replaceValue(final String value);

    /**
     * Sets an attribute node into the attribute axis of an element.
     * 
     * @param attribute
     *            The attribute to be inserted.
     */
    void insertAttribute(final N attribute);
    
    void insertAttributes(final Iterable<N> attributes);

    /**
     * Sets a namespace binding into the namespace axis of an element.
     * 
     * @param prefixString
     *            The prefix (local-name part of the dm:name) of the namespace node as a <code>String</code>.
     * @param uriSymbol
     *            The dm:string-value of the namespace node as a symbol.
     */
    void insertNamespace(final String prefixString, final String uriSymbol);
    
    // if we were to do this, it would look something like this:
    //void rename(final String namespace, final String name);
}
