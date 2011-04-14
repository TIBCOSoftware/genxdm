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
package org.genxdm.processor.w3c.xs.exception;

import java.util.Iterator;
import java.util.Set;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.exceptions.ComponentConstraintException;
import org.genxdm.xs.types.Type;


@SuppressWarnings("serial")
public abstract class SccTypeDerivationOKComplexException extends ComponentConstraintException
{
	private final Type<?> derivedType;
	private final Type<?> m_baseName;
	private final Set<DerivationMethod> m_subset;

	public static final String PART_METHOD = "1";
	public static final String PART_HIERARCHY = "2";
	public static final String PART_COMPLEX_UR_TYPE = "2.3.1";
	public static final String PART_BASE_COMPLEX = "2.3.2.1";
	public static final String PART_BASE_SIMPLE = "2.3.2.2";

	public SccTypeDerivationOKComplexException(final String partNumber, final Type<?> derivedType, final Type<?> baseName, final Set<DerivationMethod> subset)
	{
		super(ValidationOutcome.SCC_Type_Derivation_OK_Complex, partNumber);
		this.derivedType = PreCondition.assertArgumentNotNull(derivedType, "derivedType");
		this.m_baseName = PreCondition.assertArgumentNotNull(baseName, "baseName");
		this.m_subset = PreCondition.assertArgumentNotNull(subset, "subset");
	}

	public SccTypeDerivationOKComplexException(final String partNumber, final Type<?> derivedType, final Type<?> baseName, final Set<DerivationMethod> subset, final ComponentConstraintException cause)
	{
		super(ValidationOutcome.SCC_Type_Derivation_OK_Complex, partNumber, cause);
		this.derivedType = PreCondition.assertArgumentNotNull(derivedType, "derivedType");
		this.m_baseName = PreCondition.assertArgumentNotNull(baseName, "baseName");
		this.m_subset = PreCondition.assertArgumentNotNull(subset, "subset");
	}

	public final Type<?> getDerivedType()
	{
		return derivedType;
	}

	public final Type<?> getBaseName()
	{
		return m_baseName;
	}

	public final Set<DerivationMethod> getSubset()
	{
		return m_subset;
	}

	protected static String derivations(final Iterable<DerivationMethod> set)
	{
		final Iterator<DerivationMethod> it = set.iterator();
		if (it.hasNext())
		{
			final DerivationMethod first = it.next();
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

	protected static String derivation(final DerivationMethod derivation)
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
