/*
 * Copyright (c) 2019 TIBCO Software Inc.
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
package org.genxdm.processor.io.tests;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableContext;
import org.genxdm.processor.output.TreeModelDumper;

abstract public class NSFixupCHonXmlSWBase<N>
    implements ProcessingContextFactory<N>
{
    @Test
    public void testDuplicateNamespaceSameElementScope()
    {
        ProcessingContext<N> context = newProcessingContext();
        DocumentHandler<N> handler = context.newDocumentHandler();
        N malformed = makeDocument(context.newFragmentBuilder(), true);
        assertNotNull("Document is null", malformed);
        StringWriter writer = new StringWriter();
        try
        {
            handler.write(writer, malformed);
            System.out.println("--- Duplicate Namespace Same Element ---");
            System.out.println(writer.toString());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        }
    }
    
    @Test
    public void testDoubleNamespaceFromAncestorScope()
    {
        ProcessingContext<N> context = newProcessingContext();
        DocumentHandler<N> handler = context.newDocumentHandler();
        N malformed = makeDocument(context.newFragmentBuilder(), false);
        assertNotNull("Document is null", malformed);
        StringWriter writer = new StringWriter();
        try
        {
            handler.write(writer, malformed);
            System.out.println("--- Duplicate Namespace Ancestor Scope ---");
            System.out.println(writer.toString());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        }
    }
    
    @Test
    public void testConflictsNoPrefixes()
    {
        ProcessingContext<N> context = newProcessingContext();
        DocumentHandler<N> handler = context.newDocumentHandler();
        System.out.println("--- Conflicts in Document without Prefixes ---");
        N malformed = makeDocumentWithoutPrefixes(context.newFragmentBuilder());
        assertNotNull("Document is null", malformed);
        System.out.println("Input tree initially:");
        TreeModelDumper.displayTree(malformed, context.getModel(), System.out);
        StringWriter writer = new StringWriter();
        try
        {
            handler.write(writer, malformed);
            System.out.println("XML representation, without errors:");
            System.out.println(writer.toString());
            fail("Expected GenXDMException for conflicting namespace declarations versus element names");
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        }
        catch (GenXDMException genxdme)
        {
            System.out.println("Got expected exception:");
            genxdme.printStackTrace();
            System.out.println("Input tree now:");
            TreeModelDumper.displayTree(malformed, context.getModel(), System.out);
        }
    }

    private N makeDocumentWithoutPrefixes(FragmentBuilder<N> builder)
    {
        builder.startDocument(null, null);
        builder.startElement("", "local", "");
        builder.namespace("", "http://some/name/space");
        builder.startElement("http://some/name/space", "child", "");
        builder.namespace("", "");
        builder.endElement();
        builder.endElement();
        builder.endDocument();
        return builder.getNode();
    }
    
    private N makeDocument(FragmentBuilder<N> builder, boolean sameElementScope)
    {
        builder.startDocument(null, null);
        builder.startElement("http://some/name/space", "local", "prefix");
        builder.namespace("prefix", "http://some/name/space");
        // if sameElementScope, try putting both namespaces on the same element
        if (sameElementScope)
            builder.namespace("prefix", "http://some/name/space");
        else
        {
            builder.startElement("http://some/name/space", "child", "prefix");
            builder.namespace("prefix", "http://some/name/space");
            builder.endElement();
        }
        builder.endElement();
        builder.endDocument();
        return builder.getNode();
    }
}
