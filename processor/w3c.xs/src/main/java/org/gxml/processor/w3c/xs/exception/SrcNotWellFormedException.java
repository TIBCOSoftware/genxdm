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
package org.gxml.processor.w3c.xs.exception;

import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.resolve.SmLocation;

/**
 * Used for reporting errors due to ill formed documents.
 */
@SuppressWarnings("serial")
public class SrcNotWellFormedException extends SmLocationException
{
    // TODO: these part numbers are not meaningful; also, the class itself should be abstract.
    public static final String PART_SCHEMA_NOT_WELL_FORMED = "TODO_SCHEMA";
    public static final String PART_INSTANCE_NOT_WELL_FORMED = "TODO_INSTANCE";

    public SrcNotWellFormedException(final String partNumber, final SmLocation location)
    {
        super(SmOutcome.SRC_NotWellFormed, partNumber, location);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SrcNotWellFormedException(final String partNumber, final SmLocation location, Exception cause)
    {
        super(SmOutcome.SRC_NotWellFormed, partNumber, location);    //To change body of overridden methods use File | Settings | File Templates.
        m_cause = cause;
    }

    @Override
    public String getMessage()
    {
        StringBuffer sb = new StringBuffer();
        if (PART_SCHEMA_NOT_WELL_FORMED.equals(getPartNumber()))
        {
            sb.append("Schema ");
        }
        else
        {
            sb.append("Instance ");
        }
        sb.append("is not well formed");
        if (getLocation() != null)
        {
            sb.append(": ").append(getLocation());
        }
        if (m_cause != null)
        {
            sb.append(": ").append(m_cause.getMessage());
        }
        sb.append(".");
        return sb.toString();
    }

    Exception m_cause;
}
