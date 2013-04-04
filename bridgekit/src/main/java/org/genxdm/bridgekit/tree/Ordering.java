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
package org.genxdm.bridgekit.tree;

import org.genxdm.Model;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.TraversingInformer;

/**
 * Generic utility to calculate the relative order of nodes.
 */
public final class Ordering
{
    private Ordering()
    {
    }

    public static final int BEFORE = -1;
    public static final int EQUAL = 0;
    private static final int AFTER = +1;

    public static <N> int compareNodes(final N lhsNode, final N rhsNode, final Model<N> model)
    {
        if (isSameNode(lhsNode, rhsNode, model))
        {
            return EQUAL;
        }

        final int depthLhs = getDepth(lhsNode, model);
        final int depthRhs = getDepth(rhsNode, model);

        final int depthOfCommonAncestorOrSelf = getCommonAncestorOrSelfDepth(lhsNode, depthLhs, rhsNode, depthRhs, model);

        if (-1 == depthOfCommonAncestorOrSelf)
        {
            // We provide a default implementation here, but we should be
            // dealing with disparate trees in the host.
            final int hL = getRoot(lhsNode, model).hashCode();
            final int hR = getRoot(rhsNode, model).hashCode();

            if (hL > hR)
            {
                return AFTER;
            }
            else if (hL < hR)
            {
                return BEFORE;
            }
            else
            {
                // Can't get here if the nodes belong to different trees.
                throw new RuntimeException();
            }
        }
        else
        {
            // Handle cases where one of the nodes is the common
            // ancestor-or-self.
            if (depthLhs == depthOfCommonAncestorOrSelf)
            {
                if (depthRhs == depthOfCommonAncestorOrSelf)
                {
                    return EQUAL;
                }
                else
                {
                    assert (depthRhs > depthOfCommonAncestorOrSelf);
                    return BEFORE;
                }
            }
            else if (depthRhs == depthOfCommonAncestorOrSelf)
            {
                assert (depthLhs > depthOfCommonAncestorOrSelf);
                return AFTER;
            }
            else
            {
                // The nodes have a common ancestor but it is not one of them.
                // Identify proxies for these nodes that share the same parent,
                // then find the relationship between the two.

                // Calculate the proxy nodes for n1 and n2. These are the nodes
                // up the ancestor axis that are parented by the common
                // ancestor.
                N lhsProxy = lhsNode;

                for (int i = depthLhs - (depthOfCommonAncestorOrSelf + 1); i > 0; i--)
                {
                    lhsProxy = model.getParent(lhsProxy);
                }

                N rhsProxy = rhsNode;

                for (int i = depthRhs - (depthOfCommonAncestorOrSelf + 1); i > 0; i--)
                {
                    rhsProxy = model.getParent(rhsProxy);
                }

                return compareKindred(lhsProxy, rhsProxy, model);
            }
        }
    }

    /**
     * Determines the depth of a node in a tree. <br/>
     * The topmost node, the root, has depth zero. Namespaces, attributes and children have depth one greater than their
     * parent. <br/>
     * This method is intentionally package protected to allow JUnit testing.
     * 
     * @param node
     *            the node.
     */
    private static <N> int getDepth(final N node, final Model<N> model)
    {
        int level = 0;

        N currentNode = node;
        while (true)
        {
            currentNode = model.getParent(currentNode);
            if (null != currentNode)
            {
                level++;
            }
            else
            {
                break;
            }
        }

        return level;
    }

    /**
     * Determines the depth of the ancestor-or-self that is common to two nodes. <br/>
     * This method is intentionally package protected to allow JUnit testing.
     * 
     * @param node1
     *            The first node.
     * @param d1
     *            The depth of the first node.
     * @param node2
     *            The second node.
     * @param d2
     *            The depth of the second node.
     * @param model
     * @return The common depth or <code>-1</code> if the nodes do not belong to the same tree.
     */
    private static <N> int getCommonAncestorOrSelfDepth(final N node1, final int d1, final N node2, final int d2, final Model<N> model)
    {
        // Calculate the minimum of the two levels.
        final int minLR = Math.min(d1, d2);

        // For each node, identify a node along the ancestor axis which is at
        // the minimum level.
        N a1 = node1;

        for (int i = d1; i > minLR; i--)
        {
            a1 = model.getParent(a1);
        }

        N a2 = node2;

        for (int i = d2; i > minLR; i--)
        {
            a2 = model.getParent(a2);
        }

        int commonLevel = minLR;

        // Move both nodes up towards the root to find the common ancestor.
        while (!isSameNode(a1, a2, model))
        {
            a1 = model.getParent(a1);
            a2 = model.getParent(a2);

            if ((a1 != null) && (a2 != null))
            {
                commonLevel = commonLevel - 1;
            }
            else
            {
                return -1;
            }
        }

        return commonLevel;
    }

    /**
     * Compares two "kindred" nodes for document order. Kindred nodes, are nodes that are at the same level, with the
     * same parent, but may be different node kinds and therefore on different axes.
     * 
     * @param lhsNode
     *            The first kindred node.
     * @param rhsNode
     *            The second kindred node.
     * @param model
     */
    private static <N> int compareKindred(final N lhsNode, final N rhsNode, final Model<N> model)
    {
        if (model.isNamespace(lhsNode))
        {
            if (model.isNamespace(rhsNode))
            {
                // It's not meaningful to put two namespace nodes into order
                // without some algorithm such as canonicalization.
                // Return a value that would make ordering stable.
                // Note that attempting to compare siblings won't work beacuse
                // the preceding-sibling and following-sibling axis is empty
                // for namespace nodes.
                return EQUAL;
            }
            else
            {
                // Namepaces come before attributes and anything on the child
                // axis.
                return BEFORE;
            }
        }
        else if (model.isAttribute(lhsNode))
        {
            if (model.isAttribute(rhsNode))
            {
                // It's not meaningful to put two attribute nodes into order
                // without some algorithm such as canonicalization.
                // Return a value that would make ordering stable.
                // Note that attempting to compare siblings won't work beacuse
                // the preceding-sibling and following-sibling axis is empty
                // for attribute nodes.
                return EQUAL;
            }
            else if (model.isNamespace(rhsNode))
            {
                // Attributes come after namespaces.
                return AFTER;
            }
            else
            {
                // Attributes come before anything on the child axis.
                return BEFORE;
            }
        }
        else
        {
            if (model.isAttribute(rhsNode) || model.isNamespace(rhsNode))
            {
                // child axis things always come after namespaces and
                // attributes.
                return AFTER;
            }
            else
            {
                // Compare siblings because both are on the child axis.
                return compareSiblings(lhsNode, rhsNode, model);
            }
        }
    }

    /**
     * Compares two sibling nodes, meaning they are on the same namespace, attribute or child axis, for document order.
     * 
     * @param n1
     *            The first sibling node.
     * @param n2
     *            The second sibling node.
     */
    private static <N> int compareSiblings(final N n1, final N n2, final Model<N> model)
    {
        {
            N current = model.getNextSibling(n1);
            while (null != current)
            {
                if (isSameNode(current, n2, model))
                {
                    return BEFORE;
                }
                current = model.getNextSibling(current);
            }
        }

        {
            N current = model.getPreviousSibling(n1);
            while (null != current)
            {
                if (isSameNode(current, n2, model))
                {
                    return AFTER;
                }
                current = model.getPreviousSibling(current);
            }
        }

        return EQUAL;
    }

    public static <N> boolean isSameNode(final N one, final N two, final Model<N> navigator)
    {
        PreCondition.assertNotNull(one, "first");
        PreCondition.assertNotNull(two, "second");
        if (one == two)
            return true;
        if (navigator != null)
        {
            Object first = navigator.getNodeId(one);
            Object second = navigator.getNodeId(two);
            if (first != null)
                return first.equals(second);
            else
                return second == null;
        }
        return false; // generally only if model is null
    }

    public static boolean isSameNode(TraversingInformer one, TraversingInformer two)
    {
        PreCondition.assertNotNull(one, "first");
        PreCondition.assertNotNull(two, "second");
        if (one == two)
            return true;
        Object first = one.getNodeId();
        Object second = two.getNodeId();
        return first.equals(second);
    }

    private static <N> N getRoot(final N node, final Model<N> core)
    {
        final N parent = core.getParent(node);
        if (null != parent)
        {
            return getRoot(node, core);
        }
        else
        {
            return node;
        }
    }
}
