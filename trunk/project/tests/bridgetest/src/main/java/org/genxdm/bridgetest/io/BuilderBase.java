/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.bridgetest.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.xml.XMLConstants;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.junit.Test;

// Note: this *also* handles the testing of ContentHandler and NodeSource,
// since FragmentBuilder is the canonical implementation of both.
public abstract class BuilderBase<N>
    extends TestBase<N>
{
    @Test
    public void constructionWF()
    {
        // verify we can construct things well-formed
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        
        assertNotNull(builder); // never null
        assertNull(builder.getNode()); // should be null if list is empty
        assertNotNull(builder.getNodes()); // list is never null
        assertTrue(builder.getNodes().isEmpty());

        // make a sequence of documents
        builder.startDocument(null, null);
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        builder.endElement();
        builder.endDocument();
        builder.startDocument(null, null);
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        builder.endElement();
        builder.endDocument();
        List<N> nodes = builder.getNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 2);
        
        builder.reset();
        nodes = builder.getNodes();
        assertTrue(nodes.isEmpty());
        // make a sequence of elements
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        builder.endElement();
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        builder.endElement();
        nodes = builder.getNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 2);
        
        builder.reset();
        nodes = builder.getNodes();
        assertTrue(nodes.isEmpty());
        // make a sequence of children
        builder.comment("comment");
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        builder.endElement();
        builder.text("text");
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        builder.endElement();
        builder.processingInstruction("target", "data");
        nodes = builder.getNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 5);
    }
    
    public void internalSubset()
    {
        // test the internal subset (add the annotation)
        // how do we verify that it's being sucked in?
    }
    
    @Test
    public void identities()
    {
        // verify that we can get different documents from the same
        // event stream, even if we use the same builder
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        
        assertNotNull(builder); // never null
        assertNull(builder.getNode()); // should be null if list is empty
        assertNotNull(builder.getNodes()); // list is never null
        assertTrue(builder.getNodes().isEmpty());
        
        N testDoc = createSimpleAllKindsDocument(builder);
        assertNotNull(testDoc); // base verification
        
        // verify that reset works to produce a null return,
        // and that the same document built twice with the same builder
        // is a different document.
        builder.reset();
        assertNull(builder.getNode());
        assertFalse(testDoc == createSimpleAllKindsDocument(builder));
    }
    
    @Test
    public void illFormed()
    {
        // test various ways of supplying bad data
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        
        assertNotNull(builder); // never null

        // contract for attribute and namespace seems to be:
        // if there's an element open, it goes there, even if we've
        // already started putting child content in.
        // comment this out for now, and think about it a bit.
//        // TODO: vary by substituting comment, pi for text.
//        // startElement, text, attribute (sub comment|pi for text; sub ns for attr)
//        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
//        builder.text("text");
//        try
//        {
//            builder.attribute(XMLConstants.NULL_NS_URI, "attr", XMLConstants.DEFAULT_NS_PREFIX, "value", DtdAttributeKind.CDATA);
//            fail("created attribute sibling of text node inside element");
//        }
//        catch (RuntimeException ge)
//        {
//            // expected: you can't create an attribute as the sibling of a text node
//        }
//        try
//        {
//            builder.namespace("xyzzy", "http://great.underground.empire/");
//            fail("created namespace sibling of text node inside element");
//        }
//        catch (RuntimeException ge)
//        {
//            // expected; can't create namespace sibling of text node inside element.
//        }
        
//        builder.reset();
//        // startdocument, startElement, endDocument
//        builder.startDocument(null, null);
//        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
//        try
//        {
//            builder.endDocument();
//            fail("ended document with open element");
//        }
//        catch (RuntimeException ge)
//        {
//            // expected; can't end the document when there are elements open
//        }
        
        // XDM permits text nodes to be children of a document node; such
        // permission is less restrictive than the XML infoset specs.  So, 
        // the following two tests aren't suitable for an XDM, and they are 
        // commented out.
//        builder.reset();
//        // startdocument, text (not just whitespace)
//        builder.startDocument(null, null);
//        try
//        {
//            builder.text("text is not allowed in the prolog");
//            fail("permitted non-whitespace characters in the prolog");
//        }
//        catch (RuntimeException ge)
//        {
//            // expected: can't put non-whitespace text in the prolog
//        }
//        
//        builder.reset();
//        // startdocument, startelement, endelement, text (not whitespace)
//        builder.startDocument(null, null);
//        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
//        builder.endElement();
//        try
//        {
//           builder.text("text is not allowed in the epilog");
//            fail("permitted non-whitespace characters in the epilog");
//        }
//        catch (RuntimeException ge)
//        {
//            // expected: can't put non-whitespace text in the epilog
//        }

        
        builder.reset();
        // TODO: same thing for document inside document.
        // startelement, startdocument (or startdocument after *anything* but enddocument)
        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
        try
        {
            builder.startDocument(null, null);
            fail("document contained in element");
        }
        catch (RuntimeException ge)
        {
            // expected: can't put a document inside an element
        }
        
        builder.reset();
        // endelement with no corresponding startelement
        try
        {
            builder.endElement();
            fail("permitted the end of a non-existent element");
        }
        catch (RuntimeException ge)
        {
            // expected: can't end an element that isn't started
        }
        
        builder.reset();
        // enddocument with no corresponding startdocument
        try
        {
            builder.endDocument();
            fail("permitted the end of a non-existent document");
        }
        catch (RuntimeException ge)
        {
            // expected: can't end a document that isn't started
        }
        
        builder.reset();
        // sibling text
        // hmmmm.  probably can't test this here.
        // it's *legal* to have multiple calls to builder.text().
        // it just isn't legal to have multiple text nodes in a normalized model.
    }
    
    private void areTheseBitsLegal()
    {
        // a list of attributes.  Note: it isn't reasonable to have attributes mixed
        // with anything else ... except, if it's reasonable to have just
        // a list of attributes, then it ought to be possible to concatenate
        // that list with any other valid list of nodes.
        
        // a preliminary list of namespaces, followed by several elements
        // which may have complex content.  this is the "fragment" model
        // (note that this may be defined, in xquery context, as a 'document')
        
        // multiple documents in sequence.
        
        // multiple elements in sequence
        
        // a mixed list of children (text, element, comment, pi)
    }
}
