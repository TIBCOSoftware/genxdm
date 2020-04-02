package org.genxdm.bridgetest.typed;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.genxdm.ProcessingContext;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.SchemaComponentCache;
import org.junit.Test;

public abstract class TypedContextBase<N, A>
    extends TypedTestBase<N, A>
{

    @Test
    public void bridges()
    {
        TypedContext<N, A> context = getTypedContext(null);
        assertNotNull(context);
        assertNotNull(context.getAtomBridge());
        assertNotNull(context.getTypesBridge());
        TypedContext<N, A> c2 = getTypedContext(null);
        assertTrue(context == c2);
        SchemaComponentCache schema = context.getSchema();
        c2 = getTypedContext(schema);
        assertTrue(context == c2);
    }
    
    @Test
    public void navigators()
    {
        TypedContext<N, A> context = getTypedContext(null);
        ProcessingContext<N> parent = context.getProcessingContext();
        assertNotNull(parent);
        assertNotNull(context.getModel());
        FragmentBuilder<N> builder = parent.newFragmentBuilder();
        N node = createSimpleAllKindsDocument(builder);
        assertNotNull(node);
        assertNotNull(context.newCursor(node));
        assertNotNull(context.newSequenceBuilder());
    }
    
//    @Test
//    public void validation()
//    {
//        // TODO: do this here or do it somewhere else?
//    }
}
