package org.genxdm.xpath.v10.extend;

import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.VariantExpr;

public interface IConvertibleExpr {

	/**
	 * cast it as a String expression
	 */
	public StringExpr makeStringExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a boolean expression
	 */
	public BooleanExpr makeBooleanExpr(
			final ExprContextStatic statEnv);

	/**
	 * cast it as a Variant (a variable or param you can bind to a name) expression
	 */
	public VariantExpr makeVariantExpr(
			final ExprContextStatic statEnv);

	/**
	 * cast it as a NodeSet expression
	 */
	public IConvertibleNodeSetExpr makeNodeSetExpr(
			final ExprContextStatic statEnv) throws ExprParseException;

	/**
	 * cast it as a Number expression
	 */
	public NumberExpr makeNumberExpr(final ExprContextStatic statEnv);

	/**
	 * cast it as a Predicate expression
	 */
	public BooleanExpr makePredicateExpr(
			final ExprContextStatic statEnv);

}
