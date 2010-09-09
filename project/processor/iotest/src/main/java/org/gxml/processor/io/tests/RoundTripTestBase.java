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
package org.gxml.processor.io.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.TestCase;

import org.gxml.bridgekit.ProcessingContextFactory;
import org.gxml.NodeKind;
import org.gxml.base.Cursor;
import org.gxml.base.ProcessingContext;
import org.gxml.base.io.DocumentHandler;
import org.gxml.base.io.FragmentBuilder;

abstract public class RoundTripTestBase<N>
    extends TestCase
    implements ProcessingContextFactory<N>
{

    public void testRoundTrip001()
    {
        ProcessingContext<N> context = newProcessingContext();
        N document = makeDocument001(context);
        checkDocument001(context, document);
        byte[] result = writeDocument(context, document, utf8);
        checkBytes001(result);
        N doc2 = readDocument(context, new InputStreamReader(new ByteArrayInputStream(result), utf8));
        checkDocument001(context, doc2);
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
    
    private N makeDocument001(ProcessingContext<N> context)
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
    
    private void checkDocument001(ProcessingContext<N> context, N document)
    {
        assertNotNull(document);
        Cursor<N> cursor = context.newCursor(document);

        assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());
        assertTrue(cursor.hasChildren());
    }
    
    private void checkBytes001(byte[] content)
    {
        assertNotNull(content);
    }

    private static Charset utf8 = Charset.forName("UTF-8");

}
