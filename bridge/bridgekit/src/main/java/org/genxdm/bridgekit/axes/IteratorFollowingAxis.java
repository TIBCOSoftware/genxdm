/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.bridgekit.axes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.genxdm.Model;
import org.genxdm.exceptions.PreCondition;


final class IteratorFollowingAxis<N> implements Iterator<N>
{
    private N m_next;
    private final Model<N> m_navigator;

    public IteratorFollowingAxis(final N origin, final Model<N> navigator)
    {
        m_navigator = PreCondition.assertArgumentNotNull(navigator);
        m_next = prime(origin, navigator);
    }

    public boolean hasNext()
    {
        return (null != m_next);
    }

    public N next() throws NoSuchElementException
    {
        if (m_next != null)
        {
            final N last = m_next;
            final N headChild = m_navigator.getFirstChild(m_next);
            if (null != headChild)
            {
                m_next = headChild;
            }
            else
            {
                m_next = jump(m_next, m_navigator);
            }
            return last;
        }
        else
        {
            // The iteration has no more elements.
            throw new NoSuchElementException();
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    private static <N> N prime(final N origin, final Model<N> navigator)
    {
        if (navigator.isAttribute(origin) || navigator.isNamespace(origin))
        {
            final N parent = navigator.getParent(origin);
            if (null != parent)
            {
                return navigator.getFirstChild(parent);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return jump(origin, navigator);
        }
    }

    private static <N> N jump(final N origin, final Model<N> navigator)
    {
        final N nextSibling = navigator.getNextSibling(origin);
        if (null != nextSibling)
        {
            return nextSibling;
        }
        else
        {
            final N parent = navigator.getParent(origin);
            if (null != parent)
            {
                return jump(parent, navigator);
            }
            else
            {
                // terminates.
                return null;
            }
        }
    }
}
