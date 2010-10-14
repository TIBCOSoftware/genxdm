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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.exceptions.SmComponentConstraintException;
import org.genxdm.xs.exceptions.SmSimpleTypeException;


@SuppressWarnings("serial")
public abstract class SccComplexTypeDefinitionException extends SmComponentConstraintException
{
	private final QName m_typeName;

	public static final String PART_PROPERTIES = "1";
	public static final String PART_BASE_SIMPLE_TYPE = "2";
	public static final String PART_CIRCULAR_DEFINITIONS = "3";
	public static final String PART_ATTRIBUTE_UNIQUENESS = "4";
	public static final String PART_ATTRIBUTE_DERIVED_FROM_ID = "5";
	public static final String PART_FIXED_OVERRIDE = "?";

	public SccComplexTypeDefinitionException(final String partNumber, final QName typeName)
	{
		super(SmOutcome.SCC_Complex_Type_Definition_Properties, partNumber);
		m_typeName = PreCondition.assertArgumentNotNull(typeName, "typeName");
	}

	public SccComplexTypeDefinitionException(final String partNumber, final QName typeName, final SmSimpleTypeException cause)
	{
		super(SmOutcome.SCC_Complex_Type_Definition_Properties, partNumber, cause);
		m_typeName = PreCondition.assertArgumentNotNull(typeName, "typeName");
	}

	public final QName getTypeName()
	{
		return m_typeName;
	}
}
