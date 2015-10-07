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
package org.genxdm.bridge.dom;

import org.genxdm.exceptions.PreCondition;
import org.w3c.dom.Node;

/**
 * Identity object supporting {@link #hashCode} and <@link #equals} so that it may be used as a key in a {@link java.util.Map}.
 */
public final class DomNID
{
    private final Node m_node;

    public DomNID(final Node node)
    {
        m_node = PreCondition.assertArgumentNotNull(node, "node");
    }

    public int hashCode()
    {
        return System.identityHashCode(m_node);
        // holy *shit* this is so wrong it's not *even* wrong!
        //return m_node.getNodeType();
    }

    public boolean equals(final Object obj)
    {
        if (obj instanceof DomNID)
            return hashCode() == obj.hashCode();
        else if (obj instanceof Node)
            return hashCode() == System.identityHashCode(obj);
        return false;
        // an utterly improper implementation, allied with an utterly improper
        // hashCode() implementation for an utterly improper result.
        // we don't need to care what dom thinks the 'same node' is.
//        if (obj instanceof DomNID)
//        {
//            final DomNID other = (DomNID)obj;
//            if (m_node == other.m_node)
//                return true;
//            return m_node.isSameNode(other.m_node);
//        }
//        return false;
    }
}
