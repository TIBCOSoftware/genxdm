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
package org.gxml.processor.w3c.xs.exception;

import java.util.Iterator;
import java.util.Set;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmOutcome;
import org.gxml.xs.exceptions.SmComponentConstraintException;
import org.gxml.xs.types.SmType;


@SuppressWarnings("serial")
public abstract class SccTypeDerivationOKComplexException extends SmComponentConstraintException
{
	private final SmType<?> derivedType;
	private final SmType<?> m_baseName;
	private final Set<SmDerivationMethod> m_subset;

	public static final String PART_METHOD = "1";
	public static final String PART_HIERARCHY = "2";
	public static final String PART_COMPLEX_UR_TYPE = "2.3.1";
	public static final String PART_BASE_COMPLEX = "2.3.2.1";
	public static final String PART_BASE_SIMPLE = "2.3.2.2";

	public SccTypeDerivationOKComplexException(final String partNumber, final SmType<?> derivedType, final SmType<?> baseName, final Set<SmDerivationMethod> subset)
	{
		super(SmOutcome.SCC_Type_Derivation_OK_Complex, partNumber);
		this.derivedType = PreCondition.assertArgumentNotNull(derivedType, "derivedType");
		this.m_baseName = PreCondition.assertArgumentNotNull(baseName, "baseName");
		this.m_subset = PreCondition.assertArgumentNotNull(subset, "subset");
	}

	public SccTypeDerivationOKComplexException(final String partNumber, final SmType<?> derivedType, final SmType<?> baseName, final Set<SmDerivationMethod> subset, final SmComponentConstraintException cause)
	{
		super(SmOutcome.SCC_Type_Derivation_OK_Complex, partNumber, cause);
		this.derivedType = PreCondition.assertArgumentNotNull(derivedType, "derivedType");
		this.m_baseName = PreCondition.assertArgumentNotNull(baseName, "baseName");
		this.m_subset = PreCondition.assertArgumentNotNull(subset, "subset");
	}

	public final SmType<?> getDerivedType()
	{
		return derivedType;
	}

	public final SmType<?> getBaseName()
	{
		return m_baseName;
	}

	public final Set<SmDerivationMethod> getSubset()
	{
		return m_subset;
	}

	protected static String derivations(final Iterable<SmDerivationMethod> set)
	{
		final Iterator<SmDerivationMethod> it = set.iterator();
		if (it.hasNext())
		{
			final SmDerivationMethod first = it.next();
			if (it.hasNext())
			{
				final StringBuilder sb = new StringBuilder();
				sb.append(derivation(first));
				while (it.hasNext())
				{
					sb.append(" ");
					sb.append(derivation(it.next()));
				}
				return sb.toString();
			}
			else
			{
				return derivation(first);
			}
		}
		else
		{
			return "";
		}
	}

	protected static String derivation(final SmDerivationMethod derivation)
	{
		switch (derivation)
		{
			case Extension:
			{
				return "extension";
			}
			case Restriction:
			{
				return "restriction";
			}
			case Substitution:
			{
				return "substitution";
			}
			default:
			{
				throw new AssertionError(derivation);
			}
		}
	}
}
