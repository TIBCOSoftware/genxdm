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
package org.gxml.xpath.v10.expressions;

import org.gxml.base.Model;
import org.gxml.xpath.v10.iterators.NodeIterator;
import org.gxml.xpath.v10.variants.Variant;

/**
 *
 */
public abstract class ConvertibleVariantExpr 
    extends ConvertibleExpr implements VariantExpr
{

	public ConvertibleVariantExpr makeVariantExpr(final ExprContextStatic statEnv)
	{
		return this;
	}

	@Override
	public ConvertibleBooleanExpr makePredicateExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr( )
		{
			public <N> boolean booleanFunction(Model<N> model, final N node, ExprContextDynamic<N> context) throws ExprException
			{
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, node, context).convertToPredicate(context);
			}
		};
	}

	public ConvertibleBooleanExpr makeBooleanExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr()
		{
			public <N> boolean booleanFunction(Model<N> model, final N node, ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, node, dynEnv).convertToBoolean();
			}
		};
	}

	@Override
	public ConvertibleNumberExpr makeNumberExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleNumberExpr()
		{
			public <N> double numberFunction(Model<N> model, N contextNode, ExprContextDynamic<N> context) throws ExprException
			{
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, contextNode, context).convertToNumber();
			}
		};
	}

	public ConvertibleStringExpr makeStringExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> context) throws ExprException
			{
				final Variant<N> variant = ConvertibleVariantExpr.this.evaluateAsVariant(model, node, context);
				if (null != variant)
				{
					return variant.convertToString();
				}
				else
				{
					throw new AssertionError(ConvertibleVariantExpr.this + " => " + variant);
				}
			}
		};
	}

	@Override
	public ConvertibleNodeSetExpr makeNodeSetExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleNodeSetExpr()
		{
			public <N> NodeIterator<N> nodeIterator(Model<N> model, final N node, ExprContextDynamic<N> context) throws ExprException
			{
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, node, context).convertToNodeSet();
			}
		};
	}
}
