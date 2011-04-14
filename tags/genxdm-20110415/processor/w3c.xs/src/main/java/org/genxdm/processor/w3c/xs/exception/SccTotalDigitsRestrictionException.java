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

import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.exceptions.ComponentConstraintException;

@SuppressWarnings("serial")
final public class SccTotalDigitsRestrictionException extends ComponentConstraintException
{
	private final int m_totalDigits;
	private final int m_restrictedTotalDigits;

	public SccTotalDigitsRestrictionException(final int totalDigits, final int restrictedTotalDigits)
	{
		super(ValidationOutcome.SCC_TotalDigitsValidRestriction, "0");
		m_totalDigits = totalDigits;
		m_restrictedTotalDigits = restrictedTotalDigits;
	}

	@Override
	public String getMessage()
	{
		return "The {value}, " + m_totalDigits + ", of totalDigits must be less than or equal to the {value}, " + m_restrictedTotalDigits + ", of its inherited totalDigits.";
	}
}
