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
package org.genxdm.bridge.dom.tests;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.base.Model;
import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.ModelTestBase;
import org.genxdm.names.NamespaceBinding;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DomModelTest
    extends ModelTestBase<Node>
{
    public final DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

    public void testSymbolIntegrity()
        throws ParserConfigurationException, IOException, URISyntaxException
    {
        final DomProcessingContext pcx = newProcessingContext();

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);

        final DocumentBuilder db = dbf.newDocumentBuilder();

        final Document document = db.newDocument();

        final Element element = document.createElementNS(
                copy("http://www.example.com"), copy("x:foo"));

        final Attr namespace = document.createAttributeNS(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI, copy("xmlns:x"));
        namespace.setValue(copy("http://www.example.com"));
        element.setAttributeNodeNS(namespace);

        final Attr attribute = document.createAttributeNS(
                copy("http://www.example.com"), copy("foo"));
        element.setAttributeNodeNS(attribute);

        assertNodeSymbolSemantics(element, pcx);
    }

    public void testNamespaceUnawareDOM()
        throws ParserConfigurationException, IOException, URISyntaxException
    {
        final DomProcessingContext pcx = newProcessingContext();
        final Model<Node> model = pcx.getModel();

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);

        final DocumentBuilder db = dbf.newDocumentBuilder();

        final Document document = db.newDocument();

        {
            final Element element = document.createElement("foo");
            assertEquals("", model.getNamespaceURI(element));
            assertEquals("foo", model.getLocalName(element));

            document.appendChild(element);

            final Attr attribute = document.createAttribute("bar");
            assertEquals("", model.getNamespaceURI(element));
            assertEquals("bar", model.getLocalName(attribute));

            element.setAttributeNode(attribute);
        }

        for (final Node node : model.getDescendantOrSelfAxis(document))
        {
            assertNodeSymbolSemantics(node, pcx);
            for (final Node namespace : model.getNamespaceAxis(node, false))
            {
                assertNodeSymbolSemantics(namespace, pcx);
            }
            for (final Node attribute : model.getAttributeAxis(node, false))
            {
                assertNodeSymbolSemantics(attribute, pcx);
            }
        }
    }

    /**
     * Some bridge implementations may use {@link String} directly for symbols.
     */
    public static void assertNodeSymbolSemantics(final Node node, final DomProcessingContext pcx)
    {
        final Model<Node> model = pcx.getModel();

        switch (model.getNodeKind(node))
        {
            case ELEMENT:
            {

                for (final Node namespace : model.getNamespaceAxis(node, false))
                {
                    assertNodeSymbolSemantics(namespace, pcx);
                }
                for (final NamespaceBinding binding : model.getNamespaceBindings(node))
                {
                	// TODO - do we need something here?
                }
                for (final Node attribute : model.getAttributeAxis(node, false))
                {
                    assertNodeSymbolSemantics(attribute, pcx);
                }
                for (final QName name : model.getAttributeNames(node, true))
                {
                	// TODO - do we need something here?
                }
            }
                break;
            case NAMESPACE:
            {
            }
                break;
            case ATTRIBUTE:
            {
            }
                break;
            case DOCUMENT:
            case TEXT:
            case COMMENT:
            {
            }
                break;
            default:
            {
                throw new AssertionError(model.getNodeKind(node));
            }
        }

    }

    /**
     * Do anything to manufacture a String that is equal, but not identical (the
     * same), as the original.
     * <p>
     * This method has the post-condition that the strings are equal but not the
     * same.
     * </p>
     * 
     * @param original
     *            The original.
     * @return A copy of the original string.
     */
    public static String copy(final String original)
    {
        final String copy = original.concat("junk").substring(0,
                original.length());
        // Post-conditions verify that this is effective.
        assertEquals(original, copy);
        assertNotSame(original, copy);
        // Be Paranoid
        assertTrue(original.equals(copy));
        assertFalse(original == copy);
        // OK. That'll do.
        return copy;
    }
}
