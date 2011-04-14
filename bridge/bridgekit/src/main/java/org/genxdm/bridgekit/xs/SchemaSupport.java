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

import java.util.Set;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.Type;

/**
 * Implementation helper functions.
 */
public final class SchemaSupport
{
	/**
	 * Determines whether lhs is derived from rhs based upon walking up from lhs towards the complex Ur-type.
	 */
	public static <A> boolean subtype(final Type<A> lhs, final Type<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		if (!rhs.isComplexUrType())
		{
			Type<A> currentType = lhs;
			while (true)
			{
				if (currentType == rhs)
				{
					return true;
				}
				else
				{
					if (!currentType.isComplexUrType())
					{
						currentType = currentType.getBaseType();
					}
					else
					{
						return false;
					}
				}
			}
		}
		else
		{
			// All item types are derived from the Complex Ur-type.
			return true;
		}
	}

	/**
	 * Determines whether the candidateType is derived from the ancestorTYpe based upon walking up from lhs towards the complex Ur-type.
	 */
	public static <A> boolean derivedFromType(final Type<A> candidateType, final Type<A> ancestorType, final Set<DerivationMethod> derivationMethods, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(candidateType, "candidateType");
		PreCondition.assertArgumentNotNull(ancestorType, "ancestorType");
		PreCondition.assertArgumentNotNull(derivationMethods, "derivationMethods");
		PreCondition.assertArgumentNotNull(nameBridge, "nameBridge");

		return derivedFrom(candidateType, ancestorType.getTargetNamespace(), ancestorType.getLocalName(), derivationMethods, nameBridge);
	}

	public static <A> boolean derivedFrom(final Type<A> candidateType, final String namespace, final String localName, final Set<DerivationMethod> derivationMethods, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(candidateType, "candidateType");
		PreCondition.assertArgumentNotNull(namespace, "namespace");
		PreCondition.assertArgumentNotNull(localName, "localName");
		PreCondition.assertArgumentNotNull(derivationMethods, "derivationMethods");

		Type<A> currentType = candidateType;

		if (currentType.isComplexUrType())
		{
			return false;
		}
		else
		{
			if (derivationMethods.contains(currentType.getDerivationMethod()))
			{
				currentType = currentType.getBaseType();
			}
			else
			{
				return false;
			}
		}

		while (true)
		{
			if (namespace == currentType.getTargetNamespace() && localName == currentType.getLocalName())
			{
				return true;
			}
			else
			{
				if (!currentType.isComplexUrType())
				{
					if (derivationMethods.contains(currentType.getDerivationMethod()))
					{
						currentType = currentType.getBaseType();
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
		}
	}

	public static <A> boolean subtype(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");

		final KeeneQuantifier qLHS = lhs.quantifier();
		final KeeneQuantifier qRHS = rhs.quantifier();
		if (qLHS.isOptional() && lhs.prime().isNone())
		{
			return qRHS.contains(qLHS);
		}
		else
		{
			if (qRHS.contains(qLHS))
			{
				final PrimeType<A> pLHS = lhs.prime();
				final PrimeType<A> pRHS = rhs.prime();
				return pLHS.subtype(pRHS);
			}
			else
			{
				return false;
			}
		}
	}
}
