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
import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.resolve.SmLocation;

/**
 * Validation Rule: Element Locally Valid (Complex Type)
 */
@SuppressWarnings("serial")
public abstract class SmComplexTypeException extends SmLocationException
{
	public static final String PART_ABSTRACT_FALSE = "1";
	public static final String PART_CONTENT_TYPE_IS_EMPTY = "2.1";
	public static final String PART_CONTENT_TYPE_IS_SIMPLE = "2.2";
	public static final String PART_CONTENT_TYPE_ELEMENTONLY_AND_NON_WHITE_SPACE = "2.3";
	public static final String PART_CONTENT_TYPE_AND_CHILD_SEQUENCE = "2.4";
	public static final String PART_ATTRIBUTE_VALID = "3.1";
	public static final String PART_WILDCARD_ABSENT = "3.2.1";
	public static final String PART_WILDCARD_MATCH = "3.2.2";
	public static final String PART_ATTRIBUTE_REQUIRED_MISSING = "4";

	private final QName elementName;

	public final QName getElementName()
	{
		return elementName;
	}

	public SmComplexTypeException(final String partNumber, final QName elementName, final SmLocation elementLocation)
	{
		super(SmOutcome.CVC_Complex_Type, partNumber, elementLocation);
		this.elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}

	public SmComplexTypeException(final String partNumber, final QName elementName, final SmLocation elementLocation, final SmException cause)
	{
		super(SmOutcome.CVC_Complex_Type, partNumber, elementLocation, PreCondition.assertArgumentNotNull(cause, "cause"));
		this.elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}
}
