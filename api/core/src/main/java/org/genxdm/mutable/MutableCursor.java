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
package org.genxdm.mutable;

import org.genxdm.Cursor;

public interface MutableCursor<N>
    extends Cursor<N>
{

    /**
     * Appends the specified child to the end of the child axis of the current parent.
     * Corresponds to XQuery Update Facility insertIntoAsLast.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     *
     * The cursor must be positioned on a document or element node.
     * 
     * @param content
     *            The child to be added to the parent.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void appendChild(final N content);

    /**
     * Appends the specified children to the end of the child axis of the current parent.
     * Corresponds to XQuery Update Facility insertIntoAsLast.
     * 
     * The cursor must be positioned on a document or element node.
     * 
     * @param content
     *            The children to be added to the parent.  May not be null,
     *            but may be an empty sequence.  Contents must conform to the
     *            contract for appendChild.
     **/
    void appendChildren(final Iterable<N> content);

    /**
     * Removes a node from the tree.  Corresponds to XQuery Update Facility
     * delete.
     *
     * @return The node that has been removed; if null, then no node was removed.
     * The cursor should reposition itself such that it is set to move to the
     * next node in document order.
     **/
    N delete();

    /**
     * Removes all of the nodes in the child axis of a container node.  No
     * direct equivalent in XQuery Update Facility, but this method can be
     * used with appendChild (and a single text node) to reproduce the odd
     * replaceElementContent.
     * 
     * The cursor must be positioned on a document or element node.
     *
     * @return a sequence containing the nodes removed; it may be empty, but
     *         will not be null.
     **/
    Iterable<N> deleteChildren();

    /**
     * Provide a node factory compatible with the current context node.
     * No equivalent in XQuery Update facility.
     *
     * @return a node factory for the associated bridge, which must be
     *         compatible with the context node (if that's an issue for
     *         the bridge), never null.
     **/
    NodeFactory<N> getFactory();

    /**
     * Inserts the specified node as the following sibling of the
     * context node.
     * Corresponds to XQuery Update Facility insertAfter.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     * 
     * The context node must be a text, element, comment, or processing instruction node.
     * 
     * @param content
     *            The node to be added.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void insertAfter(final N next);

    /**
     * Inserts the specified node sequence, in order, as the following siblings 
     * of the context node.
     * Corresponds to XQuery Update Facility insertAfter.
     * 
     * The context node must be a text, element, comment, or processing
     * instruction node.
     * 
     * @param content
     *            The sequence to be added.  May not be null,
     *            but may be an empty sequence (in which case nothing happens).
     *            Contents must conform to the contract for insertAfter.
     **/
    void insertAfter(final Iterable<N> content);

    /**
     * Inserts an attribute node into the attribute axis of an element.
     * Corresponds to XQuery Update Facility insertAttributes.  This is a
     * convenience method, permitting a single node rather than requiring
     * construction of a single-element sequence container.
     *
     * The context node must be an element.
     * 
     * @param attribute
     *            The attribute to be inserted.  May not be null.  Must be
     *            an attribute node.
     **/
    void insertAttribute(final N attribute);

    /**
     * Inserts an attribute node into the attribute axis of an element.
     * Corresponds to XQuery Update Facility insertAttributes.
     * 
     * The context node must be an element.
     * 
     * @param attributes
     *            The list of attributes to be inserted.  May not be null.  
     *            May be empty.  Contents must be attribute nodes.
     **/
    void insertAttributes(final Iterable<N> attributes);

    /**
     * Inserts the specified node as the preceding sibling of the context
     * node.
     * Corresponds to XQuery Update Facility insertBefore.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     * 
     * The context node must be a text, element, comment, or processing
     * instruction node.
     * 
     * @param content
     *            The node to be added.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void insertBefore(final N content);

    /**
     * Inserts the specified node sequence, in order, as the preceding siblings 
     * of the context node.
     * Corresponds to XQuery Update Facility inserBefore.
     * 
     * The context node must be a text, element, comment, or processing
     * instruction node.
     * 
     * @param content
     *            The sequence to be added.  May not be null,
     *            but may be an empty sequence (in which case nothing happens).
     *            Contents must conform to the contract for insertBefore.
     **/
    void insertBefore(final Iterable<N> content);

    /**
     * Inserts a namespace binding into the namespace axis of an element.
     * This is an immediate-effect API equivalent to the delayed-effect
     * API specified in the XQuery Update Facility, propagateNamespace.
     *
     * The context node must be an element.
     * 
     * @param prefix
     *            The prefix (local-name part of the dm:name) of the namespace node.
     *            May not be null, but may be the empty string (default prefix).
     * @param uri
     *            The dm:string-value of the namespace node (the namespace URI).
     *            May not be null, but may be the empty string (default/global namespace,
     *            or namespace un-definition for Namespaces in XML 1.1).
     **/
    void insertNamespace(final String prefix, final String uri);

    /**
     * Prepends the specified child to the beginning of the child axis of the current parent.
     * Corresponds to XQuery Update Facility insertIntoAsFirst.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     *
     * The context node must be a document or element.
     * 
     * @param content
     *            The child to be added to the parent.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void prependChild(final N newChild);

    /**
     * Prepends the specified children to the beginning of the child axis of the 
     * current parent, in order.
     * Corresponds to XQuery Update Facility insertIntoAsFirst.
     * 
     * The context node must be a document or element.
     * 
     * @param content
     *            The children to be added to the parent.  May not be null,
     *            but may be an empty sequence.  Contents must conform to the
     *            contract for prependChild.
     **/
    void prependChildren(final Iterable<N> content);

    /**
     * Replaces the context node.  Corresponds to XQuery Update Facility replaceNode
     * (except that it's a single node replacement, not a sequence).
     *
     * The context node may not be a document.
     * 
     * @param content
     *            The new node that will replace the old node.  When the context
     *            node is an attribute or namespace, this node must
     *            be of the same type.  When the target is element, text,
     *            comment, or processing instruction, this node must be one
     *            of those four kinds (not a document, attribute, or namespace).
     *            May not be null (use delete).
     * 
     * @return The node that was removed; if null, no action was taken.
     */
    N replace(final N content);

    /**
     * Replaces the value of a node with a new value.  Corresponds to
     * XQuery Update Facility replaceValue.
     *
     * The context node must be a text, attribute, comment, or processing
     * instruction node.
     * 
     * @param value The replacement value.  If null, the empty string will
     *              be used instead.
     * @return the original textual value of the node. If null, then no
     *         replacement was possible.
     **/
    String replaceValue(final String value);

}
