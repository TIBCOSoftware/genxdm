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

import org.gxml.xs.enums.SmOutcome;
import org.gxml.xs.exceptions.SmComponentConstraintException;

/**
 * Date: Mar 11, 2008
 */
@SuppressWarnings("serial")
final public class SccFractionDigitsRestrictionException extends SmComponentConstraintException
{
    private final int m_fractionDigits;
    private final int m_restrictedFractionDigits;

    public SccFractionDigitsRestrictionException(final int fractionDigits, final int restrictedFractionDigits)
    {
        super(SmOutcome.SCC_FractionDigitsValidRestriction, "0");
        m_fractionDigits = fractionDigits;
        m_restrictedFractionDigits = restrictedFractionDigits;
    }
    @Override
    public String getMessage()
    {
        return "The {value}, " + m_fractionDigits + ", of fractionDigits must be less than or equal to the {value}, " + m_restrictedFractionDigits + ", of its inherited fractionDigits.";
    }
}
