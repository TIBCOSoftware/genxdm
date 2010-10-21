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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.SmListSimpleType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;

public final class ListTypeImpl<A> extends SimpleTypeImpl<A> implements SmListSimpleType<A>
{
	private final SmType<A> baseType;
	private final SmSimpleType<A> itemType;

	public ListTypeImpl(final QName name, final boolean isAnonymous, final ScopeExtent scope, final SmSimpleType<A> itemType, final SmType<A> baseType, final WhiteSpacePolicy whiteSpace, final AtomBridge<A> atomBridge)
	{
		super(name, isAnonymous, scope, DerivationMethod.List, whiteSpace, atomBridge);
		this.itemType = PreCondition.assertArgumentNotNull(itemType, "itemType");
		this.baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	@SuppressWarnings("unused")
	private List<A> compile(final List<? extends A> value) throws DatatypeException
	{
		PreCondition.assertArgumentNotNull(value, "value");

		final String strval = atomBridge.getC14NString(value);

		final ArrayList<A> compiled = new ArrayList<A>();
		final SmSimpleType<A> itemType = this.getItemType();
		final StringTokenizer tokenizer = new StringTokenizer(strval, " ");
		while (tokenizer.hasMoreTokens())
		{
			final String token = tokenizer.nextToken();
			try
			{
				final List<A> itemResult = itemType.validate(token);
				for (final A atom : itemResult)
				{
					compiled.add(atom);
				}
			}
			catch (final DatatypeException e)
			{
				throw new DatatypeException(strval, this, e);
			}
		}
		return compiled;
	}

	protected List<A> compile(final String initialValue) throws DatatypeException
	{
		PreCondition.assertArgumentNotNull(initialValue, "value");

		final ArrayList<A> compiled = new ArrayList<A>();
		final SmSimpleType<A> itemType = this.getItemType();
		final StringTokenizer tokenizer = new StringTokenizer(initialValue, " ");
		while (tokenizer.hasMoreTokens())
		{
			final String token = tokenizer.nextToken();
			final String normal = itemType.normalize(token);
			try
			{
				final List<? extends A> itemResult = itemType.validate(normal);
				for (final A atom : itemResult)
				{
					compiled.add(atom);
				}
			}
			catch (final DatatypeException e)
			{
				throw new DatatypeException(initialValue, this, e);
			}
		}
		return compiled;
	}

	protected List<A> compile(final String initialValue, final PrefixResolver resolver) throws DatatypeException
	{
		PreCondition.assertArgumentNotNull(initialValue, "value");

		final ArrayList<A> compiled = new ArrayList<A>();
		final SmSimpleType<A> itemType = this.getItemType();
		final StringTokenizer tokenizer = new StringTokenizer(initialValue, " ");
		while (tokenizer.hasMoreTokens())
		{
			final String token = tokenizer.nextToken();
			final String normal = itemType.normalize(token);
			try
			{
				final List<? extends A> itemResult = itemType.validate(normal, resolver);
				for (final A atom : itemResult)
				{
					compiled.add(atom);
				}
			}
			catch (final DatatypeException e)
			{
				throw new DatatypeException(initialValue, this, e);
			}
		}
		return compiled;
	}

	public SmType<A> getBaseType()
	{
		return baseType;
	}

	public SmSimpleType<A> getItemType()
	{
		return itemType;
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.ANY_SIMPLE_TYPE;
	}

	public final WhiteSpacePolicy getWhiteSpacePolicy()
	{
		if (null != m_whiteSpace)
		{
			return m_whiteSpace;
		}
		else
		{
			// This is a bit freaky. Do we go to baseType?
			return WhiteSpacePolicy.PRESERVE;
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
		return itemType.isIDREF();
	}

	public boolean isListType()
	{
		return true;
	}

	public boolean isNative()
	{
		return false;
	}

	public boolean isUnionType()
	{
		return false;
	}

	public String normalize(final String initialValue)
	{
		return WhiteSpacePolicy.COLLAPSE.apply(initialValue);
	}

	@Override
	public SmPrimeType<A> prime()
	{
		return itemType.prime();
	}

	@Override
	public KeeneQuantifier quantifier()
	{
		return KeeneQuantifier.ZERO_OR_MORE;
	}
}
