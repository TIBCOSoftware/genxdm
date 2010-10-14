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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConstants;

import org.genxdm.typed.types.AtomBridge;

public final class Gregorian
{
	public static <A> Gregorian dateTime(final A gregorian, final AtomBridge<A> gBridge)
	{
		final int astronomicalYear = gBridge.getYear(gregorian);
		final int month = gBridge.getMonth(gregorian);
		final int dayOfMonth = gBridge.getDayOfMonth(gregorian);
		final int hours = gBridge.getHourOfDay(gregorian);
		final int minutes = gBridge.getMinute(gregorian);
		final int integralSecond = gBridge.getIntegralSecondPart(gregorian);
		final BigDecimal fractionalSecond = gBridge.getFractionalSecondPart(gregorian);
		final int timezone = gBridge.getGmtOffset(gregorian);
		return new Gregorian(astronomicalYear, month + 1, dayOfMonth, hours, minutes, integralSecond, fractionalSecond, timezone);
	}

	private static int getAstronomicalYear(final Calendar calendar)
	{
		switch (calendar.get(Calendar.ERA))
		{
			case GregorianCalendar.AD:
			{
				return calendar.get(Calendar.YEAR);
			}
			case GregorianCalendar.BC:
			{
				return 1 - calendar.get(Calendar.YEAR);
			}
			default:
			{
				throw new AssertionError(calendar.get(Calendar.ERA));
			}
		}
	}

	private final int astronomicalYear;
	private final int day;
	private final BigDecimal fractionalSecond;
	private final int gmtOffset;
	private final int hour;
	private final int integralSecond;

	private final int minute;

	private final int month;

	public Gregorian(final int astronomicalYear, final int month, final int day, final int hour, final int minute, final int integralSecond, final BigDecimal fractionalSecond, final int gmtOffset)
	{
		this.astronomicalYear = astronomicalYear;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.integralSecond = integralSecond;
		this.fractionalSecond = fractionalSecond;
		this.gmtOffset = gmtOffset;
	}

	public int getDay()
	{
		return day;
	}

	public int getGmtOffset()
	{
		return gmtOffset;
	}

	public int getMonth()
	{
		return month;
	}

	public BigDecimal getTimeSpanSinceEpoch()
	{
		return GregorianSupport.getTimeSpanSinceEpoch(astronomicalYear, month, day, hour, minute, integralSecond, fractionalSecond, gmtOffset);
	}

	public int getYear()
	{
		return astronomicalYear;
	}

	public boolean hasDay()
	{
		return day != DatatypeConstants.FIELD_UNDEFINED;
	}

	public boolean hasGmtOffset()
	{
		return gmtOffset != DatatypeConstants.FIELD_UNDEFINED;
	}

	public boolean hasMonth()
	{
		return month != DatatypeConstants.FIELD_UNDEFINED;
	}

	public boolean hasYear()
	{
		return astronomicalYear != DatatypeConstants.FIELD_UNDEFINED;
	}

	/**
	 * Constructs a new class derived from XsGregorian of the same type as this class.
	 * 
	 * @param calendar
	 *            The Gregorian Calendar.
	 * @param overflow
	 *            An addiional seconds amount that will not cause the calendar to roll over.
	 * @param offsetInMinutes
	 *            The timezone offset.
	 * @return A new class derived from XsGregorian.
	 */
	private Gregorian newGregorian(final Calendar calendar, final BigDecimal overflow, final int offsetInMinutes)
	{
		final int year = getAstronomicalYear(calendar);
		final int month = calendar.get(Calendar.MONTH) + 1;
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		final int minutes = calendar.get(Calendar.MINUTE);

		final int seconds = calendar.get(Calendar.SECOND);
		final int millis = calendar.get(Calendar.MILLISECOND);

		final BigDecimal baseline = BigDecimal.valueOf(seconds * 1000L + millis).movePointLeft(3).add(overflow);

		final int integralSecond = baseline.intValue();
		final BigDecimal fractionalSecond = baseline.subtract(BigDecimal.valueOf(integralSecond));

		return new Gregorian(year, month, dayOfMonth, hourOfDay, minutes, integralSecond, fractionalSecond, offsetInMinutes);
	}

	public Gregorian normalize(final int offsetInMinutes)
	{
		if (offsetInMinutes != DatatypeConstants.FIELD_UNDEFINED)
		{
			if (gmtOffset != DatatypeConstants.FIELD_UNDEFINED)
			{
				// Transformation conducted keeping UTC constant.
				final BigDecimal timeSpan = getTimeSpanSinceEpoch();

				final long millis = timeSpan.movePointRight(3).longValue();

				final GregorianCalendar calendar = new GregorianCalendar();

				calendar.clear();

				calendar.setTimeZone(GregorianSupport.getTimeZone(offsetInMinutes));

				calendar.setTime(new Date(millis));

				final BigDecimal overflow = timeSpan.subtract(BigDecimal.valueOf(millis).movePointLeft(3));

				return newGregorian(calendar, overflow, offsetInMinutes);
			}
			else
			{
				// Transformation keeps local components constant.
				return new Gregorian(astronomicalYear, month, day, hour, minute, integralSecond, fractionalSecond, offsetInMinutes);
			}
		}
		else
		{
			if (gmtOffset != DatatypeConstants.FIELD_UNDEFINED)
			{
				// Transformation keeps local components constant.
				return new Gregorian(astronomicalYear, month, day, hour, minute, integralSecond, fractionalSecond, DatatypeConstants.FIELD_UNDEFINED);
			}
			else
			{
				// This is a no-op.
				return this;
			}
		}
	}

	@Override
	public String toString()
	{
		return "year=" + astronomicalYear + " month=" + month + " day=" + day;
	}
}
