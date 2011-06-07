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
import org.genxdm.processor.w3c.xs.exception.sm.SmComplexTypeException;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException extends SmComplexTypeException
{
	private final String m_text;

	public CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(final QName elementName, final String text, final LocationInSchema location)
	{
		super(PART_CONTENT_TYPE_ELEMENTONLY_AND_NON_WHITE_SPACE, elementName, location);
		m_text = PreCondition.assertArgumentNotNull(text, "text");
	}

	public String getText()
	{
		return m_text;
	}

	@Override
	public String getMessage()
	{
		return "Unexpected non-whitespace text '" + m_text + "' in element-only content.";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException)
		{
			final CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException other = (CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException)obj;
			return m_text.equals(other.m_text);
		}
		else
		{
			return false;
		}
	}
}
