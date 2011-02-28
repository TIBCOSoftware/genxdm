package org.genxdm.bridgekit.tree;

import java.util.Iterator;

import org.genxdm.NodeKind;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.NodeInformer;

/** Filter an existing iterator.
 * 
 * This uses the supplied Model's (NodeInformer's) matches() method to match on NodeKind,
 * namespace, and name.  Any or all of the three arguments may be null.
 * Neither the model nor the base iterator may be null.
 * 
 * @param <N> node abstraction
 */
public class MatchingIterator<N>
    implements Iterator<N>
{
    public MatchingIterator(final NodeInformer<N> model, final Iterator<N> base, final NodeKind kind, final String namespace, final String name)
    {
        PreCondition.assertNotNull(model);
        PreCondition.assertNotNull(base);
        this.model = model;
        this.iterator = base;
        this.kind = kind;
        this.namespace = namespace;
        this.name = name;
        matchNext();
    }

    @Override
    public boolean hasNext()
    {
        return next != null;
    }

    @Override
    public N next()
    {
        N current = next;
        matchNext();
        return current;
    }

    @Override
    public void remove()
    {
        iterator.remove();
    }
    
    private void matchNext()
    {
        while (iterator.hasNext())
        {
            N node = iterator.next();
            if (model.matches(node, kind, namespace, name))
            {
                next = node;
                return; // short-circuit
            }
        }
        next = null;
    }

    private final NodeInformer<N> model;
    private final Iterator<N> iterator;
    private final NodeKind kind;
    private final String namespace;
    private final String name;
    private N next;
}
