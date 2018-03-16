package org.genxdm.bridgekit.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class SequenceIterator<E> implements Iterator<E>
{
    private final Iterator<? extends E> m_one;
    private final Iterator<? extends E> m_two;
    private Iterator<? extends E> m_active;

    public SequenceIterator(final Iterator<? extends E> one, final Iterator<? extends E> two)
    {
        m_one = one;
        m_two = two;
        m_active = null;
    }

    public boolean hasNext()
    {
        if (m_one.hasNext())
        {
            m_active = m_one;
            return true;
        }
        else if (m_two.hasNext())
        {
            m_active = m_two;
            return true;
        }
        else
        {
            m_active = null;
            return false;
        }
    }

    public E next()
    {
        if (hasNext())
        {
            return m_active.next();
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    public void remove()
    {
        if (null != m_active)
        {
            m_active.remove();
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
