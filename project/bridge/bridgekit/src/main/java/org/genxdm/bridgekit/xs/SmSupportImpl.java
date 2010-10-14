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
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmType;

/**
 * Implementation helper functions.
 */
public final class SmSupportImpl
{
	/**
	 * Determines whether lhs is derived from rhs based upon walking up from lhs towards the complex Ur-type.
	 */
	public static <A> boolean subtype(final SmType<A> lhs, final SmType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		if (!rhs.isComplexUrType())
		{
			SmType<A> currentType = lhs;
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
	public static <A> boolean derivedFromType(final SmType<A> candidateType, final SmType<A> ancestorType, final Set<SmDerivationMethod> derivationMethods, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(candidateType, "candidateType");
		PreCondition.assertArgumentNotNull(ancestorType, "ancestorType");
		PreCondition.assertArgumentNotNull(derivationMethods, "derivationMethods");
		PreCondition.assertArgumentNotNull(nameBridge, "nameBridge");

		return derivedFrom(candidateType, ancestorType.getTargetNamespace(), ancestorType.getLocalName(), derivationMethods, nameBridge);
	}

	public static <A> boolean derivedFrom(final SmType<A> candidateType, final String namespace, final String localName, final Set<SmDerivationMethod> derivationMethods, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(candidateType, "candidateType");
		PreCondition.assertArgumentNotNull(namespace, "namespace");
		PreCondition.assertArgumentNotNull(localName, "localName");
		PreCondition.assertArgumentNotNull(derivationMethods, "derivationMethods");

		SmType<A> currentType = candidateType;

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

	public static <A> boolean subtype(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");

		final SmQuantifier qLHS = lhs.quantifier();
		final SmQuantifier qRHS = rhs.quantifier();
		if (qLHS.isOptional() && lhs.prime().isNone())
		{
			return qRHS.contains(qLHS);
		}
		else
		{
			if (qRHS.contains(qLHS))
			{
				final SmPrimeType<A> pLHS = lhs.prime();
				final SmPrimeType<A> pRHS = rhs.prime();
				return pLHS.subtype(pRHS);
			}
			else
			{
				return false;
			}
		}
	}
}
