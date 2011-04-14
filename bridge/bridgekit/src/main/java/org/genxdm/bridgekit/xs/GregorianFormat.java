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
package org.genxdm.bridgekit.xs;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

final class GregorianFormat
{
	public static final GregorianFormat gYearMonth = new GregorianFormat("gYearMonth", true, true, false, false);
	public static final GregorianFormat gYear = new GregorianFormat("gYear", true, false, false, false);
	public static final GregorianFormat gMonthDay = new GregorianFormat("gMonthDay", false, true, true, false);
	public static final GregorianFormat gDay = new GregorianFormat("gDay", false, false, true, false);
	public static final GregorianFormat gMonth = new GregorianFormat("gMonth", false, true, false, false);
	public static final GregorianFormat dateTime = new GregorianFormat("dateTime", true, true, true, true);
	public static final GregorianFormat date = new GregorianFormat("date", true, true, true, false);
	public static final GregorianFormat time = new GregorianFormat("time", false, false, false, true);

	private static final String YEAR_MISSING = "--";
	private static final String MONTH_MISSING = "-";
	private static final String DATE_SEPARATOR = "-";
	private static final String TIME_SEPARATOR = ":";
	private static final String DATETIME_SEPARATOR = "T";
	private static final int MIN_YEAR_DIGITS = 4;
	private static final int MONTH_DIGITS = 2;
	private static final int DAYOFMONTH_DIGITS = 2;

	private final String m_name;
	private final boolean m_hasYear;
	private final boolean m_hasMonth;
	private final boolean m_hasDayOfMonth;
	private final boolean m_hasTime;

	private GregorianFormat(String name, boolean hasYear, boolean hasMonth, boolean hasDayOfMonth, boolean hasTime)
	{
		m_name = name;
		m_hasYear = hasYear;
		m_hasMonth = hasMonth;
		m_hasDayOfMonth = hasDayOfMonth;
		m_hasTime = hasTime;
	}

	public String toString()
	{
		return m_name;
	}

	public String format(final XMLGregorianCalendar gregorian)
	{
		final StringBuilder buffer = new StringBuilder();

		if (m_hasYear)
		{
			// [-]YYYY
			appendDigits(gregorian.getYear(), MIN_YEAR_DIGITS, buffer);

			if (m_hasMonth)
			{
				// [-]YYYY-MM
				buffer.append(DATE_SEPARATOR);
				appendDigits(gregorian.getMonth(), MONTH_DIGITS, buffer);

				if (m_hasDayOfMonth)
				{
					// [-]YYYY-MM-DD
					buffer.append(DATE_SEPARATOR);
					appendDigits(gregorian.getDay(), DAYOFMONTH_DIGITS, buffer);

					if (m_hasTime)
					{
						// [-]YYYY-MM-DDTHH:MM:SS.SSS
						buffer.append(DATETIME_SEPARATOR);
						appendTimePart(gregorian, buffer);
					}
					else
					{
						// [-]YYYY-MM-DD
					}
				}
				else
				{
					// [-]YYYY-MM
				}
			}
			else
			{
				if (m_hasDayOfMonth)
				{
					throw new RuntimeException();
				}
				else
				{
					// [-]YYYY
				}
			}
		}
		else
		{
			if (m_hasMonth)
			{
				// --MM
				buffer.append(YEAR_MISSING);
				appendDigits(gregorian.getMonth(), MONTH_DIGITS, buffer);

				if (m_hasDayOfMonth)
				{
					// --MM-DD
					buffer.append(DATE_SEPARATOR);
					appendDigits(gregorian.getDay(), DAYOFMONTH_DIGITS, buffer);
				}
				else
				{
					// --MM
				}
			}
			else
			{
				if (m_hasDayOfMonth)
				{
					buffer.append(YEAR_MISSING);

					buffer.append(MONTH_MISSING);

					appendDigits(gregorian.getDay(), DAYOFMONTH_DIGITS, buffer);
				}
				else
				{
					if (m_hasTime)
					{
						appendTimePart(gregorian, buffer);
					}
					else
					{
						throw new RuntimeException();
					}
				}
			}
		}

		final int timezone = gregorian.getTimezone();

		if (DatatypeConstants.FIELD_UNDEFINED != timezone)
		{
			buffer.append(TimeZoneFormat.getTimeZoneString(timezone * 60000, true));
		}

		return buffer.toString();
	}

	private static StringBuilder appendTimePart(final XMLGregorianCalendar gregorian, final StringBuilder buffer)
	{
		return appendTimePart(gregorian.getHour(), gregorian.getMinute(), gregorian.getSecond(), gregorian.getFractionalSecond(), buffer);
	}

	private static StringBuilder appendTimePart(final int hour, final int minute, final int integralSec, final BigDecimal fractional, final StringBuilder buffer)
	{
		appendDigits(hour, 2, buffer);
		buffer.append(TIME_SEPARATOR);
		appendDigits(minute, 2, buffer);
		buffer.append(TIME_SEPARATOR);

		appendDigits(integralSec, 2, buffer);

		if ((null != fractional) && (fractional.signum() > 0))
		{
			final String decimalStr = fractional.toPlainString();

			final int decimalPoint = decimalStr.indexOf('.');

			String decimals = decimalStr.substring(decimalPoint);

			// The decimals part must not end with a zero.
			while (decimals.charAt(decimals.length() - 1) == '0')
			{
				decimals = decimals.substring(0, decimals.length() - 1);
			}

			buffer.append(decimals);
		}

		return buffer;
	}

	private static StringBuilder appendDigits(final int value, final int minDigits, final StringBuilder sb)
	{
		final String strval;
		if (value < 0)
		{
			sb.append('-');
			strval = Integer.toString(-value);
		}
		else
		{
			strval = Integer.toString(value);
		}
		int padding = minDigits - strval.length();
		while (padding > 0)
		{
			sb.append('0');
			padding--;
		}
		sb.append(strval);
		return sb;
	}
}
