/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.gxml.bridge.dom.tests;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.base.Cursor;
import org.gxml.bridgetest.CursorTestBase;
import org.gxml.bridge.dom.DomProcessingContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DomCursorTest
    extends CursorTestBase<Node>
{
    public final DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

    public void testCursorSymbolIntegrity()
        throws ParserConfigurationException, IOException, URISyntaxException
    {
        final DomProcessingContext pcx = newProcessingContext();

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);

        final DocumentBuilder db = dbf.newDocumentBuilder();

        final Document document = db.newDocument();
        final Element element = document.createElementNS(
                copy("http://www.example.com"), copy("x:foo"));
        document.appendChild(element);

        final Cursor<Node> cursor = pcx.newCursor(document);
        assertTrue(cursor.moveToFirstChild());
        assertEquals(copy("http://www.example.com"), cursor
                .getNamespaceURI());
        assertEquals(copy("foo"), cursor.getLocalName());
    }

// WTF is this supposed to test, anyway?
// why are we testing fragment builder in a cursor test?
// is this more of the now-removed "symbols test on =="?
//    public void testSymbolViolationOnBuilder0001()
//        throws ParserConfigurationException, IOException, URISyntaxException
//    {
//        final DomProcessingContext pcx = newProcessingContext();
//        final FragmentBuilder<Node> builder = pcx.newFragmentBuilder();
//        builder.startDocument(null);
//        try
//        {
//            builder.startElement(copy("http://www.example.com"), copy("root"), "x");
//        }
//        catch (final Throwable e)
//        {
//            return;
//        }
//        fail();
//    }
//
//    public void testSymbolViolationOnBuilder0002()
//        throws ParserConfigurationException, IOException, URISyntaxException
//    {
//        final DomProcessingContext pcx = newProcessingContext();
//        final FragmentBuilder<Node> builder = pcx.newFragmentBuilder();
//        builder.startDocument(null);
//        try
//        {
//            builder.startElement(copy("http://www.example.com"), copy("root"), "x");
//        }
//        catch (final Throwable e)
//        {
//            return;
//        }
//        fail();
//    }
//
//    public void testSymbolViolationOnBuilder0003()
//        throws ParserConfigurationException, IOException, URISyntaxException
//    {
//        final DomProcessingContext pcx = newProcessingContext();
//        final FragmentBuilder<Node> builder = pcx.newFragmentBuilder();
//        builder.startDocument(null);
//        builder.startElement(copy("http://www.example.com"), copy("root"), "x");
//        try
//        {
//            builder.namespace("y", copy("http://www.y.com"));
//        }
//        catch (final Throwable e)
//        {
//            return;
//        }
//        fail();
//    }
}