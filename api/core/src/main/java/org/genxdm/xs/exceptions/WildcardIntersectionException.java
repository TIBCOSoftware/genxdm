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

import org.genxdm.xs.enums.ValidationOutcome;

@SuppressWarnings("serial")
abstract public class WildcardIntersectionException extends ComponentConstraintException
{
    public static final String PART_INTERSECTION_NOT_EXPRESSIBLE = "5";

    public WildcardIntersectionException(final String partNumber)
    {
        super(ValidationOutcome.SCC_Attribute_Wildcard_Intersection, partNumber);
    }

    public WildcardIntersectionException(final String partNumber, final SchemaException cause)
    {
        super(ValidationOutcome.SCC_Attribute_Wildcard_Intersection, partNumber, cause);
    }

}
