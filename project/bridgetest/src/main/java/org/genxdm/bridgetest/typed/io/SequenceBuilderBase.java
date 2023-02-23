package org.genxdm.bridgetest.typed.io;

import static org.junit.Assert.assertTrue;

import org.genxdm.bridgetest.typed.TypedTestBase;
import org.junit.Before;
import org.junit.Test;

// this is now entirely no-op.
// TODO: add schemas and tests.
public abstract class SequenceBuilderBase<N, A>
    extends TypedTestBase<N, A>
{

    @Test
    public void constructionTypedSimplest()
    {
//        N docDoc = createValidTypedDocument(context, Doctype.SIMPLEST);
//        TypedModel<N, A> model = context.getModel();
        
//        assertNotNull(docDoc);
//        N current = model.getFirstChild(docDoc);
//        assertNotNull(current);
//        assertEquals(NSCOM, model.getNamespaceURI(current));
//        assertEquals("doc", model.getLocalName(current));
//        QName typeName = new QName(NSCOM, "docType");
//        assertEquals(typeName, model.getTypeName(current));
        assertTrue(true);
    }
    
    @Test
    public void constructionTypedTextMarkup()
    {
        // TODO
        assertTrue(true);
    }
    
    @Test
    public void constructionTypedPO()
    {
        // TODO
        assertTrue(true);
    }
    
    @Test
    public void constructionTypedSoapyMess()
    {
        // TODO
        assertTrue(true);
    }
    
    @Before
    public void initCache()
    {
    }

    private static final String BASE_DIR = "createcontent/";
}
