package org.genxdm.xpath.v10.extend;

import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;

public interface IConvertibleExpr {

	/**
	 * cast it as a String expression
	 */
	public IConvertibleStringExpr makeStringExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a boolean expression
	 */
	public IConvertibleBooleanExpr makeBooleanExpr(
			final ExprContextStatic statEnv);

	/**
	 * cast it as a Variant (a variable or param you can bind to a name) expression
	 */
	public IConvertibleVariantExpr makeVariantExpr(
			final ExprContextStatic statEnv);

	/**
	 * cast it as a NodeSet expression (by default, don't)
	 */
	public IConvertibleNodeSetExpr makeNodeSetExpr(
			final ExprContextStatic statEnv) throws ExprParseException;

	/**
	 * cast it as a Number expression
	 */
	public IConvertibleNumberExpr makeNumberExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a Predicate expression
	 */
	public IConvertibleBooleanExpr makePredicateExpr(
			final ExprContextStatic statEnv);

}