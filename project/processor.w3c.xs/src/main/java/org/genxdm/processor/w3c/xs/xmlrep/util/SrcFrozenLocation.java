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
package org.genxdm.processor.w3c.xs.xmlrep.util;

import java.util.HashMap;

import javax.xml.stream.Location;

import org.genxdm.xs.resolve.LocationInSchema;

public final class SrcFrozenLocation 
    implements LocationInSchema
{
    public SrcFrozenLocation(final int lineNumber, final int columnNumber, final int characterOffset, final String publicId, final String systemId)
    {
        m_lineNumber = lineNumber;
        m_columnNumber = columnNumber;
        m_characterOffset = characterOffset;
        m_publicId = publicId;
        m_systemId = systemId;
    }

    public SrcFrozenLocation(final Location location)
    {
        if (null != location)
        {
            m_lineNumber = location.getLineNumber();
            m_columnNumber = location.getColumnNumber();
            m_characterOffset = location.getCharacterOffset();
            m_publicId = location.getPublicId();
            m_systemId = location.getSystemId();
        }
        else
        {
            m_lineNumber = -1;
            m_columnNumber = -1;
            m_characterOffset = -1;
            m_publicId = null;
            m_systemId = null;
        }
    }

    public int getLineNumber()
    {
        return m_lineNumber;
    }

    public int getColumnNumber()
    {
        return m_columnNumber;
    }

    public int getCharacterOffset()
    {
        return m_characterOffset;
    }

    public String getPublicId()
    {
        return m_publicId;
    }

    public String getSystemId()
    {
        return m_systemId;
    }

    @Override
    public String toString()
    {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("lineNumber", m_lineNumber);
        map.put("columnNumber", m_columnNumber);
        map.put("characterOffset", m_characterOffset);
        map.put("publicId", m_publicId);
        map.put("systemId", m_systemId);
        return map.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_characterOffset;
        result = prime * result + m_columnNumber;
        result = prime * result + m_lineNumber;
        result = prime * result
                + ((m_publicId == null) ? 0 : m_publicId.hashCode());
        result = prime * result
                + ((m_systemId == null) ? 0 : m_systemId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SrcFrozenLocation other = (SrcFrozenLocation) obj;
        if (m_characterOffset != other.m_characterOffset)
            return false;
        if (m_columnNumber != other.m_columnNumber)
            return false;
        if (m_lineNumber != other.m_lineNumber)
            return false;
        if ( ((m_publicId == null) && (other.m_publicId != null)) ||
             ((m_publicId != null) && !m_publicId.equals(other.m_publicId)) )
            return false;
        if ( ((m_systemId == null) && (other.m_systemId != null)) ||
             ((m_systemId != null) && !m_systemId.equals(other.m_systemId)) )
            return false;
        return true;
    }

    private final int m_lineNumber;
    private final int m_columnNumber;
    private final int m_characterOffset;
    private final String m_publicId;
    private final String m_systemId;
}
