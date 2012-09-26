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

import java.util.Iterator;
import java.util.List;

import org.genxdm.Cursor;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformerDelegate;

public class ListTraverser extends TraversingInformerDelegate implements Traverser {

    public ListTraverser(List<Cursor> list)
    {
        super(null);
        PreCondition.assertNotNull(list, "list");
        this.it = list.iterator();
    }

    @Override
    public boolean moveToNext() {
        if (it.hasNext()) {
            Cursor nextLoc = it.next();
            setInformer(nextLoc);
            return true;
        }
        else {
            setInformer(null);
        }
        
        return false;
    }

    @Override
    public boolean isFinished() {
        return !it.hasNext() && getInformer() != null;
    }

    private final Iterator<Cursor> it;
}
