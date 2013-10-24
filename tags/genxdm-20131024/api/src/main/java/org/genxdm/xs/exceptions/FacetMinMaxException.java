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
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;

/**
 * xs:maxExclusive, xs:maxInclusive, xs:minExclusive, xs:minInclusive
 */
@SuppressWarnings("serial")
public final class FacetMinMaxException extends FacetException
{
    private final Limit limit;
    private final String actual;
    private final AtomBridge<?> bridge;

    public FacetMinMaxException(final Limit limit, final String actual, final AtomBridge<?> bridge)
    {
        super(getOutcome(limit.getKind()));
        this.actual = PreCondition.assertArgumentNotNull(actual, "actual");
        this.limit = PreCondition.assertArgumentNotNull(limit, "limit");
        this.bridge = PreCondition.assertArgumentNotNull(bridge, "bridge");
    }

    @Override
    public String getMessage()
    {
        final String localMessage = "The instance value, '" + actual + "', is not valid with respect to the facet value, '" + limit.getLimit(bridge) + "'.";

        final StringBuilder message = new StringBuilder();
        message.append(getOutcome().getSection());
        message.append(".");
        message.append(getPartNumber());
        message.append(": ");
        message.append(localMessage);
        return message.toString();
    }
    
    public Limit getLimit()
    {
        return limit;
    }
    
    public String getInput()
    {
        return actual;
    }

    private static ValidationOutcome getOutcome(final FacetKind opcode)
    {
        PreCondition.assertArgumentNotNull(opcode, "opcode");
        switch (opcode)
        {
            case MinInclusive:
            {
                return ValidationOutcome.CVC_MinInclusive;
            }
            case MinExclusive:
            {
                return ValidationOutcome.CVC_MinExclusive;
            }
            case MaxInclusive:
            {
                return ValidationOutcome.CVC_MaxInclusive;
            }
            case MaxExclusive:
            {
                return ValidationOutcome.CVC_MaxExclusive;
            }
            default:
            {
                throw new RuntimeException(opcode.name());
            }
        }
    }
}
