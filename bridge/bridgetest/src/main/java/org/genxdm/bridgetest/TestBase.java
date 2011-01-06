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
package org.genxdm.bridgetest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;

import org.genxdm.base.Model;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.exceptions.PreCondition;

/** Base class for deriving contract-based test cases.
 *
 * TestBase should be extended by a conformance test case.  Because
 * TestBase implements TestCase, every method beginning with "test"
 * will be tested.  Each method should test some portion of the contract
 * established by the interface which it is testing, and be appropriately
 * named.
 *
 * Because TestBase implements ProcessingContextFactory, each test, or the
 * test setup, should start by retrieving a ProcessingContext, calling
 * newProcessingContext.  As a consequence, it is trivially easy to specialize
 * the test case for each new bridge; each bridge adds a simple implementation
 * in which the only added method is the implementation of newProcessingContext().
 *
 * Otherwise, TestBase provides some useful utility methods.
 *
 * This is based on the original GxTestBase implementation, simplified.
 *
 * @author aaletal@gmail.com
 */
abstract public class TestBase<N>
    implements ProcessingContextFactory<N>
{
    
    public N createSimpleAllKindsDocument(FragmentBuilder<N> builder)
    {
        PreCondition.assertNotNull(builder);
        // create a simple document via the fragment builder.
        // this very simple document contains precisely one node of each node kind.

        // <doc att="value" xmlns:ns="ns"><!-- comment -->text<?target data?></doc>
        
        URI uri = null;
        try { uri = new URI(URI_PREFIX + SIMPLE_DOC); }
        catch (URISyntaxException urise) { /* do nothing */}
        
        builder.startDocument(uri, null);
        builder.startElement(XMLConstants.NULL_NS_URI, "doc", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "att", XMLConstants.DEFAULT_NS_PREFIX, "value", null);
        builder.namespace("ns", "ns");
        builder.comment("comment");
        builder.text("text");
        builder.processingInstruction("target", "data");
        builder.endElement();
        builder.endDocument();
        
        // return the root node.
        return builder.getNode();
    }
    
    public N createComplexTestDocument(FragmentBuilder<N> builder)
    {
        PreCondition.assertNotNull(builder);
        URI uri = null;
        try { uri = new URI(URI_PREFIX + COMPLEX_DOC); }
        catch (URISyntaxException urise) { /* do nothing */}
        
        builder.startDocument(uri, null);
        // TODO:
        // this should create a more complex document for navigation testing.
        return builder.getNode();
    }
    
    public N createIdsAndRefsTestDocument(FragmentBuilder<N> builder)
    {
        PreCondition.assertNotNull(builder);
        URI uri = null;
        try { uri = new URI(URI_PREFIX + IDS_REFS_DOC); }
        catch (URISyntaxException urise) { /* do nothing */}
        
        builder.startDocument(uri, null);
        // TODO:
        // this should create a document that has both IDs and IDREFs.
        // it should probably do this both with the doctype decl,
        // and by using xml:id syntax.
        return builder.getNode();
    }
    
    // this is an internal helper method to get a namespace node for testing.
    // it's got all sorts of checks to make it fail, so *don't* do that.
    protected N getNamespaceNode(Model<N> model, N element, String prefix)
    {
        PreCondition.assertNotNull(model);
        PreCondition.assertNotNull(element);
        PreCondition.assertTrue(model.isElement(element));
        PreCondition.assertNotNull(prefix);
        
        Iterable<N> namespaces = model.getNamespaceAxis(element, false);
        for (N namespace : namespaces)
        {
            if (model.getLocalName(namespace).equals(prefix))
                return namespace;
        }
        return null;
    }
    
    protected static final String URI_PREFIX = "http://www.genxdm.org/sample/";
    protected static final String SIMPLE_DOC = "simple";
    protected static final String COMPLEX_DOC = "complex";
    protected static final String IDS_REFS_DOC = "ids_refs";
}
