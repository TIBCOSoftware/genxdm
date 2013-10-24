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
package org.genxdm.bridgekit.misc;

import java.util.ListIterator;

public final class UnaryListIterator<E> 
    implements ListIterator<E>
{
    public UnaryListIterator(final E thing)
    {
        next = thing;
    }

    @Override
    public boolean hasNext()
    {
        return (next != null);
    }

    @Override
    public E next()
    {
        if (next != null)
        {
            previous = next;
            next = null;
            return previous;
        }
        return null;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPrevious()
    {
        return (previous != null);
    }

    @Override
    public E previous()
    {
        if (previous != null)
        {
            next = previous;
            previous = null;
            return next;
        }
        return null;
    }

    @Override
    public int nextIndex()
    {
        if (next != null)
            return 0;
        return 1;
    }

    @Override
    public int previousIndex()
    {
        if (previous != null)
            return 0;
        return -1;
    }

    @Override
    public void set(E e)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(E e)
    {
        throw new UnsupportedOperationException();
    }

    private E next;
    private E previous;
}
