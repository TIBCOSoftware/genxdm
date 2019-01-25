package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.xs.DefaultCatalog;
import org.genxdm.bridgekit.xs.DefaultCatalogResolver;
import org.genxdm.bridgekit.xs.DefaultSchemaCatalog;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.io.DocumentHandler;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.junit.Test;

public abstract class KeysBase<N, A>
    extends ValidatorTestBase<N, A>
{
    public void loadSchema(SchemaComponentCache cache)
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = new ResourceResolver();
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache().getComponentProvider();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
    
        InputStream stream = getClass().getClassLoader().getResourceAsStream(SCHEMA);
        cache.register(parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON));
    }

    public N parseInstance(DocumentHandler<N> parser, String target)
        throws IOException
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(target);
        return parser.parse(stream, null);
    }
    
    @Test
    public void validateTree()
        throws AbortException, IOException
    {
        ProcessingContext<N> context = newProcessingContext();
        N untyped = parseInstance(context.newDocumentHandler(), TEST_INSTANCE);
        assertNotNull(untyped);
        context.newDocumentHandler().write(new PrintWriter(System.out), untyped);

        TypedContext<N, A> cache = context.getTypedContext(null);
        loadSchema(cache.getSchema());

        ValidationHandler<A> validator = getValidationHandler();
        // the validate method *should* set the sequence handler and schema.
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
//        validator.setSchemaExceptionHandler(SchemaExceptionThrower.SINGLETON);
        N typed = cache.validate(untyped, validator, null);
        assertNotNull(typed);
        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(0, catcher.size());
    }

    //@Test
//    public void validateWhileParsing()
//        throws AbortException, IOException
//    {
//        ProcessingContext<N> context = newProcessingContext();
//        TypedContext<N, A> cache = context.getTypedContext(null);
//        loadSchema(cache.getSchema());
//        
//        SAXValidator<A> validator = getSAXValidator();
//        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
//        validator.setSchemaExceptionHandler(catcher);
//        TypedDocumentHandler<N, A> parser = cache.newDocumentHandler(validator, null, null);
//        InputStream stream = getClass().getClassLoader().getResourceAsStream(TEST_INSTANCE);
//        N typed = parser.parse(stream, null);
//        assertNotNull(typed);
//        assertEquals(0, catcher.size());
//    }

    private class ResourceResolver implements CatalogResolver
    {

        @Override
        public InputStream resolveInputStream(URI catalogURI)
            throws IOException
        {
            //System.out.println("Resolving " + catalogURI);
            String strong = null;
            if (catalogURI.toString().equals(SCHEMA) ||
                catalogURI.toString().equals(BARE_SCHEMA))
                strong = SCHEMA;
            if (strong != null)
                return getClass().getClassLoader().getResourceAsStream(strong);
            return DefaultCatalogResolver.SINGLETON.resolveInputStream(catalogURI);
        }
    }
    
    private static final String BASE_DIR = "keys/";
//    private static final String TEST_INSTANCE = BASE_DIR + "input.xml";
    private static final String TEST_INSTANCE = BASE_DIR + "simplest-input.xml";
//    private static final String BARE_SCHEMA = "SalesInformation.xsd";
    private static final String BARE_SCHEMA = "SimplestKey.xsd";
    private static final String SCHEMA = BASE_DIR + BARE_SCHEMA;
}

