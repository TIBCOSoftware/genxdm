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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.Feature;
import org.genxdm.NodeKind;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;

public abstract class AttributeAxisTestBase<N> 
    extends GxTestBase<N>
{
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
					builder.namespace("w", "http://www.w.com");
					builder.attribute(XMLConstants.XML_NS_URI, "space", XMLConstants.XML_NS_PREFIX, "preserve", null);
					builder.attribute(nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A", null);
					builder.startElement(nameBridge.empty(), "x", XMLConstants.DEFAULT_NS_PREFIX);
					try
					{
						builder.namespace("r", "http://www.r.com");
						builder.attribute(nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "B", null);
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
					builder.attribute(nameBridge.empty(), "c", XMLConstants.DEFAULT_NS_PREFIX, "X", null);
					builder.attribute(nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "Y", null);
					builder.attribute(nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "Z", null);
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

	private Map<QName, N> mapAttributeBindings(final Iterable<N> attributes, final Model<N> model, final NameSource nameBridge)
	{
		final Iterator<N> bindings = attributes.iterator();
		if (bindings.hasNext())
		{
			final Map<QName, N> map = new HashMap<QName, N>();
			while (bindings.hasNext())
			{
				final N binding = bindings.next();
				map.put(new QName(model.getNamespaceURI(binding), model.getLocalName(binding), model.getPrefix(binding)), binding);
			}
			return map;
		}
		else
		{
			return Collections.emptyMap();
		}
	}

	public void test00001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = make00001Document(pcx);

//		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		final NameSource nameBridge = new NameSource();

		final Model<N> model = pcx.getModel();

		final N wrap = model.getFirstChild(D0);
		final N real = model.getFirstChild(wrap);

		if (pcx.isSupported(Feature.ATTRIBUTE_AXIS_INHERIT))
		{
			final Map<QName, N> attributes = mapAttributeBindings(model.getAttributeAxis(real, true), model, nameBridge);
			assertEquals(2, attributes.size());
			final QName SPACE = new QName(XMLConstants.XML_NS_URI, "space", XMLConstants.XML_NS_PREFIX);
			final QName B = new QName("b");
			assertTrue(attributes.containsKey(SPACE));
			assertTrue(attributes.containsKey(B));

			// This namespace node is probably the same one that actually exists on the tree.
			{
				final N b = attributes.get(B);

				assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(b));

				assertEquals(nameBridge.empty(), model.getNamespaceURI(b));
				assertEquals(XMLConstants.NULL_NS_URI, model.getNamespaceURI(b));

				assertEquals("b", model.getLocalName(b));

				assertEquals(XMLConstants.DEFAULT_NS_PREFIX, model.getPrefix(b));

				assertEquals("B", model.getStringValue(b));

				assertTrue(Ordering.isSameNode(real, model.getParent(b), model));
			}
			// This attribute is a wrapped node to make the parent appear to be correct.
			{
				final N space = attributes.get(SPACE);

				assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(space));

				assertEquals(XMLConstants.XML_NS_URI, model.getNamespaceURI(space));

				assertEquals("space", model.getLocalName(space));

				assertEquals(XMLConstants.XML_NS_PREFIX, model.getPrefix(space));

				assertEquals("preserve", model.getStringValue(space));
				assertTrue(Ordering.isSameNode(real, model.getParent(space), model));

			}

			for (final N attribute : model.getAttributeAxis(real, true))
			{
				assertNull(model.getAttribute(attribute, nameBridge.empty(), "a"));
				assertFalse(model.getAttributeAxis(attribute, true).iterator().hasNext());
				assertNull(model.getFirstChild(attribute));
				assertNull(model.getFirstChildElement(attribute));
				assertNull(model.getFirstChildElementByName(attribute, null, null));
				assertNull(model.getLastChild(attribute));
				assertFalse(model.getNamespaceAxis(attribute, false).iterator().hasNext());
				assertFalse(model.getNamespaceBindings(attribute).iterator().hasNext());
				assertNull(model.getNextSibling(attribute));
				assertNull(model.getNextSiblingElement(attribute));
				assertNull(model.getNextSiblingElementByName(attribute, null, null));
				assertNull(model.getPreviousSibling(attribute));
//				assertEquals(model.getStringValue(attribute), atomBridge.getC14NString(model.getValue(attribute)));
				assertFalse(model.hasAttributes(attribute));
				assertFalse(model.hasChildren(attribute));
				assertFalse(model.hasNamespaces(attribute));
				assertTrue(model.hasParent(attribute));
				assertFalse(model.hasNextSibling(attribute));
				assertFalse(model.hasPreviousSibling(attribute));
				assertFalse(model.getAttributeNames(attribute, true).iterator().hasNext());
				assertFalse(model.getNamespaceNames(attribute, true).iterator().hasNext());
				assertTrue(Ordering.isSameNode(D0, model.getRoot(attribute), model));
			}
			{
				final N r = attributes.get(B);
				final N x = attributes.get(SPACE);

				assertTrue(Ordering.isSameNode(r, r, model));
				assertFalse(Ordering.isSameNode(r, x, model));

				assertFalse(Ordering.isSameNode(x, r, model));
				assertTrue(Ordering.isSameNode(x, x, model));

				final Object RID = model.getNodeId(r);
				final Object XID = model.getNodeId(x);

				assertEquals(RID, RID);
				assertFalse(RID.equals(XID));

				assertFalse(XID.equals(RID));
				assertEquals(XID, XID);

				// assertEquals(0, model.compare(r, r));
				// assertEquals(+1, model.compare(r, w));
				// assertEquals(-1, model.compare(w, r));
				// assertEquals(0, model.compare(r, x));
			}
		}
	}

	public void test00002()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = make00002Document(pcx);

		final Model<N> model = pcx.getModel();

		final N root = model.getFirstChild(D0);
		final Iterator<QName> names = model.getAttributeNames(root, true).iterator();
		assertTrue(names.hasNext());
		final QName a = names.next();
		assertEquals("a", a.getLocalPart());
		assertTrue(names.hasNext());
		final QName b = names.next();
		assertEquals("b", b.getLocalPart());
		assertTrue(names.hasNext());
		final QName c = names.next();
		assertEquals("c", c.getLocalPart());
		assertFalse(names.hasNext());
	}
	
}