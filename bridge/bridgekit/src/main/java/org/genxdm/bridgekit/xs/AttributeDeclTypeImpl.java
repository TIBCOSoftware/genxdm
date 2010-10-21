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
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.NodeKind;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleMarkerType;
import org.genxdm.xs.types.SmSimpleType;

public final class AttributeDeclTypeImpl<A> extends DataComponentImpl<A> implements AttributeDefinition<A>
{
	private SmSimpleMarkerType<A> m_type;

	public AttributeDeclTypeImpl(final QName name, final ScopeExtent scope, final SmSimpleMarkerType<A> type)
	{
		super(name, scope);
		this.m_type = PreCondition.assertArgumentNotNull(type, "type");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.SCHEMA_ATTRIBUTE;
	}

	public NodeKind getNodeKind()
	{
		return NodeKind.ATTRIBUTE;
	}

	public SmSimpleMarkerType<A> getType()
	{
		return m_type;
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isNative()
	{
		// This is a schema-attribute.
		return false;
	}

	public boolean isNone()
	{
		return false;
	}

	public SmPrimeType<A> prime()
	{
		return this;
	}

	public KeeneQuantifier quantifier()
	{
		return KeeneQuantifier.EXACTLY_ONE;
	}

	public void setType(final SmSimpleType<A> type)
	{
		assertNotLocked();
		m_type = PreCondition.assertArgumentNotNull(type, "type");
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		if (rhs instanceof AttributeDefinition<?>)
		{
			AttributeDefinition<A> rhsAttDecl = (AttributeDefinition<A>)rhs;
			if (rhsAttDecl.getScopeExtent() == ScopeExtent.Global)
			{
				if (getName().equals(rhsAttDecl.getName()))
				{
					return true;
				}
			}
			else
			{
				return rhs == this;
			}
		}
		return false;
	}
}
