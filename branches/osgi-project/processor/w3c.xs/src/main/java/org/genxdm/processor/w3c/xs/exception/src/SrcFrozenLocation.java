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

import java.util.HashMap;

import org.genxdm.xs.resolve.LocationInSchema;

public final class SrcFrozenLocation implements LocationInSchema
{
    private final int m_lineNumber;
    private final int m_columnNumber;
    private final int m_characterOffset;
    private final String m_publicId;
    private final String m_systemId;

    public SrcFrozenLocation(final int lineNumber, final int columnNumber, final int characterOffset, final String publicId, final String systemId)
    {
        m_lineNumber = lineNumber;
        m_columnNumber = columnNumber;
        m_characterOffset = characterOffset;
        m_publicId = publicId;
        m_systemId = systemId;
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
}
