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

final class DurationSupport
{
    public static final int HOURS_PER_DAY = 24;
    public static final long HOURS_PER_DAY_LONG = 24l;

    public static final int MINUTES_PER_HOUR = 60;
    public static final long MINUTES_PER_HOUR_LONG = 60l;

    public static final int MONTHS_PER_YEAR = 12;
    public static final int SECONDS_PER_DAY = 86400;
    public static final long SECONDS_PER_DAY_LONG = 86400l;
    public static final int SECONDS_PER_HOUR = 3600;
    public static final long SECONDS_PER_HOUR_LONG = 3600l;

    public static final int SECONDS_PER_MINUTE = 60;
    public static final long SECONDS_PER_MINUTE_LONG = 60l;

    public static String formatDayTimeDurationC14N(final BigDecimal totalSeconds)
    {
        final boolean isNegative = totalSeconds.signum() < 0;

        final StringBuilder buffer = new StringBuilder();

        if (isNegative)
        {
            buffer.append('-');
        }

        buffer.append('P');

        int designators = 0;

        if (designators == 0)
        {
            // The canonical representation of dayTimeDuration restricts the value
            // of the hours component to integer values between 0 and 23, both inclusive; the
            // value of the minutes component to integer values between 0 and 59; both inclusive;
            // and the value of the seconds component to decimal valued from 0.0 to 60.999...
            // (see XML Schema Part 2: Datatypes], Appendx D). The value can be greater than 60
            // seconds to accomodate occasional leap seconds used to keep human time
            // synchronized with the rotation of the planet.

            BigDecimal time = isNegative ? totalSeconds.negate() : totalSeconds;

            final long integralSeconds = time == null ? 0 : time.longValue();

            long minutes = integralSeconds / SECONDS_PER_MINUTE_LONG;

            BigDecimal seconds = time.subtract(BigDecimal.valueOf(minutes * SECONDS_PER_MINUTE_LONG));

            long hours = minutes / MINUTES_PER_HOUR_LONG;
            minutes = minutes - hours * MINUTES_PER_HOUR_LONG;

            long days = hours / HOURS_PER_DAY_LONG;
            hours = hours - days * HOURS_PER_DAY_LONG;

            boolean isZero = (0 == seconds.signum());

            if (days != 0)
            {
                buffer.append(Long.toString(days)).append('D');

                designators++;

                if (hours == 0 && minutes == 0 && isZero)
                {
                    return buffer.toString();
                }
            }

            buffer.append('T');

            if (hours != 0)
            {
                buffer.append(Long.toString(hours)).append('H');

                designators++;
            }

            if (minutes != 0)
            {
                buffer.append(Long.toString(minutes)).append('M');

                designators++;
            }

            if (!isZero)
            {
                buffer.append(seconds.toPlainString());

                buffer.append('S');

                designators++;
            }

            if (designators == 0)
            {
                buffer.append('0').append('S');
            }
        }
        return buffer.toString();
    }

    public static String formatDurationC14N(final BigDecimal totalSeconds, final int totalMonths)
    {
        final boolean isNegative = (totalSeconds != null ? totalSeconds.signum() < 0 : false) || totalMonths < 0;

        final StringBuilder buffer = new StringBuilder();

        int months;

        if (isNegative)
        {
            months = totalMonths * -1;

            buffer.append('-');
        }
        else
        {
            months = totalMonths;
        }

        // The canonical representation calls of yearMonthDuration restricts the value of
        // the months component to integer values between 0 and 11, both inclusive.
        int year = months / MONTHS_PER_YEAR;

        int month = months - (year * MONTHS_PER_YEAR);

        buffer.append('P');

        int designators = 0;

        if (totalMonths != 0)
        {
            // If a component has the value zero (0) then the number and designator for
            // that component must be omitted.
            if (year > 0)
            {
                buffer.append(year);
                buffer.append('Y');

                designators++;
            }

            if (month > 0)
            {
                buffer.append(month);
                buffer.append('M');

                designators++;
            }

            // If the values is zero (0) months, the canonical form is "P0M".
            if (totalSeconds == null || totalSeconds.signum() == 0)
            {
                if (designators == 0)
                {
                    buffer.append('0').append('M');
                }
            }
        }

        final BigDecimal timeSpan = (totalSeconds == null) ? BigDecimal.ZERO : totalSeconds;
        if (designators == 0 || (totalSeconds != null && totalSeconds.signum() != 0))
        {
            // The canonical representation of dayTimeDuration restricts the value
            // of the hours component to integer values between 0 and 23, both inclusive; the
            // value of the minutes component to integer values between 0 and 59; both inclusive;
            // and the value of the seconds component to decimal valued from 0.0 to 60.999...
            // (see XML Schema Part 2: Datatypes], Appendx D). The value can be greater than 60
            // seconds to accomodate occasional leap seconds used to keep human time
            // synchronized with the rotation of the planet.

            final BigDecimal time = isNegative ? timeSpan.negate() : timeSpan;

            final long integralSeconds = time == null ? 0 : time.longValue();

            long minutes = integralSeconds / SECONDS_PER_MINUTE_LONG;

            BigDecimal seconds = time.subtract(BigDecimal.valueOf(minutes * SECONDS_PER_MINUTE_LONG));

            long hours = minutes / MINUTES_PER_HOUR_LONG;
            minutes = minutes - hours * MINUTES_PER_HOUR_LONG;

            long days = hours / HOURS_PER_DAY_LONG;
            hours = hours - days * HOURS_PER_DAY_LONG;

            boolean isZero = (0 == seconds.signum());

            if (days != 0)
            {
                buffer.append(Long.toString(days)).append('D');

                designators++;

                if (hours == 0 && minutes == 0 && isZero)
                {
                    return buffer.toString();
                }
            }

            buffer.append('T');

            if (hours != 0)
            {
                buffer.append(Long.toString(hours)).append('H');

                designators++;
            }

            if (minutes != 0)
            {
                buffer.append(Long.toString(minutes)).append('M');

                designators++;
            }

            if (!isZero)
            {
                buffer.append(seconds.toPlainString());

                buffer.append('S');

                designators++;
            }

            if (designators == 0)
            {
                buffer.append('0').append('S');
            }
        }
        return buffer.toString();
    }

    public static String formatYearMonthDurationC14N(final int totalMonths)
    {
        final StringBuilder sb = new StringBuilder();

        int months;

        if (totalMonths < 0)
        {
            months = totalMonths * -1;

            sb.append('-');
        }
        else
        {
            months = totalMonths;
        }

        sb.append('P');

        // The canonical representation calls of yearMonthDuration restricts the value of
        // the months component to integer values between 0 and 11, both inclusive.
        final int year = months / MONTHS_PER_YEAR;
        final int month = months % MONTHS_PER_YEAR;

        if (year > 0)
        {
            sb.append(year);
            sb.append('Y');

            if (month > 0)
            {
                sb.append(month);
                sb.append('M');
            }
        }
        else
        {
            // If the values is zero (0) months, the canonical form is "P0M".
            sb.append(month);
            sb.append('M');
        }

        return sb.toString();
    }

    private DurationSupport()
    {
    }
}
