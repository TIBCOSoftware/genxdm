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
package org.genxdm.mutable;

import org.genxdm.Model;

/** Provides modification of the Model based on the XQuery Update Facility,
 * but with immediate effect.
 *
 **/
public interface MutableModel<N> extends Model<N>
{
    /**
     * Appends the specified child to the end of the child axis of the specified parent.
     * Corresponds to XQuery Update Facility insertIntoAsLast.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     * 
     * @param parent
     *            The parent to which the child should be added.  Must be a
     *            document or element.  May not be null.
     * @param content
     *            The child to be added to the parent.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void appendChild(final N parent, final N content);

    /**
     * Appends the specified children to the end of the child axis of the specified parent.
     * Corresponds to XQuery Update Facility insertIntoAsLast.
     * 
     * @param parent
     *            The parent to which the children should be added.  Must be a
     *            document or element.  May not be null.
     * @param content
     *            The children to be added to the parent.  May not be null,
     *            but may be an empty sequence.  Contents must conform to the
     *            contract for appendChild.
     **/
    void appendChildren(final N parent, final Iterable<N> content);

    /**
     * Create a copy (shallow or deep) of a source node.  No equivalent in
     * XQuery Update Facility.
     *
     * @param source The source node to be copied.
     * @param deep If true, and the source node is an element or document,
     *             then all of the source's contained nodes (children, attributes,
     *             namespaces) will also be copied.  If false, only the container
     *             will be copied.  There is no distinction in behavior for
     *             any other node type.
     * @return The copy.
     **/
    N copyNode(N source, boolean deep);

    /**
     * Removes a node from the tree.  Corresponds to XQuery Update Facility
     * delete.
     *
     * @param target
     *            The node to be removed.  If null, no action will be taken.
     *            If the node has no parent, no action will be taken.
     * 
     * @return The node that has been removed; if null, then no node was removed.
     **/
    N delete(final N target);

    /**
     * Removes all of the nodes in the child axis of a container node.  No
     * direct equivalent in XQuery Update Facility, but this method can be
     * used with appendChild (and a single text node) to reproduce the odd
     * replaceElementContent.
     *
     * @param target The target node.  Must be a container: a document or
     *               element node.  May not be null.
     * @return a sequence containing the nodes removed; it may be empty, but
     *         will not be null.
     **/
    Iterable<N> deleteChildren(final N target);

    /**
     * Provide a node factory compatible with the supplied context node.
     * No equivalent in XQuery Update facility.
     *
     * @param context The node with which the returned node factory must be
     *                compatible.
     * @return a node factory for the associated bridge, which must be
     *         compatible with the supplied node (if that's an issue for
     *         the bridge), never null.
     **/
    NodeFactory<N> getFactory(N context);

    /**
     * Inserts the specified node as the following sibling of the supplied
     * target node.
     * Corresponds to XQuery Update Facility insertAfter.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     * 
     * @param target
     *            The node which will become the preceding sibling of the new
     *            node.  Must be a text, element, comment, or processing
     *            instruction node.  May not be null.
     * @param content
     *            The node to be added.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void insertAfter(final N target, final N content);

    /**
     * Inserts the specified node sequence, in order, as the following siblings 
     * of the supplied target node.
     * Corresponds to XQuery Update Facility insertAfter.
     * 
     * @param target
     *            The node which will become the preceding sibling of the last
     *            node in the supplied sequence.  Must be a text, element, 
     *            comment, or processing instruction node.  May not be null.
     * @param content
     *            The sequence to be added.  May not be null,
     *            but may be an empty sequence (in which case nothing happens).
     *            Contents must conform to the contract for insertAfter.
     **/
    void insertAfter(final N target, final Iterable<N> content);

    /**
     * Inserts an attribute node into the attribute axis of an element.
     * Corresponds to XQuery Update Facility insertAttributes.  This is a
     * convenience method, permitting a single node rather than requiring
     * construction of a single-element sequence container.
     * 
     * @param element
     *            The element that will hold the attribute.  May not be null.
     *            Must be an element node.
     * @param attribute
     *            The attribute to be inserted.  May not be null.  Must be
     *            an attribute node.
     **/
    void insertAttribute(final N element, final N attribute);

    /**
     * Inserts an attribute node into the attribute axis of an element.
     * Corresponds to XQuery Update Facility insertAttributes.
     * 
     * @param element
     *            The element that will hold the attributes.  May not be null.
     *            Must be an element node.
     * @param attributes
     *            The list of attributes to be inserted.  May not be null.  
     *            May be empty.  Contents must be attribute nodes.
     **/
    void insertAttributes(final N element, final Iterable<N> attributes);

    /**
     * Inserts the specified node as the preceding sibling of the supplied
     * target node.
     * Corresponds to XQuery Update Facility insertBefore.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     * 
     * @param target
     *            The node which will become the following sibling of the new
     *            node.  Must be a text, element, comment, or processing
     *            instruction node.  May not be null.
     * @param content
     *            The node to be added.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void insertBefore(final N target, final N content);

    /**
     * Inserts the specified node sequence, in order, as the preceding siblings 
     * of the supplied target node.
     * Corresponds to XQuery Update Facility inserBefore.
     * 
     * @param target
     *            The node which will become the following sibling of the last
     *            node in the supplied sequence.  Must be a text, element, 
     *            comment, or processing instruction node.  May not be null.
     * @param content
     *            The sequence to be added.  May not be null,
     *            but may be an empty sequence (in which case nothing happens).
     *            Contents must conform to the contract for insertBefore.
     **/
    void insertBefore(final N target, final Iterable<N> content);

    // TODO: not currently included, because the rules for it are either
    // underspecified, or completely horrid for an immediate-effect API.
    /**
     * Inserts the specified node sequence, in order, somewhere in the target
     * node.  
     * Corresponds to XQuery Update Facility insertInto.
     * 
     * @param parent
     *            The node which will become the parent of the sequence.  Must 
     *            be an element or document node.  May not be null.
     * @param content
     *            The sequence to be added.  May not be null,
     *            but may be an empty sequence (in which case nothing happens).
     *            Contents must be text, element, comment and processing instruction
     *            nodes only.
     **/
//     void insertInto(final N parent, final Iterable<N> content);


    /**
     * Inserts a namespace binding into the namespace axis of an element.
     * This is an immediate-effect API equivalent to the delayed-effect
     * API specified in the XQuery Update Facility, propagateNamespace.
     * 
     * @param element
     *            The element that will hold the namespace binding.  Must be
     *            an element node.  May not be null.
     * @param prefix
     *            The prefix (local-name part of the dm:name) of the namespace node.
     *            May not be null, but may be the empty string (default prefix).
     * @param uri
     *            The dm:string-value of the namespace node (the namespace URI).
     *            May not be null, but may be the empty string (default/global namespace,
     *            or namespace un-definition for Namespaces in XML 1.1).
     **/
    N insertNamespace(final N element, final String prefix, final String uri);

    /**
     * Prepends the specified child to the beginning of the child axis of the specified parent.
     * Corresponds to XQuery Update Facility insertIntoAsFirst.  This version
     * is a convenience method that takes a single node rather than requiring
     * construction of a list to hold a single-element sequence.
     * 
     * @param parent
     *            The parent to which the child should be added.  Must be a
     *            document or element.  May not be null.
     * @param content
     *            The child to be added to the parent.  Must be a text, element,
     *            comment, or processing instruction node.  May not be null.
     **/
    void prependChild(final N parent, final N content);

    /**
     * Prepends the specified children to the beginning of the child axis of the 
     * specified parent, in order.
     * Corresponds to XQuery Update Facility insertIntoAsFirst.
     * 
     * @param parent
     *            The parent to which the children should be added.  Must be a
     *            document or element.  May not be null.
     * @param content
     *            The children to be added to the parent.  May not be null,
     *            but may be an empty sequence.  Contents must conform to the
     *            contract for prependChild.
     **/
    void prependChildren(final N parent, final Iterable<N> content);

    /**
     * Replaces a node.  Corresponds to XQuery Update Facility replaceNode
     * (except that it's a single node replacement, not a sequence).
     * 
     * Note that if Feature.NAMESPACE_AXIS is not supported, then neither
     * target nor content may be namespaces (the meaning of "replacing"
     * a namespace is arguable in any event).
     *
     * @param target
     *            The old node to be replaced.  May be any node type except
     *            document; must not be null.
     * @param content
     *            The new node that will replace the old node.  When the target
     *            node is an attribute, or namespace, this node must
     *            be of the same type.  When the target is element, text,
     *            comment, or processing instruction, this node must be one
     *            of those four kinds (not a document, attribute, or namespace).
     *            May not be null (use delete).
     * 
     * @return The node that was removed; if null, no action was taken.
     */
    N replace(final N target, final N content); //replace

    /**
     * Replaces the value of a node with a new value.  Corresponds to
     * XQuery Update Facility replaceValue.
     * 
     * Implementation note: it is permissible for unnamed nodes (text and
     * comments) to actually replace nodes, rather than values (implementations
     * not uncommonly treat text nodes as immutable, as their only identity
     * <em>is</em> their content+kind, and the same is true for comments).
     *
     * @param target The node for which the value is to be replaced.  Must
     *               be a text, attribute, comment, or processing instruction
     *               node.  May not be null.
     * @param value The replacement value.  If null, the empty string will
     *              be used instead.
     * @return the original textual value of the node. If null, then no
     *         replacement was possible.
     **/
    String replaceValue(final N target, final String value);
}
