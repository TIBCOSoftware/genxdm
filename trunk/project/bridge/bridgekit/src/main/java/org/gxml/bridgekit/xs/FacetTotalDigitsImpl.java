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
package org.gxml.bridgekit.xs;

import java.math.BigInteger;
import java.util.List;

import org.gxml.exceptions.PreCondition;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.exceptions.SmFacetException;
import org.gxml.xs.exceptions.SmFacetTotalDigitsException;
import org.gxml.xs.facets.SmFacetKind;
import org.gxml.xs.facets.SmTotalDigits;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmSimpleType;

public final class FacetTotalDigitsImpl<A> extends FacetImpl<A> implements SmTotalDigits<A>
{
	private static <A> int totalDigits(final A atom, final AtomBridge<A> atomBridge)
	{
		final SmNativeType nativeType = atomBridge.getNativeType(atom);
		if (nativeType == SmNativeType.BYTE)
		{
			final byte value = atomBridge.getByte(atom);
			if (value < 0)
			{
				return Byte.toString(value).length() - 1;
			}
			else
			{
				return Byte.toString(value).length();
			}
		}
		else if (nativeType == SmNativeType.SHORT)
		{
			final short value = atomBridge.getShort(atom);
			if (value < 0)
			{
				return Short.toString(value).length() - 1;
			}
			else
			{
				return Short.toString(value).length();
			}
		}
		else if (nativeType == SmNativeType.INT)
		{
			final int value = atomBridge.getInt(atom);
			if (value < 0)
			{
				return Integer.toString(value).length() - 1;
			}
			else
			{
				return Integer.toString(value).length();
			}
		}
		else if (nativeType == SmNativeType.LONG)
		{
			final long value = atomBridge.getLong(atom);
			if (value < 0)
			{
				return Long.toString(value).length() - 1;
			}
			else
			{
				return Long.toString(value).length();
			}
		}
		else if (nativeType.isInteger())
		{
			final BigInteger value = atomBridge.getInteger(atom);
			if (value.signum() < 0)
			{
				return value.toString().length() - 1;
			}
			else
			{
				return value.toString().length();
			}
		}
		else if (nativeType.isDecimal())
		{
			return totalDigits(atomBridge.getC14NForm(atom));
		}
		else
		{
			throw new AssertionError(nativeType);
		}
	}

	private static int totalDigits(final String strval)
	{
		final int stringLength = strval.length();

		int decimalPoint = 0; // 1=exists, 0=doesn't
		int leadingZeros = 0;

		final int minusSign; // 1=exists, 0=doesn't
		if (strval.startsWith("-"))
		{
			minusSign = 1;
		}
		else
		{
			minusSign = 0;
		}
		final int upperBound = stringLength - 1;
		for (int i = minusSign; i <= upperBound; i++)
		{
			final char chLHS = strval.charAt(i);
			if (chLHS == '0')
			{
				leadingZeros++;
			}
			else
			{
				if (chLHS >= '1' && chLHS <= '9')
				{
					for (; i <= upperBound; i++)
					{
						if (strval.charAt(i) == '.')
						{
							decimalPoint++;
							break;
						}
					}
					// Fall through
				}
				else if (chLHS == '.')
				{
					decimalPoint++;
					// Fall through
				}
				else
				{
					throw new AssertionError("chLHS=" + chLHS);
				}
				if (decimalPoint == 0)
				{
					return stringLength - (minusSign + leadingZeros);
				}
				else
				{
					int trailingZeros = 0;
					for (int j = upperBound; j > i; j--)
					{
						final char chRHS = strval.charAt(j);
						if (chRHS == '0')
						{
							trailingZeros++;
						}
						else
						{
							if (chRHS >= '1' && chRHS <= '9')
							{
								return stringLength - (minusSign + leadingZeros + decimalPoint + trailingZeros);
							}
							else
							{
								throw new AssertionError("chRHS=" + chRHS);
							}
						}
					}
					final int ignorable = minusSign + leadingZeros + decimalPoint + trailingZeros;
					if (ignorable == stringLength)
					{
						return 1; // "0.0" and similar variations with zeros
					}
					else
					{
						return stringLength - ignorable;
					}
				}
			}
		}
		PreCondition.assertTrue(stringLength == minusSign + leadingZeros, "totalDigits('" + strval + "'), leadingZeros=" + leadingZeros);
		return 1;
	}

	private final AtomBridge<A> atomBridge;

	private final int totalDigits;

	public FacetTotalDigitsImpl(final int totalDigits, final boolean isFixed, final AtomBridge<A> atomBridge)
	{
		super(isFixed);
		this.totalDigits = totalDigits;
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
	}

	public SmFacetKind getKind()
	{
		return SmFacetKind.TotalDigits;
	}

	public int getTotalDigits()
	{
		return totalDigits;
	}

	public void validate(final List<? extends A> actualValue, final SmSimpleType<A> simpleType) throws SmFacetException
	{
		for (final A atom : actualValue)
		{
			if (totalDigits(atom, atomBridge) > this.totalDigits)
			{
				final String displayString = atomBridge.getC14NString(actualValue);
				throw new SmFacetTotalDigitsException(this.totalDigits, displayString, this);
			}
		}
	}
}
