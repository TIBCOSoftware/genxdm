package org.genxdm.bridgetest.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableContext;
import org.genxdm.mutable.MutableCursor;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;
import org.junit.Test;

public abstract class MutableContextBase<N>
    extends TestBase<N>
{

    @Test
    public void accessorsAndFactories()
    {
        // there are only four methods, three of them accessors, one a factory.
        ProcessingContext<N> context = newProcessingContext();
        assertNotNull(context);
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        // this does *not* check the feature. do *not* enable
        // this test if the bridge doesn't support mutability (duh)
        MutableContext<N> mutant = context.getMutableContext();
        assertNotNull(mutant);
        
        ProcessingContext<N> c2 = mutant.getProcessingContext();
        assertNotNull(c2);
        assertEquals(context, c2);
        
        MutableModel<N> model = mutant.getModel();
        assertNotNull(model); // but don't do anything with it yet.
        MutableCursor<N> cursor = mutant.newCursor(doc);
        assertNotNull(cursor);
        
        NodeFactory<N> factory = mutant.getNodeFactory();
        assertNotNull(factory);
        
        // bth-, bth-, bthat's all, f-f-folks!
    }
    
}
