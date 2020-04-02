package org.genxdm.bridgetest.typed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.io.DocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.xs.SchemaComponentCache;

// this test checks consistency: if we have a valid document, is getValue()
// behaving? tests of atom bridge creating things is in atombridgebase,
// and of parsing and validation also in the relevant places, so ignore those.
// here we just want to test that the values are as expected, from a known
// instance with a known schema.

public abstract class TypedModelBase<N, A>
    implements ProcessingContextFactory<N>
{
    // instantiating test cases have to call getSchemaStream() and use it to populate the cache
    // bridgetest cannot depend on the schema parser
    public abstract SchemaComponentCache parseTheSchema();

    // instantiating test cases take the parsed document, the typedContext,
    // and initialize a validator (which bridgetest cannot depend upon, just like the parser)
    // validate the supplied already-parsed instance, and return the result.
    public abstract N validateTheInstance(N parsedDocument);

    @Test
    public void stringValues()
    {
        TypedModel<N, A> model = typedContext.getModel();
        assertNotNull(validDocument);
        N container = model.getFirstChildElement(validDocument);
        assertNotNull(container);
    }

    @Test
    public void numericValues() // incl float double
    {
        // TODO: not implemented. use the strings pattern, and implement
        // in schema, instance, and test evaluation code
    }

    @Test
    public void dateTimeDurationValues() // incl gDayMate date time dateTime duration (+subtypes)
    {
        // TODO: not implemented. use the strings pattern, and implement
        // in schema, instance, and test evaluation code
    }

    @Test
    public void miscellaneousValues() // base64+hex binary, boolean, anyURI, QName, NOTATION
    {
        // TODO: not implemented. use the strings pattern, and implement
        // in schema, instance, and test evaluation code
    }
    
    @Test
    public void emptyValues() // do this separately, or no?
    {
        TypedModel<N, A> model = typedContext.getModel();
        assertNotNull(validDocument);
        N container = model.getFirstChildElement(validDocument);
        assertNotNull(container);
        container = model.getFirstChildElementByName(container, NAMESPACE, EMPTY_CONTENT_CONTAINER);
        assertNotNull(container);
        N target = model.getFirstChildElement(container);
        assertNotNull(target);
        System.out.println("Element {"+model.getNamespaceURI(target)+"}"+model.getLocalName(target)+" has type: "+model.getTypeName(target)+", value=\""+model.getValue(target)+"\"");
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        System.out.println("Element {"+model.getNamespaceURI(target)+"}"+model.getLocalName(target)+" has type: "+model.getTypeName(target)+", value=\""+model.getValue(target)+"\"");
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        System.out.println("Element {"+model.getNamespaceURI(target)+"}"+model.getLocalName(target)+" has type: "+model.getTypeName(target)+", value=\""+model.getValue(target)+"\"");
    }

    protected InputStream getSchemaStream()
    {
        return getClass().getClassLoader().getResourceAsStream(SCHEMA_RESOURCE);
    }

    @Before
    public void setUp()
    {
        context = newProcessingContext();
        cache = parseTheSchema();
        typedContext = context.getTypedContext(cache);
        try 
        {
            DocumentHandler<N> parser = context.newDocumentHandler();
            InputStream instanceStream = getClass().getClassLoader().getResourceAsStream(INSTANCE_RESOURCE);
            N parsedDocument = parser.parse(instanceStream, null);
            validDocument = validateTheInstance(parsedDocument);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        }
    }

    protected ProcessingContext<N> context;
    protected SchemaComponentCache cache;
    protected TypedContext<N, A> typedContext;
    protected N validDocument;
    
    private static final String SCHEMA_RESOURCE = "typedValues.xsd";
    private static final String INSTANCE_RESOURCE = "typedValues.xml";
    private static final String NAMESPACE = "http://www.example.com/typedValues";
    private static final String EMPTY_CONTENT_CONTAINER = "desert";
    private static final String EMPTY_STRING_ELEMENT = "emptyString";
    private static final String EMPTY_NORMALIZED_STRING_ELEMENT = "emptyNormalizedString";
    private static final String EMPTY_ANY_URI = "emptyAnyURI";
}
