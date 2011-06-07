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
package org.genxdm.processor.w3c.xs.exception.scc;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

@SuppressWarnings("serial")
public final class SccAttributeDeclarationValueConstraintException extends SccAttributeDeclarationException
{
	private final String m_expectValue;

	public SccAttributeDeclarationValueConstraintException(final QName attributeName, final String normalizedValue)
	{
		super(PART_VALUE_CONSTRAINT, attributeName);
		m_expectValue = PreCondition.assertArgumentNotNull(normalizedValue, "expectValue");
	}

	@Override
	public String getMessage()
	{
		return "The {value constraint} value, '" + m_expectValue + "', must be valid with respect to the {type definition}.";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof SccAttributeDeclarationValueConstraintException)
		{
			final SccAttributeDeclarationValueConstraintException e = (SccAttributeDeclarationValueConstraintException)obj;
			return e.getAttributeName().equals(getAttributeName()) && e.m_expectValue.equals(m_expectValue);
		}
		else
		{
			return false;
		}
	}
}
