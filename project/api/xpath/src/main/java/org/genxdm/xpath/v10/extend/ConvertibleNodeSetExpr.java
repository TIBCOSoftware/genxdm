/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.xpath.v10.extend;

import org.genxdm.xpath.v10.NodeSetExpr;

public interface ConvertibleNodeSetExpr extends NodeSetExpr, ConvertibleExpr {

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

	public ConvertibleNodeSetExpr compose(final ConvertibleNodeSetExpr expr);
}
