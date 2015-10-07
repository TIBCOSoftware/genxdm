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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.NativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#duration">duration</a>.
 */
public final class XmlDuration extends XmlAbstractAtom
{
    private final int months;
    private final BigDecimal seconds;

    public XmlDuration(final int months, final BigDecimal seconds)
    {
        this.months = months;
        this.seconds = PreCondition.assertArgumentNotNull(seconds, "seconds");
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof XmlDuration)
        {
            final XmlDuration other = (XmlDuration)obj;
            return months == other.getTotalMonthsValue() && seconds.equals(other.getTotalSecondsValue());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return (17 + months) * 31 + seconds.hashCode();
    }
    
    public String getC14NForm()
    {
        return DurationSupport.formatDurationC14N(seconds, months);
    }

    public NativeType getNativeType()
    {
        return NativeType.DURATION;
    }

    public boolean isWhiteSpace()
    {
        return false;
    }

    public int getTotalMonthsValue()
    {
        return months;
    }

    public BigDecimal getTotalSecondsValue()
    {
        return seconds;
    }
}
