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
package org.genxdm.xs.exceptions;

import org.genxdm.exceptions.PreCondition;

/**
 * This exception arises when a choice is made to abort the current schema processing. This choice always follows the
 * raising of an {@link SchemaException}. This exception is intentionally not-derived from {@link SchemaException}.
 */
@SuppressWarnings("serial")
public final class AbortException extends Exception
{
    public AbortException(final SchemaException cause)
    {
        super(PreCondition.assertArgumentNotNull(cause, "cause"));
    }

    @Override
    public SchemaException getCause()
    {
        return (SchemaException)super.getCause();
    }

    @Override
    public String getMessage()
    {
        return getCause().getMessage();
    }
}
