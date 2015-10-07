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

import org.genxdm.xs.types.NativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#yearMonthDuration">yearMonthDuration</a>.
 */
public final class XmlYearMonthDuration extends XmlAbstractAtom implements Comparable<XmlYearMonthDuration>
{
    public static XmlYearMonthDuration valueOf(final int months)
    {
        return new XmlYearMonthDuration(months);
    }

    private final int months;

    private XmlYearMonthDuration(final int months)
    {
        this.months = months;
    }

    public int compareTo(final XmlYearMonthDuration other)
    {
        return months - other.months;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof XmlYearMonthDuration)
            return months == ((XmlYearMonthDuration)obj).months;
        return false;
    }

    public String getC14NForm()
    {
        return DurationSupport.formatYearMonthDurationC14N(months);
    }

    public NativeType getNativeType()
    {
        return NativeType.DURATION_YEARMONTH;
    }

    public int getTotalMonthsValue()
    {
        return months;
    }

    @Override
    public int hashCode()
    {
        return months;
    }

    public boolean isWhiteSpace()
    {
        return false;
    }
}
