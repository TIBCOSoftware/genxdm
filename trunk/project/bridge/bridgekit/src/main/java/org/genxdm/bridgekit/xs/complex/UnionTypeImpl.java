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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.simple.SimpleTypeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.UnionSimpleType;

public final class UnionTypeImpl extends SimpleTypeImpl implements UnionSimpleType
{
	private final SimpleType m_baseType;
	private final Iterable<SimpleType> m_memberTypes;

	public UnionTypeImpl(final QName name, final boolean isAnonymous, final ScopeExtent scope, final SimpleType baseType, final Iterable<SimpleType> memberTypes, final WhiteSpacePolicy whiteSpace)
	{
		super(name, isAnonymous, scope, DerivationMethod.Union, whiteSpace);
		m_baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
		m_memberTypes = PreCondition.assertArgumentNotNull(memberTypes, "memberTypes");
	}

	@Override
	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	@SuppressWarnings("unused")
	private <A> List<A> compile(final List<? extends A> value, AtomBridge<A> atomBridge) throws DatatypeException
	{
		for (final SimpleType memberType : getMemberTypes())
		{
			try
			{
				return memberType.validate(value, atomBridge);
			}
			catch (final DatatypeException e)
			{
				// Try the next one.
			}
		}
		final String strval = atomBridge.getC14NString(value);
		throw new DatatypeException(strval, this);
	}

	protected <A> List<A> compile(final String initialValue, AtomBridge<A> bridge) throws DatatypeException
	{
		for (final SimpleType memberType : getMemberTypes())
		{
			try
			{
				return memberType.validate(initialValue, bridge);
			}
			catch (final DatatypeException e)
			{
				// Try the next one.
			}
		}
		throw new DatatypeException(initialValue, this);
	}

	protected <A> List<A> compile(final String initialValue, final PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException
	{
		for (final SimpleType memberType : getMemberTypes())
		{
			try
			{
				return memberType.validate(initialValue, resolver, bridge);
			}
			catch (final DatatypeException e)
			{
				// Try the next one.
			}
		}
		throw new DatatypeException(initialValue, this);
	}

	public SimpleType getBaseType()
	{
		return m_baseType;
	}

	public Iterable<SimpleType> getMemberTypes()
	{
		return m_memberTypes;
	}

	public NativeType getNativeType()
	{
		return NativeType.ANY_SIMPLE_TYPE;
	}

	public final WhiteSpacePolicy getWhiteSpacePolicy()
	{
		if (null != m_whiteSpace)
		{
			return m_whiteSpace;
		}
		else
		{
			return m_baseType.getWhiteSpacePolicy();
		}
	}

	public boolean isAtomicType()
	{
		return false;
	}

	public boolean isID()
	{
		return false;
	}

	public boolean isIDREF()
	{
		return false;
	}

	public boolean isIDREFS()
	{
		return false;
	}

	public boolean isListType()
	{
		return false;
	}

	public boolean isNative()
	{
		return false;
	}

	public boolean isUnionType()
	{
		return true;
	}

	public String normalize(final String initialValue)
	{
		return WhiteSpacePolicy.COLLAPSE.apply(initialValue);
	}

	public PrimeType prime()
	{
		PrimeType result = null;
		for (final SimpleType simpleType : m_memberTypes)
		{
			if (null == result)
			{
				result = simpleType.prime();
			}
			else
			{
				result = ZPrimeChoiceType.choice(result, simpleType.prime());
			}
		}
		return result;
	}

	public KeeneQuantifier quantifier()
	{
		return KeeneQuantifier.EXACTLY_ONE;
	}
}
