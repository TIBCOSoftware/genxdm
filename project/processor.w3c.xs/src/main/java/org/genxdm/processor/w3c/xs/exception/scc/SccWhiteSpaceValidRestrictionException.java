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

import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.exceptions.ComponentConstraintException;

@SuppressWarnings("serial")
abstract public class SccWhiteSpaceValidRestrictionException extends ComponentConstraintException
{
    public static final String PART_PARENT_IS_COLLAPSE = "1";
    public static final String PART_PARENT_IS_REPLACE = "2";

    public SccWhiteSpaceValidRestrictionException(final String partNumber)
    {
        super(ValidationOutcome.SCC_WhiteSpaceValidRestriction, partNumber);
    }
}
