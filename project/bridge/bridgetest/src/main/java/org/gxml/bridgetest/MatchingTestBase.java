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

import java.util.List;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.names.NameSource;

public abstract class MatchingTestBase<N> 
    extends GxTestBase<N>
{
	public void test00001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.startDocument(null, null);
		builder.endDocument();

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.DOCUMENT, nodeKind);

		assertTrue(model.matches(node, NodeKind.DOCUMENT, null, null));
		assertFalse(model.matches(node, NodeKind.DOCUMENT, null, "foo"));
		assertFalse(model.matches(node, NodeKind.DOCUMENT, "http://www.x.com", null));
		assertFalse(model.matches(node, NodeKind.DOCUMENT, "http://www.x.com", "foo"));

		assertFalse(model.matches(node, NodeKind.ELEMENT, null, null));
		assertTrue(model.matches(node, null, null, null));
	}

	public void test00002()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.comment("Hello");

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.COMMENT, nodeKind);
		assertEquals("Hello", model.getStringValue(node));

		assertTrue(model.matches(node, NodeKind.COMMENT, null, null));
		assertFalse(model.matches(node, NodeKind.COMMENT, null, "foo"));
		assertFalse(model.matches(node, NodeKind.COMMENT, "http://www.x.com", null));
		assertFalse(model.matches(node, NodeKind.COMMENT, "http://www.x.com", "foo"));

		assertFalse(model.matches(node, NodeKind.ELEMENT, null, null));
		assertTrue(model.matches(node, null, null, null));
	}

	public void test00003()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.text("Hello");

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.TEXT, nodeKind);
		assertEquals("Hello", model.getStringValue(node));

		assertTrue(model.matches(node, NodeKind.TEXT, null, null));
		assertFalse(model.matches(node, NodeKind.TEXT, null, "foo"));
		assertFalse(model.matches(node, NodeKind.TEXT, "http://www.x.com", null));
		assertFalse(model.matches(node, NodeKind.TEXT, "http://www.x.com", "foo"));

		assertFalse(model.matches(node, NodeKind.ELEMENT, null, null));
		assertTrue(model.matches(node, null, null, null));
	}

	public void test00004()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final NameSource nameBridge = new NameSource();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.processingInstruction("target", "data");

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.PROCESSING_INSTRUCTION, nodeKind);
		assertEquals("target", model.getLocalName(node));
		assertEquals("data", model.getStringValue(node));

		assertTrue(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, nameBridge.empty(), "target"));
		assertTrue(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, null, "target"));
		assertTrue(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, nameBridge.empty(), null));
		assertTrue(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, null, null));
		assertFalse(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, null, "foo"));
		assertFalse(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, "http://www.x.com", null));
		assertFalse(model.matches(node, NodeKind.PROCESSING_INSTRUCTION, "http://www.x.com", "foo"));

		assertFalse(model.matches(node, NodeKind.ELEMENT, null, null));
		assertTrue(model.matches(node, null, null, null));
	}

	public void test00005()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final NameSource nameBridge = new NameSource();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.namespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.NAMESPACE, nodeKind);
		assertEquals(XMLConstants.XML_NS_PREFIX, model.getLocalName(node));
		assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(node));

		assertTrue(model.matches(node, NodeKind.NAMESPACE, nameBridge.empty(), XMLConstants.XML_NS_PREFIX));
		assertTrue(model.matches(node, NodeKind.NAMESPACE, null, XMLConstants.XML_NS_PREFIX));
		assertTrue(model.matches(node, NodeKind.NAMESPACE, nameBridge.empty(), null));
		assertTrue(model.matches(node, NodeKind.NAMESPACE, null, null));
		assertFalse(model.matches(node, NodeKind.NAMESPACE, null, "foo"));
		assertFalse(model.matches(node, NodeKind.NAMESPACE, "http://www.x.com", null));
		assertFalse(model.matches(node, NodeKind.NAMESPACE, "http://www.x.com", "foo"));

		assertFalse(model.matches(node, NodeKind.ELEMENT, null, null));
		assertTrue(model.matches(node, null, null, null));
	}

	public void test00006()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final NameSource nameBridge = new NameSource();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.attribute(nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A", null);

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.ATTRIBUTE, nodeKind);
		assertEquals(nameBridge.empty(), model.getNamespaceURI(node));
		assertEquals("a", model.getLocalName(node));
		assertEquals("", model.getPrefix(node));
		assertEquals("A", model.getStringValue(node));

		assertTrue(model.matches(node, NodeKind.ATTRIBUTE, nameBridge.empty(), "a"));
		assertTrue(model.matches(node, NodeKind.ATTRIBUTE, null, "a"));
		assertTrue(model.matches(node, NodeKind.ATTRIBUTE, nameBridge.empty(), null));
		assertTrue(model.matches(node, NodeKind.ATTRIBUTE, null, null));
		assertFalse(model.matches(node, NodeKind.ATTRIBUTE, null, "foo"));
		assertFalse(model.matches(node, NodeKind.ATTRIBUTE, "http://www.x.com", null));
		assertFalse(model.matches(node, NodeKind.ATTRIBUTE, "http://www.x.com", "foo"));

		assertFalse(model.matches(node, NodeKind.ELEMENT, null, null));
		assertTrue(model.matches(node, null, null, null));
	}

	public void test00007()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.startElement("http://www.x.com", "foo", "x");
		builder.endElement();

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.ELEMENT, nodeKind);
		assertEquals("http://www.x.com", model.getNamespaceURI(node));
		assertEquals("foo", model.getLocalName(node));
		assertEquals("x", model.getPrefix(node));
		assertEquals("", model.getStringValue(node));

		assertTrue(model.matches(node, NodeKind.ELEMENT, "http://www.x.com", "foo"));
		assertFalse(model.matches(node, NodeKind.TEXT, "http://www.x.com", "foo"));
		assertTrue(model.matches(node, null, "http://www.x.com", "foo"));
		assertFalse(model.matches(node, NodeKind.ELEMENT, "http://www.y.com", "foo"));
		assertTrue(model.matches(node, NodeKind.ELEMENT, null, "foo"));
		assertFalse(model.matches(node, NodeKind.ELEMENT, "http://www.x.com", "bar"));
		assertTrue(model.matches(node, NodeKind.ELEMENT, "http://www.x.com", null));
	}
}