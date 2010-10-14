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
public final class CvcProhibitedAttributeException extends SmComplexTypeException
{
	private final QName m_attributeName;

	public CvcProhibitedAttributeException(final QName elementName, final QName attributeName, final SmLocation location)
	{
		super(SmComplexTypeException.PART_ATTRIBUTE_VALID, elementName, location);
		this.m_attributeName = PreCondition.assertArgumentNotNull(attributeName, "attributeName");
	}

	public QName getAttributeName()
	{
		return m_attributeName;
	}

	public String getMessage()
	{
		return "Prohibited attribute: " + m_attributeName;
	}

	public String getPatternKey()
	{
		return "ProhibitedAttributeException";
	}

	public Object[] getArguments()
	{
		return new Object[] { m_attributeName };
	}

	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcProhibitedAttributeException)
		{
			final CvcProhibitedAttributeException other = (CvcProhibitedAttributeException)obj;
			return other.m_attributeName.equals(m_attributeName);
		}
		else
		{
			return false;
		}
	}
}
