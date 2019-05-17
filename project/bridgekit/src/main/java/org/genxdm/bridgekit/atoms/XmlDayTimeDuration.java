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
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#dayTimeDuration">dayTimeDuration</a>.
 */
public final class XmlDayTimeDuration 
    extends XmlAbstractAtom 
    implements Comparable<XmlDayTimeDuration>
{
    public XmlDayTimeDuration(final BigDecimal seconds)
    {
        this.seconds = PreCondition.assertArgumentNotNull(seconds, "seconds");
    }

    public int compareTo(final XmlDayTimeDuration other)
    {
        return seconds.compareTo(other.seconds);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof XmlDayTimeDuration)
            return seconds.equals(((XmlDayTimeDuration)obj).seconds);
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return seconds.hashCode();
    }

    public String getC14NForm()
    {
        return DurationSupport.formatDayTimeDurationC14N(seconds);
    }

    public NativeType getNativeType()
    {
        return NativeType.DURATION_DAYTIME;
    }

    public boolean isWhiteSpace()
    {
        return false;
    }

    public BigDecimal getTotalSecondsValue()
    {
        return seconds;
    }

    private final BigDecimal seconds;
}
