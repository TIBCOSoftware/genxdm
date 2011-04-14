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
import org.genxdm.xs.types.MultiplyType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

final class ZMultiplyType<A> extends AbstractType<A> implements MultiplyType<A>
{
	public static <A> SequenceType<A> multiply(final SequenceType<A> argument, final KeeneQuantifier multiplier)
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
				if (argument instanceof MultiplyType<?>)
				{
					final MultiplyType<A> arg = (MultiplyType<A>)argument;
					return multiply(arg.getArgument(), arg.getMultiplier().product(multiplier));
				}
				else
				{
					return new ZMultiplyType<A>(argument, multiplier);
				}
			}
		}
	}

	public static <A> SequenceType<A> optional(final SequenceType<A> argument)
	{
		return multiply(argument, KeeneQuantifier.OPTIONAL);
	}

	public static <A> SequenceType<A> zeroOrMore(final SequenceType<A> argument)
	{
		return multiply(argument, KeeneQuantifier.ZERO_OR_MORE);
	}

	private final SequenceType<A> m_argument;

	private final KeeneQuantifier m_multiplier;

	private ZMultiplyType(final SequenceType<A> argument, final KeeneQuantifier multiplier)
	{
		this.m_argument = PreCondition.assertArgumentNotNull(argument, "argument");
		this.m_multiplier = PreCondition.assertArgumentNotNull(multiplier, "multiplier");
	}

	public void accept(final SequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SequenceType<A> getArgument()
	{
		return m_argument;
	}

	public KeeneQuantifier getMultiplier()
	{
		return m_multiplier;
	}

	public boolean isChoice()
	{
		return false;
	}

	public PrimeType<A> prime()
	{
		// Formal Semantics...
		// prime(Type?) = prime(Type)
		// prime(Type*) = prime(Type)
		// prime(Type+) = prime(Type)
		return m_argument.prime();
	}

	public KeeneQuantifier quantifier()
	{
		// Formal Semantics...
		// quantifier(Type?) = quantifier(Type).?
		// quantifier(Type*) = quantifier(Type).*
		// quantifier(Type+) = quantifier(Type).+
		return m_argument.quantifier().product(m_multiplier);
	}

	public boolean subtype(final SequenceType<A> type)
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
		final KeeneQuantifier card = quantifier();
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
