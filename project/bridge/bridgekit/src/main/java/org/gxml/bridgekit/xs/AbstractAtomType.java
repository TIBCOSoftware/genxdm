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

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmType;

abstract class AbstractAtomType<A> implements SmAtomicType<A>
{
	/**
	 * Removes leading and trailing whitespace, and any leading plus sign provided it is followed by a digit (0 through 9).
	 * 
	 * @param strval
	 *            The value to be trimmed.
	 * @return A trimmed numeric representation ready for parsing.
	 * @throws NumberFormatException
	 *             if the number starts with two consecutive signs; http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5038425
	 */
	protected static String trim(final String strval) throws NumberFormatException
	{
		final String collapsed = strval.trim();

		final int collapsedLength = collapsed.length();

		if (collapsedLength > 1)
		{
			final char sign = collapsed.charAt(0);
			if (sign == '+' || sign == '-')
			{
				final char first = collapsed.charAt(1);

				if (first == '-' || first == '+')
				{
					throw new NumberFormatException(strval);
				}
				if (sign == '+' && first >= '0' && first <= '9')
				{
					return collapsed.substring(1);
				}
			}
		}
		else if (collapsedLength == 0 || collapsed.charAt(0) > '9' || collapsed.charAt(0) < '0')
		{
			throw new NumberFormatException(strval);
		}
		return collapsed;
	}

	protected final AtomBridge<A> atomBridge;

	private final SmType<A> baseType;

	private final QName name;

	AbstractAtomType(final QName name, final SmType<A> baseType, final AtomBridge<A> atomBridge)
	{
		this.name = PreCondition.assertArgumentNotNull(name);
		this.baseType = PreCondition.assertArgumentNotNull(baseType);
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge);
	}

	public final boolean derivedFromType(final SmType<A> ancestorType, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFromType(this, ancestorType, derivationMethods, atomBridge.getNameBridge());
	}

	public final SmType<A> getBaseType()
	{
		return baseType;
	}

	public final SmDerivationMethod getDerivationMethod()
	{
		return SmDerivationMethod.Restriction;
	}

	public final SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.ATOM;
	}

	public final String getLocalName()
	{
		return name.getLocalPart();
	}

	public final QName getName()
	{
		return name;
	}

	public final SmAtomicType<A> getNativeTypeDefinition()
	{
		return this;
	}

	public final String getTargetNamespace()
	{
		return name.getNamespaceURI();
	}

	public final boolean isAnonymous()
	{
		return false;
	}

	public boolean isAtomicType()
	{
		return true;
	}

	public final boolean isAtomicUrType()
	{
		return false;
	}

	public final boolean isChoice()
	{
		return false;
	}

	public final boolean isComplexUrType()
	{
		return false;
	}

	public final boolean isFinal(final SmDerivationMethod derivation)
	{
		switch (derivation)
		{
			case Extension:
			{
				return false;
			}
			default:
			{
				throw new AssertionError("isFinal(" + derivation + ")");
			}
		}
	}

	public final boolean isIDREFS()
	{
		return false;
	}

	public final boolean isListType()
	{
		return false;
	}

	public final boolean isNative()
	{
		return true;
	}

	public final boolean isNone()
	{
		return false;
	}

	public final boolean isSimpleUrType()
	{
		return false;
	}

	public final boolean isUnionType()
	{
		return false;
	}

	public final String normalize(final String initialValue)
	{
		return getWhiteSpacePolicy().apply(initialValue);
	}

	public final SmAtomicType<A> prime()
	{
		return this;
	}

	public final SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
	}

	public final boolean subtype(final SmPrimeType<A> rhs)
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
	public final String toString()
	{
		return name.toString();
	}

	public final List<A> validate(final List<? extends A> atoms) throws SmDatatypeException
	{
		final int size = atoms.size();
		switch (size)
		{
			case 1:
			{
				final A atom = atoms.get(0);
				final SmNativeType nativeType = atomBridge.getNativeType(atom);
				if (nativeType.isA(getNativeType()))
				{
					return atomBridge.wrapAtom(atom);
				}
				else if (nativeType == SmNativeType.UNTYPED_ATOMIC)
				{
					return validate(atomBridge.getC14NForm(atom));
				}
				else
				{
					// TODO: Need to investigate this because it is inefficient.
					return validate(atomBridge.getC14NForm(atom));
					// throw new AssertionError(nativeType + "=>" + getNativeType());
				}
			}
			default:
			{
				throw new AssertionError("validate(" + atoms + ")");
			}
		}
	}
}
