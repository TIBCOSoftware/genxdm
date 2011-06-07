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

import java.math.BigInteger;

import org.genxdm.exceptions.PreCondition;

/**
 * Used to indicate an implementation limitation wherein we cannot model integers values larger than Integer.MAX_VALUE
 */
@SuppressWarnings("serial")
public final class SicOversizedIntegerException extends SicException
{
    private final BigInteger m_value;

    public SicOversizedIntegerException(final BigInteger value)
    {
        super(PART_OVERSIZED_INTEGER);
        m_value = PreCondition.assertArgumentNotNull(value, "value");
    }

    public String getMessage()
    {
        final String localMessage = "The integer '" + m_value + "' is too large for this implementation.";

        final StringBuilder message = new StringBuilder();
        message.append(getOutcome().getSection());
        message.append(".");
        message.append(this.getPartNumber());
        message.append(": ");
        message.append(localMessage);
        return message.toString();
    }
}
