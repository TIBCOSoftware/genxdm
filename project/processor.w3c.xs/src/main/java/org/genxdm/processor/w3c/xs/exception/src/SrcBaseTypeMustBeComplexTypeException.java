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
package org.genxdm.processor.w3c.xs.exception.src;

import org.genxdm.processor.w3c.xs.exception.sm.SmSourceComplexTypeException;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class SrcBaseTypeMustBeComplexTypeException extends SmSourceComplexTypeException
{
    public SrcBaseTypeMustBeComplexTypeException(final LocationInSchema location)
    {
        super(PART_BASE_TYPE_MUST_BE_COMPLEX_TYPE, location);
    }

    @Override
    public String getMessage()
    {
        return "When the <complexContent> alternative is chosen, the type definition resolved to by the actual value of the base [attribute] must be a complex type definition.";
    }
}
