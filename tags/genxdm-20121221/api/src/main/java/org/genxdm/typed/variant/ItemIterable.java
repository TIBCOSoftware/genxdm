package org.genxdm.typed.variant;

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
        public Iterable<A> atomIterable()
        {
            return iterable;
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
        
        private final Iterable<N> iterable;
    }
}
