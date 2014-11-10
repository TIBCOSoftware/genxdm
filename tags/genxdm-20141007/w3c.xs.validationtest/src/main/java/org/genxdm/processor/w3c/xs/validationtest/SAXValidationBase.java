package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.DefaultCatalog;
import org.genxdm.processor.w3c.xs.DefaultCatalogResolver;
import org.genxdm.processor.w3c.xs.DefaultSchemaCatalog;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.junit.Test;

public abstract class SAXValidationBase<N, A>
    extends ValidatorTestBase<N, A>
{
    public void loadSchema(SchemaComponentCache cache)
        throws AbortException
    {
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

    @Test
    public void validatePOWhileParsing()
        throws AbortException, IOException
    {
        ProcessingContext<N> context = newProcessingContext();
        TypedContext<N, A> cache = context.getTypedContext(null);
        loadSchema(cache.getSchema());
        
        SAXValidator<A> validator = getSAXValidator();
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
        // TODO: we really do need some standard reporters and resolvers, you know.
        TypedDocumentHandler<N, A> parser = cache.newDocumentHandler(validator, null, null);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("po.xml");
        N typed = parser.parse(stream, null);
        assertNotNull(typed);
        assertEquals(0, catcher.size());
        
        POVerifier.verifyUntyped(typed, context.getModel());
        POVerifier.verifyTyped(typed, cache.getModel());
    }
}
