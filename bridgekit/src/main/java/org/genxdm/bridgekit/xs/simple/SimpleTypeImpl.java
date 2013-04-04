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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.complex.TypeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.FacetEnumerationException;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.PatternException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

/**
 * A simple type, but not the Simple Ur-Type or the Atomic Ur-Type.
 */
public abstract class SimpleTypeImpl extends TypeImpl implements SimpleType
{
    protected abstract <A> List<A> compile(String initialValue, AtomBridge<A> bridge) throws DatatypeException;

    protected abstract <A> List<A> compile(String initialValue, final PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException;

    private static <A> void checkEnumerationFacets(final List<? extends A> actualValue, final SimpleType simpleType, final AtomBridge<A> atomBridge) throws DatatypeException
    {
        // Quickee optimization; there are no enumerations for xs:anySimpleType and xs:anyAtomicType.
        if (!simpleType.isSimpleUrType() && !simpleType.isAtomicUrType())
        {
            SimpleType currentType = simpleType;
            while (true)
            {
                if (currentType.hasEnumerations())
                {
                    boolean matched = false;
                    int enumCount = 0;
                    for (final EnumerationDefinition facet : currentType.getEnumerations())
                    {
                        enumCount++;
                        if (matchesValue(facet.getValue(atomBridge), actualValue, atomBridge))
                        {
                            matched = true;
                            break;
                        }
                        else
                        {
                            // Try the next enumeration facet.
                        }
                    }
                    if (enumCount > 0 && !matched)
                    {
                        final String literal = atomBridge.getC14NString(actualValue);
                        final FacetEnumerationException cause = new FacetEnumerationException(literal);
                        throw new DatatypeException(literal, currentType, cause);
                    }
                    // Once we've found and checked enumeration facets, we don't need to go further up the
                    // type hierarchy because enumerations in one step must be subsets of base enumerations.
                    return;
                }
                else
                {
                    final Type baseType = currentType.getBaseType();
                    if (baseType.isAtomicUrType())
                    {
                        return;
                    }
                    else if (baseType.isSimpleUrType())
                    {
                        return;
                    }
                    else
                    {
                        // We shouldn't get to the complex Ur-Type, because of the optimization,
                        // but if that is changed, this cast will also act as an assertion.
                        currentType = (SimpleType)baseType;
                    }
                }
            }
        }
    }

    protected static <A> void checkNonEnumerationFacets(final List<? extends A> actualValue, final SimpleType simpleType, final AtomBridge<A> atomBridge) throws DatatypeException
    {
        // check the value space facets, excluding enumeration (e.g. length, digits, bounds)
        SimpleType currentType = simpleType;
        while (!currentType.isNative())
        {
            if (currentType.hasFacets())
            {
                for (final Facet facet : currentType.getFacets())
                {
                    try
                    {
                        facet.validate(actualValue, simpleType, atomBridge);
                    }
                    catch (final FacetException e)
                    {
                        final String literal = atomBridge.getC14NString(actualValue);
                        throw new DatatypeException(literal, currentType, e);
                    }
                }
            }
            final Type baseType = currentType.getBaseType();
            if (baseType instanceof SimpleType)
            {
                currentType = (SimpleType)baseType;
            }
            else if (baseType instanceof SimpleUrType)
            {
                return;
            }
            else
            {
                throw new AssertionError(baseType);
            }
        }
    }

    /**
     * Checks pattern facets, walking up the type hierarchy and checking against each type.
     */
    protected static <A> void checkPatternFacets(final SimpleType simpleType, final String normalizedValue, final NameSource nameBridge) throws DatatypeException
    {
        // Quickee optimization; there are no patterns for xs:anySimpleType and xs:anyAtomicType.
        if (!simpleType.isSimpleUrType() && !simpleType.isAtomicUrType())
        {
            // check the lexical space facets (i.e. pattern)
            // When multiple xs:pattern facets are defined in a single derivation step, a value
            // is considered valid if it matches at least one of the patterns, meaning that a logical
            // or is performed on all the patterns defined in the same derivation step.

            SimpleType currentType = simpleType;
            while (true)
            {
                if (currentType.hasPatterns())
                {
                    // in the simple case, there's only one pattern at a derivation step,
                    // so it's the only thing that we can match (or fail). special case it,
                    // so that we can provide better reporting.
                    if (countIterable(currentType.getPatterns()) == 1)
                    {
                        try
                        {
                            currentType.getPatterns().iterator().next().validate(normalizedValue);
                        }
                        catch (final PatternException pe)
                        {
                            throw new DatatypeException(normalizedValue, simpleType, pe);
                        }
                    }
                    else
                    {
                        int size = 0;
                        int pass = 0;
                        for (final Pattern pattern : currentType.getPatterns())
                        {
                            size++;
                            try
                            {
                                pattern.validate(normalizedValue);
                                pass++;
                            }
                            catch (final PatternException e)
                            {
                                // Ignore.
                                // TODO: figure out how to accumulate usefully.
                            }
                        }
                        if (size > 0 && pass == 0)
                        {
                            // that is, we've never completed a pass, and we've made more than
                            // one (which is pretty much guaranteed ... we're inside hasPatterns(),
                            // so the whole size parameter is pretty useless, I think). we've looked
                            // at all the patterns, and none have matched. because we can't pass all
                            // the exceptions (failure indicators), don't pass any.
                            throw new DatatypeException(normalizedValue, simpleType);
                        }
                    }
                }
                final Type baseType = currentType.getBaseType();
                if (baseType.isAtomicUrType())
                {
                    return;
                }
                else if (baseType.isSimpleUrType())
                {
                    return;
                }
                else
                {
                    // We shouldn't get to the complex Ur-Type, because of the optimization,
                    // but if that is changed, this cast will also act as an assertion.
                    currentType = (SimpleType)baseType;
                }
            }
        }
    }

    protected static <A> void checkValueSpaceFacets(final List<? extends A> actualValue, final SimpleType simpleType, final AtomBridge<A> atomBridge) throws DatatypeException
    {
        checkNonEnumerationFacets(actualValue, simpleType, atomBridge);

        checkEnumerationFacets(actualValue, simpleType, atomBridge);
    }

    /**
     * The enumeration values are in the value space of the base type definition. The actual values may not be. In order to compare the values we have to do some upcasting. I don't like this and maybe there is a better way.
     */
    private static <A> boolean matchesValue(final List<? extends A> expect, final List<? extends A> actual, final AtomBridge<A> atomBridge)
    {
        final int size = expect.size();
        if (size == actual.size())
        {
            for (int index = 0; index < size; index++)
            {
                final A expectAtom = atomBridge.getNativeAtom(expect.get(index));
                final A actualAtom = atomBridge.getNativeAtom(actual.get(index));
                if (expectAtom.equals(actualAtom))
                {
                    // Keep going
                }
                else
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private final HashSet<EnumerationDefinition> m_enumerationFacets = new HashSet<EnumerationDefinition>();

    private final Facet[] m_facetArray = (Facet[])(Array.newInstance(Facet.class, FacetKind.values().length));

    private final HashSet<Facet> m_facets = new HashSet<Facet>();
    /**
     * {final} is mutable.
     */
    private final EnumSet<DerivationMethod> m_final = EnumSet.noneOf(DerivationMethod.class);

    private final Set<DerivationMethod> m_finalUnmodifiable = Collections.unmodifiableSet(m_final);

    private boolean m_isAbstract = false;

    private final HashSet<Pattern> m_patternFacets = new HashSet<Pattern>();

    protected final WhiteSpacePolicy m_whiteSpace;

    public SimpleTypeImpl(final QName name, final boolean isAnonymous, final ScopeExtent scope, final DerivationMethod derivation, final WhiteSpacePolicy whiteSpace)
    {
        super(name, isAnonymous, scope, derivation);
        this.m_whiteSpace = whiteSpace;
    }

    public void accept(SequenceTypeVisitor visitor)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public void addEnumeration(final EnumerationDefinition enumeration)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(enumeration, "enumeration");
        m_enumerationFacets.add(enumeration);
    }

    public void addFacet(final Facet facet)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(facet, "facet");
        m_facets.add(facet);
        m_facetArray[facet.getKind().ordinal()] = facet;
    }

    public void addPattern(final Pattern pattern)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(pattern, "pattern");
        m_patternFacets.add(pattern);
    }

    public SequenceType atomSet()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Type getBaseType()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public final Iterable<EnumerationDefinition> getEnumerations()
    {
        return m_enumerationFacets;
    }

    public Facet getFacetOfKind(final FacetKind facetKind)
    {
        return m_facetArray[facetKind.ordinal()];
    }

    public final Iterable<Facet> getFacets()
    {
        return m_facets;
    }

    public final Set<DerivationMethod> getFinal()
    {
        return m_finalUnmodifiable;
    }

    public final Iterable<Pattern> getPatterns()
    {
        return m_patternFacets;
    }

    public SimpleType getNativeTypeDefinition()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public final boolean hasEnumerations()
    {
        return m_enumerationFacets.size() > 0;
    }

    public boolean hasFacetOfKind(FacetKind facetKind)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public final boolean hasFacets()
    {
        return m_facets.size() > 0;
    }

    public final boolean hasPatterns()
    {
        return m_patternFacets.size() > 0;
    }

    public final boolean isAbstract()
    {
        return m_isAbstract;
    }

    public final boolean isAtomicUrType()
    {
        return false;
    }

    public final boolean isComplexUrType()
    {
        return false;
    }

    public final boolean isFinal(final DerivationMethod derivation)
    {
        PreCondition.assertArgumentNotNull(derivation, "derivation");
        return m_final.contains(derivation);
    }

    public final boolean isSimpleUrType()
    {
        return false;
    }

    public PrimeType prime()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Quantifier quantifier()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public final void setAbstract(final boolean isAbstract)
    {
        assertNotLocked();
        m_isAbstract = isAbstract;
    }

    public final void setFinal(final DerivationMethod derivation, final boolean enabled)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(derivation, "derivation");
        PreCondition.assertTrue(derivation.isUnion() || derivation.isList() || derivation.isRestriction(), "derivation (" + derivation + ") must be union, list or restriction for a simple type");
        if (enabled)
        {
            m_final.add(derivation);
        }
        else
        {
            m_final.remove(derivation);
        }
    }

    public <A> List<A> validate(final List<? extends A> value, AtomBridge<A> atomBridge) throws DatatypeException
    {
        // TODO: Can we attempt working in the value space and then fall back to the lexical space?
        final String initialValue = atomBridge.getC14NString(value);

        // normalize (handle whitespace pseudo-facet) first.
        final String normalizedValue = normalize(initialValue);

        checkPatternFacets(this, normalizedValue, NameSource.SINGLETON);

        // compile - which will perform various forms of validation, for types w/o facets
        final List<A> actualValue = compile(normalizedValue, atomBridge);

        checkValueSpaceFacets(actualValue, this, atomBridge);

        return actualValue;
    }

    /**
     * This method can only be called on simple types.
     */
    public final <A> List<A> validate(final String initialValue, AtomBridge<A> atomBridge) throws DatatypeException
    {
        PreCondition.assertArgumentNotNull(initialValue, "initialValue");

        // normalize (handle whitespace pseudo-facet) first.
        final String normalizedValue = normalize(initialValue);

        checkPatternFacets(this, normalizedValue, NameSource.SINGLETON);

        // compile - which will perform various forms of validation, for types w/o facets
        final List<A> actualValue = compile(normalizedValue, atomBridge);

        checkValueSpaceFacets(actualValue, this, atomBridge);

        return actualValue;
    }

    public <A> List<A> validate(final String initialValue, final PrefixResolver resolver, AtomBridge<A> atomBridge) throws DatatypeException
    {
        PreCondition.assertArgumentNotNull(initialValue, "initialValue");

        // normalize (handle whitespace pseudo-facet) first.
        final String normalizedValue = normalize(initialValue);

        checkPatternFacets(this, normalizedValue, NameSource.SINGLETON);

        // compile - which will perform various forms of validation, for types w/o facets
        final List<A> actualValue = compile(normalizedValue, resolver, atomBridge);

        checkValueSpaceFacets(actualValue, this, atomBridge);

        return actualValue;
    }
    
    private static <X> int countIterable(Iterable<X> iterable)
    {
        int count = 0;
        for (X x : iterable)
            count++;
        return count;
    }
}
