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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;
import org.gxml.bridgekit.tree.Ordering;

public abstract class NamespaceAxisTestBase<N> 
    extends GxTestBase<N>
{
	public void test00001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = make00001Document(pcx);

		final NameSource nameBridge = new NameSource();

		final Model<N> model = pcx.getModel();

		final N wrap = model.getFirstChild(D0);
		final N real = model.getFirstChild(wrap);

		final Map<String, N> namespaces = mapNamespaceBindings(model.getNamespaceAxis(real, true), model);
		assertEquals(3, namespaces.size());
		assertTrue(namespaces.containsKey("xml"));
		assertTrue(namespaces.containsKey("w"));
		assertTrue(namespaces.containsKey("r"));

		// This namespace node is probably the same one that actually exists on the tree.
		{
			final N namespace = namespaces.get("r");

			assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
			assertEquals("r", model.getLocalName(namespace));
			assertEquals("http://www.r.com", model.getStringValue(namespace));
			assertTrue(Ordering.isSameNode(real, model.getParent(namespace), model));
		}
		// The XML namespace node is a fake namespace node.
		{
			final N namespace = namespaces.get("xml");

			assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
			assertEquals("xml", model.getLocalName(namespace));
			assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(namespace));
//			assertTrue(model.isSameNode(real, model.getParent(namespace)));
		}
		// This namespace is a wrapped node to make the parent appear to be correct.
		{
			final N namespace = namespaces.get("w");

			assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
			assertEquals("w", model.getLocalName(namespace));
			assertEquals("http://www.w.com", model.getStringValue(namespace));
			assertTrue(Ordering.isSameNode(real, model.getParent(namespace), model));

		}

		for (final N namespace : model.getNamespaceAxis(real, true))
		{
			assertNull(model.getAttribute(namespace, nameBridge.empty(), "a"));
			assertFalse(model.getAttributeAxis(namespace, true).iterator().hasNext());
			assertNull(model.getFirstChild(namespace));
			assertNull(model.getFirstChildElement(namespace));
			assertNull(model.getFirstChildElementByName(namespace, null, null));
			assertNull(model.getLastChild(namespace));
			assertFalse(model.getNamespaceAxis(namespace, false).iterator().hasNext());
			assertFalse(model.getNamespaceBindings(namespace).iterator().hasNext());
			assertEquals(XMLConstants.NULL_NS_URI, model.getNamespaceURI(namespace));
			assertNull(model.getNextSibling(namespace));
			assertNull(model.getNextSiblingElement(namespace));
			assertNull(model.getNextSiblingElementByName(namespace, null, null));
			assertEquals(XMLConstants.DEFAULT_NS_PREFIX, model.getPrefix(namespace));
			assertNull(model.getPreviousSibling(namespace));
			assertFalse(model.hasAttributes(namespace));
			assertFalse(model.hasChildren(namespace));
			assertFalse(model.hasNamespaces(namespace));
			assertFalse(model.hasNextSibling(namespace));
			assertFalse(model.hasPreviousSibling(namespace));
			assertTrue(model.hasParent(namespace));
			assertFalse(model.getAttributeNames(namespace, true).iterator().hasNext());
			assertFalse(model.getNamespaceNames(namespace, true).iterator().hasNext());
			assertTrue(Ordering.isSameNode(D0, model.getRoot(namespace), model));
		}
		{
			final N r = namespaces.get("r");
			final N w = namespaces.get("w");
			final N x = namespaces.get("xml");

			assertTrue(Ordering.isSameNode(r, r, model));
			assertFalse(Ordering.isSameNode(r, w, model));
			assertFalse(Ordering.isSameNode(r, x, model));

			assertFalse(Ordering.isSameNode(w, r, model));
			assertTrue(Ordering.isSameNode(w, w, model));
			assertFalse(Ordering.isSameNode(w, x, model));

			assertFalse(Ordering.isSameNode(x, r, model));
			assertFalse(Ordering.isSameNode(x, w, model));
			assertTrue(Ordering.isSameNode(x, x, model));

			final Object RID = model.getNodeId(r);
			final Object WID = model.getNodeId(w);
			final Object XID = model.getNodeId(x);

			assertEquals(RID, RID);
			assertFalse(RID.equals(WID));
			assertFalse(RID.equals(XID));

			assertFalse(WID.equals(RID));
			assertEquals(WID, WID);
			assertFalse(WID.equals(XID));

			assertFalse(XID.equals(RID));
			assertFalse(XID.equals(WID));
			assertEquals(XID, XID);

			// assertEquals(0, model.compare(r, r));
			// assertEquals(+1, model.compare(r, w));
			// assertEquals(-1, model.compare(w, r));
			// assertEquals(0, model.compare(r, x));
		}
	}

	private N make00001Document(final ProcessingContext<N> pcx)
	{
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement("http://www.w.com", "wrap", "w");
				try
				{
					builder.namespace("w", "http://www.w.com");
					builder.startElement("http://www.r.com", "real", "r");
					try
					{
						builder.namespace("r", "http://www.r.com");
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

	private Map<String, N> mapNamespaceBindings(final Iterable<N> namespaces, final Model<N> model)
	{
		final Iterator<N> bindings = namespaces.iterator();
		if (bindings.hasNext())
		{
			final Map<String, N> map = new HashMap<String, N>();
			while (bindings.hasNext())
			{
                final N binding = bindings.next();
				map.put(model.getLocalName(binding), binding);
			}
			return map;
		}
		else
		{
			return Collections.emptyMap();
		}
	}
}