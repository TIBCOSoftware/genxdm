package org.genxdm.bridgetest.typed;

import org.genxdm.bridgetest.TestBase;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.Schema;

public abstract class TypedTestBase<N, A>
    extends TestBase<N>
{
    protected TypedContext<N, A> getTypedContext()
    {
        return newProcessingContext().getTypedContext();
    }
    
    protected N createValidTypedDocument()
    {
        TypedContext<N, A> schemaContext = getTypedContext();
        // create/register the schema components
        // build the (untyped) document
        // validate it
        // return it
        return null;
    }
    
    protected void initializeSchema(Schema schema)
    {
    }
}
