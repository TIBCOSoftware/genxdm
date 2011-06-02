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
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.facets.LengthFacetUOM;
import org.genxdm.xs.facets.MaxLength;


/**
 * xs:maxLength
 */
@SuppressWarnings("serial")
public final class FacetMaxLengthException extends FacetException
{
    private final MaxLength<?> maxLength;
    private final int actualLength;
    private final LengthFacetUOM uom;

    public FacetMaxLengthException(final MaxLength<?> maxLength, final int actualLength, final LengthFacetUOM uom)
    {
        super(ValidationOutcome.CVC_MaxLength);
        this.maxLength = maxLength;
        this.actualLength = actualLength;
        this.uom = PreCondition.assertArgumentNotNull(uom, "uom");
    }

    @Override
    public String getMessage()
    {
        final String localMessage;
        switch (uom)
        {
            case Characters:
            {
                localMessage = "The length of the value (" + actualLength + "), as measured in characters must be less than or equal to " + maxLength.getMaxLength() + ".";
            }
            break;
            case Octets:
            {
                localMessage = "The length of the value (" + actualLength + "), as measured in octets of the binary data, must be less than or equal to " + maxLength.getMaxLength() + ".";
            }
            break;
            case ListItems:
            {
                localMessage = "The length of the value (" + actualLength + "), as measured in list items, must be less than or equal to " + maxLength.getMaxLength() + ".";
            }
            break;
            default:
            {
                throw new AssertionError(uom.name());
            }
        }

        final StringBuilder message = new StringBuilder();
        message.append(getOutcome().getSection());
        message.append(".");
        message.append(getPartNumber());
        message.append(": ");
        message.append(localMessage);
        return message.toString();
    }
}
