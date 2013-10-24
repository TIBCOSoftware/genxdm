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

import java.util.ArrayList;
import java.util.List;

import org.genxdm.bridgekit.tree.TraversingInformerDelegate;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.xpath.v10.ExprException;

public class CloneableTraverserImpl extends TraversingInformerDelegate implements CloneableTraverser
{
    private final NodeList list;
    private int i;

    @Override
    public Object clone()
    {
        return new CloneableTraverserImpl(list, i);
    }

    public CloneableTraverserImpl(Traverser iter)
    {
        super(null);
        list = new NodeList(iter);
        i = -1;
    }

    private CloneableTraverserImpl(NodeList list, int i)
    {
        super(list.nodeAt(i));
        this.list = list;
        this.i = i;
    }

    public boolean moveToNext()
    {
        i++;
        TraversingInformer tem = list.nodeAt(i);
        setInformer(tem);
        return tem != null;
    }

    public void bind() throws ExprException
    {
        for (int i = 0; list.nodeAt(i) != null; i++)
            ;
    }

    static class NodeList
    {
        private Traverser iter;
        List<TraversingInformer> nodes = null;
        int len = 0;

        NodeList(final Traverser iter)
        {
            this.iter = iter;
        }

        TraversingInformer nodeAt(int i)
        {
            if (i >= len && iter != null)
            {
                if (nodes == null)
                {
                    nodes = new ArrayList<TraversingInformer>(i + 4);
                }
                // Have i < nodes.length
                for (; iter.moveToNext() && len <= i; len++)
                {
                    nodes.add(iter.newCursor());
                }
                
                // clear the iterator so that we don't try to keep scanning.
                if (iter.isFinished()) {
                    iter = null;
                }
                
                // now lets make sure the requester hasn't walked off the end.
                if (i >= len) {
                    return null;
                }
                // Have i < length
            }
            return nodes.get(i);
        }
    }

    @Override
    public boolean isFinished() {
        return list.nodeAt(i) == null;
    }
}
