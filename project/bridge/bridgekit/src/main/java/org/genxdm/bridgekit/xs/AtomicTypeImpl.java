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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;

/**
 * An atomic type, but not the Atomic Ur-Type.
 * <p>
 * This is strictly used for derived atomic values
 * </p>
 */
public final class AtomicTypeImpl<A> extends SimpleTypeImpl<A> implements SmAtomicType<A>
{
	private static String collapseWhiteSpace(final String text)
	{
		// Note. We make no distinction between control characters less than ASCII 32
		// because we don't expect them in XML strings. We assume they are not there,
		// taken out by parsing. If we had to behave otherwise, we would end up
		// with annoying exception semantics and slow code.

		// trim() is our secret weapon #1. It costs virtually nothing because String
		// uses a special constructor to share the char[] value, so we only pay for the
		// String wrapper - pretty cheap by comparison. From now on, we only have to worry
		// about embedded whitespace, which allows the mainline (non whitespace) part of
		// the loop to be devoid of tests.
		final String trimmed = text.trim();

		int trimLength = trimmed.length();

		if (trimLength > 0)
		{
			if (trimLength < 3)
			{
				return trimmed;
			}
			else
			{
				// Fall through to handle (possibly) embedded whitespace.
			}
		}
		else
		{
			// It's all whitespace
			return "";
		}

		// Assume that this transformation is a no-op unless we discover otherwise.
		boolean noop = true;

		// Keep track of the number of characters required for the new char[] buffer.
		// Count down from the trimmed length to get this operation out of the mainline.
		int newLength = trimLength;

		// Used to detect consecutive whitespace characters.
		boolean inWhite = false;

		// For this loop, we only need to iterate over index > 0, index < trimLength -1
		// because we know that the first and last character are not whitespace.
		final int endIndex = trimLength - 1;

		for (int index = 1; index < endIndex; index++)
		{
			char c = trimmed.charAt(index);
			if (c <= ' ')
			{
				if (inWhite)
				{
					// Detected consecutive (embedded) whitespace characters that must be skipped.
					noop = false;

					newLength--;
				}
				else
				{
					if (c < ' ')
					{
						// Detected an (embedded) whitespace character that needs replacing.
						noop = false;
					}

					inWhite = true;
				}
			}
			else
			{
				inWhite = false;
			}
		}

		if (noop)
		{
			return trimmed;
		}
		else
		{
			char[] sb = new char[newLength];

			// The first character is not whitespace, so if we pre-process it,...
			sb[0] = trimmed.charAt(0);

			// 1. We are not in whitespace
			inWhite = false;

			// 2. Count begins at One.
			// Be careful, this count also acts as an index in this loop.
			int count = 1;

			// 3. Iterate over trimmed charcters excluding the first and last.
			for (int index = 1; index < endIndex; index++)
			{
				char c = trimmed.charAt(index);
				if (c <= ' ')
				{
					if (inWhite)
					{
						// Skip
					}
					else
					{
						sb[count++] = ' ';
						inWhite = true;
					}
				}
				else
				{
					inWhite = false;
					sb[count++] = c;
				}
			}

			// The last character is also not whitespace.
			sb[count++] = trimmed.charAt(endIndex);

			return new String(sb, 0, count);
		}
	}

	private static String replaceWhiteSpace(final String text)
	{
		return text.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	}

	private final SmAtomicType<A> baseType;

	public AtomicTypeImpl(final QName name, final boolean isAnonymous, final ScopeExtent scope, final SmAtomicType<A> baseType, final WhiteSpacePolicy whiteSpace, final AtomBridge<A> atomBridge)
	{
		super(name, isAnonymous, scope, DerivationMethod.Restriction, whiteSpace, atomBridge);
		this.baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmSequenceType<A> atomSet()
	{
		// Atomization has no effect.
		return this;
	}

	protected List<A> compile(final String initialValue) throws DatatypeException
	{
		final String normalizedValue = normalize(initialValue);
		try
		{
			final List<A> compiled = baseType.validate(initialValue);
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

	protected List<A> compile(final String initialValue, final PrefixResolver resolver) throws DatatypeException
	{
		final String normalizedValue = normalize(initialValue);
		try
		{
			final List<A> compiled = baseType.validate(initialValue, resolver);
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

	public SmSimpleType<A> getBaseType()
	{
		return baseType;
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.ATOM;
	}

	public SmNativeType getNativeType()
	{
		return baseType.getNativeType();
	}

	public SmAtomicType<A> getNativeTypeDefinition()
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
		return (getNativeType() == SmNativeType.ID);
	}

	public boolean isIDREF()
	{
		return (getNativeType() == SmNativeType.IDREF);
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
		final SmNativeType nativeType = getNativeType();
		if (nativeType.isToken())
		{
			return collapseWhiteSpace(initialValue);
		}
		else if (nativeType == SmNativeType.NORMALIZED_STRING)
		{
			return replaceWhiteSpace(initialValue);
		}
		else if (nativeType == SmNativeType.STRING)
		{
			return initialValue;
		}
		else if (nativeType == SmNativeType.UNTYPED_ATOMIC)
		{
			return initialValue;
		}
		else
		{
			return collapseWhiteSpace(initialValue);
		}
	}

	public final SmAtomicType<A> prime()
	{
		return this;
	}

	public KeeneQuantifier quantifier()
	{
		return KeeneQuantifier.EXACTLY_ONE;
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)rhs;
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
				final SmAtomicType<A> atomicType = (SmAtomicType<A>)rhs;
				return SmSupportImpl.subtype(this, atomicType);
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

	@Override
	public String toString()
	{
		return getName().toString();
	}
}
