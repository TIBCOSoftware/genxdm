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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.types.SimpleType;

@SuppressWarnings("serial")
public final class SimpleTypeException extends SchemaException
{
    public SimpleTypeException(final String initialValue, final SimpleType type, final SchemaException cause)
    {
        this(initialValue, type, cause, null);
    }
    
    public SimpleTypeException(final String initialValue, final SimpleType type, final SchemaException cause, final QName elementName)
    {
        super(ValidationOutcome.CVC_Simple_Type, "?", cause);
        this.initialValue = PreCondition.assertArgumentNotNull(initialValue, "initialValue");
        this.type = (type == null) ? null : type.getName();
        this.elementName = elementName;
        isAnonymous = (type == null) ? false : type.isAnonymous();
    }

    @Override
    public String getMessage()
    {
        final String name;
        if (type != null)
            name = isAnonymous ? "{anonymous}" : type.toString();
        else
            name = "{unknown}";
        final String message = getOutcome().getSection() + "." + getPartNumber()
               + ": The initial value '" + initialValue + "' "
               + ( (elementName == null) ? "" : "for element '" + elementName + "' ") 
               + "is not valid with respect to the simple type definition '" + name + "'.";

        return message;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof SimpleTypeException)
        {
            final SimpleTypeException other = (SimpleTypeException)obj;
            return initialValue.equals(other.initialValue) && type.equals(other.type);
        }
        else
        {
            return false;
        }
    }
    
    public QName getType()
    {
        return type;
    }
    
    public String getInput()
    {
        return initialValue;
    }

    private final String initialValue;
    private final QName type;
    private final QName elementName;
    private final boolean isAnonymous;
}
