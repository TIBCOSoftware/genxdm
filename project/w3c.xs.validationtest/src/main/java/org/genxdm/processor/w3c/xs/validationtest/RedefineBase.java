package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
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
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.junit.Test;

public abstract class RedefineBase<N, A>
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
    
        InputStream stream = getClass().getClassLoader().getResourceAsStream(SS_MG_REDEF_URI);
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
        N untyped = parseInstance(context.newDocumentHandler(), TEST_GOOD_INSTANCE);
        assertNotNull(untyped);

        TypedContext<N, A> cache = context.getTypedContext(null);
        loadSchema(cache.getSchema());

        ValidationHandler<A> validator = getValidationHandler();
        // the validate method *should* set the sequence handler and schema.
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);

        N typed = cache.validate(untyped, validator, null);
        assertNotNull(typed);
        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(0, catcher.size());
        
        untyped = parseInstance(context.newDocumentHandler(), TEST_BAD_INSTANCE);
        assertNotNull(untyped);
        
        catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
        typed = cache.validate(untyped, validator, null);
        assertNotNull(typed);

        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(1, catcher.size());
    }

    @Test
    public void validateWhileParsing()
        throws AbortException, IOException
    {
        ProcessingContext<N> context = newProcessingContext();
        TypedContext<N, A> cache = context.getTypedContext(null);
        loadSchema(cache.getSchema());
        
        SAXValidator<A> validator = getSAXValidator();
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
        TypedDocumentHandler<N, A> parser = cache.newDocumentHandler(validator, null, null);
        InputStream stream = getClass().getClassLoader().getResourceAsStream(TEST_GOOD_INSTANCE);
        N typed = parser.parse(stream, null);
        assertNotNull(typed);
        assertEquals(0, catcher.size());
        
        catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
        parser = cache.newDocumentHandler(validator, null, null);
        stream = getClass().getClassLoader().getResourceAsStream(TEST_BAD_INSTANCE);
        typed = parser.parse(stream, null);
        assertNotNull(typed);
        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(1, catcher.size());
    }

    private class ResourceResolver implements CatalogResolver
    {

        @Override
        public InputStream resolveInputStream(URI catalogURI)
            throws IOException
        {
//System.out.println("Resolving " + catalogURI);
            String strong = null;
            if (catalogURI.toString().equals(SS_MG_BASE_URI) ||
                catalogURI.toString().equals("SuperSet_ModelGroup_Base.xsd"))
                strong = SS_MG_BASE_URI;
            if (catalogURI.toString().equals(SS_MG_REDEF_URI) ||
                catalogURI.toString().equals("SuperSet_ModelGroup_Redef.xsd"))
                strong = SS_MG_REDEF_URI;
//System.out.println("    resolved: " + strong);
            if (strong != null)
                return getClass().getClassLoader().getResourceAsStream(strong);
            return DefaultCatalogResolver.SINGLETON.resolveInputStream(catalogURI);
        }
    }
    
    private static final String BASE_DIR = "redefine/";
    private static final String TEST_GOOD_INSTANCE = BASE_DIR + "Redefine_super_mg.xml";
    private static final String TEST_BAD_INSTANCE = BASE_DIR + "Redefine_super_mg_bad.xml";
    private static final String SS_MG_BASE_URI = BASE_DIR + "SuperSet_ModelGroup_Base.xsd";
    private static final String SS_MG_REDEF_URI = BASE_DIR + "SuperSet_ModelGroup_Redef.xsd";
}
