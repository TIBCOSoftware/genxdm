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

import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.names.NamespaceBinding;
import org.junit.Test;

public abstract class NodeInformerBase<N>
    extends TestBase<N>
{

    @Test
    public void nodeKinds()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // we're going to assume that navigation works; that's tested
        // in nodenavigator.
        assertTrue(model.getNodeKind(doc) == NodeKind.DOCUMENT);
        
        N docElement = model.getFirstChildElement(doc);
        assertTrue(model.getNodeKind(docElement) == NodeKind.ELEMENT);
        assertTrue(model.isElement(docElement));
        
        N node = model.getAttribute(docElement, "", "att");
        assertNotNull(node);
        assertTrue(model.getNodeKind(node) == NodeKind.ATTRIBUTE);
        assertTrue(model.isAttribute(node));
  
        node = getNamespaceNode(model, docElement, "ns");
        assertNotNull(node);
        assertTrue(model.getNodeKind(node) == NodeKind.NAMESPACE);
        assertTrue(model.isNamespace(node));
        
        node = model.getFirstChild(docElement);
        assertNotNull(node); // it should be a comment
        assertTrue(model.getNodeKind(node) == NodeKind.COMMENT);
        
        node = model.getNextSibling(node);
        assertNotNull(node); // it should be text
        assertTrue(model.getNodeKind(node) == NodeKind.TEXT);
        assertTrue(model.isText(node));
        
        node = model.getNextSibling(node);
        assertNotNull(node); // it should be a pi
        assertTrue(model.getNodeKind(node) == NodeKind.PROCESSING_INSTRUCTION);
    }
    
    @Test
    public void nodeIdentity()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);

        // in this part, test that the same node accessed in different
        // ways still *is* the same node.
        Object id = model.getNodeId(doc);
        N docElement = model.getFirstChildElement(doc);
        assertNotNull(docElement);
        assertEquals(id, model.getNodeId(model.getParent(docElement)));
        
        id = model.getNodeId(docElement);
        assertEquals(id, model.getNodeId(model.getFirstChildElement(doc)));

        // in this part, create an "identical" document, and assert that no
        // node in this second instance has the same id as the corresponding
        // node in the first instance (we ignore namespace nodes, here, but
        // maybe ought to, though they're sort of a pain).
        builder.reset();
        N doc2 = createSimpleAllKindsDocument(builder);
        N doc2Element = model.getFirstChildElement(doc2);
        
        assertFalse(model.getNodeId(doc).equals(model.getNodeId(doc2)));
        assertFalse(model.getNodeId(docElement).equals(model.getNodeId(doc2Element)));

        assertFalse(model.getNodeId(model.getAttribute(docElement, XMLConstants.NULL_NS_URI, "att")).equals(model.getNodeId(model.getAttribute(doc2Element, XMLConstants.NULL_NS_URI, "att"))));
        assertFalse(model.getNodeId(getNamespaceNode(model, docElement, "ns")).equals(model.getNodeId(getNamespaceNode(model, doc2Element, "ns"))));
        
        // comment
        N node = model.getFirstChild(docElement);
        N node2 = model.getFirstChild(doc2Element);
        assertFalse(model.getNodeId(node).equals(model.getNodeId(node2)));
        
        // text
        node = model.getNextSibling(node);
        node2 = model.getNextSibling(node2);
        assertFalse(model.getNodeId(node).equals(model.getNodeId(node2)));

        // pi
        node = model.getNextSibling(node);
        node2 = model.getNextSibling(node2);
        assertFalse(model.getNodeId(node).equals(model.getNodeId(node2)));
    }
    
    @Test
    public void idsAndRefs()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createIdsAndRefsTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        N node = model.getFirstChildElement(doc); // doc
        node = model.getFirstChildElement(node); // e1
        node = model.getFirstChildElement(node); // e2
        
        N idNode = model.getFirstChildElement(node); // e3
        assertTrue(model.isId(idNode));
        N attr = model.getAttribute(idNode, XMLConstants.NULL_NS_URI, "id");
        assertTrue(model.isId(attr));

        // TODO: we've modified this to permit implementations to disable
        // idrefs checking (set the protected boolean disableIdrefsTests to
        // true in the concrete subclass).  see issue 50.  this is a poor
        // solution to a real problem in the dom implementation.
        N idRefNode = model.getNextSibling(idNode); // e4
        attr = model.getAttribute(idRefNode, XMLConstants.NULL_NS_URI, "ref");
        // checking for an obscure issue - an attribute which is NOT an ID
        // attribute with no namespace caused problems with some bridges
        assertTrue(!model.isId(attr));
        
        if (!disableIdrefsTests)
        {
            assertTrue(model.isIdRefs(idRefNode));
            assertTrue(model.isIdRefs(attr));
        }

        idNode = model.getNextSibling(idRefNode); // e5
        assertTrue(model.isId(idNode));
        attr = model.getAttribute(idNode, XMLConstants.XML_NS_URI, "id");
        assertTrue(model.isId(attr));
    }
    
    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // contract seems less than well-documented.
        // the rule is complex: on an element, getAttributeNames never
        // returns null, but may return an empty iterable.  on any other
        // node type, it always returns null.
        // getAttributeStringValue may return null.
        // it should not return anything for a "namespace attribute",
        // when a tree model provides such things.
        
        Iterable<QName> attributes = model.getAttributeNames(doc, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(doc, "", "xyzzy"));
        N node = model.getFirstChildElement(doc);
        assertNotNull(node);
        
        attributes = model.getAttributeNames(node, false);
        assertNotNull(attributes);
        // TODO: we really need a test that has an element with no attributes
        int count = 0;
        for (QName name : attributes)
        {
            assertNotNull(name.getLocalPart());
            assertNotNull(name.getNamespaceURI());
            assertNotNull(name.getPrefix());
            count++;
        }
        assertEquals(1, count);
        assertEquals("value", model.getAttributeStringValue(node, "", "att"));
        
        node = model.getFirstChild(node); // comment
        attributes = model.getAttributeNames(node, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(node, "", "att"));
        
        node = model.getNextSibling(node); // text
        attributes = model.getAttributeNames(node, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(node, "", "att"));
        
        node = model.getNextSibling(node); // pi
        attributes = model.getAttributeNames(node, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(node, "", "att"));
    }
    
    @Test
    public void namespaces()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        N docEl = model.getFirstChildElement(doc);
        // there's only one namespace declared; we shouldn't have a problem.
        assertEquals("ns", model.getNamespaceForPrefix(docEl, "ns"));
        for (String nsName : model.getNamespaceNames(docEl, false))
        {
            assertEquals("ns", nsName);
        }
        for (NamespaceBinding binding : model.getNamespaceBindings(docEl))
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
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        assertFalse(model.hasAttributes(doc));
        assertFalse(model.hasNamespaces(doc));
        assertTrue(model.hasChildren(doc));
        assertFalse(model.hasParent(doc));
        assertFalse(model.hasPreviousSibling(doc));
        assertFalse(model.hasNextSibling(doc));

        N elem = model.getFirstChildElement(doc);
        assertNotNull(elem);
        assertTrue(model.hasAttributes(elem));
        assertTrue(model.hasNamespaces(elem));
        assertTrue(model.hasChildren(elem));
        assertTrue(model.hasParent(elem));
        assertFalse(model.hasPreviousSibling(elem));
        assertFalse(model.hasNextSibling(elem));
        
        N node = model.getAttribute(elem, "", "att");
        assertNotNull(node);
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertFalse(model.hasPreviousSibling(node));
        assertFalse(model.hasNextSibling(node));
        
        node = getNamespaceNode(model, elem, "ns");
        assertNotNull(node);
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertFalse(model.hasPreviousSibling(node));
        assertFalse(model.hasNextSibling(node));
        
        node = model.getFirstChild(elem);
        assertNotNull(node); // comment node
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertFalse(model.hasPreviousSibling(node));
        assertTrue(model.hasNextSibling(node));
        
        node = model.getNextSibling(node);
        assertNotNull(node); // text
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertTrue(model.hasPreviousSibling(node));
        assertTrue(model.hasNextSibling(node));
        
        node = model.getNextSibling(node);
        assertNotNull(node); // pi
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertTrue(model.hasPreviousSibling(node));
        assertFalse(model.hasNextSibling(node));
    }
    
    @Test
    public void names()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // docs have no name
        assertNull(model.getNamespaceURI(doc));
        assertNull(model.getLocalName(doc));
        assertNull(model.getPrefix(doc));
        
        N el = model.getFirstChildElement(doc);
        assertNotNull(el);
        assertEquals("", model.getNamespaceURI(el));
        assertEquals("doc", model.getLocalName(el));
        assertEquals("", model.getPrefix(el));
        
        N n = model.getAttribute(el, "", "att");
        assertNotNull(n);
        assertEquals("", model.getNamespaceURI(n));
        assertEquals("att", model.getLocalName(n));
        assertEquals("", model.getPrefix(n));
        
        // this looks weird, but it's all according to spec.
        // alas.
        n = getNamespaceNode(model, el, "ns");
        assertNotNull(n);
        assertEquals(XMLConstants.NULL_NS_URI, model.getNamespaceURI(n));
        assertEquals("ns", model.getLocalName(n));
        assertEquals(XMLConstants.DEFAULT_NS_PREFIX, model.getPrefix(n));
        
        // comments have no name
        n = model.getFirstChild(el);
        assertNotNull(n);
        assertNull(model.getNamespaceURI(n));
        assertNull(model.getLocalName(n));
        assertNull(model.getPrefix(n));
        
        // text nodes have no name
        n = model.getNextSibling(n);
        assertNotNull(n);
        assertNull(model.getNamespaceURI(n));
        assertNull(model.getLocalName(n));
        assertNull(model.getPrefix(n));
        
        n = model.getNextSibling(n);
        assertNotNull(n);
        assertEquals("", model.getNamespaceURI(n));
        assertEquals("target", model.getLocalName(n));
        assertEquals("", model.getPrefix(n));
    }
    
    @Test
    public void values()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        final String text = "text";
        
        assertEquals(text, model.getStringValue(doc));
        N e = model.getFirstChildElement(doc);
        assertEquals(text, model.getStringValue(e));
        
        N n = model.getAttribute(e, "", "att");
        assertEquals("value", model.getStringValue(n));
        
        // binding a prefix to itself is kinda bogus for testing
        n = getNamespaceNode(model, e, "ns");
        assertEquals("ns", model.getStringValue(n));
        
        n = model.getFirstChild(e);
        assertEquals("comment", model.getStringValue(n));
        
        n = model.getNextSibling(n);
        assertEquals(text, model.getStringValue(n));
        
        n = model.getNextSibling(n);
        assertEquals("data", model.getStringValue(n));
    }
    
    @Test
    public void uris()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        URI docURI = null;
        try { docURI = new URI(URI_PREFIX + SIMPLE_DOC); }
        catch (URISyntaxException u) { /* do nothing */ }
        
        boolean docUriSupported = context.isSupported(Feature.DOCUMENT_URI);
        boolean baseUriSupported = context.isSupported(Feature.BASE_URI);
        
        URI uri = model.getDocumentURI(doc);
        if (docUriSupported)
        {
            assertEquals(docURI, uri);
            if (baseUriSupported)
            {
                assertEquals(uri, model.getBaseURI(doc)); // which means that all three are equal
                
                N e = model.getFirstChildElement(doc);
                assertNull(model.getDocumentURI(e));
                assertEquals(uri, model.getBaseURI(e));
                
                N n = model.getAttribute(e, "", "att");
                assertNull(model.getDocumentURI(n));
                assertEquals(uri, model.getBaseURI(n));
                
                n = getNamespaceNode(model, e, "ns");
                assertNotNull(n);
                assertNull(model.getDocumentURI(n));
                assertEquals(uri, model.getBaseURI(n));
                
                n = model.getFirstChild(e);
                assertNull(model.getDocumentURI(n));
                assertEquals(uri, model.getBaseURI(n));
                
                n = model.getNextSibling(n);
                assertNull(model.getDocumentURI(n));
                assertEquals(uri, model.getBaseURI(n));
                
                n = model.getNextSibling(n);
                assertNull(model.getDocumentURI(n));
                assertEquals(uri, model.getBaseURI(n));
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
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // a document does not have a name.
        assertTrue(model.matches(doc, null, null, null));
        assertTrue(model.matches(doc, NodeKind.DOCUMENT, null, null));
        assertTrue(model.matches(doc, null, null));
        assertFalse(model.matches(doc, NodeKind.TEXT, null, null));
        assertFalse(model.matches(doc, XMLConstants.NULL_NS_URI, null));
        assertFalse(model.matches(doc, null, ""));
        
        N element = model.getFirstChild(doc);
        assertTrue(model.matches(element, null, null, null));
        assertTrue(model.matches(element, NodeKind.ELEMENT, null, null));
        assertTrue(model.matches(element, null, null));
        assertTrue(model.matches(element, NodeKind.ELEMENT, null, "doc"));
        assertTrue(model.matches(element, NodeKind.ELEMENT, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(element, NodeKind.ELEMENT, XMLConstants.NULL_NS_URI, "doc"));
        assertTrue(model.matches(element, null, "doc"));
        assertTrue(model.matches(element, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(element, XMLConstants.NULL_NS_URI, "doc"));
        assertFalse(model.matches(element, NodeKind.TEXT, XMLConstants.NULL_NS_URI, "doc"));
        assertFalse(model.matches(element, XMLConstants.NULL_NS_URI, ""));
        assertFalse(model.matches(element, "ns", "doc"));
        
        N n = model.getAttribute(element, "", "att");
        assertTrue(model.matches(n, null, null, null));
        assertTrue(model.matches(n, NodeKind.ATTRIBUTE, null, null));
        assertTrue(model.matches(n, null, null));
        assertTrue(model.matches(n, NodeKind.ATTRIBUTE, null, "att"));
        assertTrue(model.matches(n, NodeKind.ATTRIBUTE, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(n, NodeKind.ATTRIBUTE, XMLConstants.NULL_NS_URI, "att"));
        assertTrue(model.matches(n, null, "att"));
        assertTrue(model.matches(n, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(n, XMLConstants.NULL_NS_URI, "att"));
        assertFalse(model.matches(n, NodeKind.NAMESPACE, XMLConstants.NULL_NS_URI, "att"));
        assertFalse(model.matches(n, XMLConstants.NULL_NS_URI, ""));
        assertFalse(model.matches(n, "ns", "att"));
        
        n = getNamespaceNode(model, element, "ns");
        assertTrue(model.matches(n, null, null, null));
        assertTrue(model.matches(n, NodeKind.NAMESPACE, null, null));
        assertTrue(model.matches(n, null, null));
        assertTrue(model.matches(n, NodeKind.NAMESPACE, null, "ns"));
        assertTrue(model.matches(n, NodeKind.NAMESPACE, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(n, NodeKind.NAMESPACE, XMLConstants.NULL_NS_URI, "ns"));
        assertTrue(model.matches(n, null, "ns"));
        assertTrue(model.matches(n, XMLConstants.NULL_NS_URI, "ns"));
        assertTrue(model.matches(n, XMLConstants.NULL_NS_URI, "ns"));
        assertFalse(model.matches(n, NodeKind.ATTRIBUTE, null, null));
        assertFalse(model.matches(n, "ns", null));
        assertFalse(model.matches(n, XMLConstants.NULL_NS_URI, ""));
        
        n = model.getFirstChild(element); // comment
        assertTrue(model.matches(n, null, null, null));
        assertTrue(model.matches(n, NodeKind.COMMENT, null, null));
        assertTrue(model.matches(n, null, null));
        assertFalse(model.matches(n, NodeKind.TEXT, null, null));
        assertFalse(model.matches(n, XMLConstants.NULL_NS_URI, null));
        assertFalse(model.matches(n, null, ""));
        
        n = model.getNextSibling(n); // text
        assertTrue(model.matches(n, null, null, null));
        assertTrue(model.matches(n, NodeKind.TEXT, null, null));
        assertTrue(model.matches(n, null, null));
        assertFalse(model.matches(n, NodeKind.ELEMENT, null, null));
        assertFalse(model.matches(n, XMLConstants.NULL_NS_URI, null));
        assertFalse(model.matches(n, null, ""));
        
        n = model.getNextSibling(n); // pi
        assertTrue(model.matches(n, null, null, null));
        assertTrue(model.matches(n, NodeKind.PROCESSING_INSTRUCTION, null, null));
        assertTrue(model.matches(n, null, null));
        assertTrue(model.matches(n, NodeKind.PROCESSING_INSTRUCTION, null, "target"));
        assertTrue(model.matches(n, NodeKind.PROCESSING_INSTRUCTION, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(n, NodeKind.PROCESSING_INSTRUCTION, XMLConstants.NULL_NS_URI, "target"));
        assertTrue(model.matches(n, null, "target"));
        assertTrue(model.matches(n, XMLConstants.NULL_NS_URI, null));
        assertTrue(model.matches(n, XMLConstants.NULL_NS_URI, "target"));
        assertFalse(model.matches(n, NodeKind.COMMENT, XMLConstants.NULL_NS_URI, "target"));
        assertFalse(model.matches(n, "ns", "target"));
        assertFalse(model.matches(n, XMLConstants.NULL_NS_URI, ""));
        
    }
    
    protected boolean disableIdrefsTests = false;
}
