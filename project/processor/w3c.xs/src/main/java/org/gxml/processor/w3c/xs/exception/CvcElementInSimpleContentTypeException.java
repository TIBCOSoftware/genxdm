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
package org.gxml.processor.w3c.xs.exception;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public final class CvcElementInSimpleContentTypeException extends SmComplexTypeException
{
	private final QName m_childName;
	private final SmLocation m_childLocation;

	public CvcElementInSimpleContentTypeException(final QName elementName, final SmLocation elementLocation, final QName childName, final SmLocation childLocation)
	{
		super(PART_CONTENT_TYPE_IS_SIMPLE, elementName, elementLocation);
		m_childName = PreCondition.assertArgumentNotNull(childName, "childName");
		m_childLocation = childLocation;
	}

	public QName getChildName()
	{
		return m_childName;
	}

	public SmLocation getChildLocation()
	{
		return m_childLocation;
	}

	@Override
	public String getMessage()
	{
		return "The element \"" + getElementName() + "\" cannot have children because the {content type} is Simple.";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcElementInSimpleContentTypeException)
		{
			final CvcElementInSimpleContentTypeException e = (CvcElementInSimpleContentTypeException)obj;
			return e.getElementName().equals(getElementName());
		}
		else
		{
			return false;
		}
	}
}
