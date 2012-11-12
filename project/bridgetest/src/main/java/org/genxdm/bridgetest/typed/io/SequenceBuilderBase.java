package org.genxdm.bridgetest.typed.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.genxdm.bridgetest.typed.TypedTestBase;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.junit.Test;

public abstract class SequenceBuilderBase<N, A>
    extends TypedTestBase<N, A>
{

    @Test
    public void constructionTypedSimplest()
    {
        TypedContext<N, A> context = getTypedContext(null);
        N docDoc = createValidTypedDocument(context, Doctype.SIMPLEST);
        TypedModel<N, A> model = context.getModel();
        
        assertNotNull(docDoc);
        N current = model.getFirstChild(docDoc);
        assertNotNull(current);
        assertEquals(NSCOM, model.getNamespaceURI(current));
        assertEquals("doc", model.getLocalName(current));
        QName typeName = new QName(NSCOM, "docType");
        assertEquals(typeName, model.getTypeName(current));
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
    
}
