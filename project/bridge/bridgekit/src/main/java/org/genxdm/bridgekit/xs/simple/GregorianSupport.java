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
package org.genxdm.bridgekit.xs.simple;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;

final class GregorianSupport
{
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int TIMEZONE_LENGTH = 6;

    private static void addTwoDigits(final int posValue, final StringBuilder buffer)
    {
        if (posValue < 10)
        {
            buffer.append('0');
            buffer.append(posValue);
        }
        else if (posValue < 100)
        {
            buffer.append(posValue);
        }
        else
        {
            throw new RuntimeException();
        }
    }
    
   public static BigDecimal getTimeSpanSinceEpoch(final int astronomicalYear, final int month, final int day, final int hour, final int minute, final int integralSecond, final BigDecimal fractionalSecond, final int gmtOffset)
    {
        final GregorianCalendar calendar = new GregorianCalendar();

        calendar.clear();

        final BigDecimal remainder = setCalendar(calendar, astronomicalYear, month, day, hour, minute, integralSecond, fractionalSecond, gmtOffset, TimeZone.getDefault());

        final long time = calendar.getTime().getTime();
        
		final BigDecimal baseline = BigDecimal.valueOf(time).movePointLeft(3);

        return baseline.add(remainder);
    }

    public static TimeZone getTimeZone(final int offset)
    {
        if (DatatypeConstants.FIELD_UNDEFINED != offset)
        {
            final int offsetInMillis = offset * 60000;

            final String customID = "GMT" + getTimeZoneString(offsetInMillis, false);

            return new SimpleTimeZone(offsetInMillis, customID);
        }
        else
        {
            return null;
        }
    }

    private static String getTimeZoneString(final int offsetInMillis, final boolean zuluEnabled)
    {
        final int offsetInSeconds = (offsetInMillis) / 1000;

        final int offsetInMinutes = offsetInSeconds / SECONDS_PER_MINUTE;

        final int hours = offsetInMinutes / MINUTES_PER_HOUR;
        final int minutes = offsetInMinutes % MINUTES_PER_HOUR;

        return getTimeZoneString(hours, minutes, zuluEnabled);
    }
    
    private static String getTimeZoneString(final int hours, final int minutes, final boolean zuluEnabled)
    {
        return getTimeZoneString(hours, minutes, zuluEnabled, true);
    }

    /**
     * Returns a timezone string with the format Z|(+|-)HH:MM with optional Z and +.
     *
     * @param hours       The hours component of the timezone.
     * @param minutes     The minutes component of the timezone.
     * @param zuluEnabled Determines whether Z is an allowable representation for zero offset.
     * @param plusEnabled Determines whether + should be displayed for zero and positive offsets.
     */
    private static String getTimeZoneString(int hours, int minutes, final boolean zuluEnabled, boolean plusEnabled)
    {
        StringBuilder buffer;

        if (hours < 0)
        {
            if (minutes < 0)
            {
                buffer = new StringBuilder(TIMEZONE_LENGTH);

                buffer.append('-');

                hours *= (-1);
                minutes *= (-1);
            }
            else if (minutes == 0)
            {
                buffer = new StringBuilder(TIMEZONE_LENGTH);

                buffer.append('-');

                hours *= (-1);
            }
            else
            {
                throw new RuntimeException();
            }
        }
        else if (hours == 0)
        {
            if (minutes < 0)
            {
                buffer = new StringBuilder(TIMEZONE_LENGTH);

                buffer.append('-');

                minutes *= (-1);
            }
            else if (minutes == 0)
            {
                if (zuluEnabled)
                {
                    return "Z";
                }
                else
                {
                    buffer = new StringBuilder(TIMEZONE_LENGTH);

                    if (plusEnabled)
                    {
                        buffer.append('+');
                    }

                    minutes *= (-1);
                }
            }
            else
            {
                buffer = new StringBuilder(TIMEZONE_LENGTH);

                if (plusEnabled)
                {
                    buffer.append('+');
                }
            }
        }
        else
        {
            if (minutes < 0)
            {
                throw new RuntimeException();
            }
            else
            {
                buffer = new StringBuilder(TIMEZONE_LENGTH);

                if (plusEnabled)
                {
                    buffer.append('+');
                }
            }
        }

        addTwoDigits(hours, buffer);
        buffer.append(':');
        addTwoDigits(minutes, buffer);

        return buffer.toString();
    }

    private static BigDecimal setCalendar(final Calendar calendar, final int astronomicalYear, final int month, final int day, final int hour, final int minute, final int integralSecond, final BigDecimal fractionalSecond, final int offset, final TimeZone defaultTimeZone)
    {
        if (DatatypeConstants.FIELD_UNDEFINED != offset)
        {
            calendar.setTimeZone(getTimeZone(offset));
        }
        else
        {
            if (null != defaultTimeZone)
            {
                calendar.setTimeZone(defaultTimeZone);
            }
            else
            {
                throw new IllegalArgumentException("defaultTimeZone cannot be null if offset is null");
            }
        }

        setYear(astronomicalYear, calendar);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, integralSecond);

        if (fractionalSecond.signum() != 0)
        {
            final BigDecimal millis = fractionalSecond.movePointRight(3);

            final int integralMillis = millis.intValue();

            calendar.set(Calendar.MILLISECOND, integralMillis);

            return millis.subtract(BigDecimal.valueOf(integralMillis)).movePointLeft(3);
        }
        else
        {
            calendar.set(Calendar.MILLISECOND, 0);

            return BigDecimal.ZERO;
        }
    }

    private static void setYear(final int astronomicalYear, final Calendar calendar)
    {
    	if (astronomicalYear > 0)
    	{
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
            calendar.set(Calendar.YEAR, astronomicalYear);
    	}
    	else
    	{
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
            calendar.set(Calendar.YEAR, 1 - astronomicalYear);
    	}
    }

}
