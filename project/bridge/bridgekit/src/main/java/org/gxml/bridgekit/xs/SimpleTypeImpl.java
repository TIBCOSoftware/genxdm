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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.names.NameSource;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.components.SmEnumeration;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmQuantifier;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.enums.SmWhiteSpacePolicy;
import org.gxml.xs.exceptions.SmDatatypeException;
import org.gxml.xs.exceptions.SmFacetEnumerationException;
import org.gxml.xs.exceptions.SmFacetException;
import org.gxml.xs.exceptions.SmPatternException;
import org.gxml.xs.facets.SmFacet;
import org.gxml.xs.facets.SmFacetKind;
import org.gxml.xs.facets.SmPattern;
import org.gxml.xs.resolve.SmPrefixResolver;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmSequenceType;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmSimpleUrType;
import org.gxml.xs.types.SmType;

/**
 * A simple type, but not the Simple Ur-Type or the Atomic Ur-Type.
 */
public abstract class SimpleTypeImpl<A> extends TypeImpl<A> implements SmSimpleType<A>
{
	protected abstract List<A> compile(String initialValue) throws SmDatatypeException;

	protected abstract List<A> compile(String initialValue, final SmPrefixResolver resolver) throws SmDatatypeException;

	private static <A> void checkEnumerationFacets(final List<? extends A> actualValue, final SmSimpleType<A> simpleType, final AtomBridge<A> atomBridge) throws SmDatatypeException
	{
		// Quickee optimization; there are no enumerations for xs:anySimpleType and xs:anyAtomicType.
		if (!simpleType.isSimpleUrType() && !simpleType.isAtomicUrType())
		{
			SmSimpleType<A> currentType = simpleType;
			while (true)
			{
				if (currentType.hasEnumerations())
				{
					boolean matched = false;
					int enumCount = 0;
					for (final SmEnumeration<A> facet : currentType.getEnumerations())
					{
						enumCount++;
						if (matchesValue(facet.getValue(), actualValue, atomBridge))
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
						final SmFacetEnumerationException cause = new SmFacetEnumerationException(literal);
						throw new SmDatatypeException(literal, currentType, cause);
					}
					// Once we've found and checked enumeration facets, we don't need to go further up the
					// type hierarchy because enumerations in one step must be subsets of base enumerations.
					return;
				}
				else
				{
					final SmType<A> baseType = currentType.getBaseType();
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
						currentType = (SmSimpleType<A>)baseType;
					}
				}
			}
		}
	}

	protected static <A> void checkNonEnumerationFacets(final List<? extends A> actualValue, final SmSimpleType<A> simpleType, final AtomBridge<A> atomBridge) throws SmDatatypeException
	{
		// check the value space facets, excluding enumeration (e.g. length, digits, bounds)
		SmSimpleType<A> currentType = simpleType;
		while (!currentType.isNative())
		{
			if (currentType.hasFacets())
			{
				for (final SmFacet<A> facet : currentType.getFacets())
				{
					try
					{
						facet.validate(actualValue, simpleType);
					}
					catch (final SmFacetException e)
					{
						final String literal = atomBridge.getC14NString(actualValue);
						throw new SmDatatypeException(literal, currentType);
					}
				}
			}
			final SmType<A> baseType = currentType.getBaseType();
			if (baseType instanceof SmSimpleType<?>)
			{
				currentType = (SmSimpleType<A>)baseType;
			}
			else if (baseType instanceof SmSimpleUrType<?>)
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
	protected static <A> void checkPatternFacets(final SmSimpleType<A> simpleType, final String normalizedValue, final NameSource nameBridge) throws SmDatatypeException
	{
		// Quickee optimization; there are no patterns for xs:anySimpleType and xs:anyAtomicType.
		if (!simpleType.isSimpleUrType() && !simpleType.isAtomicUrType())
		{
			// check the lexical space facets (i.e. pattern)
			// When multiple xs:pattern facets are defined in a single derivation step, a value
			// is considered valid if it matches at least one of the patterns, meaning that a logical
			// or is performed on all the patterns defined in the same derivation step.

			SmSimpleType<A> currentType = simpleType;
			while (true)
			{
				if (currentType.hasPatterns())
				{
					int size = 0;
					int pass = 0;
					for (final SmPattern pattern : currentType.getPatterns())
					{
						size++;
						try
						{
							pattern.validate(normalizedValue);
							pass++;
						}
						catch (final SmPatternException e)
						{
							// Ignore.
						}
					}
					if (size > 0 && pass == 0)
					{
						throw new SmDatatypeException(normalizedValue, simpleType);
					}
				}
				final SmType<A> baseType = currentType.getBaseType();
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
					currentType = (SmSimpleType<A>)baseType;
				}
			}
		}
	}

	protected static <A> void checkValueSpaceFacets(final List<? extends A> actualValue, final SmSimpleType<A> simpleType, final AtomBridge<A> atomBridge) throws SmDatatypeException
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

	protected final AtomBridge<A> atomBridge;

	private final HashSet<SmEnumeration<A>> m_enumerationFacets = new HashSet<SmEnumeration<A>>();

	@SuppressWarnings("unchecked")
	private final SmFacet<A>[] m_facetArray = (SmFacet<A>[])(Array.newInstance(SmFacet.class, SmFacetKind.values().length));

	private final HashSet<SmFacet<A>> m_facets = new HashSet<SmFacet<A>>();
	/**
	 * {final} is mutable.
	 */
	private final EnumSet<SmDerivationMethod> m_final = EnumSet.noneOf(SmDerivationMethod.class);

	private final Set<SmDerivationMethod> m_finalUnmodifiable = Collections.unmodifiableSet(m_final);

	private boolean m_isAbstract = false;

	private final HashSet<SmPattern> m_patternFacets = new HashSet<SmPattern>();

	protected final SmWhiteSpacePolicy m_whiteSpace;

	public SimpleTypeImpl(final QName name, final boolean isAnonymous, final SmScopeExtent scope, final SmDerivationMethod derivation, final SmWhiteSpacePolicy whiteSpace, final AtomBridge<A> atomBridge)
	{
		super(name, isAnonymous, scope, derivation, atomBridge.getNameBridge());
		this.m_whiteSpace = whiteSpace;
		this.atomBridge = atomBridge;
	}

	public void accept(SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	public void addEnumeration(final SmEnumeration<A> enumeration)
	{
		assertNotLocked();
		PreCondition.assertArgumentNotNull(enumeration, "enumeration");
		m_enumerationFacets.add(enumeration);
	}

	public void addFacet(final SmFacet<A> facet)
	{
		assertNotLocked();
		PreCondition.assertArgumentNotNull(facet, "facet");
		m_facets.add(facet);
		m_facetArray[facet.getKind().ordinal()] = facet;
	}

	public void addPattern(final SmPattern pattern)
	{
		assertNotLocked();
		PreCondition.assertArgumentNotNull(pattern, "pattern");
		m_patternFacets.add(pattern);
	}

	public SmSequenceType<A> atomSet()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	public SmType<A> getBaseType()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	public final Iterable<SmEnumeration<A>> getEnumerations()
	{
		return m_enumerationFacets;
	}

	public SmFacet<A> getFacetOfKind(final SmFacetKind facetKind)
	{
		return m_facetArray[facetKind.ordinal()];
	}

	public final Iterable<SmFacet<A>> getFacets()
	{
		return m_facets;
	}

	public final Set<SmDerivationMethod> getFinal()
	{
		return m_finalUnmodifiable;
	}

	public final Iterable<SmPattern> getPatterns()
	{
		return m_patternFacets;
	}

	public SmSimpleType<A> getNativeTypeDefinition()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	public final boolean hasEnumerations()
	{
		return m_enumerationFacets.size() > 0;
	}

	public boolean hasFacetOfKind(SmFacetKind facetKind)
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

	public final boolean isFinal(final SmDerivationMethod derivation)
	{
		PreCondition.assertArgumentNotNull(derivation, "derivation");
		return m_final.contains(derivation);
	}

	public final boolean isSimpleUrType()
	{
		return false;
	}

	public SmPrimeType<A> prime()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	public SmQuantifier quantifier()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

	public final void setAbstract(final boolean isAbstract)
	{
		assertNotLocked();
		m_isAbstract = isAbstract;
	}

	public final void setFinal(final SmDerivationMethod derivation, final boolean enabled)
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

	public List<A> validate(final List<? extends A> value) throws SmDatatypeException
	{
		// TODO: Can we attempt working in the value space and then fall back to the lexical space?
		final String initialValue = atomBridge.getC14NString(value);

		// normalize (handle whitespace pseudo-facet) first.
		final String normalizedValue = normalize(initialValue);

		checkPatternFacets(this, normalizedValue, atomBridge.getNameBridge());

		// compile - which will perform various forms of validation, for types w/o facets
		final List<A> actualValue = compile(normalizedValue);

		checkValueSpaceFacets(actualValue, this, atomBridge);

		return actualValue;
	}

	/**
	 * This method can only be called on simple types.
	 */
	public final List<A> validate(final String initialValue) throws SmDatatypeException
	{
		PreCondition.assertArgumentNotNull(initialValue, "initialValue");

		// normalize (handle whitespace pseudo-facet) first.
		final String normalizedValue = normalize(initialValue);

		checkPatternFacets(this, normalizedValue, atomBridge.getNameBridge());

		// compile - which will perform various forms of validation, for types w/o facets
		final List<A> actualValue = compile(normalizedValue);

		checkValueSpaceFacets(actualValue, this, atomBridge);

		return actualValue;
	}

	public List<A> validate(final String initialValue, final SmPrefixResolver resolver) throws SmDatatypeException
	{
		PreCondition.assertArgumentNotNull(initialValue, "initialValue");

		// normalize (handle whitespace pseudo-facet) first.
		final String normalizedValue = normalize(initialValue);

		checkPatternFacets(this, normalizedValue, atomBridge.getNameBridge());

		// compile - which will perform various forms of validation, for types w/o facets
		final List<A> actualValue = compile(normalizedValue, resolver);

		checkValueSpaceFacets(actualValue, this, atomBridge);

		return actualValue;
	}
}
