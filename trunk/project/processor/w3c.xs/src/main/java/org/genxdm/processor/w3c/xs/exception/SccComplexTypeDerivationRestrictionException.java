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
public abstract class SccComplexTypeDerivationRestrictionException extends ComponentConstraintException
{
	private final QName m_typeName;

	public static final String PART_TODO = "1";
	public static final String PART_ATTRIBUTE_REQUIRED_CONFLICT = "2.1.1";

	public SccComplexTypeDerivationRestrictionException(final String partNumber, final QName typeName)
	{
		super(ValidationOutcome.SCC_Derivation_Valid_Restriction_Complex, partNumber);
		m_typeName = PreCondition.assertArgumentNotNull(typeName, "typeName");
	}

	public SccComplexTypeDerivationRestrictionException(final String partNumber, final QName attributeName, final SimpleTypeException cause)
	{
		super(ValidationOutcome.SCC_Derivation_Valid_Restriction_Complex, partNumber, cause);
		m_typeName = PreCondition.assertArgumentNotNull(attributeName, "typeName");
	}

	public final QName getTypeName()
	{
		return m_typeName;
	}
}
