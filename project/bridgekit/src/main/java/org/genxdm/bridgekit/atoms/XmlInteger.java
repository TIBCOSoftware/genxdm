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

import java.math.BigInteger;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.NativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#integer">integer</a>.
 */
public final class XmlInteger extends XmlAbstractAtom
{
    public static XmlInteger valueOf(final BigInteger value)
    {
        return new XmlInteger(value);
    }

    /**
     * The {@link XmlInteger} constant zero.
     * 
     * @since 1.0
     */
    public static final XmlInteger ZERO = new XmlInteger(BigInteger.valueOf(0));

    /**
     * Initialize static constant array when class is loaded.
     */
    private final static int MAX_CONSTANT = 16;
    private static XmlInteger posConst[] = new XmlInteger[MAX_CONSTANT + 1];
    private static XmlInteger negConst[] = new XmlInteger[MAX_CONSTANT + 1];
    static
    {
        for (int i = 1; i <= MAX_CONSTANT; i++)
        {
            posConst[i] = new XmlInteger(BigInteger.valueOf(+i));
            negConst[i] = new XmlInteger(BigInteger.valueOf(-i));
        }
    }

    /**
     * The {@link XmlInteger} constant one.
     * 
     * @since 1.0
     */
    public static final XmlInteger ONE = valueOf(1);

    /**
     * The {@link XmlInteger} constant ten.
     * 
     * @since 1.0
     */
    public static final XmlInteger TEN = valueOf(10);

    public static XmlInteger valueOf(final long value)
    {
        if (value == 0)
        {
            return ZERO;
        }
        if (value > 0 && value <= MAX_CONSTANT)
        {
            return posConst[(int)value];
        }
        else if (value < 0 && value >= -MAX_CONSTANT)
        {
            return negConst[(int)-value];
        }
        else
        {
            return new XmlInteger(BigInteger.valueOf(value));
        }
    }

    private final BigInteger integerValue;

    private XmlInteger(final BigInteger integerValue)
    {
        this.integerValue = PreCondition.assertArgumentNotNull(integerValue, "integerValue");
    }

    @Override
    public boolean equals(final Object arg)
    {
        if (arg instanceof XmlInteger)
            return integerValue.equals(((XmlInteger)arg).integerValue);
        return false;
    }

    public String getC14NForm()
    {
        return integerValue.toString();
    }

    public NativeType getNativeType()
    {
        return NativeType.INTEGER;
    }

    @Override
    public int hashCode()
    {
        return integerValue.hashCode();
    }

    /**
     * Returns this value as a {@link BigInteger}.
     */
    public BigInteger getBigIntegerValue()
    {
        return integerValue;
    }

    public boolean isWhiteSpace()
    {
        return false;
    }
}
