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
public final class CvcMissingAttributeDeclarationException extends SmComplexTypeException
{
	private final QName m_attributeName;

	public CvcMissingAttributeDeclarationException(final QName elementName, final QName attributeName, final SmLocation location)
	{
		super(PART_ATTRIBUTE_REQUIRED_MISSING, elementName, location);
		m_attributeName = PreCondition.assertArgumentNotNull(attributeName, "attributeName");
	}

	public QName getAttributeName()
	{
		return m_attributeName;
	}

	@Override
	public String getMessage()
	{
		return "Missing declaration for attribute " + m_attributeName.toString();
	}

	public String getPatternKey()
	{
		return "MissingAttributeDeclarationException";
	}

	public Object[] getArguments()
	{
		return new Object[] { m_attributeName };
	}
}
