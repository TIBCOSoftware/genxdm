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

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.components.SmAttribute;
import org.gxml.xs.enums.SmNodeKind;
import org.gxml.xs.enums.SmQuantifier;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmPrimeTypeKind;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmSimpleMarkerType;
import org.gxml.xs.types.SmSimpleType;

public final class AttributeDeclTypeImpl<A> extends DataComponentImpl<A> implements SmAttribute<A>
{
	private SmSimpleMarkerType<A> m_type;

	public AttributeDeclTypeImpl(final QName name, final SmScopeExtent scope, final SmSimpleMarkerType<A> type)
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

	public SmNodeKind getNodeKind()
	{
		return SmNodeKind.ATTRIBUTE;
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

	public SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
	}

	public void setType(final SmSimpleType<A> type)
	{
		assertNotLocked();
		m_type = PreCondition.assertArgumentNotNull(type, "type");
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		if (rhs instanceof SmAttribute<?>)
		{
			SmAttribute<A> rhsAttDecl = (SmAttribute<A>)rhs;
			if (rhsAttDecl.getScopeExtent() == SmScopeExtent.Global)
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
