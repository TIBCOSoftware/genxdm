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
package org.genxdm.bridgetest.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.Cursor;
import org.genxdm.Feature;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.names.NamespaceBinding;
import org.junit.Test;

/**
 * Base Test Class for Cursor unit tests for the Informer methods. 
 *
 * @param <N> - Generic Node.
 */
public abstract class InformerBase<N> 
    extends TestBase<N>
{
    
    @Test
    public void nodeKinds()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc); 
        Cursor cursor = context.newCursor(doc); // Create new Cursor from the Context
        assertNotNull(cursor); 
        
        assertTrue(cursor.getNodeKind() == NodeKind.DOCUMENT);
        
        cursor.moveToFirstChildElement();
        assertTrue(cursor.getNodeKind() == NodeKind.ELEMENT);
        assertTrue(cursor.isElement());
        
        cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "att");
        assertTrue(cursor.getNodeKind() == NodeKind.ATTRIBUTE);
        assertTrue(cursor.isAttribute());

        //TODO : Find a way to find Namespace Node from the Cursor
//        node = getNamespaceNode(cursor, docElement, "ns");
//        assertNotNull();
//        assertTrue(cursor.getNodeKind() == NodeKind.NAMESPACE);
//        assertTrue(cursor.isNamespace());
        
        assertTrue(cursor.moveToParent()); // The cursor is on Attribute Node so move to Element Node.

        assertTrue(cursor.moveToFirstChild());
         
        assertTrue(cursor.getNodeKind() == NodeKind.COMMENT);
        
        assertTrue(cursor.moveToNextSibling());
        
        assertTrue(cursor.getNodeKind() == NodeKind.TEXT);
        assertTrue(cursor.isText());
        
        assertTrue(cursor.moveToNextSibling());
        
        assertTrue(cursor.getNodeKind() == NodeKind.PROCESSING_INSTRUCTION);        
    }

    @Test
    public void nodeIdentity()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);

        // in this part, test that the same node accessed in different
        // ways still *is* the same node.
        Object id = cursor.getNodeId();
        assertTrue(cursor.moveToFirstChildElement());
        
        cursor.moveToParent();
        assertEquals(id, cursor.getNodeId());
        
        builder.reset();
        N doc2 = createSimpleAllKindsDocument(builder);
        Cursor cursor2 = context.newCursor(doc2);
        
        assertTrue( cursor.getNodeKind().equals( cursor2.getNodeKind()));
        assertFalse(cursor.getNodeId().equals(cursor2.getNodeId()));

        cursor.moveToFirstChildElement();
        cursor2.moveToFirstChildElement();

        assertTrue( cursor.getNodeKind().equals( cursor2.getNodeKind()));
        assertFalse(cursor.getNodeId().equals(cursor2.getNodeId()));

        cursor.moveToParent();
        cursor2.moveToParent();
        
        cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "att");
        cursor2.moveToAttribute(XMLConstants.NULL_NS_URI, "att");
        assertTrue(cursor.getNodeKind().equals(cursor2.getNodeKind()));
        assertFalse(cursor.getNodeId().equals(cursor2.getNodeId()));
        
        //TODO : Check for Namespace node on both the cursors 
        //assertFalse(<NameSpace Node on Cursor 1>.equals(<Namespace Node on Cursor 2>));
        
        // comment
        cursor.moveToParent();
        cursor2.moveToParent();

        cursor.moveToFirstChild();
        cursor2.moveToFirstChild();
        assertFalse(cursor.equals(cursor2));
        assertTrue(cursor.getNodeKind().equals(cursor2.getNodeKind()));
        
        // text
        cursor.moveToNextSibling();
        cursor2.moveToNextSibling();
        assertTrue(cursor.getNodeKind().equals(cursor2.getNodeKind()));
        assertFalse(cursor.getNodeId().equals(cursor2.getNodeId()));

        // pi
        cursor.moveToNextSibling();
        cursor2.moveToNextSibling();
        assertTrue(cursor.getNodeKind().equals(cursor2.getNodeKind()));
        assertFalse(cursor.getNodeId().equals(cursor2.getNodeId()));
    }

    @Test
    public void idsAndRefs()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createIdsAndRefsTestDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        cursor.moveToFirstChildElement();
        cursor.moveToFirstChildElement(); // e1
        cursor.moveToFirstChildElement(); // e2
        
        cursor.moveToFirstChildElement();  // e3
        assertTrue(cursor.isId());
        
        cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "id");
        assertTrue(cursor.isId());
        
        cursor.moveToParent();

        // TODO: see issue 50.  idrefs are problematic, at the moment.
        // this workaround allows a specific implementation (dom) to
        // bypass a check it would otherwise currently fail.
        cursor.moveToNextSibling(); // e4
        if (!disableIdrefsTests)
        {
            assertTrue(cursor.isIdRefs());
            cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "ref");
            assertTrue(cursor.isIdRefs());
    
            cursor.moveToParent();
        }
        
        cursor.moveToNextSibling(); // e5
        assertTrue(cursor.isId());
        cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "id");
        assertTrue(cursor.isId());
    }
    
    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        Iterable<QName> attributes = cursor.getAttributeNames(true);
        assertNull(attributes);
        
        assertNull(cursor.getAttributeStringValue(XMLConstants.NULL_NS_URI, "att"));
        
        assertTrue(cursor.moveToFirstChildElement());        
        
        attributes = cursor.getAttributeNames( false);
        assertNotNull(attributes);

        int count = 0;
        for (QName name : attributes)
        {
            assertNotNull(name.getLocalPart());
            assertNotNull(name.getNamespaceURI());
            assertNotNull(name.getPrefix());
            count++;
        }
        assertEquals(1, count);
        assertEquals("value", cursor.getAttributeStringValue("", "att"));

        cursor.moveToFirstChild(); // comment
        attributes = cursor.getAttributeNames(false);
        assertNull(attributes);
        assertNull(cursor.getAttributeStringValue("", "att"));
        
        cursor.moveToNextSibling(); // text
        attributes = cursor.getAttributeNames(false);
        assertNull(attributes);
        assertNull(cursor.getAttributeStringValue("", "att"));

        cursor.moveToNextSibling(); // pi
        attributes = cursor.getAttributeNames(false);
        assertNull(attributes);
        assertNull(cursor.getAttributeStringValue("", "att"));
    }

    @Test
    public void namespaces()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        cursor.moveToFirstChildElement();
        // there's only one namespace declared; we shouldn't have a problem.
        assertEquals("ns", cursor.getNamespaceForPrefix("ns"));
        for (String nsName : cursor.getNamespaceNames(false))
        {
            assertEquals("ns", nsName);
        }
        for (NamespaceBinding binding : cursor.getNamespaceBindings())
        {
            assertEquals("ns", binding.getPrefix());
            assertEquals("ns", binding.getNamespaceURI());
        }
    }
    
    @Test
    public void relationships()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        assertFalse(cursor.hasAttributes());
        assertFalse(cursor.hasNamespaces());
        assertTrue(cursor.hasChildren());
        assertFalse(cursor.hasParent());
        assertFalse(cursor.hasPreviousSibling());
        assertFalse(cursor.hasNextSibling());

        assertTrue(cursor.moveToFirstChildElement());
        
        assertTrue(cursor.hasAttributes());
        assertTrue(cursor.hasNamespaces());
        assertTrue(cursor.hasChildren());
        assertTrue(cursor.hasParent());
        assertFalse(cursor.hasPreviousSibling());
        assertFalse(cursor.hasNextSibling());
        
        assertTrue(cursor.moveToAttribute("", "att"));
        
        assertFalse(cursor.hasAttributes());
        assertFalse(cursor.hasNamespaces());
        assertFalse(cursor.hasChildren());
        assertTrue(cursor.hasParent());
        assertFalse(cursor.hasPreviousSibling());
        assertFalse(cursor.hasNextSibling());

        //TODO : How to find the Namespace node from the Cursor
//        node = getNamespaceNode(cursor, elem, "ns");
//        assertNotNull();
//        assertFalse(cursor.hasAttributes());
//        assertFalse(cursor.hasNamespaces());
//        assertFalse(cursor.hasChildren());
//        assertTrue(cursor.hasParent());
//        assertFalse(cursor.hasPreviousSibling());
//        assertFalse(cursor.hasNextSibling());
        
        cursor.moveToParent();
        assertTrue(cursor.moveToFirstChild());
         // comment node
        assertFalse(cursor.hasAttributes());
        assertFalse(cursor.hasNamespaces());
        assertFalse(cursor.hasChildren());
        assertTrue(cursor.hasParent());
        assertFalse(cursor.hasPreviousSibling());
        assertTrue(cursor.hasNextSibling());
        
        assertTrue(cursor.moveToNextSibling());
         // text
        assertFalse(cursor.hasAttributes());
        assertFalse(cursor.hasNamespaces());
        assertFalse(cursor.hasChildren());
        assertTrue(cursor.hasParent());
        assertTrue(cursor.hasPreviousSibling());
        assertTrue(cursor.hasNextSibling());
        
        assertTrue(cursor.moveToNextSibling());
         // pi
        assertFalse(cursor.hasAttributes());
        assertFalse(cursor.hasNamespaces());
        assertFalse(cursor.hasChildren());
        assertTrue(cursor.hasParent());
        assertTrue(cursor.hasPreviousSibling());
        assertFalse(cursor.hasNextSibling());
    }

    @Test
    public void names()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        // docs have no name
        assertNull(cursor.getNamespaceURI());
        assertNull(cursor.getLocalName());
        assertNull(cursor.getPrefix());
        
        assertTrue(cursor.moveToFirstChildElement());
        
        assertEquals("", cursor.getNamespaceURI());
        assertEquals("doc", cursor.getLocalName());
        assertEquals("", cursor.getPrefix());
        
        assertTrue(cursor.moveToAttribute("", "att"));
        
        assertEquals("", cursor.getNamespaceURI());
        assertEquals("att", cursor.getLocalName());
        assertEquals("", cursor.getPrefix());
        
        cursor.moveToParent();

        //TODO : How to find namespace node from Cursor
//        // this looks weird, but it's all according to spec.
//        // alas.
//        n = getNamespaceNode(model, el, "ns");
//        assertNotNull(n);
//        assertEquals(XMLConstants.NULL_NS_URI, cursor.getNamespaceURI(n));
//        assertEquals("ns", cursor.getLocalName(n));
//        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, cursor.getPrefix(n));
        
        
        // comments have no name
        assertTrue(cursor.moveToFirstChild());
        
        assertNull(cursor.getNamespaceURI());
        assertNull(cursor.getLocalName());
        assertNull(cursor.getPrefix());
        
        // text nodes have no name
        assertTrue(cursor.moveToNextSibling());
        
        assertNull(cursor.getNamespaceURI());
        assertNull(cursor.getLocalName());
        assertNull(cursor.getPrefix());
        
        cursor.moveToNextSibling();
        
        assertEquals("", cursor.getNamespaceURI());
        assertEquals("target", cursor.getLocalName());
        assertEquals("", cursor.getPrefix());
    }
    
    @Test
    public void values()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        final String text = "text";
        
        assertEquals(text, cursor.getStringValue());
        cursor.moveToFirstChildElement();
        assertEquals(text, cursor.getStringValue());
        
        cursor.moveToAttribute("", "att");
        assertEquals("value", cursor.getStringValue());

        //TODO : How to get Namespace node for cursor
//        n = getNamespaceNode(model, e, "ns");
//        assertEquals("ns", cursor.getStringValue(n));
        
        cursor.moveToParent();
        
        cursor.moveToFirstChild();
        assertEquals("comment", cursor.getStringValue());
        
        cursor.moveToNextSibling();
        assertEquals(text, cursor.getStringValue());
        
        cursor.moveToNextSibling();
        assertEquals("data", cursor.getStringValue());
    }
    
    @Test
    public void uris()
    {
        // TODO: see comments in NodeInformerBase about the issues
        // with this API's optionality.
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        URI docURI = null;
        try { docURI = new URI(URI_PREFIX + SIMPLE_DOC); }
        catch (URISyntaxException u) { /* do nothing */ } 
        
        boolean docUriSupported = context.isSupported(Feature.DOCUMENT_URI);
        boolean baseUriSupported = context.isSupported(Feature.BASE_URI);
        
        URI uri = cursor.getDocumentURI();
        if (docUriSupported)
        {
            assertEquals(docURI, uri);
            if (baseUriSupported)
            {
                assertEquals(uri, cursor.getBaseURI()); // which means that all three are equal
                
                cursor.moveToFirstChildElement();
                assertNull(cursor.getDocumentURI());
                assertEquals(uri, cursor.getBaseURI());
                
                cursor.moveToAttribute("", "att");
                assertNull(cursor.getDocumentURI());
                assertEquals(uri, cursor.getBaseURI());
                
                cursor.moveToParent();
                //TODO: Find a way to get a Namespace Node from the Cursor.
//                n = getNamespaceNode(model, e, "ns");
//                assertNull(cursor.getDocumentURI(n));
//                assertEquals(uri, cursor.getBaseURI(n));
                
                cursor.moveToFirstChild();
                assertNull(cursor.getDocumentURI());
                assertEquals(uri, cursor.getBaseURI());
                
                cursor.moveToFirstChild();
                assertNull(cursor.getDocumentURI());
                assertEquals(uri, cursor.getBaseURI());
                
                cursor.moveToFirstChild();
                assertNull(cursor.getDocumentURI());
                assertEquals(uri, cursor.getBaseURI());
            }
        }
    }
    
    @Test
    public void matching()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        // a document does not have a name.
        assertTrue(cursor.matches(null, null, null));
        assertTrue(cursor.matches(NodeKind.DOCUMENT, null, null));
        assertTrue(cursor.matches(null, null));
        assertFalse(cursor.matches(NodeKind.TEXT, null, null));
        assertFalse(cursor.matches(XMLConstants.NULL_NS_URI, null));
        assertFalse(cursor.matches(null, ""));
        
        cursor.moveToFirstChild();
        assertTrue(cursor.matches(null, null, null));
        assertTrue(cursor.matches(NodeKind.ELEMENT, null, null));
        assertTrue(cursor.matches(null, null));
        assertTrue(cursor.matches(NodeKind.ELEMENT, null, "doc"));
        assertTrue(cursor.matches(NodeKind.ELEMENT, XMLConstants.NULL_NS_URI, null));
        assertTrue(cursor.matches(NodeKind.ELEMENT, XMLConstants.NULL_NS_URI, "doc"));
        assertTrue(cursor.matches(null, "doc"));
        assertTrue(cursor.matches(XMLConstants.NULL_NS_URI, null));
        assertTrue(cursor.matches(XMLConstants.NULL_NS_URI, "doc"));
        assertFalse(cursor.matches(NodeKind.TEXT, XMLConstants.NULL_NS_URI, "doc"));
        assertFalse(cursor.matches( XMLConstants.NULL_NS_URI, ""));
        assertFalse(cursor.matches("ns", "doc"));
        
        cursor.moveToAttribute("", "att");
        assertTrue(cursor.matches( null, null, null));
        assertTrue(cursor.matches( NodeKind.ATTRIBUTE, null, null));
        assertTrue(cursor.matches( null, null));
        assertTrue(cursor.matches( NodeKind.ATTRIBUTE, null, "att"));
        assertTrue(cursor.matches( NodeKind.ATTRIBUTE, XMLConstants.NULL_NS_URI, null));
        assertTrue(cursor.matches( NodeKind.ATTRIBUTE, XMLConstants.NULL_NS_URI, "att"));
        assertTrue(cursor.matches( null, "att"));
        assertTrue(cursor.matches( XMLConstants.NULL_NS_URI, null));
        assertTrue(cursor.matches( XMLConstants.NULL_NS_URI, "att"));
        assertFalse(cursor.matches( NodeKind.NAMESPACE, XMLConstants.NULL_NS_URI, "att"));
        assertFalse(cursor.matches( XMLConstants.NULL_NS_URI, ""));
        assertFalse(cursor.matches( "ns", "att"));
        
//        n = getNamespaceNode(model, element, "ns");
//        assertTrue(cursor.matches( null, null, null));
//        assertTrue(cursor.matches( NodeKind.NAMESPACE, null, null));
//        assertTrue(cursor.matches( null, null));
//        assertTrue(cursor.matches( NodeKind.NAMESPACE, null, "ns"));
//        assertTrue(cursor.matches( NodeKind.NAMESPACE, XMLConstants.NULL_NS_URI, null));
//        assertTrue(cursor.matches( NodeKind.NAMESPACE, XMLConstants.NULL_NS_URI, "ns"));
//        assertTrue(cursor.matches( null, "ns"));
//        assertTrue(cursor.matches( XMLConstants.NULL_NS_URI, "ns"));
//        assertTrue(cursor.matches( XMLConstants.NULL_NS_URI, "ns"));
//        assertFalse(cursor.matches( NodeKind.ATTRIBUTE, null, null));
//        assertFalse(cursor.matches( "ns", null));
//        assertFalse(cursor.matches( XMLConstants.NULL_NS_URI, ""));
        
        cursor.moveToParent();
        
        cursor.moveToFirstChild();
//        assertTrue(cursor.matches( null, null, null));
        assertTrue(cursor.matches( NodeKind.COMMENT, null, null));
        assertTrue(cursor.matches( null, null));
        assertFalse(cursor.matches( NodeKind.TEXT, null, null));
        assertFalse(cursor.matches( XMLConstants.NULL_NS_URI, null));
        assertFalse(cursor.matches( null, ""));
        
        cursor.moveToNextSibling(); // text
        assertTrue(cursor.matches( null, null, null));
        assertTrue(cursor.matches( NodeKind.TEXT, null, null));
        assertTrue(cursor.matches( null, null));
        assertFalse(cursor.matches( NodeKind.ELEMENT, null, null));
        assertFalse(cursor.matches( XMLConstants.NULL_NS_URI, null));
        assertFalse(cursor.matches( null, ""));
        
        cursor.moveToNextSibling();// pi
        assertTrue(cursor.matches( null, null, null));
        assertTrue(cursor.matches( NodeKind.PROCESSING_INSTRUCTION, null, null));
        assertTrue(cursor.matches( null, null));
        assertTrue(cursor.matches( NodeKind.PROCESSING_INSTRUCTION, null, "target"));
        assertTrue(cursor.matches( NodeKind.PROCESSING_INSTRUCTION, XMLConstants.NULL_NS_URI, null));
        assertTrue(cursor.matches( NodeKind.PROCESSING_INSTRUCTION, XMLConstants.NULL_NS_URI, "target"));
        assertTrue(cursor.matches( null, "target"));
        assertTrue(cursor.matches( XMLConstants.NULL_NS_URI, null));
        assertTrue(cursor.matches( XMLConstants.NULL_NS_URI, "target"));
        assertFalse(cursor.matches( NodeKind.COMMENT, XMLConstants.NULL_NS_URI, "target"));
        assertFalse(cursor.matches( "ns", "target"));
        assertFalse(cursor.matches( XMLConstants.NULL_NS_URI, ""));
        
    }

    protected boolean disableIdrefsTests = false;
}
