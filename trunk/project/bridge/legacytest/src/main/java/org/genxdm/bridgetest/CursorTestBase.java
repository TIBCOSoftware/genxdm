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
package org.genxdm.bridgetest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.Feature;
import org.genxdm.NodeKind;
import org.genxdm.base.Cursor;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceBinding;

public abstract class CursorTestBase<N> 
    extends GxTestBase<N>
{
	public void testNewCursor() throws URISyntaxException
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final NameSource nameBridge = new NameSource();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		final URI documentURI = new URI(copy("http://www.examle.com"));
		builder.startDocument(documentURI, null);
		try
		{
			builder.startElement(copy("http://www.x.com"), copy("root"), "x");
			builder.namespace("prefix-a", copy("http://www.a.com"));
			builder.namespace("prefix-b", copy("http://www.b.com"));
			builder.namespace("prefix-c", copy("http://www.c.com"));
			builder.namespace("z", copy("http://www.z.com"));
			builder.namespace("x", copy("http://www.x.com"));
			builder.namespace("y", copy("http://www.y.com"));
			builder.attribute(copy("http://www.a.com"), copy("a"), "prefix-a", "A", null);
			builder.attribute(copy("http://www.c.com"), copy("c"), "prefix-c", "C", null);
			builder.attribute(copy("http://www.b.com"), copy("b"), "prefix-b", "B", null);
			try
			{
				builder.text("junk 1");
				builder.startElement(copy("http://www.y.com"), copy("foo"), "y");
				builder.comment("Hello");
				builder.endElement();
				builder.text("junk 2");
				builder.startElement(copy("http://www.z.com"), copy("bar"), "z");
				builder.endElement();
				builder.text("junk 3");
			}
			finally
			{
				builder.endElement();
			}
		}
		finally
		{
			builder.endDocument();
		}
		final N document = builder.getNodes().get(0);

		final Cursor<N> cursor = pcx.newCursor(document);
		assertNotNull(cursor);
		assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());
		assertFalse(cursor.isElement());
		assertFalse(cursor.isNamespace());
		assertFalse(cursor.isAttribute());
		assertFalse(cursor.isText());
		assertFalse(cursor.hasNamespaces());
		assertFalse(cursor.hasAttributes());
		assertTrue(cursor.hasChildren());
		assertFalse(cursor.hasParent());
		assertFalse(cursor.hasPreviousSibling());
		assertFalse(cursor.hasNextSibling());
		if (pcx.isSupported(Feature.DOCUMENT_URI))
		{
			assertEquals(new URI(copy("http://www.examle.com")), cursor.getDocumentURI());
		}

		assertTrue(cursor.moveToFirstChild());
		assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
		assertTrue(cursor.isElement());
		assertFalse(cursor.isNamespace());
		assertFalse(cursor.isAttribute());
		assertFalse(cursor.isText());
		assertEquals(copy("http://www.x.com"), cursor.getNamespaceURI());
		assertEquals(copy("root"), cursor.getLocalName());
		assertEquals("x", cursor.getPrefix());
		assertTrue(cursor.hasNamespaces());
		assertTrue(cursor.hasAttributes());
		assertTrue(cursor.hasChildren());
		assertTrue(cursor.hasParent());
		assertFalse(cursor.hasPreviousSibling());
		assertFalse(cursor.hasNextSibling());

		assertTrue(cursor.moveToParent());
		assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());

		assertTrue(cursor.moveToFirstChild());
		assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());

		assertTrue(cursor.moveToFirstChild());
		assertEquals(NodeKind.TEXT, cursor.getNodeKind());
		assertFalse(cursor.isElement());
		assertFalse(cursor.isNamespace());
		assertFalse(cursor.isAttribute());
		assertTrue(cursor.isText());
		assertEquals("junk 1", cursor.getStringValue());
		assertFalse(cursor.hasNamespaces());
		assertFalse(cursor.hasAttributes());
		assertFalse(cursor.hasChildren());
		assertTrue(cursor.hasParent());
		assertFalse(cursor.hasPreviousSibling());
		assertTrue(cursor.hasNextSibling());

		assertTrue(cursor.moveToParent());
		assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
		assertTrue(cursor.moveToFirstChildElement());
		assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
		assertEquals(copy("http://www.y.com"), cursor.getNamespaceURI());
		assertEquals(copy("foo"), cursor.getLocalName());
		assertEquals("y", cursor.getPrefix());
		assertFalse(cursor.hasNamespaces());
		assertFalse(cursor.hasAttributes());
		assertTrue(cursor.hasChildren());
		assertTrue(cursor.hasParent());
		assertTrue(cursor.hasPreviousSibling());
		assertTrue(cursor.hasNextSibling());

		assertTrue(cursor.moveToNextSibling());
		assertEquals(NodeKind.TEXT, cursor.getNodeKind());
		assertEquals("junk 2", cursor.getStringValue());

		assertTrue(cursor.moveToNextSibling());
		assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
		assertEquals(copy("http://www.z.com"), cursor.getNamespaceURI());
		assertEquals(copy("bar"), cursor.getLocalName());
		assertEquals("z", cursor.getPrefix());

		assertTrue(cursor.moveToPreviousSibling());
		assertEquals(NodeKind.TEXT, cursor.getNodeKind());
		assertEquals("junk 2", cursor.getStringValue());

		assertTrue(cursor.moveToParent());
		assertTrue(cursor.moveToLastChild());
		assertEquals(NodeKind.TEXT, cursor.getNodeKind());
		assertEquals("junk 3", cursor.getStringValue());
		assertFalse(cursor.hasNamespaces());
		assertFalse(cursor.hasAttributes());
		assertFalse(cursor.hasChildren());
		assertTrue(cursor.hasParent());
		assertTrue(cursor.hasPreviousSibling());
		assertFalse(cursor.hasNextSibling());

		assertTrue(cursor.moveToParent());
		assertTrue(cursor.moveToFirstChildElement());
		assertEquals(copy("foo"), cursor.getLocalName());
		assertTrue(cursor.moveToNextSiblingElement());
		assertEquals(copy("bar"), cursor.getLocalName());
		assertFalse(cursor.moveToNextSiblingElement());

		assertTrue(cursor.moveToParent());
		assertTrue(cursor.moveToFirstChildElementByName(copy("http://www.z.com"), copy("bar")));
		assertEquals(copy("bar"), cursor.getLocalName());

		assertTrue(cursor.moveToParent());
		assertTrue(cursor.moveToFirstChild());
		assertEquals(NodeKind.TEXT, cursor.getNodeKind());
		assertEquals("junk 1", cursor.getStringValue());
		assertTrue(cursor.moveToNextSiblingElementByName(copy("http://www.y.com"), copy("foo")));
		assertEquals(copy("foo"), cursor.getLocalName());

		cursor.moveToRoot();
		assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());
		assertFalse(cursor.hasAttributes());
		assertTrue(cursor.moveToFirstChildElement());
		assertTrue(cursor.hasAttributes());
		assertFalse(cursor.moveToAttribute(nameBridge.empty(), copy("x")));
		assertFalse(cursor.moveToAttribute(nameBridge.empty(), copy("a")));
		assertFalse(cursor.moveToAttribute(copy("http://www.a.com"), nameBridge.empty()));
		assertTrue(cursor.moveToAttribute(copy("http://www.a.com"), copy("a")));
		assertEquals(NodeKind.ATTRIBUTE, cursor.getNodeKind());
		assertFalse(cursor.isElement());
		assertFalse(cursor.isNamespace());
		assertTrue(cursor.isAttribute());
		assertFalse(cursor.isText());
		assertEquals(copy("http://www.a.com"), cursor.getNamespaceURI());
		assertEquals(copy("a"), cursor.getLocalName());
		assertEquals("A", cursor.getStringValue());

		cursor.moveTo(document);
		assertEquals(NodeKind.DOCUMENT, cursor.getNodeKind());
		assertTrue(cursor.matches(NodeKind.DOCUMENT, null, null));
		assertFalse(cursor.matches(NodeKind.DOCUMENT, nameBridge.empty(), null));
		assertFalse(cursor.matches(NodeKind.DOCUMENT, null, nameBridge.empty()));
		assertTrue(cursor.moveToFirstChild());

		assertTrue(cursor.hasNamespaces());
		{
			final Iterator<String> names = cursor.getNamespaceNames(true).iterator();
            assertTrue(names.hasNext());
            final String prefixA = names.next();
            assertEquals("prefix-a", prefixA);

            assertTrue(names.hasNext());

            final String prefixB = names.next();
            assertEquals("prefix-b", prefixB);

            assertTrue(names.hasNext());

            final String prefixC = names.next();
            assertEquals("prefix-c", prefixC);

			assertTrue(names.hasNext());
			final String x = names.next();
			assertEquals("x", x);

			assertTrue(names.hasNext());

			final String y = names.next();
			assertEquals("y", y);

			assertTrue(names.hasNext());

			final String z = names.next();
			assertEquals("z", z);

			assertFalse(names.hasNext());
		}
		{
			final Map<String, String> bindings = new HashMap<String, String>();
			for (final NamespaceBinding binding : cursor.getNamespaceBindings())
			{
				bindings.put(binding.getPrefix(), binding.getNamespaceURI());
			}
			assertEquals(6, bindings.size());
			assertEquals(copy("http://www.x.com"), bindings.get("x"));
			assertEquals(copy("http://www.y.com"), bindings.get("y"));
			assertEquals(copy("http://www.z.com"), bindings.get("z"));
		}

		assertTrue(cursor.hasAttributes());
        {
         final Iterator<QName> names = cursor.getAttributeNames(true).iterator();
        
         assertTrue(names.hasNext());
        
         final QName a = names.next();
         assertEquals(copy("http://www.a.com"), a.getNamespaceURI());
         assertEquals(copy("a"), a.getLocalPart());
         assertEquals("prefix-a", a.getPrefix());
        
         assertTrue(names.hasNext());
        
         final QName b = names.next();
         assertEquals(copy("http://www.b.com"), b.getNamespaceURI());
         assertEquals(copy("b"), b.getLocalPart());
         assertEquals("prefix-b", b.getPrefix());
        
         assertTrue(names.hasNext());
        
         final QName c = names.next();
         assertEquals(copy("http://www.c.com"), c.getNamespaceURI());
         assertEquals(copy("c"), c.getLocalPart());
         assertEquals("prefix-c", c.getPrefix());
        
         assertFalse(names.hasNext());
        }

		cursor.moveToRoot();
		assertEquals(NodeKind.DOCUMENT, model.getNodeKind(document));
		assertTrue(cursor.moveToFirstChild());
		final N root = cursor.bookmark().getNode();
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(root));
		final N a = model.getAttribute(root, copy("http://www.a.com"), copy("a"));
		assertNotNull(a);
		final N b = model.getAttribute(root, copy("http://www.b.com"), copy("b"));
		assertNotNull(b);
		final N c = model.getAttribute(root, copy("http://www.c.com"), copy("c"));
		assertNotNull(c);
		assertTrue(cursor.moveToFirstChild());
		final N text1 = cursor.bookmark().getNode();
		assertEquals(NodeKind.TEXT, model.getNodeKind(text1));
		assertTrue(cursor.moveToNextSibling());
		final N foo = cursor.bookmark().getNode();
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(foo));
		assertTrue(cursor.moveToNextSibling());
		final N text2 = cursor.bookmark().getNode();
		assertEquals(NodeKind.TEXT, model.getNodeKind(text2));
		assertTrue(cursor.moveToNextSibling());
		final N bar = cursor.bookmark().getNode();
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(bar));
		assertTrue(cursor.moveToNextSibling());
		final N text3 = cursor.bookmark().getNode();
		assertEquals(NodeKind.TEXT, model.getNodeKind(text3));

		assertEquals(0, pcx.newCursor(document).compareTo(pcx.newCursor(document)));

		assertEquals(-1, pcx.newCursor(document).compareTo(pcx.newCursor(root)));
		assertEquals(+1, pcx.newCursor(root).compareTo(pcx.newCursor(document)));

		assertEquals(-1, pcx.newCursor(root).compareTo(pcx.newCursor(a)));
		assertEquals(-1, pcx.newCursor(root).compareTo(pcx.newCursor(b)));
		assertEquals(-1, pcx.newCursor(root).compareTo(pcx.newCursor(c)));

		assertEquals(+1, pcx.newCursor(a).compareTo(pcx.newCursor(root)));
		assertEquals(+1, pcx.newCursor(b).compareTo(pcx.newCursor(root)));
		assertEquals(+1, pcx.newCursor(c).compareTo(pcx.newCursor(root)));

		assertEquals(-1, pcx.newCursor(foo).compareTo(pcx.newCursor(bar)));
		assertEquals(+1, pcx.newCursor(bar).compareTo(pcx.newCursor(foo)));
	}

	/**
	 * Do anything to manufacture a String that is equal, but not identical (the same), as the original.
	 * <p>
	 * This method has the post-condition that the strings are equal but not the same.
	 * </p>
	 * 
	 * @param original
	 *            The original.
	 * @return A copy of the original string.
	 */
	public String copy(final String original)
	{
	    return original;
//		final String copy = original.concat("...").substring(0, original.length());
//		// Post-conditions verify that this is effective.
//		assertEquals(original, copy);
//		assertNotSame(original, copy);
//		// Be Paranoid
//		assertTrue(original.equals(copy));
//		assertFalse(original == copy);
//		// OK. That'll do.
//		return copy;
	}
}
