package org.genxdm.bridgekit.misc;

import java.util.Iterator;


public final class SequenceIterable<E> implements Iterable<E>
{
    private final Iterable<? extends E> m_one;
    private final Iterable<? extends E> m_two;

    public SequenceIterable(final Iterable<? extends E> one, final Iterable<? extends E> two)
    {
        m_one = one;
        m_two = two;
    }

    public Iterator<E> iterator()
    {
        return new SequenceIterator<E>(m_one.iterator(), m_two.iterator());
    }
}
