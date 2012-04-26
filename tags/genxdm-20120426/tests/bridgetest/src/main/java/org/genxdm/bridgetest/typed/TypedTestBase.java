package org.genxdm.bridgetest.typed;

import org.genxdm.bridgetest.TestBase;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.xs.Schema;

public abstract class TypedTestBase<N, A>
    extends TestBase<N>
{
    protected static enum Doctype { PO, OTHER; };
    
    protected TypedContext<N, A> getTypedContext()
    {
        return newProcessingContext().getTypedContext(null);
    }
    
    protected N createValidTypedDocument(Doctype dc)
    {
        TypedContext<N, A> schemaContext = getTypedContext();
        // create/register the schema components
        initializeSchema(schemaContext.getTypesBridge(), dc);
        // build the typed document
        N document = buildTypedDocument(schemaContext.newSequenceBuilder(), dc);
        // return it
        return document;
    }
    
    protected void initializeSchema(Schema schema, Doctype dc)
    {
        // the challenge here is that we want to register schema components
        // without testing a schema parser.
        // so we manually create the components and register them.
    }
    
    protected N buildTypedDocument(SequenceBuilder<N, A> builder, Doctype dc)
    {
        // the challenge here is creating a document with type annotations,
        // without testing the validator.  validation testing is inherently
        // dependent on a specific validation processor; we don't want to
        // test that, here.  So we create the document using the typed
        // builder APIs, after first setting up the schema cache.
        return null;
    }
    
    // okay. document. let's do something purchase-order-ish
    // shipto addr; billto addr; orderdate; 
    // items : item { name, sku, quantity }
    // addr { name, address, city, state zip, phone }
}
