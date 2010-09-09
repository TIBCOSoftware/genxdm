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

import org.gxml.xpath.v10.expressions.BooleanExpr;
import org.gxml.xpath.v10.expressions.ConvertibleBooleanExpr;
import org.gxml.xpath.v10.expressions.ExprContextDynamic;
import org.gxml.xpath.v10.expressions.ExprException;
import org.gxml.base.Model;

final class AndExpr
    extends ConvertibleBooleanExpr
{
	private final BooleanExpr expr1;
	private final BooleanExpr expr2;

	AndExpr(final BooleanExpr expr1, final BooleanExpr expr2)
	{
		super();
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public <N> boolean booleanFunction(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		return expr1.booleanFunction(model, contextNode, dynEnv) && expr2.booleanFunction(model, contextNode, dynEnv);
	}
}
