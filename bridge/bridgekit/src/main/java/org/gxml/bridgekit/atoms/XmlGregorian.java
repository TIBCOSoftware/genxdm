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
package org.gxml.bridgekit.atoms;

import java.math.BigDecimal;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.types.SmNativeType;

public final class XmlGregorian extends XmlAbstractAtom
{
	private final int day;
	private final BigDecimal fSecond;
	private final int hour;
	private final int minute;
	private final int month;
	private final SmNativeType nativeType;
	private final int second;
	private final int timezone;
	private final int year;

	public XmlGregorian(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fractionalSecond, final int timezone, final SmNativeType nativeType)
	{
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.fSecond = fractionalSecond;
		this.timezone = timezone;
		this.nativeType = PreCondition.assertArgumentNotNull(nativeType, "nativeType");
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof XmlGregorian)
		{
			final XmlGregorian other = (XmlGregorian)obj;
			return year == other.getYear() && month == other.getMonth() && day == other.getDayOfMonth() && hour == other.getHourOfDay() && minute == other.getMinute() && second == other.getSecond() && getFractionalSecond().equals(other.getFractionalSecond())
					&& timezone == other.getGmtOffset();
		}
		else
		{
			return false;
		}
	}

	public String getC14NForm()
	{
		switch (nativeType)
		{
			case DATE:
			{
				return GregorianFormat.date.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case DATETIME:
			{
				return GregorianFormat.dateTime.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case TIME:
			{
				return GregorianFormat.time.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case GYEARMONTH:
			{
				return GregorianFormat.gYearMonth.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case GYEAR:
			{
				return GregorianFormat.gYear.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case GMONTHDAY:
			{
				return GregorianFormat.gMonthDay.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case GDAY:
			{
				return GregorianFormat.gDay.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			case GMONTH:
			{
				return GregorianFormat.gMonth.format(year, month, day, hour, minute, second, fSecond, timezone);
			}
			default:
			{
				throw new AssertionError(nativeType);
			}
		}
	}

	public int getDayOfMonth()
	{
		return day;
	}

	public BigDecimal getFractionalSecond()
	{
		return (null != fSecond) ? fSecond : BigDecimal.ZERO;
	}

	public int getGmtOffset()
	{
		return timezone;
	}

	public int getHourOfDay()
	{
		return hour;
	}

	public int getMinute()
	{
		return minute;
	}

	public int getMonth()
	{
		return month;
	}

	public SmNativeType getNativeType()
	{
		return nativeType;
	}

	public int getSecond()
	{
		return second;
	}

	public int getYear()
	{
		return year;
	}

	public boolean isWhiteSpace()
	{
		return false;
	}
}
