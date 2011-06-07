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

import org.genxdm.bridgekit.xs.simple.AbstractPrimeExcludingNoneType;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;

public final class ZPrimeChoiceType extends AbstractPrimeExcludingNoneType implements PrimeChoiceType
{
	public static  PrimeType choice(final PrimeType lhs, final PrimeType rhs)
	{
		// Make an attempt to simplify, but not approximate the type expression by detecting
		// "none" type subtrees which are the identity element for choice. However we must use
		// the prime() and quantifier() approximations to detect the "none" type in a subtree.
		if (lhs.prime().isNone() && lhs.quantifier().isExactlyOne())
		{
			return rhs;
		}
		else if (rhs.prime().isNone() && rhs.quantifier().isExactlyOne())
		{
			return lhs;
		}
		else if (lhs.subtype(rhs))
		{
			return rhs;
		}
		else if (rhs.subtype(lhs))
		{
			return lhs;
		}
		else
		{
			return new ZPrimeChoiceType(lhs, rhs);
		}
	}

	private final PrimeType m_lhs;

	private final PrimeType m_rhs;

	private ZPrimeChoiceType(final PrimeType lhs, final PrimeType rhs)
	{
		m_lhs = PreCondition.assertArgumentNotNull(lhs);
		m_rhs = PreCondition.assertArgumentNotNull(rhs);
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	public PrimeTypeKind getKind()
	{
		return PrimeTypeKind.CHOICE;
	}

	public PrimeType getLHS()
	{
		return m_lhs;
	}

	public PrimeType getRHS()
	{
		return m_rhs;
	}

	public boolean isChoice()
	{
		return true;
	}

	public boolean isNative()
	{
		return m_lhs.isNative() && m_rhs.isNative();
	}

	public PrimeChoiceType prime()
	{
		return this;
	}

	public boolean subtype(final PrimeType rhs)
	{
		return m_lhs.subtype(rhs) && m_rhs.subtype(rhs);
	}

	@Override
	public String toString()
	{
		final boolean lhsParen = m_lhs.isChoice();
		final boolean rhsParen = m_rhs.isChoice();
		if (lhsParen)
		{
			if (rhsParen)
			{
				return "(".concat(m_lhs.toString()).concat(") | (").concat(m_rhs.toString()).concat(")");
			}
			else
			{
				return "(".concat(m_lhs.toString()).concat(") | ").concat(m_rhs.toString());
			}
		}
		else
		{
			if (rhsParen)
			{
				return m_lhs.toString().concat(" | (").concat(m_rhs.toString()).concat(")");
			}
			else
			{
				return m_lhs.toString().concat(" | ").concat(m_rhs.toString());
			}
		}
	}
}
