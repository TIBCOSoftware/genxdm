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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.names.NameSource;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.components.SmEnumeration;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.enums.SmWhiteSpacePolicy;
import org.gxml.xs.facets.SmFacet;
import org.gxml.xs.facets.SmFacetKind;
import org.gxml.xs.facets.SmPattern;
import org.gxml.xs.resolve.SmPrefixResolver;
import org.gxml.xs.types.SmComplexUrType;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmPrimeChoiceType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmPrimeTypeKind;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmSimpleUrType;
import org.gxml.xs.types.SmType;

final class SimpleUrTypeImpl<A> extends AbstractPrimeExcludingNoneType<A> implements SmSimpleUrType<A>
{
	private final AtomBridge<A> atomBridge;
	private final NameSource nameBridge;
	private final QName m_name;
	private final SmCache<A> cache;

	public SimpleUrTypeImpl(final String W3C_XML_SCHEMA_NS_URI, final AtomBridge<A> atomBridge, final SmCache<A> cache)
	{
		this.atomBridge = atomBridge;
		this.nameBridge = atomBridge.getNameBridge();
		this.m_name = new QName(W3C_XML_SCHEMA_NS_URI, "anySimpleType");
		this.cache = cache;
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public void addEnumeration(final SmEnumeration<A> enumeration)
	{
		throw new AssertionError(getName());
	}

	public void addFacet(final SmFacet<A> facet)
	{
		throw new AssertionError(getName());
	}

	public void addPattern(final SmPattern pattern)
	{
		throw new AssertionError(getName());
	}

	public boolean derivedFrom(final String namespace, final String name, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFrom(this, namespace, name, derivationMethods, nameBridge);
	}

	public boolean derivedFromType(final SmType<A> ancestorType, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFromType(this, ancestorType, derivationMethods, nameBridge);
	}

	public SmComplexUrType<A> getBaseType()
	{
		return cache.getComplexUrType();
	}

	public SmDerivationMethod getDerivationMethod()
	{
		return SmDerivationMethod.Restriction;
	}

	public Iterable<SmEnumeration<A>> getEnumerations()
	{
		throw new AssertionError(getName());
	}

	public SmFacet<A> getFacetOfKind(final SmFacetKind facetKind)
	{
		throw new AssertionError(getName());
	}

	public Iterable<SmFacet<A>> getFacets()
	{
		throw new AssertionError(getName());
	}

	public Set<SmDerivationMethod> getFinal()
	{
		return EnumSet.noneOf(SmDerivationMethod.class);
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.ANY_SIMPLE_TYPE;
	}

	public String getLocalName()
	{
		return m_name.getLocalPart();
	}

	public QName getName()
	{
		return m_name;
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.ANY_SIMPLE_TYPE;
	}

	public SmSimpleType<A> getNativeTypeDefinition()
	{
		return this;
	}

	public Iterable<SmPattern> getPatterns()
	{
		throw new AssertionError(getName());
	}

	public SmScopeExtent getScopeExtent()
	{
		return SmScopeExtent.Global;
	}

	public String getTargetNamespace()
	{
		return m_name.getNamespaceURI();
	}

	public SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		return SmWhiteSpacePolicy.PRESERVE;
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

	public boolean isAnonymous()
	{
		return false;
	}

	public boolean isAtomicType()
	{
		return false;
	}

	public boolean isAtomicUrType()
	{
		return false;
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isComplexUrType()
	{
		return false;
	}

	public boolean isFinal(final SmDerivationMethod derivation)
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

	public boolean isIDREFS()
	{
		return false;
	}

	public boolean isListType()
	{
		return false;
	}

	public boolean isLocked()
	{
		return true;
	}

	public boolean isNative()
	{
		return true;
	}

	public boolean isSimpleUrType()
	{
		return true;
	}

	public boolean isUnionType()
	{
		return false;
	}

	public void lock()
	{
		// Ignore
	}

	public String normalize(final String initialValue)
	{
		return initialValue;
	}

	public SmSimpleUrType<A> prime()
	{
		return this;
	}

	public void setAbstract(final boolean isAbstract)
	{
		throw new AssertionError(getName());
	}

	public void setFinal(final SmDerivationMethod derivation, final boolean enabled)
	{
		throw new AssertionError(getName());
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
			case ANY_TYPE:
			case ANY_SIMPLE_TYPE:
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@Override
	public String toString()
	{
		return "xs:anySimpleType";
	}

	public List<A> validate(final List<? extends A> value)
	{
		final String strval = atomBridge.getC14NString(value);
		return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(strval));
	}

	public List<A> validate(final String value)
	{
		return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(value));
	}

	public List<A> validate(final String value, final SmPrefixResolver resolver)
	{
		return validate(value);
	}
}
