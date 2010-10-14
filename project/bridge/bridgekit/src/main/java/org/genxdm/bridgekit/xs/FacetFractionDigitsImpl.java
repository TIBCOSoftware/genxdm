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
package org.genxdm.bridgekit.xs;

import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.SmFacetException;
import org.genxdm.xs.exceptions.SmFacetFractionDigitsException;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmFractionDigits;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;

public final class FacetFractionDigitsImpl<A> extends FacetImpl<A> implements SmFractionDigits<A>
{
	private final AtomBridge<A> atomBridge;
	private final int fractionDigits;

	public FacetFractionDigitsImpl(final int fractionDigits, final boolean isFixed, final AtomBridge<A> atomBridge)
	{
		super(isFixed);
		this.fractionDigits = fractionDigits;
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");

	}

	public SmFacetKind getKind()
	{
		return SmFacetKind.FractionDigits;
	}

	public int getFractionDigits()
	{
		return fractionDigits;
	}

	public void validate(final List<? extends A> actualValue, final SmSimpleType<A> simpleType) throws SmFacetException
	{
		for (final A atom : actualValue)
		{
			if (fractionDigits(atom, atomBridge) > fractionDigits)
			{
				final String displayString = atomBridge.getC14NString(actualValue);
				throw new SmFacetFractionDigitsException(displayString, this);
			}
		}
	}

	private static <A> int fractionDigits(final A atom, final AtomBridge<A> atomBridge)
	{
		final SmNativeType nativeType = atomBridge.getNativeType(atom);
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
