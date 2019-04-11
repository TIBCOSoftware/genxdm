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
package org.genxdm.bridgekit.xs.simple;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.SchemaSupport;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;

/**
 * An atomic type, but not the Atomic Ur-Type.
 * <p>
 * This is strictly used for derived atomic values
 * </p>
 */
public final class AtomicTypeImpl 
    extends SimpleTypeImpl 
    implements AtomicType
{
    public AtomicTypeImpl(final QName name, final boolean isAnonymous, final ScopeExtent scope, final AtomicType baseType, final WhiteSpacePolicy whiteSpace)
    {
        super(name, isAnonymous, scope, DerivationMethod.Restriction, whiteSpace);
        this.baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public SequenceType atomSet()
    {
        // Atomization has no effect.
        return this;
    }

    protected <A> List<A> compile(final String initialValue, AtomBridge<A> atomBridge) throws DatatypeException
    {
        final String normalizedValue = normalize(initialValue);
        try
        {
            final List<A> compiled = baseType.validate(initialValue, atomBridge);
            if (compiled.size() == 1)
            {
                final A baseAtom = compiled.get(0);
                final A thisAtom = atomBridge.makeForeignAtom(getName(), baseAtom);
                return atomBridge.wrapAtom(thisAtom);
            }
            else
            {
                throw new AssertionError();
            }
        }
        catch (final DatatypeException e)
        {
            throw new DatatypeException(normalizedValue, this, e);
        }
    }

    protected <A> List<A> compile(final String initialValue, final PrefixResolver resolver, AtomBridge<A> atomBridge) throws DatatypeException
    {
        final String normalizedValue = normalize(initialValue);
        try
        {
            final List<A> compiled = baseType.validate(initialValue, resolver, atomBridge);
            if (compiled.size() == 1)
            {
                final A baseAtom = compiled.get(0);
                final A thisAtom = atomBridge.makeForeignAtom(getName(), baseAtom);
                return atomBridge.wrapAtom(thisAtom);
            }
            else
            {
                throw new AssertionError();
            }
        }
        catch (final DatatypeException e)
        {
            throw new DatatypeException(normalizedValue, this, e);
        }
    }

    public SimpleType getBaseType()
    {
        return baseType;
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.ATOM;
    }

    public NativeType getNativeType()
    {
        return baseType.getNativeType();
    }

    public AtomicType getNativeTypeDefinition()
    {
        return baseType.getNativeTypeDefinition();
    }

    public final WhiteSpacePolicy getWhiteSpacePolicy()
    {
        if (null != m_whiteSpace)
        {
            return m_whiteSpace;
        }
        else
        {
            return baseType.getWhiteSpacePolicy();
        }
    }

    public boolean isAtomicType()
    {
        return true;
    }

    public boolean isChoice()
    {
        return false;
    }

    public boolean isNative()
    {
        return false;
    }

    public boolean isID()
    {
        return (getNativeType() == NativeType.ID);
    }

    public boolean isIDREF()
    {
        return (getNativeType() == NativeType.IDREF);
    }

    public boolean isIDREFS()
    {
        return false;
    }

    public boolean isListType()
    {
        return false;
    }

    public boolean isNone()
    {
        return false;
    }

    public boolean isUnionType()
    {
        return false;
    }

    public String normalize(final String initialValue)
    {
        final NativeType nativeType = getNativeType();
        if (nativeType.isToken())
        {
            return WhiteSpacePolicy.COLLAPSE.apply(initialValue);
        }
        else if (nativeType == NativeType.NORMALIZED_STRING)
        {
            // in theory, it could be the base type for something constrained
            // to collapse, but why would you do that? use a collapse-string base
            return WhiteSpacePolicy.REPLACE.apply(initialValue);
        }
        else if (nativeType == NativeType.STRING)
        {
            // the only type that allows preserve (well, apart from untypedatomic)
            return getWhiteSpacePolicy().apply(initialValue);
        }
        else if (nativeType == NativeType.UNTYPED_ATOMIC)
        {
            return initialValue;
        }
        else
        {
            return WhiteSpacePolicy.COLLAPSE.apply(initialValue);
        }
    }

    public final AtomicType prime()
    {
        return this;
    }

    public Quantifier quantifier()
    {
        return Quantifier.EXACTLY_ONE;
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
            case ANY_ATOMIC_TYPE:
            case ANY_SIMPLE_TYPE:
            case ANY_TYPE:
            {
                return true;
            }
            case ATOM:
            {
                final AtomicType atomicType = (AtomicType)rhs;
                return SchemaSupport.subtype(this, atomicType);
            }
            case EMPTY:
            {
                return false;
            }
            case NONE:
            {
                return false;
            }
            default:
            {
                return false;
            }
        }
    }

//    @Override
//    protected String specializeNamedComponentString()
//    {
//        return "Simple Type/Atomic Type; Base: "+baseType.getName();
//    }

//    @Override
//    public String toString()
//    {
//        return getName().toString();
//    }

    private final AtomicType baseType;
}
