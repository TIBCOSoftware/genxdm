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
package org.genxdm.bridgekit.xs.simple;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;

public final class DecimalType extends AbstractAtomType
{
    public static boolean DECIMAL_ALLOWS_EXPONENT = false;
    public DecimalType(final QName name, final SimpleType baseType)
    {
        super(name, baseType);
    }

    public void accept(SequenceTypeVisitor visitor)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Iterable<EnumerationDefinition> getEnumerations()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Facet getFacetOfKind(FacetKind facetKind)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Iterable<Facet> getFacets()
    {
        return Collections.emptyList();
    }

    public Set<DerivationMethod> getFinal()
    {
        return Collections.emptySet();
    }

    public NativeType getNativeType()
    {
        return NativeType.DECIMAL;
    }

    public Iterable<Pattern> getPatterns()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public ScopeExtent getScopeExtent()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public WhiteSpacePolicy getWhiteSpacePolicy()
    {
        return WhiteSpacePolicy.COLLAPSE;
    }

    public boolean hasEnumerations()
    {
        return false;
    }

    public boolean hasFacetOfKind(final FacetKind facetKind)
    {
        return false;
    }

    public boolean hasFacets()
    {
        return false;
    }

    public boolean hasPatterns()
    {
        return false;
    }

    public boolean isAbstract()
    {
        return false;
    }

    public boolean isID()
    {
        return false;
    }

    public boolean isIDREF()
    {
        return false;
    }

    public <A> List<A> validate(final String initialValue, AtomBridge<A> atomBridge) throws DatatypeException
    {
        try
        {
            final String trimmed = scale(trim(initialValue), this);

            if (0 == trimmed.length())
            {
                throw new DatatypeException(initialValue, this);
            }
            else
            {
                return atomBridge.wrapAtom(atomBridge.createDecimal(new BigDecimal(trimmed)));
            }
        }
        catch (final NumberFormatException e)
        {
            throw new DatatypeException(initialValue, this);
        }
    }

    public <A> List<A> validate(String initialValue, PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException
    {
        return validate(initialValue, bridge);
    }

    private static String scale(String value, final SimpleType type) throws DatatypeException
    {
        boolean allowExponential = DECIMAL_ALLOWS_EXPONENT;
        int exponential = value.indexOf('e');
        if (exponential < 0)
        {
            exponential = value.indexOf('E');
        }

        boolean hasExponential = exponential >= 0;
        if (!allowExponential && hasExponential)
        {
            throw new DatatypeException(value, type);
        }
        int dotIdx = value.indexOf('.');
        if (dotIdx >= 0 && (!hasExponential || dotIdx < exponential))
        {
            int last = (hasExponential ? exponential : value.length()) - 1;
            if (value.charAt(last) == '0')
            {
                do
                {
                    last--;
                }
                while (value.charAt(last) == '0');
                if (value.charAt(last) == '.')
                {
                    last--;
                    if (last < 0)
                    {
                        last = 0;
                        value = '0' + value;
                        if (exponential > 0)
                        {
                            exponential++;
                        }
                    }
                }
                if (hasExponential)
                {
                    value = value.substring(0, last + 1) + value.substring(exponential);
                }
                else
                {
                    value = value.substring(0, last + 1);
                }
            }
        }
        return value;
    }
    
}
