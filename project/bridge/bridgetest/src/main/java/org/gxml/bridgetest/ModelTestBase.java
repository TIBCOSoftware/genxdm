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

import javax.xml.XMLConstants;

import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.names.NameSource;

public abstract class ModelTestBase<N> 
    extends GxTestBase<N>
{
	public void testStringValueOnDocumentNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		builder.startDocument(null, null);
		builder.endDocument();
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testStringValueOnElementNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		final NameSource nameBridge = new NameSource();

		builder.startElement(nameBridge.empty(), "makeHandle", XMLConstants.DEFAULT_NS_PREFIX);
		builder.endElement();
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testStringValueOnAttributeNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		final NameSource nameBridge = new NameSource();

		builder.attribute(nameBridge.empty(), "makeHandle", XMLConstants.NULL_NS_URI, "", null);
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testStringValueOnTextNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.text("");
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testStringValueOnNamespaceNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final NameSource nameBridge = new NameSource();

		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.namespace("x", nameBridge.empty());
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testStringValueOnCommentNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.comment("");
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testStringValueOnProcessingInstructionNode()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.processingInstruction("target", "");
		doNode(builder.getNodes().get(0), pcx);
	}

	public void testAppend()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();

		final N lhs = makeNode(pcx, "a", "b");
		@SuppressWarnings("unused")
		final N l = model.getFirstChild(lhs);

		final N rhs = makeNode(pcx, "c", "d");
		@SuppressWarnings("unused")
		final N c = model.getFirstChild(model.getFirstChild(rhs));
		// final N t3 = model.getFirstChild(c);
		// final N d = model.getNextSibling(c);
		// final N t4 = model.getFirstChild(b);

		// final N result = model.appendChild(l, model.importNode(lhs, c, true));

		// assertEquals("<r><a>A</a><b>B</b><c>C</c></r>", toString(lhs, pcx));
		// assertEquals("<c>C</c>", toString(result, pcx));
	}

	/**
	 * Test the ordering implementation.
	 */
	public void testOrdering()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();

		final N d = makeNode(pcx, "a", "b");
		final N r = model.getFirstChild(d);
		final N a = model.getFirstChild(r);
		final N x = model.getFirstChild(a);
		final N b = model.getNextSibling(a);
		final N y = model.getFirstChild(b);

		assertTrue(model.compare(d, d) == 0);
		assertTrue(model.compare(d, r) < 0);
		assertTrue(model.compare(d, a) < 0);
		assertTrue(model.compare(d, x) < 0);
		assertTrue(model.compare(d, b) < 0);
		assertTrue(model.compare(d, y) < 0);

		assertTrue(model.compare(r, d) > 0);
		assertTrue(model.compare(r, r) == 0);
		assertTrue(model.compare(r, a) < 0);
		assertTrue(model.compare(r, x) < 0);
		assertTrue(model.compare(r, b) < 0);
		assertTrue(model.compare(r, y) < 0);

		assertTrue(model.compare(a, d) > 0);
		assertTrue(model.compare(a, r) > 0);
		assertTrue(model.compare(a, a) == 0);
		assertTrue(model.compare(a, x) < 0);
		assertTrue(model.compare(a, b) < 0);
		assertTrue(model.compare(a, y) < 0);

		assertTrue(model.compare(x, d) > 0);
		assertTrue(model.compare(x, r) > 0);
		assertTrue(model.compare(x, a) > 0);
		assertTrue(model.compare(x, x) == 0);
		assertTrue(model.compare(x, b) < 0);
		assertTrue(model.compare(x, y) < 0);

		assertTrue(model.compare(b, d) > 0);
		assertTrue(model.compare(b, r) > 0);
		assertTrue(model.compare(b, a) > 0);
		assertTrue(model.compare(b, x) > 0);
		assertTrue(model.compare(b, b) == 0);
		assertTrue(model.compare(b, y) < 0);

		assertTrue(model.compare(y, d) > 0);
		assertTrue(model.compare(y, r) > 0);
		assertTrue(model.compare(y, a) > 0);
		assertTrue(model.compare(y, x) > 0);
		assertTrue(model.compare(y, b) > 0);
		assertTrue(model.compare(y, y) == 0);
	}

	/**
	 * Check a bunch of invariants for the node.
	 */
	public void doNode(final N node, final ProcessingContext<N> pcx)
	{
		final Model<N> model = pcx.getModel();

		final String strval = model.getStringValue(node);

		assertNotNull(strval);
		assertEquals("", strval);

	}

	public N makeNode(final ProcessingContext<N> pcx, final String aname, final String bname)
	{
		final NameSource nameBridge = new NameSource();

		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.startDocument(null, null);
		try
		{
			builder.startElement(nameBridge.empty(), "r", XMLConstants.DEFAULT_NS_PREFIX);
			try
			{
				builder.startElement(nameBridge.empty(), aname.toLowerCase(), XMLConstants.DEFAULT_NS_PREFIX);
				try
				{
					builder.text(aname.toUpperCase());
				}
				finally
				{
					builder.endElement();
				}
				builder.startElement(nameBridge.empty(), bname.toLowerCase(), XMLConstants.DEFAULT_NS_PREFIX);
				try
				{
					builder.text(bname.toUpperCase());
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

		final N d = builder.getNodes().get(0);

		assertNotNull(d);

		final N r = model.getFirstChild(d);
		assertNull(model.getPreviousSibling(r));
		assertNull(model.getNextSibling(r));

		assertNotNull(r);

		final N a = model.getFirstChild(r);
		final N x = model.getFirstChild(a);

		assertNotNull(a);
		assertEquals(aname.toUpperCase(), model.getStringValue(a));
		assertEquals(aname.toUpperCase(), model.getStringValue(x));

		final N b = model.getNextSibling(a);
		final N y = model.getFirstChild(b);

		assertNotNull(b);
		assertEquals(bname.toUpperCase(), model.getStringValue(b));
		assertEquals(bname.toUpperCase(), model.getStringValue(y));

		return d;
	}

	/*
	 * public void testHierarchyError() { final GxProcessingContext<N, A> pcx = newProcessingContext();
	 * final GxModel<N, A> model = pcx.getModel();
	 * 
	 * final N documentNode = pcx.newDocument();
	 * 
	 * final N textNode = model.createText(documentNode, "Hello");
	 * 
	 * model.appendChild(documentNode, textNode); }
	 */
}
