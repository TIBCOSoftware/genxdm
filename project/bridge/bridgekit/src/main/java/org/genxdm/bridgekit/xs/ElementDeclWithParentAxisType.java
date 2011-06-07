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

import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.NodeKind;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.Type;

final class ElementDeclWithParentAxisType implements ElementDefinition
{
	private final ElementDefinition m_element;

	public ElementDeclWithParentAxisType(final ElementDefinition element, final ElementDefinition parentAxis)
	{
		m_element = PreCondition.assertArgumentNotNull(element);
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	public Set<DerivationMethod> getDisallowedSubtitutions()
	{
		return m_element.getDisallowedSubtitutions();
	}

	public Iterable<IdentityConstraint> getIdentityConstraints()
	{
		return m_element.getIdentityConstraints();
	}

	public PrimeTypeKind getKind()
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

	public NodeKind getNodeKind()
	{
		return m_element.getNodeKind();
	}

	public ScopeExtent getScopeExtent()
	{
		return m_element.getScopeExtent();
	}

	public ElementDefinition getSubstitutionGroup()
	{
		return m_element.getSubstitutionGroup();
	}

	public Set<DerivationMethod> getSubstitutionGroupExclusions()
	{
		return m_element.getSubstitutionGroupExclusions();
	}

	public Iterable<ElementDefinition> getSubstitutionGroupMembers()
	{
		return m_element.getSubstitutionGroupMembers();
	}

	public String getTargetNamespace()
	{
		return m_element.getTargetNamespace();
	}

	public Type getType()
	{
		return m_element.getType();
	}

	public ValueConstraint getValueConstraint()
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

	public PrimeType prime()
	{
		return this;
	}

	public KeeneQuantifier quantifier()
	{
		return m_element.quantifier();
	}

	public boolean subtype(final PrimeType rhs)
	{
		return m_element.subtype(rhs);
	}

	@Override
	public String toString()
	{
		return m_element.toString();
	}
}
