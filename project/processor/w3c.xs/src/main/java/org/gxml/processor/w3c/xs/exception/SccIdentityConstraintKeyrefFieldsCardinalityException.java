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

@SuppressWarnings("serial")
public final class SccIdentityConstraintKeyrefFieldsCardinalityException extends SccIdentityConstraintDefinitionException
{
	private final int m_constraintSize;
	private QName m_referencedName;
	private final int m_referencedFieldSize;

	public SccIdentityConstraintKeyrefFieldsCardinalityException(final QName constraintName, final int constraintSize, final QName referencedName, final int referencedFieldSize)
	{
		super(constraintName, PART_KEYREF_FIELDS_CARDINALITY);
		m_constraintSize = constraintSize;
		m_referencedName = PreCondition.assertArgumentNotNull(referencedName, "referencedName");
		m_referencedFieldSize = referencedFieldSize;
	}

	@Override
	public String getMessage()
	{
		return "Mismatch between size of constraint fields (" + m_constraintSize + ") of the constraint, '" + getConstraintName() + "', and size of fields (" + m_referencedFieldSize + ") for the referenced constraint, '" + m_referencedName + "'.";
	}
}
