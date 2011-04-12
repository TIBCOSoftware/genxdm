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
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.XPathToolkit;

public interface XPathExtendToolkit extends XPathToolkit {

	/**
	 * Extend the XPath engine by declaring a function.
	 * 
	 * @param name	The name of the function to add
	 * @param newFunction	The implementation of the function.
	 * 
	 * @return	Any existing function declared with the same name.
	 */
	Function declareFunction(String name, Function newFunction);
	
	ConvertibleExpr wrapNodeSetExpr(NodeSetExpr nodeSetExpr, int optimizeFlags);
	
	ConvertibleExpr wrapBooleanExpr(BooleanExpr expr);
	
	ConvertibleExpr wrapNumberExpr(NumberExpr expr);
	
	ConvertibleExpr wrapStringExpr(StringExpr expr);
	
	ConvertibleExpr wrapVariantExpr(VariantExpr expr);
}
