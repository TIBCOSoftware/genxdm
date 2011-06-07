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
import org.genxdm.processor.w3c.xs.exception.sm.SmLocationException;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public abstract class CvcTypeException extends SmLocationException
{
	private final QName m_elementName;

	public static final String PART_ABSENT = "1";
	public static final String PART_ABSTRACT = "2";
	public static final String PART_SIMPLE_TYPE_NO_CHILDREN = "3.1.2";

	public CvcTypeException(final String partNumber, final QName elementName, final LocationInSchema elementLocation)
	{
		super(ValidationOutcome.CVC_Type, partNumber, elementLocation);
		this.m_elementName = PreCondition.assertArgumentNotNull(elementName, "element");
	}

	public QName getElementName()
	{
		return m_elementName;
	}
}
