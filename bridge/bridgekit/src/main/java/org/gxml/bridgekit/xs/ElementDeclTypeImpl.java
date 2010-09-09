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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmNodeKind;
import org.gxml.xs.enums.SmQuantifier;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.types.SmElementNodeType;
import org.gxml.xs.types.SmPrimeChoiceType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmPrimeTypeKind;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmType;

public final class ElementDeclTypeImpl<A> extends DataComponentImpl<A> implements SmElement<A>
{
	private final EnumSet<SmDerivationMethod> m_block = EnumSet.noneOf(SmDerivationMethod.class);
	private final Set<SmDerivationMethod> m_blockUnmodifiable = Collections.unmodifiableSet(m_block);
	private final HashMap<QName, SmIdentityConstraint<A>> m_constraints = new HashMap<QName, SmIdentityConstraint<A>>();
	private final EnumSet<SmDerivationMethod> m_final = EnumSet.noneOf(SmDerivationMethod.class);
	private final Set<SmDerivationMethod> m_finalUnmodifiable = Collections.unmodifiableSet(m_final);

	/**
	 * The {abstract} property is mutable defaults to <code>false</code>
	 */
	private boolean m_isAbstract = false;

	/**
	 * The {nullable} property is mutable defaults to <code>false</code>
	 */
	private boolean m_isNillable = false;

	/**
	 * The {substitution group} (head) is mutable.
	 */
	private ElementDeclTypeImpl<A> m_substitutionGroup = null;

	private final HashSet<SmElement<A>> m_substitutionGroupMembers = new HashSet<SmElement<A>>();

	/**
	 * The {type} property is mutable.
	 */
	private SmType<A> m_type;

	public ElementDeclTypeImpl(final QName name, final SmScopeExtent scope, final SmType<A> type)
	{
		super(name, scope);
		this.m_type = PreCondition.assertArgumentNotNull(type, "type");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public void addIdentityConstraint(final SmIdentityConstraint<A> identityConstraint)
	{
		PreCondition.assertArgumentNotNull(identityConstraint, "identityConstraint");
		m_constraints.put(identityConstraint.getName(), identityConstraint);
	}

	public void addSubstitutionGroupMember(final SmElement<A> member)
	{
		assertNotLocked();
		PreCondition.assertArgumentNotNull(member, "member");
		m_substitutionGroupMembers.add(member);
	}

	public Set<SmDerivationMethod> getDisallowedSubtitutions()
	{
		return m_blockUnmodifiable;
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return m_constraints.values();
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.SCHEMA_ELEMENT;
	}

	public SmNodeKind getNodeKind()
	{
		return SmNodeKind.ELEMENT;
	}

	public SmElement<A> getSubstitutionGroup()
	{
		return m_substitutionGroup;
	}

	public Set<SmDerivationMethod> getSubstitutionGroupExclusions()
	{
		return m_finalUnmodifiable;
	}

	public Iterable<SmElement<A>> getSubstitutionGroupMembers()
	{
		return m_substitutionGroupMembers;
	}

	public SmType<A> getType()
	{
		return m_type;
	}

	public boolean hasIdentityConstraints()
	{
		return !m_constraints.isEmpty();
	}

	public boolean hasSubstitutionGroup()
	{
		return (null != m_substitutionGroup);
	}

	public boolean hasSubstitutionGroupMembers()
	{
		return !m_substitutionGroupMembers.isEmpty();
	}

	public boolean isAbstract()
	{
		return m_isAbstract;
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isDisallowedSubstitution(final SmDerivationMethod derivation)
	{
		PreCondition.assertArgumentNotNull(derivation, "derivation");
		return m_block.contains(derivation);
	}

	public boolean isNative()
	{
		return false;
	}

	public boolean isNillable()
	{
		return m_isNillable;
	}

	public boolean isNone()
	{
		return false;
	}

	public SmPrimeType<A> prime()
	{
		return this;
	}

	public SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
	}

	public void removeSubstitutionGroupMember(final SmElement<A> member)
	{
		assertNotLocked();
		PreCondition.assertArgumentNotNull(member, "member");
		m_substitutionGroupMembers.add(member);
	}

	public void setAbstract(boolean isAbstract)
	{
		assertNotLocked();
		m_isAbstract = isAbstract;
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

	public void setFinal(final SmDerivationMethod derivation, final boolean enabled)
	{
		assertNotLocked();
		if (enabled)
		{
			m_final.add(derivation);
		}
		else
		{
			m_final.remove(derivation);
		}
	}

	public void setNillable(boolean isNillable)
	{
		assertNotLocked();
		m_isNillable = isNillable;
	}

	public void setSubstitutionGroup(final ElementDeclTypeImpl<A> substitutionGroup)
	{
		assertNotLocked();
		if (m_substitutionGroup != substitutionGroup)
		{
			if (null != m_substitutionGroup)
			{
				m_substitutionGroup.removeSubstitutionGroupMember(this);
			}
			if (null != substitutionGroup)
			{
				substitutionGroup.addSubstitutionGroupMember(this);
			}
			m_substitutionGroup = substitutionGroup;
		}
	}

	public void setType(final SmType<A> type)
	{
		assertNotLocked();
		m_type = PreCondition.assertArgumentNotNull(type, "type");
	}

	@SuppressWarnings("unchecked")
	public boolean subtype(final SmPrimeType rhs)
	{
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType choiceType = (SmPrimeChoiceType)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
			case SCHEMA_ELEMENT:
			{
				final SmElement<A> other = (SmElement<A>)rhs;
				return getName().equals(other.getName());
			}
			case ELEMENT:
			{
				final SmElementNodeType<A> other = (SmElementNodeType<A>)rhs;
				return getName().equals(other.getName());
			}
			case COMPLEX:
			case ANY_TYPE:
			case NODE:
			case ITEM:
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
		return "schema-element(" + getName().toString() + ")";
	}
}
