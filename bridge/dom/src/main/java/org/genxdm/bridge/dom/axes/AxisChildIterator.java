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
package org.genxdm.bridge.dom.axes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.genxdm.bridge.dom.DomSupport;
import org.w3c.dom.Node;

final class AxisChildIterator implements Iterator<Node>
{
    private Node m_next;

    public AxisChildIterator(final Node origin)
    {
        m_next = DomSupport.getFirstChild(origin);
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
            while (null != m_next.getNextSibling())
            {
                switch (m_next.getNodeType())
                {
                    case Node.ELEMENT_NODE:
                    case Node.CDATA_SECTION_NODE:
                    case Node.TEXT_NODE:
                    case Node.COMMENT_NODE:
                    case Node.PROCESSING_INSTRUCTION_NODE:
                    {
                        m_next = m_next.getNextSibling();
                        return last;
                    }
                    case Node.DOCUMENT_TYPE_NODE:
                    {
                        m_next = m_next.getNextSibling();
                    }
                    break;
                    default:
                    {
                        throw new RuntimeException(Integer.toString(m_next.getNodeType()));
                    }
                }
            }
            m_next = null;
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
