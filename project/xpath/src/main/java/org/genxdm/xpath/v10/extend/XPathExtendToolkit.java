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
