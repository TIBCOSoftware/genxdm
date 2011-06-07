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
package org.genxdm.processor.w3c.xs.exception.cvc;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.sm.SmComplexTypeException;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.ComplexType;

@SuppressWarnings("serial")
public final class CvcAbstractComplexTypeException extends SmComplexTypeException
{
    private final ComplexType complexType;

    public ComplexType getComplexType()
    {
        return complexType;
    }

    public CvcAbstractComplexTypeException(final QName elementName, final ComplexType complexType, final LocationInSchema location)
    {
        super(PART_ABSTRACT_FALSE, elementName, location);
        this.complexType = PreCondition.assertArgumentNotNull(complexType, "complexType");
    }

    public String getMessage()
    {
        final String localMessage = "Attempting to use an abstract complex type, '" + getComplexType() + "', with element '" + getElementName() + "'.";

        final StringBuilder message = new StringBuilder();
        message.append(getOutcome().getSection());
        message.append(".");
        message.append(getPartNumber());
        message.append(": ");
        message.append(localMessage);
        return message.toString();
    }
}
