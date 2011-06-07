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
public abstract class SccDerivationRestrictionSimpleException extends ComponentConstraintException
{
	private final QName m_simpleType;

	public static final String PART_ITEM_TYPE_MUST_BE_ATOMIC_OR_UNION = "2.1";
	public static final String PART_MEMBER_TYPE_MUST_BE_ATOMIC_OR_LIST = "3.1";

	public SccDerivationRestrictionSimpleException(final String partNumber, final QName simpleType)
	{
		super(ValidationOutcome.SCC_Derivation_Valid_Restriction_Simple, partNumber);
		m_simpleType = PreCondition.assertArgumentNotNull(simpleType, "simpleType");
	}

	public QName getSimpleType()
	{
		return m_simpleType;
	}
}
