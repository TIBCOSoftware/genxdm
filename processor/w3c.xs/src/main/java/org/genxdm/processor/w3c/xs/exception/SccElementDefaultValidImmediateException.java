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
import org.genxdm.xs.exceptions.ComponentConstraintException;
import org.genxdm.xs.exceptions.SimpleTypeException;


@SuppressWarnings("serial")
public abstract class SccElementDefaultValidImmediateException extends ComponentConstraintException
{
	private final QName m_elementName;

	public static final String PART_CONTENT_TYPE_SIMPLE_OR_MIXED = "2.1";

	public SccElementDefaultValidImmediateException(final String partNumber, final QName elementName)
	{
		super(ValidationOutcome.SCC_Element_Default_Valid_Immediate, partNumber);
		m_elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}

	public SccElementDefaultValidImmediateException(final String partNumber, final QName elementName, final SimpleTypeException cause)
	{
		super(ValidationOutcome.SCC_Element_Default_Valid_Immediate, partNumber, cause);
		m_elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}

	public final QName getElementName()
	{
		return m_elementName;
	}
}
