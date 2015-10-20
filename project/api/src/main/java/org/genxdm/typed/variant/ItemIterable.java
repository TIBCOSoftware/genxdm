package org.genxdm.typed.variant;

import java.util.Iterator;

import org.genxdm.exceptions.PreCondition;

public class ItemIterable<N, A>
{
    private ItemIterable() {}
    
    public Iterable<A> atomIterable()
    {
        return null;
    }
    
    public Iterable<N> nodeIterable()
    {
        return null;
    }
    
    public Iterable<Item<N, A>> mixedIterable()
    {
        return null;
    }
    
    public boolean isAtomIterable()
    {
        return false;
    }
    
    public boolean isNodeIterable()
    {
        return false;
    }
    
    public boolean isMixedIterable()
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
        public Iterable<A> atomIterable()
        {
            return iterable;
        }
        
        @Override
        public Iterable<Item<N, A>> mixedIterable()
        {
            return new ItemsIterable<N, A>(null, iterable);
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
        public Iterable<N> nodeIterable()
        {
            return iterable;
        }
        
        @Override
        public Iterable<Item<N, A>> mixedIterable()
        {
            return new ItemsIterable<N, A>(iterable, null);
        }
        
        private final Iterable<N> iterable;
    }
    
    public static final class MixedIterable<N, A>
        extends ItemIterable<N, A>
    {
        public MixedIterable(Iterable<Item<N, A>> iterable)
        {
            this.iterable = PreCondition.assertNotNull(iterable);
        }
        
        @Override
        public boolean isMixedIterable()
        {
            return true;
        }
        
        @Override
        public Iterable<Item<N, A>> mixedIterable()
        {
            return iterable;
        }
        
        private final Iterable<Item<N, A>> iterable;
    }
    
    private static final class ItemsIterator<N, A>
        implements Iterator<Item<N, A>>
    {   // can supply nodes or atoms or both null; not both non-null
        ItemsIterator(Iterator<N> n, Iterator<A> a)
        {
            nodes = n;
            atoms = a;
        }
        @Override
        public boolean hasNext()
        {
            if (nodes != null)
                return nodes.hasNext();
            else if (atoms != null)
                return atoms.hasNext();
            return false;
        }
        @Override
        public Item<N, A> next()
        {
            if (nodes != null)
                return new Item.NodeItem<N, A>(nodes.next());
            else if (atoms != null)
                return new Item.AtomItem<N, A>(atoms.next());
            return null;
        }
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        private final Iterator<N> nodes;
        private final Iterator<A> atoms;
    }
    private static final class ItemsIterable<N, A>
        implements Iterable<Item<N, A>>
    {   // can supply nodes or atoms or both null; not both non-null
        ItemsIterable(Iterable<N> n, Iterable<A> a)
        {
            nodes = n;
            atoms = a;
        }
        @Override
        public Iterator<Item<N, A>> iterator()
        {
            if (nodes != null)
                return new ItemsIterator<N, A>(nodes.iterator(), null);
            else if (atoms != null)
                return new ItemsIterator<N, A>(null, atoms.iterator());
            return new ItemsIterator<N, A>(null, null);
        }
        private final Iterable<N> nodes;
        private final Iterable<A> atoms;
    }
}
