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

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.types.SmPrimeChoiceType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmPrimeTypeKind;
import org.gxml.xs.types.SmSequenceTypeVisitor;

final class ZPrimeChoiceType<A> extends AbstractPrimeExcludingNoneType<A> implements SmPrimeChoiceType<A>
{
	public static <A> SmPrimeType<A> choice(final SmPrimeType<A> lhs, final SmPrimeType<A> rhs)
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
			return new ZPrimeChoiceType<A>(lhs, rhs);
		}
	}

	private final SmPrimeType<A> m_lhs;

	private final SmPrimeType<A> m_rhs;

	private ZPrimeChoiceType(final SmPrimeType<A> lhs, final SmPrimeType<A> rhs)
	{
		m_lhs = PreCondition.assertArgumentNotNull(lhs);
		m_rhs = PreCondition.assertArgumentNotNull(rhs);
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.CHOICE;
	}

	public SmPrimeType<A> getLHS()
	{
		return m_lhs;
	}

	public SmPrimeType<A> getRHS()
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

	public SmPrimeChoiceType<A> prime()
	{
		return this;
	}

	public boolean subtype(final SmPrimeType<A> rhs)
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
