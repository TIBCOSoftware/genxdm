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

import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class CvcElementChildElementWithFixedException extends CvcElementException
{
    @SuppressWarnings("unused")
    private final QName childName;

    public CvcElementChildElementWithFixedException(final ElementDefinition elementDeclaration, final QName childName, final LocationInSchema location)
    {
        super(PART_VALUE_CONSTRAINT_WITH_CHILD_ELEMENT, elementDeclaration, location);
        this.childName = childName;
    }

    @Override
    public String getMessage()
    {
        return "The element information item, '" + getElementDeclaration() + "', must have no element information item [children] because there is a fixed {value constraint}.";
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof CvcElementChildElementWithFixedException)
        {
            final CvcElementChildElementWithFixedException e = (CvcElementChildElementWithFixedException)obj;
            return e.getElementDeclaration().equals(getElementDeclaration());
        }
        else
        {
            return false;
        }
    }
}
