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
public final class CvcAttributeNormalizedValueException extends CvcAttributeException
{
	private final String normalizedValue;

	public CvcAttributeNormalizedValueException(final QName attributeName, final String normalizedValue, final SmLocation location, final SmSimpleTypeException cause)
	{
		super(PART_NORMALIZED_VALUE, attributeName, location, cause);
		this.normalizedValue = PreCondition.assertArgumentNotNull(normalizedValue, "normalizedValue");
	}

	@Override
	public String getMessage()
	{
		return "The normalized value, '" + normalizedValue + "', of the attribute " + getAttributeName() + " is not locally valid with respect to the {type definition}.";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcAttributeNormalizedValueException)
		{
			final CvcAttributeNormalizedValueException e = (CvcAttributeNormalizedValueException)obj;
			return e.getAttributeName().equals(getAttributeName()) && e.normalizedValue.equals(normalizedValue);
		}
		else
		{
			return false;
		}
	}
}
