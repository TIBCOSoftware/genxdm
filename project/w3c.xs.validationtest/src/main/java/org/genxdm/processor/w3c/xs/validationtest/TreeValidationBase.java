package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.io.DocumentHandler;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.DefaultCatalog;
import org.genxdm.processor.w3c.xs.DefaultCatalogResolver;
import org.genxdm.processor.w3c.xs.DefaultSchemaCatalog;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.Schema;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.junit.Test;

public abstract class TreeValidationBase<N, A>
    extends ValidatorTestBase<N, A>
{

    public void loadSchema(Schema cache)
        throws AbortException
    {
        // TODO: load po.xsd
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = DefaultCatalogResolver.SINGLETON;
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache().getComponentProvider();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);

        InputStream stream = getClass().getClassLoader().getResourceAsStream("po.xsd");
        cache.register(parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON));
    }
    
    public N parseInstance(DocumentHandler<N> parser)
        throws IOException
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("po.xml");
        return parser.parse(stream, null);
    }
    
    @Test
    public void validatePO()
        throws AbortException, IOException
    {
        ProcessingContext<N> context = newProcessingContext();
        N untyped = parseInstance(context.newDocumentHandler());
        assertNotNull(untyped);
        POVerifier.verifyUntyped(untyped, context.getModel());

        TypedContext<N, A> cache = context.getTypedContext(null);
        loadSchema(cache.getSchema());

        ValidationHandler<A> validator = getValidationHandler();
        // the validate method *should* set the sequence handler and schema.
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);

        N typed = cache.validate(untyped, validator, URI.create(""));
        assertNotNull(typed);
        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(0, catcher.size());
        
        POVerifier.verifyTyped(typed, cache.getModel());
        // this is a means of testing sequence handler output, which had problems.
//        SequenceHandler<A> handler = new SequenceHandlerEmitter<A>(cache.getAtomBridge());
//        cache.getModel().stream(typed, handler, false);
    }
    
}
