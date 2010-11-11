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

    /**
     * Adopts a node from an external document. The source node (and its subtree) is removed from the document it is in
     * (if any), and its owner document is changed to the target document. The source node can then be inserted into the
     * target document.
     * 
     * @param source
     *            The source node.
     */
    N adoptNode(final N source);

    /**
     * Appends the specified child to the end of the child axis of the specified parent.
     * 
     * @param newChild
     *            The child to be added to the parent.
     * @return The child added to the parent.
     */
    void appendChild(final N newChild);

    /**
     * Copies a node to a specified depth.
     * <p>
     * The resulting node has the same owner as the source node.
     * </p>
     * 
     * @param deep
     *            Determines whether to include the children of the copied node.
     * @return A copy of the source node.
     */
    N clone(final boolean deep);

    /**
     * Returns the owner document node of the specified node.
     * 
     * @return The owner document, never <code>null</code>.
     */
    N getOwner();

    /**
     * Normalizes the specified node and all of its descendants.
     * <p>
     * Normalization coalesces adjacent text nodes into a single node and removes any text nodes that have
     * dm:string-value equivalent to a zero-length string.
     * </p>
     * 
     */
    void normalize();

    /**
     * Creates a copy of a source node from an external document that can be inserted into the target document.
     * 
     * @param source
     *            The node from another document to be imported.
     * @param deep
     *            indicates whether the children of the node should be imported.
     * @return The new node that is imported into the target document.
     */
    N importNode(final N source, final boolean deep);

    /**
     * Inserts a new child node before a specified reference node in the child axis of a parent node.
     * <p>
     * Insertion is not expected and not required to result in a normalized tree.
     * </p>
     * 
     * @param newChild
     *            The new child to be added to the parent.
     * @param refChild
     *            The reference child before which the new node will be added. If no reference child is specified then
     *            the new child is appended to the children of the parent node.
     * @return The node that was inserted.
     */
    N insertBefore(final N newChild, final N refChild);

    /**
     * Removes an attribute from an element by specifying the name of the attribute.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     */
    void removeAttribute(final String namespaceURI, final String localName);

    /**
     * Removes a node from the child axis of the parent node.
     * 
     * @param oldChild
     *            The child to be removed.
     * @return The child that has been removed.
     */
    N removeChild(final N oldChild);

    /**
     * Removes a namespace from an element by specifying the prefix (dm:local-name) of the namespace.
     * 
     * @param prefix
     *            The prefix (local-name part of dm:name) of the namespace node.
     */
    void removeNamespace(final String prefix);

    /**
     * Replaces a node in the child axis of a parent node.
     * 
     * @param newChild
     *            The new node that will replace the old node.
     * @param oldChild
     *            The old node to be replaced.
     * @return The old node that was removed.
     */
    N replaceChild(final N newChild, final N oldChild);

    /**
     * Sets an attribute node into the attribute axis of an element.
     * 
     * @param attribute
     *            The attribute to be inserted.
     */
    void setAttribute(final N attribute);

    /**
     * Sets an attribute into the attribute axis of an element.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the dm:name of the attribute.
     * @param localName
     *            The local-name part of the dm:name of the attribute.
     * @param prefix
     *            The prefix part of the dm:name of the attribute.
     * @param value
     *            The value of the attribute.
     * @return The new attribute node.
     */
    N setAttribute(final String namespaceURI, final String localName, final String prefix, final String value);

    /**
     * Sets a namespace node into the namespace axis of an element.
     * 
     * @param namespace
     *            The namespace node to be added.
     */
    void setNamespace(final N namespace);

    /**
     * Sets a namespace binding into the namespace axis of an element.
     * 
     * @param prefixString
     *            The prefix (local-name part of the dm:name) of the namespace node as a <code>String</code>.
     * @param uriSymbol
     *            The dm:string-value of the namespace node as a symbol.
     */
    void setNamespace(final String prefixString, final String uriSymbol);
}
