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
package org.genxdm.processor.xpath.v10.functions;

import org.genxdm.base.Model;
import org.genxdm.processor.xpath.v10.iterators.DifferenceNodeIterator;
import org.genxdm.xpath.v10.expressions.ConvertibleExpr;
import org.genxdm.xpath.v10.expressions.ConvertibleNodeSetExpr;
import org.genxdm.xpath.v10.expressions.ExprContextDynamic;
import org.genxdm.xpath.v10.expressions.ExprContextStatic;
import org.genxdm.xpath.v10.expressions.ExprException;
import org.genxdm.xpath.v10.expressions.ExprParseException;
import org.genxdm.xpath.v10.expressions.NodeSetExpr;
import org.genxdm.xpath.v10.iterators.NodeIterator;

/**
 * James Clark's extension function: node-set xt:difference(node-set, node-set)
 */
final class DifferenceFunction 
    extends Function2
{
	public DifferenceFunction()
	{
		super();
	}

	ConvertibleExpr makeCallExpr(final ConvertibleExpr e1, final ConvertibleExpr e2, final ExprContextStatic statEnv) throws ExprParseException
	{
		final NodeSetExpr nse1 = e1.makeNodeSetExpr(statEnv);
		final NodeSetExpr nse2 = e2.makeNodeSetExpr(statEnv);

		return new ConvertibleNodeSetExpr()
		{
			public <N> NodeIterator<N> nodeIterator(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return new DifferenceNodeIterator<N>(nse1.nodeIterator(model, node, dynEnv),
						nse2.nodeIterator(model, node, dynEnv), model);
			}
		};
	}
}
