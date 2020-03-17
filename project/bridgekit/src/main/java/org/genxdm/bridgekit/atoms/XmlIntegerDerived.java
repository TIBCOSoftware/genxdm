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

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.NativeType;

public final class XmlIntegerDerived 
    extends XmlAbstractAtom
{
    public static XmlIntegerDerived valueOf(final BigInteger integerValue, final NativeType nativeType)
    {
        return new XmlIntegerDerived(integerValue, nativeType);
    }

    public static XmlIntegerDerived valueOf(final long integerValue, final NativeType nativeType)
    {
        return new XmlIntegerDerived(BigInteger.valueOf(integerValue), nativeType);
    }

    private XmlIntegerDerived(final BigInteger integerValue, final NativeType nativeType)
    {
        this.value = PreCondition.assertArgumentNotNull(integerValue, "integerValue");
        this.type = PreCondition.assertArgumentNotNull(nativeType, "nativeType");
        // bounds checking
        switch (nativeType)
        {
            case NEGATIVE_INTEGER:
                if (integerValue.compareTo(BigInteger.ZERO) >= 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' + "+integerValue.toString());
                break;
            case NON_POSITIVE_INTEGER:
                if (integerValue.compareTo(BigInteger.ZERO) > 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' + "+integerValue.toString());
                break;
            case POSITIVE_INTEGER:
                if (integerValue.compareTo(BigInteger.ZERO) <= 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' : "+integerValue.toString());
                break;
            case NON_NEGATIVE_INTEGER:
            case UNSIGNED_LONG:
            case UNSIGNED_INT:
            case UNSIGNED_SHORT:
            case UNSIGNED_BYTE:
                if (integerValue.compareTo(BigInteger.ZERO) < 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' : "+integerValue.toString());
                break;
            default:
        }
        switch (nativeType)
        {
            case UNSIGNED_LONG:
                if (integerValue.compareTo(CastingSupport.UNSIGNED_LONG_MAX_VALUE) > 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' : "+integerValue.toString());
                break;
            case UNSIGNED_INT:
                if (integerValue.compareTo(CastingSupport.UNSIGNED_INT_MAX_VALUE) > 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' : "+integerValue.toString());
                break;
            case UNSIGNED_SHORT:
                if (integerValue.compareTo(CastingSupport.UNSIGNED_SHORT_MAX_VALUE) > 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' : "+integerValue.toString());
                break;
            case UNSIGNED_BYTE:
                if (integerValue.compareTo(CastingSupport.UNSIGNED_BYTE_MAX_VALUE) > 0)
                    throw new GenXDMException("Invalid value for type '"+nativeType.toQName()+"' : "+integerValue.toString());
                break;
            default:
        }
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived x = (XmlIntegerDerived)obj;
            return type == x.type && value.equals(x.value);
        }
        return false;
    }

    public String getC14NForm()
    {
        return value.toString();
    }

    public NativeType getNativeType()
    {
        return type;
    }

    @Override
    public int hashCode()
    {
        return (17 + value.hashCode()) * 31 + type.hashCode();
    }

    public BigInteger integerValue()
    {
        return value;
    }

    public int intValue()
    {
        return value.intValue();
    }

    public boolean isWhiteSpace()
    {
        return false;
    }

    public long longValue()
    {
        return value.longValue();
    }

    public short shortValue()
    {
        return value.shortValue();
    }

    private final NativeType type;

    private final BigInteger value;
}
