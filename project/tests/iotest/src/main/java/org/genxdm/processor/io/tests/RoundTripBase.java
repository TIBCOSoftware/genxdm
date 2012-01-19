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
package org.genxdm.processor.io.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;

import org.genxdm.Cursor;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.junit.Test;

// TODO: this should be testing that we can read and write the
// same thing, every time.  Can we re-use the Events filter in
// bridgetest to verify?  We should also have more documents.

abstract public class RoundTripBase<N>
    implements ProcessingContextFactory<N>
{

    @Test
    public void roundTripUglyDocument()
    {
        ProcessingContext<N> context = newProcessingContext();
        N document = makeUglyDocument(context);
        checkUglyDocument(context, document);
        byte[] result = writeDocument(context, document, utf8);
        assertNotNull(result);
        N doc2 = readDocument(context, new InputStreamReader(new ByteArrayInputStream(result), utf8));
        checkUglyDocument(context, doc2);
    }
    
    private byte[] writeDocument(ProcessingContext<N> context, N document, Charset encoding)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos, utf8);
        try
        {
            DocumentHandler<N> handler = context.newDocumentHandler();
            handler.write(writer, document);
            writer.flush();
            baos.flush();
            writer.close();
            baos.close();
            return baos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
            return null; // which won't *happen* ... stupid compiler
        }
    }
    
    private N readDocument(ProcessingContext<N> context, Reader reader)
    {
        DocumentHandler<N> handler = context.newDocumentHandler();
        try
        {
            return handler.parse(reader, null);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail(ioe.getMessage());
            return null; // *sigh*
        }
    }
    
    private N makeUglyDocument(ProcessingContext<N> context)
    {
/* approximately:
<?xml version="1.0" encoding="UTF-8"?>
<?pi 3.14159265365?>
 <a:a xmlns:a="http://www.example.com/ns/a" a="a">Xmllo, world!<b:b xmlns:b="http://www.example.com/ns/b">
 <!--This child element has no content.--></b:b></a:a>
 <!--All finished now.-->
 
 'cept all one line, or something.
 */
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        builder.startDocument(null, null);
        builder.processingInstruction("pi", "3.1415926535");
        builder.startElement("http://www.example.com/ns/a", "a", "a");
        builder.attribute(XMLConstants.NULL_NS_URI, "a", XMLConstants.DEFAULT_NS_PREFIX, "a", null);
        builder.namespace("a", "http://www.example.com/ns/a");
        builder.text("Xmllo, world!");
        builder.startElement("http://www.example.com/ns/b", "b", "b");
        builder.namespace("b", "http://www.example.com/ns/b");
        builder.comment("This child element has no content.");
        builder.endElement();
        builder.endElement();
        builder.comment("All finished now.");
        builder.endDocument();
        return builder.getNode();
    }
    
    private void checkUglyDocument(ProcessingContext<N> context, N document)
    {
        assertNotNull(document);
        Cursor<N> cursor = context.newCursor(document);

        assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());
        assertTrue(cursor.hasChildren());
    }
    
    private static Charset utf8 = Charset.forName("UTF-8");

}
