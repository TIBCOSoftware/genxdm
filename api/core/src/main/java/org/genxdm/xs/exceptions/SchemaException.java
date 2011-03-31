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
package org.genxdm.xs.exceptions;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.ValidationOutcome;

/**
 * Base for W3C XML Schema exceptions.
 */
@SuppressWarnings("serial")
public abstract class SchemaException extends Exception
{
	private final ValidationOutcome m_outcome;
	private final String m_partNumber;

	public SchemaException(final ValidationOutcome outcome, final String partNumber)
	{
		m_outcome = PreCondition.assertArgumentNotNull(outcome, "outcome");
		m_partNumber = PreCondition.assertArgumentNotNull(partNumber, "partNumber");
	}

	public SchemaException(final ValidationOutcome outcome, final String partNumber, final SchemaException cause)
	{
		super(cause);
		m_outcome = PreCondition.assertArgumentNotNull(outcome, "outcome");
		m_partNumber = PreCondition.assertArgumentNotNull(partNumber, "partNumber");
	}

	public final ValidationOutcome getOutcome()
	{
		return m_outcome;
	}

	public final String getPartNumber()
	{
		return m_partNumber;
	}

	@Override
	public SchemaException getCause()
	{
		return (SchemaException)super.getCause();
	}
}
