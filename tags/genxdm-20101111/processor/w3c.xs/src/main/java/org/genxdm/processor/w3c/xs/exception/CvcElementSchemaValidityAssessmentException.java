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
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.resolve.LocationInSchema;

/**
 * Corresponds to the <b>Schema-Validity Assessment (Element)</b> validation rule.
 */
@SuppressWarnings("serial")
public abstract class CvcElementSchemaValidityAssessmentException extends SmLocationException
{
	private final QName elementName;

	public static final String PART_ONE = "1";
	public static final String PART_TWO = "1";

	public CvcElementSchemaValidityAssessmentException(final String partNumber, final QName elementName, final LocationInSchema location)
	{
		super(ValidationOutcome.CVC_Schema_Validity_Assessment_Element, partNumber, location);
		this.elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}

	public final QName getElementName()
	{
		return elementName;
	}
}
