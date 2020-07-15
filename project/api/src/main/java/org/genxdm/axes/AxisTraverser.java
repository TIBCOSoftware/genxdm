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

import org.genxdm.nodes.Traverser;

/**
 * Provides the various "axes" by which nodes can be traversed, as defined by
 * XPath 2.0.
 *
 * <p>Differs from {@link AxisNodeNavigator} in that it presumes positional
 * state; in this way it corresponds to AxisNodeNavigator as {@link Navigator}
 * corresponds to {@link NodeNavigator} and {@link Informer} corresponds to
 * {@link org.genxdm.nodes.NodeInformer}, or more generally, as {@link org.genxdm.Cursor} corresponds to
 * {@link org.genxdm.Model}.</p> 
 *
 * @see <a href="http://www.w3.org/TR/xpath20/#axes">XPath 2.0 - Axes </a>
 */
public interface AxisTraverser
{
    /**
     * Returns the nodes along the ancestor axis using this node as the origin.
     * 
     * @return A Traverser for the axis.
     */
    Traverser traverseAncestorAxis();

    /**
     * Returns the nodes along the ancestor-or-self axis using this node as the origin.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traverseAncestorOrSelfAxis();

    /**
     * Returns the nodes along the attribute axis using this node as the origin.
     * 
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-attributes">
     * dm:attributes</a> accessor in the XDM.</p>
     * 
     * @param inherit
     *            Determines whether attributes in the XML namespace will be inherited. The standard value for this
     *            parameter is <code>false</code>.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-attributes">XDM attributes accessor</a>
     * @return  A Traverser for the axis.
     */
    Traverser traverseAttributeAxis(boolean inherit);

    /**
     * Returns the nodes along the child axis using this node as the origin.
     *  
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-children">
     * dm:children</a> accessor in the XDM.</p>
     * 
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-children">XDM dm:children accessor</a>
     * @return  A Traverser for the axis.
     */
    Traverser traverseChildAxis();

    /**
     * Returns all the elements along the child axis.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traverseChildElements();

    /**
     * Returns all the elements along the child axis whose names match the arguments supplied.
     * 
     * @param namespaceURI
     *            The namespace-uri to be matched.
     * @param localName
     *            The local-name to be matched.
     * @return  A Traverser for the axis.
     */
    Traverser traverseChildElementsByName(String namespaceURI, String localName);

    /**
     * Returns the nodes along the descendant axis using this node as the origin.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traverseDescendantAxis();

    /**
     * Returns the nodes along the descendant-or-self axis using this node as the origin.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traverseDescendantOrSelfAxis();

    /**
     * Returns the nodes along the following axis using this node as the origin.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traverseFollowingAxis();

    /**
     * Returns the nodes along the following-sibling axis using this node as the origin.
     * 
     * @return A Traverser for the axis.
     */
    Traverser traverseFollowingSiblingAxis();

    /**
     * Returns the nodes along the namespace axis using this node as the origin.
     * 
     * <p>The namespace axis contains the namespace nodes of the context node; the axis will be empty unless the context
     * node is an element.</p>
     * 
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-nodes">
     * dm:namespace-nodes</a> of XDM.</p>
     * 
     * @param inherit
     *            Determines whether in-scope prefix mappings will be included in the result. The standard setting for
     *            this parameter is <code>true</code>.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-nodes">XDM dm:namespace-nodes</a>
     * @return  A Traverser for the axis.
     */
    Traverser traverseNamespaceAxis(boolean inherit);

    /**
     * Returns the nodes along the preceding axis using this node as the origin.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traversePrecedingAxis();

    /**
     * Returns the nodes along the preceding-sibling axis using this node as the origin.
     * 
     * @return  A Traverser for the axis.
     */
    Traverser traversePrecedingSiblingAxis();
}
