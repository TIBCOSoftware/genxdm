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

import org.genxdm.bridgekit.axes.BaseImmutableIterator;
import org.w3c.dom.Node;

final class AxisChildElementIterator extends BaseImmutableIterator<Node>
{
    public AxisChildElementIterator(final Node origin)
    {
    	// Note that the following doesn't call DomSupport.getFirstChild(), because it is already
    	// filtering for just element nodes with the call to getNextElement().
        super(getNextElement( origin.getFirstChild() ));
    }

    public Node next(final Node current)
    {
        return getNextElement(current.getNextSibling());
    }

    private static Node getNextElement(Node candidate)
    {
        while (null != candidate)
        {
            if (Node.ELEMENT_NODE == candidate.getNodeType())
            {
                return candidate;
            }

            candidate = candidate.getNextSibling();
        }

        return null;
    }
}
