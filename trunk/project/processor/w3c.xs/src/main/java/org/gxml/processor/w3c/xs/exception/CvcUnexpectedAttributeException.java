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
public final class CvcUnexpectedAttributeException extends SmComplexTypeException
{
	private final QName attributeName;

	public CvcUnexpectedAttributeException(final QName elementName, final QName attributeName, final SmLocation location)
	{
		super("?", elementName, location);
		this.attributeName = PreCondition.assertArgumentNotNull(attributeName, "name");
	}

	public QName getAttributeName()
	{
		return attributeName;
	}

	@Override
	public String getMessage()
	{
		return "Unexpected attribute '" + attributeName + "' on element '" + getElementName() + "'.";
	}

	public String getPatternKey()
	{
		return "UnexpectedAttributeException";
	}

	public Object[] getArguments()
	{
		return new Object[] { attributeName };
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcUnexpectedAttributeException)
		{
			final CvcUnexpectedAttributeException other = (CvcUnexpectedAttributeException)obj;
			return other.attributeName.equals(attributeName) && other.getElementName().equals(getElementName());
		}
		else
		{
			return false;
		}
	}
}
