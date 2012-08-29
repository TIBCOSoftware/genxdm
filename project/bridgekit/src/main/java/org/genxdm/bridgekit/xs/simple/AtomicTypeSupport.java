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
import java.math.BigInteger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.NativeType;

public final class AtomicTypeSupport
{
    public static String formatBase64BinaryC14N(final byte[] bytes, final boolean dontBreakLines)
    {
        return Base64Codec.encodeBase64(bytes, dontBreakLines);
    }

    public static String formatQNameC14N(final String localName, final String prefix)
    {
        PreCondition.assertArgumentNotNull(localName, "localName");
        PreCondition.assertArgumentNotNull(prefix, "prefix");
        if (prefix.length() > 0)
        {
            return prefix.concat(":").concat(localName);
        }
        else
        {
            return localName;
        }
    }

    public static String formatBooleanC14N(final boolean booleanValue)
    {
        return booleanValue ? "true" : "false";
    }

    public static String formatGregorianC14N(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fractionalSecond, final int timezone, final NativeType nativeType)
    {
        // TODO: Change this so that we don't have to create the calendar data structure.
        final int millis = (null != fractionalSecond) ? fractionalSecond.movePointRight(3).intValue() : 0;
        final XMLGregorianCalendar calendar = getFactory().newXMLGregorianCalendar(year, month, day, hour, minute, second, millis, timezone);
        switch (nativeType)
        {
            case DATE:
            {
                return GregorianFormat.date.format(calendar);
            }
            case DATETIME:
            {
                return GregorianFormat.dateTime.format(calendar);
            }
            case TIME:
            {
                return GregorianFormat.time.format(calendar);
            }
            case GYEARMONTH:
            {
                return GregorianFormat.gYearMonth.format(calendar);
            }
            case GYEAR:
            {
                return GregorianFormat.gYear.format(calendar);
            }
            case GMONTHDAY:
            {
                return GregorianFormat.gMonthDay.format(calendar);
            }
            case GDAY:
            {
                return GregorianFormat.gDay.format(calendar);
            }
            case GMONTH:
            {
                return GregorianFormat.gMonth.format(calendar);
            }
            default:
            {
                throw new AssertionError(nativeType);
            }
        }
    }

    private static DatatypeFactory getFactory()
    {
        try
        {
            return DatatypeFactory.newInstance();
        }
        catch (final DatatypeConfigurationException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String formatByteC14N(final byte byteValue)
    {
        return Byte.toString(byteValue);
    }

    public static String formatDayTimeDurationC14N(final BigDecimal seconds)
    {
        return DurationSupport.formatDayTimeDurationC14N(seconds);
    }

    public static String formatDecimalC14N(final BigDecimal decval)
    {
        return NumericSupport.formatDecimalC14N(decval);
    }

    public static String formatDoubleC14N(final double doubleValue)
    {
        return NumericSupport.formatDoubleC14N(doubleValue);
    }

    public static String formatDurationC14N(final int months, final BigDecimal seconds)
    {
        return DurationSupport.formatDurationC14N(seconds, months);
    }

    public static String formatFloatC14N(final float floatValue)
    {
        return NumericSupport.formatFloatC14N(floatValue);
    }

    public static String formatHexBinaryC14N(final byte[] bytes)
    {
        return HexCodec.encodeHex(bytes);
    }

    public static String formatIntC14N(final int intValue)
    {
        return Integer.toString(intValue);
    }

    public static String formatIntegerC14N(final BigInteger integerValue)
    {
        return integerValue.toString();
    }

    public static String formatLongC14N(final long longValue)
    {
        return Long.toString(longValue);
    }

    public static String formatShortC14N(final short shortValue)
    {
        return Short.toString(shortValue);
    }

    public static String formatYearMonthDurationC14N(final int months)
    {
        return DurationSupport.formatYearMonthDurationC14N(months);
    }

    private AtomicTypeSupport()
    {
        throw new AssertionError();
    }
}
