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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class AbstractUnaryList<T> implements List<T>
{
    public final void add(final int arg0, final T arg1)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean add(final T arg0)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean addAll(final Collection<? extends T> arg0)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean addAll(final int arg0, final Collection<? extends T> arg1)
    {
        throw new UnsupportedOperationException();
    }

    public final void clear()
    {
        throw new UnsupportedOperationException();
    }

    public final boolean contains(final Object object)
    {
        if (object == null)
        {
            return false;
        }
        else
        {
            return object.equals(this);
        }
    }

    @SuppressWarnings("unchecked")
    public final boolean containsAll(final Collection<?> c)
    {
        if ( (c != null) && (c.size() == 1) )
        {
            for (Object o : (Collection<Object>)c)
                return contains(o);
        }
        return false;
    }

    public final int indexOf(final Object object)
    {
        return contains(object) ? 0 : -1;
    }

    public final boolean isEmpty()
    {
        return false;
    }

    public final Iterator<T> iterator()
    {
        return new UnaryIterator<T>(this.get(0));
    }

    public final int lastIndexOf(final Object object)
    {
        return indexOf(object);
    }

    public final ListIterator<T> listIterator()
    {
        return new UnaryListIterator<T>(this.get(0));
    }

    public final ListIterator<T> listIterator(final int index)
    {
        if (index != 0)
            throw new ArrayIndexOutOfBoundsException(index);
        return listIterator();
    }

    public final T remove(final int arg0)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean remove(final Object arg0)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean removeAll(final Collection<?> arg0)
    {
        throw new UnsupportedOperationException();
    }

    public final boolean retainAll(final Collection<?> arg0)
    {
        throw new UnsupportedOperationException();
    }

    public final T set(final int arg0, final T arg1)
    {
        throw new UnsupportedOperationException();
    }

    public final int size()
    {
        return 1;
    }

    public final List<T> subList(final int fromIndex, final int toIndex)
    {
        if (fromIndex != 0)
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        if (toIndex != 1)
            throw new ArrayIndexOutOfBoundsException(toIndex);
        return this;
    }

    public final Object[] toArray()
    {
        return new Object[] { this };
    }
}
