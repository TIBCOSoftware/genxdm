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


/**
 * An XPath expression (component) which can be cast to any of several types as needed
 */
public abstract class ConvertibleExpr 
{
	/**
	 * cast it as a String expression
	 */
	public abstract ConvertibleStringExpr makeStringExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a boolean expression
	 */
	public abstract ConvertibleBooleanExpr makeBooleanExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a Variant (a variable or param you can bind to a name) expression
	 */
	public abstract ConvertibleVariantExpr makeVariantExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a NodeSet expression (by default, don't)
	 */
	public ConvertibleNodeSetExpr makeNodeSetExpr(final ExprContextStatic statEnv) throws ExprParseException
	{
		throw new ExprParseException("value of expression cannot be converted to a node-set");
	}

	/**
	 * cast it as a Number expression
	 */
	public ConvertibleNumberExpr makeNumberExpr(final ExprContextStatic statEnv)
	{
		return makeStringExpr(statEnv).makeNumberExpr(statEnv);
	}

	/**
	 * cast it as a Predicate expression
	 */
	public ConvertibleBooleanExpr makePredicateExpr(final ExprContextStatic statEnv)
	{
		return makeBooleanExpr(statEnv);
	}
}
