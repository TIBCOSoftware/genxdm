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

import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.VariantExpr;

public interface ConvertibleExpr {

    /**
     * cast it as a String expression
     */
    public StringExpr makeStringExpr(final StaticContext statEnv);

    /**
     * cast it as a boolean expression
     */
    public BooleanExpr makeBooleanExpr(
            final StaticContext statEnv);

    /**
     * cast it as a Variant (a variable or param you can bind to a name) expression
     */
    public VariantExpr makeVariantExpr(
            final StaticContext statEnv);

    /**
     * cast it as a NodeSet expression
     */
    public ConvertibleNodeSetExpr makeNodeSetExpr(
            final StaticContext statEnv) throws ExprParseException;

    /**
     * cast it as a Number expression
     */
    public NumberExpr makeNumberExpr(final StaticContext statEnv);

    /**
     * cast it as a Predicate expression
     */
    public BooleanExpr makePredicateExpr(
            final StaticContext statEnv);

}
