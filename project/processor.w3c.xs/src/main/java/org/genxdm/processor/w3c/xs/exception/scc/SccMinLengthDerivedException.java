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

import javax.xml.namespace.QName;

@SuppressWarnings("serial")
public final class SccMinLengthDerivedException extends SccLengthAndMinLengthOrMaxLengthException
{
    private final int m_minLength;
    private final int m_length;
    private final QName m_simpleType;

    public SccMinLengthDerivedException(final int minLength, final int length, final QName simpleType)
    {
        super(PART_MIN_LENGTH_DERIVED);
        m_minLength = minLength;
        m_length = length;
        m_simpleType = simpleType;
    }

    @Override
    public String getMessage()
    {
        return "minLength(" + m_minLength + ") cannot be a member of facets of " + m_simpleType + " with length(" + m_length + ") unless there is a type definition from which this one is derived by restriction with the same minLength.";
    }
}
