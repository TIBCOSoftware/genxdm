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
import org.genxdm.xs.exceptions.SmComponentConstraintException;
import org.genxdm.xs.exceptions.SmException;


@SuppressWarnings("serial")
public abstract class SccDerivationExtensionException extends SmComponentConstraintException
{
	private final QName m_complexTypeName;

	public static final String PART_WHEN_BASE_COMPLEX_TYPE_FINAL_OF_BASE_MUST_NOT_CONTAINT_EXTENSION = "1.1";
	public static final String PART_WHEN_BASE_COMPLEX_TYPE_CONTENT_TYPE = "1.4";
	public static final String PART_WHEN_BASE_SIMPLE_TYPE_CONTENT_TYPE_MUST_BE_SAME_SIMPLE_TYPE_DEFINITION = "2.1";
	public static final String PART_WHEN_BASE_SIMPLE_TYPE_FINAL_OF_BASE_MUST_NOT_CONTAINT_EXTENSION = "2.2";

	public SccDerivationExtensionException(final String partNumber, final QName complexTypeName)
	{
		super(SmOutcome.SCC_Derivation_Valid_Extension, partNumber);
		m_complexTypeName = PreCondition.assertArgumentNotNull(complexTypeName, "complexTypeName");
	}

	public SccDerivationExtensionException(final String partNumber, final QName complexTypeName, final SmException cause)
	{
		super(SmOutcome.SCC_Derivation_Valid_Extension, partNumber, PreCondition.assertArgumentNotNull(cause, "cause"));
		m_complexTypeName = PreCondition.assertArgumentNotNull(complexTypeName, "complexTypeName");
	}

	public QName getComplexTypeName()
	{
		return m_complexTypeName;
	}
}
