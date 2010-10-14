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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.types.SmMultiplyType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;

final class ZMultiplyType<A> extends AbstractType<A> implements SmMultiplyType<A>
{
	public static <A> SmSequenceType<A> multiply(final SmSequenceType<A> argument, final SmQuantifier multiplier)
	{
		switch (multiplier)
		{
			case EMPTY:
			{
				return new ZEmptyType<A>();
			}
			case EXACTLY_ONE:
			{
				return argument;
			}
			default:
			{
				if (argument instanceof SmMultiplyType<?>)
				{
					final SmMultiplyType<A> arg = (SmMultiplyType<A>)argument;
					return multiply(arg.getArgument(), arg.getMultiplier().product(multiplier));
				}
				else
				{
					return new ZMultiplyType<A>(argument, multiplier);
				}
			}
		}
	}

	public static <A> SmSequenceType<A> optional(final SmSequenceType<A> argument)
	{
		return multiply(argument, SmQuantifier.OPTIONAL);
	}

	public static <A> SmSequenceType<A> zeroOrMore(final SmSequenceType<A> argument)
	{
		return multiply(argument, SmQuantifier.ZERO_OR_MORE);
	}

	private final SmSequenceType<A> m_argument;

	private final SmQuantifier m_multiplier;

	private ZMultiplyType(final SmSequenceType<A> argument, final SmQuantifier multiplier)
	{
		this.m_argument = PreCondition.assertArgumentNotNull(argument, "argument");
		this.m_multiplier = PreCondition.assertArgumentNotNull(multiplier, "multiplier");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmSequenceType<A> getArgument()
	{
		return m_argument;
	}

	public SmQuantifier getMultiplier()
	{
		return m_multiplier;
	}

	public boolean isChoice()
	{
		return false;
	}

	public SmPrimeType<A> prime()
	{
		// Formal Semantics...
		// prime(Type?) = prime(Type)
		// prime(Type*) = prime(Type)
		// prime(Type+) = prime(Type)
		return m_argument.prime();
	}

	public SmQuantifier quantifier()
	{
		// Formal Semantics...
		// quantifier(Type?) = quantifier(Type).?
		// quantifier(Type*) = quantifier(Type).*
		// quantifier(Type+) = quantifier(Type).+
		return m_argument.quantifier().product(m_multiplier);
	}

	public boolean subtype(final SmSequenceType<A> type)
	{
		if (type.quantifier().contains(m_multiplier))
		{
			return m_argument.prime().subtype(type.prime());
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		final SmQuantifier card = quantifier();
		if (card.isNone())
		{
			return "none";
		}
		if (card.isEmpty())
		{
			return "empty";
		}
		return "(".concat(m_argument.toString()).concat(")").concat(m_multiplier.toString());
	}
}