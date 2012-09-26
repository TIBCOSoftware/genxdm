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

import org.genxdm.Cursor;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformerDelegate;

public class MergeNodeTraverser extends TraversingInformerDelegate implements Traverser
{
    private Traverser[] iters;
    private List<Cursor> nodes;
    private int length;

    /**
     * construct with an array of iterators
     * 
     * @param length
     *            the number of slots in the array which really have NodeIterators for us
     */
    public MergeNodeTraverser(Traverser[] iters, int length)
    {
        super(null);
        this.length = length;
        this.iters = iters;
        nodes = new ArrayList<Cursor>(length);
        int j = 0;
        for (int i = 0; i < length; i++)
        {
            // we squeeze out NodeIterators with no nodes
            // and put the first node from each iterator
            // in our "nodes" array
            if (i != j)
            {
                iters[j] = iters[i];
            }
            if (iters[j].moveToNext())
            {
                nodes.add(iters[j].newPrecursor());
                j++;
            }
        }
        this.length = j; // reset the length to reflect squeezing
        buildHeap();
    }

    /**
     * Make the heap rooted at i a heap, assuming its children are heaps.
     */
    private final void heapify(int i)
    {
        // i starts out around (length / 2) - 1
        for (;;)
        {
            int left = (i << 1) | 1; // (i*2) + 1 ??
            int right = left + 1; // (i*2) + 2 ??

            if (right < length)
            {

                if (compare(left, right) <= 0)
                {
                    // left <= right

                    if (compare(left, i) > 0)
                    {
                        break;
                    }
                    exchange(left, i);
                    i = left;
                }
                else
                {
                    // right >= left
                    if (compare(right, i) > 0)
                    {
                        break;
                    }
                    exchange(right, i);
                    i = right;
                }
            }
            else if (left < length)
            {
                if (compare(left, i) > 0)
                {
                    break;
                }
                exchange(left, i);
                i = left;
            }
            else
            {
                break;
            }
        }
    }

    /**
     * swaps the items with the given indices
     */
    private final void exchange(int i, int j)
    {
        {
            Cursor tem = nodes.get(i);
            nodes.set(i, nodes.get(j));
            nodes.set(j, tem);
        }
        {
            Traverser tem = iters[i];
            iters[i] = iters[j];
            iters[j] = tem;
        }
    }

    private final int compare(final int i, final int j)
    {
        return nodes.get(i).compareTo(nodes.get(j));
    }

    private void buildHeap()
    {
        for (int i = length / 2 - 1; i >= 0; --i)
        {
            heapify(i);
        }
    }

    /**
     * finds and returns the next node (in document(s) order?)
     */
    @Override
    public boolean moveToNext()
    {
        if (length == 0)
        {
            setInformer(null);
            return false;
        }
        Cursor max = nodes.get(0);
        do
        {
            if (!iters[0].moveToNext())
            {
                if (--length == 0)
                    break;
                nodes.set(0, nodes.get(length));
                iters[0] = iters[length];
            }
            else
            {
                nodes.set(0, iters[0].newPrecursor());
            }
            heapify(0);
        }
        while (max.equals(nodes.get(0)));
        setInformer(max);
        return true;
    }

    @Override
    public boolean isFinished() {
        return length == 0;
    }
}
