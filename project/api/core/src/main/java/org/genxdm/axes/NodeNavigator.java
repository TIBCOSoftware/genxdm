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
package org.genxdm.axes;

/** Methods for acquiring a &lt;N>ode that stands in a particular relationship
 * to another (supplied) &lt;N>ode.
 *
 * @param <N> the node abstraction.
 */
public interface NodeNavigator<N> 
{

    /**
     * Returns the attribute node with the specified name.
     * 
     * @param node
     *            The node that is the parent of the attribute node.
     *            May not be null.
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     *            May not be null.
     * @param localName
     *            The local-name part of the attribute name.
     *            May not be null.
     * @return the attribute node with the specified name, if the supplied
     *         node is an element node and contains an attribute with that
     *         name, otherwise null.
     */
    N getAttribute(N node, String namespaceURI, String localName);
    
    /**
     * Return the element that has the specified ID
     * 
     * @param context   Any node from the document for which the ID 
     *                  lookup is being done; may not be null.
     * @param id        The id being searched for, may not be null.
     * 
     * @return the element node (in the same document as the context node)
     * that has the specified ID, or <code>null</code> if no such element exists.
     */
    N getElementById(N context, String id);

    /**
     * Returns the first child node of the node provided.
     * 
     * @param node
     *            The node for which the first child node is required;
     *            may not be null.
     * @return the first child node of the supplied origin node, if that
     *         origin is a container (document or element) and it actually
     *         contains children, otherwise null.
     */
    N getFirstChild(N origin);

    /**
     * Returns the first child element of the node provided.
     * 
     * @param node
     *            The node for which the first child element node is required;
     *            may not be null.
     * @return the first child element node of the supplied parent node, if
     *         that parent is a container (document or element) and it actually
     *         contains at least one child element, otherwise null.
     */
    N getFirstChildElement(N node);

    /**
     * Returns the first child element of the node provided, whose name matches the arguments supplied.
     * 
     * <p>Note that this is equivalent to getFirstChildElement when the second and
     * third arguments are both null.</p>
     * 
     * @param node
     *            The node for which the first child element node of specified
     *            name is required; may not be null.
     * @param namespaceURI
     *            The namespace-uri to be matched.  If <code>null</code>, will match any namespace.
     * @param localName
     *            The local-name to be matched.  If <code>null</code>, will match any local name.
     * @return the first child element with the specified name of the supplied
     *         parent node, if that parent is a container (document or element)
     *         and it actually contains at least one child element which has
     *         the specified name, otherwise null.
     */
    N getFirstChildElementByName(N node, String namespaceURI, String localName);

    /**
     * Returns the last child node of the node provided.
     * 
     * @param node
     *            The node for which the last child node is required;
     *            may not be null.
     * @return the last child of the supplied parent node, if that
     *         parent is a container (document or element) that actually
     *         contains at least one child node, otherwise null.
     */
    N getLastChild(N node);

    /**
     * Returns the next sibling node of the node provided.
     * 
     * @param node
     *            The node for which the next sibling node is required;
     *            may not be null.
     * @return the following sibling of the node provided, if the node
     *         provided is a child node (element, text, comment, or processing
     *         instruction) and actually has a following sibling, otherwise null.
     */
    N getNextSibling(N node);

    /**
     * Returns the next sibling element node of the node provided.
     * 
     * @param node
     *            The node for which the next element sibling node is required;
     *            may not be null.
     * @return the next element sibling of the node provided, if the node provided
     *         is a child node (element, text, comment, or processing instruction)
     *         and actually has an element node in its following-sibling axis,
     *         otherwise null.
     */
    N getNextSiblingElement(N node);

    /**
     * Returns the next element along the following-sibling axis whose name matches the arguments supplied.
     * 
     * @param node
     *            The node for which the next element sibling node is required;
     *            may not be null.
     * @param namespaceURI
     *            The namespace-uri to be matched; if null, will match any namespace.
     * @param localName
     *            The local-name to be matched; if null, will match any name.
     * @return the next element sibling which matches the supplied namespace and
     *         name of the node provided, if the node provided is a child node
     *         (element, text, comment, or processing instruction) and actually
     *         has an element node with the specified name in its following-sibling
     *         axis, otherwise null.
     */
    N getNextSiblingElementByName(N node, String namespaceURI, String localName);

    /**
     * Returns the parent node of the node provided.
     * <p>May return <code>null</code> for top-most or orphaned nodes.</p>
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-parent">
     * dm:parent</a> accessor in the XDM.</p>
     * 
     * <p>Note that it may not return null for attribute or namespace nodes unless
     * those nodes are <em>orphaned</em>, regardless of whether the tree model
     * underlying the bridge uses the term 'parent' for the elements which contain
     * these node types.</p>
     * 
     * @param node
     *            The node for which the parent is required; may not be null.
     * @return the parent of the specified node, if it has one; otherwise null.
     *         A document node <em>must</em> return null.            
     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-parent
     */
    N getParent(N origin);

    /**
     * Returns the previous sibling node of the node provided.
     * 
     * @param node
     *            The node for which the previous sibling node is required;
     *            may not be null.
     * @return the previous sibling of the node supplied, if the supplied node
     *         is a child (element, text, comment, or processing instruction)
     *         node and actually has a previous sibling, otherwise null.
     */
    N getPreviousSibling(N node);

    /**
     * Returns the top-most node along the ancestor-or-self axis from this node.
     * 
     * <p>Note that this may be conceptually implemented as a recursive getParent(),
     * returning the last non-null result.</p>
     * 
     * @param node
     *            The node from which to begin the search for the top-most node;
     *            may not be null.
     * @return the ultimate root container for this node, or the node itself;
     *         never null.
     */
    N getRoot(N node);

}
