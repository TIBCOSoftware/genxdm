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

import java.math.BigInteger;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.types.SmNativeType;

public final class XmlIntegerDerived extends XmlAbstractAtom
{
	public static XmlIntegerDerived valueOf(final BigInteger integerValue, final SmNativeType nativeType)
	{
		return new XmlIntegerDerived(integerValue, nativeType);
	}

	public static XmlIntegerDerived valueOf(final long integerValue, final SmNativeType nativeType)
	{
		return new XmlIntegerDerived(BigInteger.valueOf(integerValue), nativeType);
	}

	private final SmNativeType type;

	private final BigInteger value;

	private XmlIntegerDerived(final BigInteger integerValue, final SmNativeType nativeType)
	{
		this.value = PreCondition.assertArgumentNotNull(integerValue, "integerValue");
		this.type = PreCondition.assertArgumentNotNull(nativeType, "nativeType");
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof XmlIntegerDerived)
		{
			final XmlIntegerDerived x = (XmlIntegerDerived)obj;
			return type == x.type && value.equals(x.value);
		}
		else
		{
			return false;
		}
	}

	public String getC14NForm()
	{
		return value.toString();
	}

	public SmNativeType getNativeType()
	{
		return type;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
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
}
