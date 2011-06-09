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
package org.genxdm.bridgekit.atoms;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.XMLConstants;

public final class NumericSupport
{
	private static final BigDecimal DECIMAL_ONE = new BigDecimal(BigInteger.ONE);
	private static final BigDecimal DECIMAL_TEN = BigDecimal.valueOf(10);
	private static final BigDecimal DECIMAL_ZERO = new BigDecimal(BigInteger.ZERO);
	private static final double DOUBLE_LOWER_LIMIT = Double.parseDouble("0.000001");
	private static final double DOUBLE_UPPER_LIMIT = Double.parseDouble("1000000");
	private static final double FLOAT_LOWER_LIMIT = Float.parseFloat("0.000001");
	private static final double FLOAT_UPPER_LIMIT = Float.parseFloat("1000000");
	private static final String LEGACY_NEGATIVE_INFINITY_LITERAL = "-Infinity";
	private static final String LEGACY_POSITIVE_INFINITY_LITERAL = "Infinity";
	private static final String MODERN_NEGATIVE_INFINITY_LITERAL = "-INF";
	private static final String MODERN_POSITIVE_INFINITY_LITERAL = "INF";
	private static final String NAN_LITERAL = "NaN";

	/**
	 * Removes leading zeros (even when there is a minus sign). <br/>
	 * Behavior is undefined for input containing '+', '.', and 'E'.
	 */
	static String compressIntegerNumberString(String s)
	{
		final boolean negative;

		if (s.charAt(0) == '-')
		{
			negative = true;
			s = s.substring(1);
		}
		else
		{
			negative = false;
		}

		while (s.length() > 1 && s.charAt(0) == '0')
		{
			s = s.substring(1);
		}

		return negative ? (s.equals("0") ? s : ("-" + s)) : s;
	}

	/**
	 * Converts the Java String representation of a non-scientific notation number to a format where the number is the
	 * most compact representation. <br/>
	 * Behavior is undefined for input containing '+', and 'E'.
	 * 
	 * @param s
	 *            The Java String representation.
	 */
	private static String compressNonScientificNumberString(String s)
	{
		final int len = s.length();

		if (len > 0)
		{
			final int indexOfDecimalPoint = s.indexOf('.');
			if (indexOfDecimalPoint < 0)
			{
				return compressIntegerNumberString(s);
			}
			else
			{
				while (s.charAt(s.length() - 1) == '0')
				{
					s = s.substring(0, s.length() - 1);
				}

				if (s.charAt(s.length() - 1) == '.')
				{
					return compressIntegerNumberString(s.substring(0, s.length() - 1));
				}
				else
				{
					return s;
				}
			}
		}
		else
		{
			// Zero-length string is just returned.
			return s;
		}
	}

	/**
	 * Converts the Java String representation of a Float, Double, or Decimal to a non-scientific notation format where
	 * the number is the most compact representation.
	 * 
	 * @param s
	 *            The Java String representation.
	 */
	public static String compressNumberString(String s)
	{
		final int len = s.length();

		if (len > 0)
		{
			int indexOfE = s.indexOf('E');
			if (indexOfE < 0)
			{
				return compressNonScientificNumberString(s);
			}
			else
			{
				final int exp = parseExponent(s.substring(indexOfE + 1));

				final StringBuilder buffer = new StringBuilder();

				if (s.charAt(0) == '-')
				{
					buffer.append('-');
					s = s.substring(1);
					--indexOfE;
				}

				final int indexOfDP = s.indexOf('.');

				// Calculate the number of digits to the left and right of the decimal point,
				// allowing for the fact that there may not be a decimal point.
				final int digitsL;
				final int digitsR;

				if (indexOfDP >= 0)
				{
					digitsL = indexOfDP;
					digitsR = indexOfE - indexOfDP - 1;
				}
				else
				{
					digitsL = indexOfE;
					digitsR = 0;
				}

				if (exp < 0 && (-exp >= digitsL))
				{
					buffer.append("0.");

					if (-exp > digitsL)
					{
						buffer.append(pad(-exp - digitsL, '0'));
					}
				}

				final int endDigitsL = digitsL;

				if ((exp < 0) && (-exp < digitsL))
				{
					buffer.append(s.substring(0, endDigitsL + exp)).append(".").append(s.substring(endDigitsL + exp, endDigitsL));
				}
				else
				{
					buffer.append(s.substring(0, endDigitsL));
				}

				if (exp == 0)
				{
					buffer.append(".");
				}

				if (digitsR > 0)
				{
					final int beginDigitsR = digitsL + 1;

					if ((exp > 0) && (exp < digitsR))
					{
						buffer.append(s.substring(beginDigitsR, beginDigitsR + exp)).append(".").append(s.substring(beginDigitsR + exp, indexOfE));
					}
					else
					{
						buffer.append(s.substring(beginDigitsR, indexOfE));
					}
				}

				if (exp > digitsR)
				{
					buffer.append(pad(exp - digitsR, '0'));
				}

				return compressNonScientificNumberString(buffer.toString());
			}
		}
		else
		{
			// Zero-length string is just returned.
			return s;
		}
	}

	/**
	 * Returns the decimal as {http://www.w3.org/2001/XMLSchema}decimal('canonical')
	 */
	public static String constructionString(final BigDecimal decval)
	{
		return "{".concat(XMLConstants.W3C_XML_SCHEMA_NS_URI).concat("}decimal('").concat(NumericSupport.formatDecimalC14N(decval)).concat("')");
	}

	public static String formatBooleanC14N(final boolean bVal)
	{
		return bVal ? "true" : "false";
	}

	/**
	 * <code>decimal</code> has a lexical representation consisting of a finite-length sequence of decimal digits
	 * (#x30-#x39) separated by a period as a decimal indicator. The canonical representation for decimal is defined by
	 * prohibiting certain options from the lexical representation. Specifically, the preceding optional "+" sign is
	 * prohibited. The decimal point is required. Leading and trailing zeroes are prohibited subject to the following:
	 * there must be at least one digit to the right and to the left of the decimal point which may be a zero.
	 * 
	 * @param decval
	 *            The decimal value for which the canonical representation is required.
	 * @return The canonical representation of a decimal value.
	 */
	public static String formatDecimalC14N(final BigDecimal decval)
	{
		final String strval = decval.toPlainString();

		final int dp = strval.indexOf('.');

		if (dp < 0)
		{
			return (new StringBuilder(strval)).append(".0").toString();
		}
		else
		{
			final int lbound = dp + 1;
			int ubound = strval.length() - 1;

			while ((ubound > lbound) && (strval.charAt(ubound) == '0'))
			{
				--ubound;
			}

			// substring(beginIndex, endIndex) includes {beginIndex,...,endIndex-1}
			return strval.substring(0, ubound + 1);
		}
	}

	public static String formatDecimalXPath10(final BigDecimal number)
	{
		return compressNumberString(number.toPlainString());
	}

	public static String formatDecimalXQuery10(final BigDecimal number)
	{
		return compressNumberString(number.toPlainString());
	}

	public static String formatDoubleC14N(final double dblval)
	{
		if (dblval == Double.POSITIVE_INFINITY)
		{
			return MODERN_POSITIVE_INFINITY_LITERAL;
		}
		else if (dblval == Double.NEGATIVE_INFINITY)
		{
			return MODERN_NEGATIVE_INFINITY_LITERAL;
		}
		else if (Double.isNaN(dblval))
		{
			return NAN_LITERAL;
		}
		else if (dblval == 0.0)
		{
			return "0.0E0";
		}
		else
		{
			String s = Double.toString(dblval);

			boolean negative;

			if (s.charAt(0) == '-')
			{
				negative = true;
				s = s.substring(1);
			}
			else
			{
				negative = false;
			}

			BigDecimal mantissa;
			int exponent;

			int indexE = s.indexOf('E');

			if (indexE < 0)
			{
				mantissa = new BigDecimal(s);
				exponent = 0;
			}
			else
			{
				mantissa = new BigDecimal(s.substring(0, indexE));
				exponent = Integer.parseInt(s.substring(indexE + 1));
			}

			if (mantissa.compareTo(DECIMAL_ZERO) != 0)
			{
				while (mantissa.compareTo(DECIMAL_ONE) < 0)
				{
					mantissa = mantissa.movePointRight(1);
					--exponent;
				}

				while (mantissa.compareTo(DECIMAL_TEN) >= 0)
				{
					mantissa = mantissa.movePointLeft(1);
					++exponent;
				}
			}

			final StringBuilder buffer = new StringBuilder();

			if (negative)
			{
				buffer.append('-');
			}

			buffer.append(formatDecimalC14N(mantissa));

			// XMLSchema-datatypes makes the "E" optional but XML Query
			// makes the "E" required for a DoubleLiteral.
			buffer.append('E');
			buffer.append(exponent);

			return buffer.toString();
		}
	}

	public static String formatDoubleXPath10(final double number)
	{
		if (Double.isNaN(number))
		{
			return NAN_LITERAL;
		}
		else if (Double.isInfinite(number))
		{
			return (number > 0) ? LEGACY_POSITIVE_INFINITY_LITERAL : LEGACY_NEGATIVE_INFINITY_LITERAL;
		}
		else
		{
			final int iVal = (int)number;
			if (number == iVal)
			{
				if (0 == iVal)
				{
					if (new Double(number).compareTo(0.0d) < 0)
					{
						return "-0";
					}
					else
					{
						return "0";
					}
				}
				else
				{
					return Integer.toString(iVal);
				}
			}
			else
			{
				return NumericSupport.compressNumberString(Double.toString(number));
			}
		}
	}

	public static String formatDoubleXQuery10(final double number)
	{
		if (Double.isNaN(number))
		{
			return NAN_LITERAL;
		}
		else if (Double.isInfinite(number))
		{
			return (number > 0) ? MODERN_POSITIVE_INFINITY_LITERAL : MODERN_NEGATIVE_INFINITY_LITERAL;
		}
		else
		{
			if (0.0d == number)
			{
				if (new Double(number).compareTo(0.0d) < 0)
				{
					return "-0";
				}
				else
				{
					return "0";
				}
			}
			else
			{
				final double abs = Math.abs(number);
				if (abs >= DOUBLE_LOWER_LIMIT && abs < DOUBLE_UPPER_LIMIT)
				{
					return NumericSupport.formatDoubleXPath10(number);
				}
				else
				{
					return NumericSupport.formatDoubleC14N(number);
				}
			}
		}
	}

	public static String formatFloatC14N(final float fltval)
	{
		if (fltval == Float.POSITIVE_INFINITY)
		{
			return MODERN_POSITIVE_INFINITY_LITERAL;
		}
		else if (fltval == Float.NEGATIVE_INFINITY)
		{
			return MODERN_NEGATIVE_INFINITY_LITERAL;
		}
		else if (Float.isNaN(fltval))
		{
			return NAN_LITERAL;
		}
		else if (fltval == 0.0)
		{
			return "0.0E0";
		}
		else
		{
			String s = Float.toString(fltval);

			boolean negative;

			if (s.charAt(0) == '-')
			{
				negative = true;
				s = s.substring(1);
			}
			else
			{
				negative = false;
			}

			BigDecimal mantissa;
			int exponent;

			int indexE = s.indexOf('E');

			if (indexE < 0)
			{
				mantissa = new BigDecimal(s);
				exponent = 0;
			}
			else
			{
				mantissa = new BigDecimal(s.substring(0, indexE));
				exponent = Integer.parseInt(s.substring(indexE + 1));
			}

			if (mantissa.compareTo(DECIMAL_ZERO) != 0)
			{
				while (mantissa.compareTo(DECIMAL_ONE) < 0)
				{
					mantissa = mantissa.movePointRight(1);
					--exponent;
				}

				while (mantissa.compareTo(DECIMAL_TEN) >= 0)
				{
					mantissa = mantissa.movePointLeft(1);
					++exponent;
				}
			}

			final StringBuilder buffer = new StringBuilder();

			if (negative)
			{
				buffer.append('-');
			}

			buffer.append(formatDecimalC14N(mantissa));

			// XMLSchema-datatypes makes the "E" optional but XML Query
			// makes the "E" required for a DoubleLiteral. Make xs:float
			// consistent with xs:double!
			buffer.append('E');
			buffer.append(exponent);

			return buffer.toString();
		}
	}

	public static String formatFloatXPath10(final float number)
	{
		if (Float.isNaN(number))
		{
			return NAN_LITERAL;
		}
		else if (Float.isInfinite(number))
		{
			return (number > 0) ? LEGACY_POSITIVE_INFINITY_LITERAL : LEGACY_NEGATIVE_INFINITY_LITERAL;
		}
		else
		{
			final int iVal = (int)number;
			if (number == iVal)
			{
				if (0 == iVal)
				{
					if (new Float(number).compareTo(0.0f) < 0)
					{
						return "-0";
					}
					else
					{
						return "0";
					}
				}
				else
				{
					return Integer.toString(iVal);
				}
			}
			else
			{
				return NumericSupport.compressNumberString(Float.toString(number));
			}
		}
	}

	public static String formatFloatXQuery10(final float number)
	{
		if (Float.isNaN(number))
		{
			return NAN_LITERAL;
		}
		else if (Float.isInfinite(number))
		{
			return (number > 0) ? MODERN_POSITIVE_INFINITY_LITERAL : MODERN_NEGATIVE_INFINITY_LITERAL;
		}
		else
		{
			if (0.0f == number)
			{
				if (new Float(number).compareTo(0.0f) < 0)
				{
					return "-0";
				}
				else
				{
					return "0";
				}
			}
			else
			{
				final float abs = Math.abs(number);
				if (abs >= FLOAT_LOWER_LIMIT && abs < FLOAT_UPPER_LIMIT)
				{
					return NumericSupport.formatFloatXPath10(number);
				}
				else
				{
					return NumericSupport.formatFloatC14N(number);
				}
			}
		}
	}

	/**
	 * Returns a character array containing the specified number of the specified character. For n less than or equal to
	 * zero, returns the empty string.
	 * 
	 * @param n
	 *            The number of characters to generate.
	 * @param ch
	 *            The repeated character.
	 * @throws IllegalArgumentException
	 *             if n is less than or equal to zero.
	 */
	private static char[] pad(final int n, final char ch) throws IllegalArgumentException
	{
		if (n <= 0)
		{
			throw new IllegalArgumentException();
		}
		else
		{
			final char[] buf = new char[n];

			for (int i = 0; i < n; i++)
			{
				buf[i] = ch;
			}

			return buf;
		}
	}

	private static int parseExponent(final String exponentString)
	{
		// JDK 1.5 can introduce a plus sign that Integer.parseInt can't handle.
		// e.g. 1.234E+9

		if (exponentString.startsWith("+"))
		{
			return Integer.parseInt(exponentString.substring(1));
		}
		else
		{
			return Integer.parseInt(exponentString);
		}
	}

	/**
	 * Removes leading and trailing whitespace, and any leading plus sign provided it is followed by a digit (0 through
	 * 9).
	 * 
	 * @param strval
	 *            The value to be trimmed.
	 * @return A trimmed numeric representation ready for parsing.
	 * @throws NumberFormatException
	 *             if the number starts with two consecutive signs;
	 *             http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5038425
	 */
	public static String trim(final String strval) throws NumberFormatException
	{
		final String collapsed = strval.trim();

		final int collapsedLength = collapsed.length();

		if (collapsedLength > 1)
		{
			final char sign = collapsed.charAt(0);
			if (sign == '+' || sign == '-')
			{
				final char first = collapsed.charAt(1);

				if (first == '-' || first == '+')
				{
					throw new NumberFormatException(strval);
				}
				if (sign == '+' && first >= '0' && first <= '9')
				{
					return collapsed.substring(1);
				}
			}
		}
		else if (collapsedLength == 0 || collapsed.charAt(0) > '9' || collapsed.charAt(0) < '0')
		{
			throw new NumberFormatException(strval);
		}
		return collapsed;
	}
}
