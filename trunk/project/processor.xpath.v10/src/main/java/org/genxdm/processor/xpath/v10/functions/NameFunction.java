/*
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2011 TIBCO Software Inc.
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

import org.genxdm.Model;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleStringExpr;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class NameFunction 
    extends FunctionOpt1
{

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr expr, final ExprContextStatic statEnv) throws ExprParseException
	{
		final NodeSetExpr nse = expr.makeNodeSetExpr(statEnv);

		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
				final N node = nse.nodeIterator(model, contextNode, dynEnv).next();
				String result = "";
				if (node != null)
				{
					final String prefix = model.getPrefix(node);
					if (null != prefix && prefix.length() > 0)
					{
						final String localName = model.getLocalName(node);
						result = prefix.concat(":").concat(localName);
					}
					else
					{
						result = model.getLocalName(node);
						if (result == null)
							result = "";
					}
				}
				return result;
			}

            @Override
            public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                final Traverser node = nse.traverseNodes(contextNode, dynEnv);
                
                String result = "";
                if (node.moveToNext())
                {
                    final String prefix = node.getPrefix();
                    if (null != prefix && prefix.length() > 0)
                    {
                        final String localName = node.getLocalName();
                        result = prefix.concat(":").concat(localName);
                    }
                    else
                    {
                        result = node.getLocalName();
                        if (result == null)
                            result = "";
                    }
                }
                return result;
            }
		};
	}
}
