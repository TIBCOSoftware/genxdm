package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.xml.namespace.QName;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.DefaultCatalog;
import org.genxdm.processor.w3c.xs.DefaultCatalogResolver;
import org.genxdm.processor.w3c.xs.DefaultSchemaCatalog;
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

public abstract class TypedAnonymousValidationBase<N, A>
    extends ValidatorTestBase<N, A>
{

    public void loadSchema(SchemaComponentCache cache)
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
    
        InputStream stream = getClass().getClassLoader().getResourceAsStream("typed-anonymous-elements.xsd");
        cache.register(parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON));
    }
    
    @Test
    public void validateTypedAnonymousElements()
        throws AbortException, IOException
    {
        ProcessingContext<N> context = newProcessingContext();

        TypedContext<N, A> cache = context.getTypedContext(null);
        loadSchema(cache.getSchema());

        N untypedSimple = generateSimple(context);
        assertNotNull(untypedSimple);
    
        ValidationHandler<A> validator = getValidationHandler();
        // the validate method *should* set the sequence handler and schema.
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
    
        N typedSimple = cache.validate(untypedSimple, validator, TNS_RST);
        assertNotNull(typedSimple);
        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(0, catcher.size());

        N untypedComplex = generateComplex(context);
        assertNotNull(untypedComplex);
        
        validator = getValidationHandler();
        // the validate method *should* set the sequence handler and schema.
        catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
    
        N typedComplex = cache.validate(untypedComplex, validator, TNS_RCT);
        assertNotNull(typedComplex);
        for (SchemaException ex : catcher)
            ex.printStackTrace();
        assertEquals(0, catcher.size());
    }

    private N generateSimple(ProcessingContext<N> context)
    {
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        builder.startElement(TNS, generateRandomName(), TNSP);
        builder.namespace(TNSP, TNS);
        builder.text("123AZ");
        builder.endElement();
        return builder.getNode();
    }
    
    private N generateComplex(ProcessingContext<N> context)
    {
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        builder.startElement(TNS, generateRandomName(), TNSP);
        builder.namespace(TNSP, TNS);
        builder.attribute("", "rst-attr", "", "456BY", DtdAttributeKind.CDATA);
        builder.startElement(TNS, "rst-element", TNSP);
        builder.text("789CX");
        builder.endElement();
        builder.startElement(TNS, "str-element", TNSP);
        builder.text(generateRandomName());
        builder.endElement();
        builder.endElement(); // the container with the unknown name
        return builder.getNode();
    }
    
    private String generateRandomName()
    {
        sb.setLength(0);
        int len = random.nextInt(6) + 4;
        for (int i = 0; i < len ; i++)
            sb.append(alpha[random.nextInt(26)]);
        return sb.toString();
    }
 
    private final Random random = new Random();
    private final StringBuilder sb = new StringBuilder();
 
    private static final char[] alpha = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                                          'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final String TNS = "http://tns.genxdm.org/";
    private static final String TNSP = "tns";
    private static final String RST = "randomSimpleType";
    private static final String RCT = "randomComplexType";
    private static final QName TNS_RST = new QName(TNS, RST);
    private static final QName TNS_RCT = new QName(TNS, RCT);
}
