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

public class UnionNodeTraverser extends TraversingInformerDelegate implements Traverser
{
    private final Traverser iter1;
    private final Traverser iter2;
    private Traverser current = null;
    
    public UnionNodeTraverser(final Traverser iter1, final Traverser iter2)
    {
        super(null);
        this.iter1 = iter1;
        current = this.iter1;
        this.iter2 = iter2;
        this.iter2.moveToNext();
    }

    @Override
    public boolean moveToNext()
    {
        // current is null to start, because we've not returned anything yet.
        // whatever the current is, we want to advance it, but only if it wasn't already
        // at the end.
        if (current != null) {
            current.moveToNext();
        }
        
        if (iter1.isFinished() && iter2.isFinished()) {
            setInformer(null);
            current = null;
            return false;
        }
        if ( iter1.isFinished() ) {
            setAsInformer(iter2);
            return true;
        }
        if ( iter2.isFinished() ) {
            setAsInformer(iter1);
            return true;
        }
        
        int cmp = iter1.compareTo(iter2);
        if (cmp == 0) {
            setAsInformer(iter1);
            iter2.moveToNext();
        }
        else if (cmp < 0) {
            setAsInformer(iter1);
        }
        else {
            setAsInformer(iter2);
        }
        
        return true;
    }

    @Override
    public boolean isFinished() {
        return iter1.isFinished() && !iter2.isFinished();
    }
    private void setAsInformer(Traverser newCurrent) {
        current = newCurrent;
        setInformer(current);
    }

}
