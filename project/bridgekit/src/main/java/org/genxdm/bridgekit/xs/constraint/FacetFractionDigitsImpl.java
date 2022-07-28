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

import java.math.BigDecimal;
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
            // old style used canonicalization then measured the string length after '.'
            // this means we have to care about variant presentation (which happens).
            // use scale instead, but do some dancing to make BD.scale() work properly.
            BigDecimal value = atomBridge.getDecimal(atom);
            // get the remainder after division by 1.0 to strip out the whole-number portion,
            // leaving only the fraction. bigdecimal preserves scale, though, so that new
            // bigdecimal has the same scale as the old, but while stripping zeros from the
            // original feels risky, stripping trailing zeros from 0.fractionalpart000000 is
            // quite safe, and gives correct results for 0, 1.0, 123456789.0, 1.00000,
            // and 100.012345000 100.01234500000 (the line above: 0, this line: 6).
            int scale = value.remainder(BigDecimal.ONE).stripTrailingZeros().scale();
            if (scale >= 0)
                return scale;
            return 0; // if it's negative, the stored (integral) value is multiplied by 10*abs(scale)
            // no assertions allowed here: it's either a decimal that has a value, which is legal, or
            // we aren't inside this bloc.
        }
        else
        {
            throw new AssertionError(nativeType); // fractionDigits is only legal for these two types.
        }
    }
}
