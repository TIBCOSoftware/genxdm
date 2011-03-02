package org.genxdm.processor.xpath.v10.expressions;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.extend.IConvertibleExpr;

public class WrappedNumberExpr extends ConvertibleNumberExpr {

	public static IConvertibleExpr wrap(NumberExpr expr) {
		
		if (expr instanceof IConvertibleExpr) {
			return (IConvertibleExpr) expr;
		}
		
		return new WrappedNumberExpr(expr);
	}
	
	public WrappedNumberExpr(NumberExpr expr) {
		m_wrappedExpr = expr;
	}
	
	@Override
	public <N> double numberFunction(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		return m_wrappedExpr.numberFunction(model, contextNode, dynEnv);
	}

	private final NumberExpr m_wrappedExpr;
}
