package org.genxdm.bridgetest.mutable;

import java.net.URI;

import org.genxdm.bridgetest.TestBase;
import org.genxdm.mutable.NodeFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public abstract class NodeFactoryBase<N>
    extends TestBase<N>
{

    @Test
    public void document()
    {
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getNodeFactory(); 
        assertNotNull(factory);
        
        URI uri = URI.create("http://www.example.com/");
        String intSub = "<!DOCTYPE x []>"; // minimally legal
        
        N doc = factory.createDocument(null, null);
        assertNotNull(doc);
        
        doc = factory.createDocument(uri, null);
        assertNotNull(doc);
        
        doc = factory.createDocument(null, intSub);
        assertNotNull(doc);
        
        doc = factory.createDocument(uri, intSub);
        assertNotNull(doc);
    }
    
    @Test
    public void element()
    {
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getNodeFactory(); 
        assertNotNull(factory);
        
        N element = factory.createElement("", "e", "");
        assertNotNull(element);
    }
    
    @Test
    public void attribute()
    {
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getNodeFactory(); 
        assertNotNull(factory);
        
        N attribute = factory.createAttribute("", "a", "", "att");
        assertNotNull(attribute);
        
        attribute = factory.createAttribute("", "a", "", null);
        assertNotNull(attribute);
    }
    
    @Test
    public void text()
    {
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getNodeFactory(); 
        assertNotNull(factory);
        
        N text = factory.createText("text");
        assertNotNull(text);
        
        text = factory.createText(null);
        assertNotNull(text);
    }
    
    @Test
    public void comment()
    {
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getNodeFactory(); 
        assertNotNull(factory);
        
        N comment = factory.createComment(null);
        assertNotNull(comment);
        
        comment = factory.createComment("comment");
        assertNotNull(comment);
    }
    
    @Test
    public void processingInstruction()
    {
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getNodeFactory();
        assertNotNull(factory);
        
        N pi = factory.createProcessingInstruction("processing", null);
        assertNotNull(pi);
        
        pi = factory.createProcessingInstruction("processing", "instruction");
        assertNotNull(pi);
    }
    
}
