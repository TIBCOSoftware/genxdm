/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.bridgetest.base.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;

import org.genxdm.bridgetest.TestBase;

import org.junit.Test;
import org.junit.Ignore;

// Note: this *also* handles the testing of ContentHandler and NodeSource,
// since FragmentBuilder is the canonical implementation of both.
public abstract class BuilderTestBase<N>
    extends TestBase<N>
{
    @Test
    @Ignore("Isn't this accomplished with any frag build? (we got lots)")
    public void constructionWF()
    {
        // verify we can construct things well-formed
        // make a sequence of documents
        // make a sequence of elements
        // make a sequence of children
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
        // startElement, text, attribute (sub comment|pi for text; sub ns for attr)
        // startdocument, startElement, endDocument
        // startdocument, text (not just whitespace)
        // startdocument, startelement, endelement, text (not whitespace)
        // startelement, startdocument (or startdocument after *anything* but enddocument)
        // endelement with no corresponding startelement
        // enddocument with no corresponding startdocument
        // sibling text?
    }
    
    private void createSequence(FragmentBuilder<N> builder)
    {
        builder.reset();
        
        // create something *weird*.  there are several possibilities,
        // and the question is, what's legal?
        
        // a list of attributes.  Note: it isn't reasonable to have attributes mixed
        // with anything else.
        
        // a preliminary list of namespaces, followed by several elements
        // which may have complex content.  this is the "fragment" model
        // (note that this may be defined, in xquery context, as a 'document')
        
        // multiple documents in sequence.
        
        // multiple elements in sequence
        
        // a mixed list of children (text, element, comment, pi)
    }
}
