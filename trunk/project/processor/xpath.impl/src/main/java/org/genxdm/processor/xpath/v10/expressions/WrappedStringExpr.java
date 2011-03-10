package org.genxdm.processor.xpath.v10.expressions;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public class WrappedStringExpr extends ConvertibleStringExpr {

	public static ConvertibleExpr wrap(StringExpr expr) {
		
		if (expr instanceof ConvertibleExpr) {
			return (ConvertibleExpr) expr;
		}
		
		return new WrappedStringExpr(expr);
	}
	
	public WrappedStringExpr(StringExpr expr) {
		m_wrappedExpr = expr;
	}
	
	@Override
	public <N> String stringFunction(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		return m_wrappedExpr.stringFunction(model, contextNode, dynEnv);
	}

	private StringExpr m_wrappedExpr;
}
