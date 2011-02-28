/**
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.xpath.v10.expressions;

import org.genxdm.Model;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.variants.NodeSetVariant;
import org.genxdm.xpath.v10.variants.Variant;

/**
 * A compiled XPath pattern component which returns a Node set, but is convertible (castable) to a String expression, boolean expression or VariantExpression convertible (castable) to a String expression, boolean expression or VariantExpression convertible (castable) to
 * a String expression, boolean expression or VariantExpression convertible (castable) to a String expression, boolean expression or VariantExpression
 */
public abstract class ConvertibleNodeSetExpr 
    extends ConvertibleExpr
    implements NodeSetExpr
{

	public ConvertibleStringExpr makeStringExpr(ExprContextStatic statEnv)
	{
		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return Converter.toString(ConvertibleNodeSetExpr.this.nodeIterator(model, node, dynEnv), model);
			}
		};
	}

	/**
     *
     */
	public ConvertibleBooleanExpr makeBooleanExpr(ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr( )
		{
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return Converter.toBoolean(ConvertibleNodeSetExpr.this.nodeIterator(model, node, dynEnv));
			}
		};
	}

	/**
     *
     */
	@Override
	public ConvertibleNodeSetExpr makeNodeSetExpr(ExprContextStatic statEnv)
	{
		return this;
	}

	/**
     *
     */
	public ConvertibleVariantExpr makeVariantExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleVariantExpr()
		{
			public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return new NodeSetVariant<N>(ConvertibleNodeSetExpr.this.nodeIterator(model, contextNode, dynEnv), model);
			}
		};
	}

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

	public int getOptimizeFlags()
	{
		return 0;
	}

	/**
	 * Return an expression for this/expr
	 */
	public ConvertibleNodeSetExpr compose(final ConvertibleNodeSetExpr expr)
	{
		final int opt1 = this.getOptimizeFlags();
		final int opt2 = expr.getOptimizeFlags();
		if ((opt1 & SINGLE_LEVEL) != 0 && (opt2 & STAYS_IN_SUBTREE) != 0)
		{
			return new SequenceComposeExpr(this, expr);
		}
		return new ComposeExpr(this, expr);
	}
}
