/*
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
package org.genxdm.processor.w3c.xs.impl;

import java.util.HashMap;

/**
 * 
 * @param <T> the "token" parameter
 */
final class ContentModelMachine<T>
{
    private Integer m_state;
    private final ContentModelTable<T> m_table;
    private final T m_epsilon;

    public ContentModelMachine(final ContentModelTable<T> table, final T epsilon)
    {
        m_state = 0;
        m_table = table;
        m_epsilon = epsilon;
    }

    public boolean step(final T token)
    {
        if (m_state != ContentModelTable.END)
        {
            if (m_table.containsKey(m_state))
            {
                final HashMap<T, Integer> transitions = m_table.get(m_state);
                if (transitions.containsKey(token))
                {
                    m_state = transitions.get(token);
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                throw new AssertionError(m_state);
            }
        }
        else
        {
            return false;
        }
    }

    public boolean end()
    {
        if (m_state == ContentModelTable.END)
        {
            return true;
        }
        else
        {
            if (m_table.containsKey(m_state))
            {
                return m_table.get(m_state).containsKey(m_epsilon);
            }
            else
            {
                return false;
            }
        }
    }
}
