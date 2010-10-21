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
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.NodeKind;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;

final class ElementNodeWithParentAxisType<A> implements SmElementNodeType<A>
{
	private final SmElementNodeType<A> m_element;

	public ElementNodeWithParentAxisType(final SmElementNodeType<A> element, final ElementDefinition<A> parentAxis)
	{
		m_element = PreCondition.assertArgumentNotNull(element);
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
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

	public NodeKind getNodeKind()
	{
		return m_element.getNodeKind();
	}

	public ScopeExtent getScopeExtent()
	{
		return m_element.getScopeExtent();
	}

	public String getTargetNamespace()
	{
		return m_element.getTargetNamespace();
	}

	public SmSequenceType<A> getType()
	{
		return m_element.getType();
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

	public KeeneQuantifier quantifier()
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
