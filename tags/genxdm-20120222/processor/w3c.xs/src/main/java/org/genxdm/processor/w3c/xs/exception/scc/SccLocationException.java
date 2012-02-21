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
package org.genxdm.processor.w3c.xs.exception.scc;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.exceptions.ComponentConstraintException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.resolve.LocationInSchema;

/**
 * A Schema Component Constraint Exception wrapped in physical location information.
 */
@SuppressWarnings("serial")
public final class SccLocationException extends ComponentConstraintException
{
    private final LocationInSchema m_location;

    public SccLocationException(final LocationInSchema location, final SchemaException cause)
    {
        super(PreCondition.assertArgumentNotNull(cause, "cause").getOutcome(), cause.getPartNumber(), cause);
        m_location = PreCondition.assertArgumentNotNull(location, "location");
    }

    public final LocationInSchema getLocation()
    {
        return m_location;
    }

    @Override
    public SchemaException getCause()
    {
        return (SchemaException)super.getCause();
    }
}
