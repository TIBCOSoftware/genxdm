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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.components.SmEnumeration;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.enums.SmWhiteSpacePolicy;
import org.gxml.xs.exceptions.SmDatatypeException;
import org.gxml.xs.facets.SmFacet;
import org.gxml.xs.facets.SmFacetKind;
import org.gxml.xs.facets.SmPattern;
import org.gxml.xs.resolve.SmPrefixResolver;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmSimpleType;

final class DoubleType<A> extends AbstractAtomType<A>
{
	private static final String LEGACY_NEGATIVE_INFINITY_LITERAL = "-Infinity";
	private static final int LEGACY_NEGATIVE_INFINITY_LITERAL_LENGTH = 9;
	private static final String LEGACY_POSITIVE_INFINITY_LITERAL = "Infinity";
	private static final int LEGACY_POSITIVE_INFINITY_LITERAL_LENGTH = 8;
	private static final String MODERN_NEGATIVE_INFINITY_LITERAL = "-INF";
	private static final int MODERN_NEGATIVE_INFINITY_LITERAL_LENGTH = 4;
	private static final String MODERN_POSITIVE_INFINITY_LITERAL = "INF";
	private static final int MODERN_POSITIVE_INFINITY_LITERAL_LENGTH = 3;
	private static final String NAN_LITERAL = "NaN";
	private static final int NAN_LITERAL_LENGTH = 3;

	public DoubleType(final QName name, final SmSimpleType<A> baseType, final AtomBridge<A> atomBridge)
	{
		super(name, baseType, atomBridge);
	}

	public void accept(SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean derivedFrom(String namespace, String name, Set<SmDerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmEnumeration<A>> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmFacet<A> getFacetOfKind(SmFacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmFacet<A>> getFacets()
	{
		return Collections.emptyList();
	}

	public Set<SmDerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.DOUBLE;
	}

	public Iterable<SmPattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		return SmWhiteSpacePolicy.COLLAPSE;
	}

	public boolean hasEnumerations()
	{
		return false;
	}

	public boolean hasFacetOfKind(final SmFacetKind facetKind)
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

	public List<A> validate(final String strval) throws SmDatatypeException
	{
		try
		{
			final String trimmed = trim(strval);

			final int trimmedLength = trimmed.length();

			if (trimmedLength == 0)
			{
				throw new SmDatatypeException(strval, this);
			}

			if (trimmedLength == NAN_LITERAL_LENGTH && trimmed.equals(NAN_LITERAL))
			{
				return atomBridge.wrapAtom(atomBridge.createDouble(Double.NaN));
			}

			if (trimmedLength == MODERN_POSITIVE_INFINITY_LITERAL_LENGTH && trimmed.equals(MODERN_POSITIVE_INFINITY_LITERAL))
			{
				return atomBridge.wrapAtom(atomBridge.createDouble(Double.POSITIVE_INFINITY));
			}

			if (trimmedLength == MODERN_NEGATIVE_INFINITY_LITERAL_LENGTH && trimmed.equals(MODERN_NEGATIVE_INFINITY_LITERAL))
			{
				return atomBridge.wrapAtom(atomBridge.createDouble(Double.NEGATIVE_INFINITY));
			}

			if (trimmedLength == LEGACY_POSITIVE_INFINITY_LITERAL_LENGTH && trimmed.equals(LEGACY_POSITIVE_INFINITY_LITERAL))
			{
				throw new SmDatatypeException(strval, this);
			}
			if (trimmedLength == LEGACY_NEGATIVE_INFINITY_LITERAL_LENGTH && trimmed.equals(LEGACY_NEGATIVE_INFINITY_LITERAL))
			{
				throw new SmDatatypeException(strval, this);
			}
			// Java Wrapper parseDouble method is more lenient than XML.
			if (trimmed.endsWith("d") || trimmed.endsWith("D"))
			{
				throw new SmDatatypeException(strval, this);
			}
			else
			{
				return atomBridge.wrapAtom(atomBridge.createDouble(Double.parseDouble(trimmed)));
			}
		}
		catch (final NumberFormatException e)
		{
			// The unchecked exception is generally useless noise so we drop it.
			throw new SmDatatypeException(strval, this);
		}
	}

	public List<A> validate(String initialValue, SmPrefixResolver resolver) throws SmDatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
}
