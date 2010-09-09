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
package org.gxml.processor.xpath.v10.expressions;

import org.gxml.xpath.v10.expressions.ConvertibleBooleanExpr;
import org.gxml.xpath.v10.expressions.ExprContextDynamic;
import org.gxml.xpath.v10.expressions.ExprException;
import org.gxml.xpath.v10.expressions.NumberExpr;
import org.gxml.base.Model;
import org.gxml.processor.xpath.v10.relations.Relation;

final class NumberRelationalExpr 
    extends ConvertibleBooleanExpr
{
	private final Relation rel;
	private final NumberExpr expr1;
	private final NumberExpr expr2;

	NumberRelationalExpr(final Relation rel, final NumberExpr expr1, final NumberExpr expr2)
	{
		super();
		this.rel = rel;
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		return rel.relate(expr1.numberFunction(model, node, dynEnv), expr2.numberFunction(model, node, dynEnv));
	}
}
