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
package org.genxdm.bridgekit.xs.constraint;

import java.util.List;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.FacetFractionDigitsException;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.FractionDigits;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;

public final class FacetFractionDigitsImpl extends FacetImpl implements FractionDigits
{
	private final int fractionDigits;

	public FacetFractionDigitsImpl(final int fractionDigits, final boolean isFixed)
	{
		super(isFixed);
		this.fractionDigits = fractionDigits;
	}

	public FacetKind getKind()
	{
		return FacetKind.FractionDigits;
	}

	public int getFractionDigits()
	{
		return fractionDigits;
	}

	public <A> void validate(final List<? extends A> actualValue, final SimpleType simpleType, AtomBridge<A> atomBridge) throws FacetException
	{
		for (final A atom : actualValue)
		{
			if (fractionDigits(atom, atomBridge) > fractionDigits)
			{
				final String displayString = atomBridge.getC14NString(actualValue);
				throw new FacetFractionDigitsException(displayString, this);
			}
		}
	}

	private static <A> int fractionDigits(final A atom, final AtomBridge<A> atomBridge)
	{
		final NativeType nativeType = atomBridge.getNativeType(atom);
		if (nativeType.isInteger())
		{
			return 0;
		}
		else if (nativeType.isDecimal())
		{
			final String s = atomBridge.getC14NForm(atom);
			if (s.endsWith(".0"))
			{
				return 0;
			}
			else
			{
				final int decimalPoint = s.indexOf('.');
				if (decimalPoint >= 0)
				{
					return s.length() - decimalPoint - 1;
				}
				else
				{
					throw new AssertionError(nativeType);
				}
			}
		}
		else
		{
			throw new AssertionError(nativeType);
		}
	}
}
