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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmEnumeration;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmPattern;
import org.genxdm.xs.resolve.SmPrefixResolver;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;

final class LanguageType<A> extends AbstractAtomType<A>
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

	public LanguageType(final QName name, final SmSimpleType<A> baseType, final AtomBridge<A> atomBridge)
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
		return SmNativeType.LANGUAGE;
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

	public List<A> validate(final String initialValue) throws SmDatatypeException
	{
		final String normalized = normalize(initialValue);
		if (isValidLanguage(normalized))
		{
			return atomBridge.wrapAtom(atomBridge.createStringDerived(normalized, SmNativeType.LANGUAGE));
		}
		else
		{
			throw new SmDatatypeException(normalized, this);
		}
	}

	public List<A> validate(String initialValue, SmPrefixResolver resolver) throws SmDatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
}
