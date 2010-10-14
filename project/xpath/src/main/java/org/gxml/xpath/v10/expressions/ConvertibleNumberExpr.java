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

import org.genxdm.base.Model;
import org.gxml.xpath.v10.Converter;
import org.gxml.xpath.v10.variants.NumberVariant;
import org.gxml.xpath.v10.variants.Variant;

public abstract class ConvertibleNumberExpr 
    extends ConvertibleExpr
    implements NumberExpr
{

	@Override
	public ConvertibleNumberExpr makeNumberExpr(final ExprContextStatic statEnv)
	{
		return this;
	}

	@Override
	public ConvertibleBooleanExpr makePredicateExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr()
		{
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> context) throws ExprException
			{
				return Converter.positionToBoolean(ConvertibleNumberExpr.this.numberFunction(model, node, context), context);
			}
		};
	}

	public ConvertibleBooleanExpr makeBooleanExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr()
		{
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return Converter.toBoolean(ConvertibleNumberExpr.this.numberFunction(model, node, dynEnv));
			}
		};
	}

	public ConvertibleVariantExpr makeVariantExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleVariantExpr()
		{
			public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return new NumberVariant<N>(ConvertibleNumberExpr.this.numberFunction(model, contextNode, dynEnv));
			}
		};
	}

	public ConvertibleStringExpr makeStringExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> context) throws ExprException
			{
				return Converter.toString(ConvertibleNumberExpr.this.numberFunction(model, node, context));
			}
		};
	}
}
