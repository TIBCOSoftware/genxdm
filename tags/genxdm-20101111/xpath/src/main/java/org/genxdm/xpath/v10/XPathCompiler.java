/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.xpath.v10;

import org.genxdm.xpath.v10.expressions.BooleanExpr;
import org.genxdm.xpath.v10.expressions.ExprContextStatic;
import org.genxdm.xpath.v10.expressions.ExprParseException;
import org.genxdm.xpath.v10.expressions.NodeSetExpr;
import org.genxdm.xpath.v10.expressions.NumberExpr;
import org.genxdm.xpath.v10.expressions.StringExpr;
import org.genxdm.xpath.v10.expressions.VariantExpr;

public interface XPathCompiler
{
	VariantExpr compile(String expression, final ExprContextStatic statEnv) throws ExprParseException;

	BooleanExpr compileBooleanExpr(String expression, final ExprContextStatic statEnv) throws ExprParseException;

	NodeSetExpr compileNodeSetExpr(String expression, final ExprContextStatic statEnv) throws ExprParseException;

	NumberExpr compileNumberExpr(String expression, final ExprContextStatic statEnv) throws ExprParseException;

	StringExpr compileStringExpr(String expression, final ExprContextStatic statEnv) throws ExprParseException;
}
