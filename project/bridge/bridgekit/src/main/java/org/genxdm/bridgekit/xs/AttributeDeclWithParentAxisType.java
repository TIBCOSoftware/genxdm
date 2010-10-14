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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmNodeKind;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleMarkerType;

final class AttributeDeclWithParentAxisType<A> implements SmAttribute<A>
{
	private final SmAttribute<A> m_attribute;

	public AttributeDeclWithParentAxisType(final SmAttribute<A> element, final SmElement<A> parentAxis)
	{
		m_attribute = PreCondition.assertArgumentNotNull(element);
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmPrimeTypeKind getKind()
	{
		return m_attribute.getKind();
	}

	public String getLocalName()
	{
		return m_attribute.getLocalName();
	}

	public QName getName()
	{
		return m_attribute.getName();
	}

	public SmNodeKind getNodeKind()
	{
		return m_attribute.getNodeKind();
	}

	public SmScopeExtent getScopeExtent()
	{
		return m_attribute.getScopeExtent();
	}

	public String getTargetNamespace()
	{
		return m_attribute.getTargetNamespace();
	}

	public SmSimpleMarkerType<A> getType()
	{
		return m_attribute.getType();
	}

	public SmValueConstraint<A> getValueConstraint()
	{
		return m_attribute.getValueConstraint();
	}

	public boolean isAnonymous()
	{
		return m_attribute.isAnonymous();
	}

	public boolean isChoice()
	{
		return m_attribute.isChoice();
	}

	public boolean isNative()
	{
		return m_attribute.isNative();
	}

	public boolean isNone()
	{
		return m_attribute.isNone();
	}

	public SmPrimeType<A> prime()
	{
		return this;
	}

	public SmQuantifier quantifier()
	{
		return m_attribute.quantifier();
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		return m_attribute.subtype(rhs);
	}

	@Override
	public String toString()
	{
		return m_attribute.toString();
	}
}
