/*
 * Copyright (c) 2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridgetest.mutable;

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.Feature;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.filters.NamespaceFixupFilter;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.mutable.MutableCursor;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;
import org.junit.Test;

public abstract class MutableCursorBase<N>
    extends TestBase<N>
{

    @Test
    public void factory()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        NodeFactory<N> factory = newProcessingContext().getMutableContext().newCursor(doc).getFactory();
        assertNotNull(factory);
    }
    
    @Test
    public void children()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        MutableCursor<N> cursor = context.getMutableContext().newCursor(doc);
        MutableCursor<N> cursor2 = context.getMutableContext().newCursor(doc);
        cursor.moveToFirstChild();
        cursor2.moveToFirstChild();
        NodeFactory<N> factory = cursor.getFactory();
        
        
        //appendChild 
       
        //text node 
        N node = factory.createText("New Text1");
        cursor.appendChild(node);
        assertEquals(cursor2.getNodeId(), cursor.getNodeId());
        cursor.moveToLastChild();
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());
        assertEquals("New Text1", cursor.getStringValue());
        
        //element node
        cursor.moveToParent();
        node = factory.createElement(NULL_NS_URI, "child1", DEFAULT_NS_PREFIX);
        cursor.appendChild(node);
        assertEquals( cursor2.getNodeId(), cursor.getNodeId());
        cursor.moveToLastChild();
        assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
        assertEquals("child1", cursor.getLocalName());
        
        
        //appendChildren Comment and PI node
        cursor.moveToParent();
        N comment = factory.createComment("New Comment1");
        N pi = factory.createProcessingInstruction("pi1", "value1");
        List<N> children = new ArrayList<N>(2);
        children.add(comment);
        children.add(pi);
        
        cursor.appendChildren(children);
        assertEquals(cursor2.getNodeId(),cursor.getNodeId() );
        cursor.moveToLastChild();
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, cursor.getNodeKind());
        assertEquals("pi1", cursor.getLocalName());
        cursor.moveToPreviousSibling();
        assertEquals(NodeKind.COMMENT, cursor.getNodeKind());
        assertEquals("New Comment1", cursor.getStringValue());
        

        //prependChild    text node 
        cursor.moveToParent();
        node = factory.createText("New Text1");
        cursor.prependChild(node);
        assertEquals(cursor2.getNodeId(),cursor.getNodeId());
        cursor.moveToFirstChild();
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());
        assertEquals("New Text1", cursor.getStringValue());
        
        //prependChild    element node 
        cursor.moveToParent();
        node = factory.createElement(NULL_NS_URI, "child2", DEFAULT_NS_PREFIX);
        cursor.prependChild(node);
        assertEquals(cursor2.getNodeId(), cursor.getNodeId());
        cursor.moveToFirstChild();
        assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
        assertEquals("child2", cursor.getLocalName());


        
        //prependChildren comments and PI nodes
        cursor.moveToParent();
        comment = factory.createComment("comment2");
        pi = factory.createProcessingInstruction("pi2", "value2");
        children = new ArrayList<N>(2);
        children.add(comment);
        children.add(pi);
        
        cursor.prependChildren(children);
        assertEquals(cursor2.getNodeId(), cursor.getNodeId());
        cursor.moveToFirstChild();
        assertEquals(NodeKind.COMMENT, cursor.getNodeKind());
        assertEquals("comment2", cursor.getStringValue());
        cursor.moveToNextSibling();
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, cursor.getNodeKind());
        assertEquals("pi2", cursor.getLocalName());
        

    }
    
    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        MutableCursor<N> cursor = context.getMutableContext().newCursor(doc);
        cursor.moveToFirstChild();
        MutableModel<N> model = context.getMutableContext().getModel();
        NodeFactory<N> factory = cursor.getFactory();
        N docElem = model.getFirstChild(doc);
        N attr1 = factory.createAttribute(NULL_NS_URI, "attr1", DEFAULT_NS_PREFIX, "value1");
        cursor.insertAttribute(attr1);
       
        
        assertEquals(model.getNodeId(docElem),cursor.getNodeId());
        assertEquals("value1",cursor.getAttributeStringValue(NULL_NS_URI, "attr1"));
        
        cursor.moveTo(attr1);
        cursor.delete();
        cursor.moveTo(docElem);
        
        N attr2 = factory.createAttribute(NULL_NS_URI, "attr2", DEFAULT_NS_PREFIX, "value2");
        N attr3 = factory.createAttribute(NULL_NS_URI, "attr3", DEFAULT_NS_PREFIX, "value3");
        
        List<N> attList = new ArrayList<N>(2);
        attList.add(attr2);
        attList.add(attr3);
        cursor.insertAttributes(attList);
        
        assertEquals(model.getNodeId(docElem),cursor.getNodeId() );
        assertEquals("value2",cursor.getAttributeStringValue(NULL_NS_URI, "attr2"));
        assertEquals("value3",cursor.getAttributeStringValue(NULL_NS_URI, "attr3"));
    }
    
    @Test
    public void namespace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        MutableCursor<N> cursor = context.getMutableContext().newCursor(doc);
        cursor.moveToFirstChild();
        MutableModel<N> model = context.getMutableContext().getModel();
        N docElem = model.getFirstChild(doc);
        
        // note: not having a namespace axis isn't an excuse
        // namespace *support* is required, it's just that namespaces
        // are allowed to be metadata, not nodes
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            cursor.insertNamespace("ns1", "http://www.test.com/ns1");
            assertEquals( model.getNodeId(docElem), cursor.getNodeId());
            assertEquals("http://www.test.com/ns1",cursor.getNamespaceForPrefix("ns1"));
            
            cursor.insertNamespace(DEFAULT_NS_PREFIX, NULL_NS_URI);
            assertEquals(model.getNodeId(docElem),cursor.getNodeId());
            assertEquals(NULL_NS_URI,cursor.getNamespaceForPrefix(DEFAULT_NS_PREFIX));
            
            cursor.insertNamespace("ns2", "http://www.test.com/ns2");
            assertEquals(model.getNodeId(docElem) , cursor.getNodeId());
            assertEquals("http://www.test.com/ns1",cursor.getNamespaceForPrefix("ns1"));
            cursor.moveTo(getNamespaceNode(model, docElem, "ns2"));
            assertEquals("http://www.test.com/ns2", cursor.getStringValue());
            assertEquals("ns2", cursor.getLocalName());
        }
        
    }
    
    
    @Test
    public void siblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        MutableModel<N> model = context.getMutableContext().getModel();
        MutableCursor<N> cursor = context.getMutableContext().newCursor(doc);
        NodeFactory<N> factory = cursor.getFactory();
        
        N e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        N text1 = factory.createText("Text1");
        
        N e2 = factory.createElement(NULL_NS_URI, "e2", DEFAULT_NS_PREFIX);
        N e3 = factory.createElement(NULL_NS_URI, "e3", DEFAULT_NS_PREFIX);
        
        cursor.moveToFirstChild();
        N docElem = model.getFirstChild(doc);
        
        // insert element before comment
        N mark = model.getFirstChild(docElem);
        cursor.moveToFirstChild();
        cursor.insertBefore(e1);
        assertEquals( model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(e1),cursor.getNodeId());
        
        
        
        // insert element before text
        mark = model.getNextSibling(mark);
        cursor.moveToNextSibling();
        cursor.moveToNextSibling();
        cursor.insertBefore(e2);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(e2),cursor.getNodeId());
        
        
        // insert text before text
        cursor.moveToNextSibling();
        cursor.insertBefore(text1);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(text1),cursor.getNodeId());
 
 
        // checking for merging of text node
        cursor.moveToParent();
        cursor.moveToLastChild();
        cursor.moveToPreviousSibling();
        assertEquals("text", cursor.getStringValue());
        
        // insert element before pi
        mark = model.getLastChild(docElem);
        cursor.moveToParent();
        cursor.moveToLastChild();
        cursor.insertBefore(e3);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(e3), cursor.getNodeId());

  
        model.delete(e1);
        model.delete(e2);
        model.delete(text1);

        // insert element before element
        e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        cursor.insertBefore(e1);
        assertEquals(model.getNodeId(e3),cursor.getNodeId());
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(e1),cursor.getNodeId());
        cursor.moveToParent();

        
  
        model.delete(e1); 
        model.delete(e3);

       
        
        // now do it again, the other way round.
        e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        e2 = factory.createElement(NULL_NS_URI, "e2", DEFAULT_NS_PREFIX);
        e3 = factory.createElement(NULL_NS_URI, "e3", DEFAULT_NS_PREFIX);
        text1 = factory.createText("Text1");
      
        
        
        
        // insert element after comment
        mark = model.getFirstChild(docElem);
        cursor.moveToFirstChild();
        cursor.insertAfter(e1);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(e1),cursor.getNodeId());
        
        
        
        // insert element after text
        mark = model.getNextSibling(model.getNextSibling(mark));
        cursor.moveToNextSibling();
        cursor.insertAfter(e2);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(e2),cursor.getNodeId());
        
        
        // insert text after text
        cursor.moveToPreviousSibling();
        cursor.insertAfter(text1);
        assertEquals(model.getNodeId(mark),cursor.getNodeId() );
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(text1),cursor.getNodeId());
 
        cursor.moveToParent();
        cursor.moveToLastChild();
        cursor.moveToPreviousSibling();
        cursor.moveToPreviousSibling();
        // checking for merging of text node
        assertEquals("Text1", cursor.getStringValue());
        
        //insert element after pi
        mark = model.getLastChild(docElem);
        cursor.moveToNextSibling();
        cursor.moveToNextSibling();
        cursor.insertAfter(e3);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(e3),cursor.getNodeId());


  
        model.delete(e1);
        model.delete(e2);
        model.delete(text1);

        // insert element after element
        e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        cursor.insertAfter(e1);
        assertEquals(model.getNodeId(e3),cursor.getNodeId());
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(e1),cursor.getNodeId());
        cursor.moveToParent();
        
  
        model.delete(e1); 
        model.delete(e3);

        
        //  Now inserting in group
        cursor.moveToFirstChild();
        mark = model.getFirstChild(docElem);
        
        
        e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        text1 = factory.createText("Text1");
        N pi1 = factory.createProcessingInstruction("PI1", "value1");
        N comment1 = factory.createComment("comment1");

        List<N> sibs = new ArrayList<N>(2);
        sibs.add(e1);
        sibs.add(text1);
        sibs.add(pi1);
        sibs.add(comment1);
        
        cursor.insertBefore(sibs);
        assertEquals(model.getNodeId(mark),cursor.getNodeId());

        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(comment1),cursor.getNodeId());
        
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(pi1),cursor.getNodeId() );
        
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(text1),cursor.getNodeId());
        
        cursor.moveToPreviousSibling();
        assertEquals(model.getNodeId(e1),cursor.getNodeId());
        
        
        // remove them 
        cursor.moveTo(mark);
        cursor.moveToPreviousSibling();
        
        cursor.delete();
        cursor.delete();
        cursor.delete();
        cursor.delete();
        
        
        //insert after comment node
        cursor.moveTo(mark);
        cursor.insertAfter(sibs);
        
        assertEquals(model.getNodeId(mark),cursor.getNodeId());

        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(e1),cursor.getNodeId());
        
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(text1),cursor.getNodeId());
        
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(pi1),cursor.getNodeId());
        
        cursor.moveToNextSibling();
        assertEquals(model.getNodeId(comment1),cursor.getNodeId());

        cursor.delete();
        cursor.delete();
        cursor.delete();
        cursor.delete();
        
        //Inserting node as siblings of the root element is 
        //allowed in Axiom and CX but not allowed in DOM
    }
    
    @Test
    public void delete()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        MutableModel<N> model = context.getMutableContext().getModel();
        MutableCursor<N> cursor = context.getMutableContext().newCursor(doc);
        cursor.moveToFirstChild();
        cursor.moveToLastChild();
        
        N docElem = model.getFirstChildElement(doc);
        N target = model.getLastChild(docElem);
        N marker = model.getPreviousSibling(target);
        N result = cursor.delete(); //delete PI
        assertEquals(target, result);
        assertEquals(model.getNodeId(marker),cursor.getNodeId());
        
        target = marker;
        marker = model.getPreviousSibling(marker);
        result = cursor.delete(); //delete text
        assertEquals(target, result);
        assertEquals(model.getNodeId(marker),cursor.getNodeId());
        
        target = marker;
        result = cursor.delete(); // delete comment
        assertEquals(target, result);
        assertEquals(model.getNodeId(docElem),cursor.getNodeId());
        
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            target = getNamespaceNode(context.getModel(), docElem, "ns");
            cursor.moveTo(target);
            result  = cursor.delete();//delete namespace
            assertEquals(target, result);
            assertEquals(model.getNodeId(docElem),cursor.getNodeId());
            
        }
        
        target = model.getAttribute(docElem, NULL_NS_URI, "att");
        cursor.moveTo(target);
        result = cursor.delete(); // delete att
        assertEquals(target, result);
        assertEquals(model.getNodeId(docElem),cursor.getNodeId());
        
        
        target = docElem;
        result = cursor.delete(); // delete element
        assertEquals(target, result);
        assertEquals(model.getNodeId(doc),cursor.getNodeId());
        
        doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        docElem = model.getFirstChild(doc);
        cursor = context.getMutableContext().newCursor(doc);
        cursor.moveToFirstChild();
        Iterable<N> iter = cursor.deleteChildren(); //delete children
        assertNotNull(iter);
        assertEquals(model.getNodeId(docElem),cursor.getNodeId());
        assertFalse(cursor.moveToFirstChild());
        assertFalse(model.hasChildren(docElem));


    }
    
    
    @Test
    public void replace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        MutableModel<N> model = context.getMutableContext().getModel();
        MutableCursor<N> cursor = context.getMutableContext().newCursor(doc);
        cursor.moveToFirstChild();
        N docElem = model.getFirstChildElement(doc);
        
        
        
        // note that there's no guarantee that a text or comment will
        // retain identity.

        //attribute
        N target = model.getAttribute(docElem, NULL_NS_URI, "att");
        cursor.moveTo(target);
        String val = cursor.replaceValue("none");
        assertEquals(model.getNodeId(target),cursor.getNodeId());
        assertEquals("value", val);
        assertEquals("none", cursor.getStringValue());
        cursor.moveToParent();
        assertEquals("none", cursor.getAttributeStringValue(NULL_NS_URI, "att"));
        
        //comment
        target = model.getFirstChild(docElem); 
        cursor.moveTo(target);
        val = cursor.replaceValue("no comment");
        assertEquals(model.getNodeId(target),cursor.getNodeId());
        assertEquals("comment", val);
        assertEquals("no comment", cursor.getStringValue());
        // don't need to test the value of target it may not have changed.
        
        //text
        target = model.getNextSibling(model.getFirstChild(docElem)); 
        cursor.moveTo(target);
        val = cursor.replaceValue("no text");
        assertEquals(model.getNodeId(target),cursor.getNodeId());
        assertEquals("text", val);
        
        // For Axiom can't actually replace a text node's value,
        // So we have to test with workaround 
        cursor.moveTo(doc);
        cursor.moveToFirstChild();
        cursor.moveToFirstChild();
        cursor.moveToNextSibling();
        assertEquals("no text", cursor.getStringValue());
        
        target = model.getLastChild(docElem); // pi
        cursor.moveTo(target);
        val = cursor.replaceValue("no data");
        assertEquals(model.getNodeId(target),cursor.getNodeId());
        assertEquals("data", val);
        assertEquals("no data", cursor.getStringValue());

        
        // negative testing 

        
        //replacing value of element nodes
        
        cursor.moveToParent();
        
        try
        {
            //it should either throw error or it should return null
            String str = cursor.replaceValue("sample");
            assertEquals(null, str);
        }
        catch (Throwable e) 
        {
        }
        
        

        //replacing value of namespace node
        cursor.moveTo(getNamespaceNode(model, docElem,"ns" ));
        
        try
        {
            // it should either throw error or it should return null 
            String str = cursor.replaceValue("test");
            assertEquals(null, str);
        }
        catch (Throwable e) 
        {
        }
        
        
        
        //now replace elements instead of value
        
        NodeFactory<N> factory = cursor.getFactory();
        N e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        N text1 = factory.createText("Text1");
        N comment1 = factory.createComment("comment1");
        
        
        //replacing attribute with another attribute 
        target = factory.createAttribute("http://www.test.com/attr1", "attr1", "t", "value1");
        N att = model.getAttribute(docElem, NULL_NS_URI, "att");
        cursor.moveTo(att);
        N old = cursor.replace(target);
        assertEquals(model.getNodeId(target), cursor.getNodeId());
        assertEquals("attr1", cursor.getLocalName());
        assertEquals(model.getNodeId(old), model.getNodeId(att));
        
        
        //replacing namespace node with another namespace 
        if(context.isSupported(Feature.NAMESPACE_AXIS))
        {
            cursor.moveTo(docElem);
            e1 = factory.createElement(NULL_NS_URI, "temp", DEFAULT_NS_PREFIX);
            target = model.insertNamespace(e1, "ns2", "http://www.test.com/ns2");
            model.delete(target);
            N ns = getNamespaceNode(model, docElem, "ns");
            cursor.moveTo(ns);
            old = cursor.replace(target);
            assertEquals(model.getNodeId(target), cursor.getNodeId());
            assertEquals("ns2", cursor.getLocalName());
            assertEquals(model.getNodeId(old), model.getNodeId(ns));
        }
        
        
        // replacing comment node with element node
        e1 = factory.createElement(NULL_NS_URI, "e1", DEFAULT_NS_PREFIX);
        target = model.getFirstChild(docElem);
        cursor.moveTo(target);
        old = cursor.replace(e1);
        assertEquals(model.getNodeId(e1), cursor.getNodeId());
        assertEquals("e1", cursor.getLocalName());
        assertEquals(model.getNodeId(old), model.getNodeId(target));
        

        //replacing text node with another text node 
        target = model.getNextSibling(e1);
        cursor.moveToNextSibling();
        old = cursor.replace(text1);
        assertEquals(model.getNodeId(text1), cursor.getNodeId());
        assertEquals("Text1", cursor.getStringValue());
        assertEquals(model.getNodeId(old), model.getNodeId(target));
        
        
        //replacing PI node with another comment node 
        target = model.getNextSibling(text1);
        cursor.moveToNextSibling();
        old = cursor.replace(comment1);
        assertEquals(model.getNodeId(comment1), cursor.getNodeId());
        assertEquals("comment1", cursor.getStringValue());
        assertEquals(model.getNodeId(old), model.getNodeId(target));
        
        //replacing document element node with another element node 
        e1 = factory.createElement(NULL_NS_URI, "root", DEFAULT_NS_PREFIX);
        cursor.moveToParent();
        old = cursor.replace(e1);
        assertEquals(model.getNodeId(e1), cursor.getNodeId());
        assertEquals("root", cursor.getLocalName());
        assertEquals(model.getNodeId(old), model.getNodeId(docElem));
       
        
        // restoring old document node
        doc = createSimpleAllKindsDocument(context.newFragmentBuilder(new NamespaceFixupFilter()));
        cursor = context.getMutableContext().newCursor(doc);
        docElem = model.getFirstChild(doc);
        old = null;
        
        
        //trying to break 
        
        
        // replacing text node with attribute
        target = model.getNextSibling(model.getFirstChild(docElem));
        cursor.moveTo(target);
        att = model.getAttribute(docElem, NULL_NS_URI, "att");
        try
        {
            old =  cursor.replace(att);
        }
        catch (Throwable e) 
        {
            
        }
        
        assertEquals(model.getNodeId(target), cursor.getNodeId());
        assertEquals("text", cursor.getStringValue());
        assertNull(old);
        
        
        // replacing text node with namespace
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            N namespace = getNamespaceNode(model, docElem, "ns");
            try
            {
                old =  cursor.replace(namespace);
            }
            catch (Throwable e) 
            {

            }
        }
        
        assertEquals(model.getNodeId(target), cursor.getNodeId());
        assertEquals("text", cursor.getStringValue());
        assertNull(old);
        
        
        
        // replacing text node with null element
        
        try
        {
            old =  cursor.replace(null);
        }
        catch (Throwable e) 
        {

        }
        
        assertEquals(model.getNodeId(target), cursor.getNodeId());
        assertEquals("text", cursor.getStringValue());
        assertNull(old);
        

        // replacing attribute with comment
        target = model.getFirstChild(docElem);
        cursor.moveTo(att);
        try
        {
            old = cursor.replace(target);
        }
        catch (Throwable e) 
        {
            
        }
       
        assertEquals(model.getNodeId(att), cursor.getNodeId());
        assertEquals("value", cursor.getStringValue());
        assertNull(old);
        
        
        // replacing attribute with text
        target = model.getNextSibling(target);
        try
        {
            old = cursor.replace(target);
        }
        catch (Throwable e) 
        {
            
        }
       
        assertEquals(model.getNodeId(att), cursor.getNodeId());
        assertEquals("value", cursor.getStringValue());
        assertNull(old);
        
        
        // replacing attribute with element
        target = factory.createElement(NULL_NS_URI, "element", DEFAULT_NS_PREFIX);
        try
        {
            old = cursor.replace(target);
        }
        catch (Throwable e) 
        {
            
        }
       
        assertEquals(model.getNodeId(att), cursor.getNodeId());
        assertEquals("value", cursor.getStringValue());
        assertNull(old);
        
        
        
        
        // replacing namespace with text
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            N namespace = getNamespaceNode(model, docElem, "ns");
            target = model.getNextSibling(model.getFirstChild(docElem));
            cursor.moveTo(namespace);
            try
            {
                old = cursor.replace(target);
            }
            catch (Throwable e) 
            {
                
            }
           
            assertEquals(model.getNodeId(namespace), cursor.getNodeId());
            assertEquals("ns", cursor.getStringValue());
            assertNull(old);
        }
        

        // replacing document with another element
        target = factory.createElement(NULL_NS_URI, "sample", DEFAULT_NS_PREFIX);;
        cursor.moveTo(doc);
        old = null;
        try
        {
            old = cursor.replace(target);
        }
        catch (Throwable e) 
        {

        }
        assertEquals(model.getNodeId(doc), cursor.getNodeId());
        assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());
        assertNull(old);
        
    }
}
