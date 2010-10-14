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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.exceptions.SmSimpleTypeException;
import org.genxdm.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public final class SmAttributeUseException extends SmComplexTypeException
{
	private final QName attributeName;

	public SmAttributeUseException(final QName elementName, final QName attributeName, final SmLocation location, final SmSimpleTypeException cause)
	{
		super(PART_ATTRIBUTE_VALID, elementName, location, cause);
		this.attributeName = PreCondition.assertArgumentNotNull(attributeName, "name");
	}

	public SmAttributeUseException(final QName elementName, final QName attributeName, final SmLocation location, final CvcIDException cause)
	{
		super(PART_ATTRIBUTE_VALID, elementName, location, cause);
		this.attributeName = PreCondition.assertArgumentNotNull(attributeName, "name");
	}

	public QName getAttributeName()
	{
		return attributeName;
	}

	@Override
	public String getMessage()
	{
		final String localMessage = "The attribute, '" + attributeName + "', is not valid with respect to its attribute use.";

		final StringBuilder message = new StringBuilder();
		message.append(getOutcome().getSection());
		message.append(".");
		message.append(getPartNumber());
		message.append(": ");
		message.append(localMessage);
		return message.toString();
	}
}
