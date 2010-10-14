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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmEnumeration;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmPattern;
import org.genxdm.xs.resolve.SmPrefixResolver;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmAtomicUrType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmType;

final class AtomicUrTypeImpl<A> extends AbstractPrimeExcludingNoneType<A> implements SmAtomicUrType<A>
{
	private final AtomBridge<A> m_atomBridge;
	private final SimpleUrTypeImpl<A> m_baseType;
	private final QName m_name;

	public AtomicUrTypeImpl(final String W3C_XML_SCHEMA_NS_URI, final SimpleUrTypeImpl<A> baseType, final AtomBridge<A> atomBridge)
	{
		this.m_atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		this.m_name = new QName(W3C_XML_SCHEMA_NS_URI, "anyAtomicType");
		this.m_baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public boolean derivedFrom(final String namespace, final String name, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFrom(this, namespace, name, derivationMethods, m_atomBridge.getNameBridge());
	}

	public boolean derivedFromType(final SmType<A> ancestorType, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFromType(this, ancestorType, derivationMethods, m_atomBridge.getNameBridge());
	}

	public SimpleUrTypeImpl<A> getBaseType()
	{
		return m_baseType;
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
		return SmPrimeTypeKind.ANY_ATOMIC_TYPE;
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
		return SmNativeType.ANY_ATOMIC_TYPE;
	}

	public Iterable<SmPattern> getPatterns()
	{
		throw new AssertionError(getName());
	}

	public SmAtomicType<A> getNativeTypeDefinition()
	{
		return this;
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

	public boolean hasFacetOfKind(SmFacetKind facetKind)
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
		return true;
	}

	public boolean isAtomicUrType()
	{
		return true;
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

	public boolean isNative()
	{
		return true;
	}

	public boolean isSimpleUrType()
	{
		return false;
	}

	public boolean isUnionType()
	{
		return false;
	}

	public String normalize(final String initialValue)
	{
		return initialValue;
	}

	public SmAtomicUrType<A> prime()
	{
		return this;
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
			case ANY_ATOMIC_TYPE:
			case ANY_SIMPLE_TYPE:
			case ANY_TYPE:
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
		return "xs:anyAtomicType";
	}

	public List<A> validate(final List<? extends A> value)
	{
		final String strval = m_atomBridge.getC14NString(value);
		return m_atomBridge.wrapAtom(m_atomBridge.createUntypedAtomic(strval));
	}

	public List<A> validate(final String initialValue)
	{
		return m_atomBridge.wrapAtom(m_atomBridge.createUntypedAtomic(initialValue));
	}

	public List<A> validate(final String value, final SmPrefixResolver resolver)
	{
		return validate(value);
	}
}
