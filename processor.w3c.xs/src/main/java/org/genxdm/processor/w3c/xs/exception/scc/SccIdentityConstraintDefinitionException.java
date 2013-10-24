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
package org.genxdm.processor.w3c.xs.exception.scc;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.exceptions.ComponentConstraintException;


@SuppressWarnings("serial")
public abstract class SccIdentityConstraintDefinitionException extends ComponentConstraintException
{
    public static final String PART_REFER_IS_KEY_OR_UNIQUE = "?";
    public static final String PART_KEYREF_FIELDS_CARDINALITY = "2";

    private final QName m_constraintName;

    public SccIdentityConstraintDefinitionException(final QName constraintName, final String partNumber)
    {
        super(ValidationOutcome.SCC_Identity_Constraint_Definition_Properties_Correct, partNumber);
        m_constraintName = PreCondition.assertArgumentNotNull(constraintName, "constraintName");
    }

    public QName getConstraintName()
    {
        return m_constraintName;
    }
}
