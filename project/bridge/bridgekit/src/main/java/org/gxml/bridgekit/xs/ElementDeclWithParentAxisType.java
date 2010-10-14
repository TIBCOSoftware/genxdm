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

import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmNodeKind;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmType;

final class ElementDeclWithParentAxisType<A> implements SmElement<A>
{
	private final SmElement<A> m_element;

	public ElementDeclWithParentAxisType(final SmElement<A> element, final SmElement<A> parentAxis)
	{
		m_element = PreCondition.assertArgumentNotNull(element);
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public Set<SmDerivationMethod> getDisallowedSubtitutions()
	{
		return m_element.getDisallowedSubtitutions();
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return m_element.getIdentityConstraints();
	}

	public SmPrimeTypeKind getKind()
	{
		return m_element.getKind();
	}

	public String getLocalName()
	{
		return m_element.getLocalName();
	}

	public QName getName()
	{
		return m_element.getName();
	}

	public SmNodeKind getNodeKind()
	{
		return m_element.getNodeKind();
	}

	public SmScopeExtent getScopeExtent()
	{
		return m_element.getScopeExtent();
	}

	public SmElement<A> getSubstitutionGroup()
	{
		return m_element.getSubstitutionGroup();
	}

	public Set<SmDerivationMethod> getSubstitutionGroupExclusions()
	{
		return m_element.getSubstitutionGroupExclusions();
	}

	public Iterable<SmElement<A>> getSubstitutionGroupMembers()
	{
		return m_element.getSubstitutionGroupMembers();
	}

	public String getTargetNamespace()
	{
		return m_element.getTargetNamespace();
	}

	public SmType<A> getType()
	{
		return m_element.getType();
	}

	public SmValueConstraint<A> getValueConstraint()
	{
		return m_element.getValueConstraint();
	}

	public boolean hasIdentityConstraints()
	{
		return m_element.hasIdentityConstraints();
	}

	public boolean hasSubstitutionGroup()
	{
		return m_element.hasSubstitutionGroup();
	}

	public boolean hasSubstitutionGroupMembers()
	{
		return m_element.hasSubstitutionGroupMembers();
	}

	public boolean isAbstract()
	{
		return m_element.isAbstract();
	}

	public boolean isAnonymous()
	{
		return m_element.isAnonymous();
	}

	public boolean isChoice()
	{
		return m_element.isChoice();
	}

	public boolean isNative()
	{
		return m_element.isNative();
	}

	public boolean isNillable()
	{
		return m_element.isNillable();
	}

	public boolean isNone()
	{
		return m_element.isNone();
	}

	public SmPrimeType<A> prime()
	{
		return this;
	}

	public SmQuantifier quantifier()
	{
		return m_element.quantifier();
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		return m_element.subtype(rhs);
	}

	@Override
	public String toString()
	{
		return m_element.toString();
	}
}
