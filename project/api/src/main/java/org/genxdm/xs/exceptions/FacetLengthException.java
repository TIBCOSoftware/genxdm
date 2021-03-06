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
import org.genxdm.xs.facets.Length;


/**
 * xs:length
 */
@SuppressWarnings("serial")
public final class FacetLengthException extends FacetException
{
    private final Length expectLength;
    private final int actualLength;

    public FacetLengthException(final Length expectLength, final int actualLength)
    {
        this.expectLength = PreCondition.assertArgumentNotNull(expectLength, "expectLength");
        this.actualLength = actualLength;
    }

    @Override
    public String getMessage()
    {
        return "The expected length(" + expectLength.getValue() + ") does not match the actual length (" + actualLength + ").";
    }
    
    public int getExpectedLength()
    {
        return expectLength.getValue();
    }
    
    public int getActualLength()
    {
        return actualLength;
    }
}
