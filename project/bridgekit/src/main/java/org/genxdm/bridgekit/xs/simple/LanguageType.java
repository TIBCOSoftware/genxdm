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

public final class LanguageType extends AbstractAtomType
{
    private static boolean isValidBlock(final String language, final int start, final int end, final boolean noNumbers)
    {
        final int len = end - start;
        if (len > 8 || len == 0)
        {
            return false;// empty or longer than 8
        }
        for (int i = start; i < end; i++)
        {
            char c = language.charAt(i);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (noNumbers || c >= '0' && c <= '9'))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: Redesign to give better description of errors.
     */
    private static boolean isValidLanguage(final String language)
    {
        if (language.length() > 0)
        {
            if (language.charAt(0) == '-')
            {
                return false;
            }

            int lastDashIdx = 0;
            boolean noNumbers = true;
            do
            {
                int dashIdx = language.indexOf('-', lastDashIdx);
                if (dashIdx < 0)
                {
                    return isValidBlock(language, lastDashIdx, language.length(), noNumbers);
                }
                if (!isValidBlock(language, lastDashIdx, dashIdx, noNumbers))
                {
                    return false;
                }
                noNumbers = false;
                lastDashIdx = dashIdx + 1;
            }
            while (true);
        }
        else
        {
            return false;
        }
    }

    public LanguageType(final QName name, final SimpleType baseType)
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
        return NativeType.LANGUAGE;
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
        final String normalized = normalize(initialValue);
        if (isValidLanguage(normalized))
        {
            return atomBridge.wrapAtom(atomBridge.createStringDerived(normalized, NativeType.LANGUAGE));
        }
        else
        {
            throw new DatatypeException(normalized, this);
        }
    }

    public <A> List<A> validate(String initialValue, PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException
    {
        return validate(initialValue, bridge);
    }
}
