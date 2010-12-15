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

import javax.xml.XMLConstants;

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
     
        builder.startDocument(null, null); // this should be possible, right?
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
        // TODO:
        // this should create a more complex document for navigation testing.
        return builder.getNode();
    }
}
