/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.typed.variant;

import org.genxdm.exceptions.PreCondition;

public class Item<N, A>
{
    private Item() {}

    public N node()
    {
        return null;
    }

    public A atom()
    {
        return null;
    }

    public boolean isNode()
    {
        return false;
    }

    public boolean isAtom()
    {
        return false;
    }

    public static final class NodeItem<N, A>
        extends Item<N, A>
    {
        public NodeItem(N node)
        {
            this.node = PreCondition.assertNotNull(node, "node");
        }

        public N node()
        {
            return this.node;
        }

        public boolean isNode()
        {
            return true;
        }
        private N node;
    }

    public static final class AtomItem<N, A>
        extends Item<N, A>
    {
        public AtomItem(A atom)
        {
            this.atom = PreCondition.assertNotNull(atom, "atom");
        }

        public A atom()
        {
            return this.atom;
        }

        public boolean isAtom()
        {
            return true;
        }
        private A atom;
    }
}
