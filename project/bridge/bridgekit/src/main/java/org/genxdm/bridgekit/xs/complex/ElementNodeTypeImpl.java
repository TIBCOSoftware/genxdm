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
package org.genxdm.bridgekit.xs.complex;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.names.QNameAsSet;
import org.genxdm.bridgekit.xs.SchemaCache;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.NodeKind;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

public final class ElementNodeTypeImpl extends AbstractBranchNodeType implements ElementNodeType
{
	private final SequenceType m_dataType;
	private final boolean m_nillable;
	private final QName name;

	public ElementNodeTypeImpl(final QName name, final SequenceType dataType, final boolean nillable, final SchemaCache cache)
	{
		super(NodeKind.ELEMENT, cache);
		this.name = PreCondition.assertArgumentNotNull(name, "name");
		if (null != dataType)
		{
			m_dataType = dataType;
			m_nillable = nillable;
		}
		else
		{
			// Normalization as per formal semantics.
			m_dataType = cache.getTypeDefinition(NativeType.ANY_TYPE);
			m_nillable = (null != name);
		}
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	public PrimeTypeKind getKind()
	{
		return PrimeTypeKind.ELEMENT;
	}

	public String getLocalName()
	{
		if (null != name)
		{
			return name.getLocalPart();
		}
		else
		{
			return null;
		}
	}

	public QName getName()
	{
		return name;
	}

	public ScopeExtent getScopeExtent()
	{
		return ScopeExtent.Global;
	}

	public String getTargetNamespace()
	{
		if (name != null)
		{
			return name.getNamespaceURI();
		}
		else
		{
			return null;
		}
	}

	public SequenceType getType()
	{
		return m_dataType;
	}

	public boolean isAnonymous()
	{
		return false;
	}

	public boolean isNillable()
	{
		return m_nillable;
	}

	public ElementNodeType prime()
	{
		return this;
	}

	public boolean subtype(final PrimeType rhs)
	{
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType choiceType = (PrimeChoiceType)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
			case ELEMENT:
			{
				final ElementNodeType other = (ElementNodeType)rhs;
				return QNameAsSet.subset(name, other.getName());
			}
			case SCHEMA_ELEMENT:
			{
				return false;
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
		if (m_nillable)
		{
			return "element " + name + " nillable of type " + m_dataType;
		}
		else
		{
			return "element " + name + " of type " + m_dataType;
		}
	}
}
