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

import org.genxdm.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public final class SmUnexpectedEndException extends SmComplexTypeException
{
	public SmUnexpectedEndException(final QName elementName, final SmLocation location)
	{
		super(PART_CONTENT_TYPE_AND_CHILD_SEQUENCE, elementName, location);
	}

	@Override
	public String getMessage()
	{
		final String localMessage = "Unexpected end of content in element '" + getElementName() + "'.";

		final StringBuilder message = new StringBuilder();
		message.append(getOutcome().getSection());
		message.append(".");
		message.append(getPartNumber());
		message.append(": ");
		message.append(localMessage);
		return message.toString();
	}
}
