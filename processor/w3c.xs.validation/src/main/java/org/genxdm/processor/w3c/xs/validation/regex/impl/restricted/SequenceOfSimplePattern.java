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
package org.genxdm.processor.w3c.xs.validation.regex.impl.restricted;


import java.util.Iterator;
import java.util.List;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExBridge;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExMachine;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPattern;


final class SequenceOfSimplePattern<E, T> extends RestrictedBase<E, T> implements RegExPattern<E, T>
{
    public SequenceOfSimplePattern(final List<E> subtermList, final RegExBridge<E, T> bridge)
    {
        m_subterms = subtermList;
        m_bridge = bridge;
    }

    public RegExMachine<E, T> createRegExMachine(final List<E> followerTerms)
    {
        return new SequenceOfSimpleRegExMachine<E, T>(m_subterms, followerTerms, m_bridge);
    }

    public String toString()
    {
        final Iterator<E> it = m_subterms.iterator();
        if (it.hasNext())
        {
            final E first = it.next();
            if (it.hasNext())
            {
                final StringBuilder sb = new StringBuilder();
                sb.append(first.toString());
                while(it.hasNext())
                {
                    sb.append(",");
                    sb.append(it.next().toString());
                }
                return sb.toString();
            }
            else
            {
                return first.toString();
            }
        }
        else
        {
            return "";
        }
    }

    private List<E> m_subterms;
    private final RegExBridge<E, T> m_bridge;
}
