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

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.names.QNameAsSet;
import org.genxdm.bridgekit.xs.simple.AbstractPrimeExcludingNoneType;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

public final class AttributeNodeTypeImpl extends AbstractPrimeExcludingNoneType implements AttributeNodeType
{
	private final QName name;
	private final SequenceType m_type;

	public AttributeNodeTypeImpl(final QName name, final SequenceType type, ComponentProvider cache)
	{
		this.name = PreCondition.assertArgumentNotNull(name, "name");
		if (null != type)
		{
			this.m_type = type;
		}
		else
		{
			this.m_type = cache.getSimpleUrType();
		}
	}

	public PrimeType prime()
	{
		return this;
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	public PrimeTypeKind getKind()
	{
		return PrimeTypeKind.ATTRIBUTE;
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

	public NodeKind getNodeKind()
	{
		return NodeKind.ATTRIBUTE;
	}

	public ScopeExtent getScopeExtent()
	{
		return ScopeExtent.Global;
	}

	public String getTargetNamespace()
	{
		if (null != name)
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
		return m_type;
	}

	public boolean isAnonymous()
	{
		return false;
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isNative()
	{
		return false;
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
			case ATTRIBUTE:
			{
				final AttributeNodeType other = (AttributeNodeType)rhs;
				return QNameAsSet.subset(name, other.getName());
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
		return "attribute " + name + " of type " + m_type;
	}
}
