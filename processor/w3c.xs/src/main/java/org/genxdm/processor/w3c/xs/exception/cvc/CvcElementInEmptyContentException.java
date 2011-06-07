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
public final class CvcElementInEmptyContentException extends SmComplexTypeException
{
	private final QName m_childName;
	private final LocationInSchema m_childLocation;

	public CvcElementInEmptyContentException(final QName elementName, final LocationInSchema elementLocation, final QName childName, final LocationInSchema childLocation)
	{
		super(PART_CONTENT_TYPE_IS_EMPTY, elementName, elementLocation);
		m_childName = PreCondition.assertArgumentNotNull(childName, "childName");
		m_childLocation = childLocation;
	}

	public QName getChildName()
	{
		return m_childName;
	}

	public LocationInSchema getChildLocation()
	{
		return m_childLocation;
	}

	@Override
	public String getMessage()
	{
		return "Element " + m_childName + " is not allowed as a child of " + getElementName() + " because the {content type} is Empty.";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcElementInEmptyContentException)
		{
			final CvcElementInEmptyContentException other = (CvcElementInEmptyContentException)obj;
			return m_childName.equals(other.m_childName);
		}
		else
		{
			return false;
		}
	}
}
