package org.genxdm.bridgetest.typed;

import java.io.IOException;
import java.io.InputStream;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.DocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.SchemaParser;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.junit.Before;

// massive changes applied here jan 2023. Original implementation tried to
// apply the untyped pattern to typed code, which was naive, clumsy, and
// so irritating to attempt to do that it was apparently repeatedly deferred.
// modified to be properly abstract, to throw away the enumeration of typed
// documents schema caches (which was largely empty anyway due to the above
// noted problems).
// this is now abstract, and where necessary, asks the implementing concrete
// class to provide information by the usual abstract "give me this object"
// pattern used everywhere else. In this case, we get a validator and a
// schema parser (but not, please note, a schemacomponentcache; it's up to
// our tests to initialize that and keep track of the right one).

public abstract class TypedTestBase<N, A>
    extends TestBase<N>
{
    
    // concrete classes must provide; we will configure
    // this one isn't a member variable, because the whole process of
    // parsing needs to happen before we initialize the validator with
    // an initialized cache.
    public abstract ValidationHandler<A> getValidationHandler();
    
    // concrete classes must provide; we will configure
    public abstract SchemaParser getSchemaParser();
    
    // default implementation returns null; this will be used to get a TypedContext which
    // will then be associated with the specific schema component cache initialized for us.
    // it can be overridden by a subclass (including concrete subclass) to initialize the
    // cache with stuff that's not part of the test.
    public SchemaComponentCache getCache()
    {
        if (schema == null)
        {
            SchemaCacheFactory factory = new SchemaCacheFactory();
            schema =  factory.newSchemaCache();
        }
        return schema;
    }
    
    @Before
    public void setContext()
    {
        context = newProcessingContext();
        schema = getCache();
        tc = context.getTypedContext(schema);
    }
    
    protected ComponentBag parseSchema(final String baseDir, final String targetResource)
        throws AbortException, IOException
    {
        schema = getCache();
        parser = getSchemaParser();
        parser.setComponentProvider(schema.getComponentProvider());
    
        InputStream stream = getClass().getClassLoader().getResourceAsStream(baseDir+targetResource);

        return parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
    }

    public N parseInstance(final DocumentHandler<N> docHandler, final String baseDir, final String target)
        throws IOException
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(baseDir+target);
        return docHandler.parse(stream, null);
    }
    
    protected ProcessingContext<N> context;
    protected TypedContext<N, A> tc;
    protected SchemaComponentCache schema; // once initialized, we can run multiple tests with the same cache.
    protected SchemaParser parser;
}
