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
package org.genxdm.bridgekit.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class UnaryIterable<E> implements List<E>
{
	private final E m_thing;

	public UnaryIterable(final E thing)
	{
		m_thing = thing;
	}

	public Iterator<E> iterator()
	{
		return new UnaryIterator<E>(m_thing);
	}

	public int size()
	{
		return (null != m_thing) ? 1 : 0;
	}

	public boolean isEmpty()
	{
		return (null == m_thing);
	}

	public E get(final int index)
	{
		if (null != m_thing)
		{
			if (0 == index)
			{
				return m_thing;
			}
			else
			{
				throw new IndexOutOfBoundsException();
			}
		}
		else
		{
			throw new IndexOutOfBoundsException();
		}
	}

	public boolean contains(final Object object)
	{
		if (object == null)
		{
			return null == m_thing;
		}
		else
		{
			if (null != m_thing)
			{
				return m_thing.equals(object);
			}
			else
			{
				return false;
			}
		}
	}

	public Object[] toArray()
	{
		if (null != m_thing)
		{
			return new Object[] { m_thing };
		}
		else
		{
			return new Object[] {};
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a)
	{
		final int size = size();
		if (a.length < size)
		{
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		}
		System.arraycopy(toArray(), 0, a, 0, size);
		if (a.length > size)
		{
			a[size] = null;
		}
		return a;
	}

	public boolean add(E e)
	{
		throw new UnsupportedOperationException("add");
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("remove");
	}

	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("containsAll");
	}

	public boolean addAll(Collection<? extends E> c)
	{
		throw new UnsupportedOperationException("addAll");
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		throw new UnsupportedOperationException("addAll");
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("removeAll");
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("retainAll");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("clear");
	}

	public E set(int index, E element)
	{
		throw new UnsupportedOperationException("set");
	}

	public void add(int index, E element)
	{
		throw new UnsupportedOperationException("add");
	}

	public E remove(int index)
	{
		throw new UnsupportedOperationException("remove");
	}

	public int indexOf(final Object object)
	{
		return contains(object) ? 0 : -1;
	}

	public int lastIndexOf(final Object object)
	{
		return indexOf(object);
	}

	public ListIterator<E> listIterator()
	{
		throw new UnsupportedOperationException("listIterator");
	}

	public ListIterator<E> listIterator(int index)
	{
		throw new UnsupportedOperationException("listIterator");
	}

	public List<E> subList(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException("subList");
	}

	@Override
	public String toString()
	{
		if (null != m_thing)
		{
			return m_thing.toString();
		}
		else
		{
			return "()";
		}
	}
}
