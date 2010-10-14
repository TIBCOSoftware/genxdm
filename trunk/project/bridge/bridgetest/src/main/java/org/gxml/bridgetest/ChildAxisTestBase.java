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
package org.gxml.bridgetest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;

public abstract class ChildAxisTestBase<N> 
    extends GxTestBase<N>
{
	public void test00001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = make00001Document(pcx);

		final Model<N> model = pcx.getModel();

		final N root = model.getFirstChild(D0);
		{
			final Iterable<N> children = model.getChildAxis(root);
			assertNotNull(children);
			final Iterator<N> nodes = children.iterator();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertTrue(nodes.hasNext());
			nodes.next();
			assertFalse(nodes.hasNext());
		}
		{
			final Iterable<N> children = model.getChildElements(root);
			assertNotNull(children);
			final Iterator<N> elements = children.iterator();
			assertTrue(elements.hasNext());
			elements.next();
			assertTrue(elements.hasNext());
			elements.next();
			assertTrue(elements.hasNext());
			elements.next();
			assertFalse(elements.hasNext());
		}
		{
			final Iterable<N> children = model.getChildElementsByName(root, null, null);
			assertNotNull(children);
			final Iterator<N> elements = children.iterator();
			assertTrue(elements.hasNext());
			elements.next();
			assertTrue(elements.hasNext());
			elements.next();
			assertTrue(elements.hasNext());
			elements.next();
			assertFalse(elements.hasNext());
		}
		{
			final Iterable<N> children = model.getChildElementsByName(root, "http://www.a.com", null);
			assertNotNull(children);
			final Iterator<N> elements = children.iterator();
			assertTrue(elements.hasNext());
			elements.next();
			assertFalse(elements.hasNext());
		}
		{
			final Iterable<N> children = model.getChildElementsByName(root, "http://www.b.com", null);
			assertNotNull(children);
			final Iterator<N> elements = children.iterator();
			assertTrue(elements.hasNext());
			elements.next();
			assertTrue(elements.hasNext());
			elements.next();
			assertFalse(elements.hasNext());
		}
		{
			final Iterable<N> children = model.getChildElementsByName(root, null, "a");
			assertNotNull(children);
			final Iterator<N> elements = children.iterator();
			assertTrue(elements.hasNext());
			elements.next();
			assertTrue(elements.hasNext());
			elements.next();
			assertFalse(elements.hasNext());
		}
		{
			final Iterable<N> children = model.getChildElementsByName(root, null, "b");
			assertNotNull(children);
			final Iterator<N> elements = children.iterator();
			assertTrue(elements.hasNext());
			elements.next();
			assertFalse(elements.hasNext());
		}
	}

	public void test00002()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = make00002Document(pcx);

		final Model<N> model = pcx.getModel();

		final N root = model.getFirstChildElement(D0);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(root));

		final N a = model.getFirstChildElement(root);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(a));
		assertEquals("a", model.getLocalName(a));

		final N b = model.getNextSiblingElement(a);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(b));
		assertEquals("b", model.getLocalName(b));

		final N c = model.getNextSiblingElement(b);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(c));
		assertEquals("c", model.getLocalName(c));

		final N d = model.getNextSiblingElement(c);
		assertNull(d);
	}

	public void test00003()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = make00003Document(pcx);

		final Model<N> model = pcx.getModel();

		final N root = model.getFirstChildElement(D0);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(root));

		final N e1 = model.getFirstChildElementByName(root, "http://www.a.com", "a");
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(e1));
		assertEquals("1", model.getStringValue(e1));

		final N e2 = model.getFirstChildElementByName(root, "http://www.b.com", "b");
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(e2));
		assertEquals("2", model.getStringValue(e2));

		final N e3 = model.getFirstChildElementByName(root, "http://www.c.com", "c");
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(e3));
		assertEquals("3", model.getStringValue(e3));

		final N e4 = model.getNextSiblingElementByName(e1, "http://www.a.com", "a");
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(e4));
		assertEquals("4", model.getStringValue(e4));

		final N e5 = model.getNextSiblingElementByName(e2, "http://www.b.com", "b");
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(e5));
		assertEquals("5", model.getStringValue(e5));
	}

	private N make00001Document(final ProcessingContext<N> pcx)
	{
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		final NameSource nameBridge = new NameSource();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);
				try
				{
					builder.text("1");
					builder.startElement("http://www.a.com", "a", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("2");
					}
					finally
					{
						builder.endElement();
					}
					builder.text("3");
					builder.startElement("http://www.b.com", "a", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("4");
					}
					finally
					{
						builder.endElement();
					}
					builder.text("5");
					builder.startElement("http://www.b.com", "b", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("6");
					}
					finally
					{
						builder.endElement();
					}
					builder.text("7");
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
		}
		catch (final GxmlException e)
		{
			throw new AssertionError(e);
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError(e);
		}
		return builder.getNodes().get(0);
	}

	private N make00002Document(final ProcessingContext<N> pcx)
	{
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		final NameSource nameBridge = new NameSource();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);
				try
				{
					builder.text("1");
					builder.startElement("http://www.a.com", "a", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("2");
					}
					finally
					{
						builder.endElement();
					}
					builder.text("3");
					builder.startElement("http://www.b.com", "b", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("4");
					}
					finally
					{
						builder.endElement();
					}
					builder.text("5");
					builder.startElement("http://www.c.com", "c", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("6");
					}
					finally
					{
						builder.endElement();
					}
					builder.text("7");
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
		}
		catch (final GxmlException e)
		{
			throw new AssertionError(e);
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError(e);
		}
        return builder.getNodes().get(0);
	}

	private N make00003Document(final ProcessingContext<N> pcx)
	{
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		final NameSource nameBridge = new NameSource();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);
				try
				{
					builder.startElement("http://www.a.com", "a", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("1");
					}
					finally
					{
						builder.endElement();
					}
					builder.startElement("http://www.b.com", "b", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("2");
					}
					finally
					{
						builder.endElement();
					}
					builder.startElement("http://www.c.com", "c", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("3");
					}
					finally
					{
						builder.endElement();
					}
					builder.startElement("http://www.a.com", "a", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("4");
					}
					finally
					{
						builder.endElement();
					}
					builder.startElement("http://www.b.com", "b", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("5");
					}
					finally
					{
						builder.endElement();
					}
					builder.startElement("http://www.c.com", "c", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.text("6");
					}
					finally
					{
						builder.endElement();
					}
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
		}
		catch (final GxmlException e)
		{
			throw new AssertionError(e);
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError(e);
		}
		return builder.getNodes().get(0);
	}
}