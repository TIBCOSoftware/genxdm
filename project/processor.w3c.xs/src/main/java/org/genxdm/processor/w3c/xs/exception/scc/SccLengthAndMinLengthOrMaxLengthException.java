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
public abstract class SccLengthAndMinLengthOrMaxLengthException extends ComponentConstraintException
{
    public static final String PART_MIN_LENGTH_COMPATIBLE = "1.1";
    public static final String PART_MIN_LENGTH_DERIVED = "1.2";
    public static final String PART_MAX_LENGTH_COMPATIBLE = "2.1";
    public static final String PART_MAX_LENGTH_DERIVED = "2.2";

    public SccLengthAndMinLengthOrMaxLengthException(final String partNumber)
    {
        super(ValidationOutcome.SCC_Length_MinLength_Or_MaxLength, partNumber);
    }
}
