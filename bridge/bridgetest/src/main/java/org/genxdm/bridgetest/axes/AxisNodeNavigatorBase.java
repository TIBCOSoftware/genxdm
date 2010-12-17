package org.genxdm.bridgetest.axes;

import org.genxdm.bridgetest.TestBase;

public abstract class AxisNodeNavigatorBase<N>
    extends TestBase<N>
{

//    /**
//     * Returns the nodes along the ancestor axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getAncestorAxis(N node);
//
//    /**
//     * Returns the nodes along the ancestor-or-self axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getAncestorOrSelfAxis(N node);
//
//    /**
//     * Returns the nodes along the attribute axis using the specified node as the origin.
//     * 
//     * <br/>
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-attributes">
//     * dm:attributes</a> accessor in the XDM.
//     * 
//     * @param node
//     *            The origin node.
//     * @param inherit
//     *            Determines whether attributes in the XML namespace will be inherited. The standard value for this
//     *            parameter is <code>false</code>.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-attributes
//     */
//    Iterable<N> getAttributeAxis(N node, boolean inherit);
//
//    /**
//     * Returns the nodes along the child axis using the specified node as the origin.
//     * 
//     * <br/>
//     * 
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-children">
//     * dm:children</a> accessor in the XDM.
//     * 
//     * @param node
//     *            The origin node.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-children
//     */
//    Iterable<N> getChildAxis(N node);
//
//    /**
//     * Returns all the child element along the child axis.
//     * 
//     * @param node
//     *            The parent node that owns the child axis.
//     */
//    Iterable<N> getChildElements(N node);
//
//    /**
//     * Returns all the child element along the child axis whose names match the arguments supplied.
//     * 
//     * @param node
//     *            The parent node that owns the child axis.
//     * @param namespaceURI
//     *            The namespace-uri to be matched.
//     * @param localName
//     *            The local-name to be matched.
//     */
//    Iterable<N> getChildElementsByName(N node, String namespaceURI, String localName);
//
//    /**
//     * Returns the nodes along the descendant axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getDescendantAxis(N node);
//
//    /**
//     * Returns the nodes along the descendant-or-self axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getDescendantOrSelfAxis(N node);
//
//    /**
//     * Returns the nodes along the following axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getFollowingAxis(N node);
//
//    /**
//     * Returns the nodes along the following-sibling axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getFollowingSiblingAxis(N node);
//
//    /**
//     * Returns the nodes along the namespace axis using the specified node as the origin.
//     * 
//     * <p>
//     * The namespace axis contains the namespace nodes of the context node; the axis will be empty unless the context
//     * node is an element.
//     * </p>
//     * 
//     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-nodes">
//     * dm:namespace-nodes</a> of XDM.</p>
//     * 
//     * @param node
//     *            The origin node.
//     * @param inherit
//     *            Determines whether in-scope prefix mappings will be included in the result. The standard setting for
//     *            this parameter is <code>true</code>.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-nodes
//     */
//    Iterable<N> getNamespaceAxis(N node, boolean inherit);
//
//    /**
//     * Returns the nodes along the preceding axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getPrecedingAxis(N node);
//
//    /**
//     * Returns the nodes along the preceding-sibling axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getPrecedingSiblingAxis(N node);
}
