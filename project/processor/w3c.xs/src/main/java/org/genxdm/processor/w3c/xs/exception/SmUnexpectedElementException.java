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
import org.genxdm.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public final class SmUnexpectedElementException extends SmComplexTypeException
{
	private final QName m_childName;

	public SmUnexpectedElementException(final QName elementName, final SmLocation location, final QName childName, final SmLocation childLocation)
	{
		super(PART_CONTENT_TYPE_AND_CHILD_SEQUENCE, elementName, location);
		m_childName = PreCondition.assertArgumentNotNull(childName, "childName");
	}

	public QName getChildName()
	{
		return m_childName;
	}

	@Override
	public String getMessage()
	{
		return "Unexpected element sequence, got '" + m_childName + "' in " + getElementName() + ".";
	}

	public boolean equals(final Object obj)
	{
		if (obj instanceof SmUnexpectedElementException)
		{
			final SmUnexpectedElementException other = (SmUnexpectedElementException)obj;
			return getElementName().equals(other.getElementName());
		}
		else
		{
			return false;
		}
	}
}
