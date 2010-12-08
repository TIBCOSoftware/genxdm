/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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

import org.genxdm.base.Model;

/** Provides modification of the Model based on the XQuery Update Facility,
 * but with immediate effect.
 *
 * @author Amy! &lt;aaletal@gmail.com>
 **/
public interface MutableModel<N> extends Model<N>
{
    /**
     * Appends the specified child to the end of the child axis of the specified parent.
     * 
     * @param parent
     *            The parent to which the child should be added.
     * @param newChild
     *            The child to be added to the parent.
     */
    void appendChild(final N parent, final N content);

    void appendChildren(final N parent, final Iterable<N> content);

    N copyNode(N source, boolean deep);

    /**
     * Removes a node from the child axis of the parent node.
     * @param target
     *            The child to be removed.
     * 
     * @return The child that has been removed.
     */
    N delete(final N target);

    Iterable<N> deleteChildren(final N target);

    NodeFactory<N> getFactoryForContext(N node);

    void insertAfter(final N target, final N content);

    void insertAfter(final N target, final Iterable<N> content);

    /**
     * Sets an attribute node into the attribute axis of an element.
     * 
     * @param element
     *            The element that will hold the attribute.
     * @param attribute
     *            The attribute to be inserted.
     */
    void insertAttribute(final N element, final N attribute);

    void insertAttributes(final N element, final Iterable<N> attributes);

    /**
     * Inserts a new child node before a specified reference node.
     * <p>
     * Insertion is not expected and not required to result in a normalized tree.
     * </p>
     * @param target
     *            The reference node before which the new node will be added.
     * @param content
     *            The new node.
     */
    void insertBefore(final N target, final N content);

    void insertBefore(final N target, final Iterable<N> content);

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
    void insertNamespace(final N element, final String prefix, final String uri);

    void prependChild(final N parent, final N content);

    void prependChildren(final N parent, final Iterable<N> content);

    /**
     * Replaces a node in the child axis of a parent node.
     * @param target
     *            The old node to be replaced.
     * @param content
     *            The new node that will replace the old node.
     * 
     * @return The old node that was removed.
     */
    N replace(final N target, final N content); //replace

    /**
     * @return the string that was replaced.
     **/
    String replaceValue(final N target, final String value);
}
