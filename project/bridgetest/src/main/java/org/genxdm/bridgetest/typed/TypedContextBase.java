package org.genxdm.bridgetest.typed;

import static org.junit.Assert.assertNotNull;

import org.genxdm.typed.ValidationHandler;
import org.genxdm.xs.SchemaParser;
import org.junit.Test;

public abstract class TypedContextBase<N, A>
    extends TypedTestBase<N, A>
{
    
    // not needed for this test
    @Override
    public ValidationHandler<A> getValidationHandler()
    {
        return null;
    }
    @Override
    public SchemaParser getSchemaParser()
    {
        return null;
    }

    @Test
    public void bridges()
    {
        assertNotNull(tc);
        assertNotNull(tc.getAtomBridge());
        assertNotNull(tc.getTypesBridge());
    }
    
    @Test
    public void navigators()
    {
    }
    
}
