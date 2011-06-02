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
package org.genxdm.bridge.dom.axes;

import java.util.LinkedList;

import org.genxdm.bridge.dom.DomSupport;
import org.genxdm.bridgekit.axes.BaseImmutableIterator;
import org.w3c.dom.Node;

final class AxisDescendantIterator extends BaseImmutableIterator<Node>
{
    private LinkedList<Node> m_stack;

    public AxisDescendantIterator(final Node origin)
    {
        super(origin);

        m_stack = new LinkedList<Node>();

        Node child = DomSupport.getTailChild(origin);

        while (null != child)
        {
            m_stack.addFirst(child);

            child = child.getPreviousSibling();
        }

        setFirstResult(next(null));
    }

    public Node next(final Node unused)
    {
        if (m_stack.size() > 0)
        {
            final Node result = m_stack.removeFirst();

            Node child = DomSupport.getTailChild(result);

            while (null != child)
            {
                m_stack.addFirst(child);

                child = child.getPreviousSibling();
            }

            return result;
        }
        else
        {
            return null;
        }
    }
}
