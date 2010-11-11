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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.exceptions.ComponentConstraintException;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.Type;

@SuppressWarnings("serial")
public final class CvcElementLocalTypeDerivationException extends CvcElementException
{
	private final Type<?> m_localType;

	public CvcElementLocalTypeDerivationException(final Type<?> localType, final ElementDefinition<?> elementDeclaration, final ComponentConstraintException cause, final LocationInSchema location)
	{
		super(PART_LOCAL_TYPE_DERIVATION, elementDeclaration, location, cause);
		m_localType = PreCondition.assertArgumentNotNull(localType, "localType");
	}

	@Override
	public String getMessage()
	{
		final String localMessage = "The local type '" + m_localType + "' is not validly derived from the type definition '" + getElementDeclaration().getType() + "', of element '" + getElementDeclaration() + "'.";

		final StringBuilder message = new StringBuilder();
		message.append(getOutcome().getSection());
		message.append(".");
		message.append(getPartNumber());
		message.append(": ");
		message.append(localMessage);
		return message.toString();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcElementLocalTypeDerivationException)
		{
			final CvcElementLocalTypeDerivationException e = (CvcElementLocalTypeDerivationException)obj;
			return (m_localType.equals(e.m_localType) && getElementDeclaration().equals(e.getElementDeclaration()));
		}
		else
		{
			return false;
		}
	}
}
