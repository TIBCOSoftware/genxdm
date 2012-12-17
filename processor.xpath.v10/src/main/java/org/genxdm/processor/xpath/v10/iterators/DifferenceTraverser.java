/*
 * Copyright (c) 2012 TIBCO Software Inc.
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
package org.genxdm.processor.xpath.v10.iterators;

import org.genxdm.bridgekit.tree.TraversingInformerDelegate;
import org.genxdm.nodes.Traverser;

public class DifferenceTraverser extends TraversingInformerDelegate implements Traverser {

    private final Traverser iter1;
    private final Traverser iter2;

    public DifferenceTraverser(final Traverser iter1, final Traverser iter2)
    {
        super(iter1);
        this.iter1 = iter1;
        this.iter2 = iter2;
        // prime iterator #2 to see what matches.
        this.iter2.moveToNext();
    }

    @Override
    public boolean moveToNext()
    {
        // whatever we were looking at, skip to the next item...
        iter1.moveToNext();
        
        while (!iter1.isFinished())
        {
            if (iter2.isFinished()) {
                return true;
            }
            final int cmp = iter1.compareTo(iter2);
            if (cmp < 0)
            {
                return true;
            }
            if (cmp == 0)
            {
                // skip both items...
                iter1.moveToNext();
                iter2.moveToNext();
            }
            else
            {
                iter2.moveToNext();
            }
        }
        return false;
    }

    @Override
    public boolean isFinished() {
        return iter1.isFinished();
    }
}
