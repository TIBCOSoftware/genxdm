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
package org.genxdm.processor.w3c.xs.xmlrep;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.src.SrcFrozenLocation;

/**
 * XML representation of a value. <br/>
 * Remembers the string value, the resolver and the location.
 */
final class XMLValue
{
    private final String m_value;
    private final SrcFrozenLocation m_location;

    public XMLValue(final String value, final SrcFrozenLocation location)
    {
        m_value = PreCondition.assertArgumentNotNull(value, "value");
        m_location = PreCondition.assertArgumentNotNull(location, "location");
    }

    /**
     * Returns the value of {value constraint} property.
     */
    public String getValue()
    {
        return m_value;
    }

    /**
     * Returns the location of the attribute defining the value.
     */
    public SrcFrozenLocation getLocation()
    {
        return m_location;
    }
}
