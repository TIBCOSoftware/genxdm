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
import org.genxdm.xs.facets.FractionDigits;

/**
 * xs:totalDigits
 */
@SuppressWarnings("serial")
public final class FacetFractionDigitsException extends FacetException
{
    private final String value;
    private final FractionDigits fractionDigits;

    public FacetFractionDigitsException(final String value, final FractionDigits fractionDigits)
    {
        this.value = PreCondition.assertArgumentNotNull(value, "value");
        this.fractionDigits = fractionDigits;
    }

    public String getMessage()
    {
        return "The fraction number of digits(" + fractionDigits.getFractionDigits() + ") in '" + value + "' exceeds the number allowed for the type.";
    }
    
    public FractionDigits getFractionFigits()
    {
        return fractionDigits;
    }
    
    public String getInput()
    {
        return value;
    }
}
