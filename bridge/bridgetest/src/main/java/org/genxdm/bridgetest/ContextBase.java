/**
 * Copyright (c) 2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridgetest;

import org.genxdm.Cursor;
import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableContext;
import org.genxdm.typed.TypedContext;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Abstract base tests for the contract of ProcessingContext.
 * 
 * @author aaletal
 *
 * @param <N> the node handle
 */
public abstract class ContextBase<N> 
    extends TestBase<N> 
{
    
    @Test
    public void bookmark()
    {
        // a bookmark *must* be created.
        // the supplied node *must not* be null (bridge should assert)
        // further testing of the bookmark is up to the bookmark test.
        ProcessingContext<N> context = newProcessingContext();
        
        N testDoc = createSimpleAllKindsDocument(context.newFragmentBuilder()); 
        assertNotNull(testDoc);
        
        assertNotNull(context.bookmark(testDoc));
        try
        {
            context.bookmark(null);
            fail();
        }
        catch (AssertionError err)
        {
            // all correct.
        }
    }
    
    @Test
    public void model()
    {
        // the model *must* be accessible.
        // when called twice in a row, it *must* be the same model.
        // (if the latter is ever to be varied, we should provide a feature for it)
        ProcessingContext<N> context = newProcessingContext();

        Model<N> model = context.getModel();
        assertNotNull(model); // never null
        assertEquals(model, context.getModel()); // always the same
    }
    
    @Test
    public void cursor()
    {
        // verify that the cursor returned *isn't* the same one each time.
        // must be non-null.
        // must assert if node is null.
        ProcessingContext<N> context = newProcessingContext();
        
        N testDoc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        assertNotNull(testDoc);
        
        Cursor<N> cursor = context.newCursor(testDoc);
        assertNotNull(cursor); // never null
                
        // now, we should be able to start two cursors from the same position,
        // and still have different cursors.
        assertFalse(cursor == context.newCursor(testDoc)); // never the same
    }
    
    @Test
    public void fragmentBuilder()
    {
        // *must* be non-null.
        // called twice, should return two different objects.
       ProcessingContext<N> context = newProcessingContext();
       
       FragmentBuilder<N> builder = context.newFragmentBuilder();
       FragmentBuilder<N> slacker = context.newFragmentBuilder();
       
       assertNotNull(builder); // never null
       assertNotNull(slacker);
       assertFalse(builder == slacker); // never the same
       
       N testDoc = createSimpleAllKindsDocument(builder);
       assertNotNull(testDoc);
       
       assertFalse(testDoc == createSimpleAllKindsDocument(slacker)); // and they create different docs, too.

    }
    
    @Test
    public void mutableContext()
    {
        // *may* return null; if so, then the corresponding feature must be unsupported.
        // if the feature is unsupported, *must* return null.
        // if the feature is supported, must return non-null.
        ProcessingContext<N> context = newProcessingContext();

        MutableContext<N> mutant = context.getMutableContext();
        if (context.isSupported(Feature.MUTABILITY))
        {
            assertNotNull(mutant);
            // possibly we should consider whether it's always the same?
        }
        else
        {
            assertNull(mutant);
        }
    }
    
    @Test
    public void typedContext()
    {
        // same as for mutable context: if the return is null, then the corresponding feature must be unsupported.
        // if the feature is unsupported, must return null.
        // if the feature is supported, must return non-null.
        ProcessingContext<N> context = newProcessingContext();
        
        TypedContext<N, ?> typo = context.getTypedContext();
        if (context.isSupported(Feature.TYPED))
        {
            assertNotNull(typo);
            // if it's non-null, should it always be the same one?
        }
        else
        {
            assertNull(typo);
        }
    }
    
    @Test
    public void nodes()
    {
        ProcessingContext<N> context = newProcessingContext();

        assertFalse(context.isNode(this)); // this test case is nobody's node
        assertNull(context.node(this)); // so can't be cast.
        
        N testDoc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        
        assertNotNull(testDoc); // don't crash.  :-)
        
        assertTrue(context.isNode(testDoc)); // the document node is a node.
        assertNotNull(context.node(testDoc)); // cast it as such.
        assertEquals(testDoc, context.node(testDoc)); // and it's always the *same* node; node() doesn't do something stupid.
        
        N[] nodes = context.nodeArray(6); // 6 is a guaranteed-random number.  snrk.
        assertNotNull(nodes);
        assertEquals(nodes.length, 6);
        // TODO
        // test node(), isNode(), and nodeArray().
        // node() must return null if isNode() is false.
        // node() must return null and isNode() must return false if a non-node is supplied.
        // if a real N is passed, then isNode() must return true, and node() must return the same object.
    }
    
    @Test
    public void documentHandlers()
    {
        // just verify that non-null is returned?  always different handler?
        // four methods: two set default resolver and reporter.
        // one creates a document handler with default resolver and reporter.
        // one allows resolver and reporter to be specified.
        ProcessingContext<N> context = newProcessingContext();
        
        DocumentHandler<N> handler1 = context.newDocumentHandler();
        assertNotNull(handler1);
        DocumentHandler<N> handler2 = context.newDocumentHandler();
        assertNotNull(handler2);
        assertFalse(handler1 == handler2);
        
        // TODO: figure out the testable contract for resolvers and reporters, here.
        handler2 = context.newDocumentHandler(null, null);
        assertNotNull(handler2);
    }
}
