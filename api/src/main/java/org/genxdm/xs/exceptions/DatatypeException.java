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
/*
 * * Used by validation for CVC-datatype errors.
 */
public final class DatatypeException extends SchemaException
{
    public static String PART_ATOMIC = "1.2.1";
    public static String PART_FACET = "2";

    public static String PART_LIST = "1.2.2";
    public static String PART_PATTERN = "1.1";
    public static String PART_UNION = "1.2.3";
    private final String literal;
    private final QName type;

    public DatatypeException(final String literal, final SimpleType type)
    {
        super(ValidationOutcome.CVC_Datatype, "?");
        this.literal = PreCondition.assertArgumentNotNull(literal, "literal");
        this.type = ( type == null ) ? null : type.getName();
    }

    public DatatypeException(final String literal, final SimpleType type, final SchemaException cause)
    {
        super(ValidationOutcome.CVC_Datatype, "?", cause);
        this.literal = PreCondition.assertArgumentNotNull(literal, "literal");
        this.type = ( type == null ) ? null : type.getName();
    }

    public String getLiteral()
    {
        return literal;
    }

    public String getMessage()
    {
        final String localMessage = "The literal '" + literal + "' is not datatype-valid with respect to the datatype definition '" + type + "'.";

        final StringBuilder message = new StringBuilder();
        message.append(getOutcome().getSection());
        message.append(".");
        message.append(getPartNumber());
        message.append(": ");
        message.append(localMessage);
        return message.toString();
    }

    public QName getType()
    {
        return type;
    }
}
