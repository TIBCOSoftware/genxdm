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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmPattern;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;

final class DecimalType<A> extends AbstractAtomType<A>
{
	public DecimalType(final QName name, final SmSimpleType<A> baseType, final AtomBridge<A> atomBridge)
	{
		super(name, baseType, atomBridge);
	}

	public void accept(SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<EnumerationDefinition<A>> getEnumerations()
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

	public Set<DerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.DECIMAL;
	}

	public Iterable<SmPattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public ScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public WhiteSpacePolicy getWhiteSpacePolicy()
	{
		return WhiteSpacePolicy.COLLAPSE;
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

	public List<A> validate(final String initialValue) throws DatatypeException
	{
		try
		{
			final String trimmed = scale(trim(initialValue), false, this);

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

	public List<A> validate(String initialValue, PrefixResolver resolver) throws DatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	private static String scale(String value, boolean allowExponential, final SmSimpleType<?> type) throws DatatypeException
	{
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
