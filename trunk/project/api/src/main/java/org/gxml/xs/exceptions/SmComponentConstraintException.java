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
package org.gxml.xs.exceptions;

import org.gxml.xs.enums.SmOutcome;


/**
 * Abstract base class for all Schema Component Constraint exceptions.
 */
@SuppressWarnings("serial")
public abstract class SmComponentConstraintException extends SmException
{
    public SmComponentConstraintException(final SmOutcome outcome, final String partNumber)
    {
        super(outcome, partNumber);
    }

    public SmComponentConstraintException(final SmOutcome outcome, final String partNumber, final SmException cause)
    {
        super(outcome, partNumber, cause);
    }
}
