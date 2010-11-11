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
package org.genxdm.bridge.dom.axes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;

/**
 * Provides the basic pattern for iterating over an axis.  Derived classes provide the axis-specific strategy.
 * Designed so that the strategy may be re-used by a filtering engine without sacrificing performance in the
 * un-filtered case.
 *
 * @author David Holmes
 */
abstract class AxisStrategyIterator<ORIGIN_TYPE extends Node> implements Iterator<Node>, AxisStrategy<ORIGIN_TYPE>
{
    /**
     * The next element in the iteration.
     */
    private Node m_next;

    public AxisStrategyIterator(final ORIGIN_TYPE origin)
    {
        if (null != origin)
        {
            m_next = prime(origin);
        }
    }

    public boolean hasNext()
    {
        return (null != m_next);
    }

    public Node next() throws NoSuchElementException
    {
        if (m_next != null)
        {
            final Node last = m_next;

            m_next = next(m_next);

            return last;
        }
        else
        {
            // The iteration has no more elements.
            throw new NoSuchElementException();
        }
    }

    public void remove() throws IllegalStateException
    {
        throw new UnsupportedOperationException();
    }
}
