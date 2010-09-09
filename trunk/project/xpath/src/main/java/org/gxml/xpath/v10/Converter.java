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
package org.gxml.xpath.v10;

import org.gxml.base.Model;
import org.gxml.xpath.v10.expressions.ExprContextDynamic;
import org.gxml.xpath.v10.expressions.ExprException;
import org.gxml.xpath.v10.iterators.NodeIterator;

public final class Converter
{
	private Converter()
	{
	}

	public static double toNumber(String str)
	{
		try
		{
			return Double.valueOf(str).doubleValue();
		}
		catch (NumberFormatException e)
		{
			return Double.NaN;
		}
	}

	public static <N> double toNumber(final NodeIterator<N> iter, final Model<N> model) throws ExprException
	{
		return toNumber(toString(iter, model));
	}

	public static double toNumber(boolean b)
	{
		return b ? 1.0 : 0.0;
	}

	public static boolean toBoolean(String str)
	{
		return str.length() != 0;
	}

	public static boolean toBoolean(double num)
	{
		return num != 0.0 && !Double.isNaN(num);
	}

	public static <N> boolean toBoolean(NodeIterator<N> iter) throws ExprException
	{
		return iter.next() != null;
	}

	public static <N> String toString(final NodeIterator<N> iter, final Model<N> model) throws ExprException
	{
		return toString(iter.next(), model);
	}

	public static <N> String toString(final N node, final Model<N> model) throws ExprException
	{
		return model.getStringValue(node);
	}

	public static String toString(double num)
	{
		if (!Double.isInfinite(num) && (num >= (double)(1L << 53) || -num >= (double)(1L << 53)))
			return new java.math.BigDecimal(num).toString();
		String s = Double.toString(num);
		int len = s.length();
		if (s.charAt(len - 2) == '.' && s.charAt(len - 1) == '0')
		{
			s = s.substring(0, len - 2);
			if (s.equals("-0"))
				return "0";
			return s;
		}
		int e = s.indexOf('E');
		if (e < 0)
			return s;
		int exp = Integer.parseInt(s.substring(e + 1));
		String sign;
		if (s.charAt(0) == '-')
		{
			sign = "-";
			s = s.substring(1);
			--e;
		}
		else
			sign = "";
		int nDigits = e - 2;
		if (exp >= nDigits)
		{
			return sign + s.substring(0, 1) + s.substring(2, e) + zeros(exp - nDigits);
		}
		if (exp > 0)
		{
			return sign + s.substring(0, 1) + s.substring(2, 2 + exp) + "." + s.substring(2 + exp, e);
		}
		return sign + "0." + zeros(-1 - exp) + s.substring(0, 1) + s.substring(2, e);
	}

	static private String zeros(int n)
	{
		char[] buf = new char[n];
		for (int i = 0; i < n; i++)
			buf[i] = '0';
		return new String(buf);
	}

	public static String toString(boolean b)
	{
		return b ? "true" : "false";
	}

	public static <N> boolean positionToBoolean(double d, ExprContextDynamic<N> context) throws ExprException
	{
		return context.getContextPosition() == d;
	}
}
