package org.genxdm.bridgetest.typed.io;

import static org.junit.Assert.assertTrue;

import org.genxdm.bridgetest.typed.TypedTestBase;
import org.junit.Before;
import org.junit.Test;

// TODO: this doesn't actually need to implement TypedTestBase
// at present. Maybe we should fix that first? Because this class
// wants schema (a ComponentProvider), an atom bridge, and a
// SequenceHandler (the SequenceBuilder) for its output. A set
// of unit tests for this would be quite useful. For that, we
// need to drop the enum idiocy in TypedTestBase and add getSchema()
// and getValidatorFactory() as abstract methods, with some useful
// implementation built into these tests.

public abstract class TypedContentHelperBase<N, A>
    extends TypedTestBase<N, A>
{
    @Test
    public void createBLOBContent()
    {
        // TODO
        assertTrue(true);
    }
    
    @Before
    public void initCache()
    {
    }
    
    private static final String BASE_DIR = "createcontent/";
}
