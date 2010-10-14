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
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.components.SmComponentProvider;
import org.genxdm.xs.components.SmWildcard;
import org.genxdm.xs.constraints.SmAttributeUse;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmType;

/**
 * A complex type, but not the Complex Ur-Type.
 */
public final class ComplexTypeImpl<A> extends TypeImpl<A> implements SmComplexType<A>, SmPrimeType<A>
{
	private static <A> SmWildcard<A> computeAttributeWildcard(final SmType<A> baseType, final SmDerivationMethod derivation)
	{
		if (derivation.isRestriction())
		{
			return null;
		}
		if (derivation.isExtension())
		{
			if (baseType instanceof SmComplexType<?>)
			{
				return ((SmComplexType<A>)baseType).getAttributeWildcard();
			}
			else
			{
				return null;
			}
		}
		else
		{
			throw new AssertionError(derivation);
		}
	}

	private final boolean isNative;
	private final Map<QName, SmAttributeUse<A>> m_attributeUses;
	/**
	 * {attribute wildcard} is mutable
	 */
	private SmWildcard<A> m_attributeWildcard = null;
	private final SmType<A> m_baseType;

	private final Set<SmDerivationMethod> m_block;

	private final Set<SmDerivationMethod> m_blockUnmodifiable;

	private final SmComponentProvider<A> m_cache;

	/**
	 * {content type} is mutable.
	 */
	private SmContentType<A> m_contentType;

	/**
	 * {final} is mutable.
	 */
	private final EnumSet<SmDerivationMethod> m_final = EnumSet.noneOf(SmDerivationMethod.class);

	/**
	 * {abstract} is mutable and defaults to <code>false</code>.
	 */
	private boolean m_isAbstract = false;

	public ComplexTypeImpl(final QName name, final boolean isNative, final boolean isAnonymous, final SmScopeExtent scope, final SmType<A> baseType, final SmDerivationMethod derivation, final Map<QName, SmAttributeUse<A>> attributeUses,
			final SmContentType<A> contentType, final Set<SmDerivationMethod> block, final NameSource nameBridge, final SmComponentProvider<A> cache)
	{
		super(PreCondition.assertArgumentNotNull(name, "name"), isAnonymous, scope, derivation, nameBridge);
		this.isNative = isNative;
		this.m_cache = PreCondition.assertArgumentNotNull(cache, "cache");
		m_baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
		m_contentType = PreCondition.assertArgumentNotNull(contentType, "contentType");
		m_block = PreCondition.assertArgumentNotNull(block, "block");
		m_blockUnmodifiable = Collections.unmodifiableSet(m_block);
		m_attributeUses = PreCondition.assertArgumentNotNull(attributeUses, "attributeUses");
		m_attributeWildcard = computeAttributeWildcard(baseType, derivation);
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmSequenceType<A> atomSet()
	{
		return m_cache.getAtomicType(SmNativeType.UNTYPED_ATOMIC);
	}

	public Map<QName, SmAttributeUse<A>> getAttributeUses()
	{
		return m_attributeUses;
	}

	public SmWildcard<A> getAttributeWildcard()
	{
		return m_attributeWildcard;
	}

	public SmType<A> getBaseType()
	{
		return m_baseType;
	}

	public SmContentType<A> getContentType()
	{
		return m_contentType;
	}

	public final Set<SmDerivationMethod> getFinal()
	{
		return m_final;
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.COMPLEX;
	}

	public Set<SmDerivationMethod> getProhibitedSubstitutions()
	{
		return m_blockUnmodifiable;
	}

	public boolean isAbstract()
	{
		return m_isAbstract;
	}

	public boolean isAtomicType()
	{
		return false;
	}

	public final boolean isAtomicUrType()
	{
		return false;
	}

	public boolean isChoice()
	{
		return false;
	}

	public final boolean isComplexUrType()
	{
		return false;
	}

	public boolean isFinal(final SmDerivationMethod derivation)
	{
		PreCondition.assertArgumentNotNull(derivation, "derivation");
		return m_final.contains(derivation);
	}

	public boolean isNative()
	{
		return isNative;
	}

	public boolean isNone()
	{
		return false;
	}

	public final boolean isSimpleUrType()
	{
		return false;
	}

	public final SmPrimeType<A> prime()
	{
		return this;
	}

	public SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
	}

	public void setAbstract(final boolean isAbstract)
	{
		assertNotLocked();
		m_isAbstract = isAbstract;
	}

	public void setAttributeWildcard(final SmWildcard<A> attributeWildcard)
	{
		assertNotLocked();
		m_attributeWildcard = attributeWildcard;
	}

	public void setBlock(final SmDerivationMethod derivation, final boolean enabled)
	{
		assertNotLocked();
		if (enabled)
		{
			m_block.add(derivation);
		}
		else
		{
			m_block.remove(derivation);
		}
	}

	public void setContentType(final SmContentType<A> contentType)
	{
		assertNotLocked();
		m_contentType = PreCondition.assertArgumentNotNull(contentType, "name");
	}

	public void setFinal(final SmDerivationMethod derivation, final boolean enabled)
	{
		assertNotLocked();
		PreCondition.assertArgumentNotNull(derivation, "derivation");
		PreCondition.assertTrue(derivation.isExtension() || derivation.isRestriction(), "derivation (" + derivation + ") must be extension or restriction for a complex type");
		if (enabled)
		{
			m_final.add(derivation);
		}
		else
		{
			m_final.remove(derivation);
		}
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(rhs, "type");
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
				// case ANY_TYPE:
				// case COMPLEX:
				// {
				// final SmComplexType<A> complexType = (SmComplexType<A>)rhs;
				// return SmSupportImpl.subtype(this, complexType);
				// }
			case ELEMENT:
			{
				@SuppressWarnings("unused")
				final SmElementNodeType<A> element = (SmElementNodeType<A>)rhs;
				return false;
			}
			default:
			{
				return false;
			}
		}
	}
}
