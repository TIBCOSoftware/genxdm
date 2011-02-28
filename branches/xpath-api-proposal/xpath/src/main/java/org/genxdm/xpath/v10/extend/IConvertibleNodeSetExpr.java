package org.genxdm.xpath.v10.extend;

import org.genxdm.xpath.v10.NodeSetExpr;

public interface IConvertibleNodeSetExpr extends NodeSetExpr, IConvertibleExpr {

	/**
	 * If this is set, then all nodes in the result of eval(x, c) are guaranteed to be in the subtree rooted at x.
	 */
	public static final int STAYS_IN_SUBTREE = 01;

	/**
	 * If this is set, then all nodes in the result of eval(x, c) are guaranteed to be at the same level of the tree. More precisely, define the level of a node to be the number of ancestors it has, and then define an expression to be single-level if and only if there
	 * exists an integer n such that for any node x, for any node y in the result of evaluating the expression with respect to x, the difference between the level of x and the level of y is equal to n. For example, the children axis is single-level but the descendants
	 * axis is not.
	 */
	public static final int SINGLE_LEVEL = 02;

	public int getOptimizeFlags();

	public IConvertibleNodeSetExpr compose(final IConvertibleNodeSetExpr expr);
}
