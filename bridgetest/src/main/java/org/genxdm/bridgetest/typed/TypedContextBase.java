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
/*
** Validate or revalidate a tree in memory.
 * 
 * @param source the starting point for validation; must not be null.
 * @param validator the (partially-initialized) validation handler to be
 * used; must not be null.
 * @param schemaNamespace the URI of the schema namespace to be used for
 * validation; may not be null, but may be the empty string (global namespace).
 * @return a node representing the equivalent to the supplied source node,
 * as the base of a validated (type-annotated and type-valued) tree; never
 * null.  If validation has failed, the tree may not be typed.  The returned
 * node <em>may</em>, but is not required to be the same node supplied as an
 * argument, suitably modified with type annotations and typed values, if
 * the bridge supports this form of mutation; otherwise, the bridge may
 * create and return a tree comparable to the supplied tree, with added
 * type annotations and typed values.
 *
N validate(N source, ValidationHandler<A> validator, URI schemaNamespace);
*/