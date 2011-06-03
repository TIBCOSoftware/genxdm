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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.types.ConcatType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

final class ZConcatType implements ConcatType
{
	public static  SequenceType concat(final SequenceType lhs, final SequenceType rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");

		// Make an attempt to simplify, but not approximate the type expression by detecting
		// empty types which are the identity element for concatenation. However we must use
		// the prime() and quantifier() approximations to detect the empty type.
		if (lhs.prime().isNone() && lhs.quantifier().isOptional())
		{
			return rhs;
		}
		else if (rhs.prime().isNone() && rhs.quantifier().isOptional())
		{
			return lhs;
		}
		else
		{
			return new ZConcatType(lhs, rhs);
		}
	}
	private final SequenceType m_lhs;

	private final SequenceType m_rhs;

	private ZConcatType(final SequenceType lhs, final SequenceType rhs)
	{
		m_lhs = PreCondition.assertArgumentNotNull(lhs);
		m_rhs = PreCondition.assertArgumentNotNull(rhs);
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	public SequenceType getLHS()
	{
		return m_lhs;
	}

	public SequenceType getRHS()
	{
		return m_rhs;
	}

	public PrimeType prime()
	{
		return ZPrimeChoiceType.choice(m_lhs.prime(), m_rhs.prime());
	}

	public KeeneQuantifier quantifier()
	{
		return m_lhs.quantifier().sum(m_rhs.quantifier());
	}

	@Override
	public String toString()
	{
		return m_lhs + " , " + m_rhs;
	}
}
