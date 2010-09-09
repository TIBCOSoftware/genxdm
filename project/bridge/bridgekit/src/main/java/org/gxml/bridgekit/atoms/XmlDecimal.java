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

import java.math.BigDecimal;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.types.SmNativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#decimal">decimal</a>.
 */
public final class XmlDecimal extends XmlAbstractAtom
{
	// TODO: Caching of common values.
	private final BigDecimal decimalValue;

	public static XmlDecimal valueOf(final BigDecimal decimalValue)
	{
		return new XmlDecimal(decimalValue);
	}

	public static XmlDecimal valueOf(final long decimalValue)
	{
		return new XmlDecimal(decimalValue);
	}

	private XmlDecimal(final BigDecimal decimalValue)
	{
		this.decimalValue = PreCondition.assertArgumentNotNull(decimalValue, "decimalValue");
	}

	private XmlDecimal(final long decimalValue)
	{
		this.decimalValue = BigDecimal.valueOf(decimalValue);
	}

	/**
	 * Returns this value as a {@link BigDecimal}.
	 */
	public BigDecimal getBigDecimalValue()
	{
		return decimalValue;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof XmlDecimal)
		{
			return decimalValue.equals(((XmlDecimal)obj).decimalValue);
		}
		else
		{
			return false;
		}
	}

	public String getC14NForm()
	{
		return NumericSupport.formatDecimalC14N(decimalValue);
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.DECIMAL;
	}

	@Override
	public int hashCode()
	{
		return decimalValue.hashCode();
	}

	public boolean isWhiteSpace()
	{
		return false;
	}
}
