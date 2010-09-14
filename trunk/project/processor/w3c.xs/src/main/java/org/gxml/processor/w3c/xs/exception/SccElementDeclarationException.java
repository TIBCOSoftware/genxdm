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
import org.gxml.xs.enums.SmOutcome;
import org.gxml.xs.exceptions.SmComponentConstraintException;
import org.gxml.xs.exceptions.SmException;


@SuppressWarnings("serial")
public abstract class SccElementDeclarationException extends SmComponentConstraintException
{
	private final QName m_elementName;

	public static final String PART_SUBSTITUTION_GROUP_TYPE_DERIVATION = "4";
	public static final String PART_DERIVED_FROM_ID_WITH_VALUE_CONSTRAINT = "5";

	public SccElementDeclarationException(final String partNumber, final QName elementName)
	{
		super(SmOutcome.SCC_Element_Declaration_Properties_Correct, partNumber);
		m_elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}

	public SccElementDeclarationException(final String partNumber, final QName elementName, final SmException cause)
	{
		super(SmOutcome.SCC_Element_Declaration_Properties_Correct, partNumber, cause);
		m_elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");
	}

	public final QName getElementName()
	{
		return m_elementName;
	}
}