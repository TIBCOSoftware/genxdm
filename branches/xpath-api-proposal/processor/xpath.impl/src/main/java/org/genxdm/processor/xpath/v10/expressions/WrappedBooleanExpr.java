package org.genxdm.processor.xpath.v10.expressions;

import org.genxdm.Model;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public class WrappedBooleanExpr extends ConvertibleBooleanExpr {

	public static ConvertibleExpr wrap(BooleanExpr expr) {
		
		if (expr instanceof ConvertibleExpr) {
			return (ConvertibleExpr) expr;
		}
		
		return new WrappedBooleanExpr(expr);
	}
	
	public WrappedBooleanExpr(BooleanExpr expr) {
		m_wrappedExpr = expr;
	}
	
	@Override
	public <N> boolean booleanFunction(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		// TODO Auto-generated method stub
		return m_wrappedExpr.booleanFunction(model, contextNode, dynEnv);
	}

	private final BooleanExpr m_wrappedExpr;
}
