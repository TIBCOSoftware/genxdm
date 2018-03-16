package org.genxdm.bridgekit.misc;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Parameterized LIFO (Last-In-First-Out) structure, commonly know as a Stack.
 */
public final class Stack<E> implements Iterable<E>
{
// apparently we want to have our own implementation of a stack?
// i guess this is because it's simpler than the java.util.collections mess?
    private final LinkedList<E> m_list = new LinkedList<E>();

    public void push(final E entry)
    {
        m_list.addFirst(entry);
    }

    public E peek()
    {
        return m_list.getFirst();
    }

    public E pop()
    {
        return m_list.removeFirst();
    }

    public E change(E entry)
    {
        final E previous = m_list.removeFirst();
        m_list.addFirst(entry);
        return previous;
    }

    public int size()
    {
        return m_list.size();
    }

    public void clear()
    {
        m_list.clear();
    }

    public boolean isEmpty()
    {
        return m_list.isEmpty();
    }

    public Iterator<E> iterator()
    {
        return m_list.iterator();
    }

    public E[] toArray(final E[] nodes)
    {
        return m_list.toArray(nodes);
    }

    public boolean contains(final E entry)
    {
        return m_list.contains(entry);
    }
}
