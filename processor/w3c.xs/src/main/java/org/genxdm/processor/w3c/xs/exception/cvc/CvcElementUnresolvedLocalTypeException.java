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
package org.genxdm.processor.w3c.xs.exception.cvc;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.sm.SmLocationException;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class CvcElementUnresolvedLocalTypeException extends SmLocationException
{
	private final QName m_localType;

	public CvcElementUnresolvedLocalTypeException(final QName localType, final QName elementName, final LocationInSchema location)
	{
		super(ValidationOutcome.CVC_Element_Locally_Valid, "", location);
		m_localType = PreCondition.assertArgumentNotNull(localType, "localType");
	}

	@Override
	public String getMessage()
	{
		final String localMessage = "The local type '" + m_localType + "' could not be resolved.";

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
		if (obj instanceof CvcElementUnresolvedLocalTypeException)
		{
			final CvcElementUnresolvedLocalTypeException e = (CvcElementUnresolvedLocalTypeException)obj;
			return m_localType.equals(e.m_localType);
		}
		else
		{
			return false;
		}
	}
}
