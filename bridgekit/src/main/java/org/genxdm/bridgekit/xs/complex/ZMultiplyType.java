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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.types.MultiplyType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

public final class ZMultiplyType extends AbstractType implements MultiplyType
{
    public static SequenceType multiply(final SequenceType argument, final Quantifier multiplier)
    {
        switch (multiplier)
        {
            case EMPTY:
            {
                return new ZEmptyType();
            }
            case EXACTLY_ONE:
            {
                return argument;
            }
            default:
            {
                if (argument instanceof MultiplyType)
                {
                    final MultiplyType arg = (MultiplyType)argument;
                    return multiply(arg.getArgument(), arg.getMultiplier().product(multiplier));
                }
                else
                {
                    return new ZMultiplyType(argument, multiplier);
                }
            }
        }
    }

    public static  SequenceType optional(final SequenceType argument)
    {
        return multiply(argument, Quantifier.OPTIONAL);
    }

    public static  SequenceType zeroOrMore(final SequenceType argument)
    {
        return multiply(argument, Quantifier.ZERO_OR_MORE);
    }

    private final SequenceType m_argument;

    private final Quantifier m_multiplier;

    private ZMultiplyType(final SequenceType argument, final Quantifier multiplier)
    {
        this.m_argument = PreCondition.assertArgumentNotNull(argument, "argument");
        this.m_multiplier = PreCondition.assertArgumentNotNull(multiplier, "multiplier");
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public SequenceType getArgument()
    {
        return m_argument;
    }

    public Quantifier getMultiplier()
    {
        return m_multiplier;
    }

    public boolean isChoice()
    {
        return false;
    }

    public PrimeType prime()
    {
        // Formal Semantics...
        // prime(Type?) = prime(Type)
        // prime(Type*) = prime(Type)
        // prime(Type+) = prime(Type)
        return m_argument.prime();
    }

    public Quantifier quantifier()
    {
        // Formal Semantics...
        // quantifier(Type?) = quantifier(Type).?
        // quantifier(Type*) = quantifier(Type).*
        // quantifier(Type+) = quantifier(Type).+
        return m_argument.quantifier().product(m_multiplier);
    }

    public boolean subtype(final SequenceType type)
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
        final Quantifier card = quantifier();
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
