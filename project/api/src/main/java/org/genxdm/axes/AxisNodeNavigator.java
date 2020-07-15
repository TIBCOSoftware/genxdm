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
package org.genxdm.axes;

/**
 * Provides the various "axes" by which nodes can be traversed, as defined by
 * XPath 2.0.
 *  
 * 
<!-- cut and paste from the spec, with some ellipses -->
<ul>
<li>The child axis contains the children of the context node, which are the nodes returned by 
the dm:children accessor in the XDM.</li>

<li>the descendant axis is defined as the transitive closure of the child axis; 
it contains the descendants of the context node (the children, 
the children of the children, and so on)</li>

<li>the parent axis contains the sequence returned by the dm:parent accessor in 
the XDM, 
which returns the parent of the context node, or an empty sequence if the 
context node has no parent</li>

<li>the ancestor axis is defined as the transitive closure of the parent axis; 
it contains the ancestors of the context node (the parent, the parent of the parent, 
and so on)</li>

<li>the following-sibling axis contains the context node's following siblings, 
those children of the context node's parent that occur after the context node in 
document order; if the context node is an attribute or namespace node, 
the following-sibling axis is empty</li>

<li>the preceding-sibling axis contains the context node's preceding siblings, 
those children of the context node's parent that occur before the context node 
in document order; if the context node is an attribute or namespace node, the 
preceding-sibling axis is empty</li>

<li>the following axis contains all nodes that are descendants of the root of the 
tree in which the context node is found, are not descendants of the context node, 
and occur after the context node in document order</li>

<li>the preceding axis contains all nodes that are descendants of the root of the tree 
in which the context node is found, are not ancestors of the context node, 
and occur before the context node in document order</li>

<li>the attribute axis contains the attributes of the context node, which are the 
nodes returned by the dm:attributes accessor in 
the XDM; the axis will be empty 
unless the context node is an element</li>

<li>the self axis contains just the context node itself</li>

<li>the descendant-or-self axis contains the context node and the descendants of the 
context node</li>

<li>the ancestor-or-self axis contains the context node and the ancestors of the 
context node; thus, the ancestor-or-self axis will always include the root node</li>

<li>the namespace axis contains the namespace nodes of the context node, which are the 
nodes returned by the dm:namespace-nodes accessor in 
the XDM; this axis is empty 
unless the context node is an element node.</li>

</ul>

 * @param <N> the 'node' abstraction.
 * @see <a href="http://www.w3.org/TR/xpath20/#axes">XDM axes</a>
 */
public interface AxisNodeNavigator<N>
{
    /**
     * Returns the nodes along the ancestor axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getAncestorAxis(final N node);

    /**
     * Returns the nodes along the ancestor-or-self axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which must contain at least one node; never null.
     */
    Iterable<N> getAncestorOrSelfAxis(final N node);

    /**
     * Returns the nodes along the attribute axis using the specified node as the origin.
     * 
     * <br/>
     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-attributes">
     * dm:attributes</a> accessor in the XDM.
     * 
     * @param node
     *            The origin node; may not be null.
     * @param inherit
     *            Determines whether attributes in the XML namespace will be inherited. The standard value for this
     *            parameter is <code>false</code>.
     * 
     * @return an iterable, which may be empty; never null.
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-attributes">XDM dm:attributes accessor</a>
     */
    Iterable<N> getAttributeAxis(final N node, final boolean inherit);

    /**
     * Returns the nodes along the child axis using the specified node as the origin.
     * 
     * <br/>
     * 
     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-children">
     * dm:children</a> accessor in the XDM.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-children">XDM dm:children accessor</a>
     */
    Iterable<N> getChildAxis(final N node);

    /**
     * Returns all the child element along the child axis.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getChildElements(final N node);

    /**
     * Returns all the child element along the child axis whose names match the arguments supplied.
     * 
     * @param node
     *            The origin node; may not be null.
     * @param namespaceURI
     *            The namespace-uri to be matched.  If <code>null</code>, will match any namespace.
     * @param localName
     *            The local-name to be matched.  If <code>null</code>, will match any local name.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getChildElementsByName(final N node, final String namespaceURI, final String localName);

    /**
     * Returns the nodes along the descendant axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getDescendantAxis(final N node);

    /**
     * Returns the nodes along the descendant-or-self axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which must contain at least one node; never null.
     */
    Iterable<N> getDescendantOrSelfAxis(final N node);

    /**
     * Returns the nodes along the following axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getFollowingAxis(final N node);

    /**
     * Returns the nodes along the following-sibling axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getFollowingSiblingAxis(final N node);

    /**
     * Returns the nodes along the namespace axis using the specified node as the origin.
     * 
     * <p>
     * The namespace axis contains the namespace nodes of the context node; the axis will be empty unless the context
     * node is an element.
     * </p>
     * 
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-nodes">
     * dm:namespace-nodes</a> of XDM.</p>
     * 
     * @param node
     *            The origin node; may not be null.
     * @param inherit
     *            Determines whether in-scope prefix mappings will be included in the result. The standard setting for
     *            this parameter is <code>true</code>.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-nodes">XDM dm:namespace-nodes accessor</a>
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getNamespaceAxis(final N node, final boolean inherit);

    /**
     * Returns the nodes along the preceding axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getPrecedingAxis(final N node);

    /**
     * Returns the nodes along the preceding-sibling axis using the specified node as the origin.
     * 
     * @param node
     *            The origin node; may not be null.
     * @return an iterable, which may be empty; never null.
     */
    Iterable<N> getPrecedingSiblingAxis(final N node);
}
