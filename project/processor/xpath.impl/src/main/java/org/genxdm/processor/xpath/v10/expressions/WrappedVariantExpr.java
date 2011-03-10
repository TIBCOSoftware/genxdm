package org.genxdm.processor.xpath.v10.expressions;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * Wrapper around a {@link VariantExpr} that turns it into an IConvertibleExpr
 */
public class WrappedVariantExpr extends ConvertibleVariantExpr {

	public static ConvertibleExpr wrap(VariantExpr expr) {
		if (expr instanceof ConvertibleExpr) {
			return (ConvertibleExpr) expr;
		}
		
		return new WrappedVariantExpr(expr);
	}
	
	public WrappedVariantExpr(VariantExpr expr) {
		m_expr = expr;
	}
	
	@Override
	public <N> Variant<N> evaluateAsVariant(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		return m_expr.evaluateAsVariant(model, contextNode, dynEnv);
	}

	private VariantExpr m_expr;
}
