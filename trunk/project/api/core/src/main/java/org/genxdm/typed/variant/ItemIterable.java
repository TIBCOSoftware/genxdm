package org.genxdm.typed.variant;

import java.util.Iterator;

import org.genxdm.exceptions.PreCondition;

public class ItemIterable<N, A>
{
    private ItemIterable() {}
    
    public Iterator<A> atomIterator()
    {
        return new EmptyIterator<A>();
    }
    
    public Iterator<N> nodeIterator()
    {
        return new EmptyIterator<N>();
    }
    
    public boolean isAtomIterable()
    {
        return false;
    }
    
    public boolean isNodeIterable()
    {
        return false;
    }
    
    public static final class AtomIterable<N, A>
        extends ItemIterable<N, A>
    {
        public AtomIterable(Iterable<A> iterable)
        {
            this.iterable = PreCondition.assertNotNull(iterable);
        }
        
        @Override
        public boolean isAtomIterable()
        {
            return true;
        }
        
        @Override
        public Iterator<A> atomIterator()
        {
            return iterable.iterator();
        }
        
        private final Iterable<A> iterable;
    }
    
    public static final class NodeIterable<N, A>
        extends ItemIterable<N, A>
    {
        public NodeIterable(Iterable<N> iterable)
        {
            this.iterable = PreCondition.assertNotNull(iterable);
        }
        
        @Override
        public boolean isNodeIterable()
        {
            return true;
        }
        
        @Override
        public Iterator<N> nodeIterator()
        {
            return iterable.iterator();
        }
        
        private final Iterable<N> iterable;
    }

    private class EmptyIterator<E>
        implements Iterator<E>
    {
        @Override
        public boolean hasNext()
        {
            return false;
        }
        @Override
        public E next()
        {
            return null;
        }

        @Override
        public void remove()
        {
        }
    }
}
