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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmQuantifier;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.enums.SmWhiteSpacePolicy;
import org.gxml.xs.exceptions.SmDatatypeException;
import org.gxml.xs.resolve.SmPrefixResolver;
import org.gxml.xs.types.SmListSimpleType;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmType;

public final class ListTypeImpl<A> extends SimpleTypeImpl<A> implements SmListSimpleType<A>
{
	private final SmType<A> baseType;
	private final SmSimpleType<A> itemType;

	public ListTypeImpl(final QName name, final boolean isAnonymous, final SmScopeExtent scope, final SmSimpleType<A> itemType, final SmType<A> baseType, final SmWhiteSpacePolicy whiteSpace, final AtomBridge<A> atomBridge)
	{
		super(name, isAnonymous, scope, SmDerivationMethod.List, whiteSpace, atomBridge);
		this.itemType = PreCondition.assertArgumentNotNull(itemType, "itemType");
		this.baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	@SuppressWarnings("unused")
	private List<A> compile(final List<? extends A> value) throws SmDatatypeException
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
			catch (final SmDatatypeException e)
			{
				throw new SmDatatypeException(strval, this, e);
			}
		}
		return compiled;
	}

	protected List<A> compile(final String initialValue) throws SmDatatypeException
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
			catch (final SmDatatypeException e)
			{
				throw new SmDatatypeException(initialValue, this, e);
			}
		}
		return compiled;
	}

	protected List<A> compile(final String initialValue, final SmPrefixResolver resolver) throws SmDatatypeException
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
			catch (final SmDatatypeException e)
			{
				throw new SmDatatypeException(initialValue, this, e);
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

	public final SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		if (null != m_whiteSpace)
		{
			return m_whiteSpace;
		}
		else
		{
			// This is a bit freaky. Do we go to baseType?
			return SmWhiteSpacePolicy.PRESERVE;
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
		return SmWhiteSpacePolicy.COLLAPSE.apply(initialValue);
	}

	@Override
	public SmPrimeType<A> prime()
	{
		return itemType.prime();
	}

	@Override
	public SmQuantifier quantifier()
	{
		return SmQuantifier.ZERO_OR_MORE;
	}
}
