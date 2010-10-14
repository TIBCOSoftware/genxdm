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
package org.gxml.bridgetest.typed;

import java.io.PrintStream;
import java.math.BigInteger;
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
import org.genxdm.base.ProcessingContext;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.gxml.bridgekit.tree.Ordering;
import org.gxml.bridgetest.Axis;
import org.gxml.bridgetest.GxTestBase;
import org.xml.sax.SAXException;

public abstract class BridgeTestBase<N, A> 
    extends GxTestBase<N>
{
	private void assertCompare(final int expect, final N lhs, final N rhs, final TypedModel<N, A> model)
	{
		assertEquals(expect, model.compare(lhs, rhs));
	}

	private void assertEquals(final Axis axis, final String expected, final Iterable<? extends N> nodes, final TypedModel<N, A> model, AtomBridge<A> atomBridge)
	{
		final String actual = getNames(axis, nodes, model);

		if (!expected.equals(actual))
		{
			System.out.println("axis  =" + axis);
			System.out.println("expect=" + expected);
			System.out.println("actual=" + actual);
		}

		if (null != axis)
		{
			assertEquals(axis.toString(), expected, actual);
		}
		else
		{
			assertEquals("", expected, actual);
		}
	}

	private void assertEquals(final Axis message, final String expected, final N node, final TypedModel<N, A> model, final AtomBridge<A> atomBridge)
	{
		final String actual = getMoniker(node, model);

		if (!expected.equals(actual))
		{
			System.out.println("axis  =" + message);
			System.out.println("expect=" + expected);
			System.out.println("actual=" + actual);
		}

		assertEquals(message.toString(), expected, actual);
	}

	/**
	 * dm:typed-value assertion using a display string.
	 */
	private void assertEquals(final String message, final String expected, final N node, final TypedModel<N, A> model, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final StringBuilder sb = new StringBuilder();
		final Iterator<? extends A> atoms = model.getValue(node).iterator();
		if (atoms.hasNext())
		{
			sb.append(getXQueryString(atoms.next(), atomBridge));
			while (atoms.hasNext())
			{
				sb.append(",");
				sb.append(getXQueryString(atoms.next(), atomBridge));
			}
		}

		assertEquals(message, expected, sb.toString());
	}

	private void assertNameEq(final String message, final QName expected, final N node, final TypedModel<N, A> model, final TypedContext<N, A> pcx)
	{
		final String localName = model.getLocalName(node);

		final boolean match;
		final QName actual;
		if (null != localName)
		{
			actual = new QName(model.getNamespaceURI(node), localName, model.getPrefix(node));
			match = expected.equals(actual);
		}
		else
		{
			actual = null;
			match = (expected == null);
		}

		if (!match)
		{
			final PrintStream consoleErr = System.err;
			consoleErr.println("namespace-uri -from- QName(expect)=" + expected.getNamespaceURI());
			consoleErr.println("local-name    -from- QName(expect)=" + expected.getLocalPart());
			consoleErr.println("prefix        -from- QName(expect)=" + expected.getPrefix());
			if (null != actual)
			{
				consoleErr.println("namespace-uri -from- QName(actual)=" + actual.getNamespaceURI());
				consoleErr.println("local-name    -from- QName(actual)=" + actual.getLocalPart());
				consoleErr.println("prefix        -from- QName(actual)=" + actual.getPrefix());
			}
		}

		assertEquals(message, true, match);
	}

	public void doAttributeCreate(final QName qname)
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final String content = "Loop Quantum Gravity";

		final A data = atomBridge.createString(content);

		builder.attribute(qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), atomBridge.wrapAtom(data), null);
		final N node = builder.getNode();
		
		assertNotNull("Did not create an attribute", node);

		assertEquals("dm:node-kind", NodeKind.ATTRIBUTE, model.getNodeKind(node));
		assertNameEq("dm:node-name", qname, node, model, pcx);
		assertEquals("dm:string-value", content, model.getStringValue(node));
		// assertEquals("dm:typed-value", "xs:string('" + content + "')", node, model);
	}

	public void doElementCreate(final N owner, final QName qname)
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		builder.startElement(qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), null);
		builder.endElement();
		final N node = builder.getNodes().get(0);

		assertEquals("dm:node-kind", NodeKind.ELEMENT, model.getNodeKind(node));
		assertNameEq("dm:node-name", qname, node, model, pcx);
		assertEquals("dm:string-value", "", model.getStringValue(node));

		// DOM returns xs:string('')
		// assertEquals("dm:typed-value", "", uuid, model);
	}

	private String getXQueryString(final A atom, final AtomBridge<A> atomBridge)
	{
		final StringBuilder sb = new StringBuilder();
		final QName dataType = atomBridge.getDataType(atom);
		if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(dataType.getNamespaceURI()))
		{
			sb.append("xs");
		}
		else
		{
			sb.append(dataType.getNamespaceURI());
		}
		sb.append(":");
		sb.append(dataType.getLocalPart());
		sb.append("('");
		sb.append(atomBridge.getC14NForm(atom));
		sb.append("')");
		return sb.toString();
	}

	private N make0010Document(final TypedContext<N, A> pcx)
	{
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{

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

	private N make0020Document(final TypedContext<N, A> pcx)
	{
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{
					builder.namespace("x", "http://www.x.com");
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

	private N make0030Document(final TypedContext<N, A> pcx)
	{
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{
					builder.namespace("x", "http://www.x.com");
					builder.startElement(nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, null);
					try
					{
						builder.namespace("p", "http://www.p.com");
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

	private N make0040Document(final TypedContext<N, A> pcx)
	{
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{
					builder.namespace("x", "http://www.x.com");
					builder.startElement(nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, null);
					try
					{
						builder.namespace("x", "");
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

	/**
	 * A1 xmlns=N4 xmlns:P4=N4 xmlns:P3=N3 X1=V1 X2=V2 X3=V3 {N4}X4=V4 B1 C1 C2 B2 C3 D1 C4 X5=V5 D2 D3 D4
	 */
	private N makeAxisDocument(final TypedContext<N, A> pcx)
	{
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();

		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		builder.startDocument(null, null);
		try
		{
			builder.startElement(nameBridge.empty(), "A1", XMLConstants.DEFAULT_NS_PREFIX, null);
			try
			{
				builder.namespace("", "N4");
				builder.namespace("P4", "N4");
				builder.namespace("P3", "N3");
				builder.attribute(nameBridge.empty(), "X1", XMLConstants.DEFAULT_NS_PREFIX, "V1", null);
				builder.attribute(nameBridge.empty(), "X2", XMLConstants.DEFAULT_NS_PREFIX, "V2", null);
				builder.attribute(nameBridge.empty(), "X3", XMLConstants.DEFAULT_NS_PREFIX, "V3", null);
				builder.attribute("N4", "X4", XMLConstants.DEFAULT_NS_PREFIX, "V4", null);
				builder.startElement(nameBridge.empty(), "B1", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{
					builder.startElement(nameBridge.empty(), "C1", XMLConstants.DEFAULT_NS_PREFIX, null);
					builder.endElement();
					builder.startElement(nameBridge.empty(), "C2", XMLConstants.DEFAULT_NS_PREFIX, null);
					builder.endElement();
				}
				finally
				{
					builder.endElement();
				}
				builder.startElement(nameBridge.empty(), "B2", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{
					builder.startElement(nameBridge.empty(), "C3", XMLConstants.DEFAULT_NS_PREFIX, null);
					try
					{
						builder.startElement(nameBridge.empty(), "D1", XMLConstants.DEFAULT_NS_PREFIX, null);
						builder.endElement();
					}
					finally
					{
						builder.endElement();
					}
					builder.startElement(nameBridge.empty(), "C4", XMLConstants.DEFAULT_NS_PREFIX, null);
					try
					{
						builder.attribute(nameBridge.empty(), "X5", XMLConstants.DEFAULT_NS_PREFIX, "V5", null);
						builder.startElement(nameBridge.empty(), "D2", XMLConstants.DEFAULT_NS_PREFIX, null);
						builder.endElement();
						builder.startElement(nameBridge.empty(), "D3", XMLConstants.DEFAULT_NS_PREFIX, null);
						builder.endElement();
						builder.startElement(nameBridge.empty(), "D4", XMLConstants.DEFAULT_NS_PREFIX, null);
						builder.endElement();
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
				builder.startElement(nameBridge.empty(), "B3", XMLConstants.DEFAULT_NS_PREFIX, null);
				try
				{
					builder.startElement(nameBridge.empty(), "C5", XMLConstants.DEFAULT_NS_PREFIX, null);
					builder.endElement();
					builder.startElement(nameBridge.empty(), "C6", XMLConstants.DEFAULT_NS_PREFIX, null);
					builder.endElement();
				}
				finally
				{
					builder.endElement();
				}
				builder.startElement(nameBridge.empty(), "B4", XMLConstants.DEFAULT_NS_PREFIX, null);
				builder.endElement();
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

		return builder.getNodes().get(0);
	}

	private N makeFibonacci(final int n, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
        final NameSource nameBridge = atomBridge.getNameBridge();

		BigInteger low = BigInteger.ONE;
		BigInteger high = BigInteger.ONE;

		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		builder.startDocument(null, null);
		try
		{
			builder.startElement(nameBridge.empty(), "root", "", null);
			try
			{
				builder.namespace("x", "http://www.fibonacci.org");
				builder.attribute(nameBridge.empty(), "n", XMLConstants.DEFAULT_NS_PREFIX, atomBridge.wrapAtom(atomBridge.createInt(n)), null);
				for (int i = 1; i <= n; i++)
				{
					builder.startElement(nameBridge.empty(), "fibonacci", "", null);
					try
					{
						builder.text(atomBridge.wrapAtom(atomBridge.createInteger(low)));
					}
					finally
					{
						builder.endElement();
					}

					final BigInteger temp = high;
					high = high.add(low);
					low = temp;
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

		return builder.getNodes().get(0);
	}

	private N makeTrivialDocument(final TypedContext<N, A> pcx)
	{
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();
		try
		{
			builder.startDocument(new URI("http://www.trivial.com"), null);
		}
		catch (final GxmlException e)
		{
			throw new AssertionError(e);
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError(e);
		}
		builder.endDocument();
		return builder.getNodes().get(0);
	}

	public void testAccess()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();

		final N document = makeAxisDocument(pcx);

		// System.out.println(serialize(document));

		final TypedModel<N, A> model = pcx.getModel();

		final N A1 = getChildAt(document, model, 0);
		final N B1 = getChildAt(A1, model, 0);
		// final N B2 = getChildAt(A1, model, 1);
		// final N B3 = getChildAt(A1, model, 2);
		// final N B4 = getChildAt(A1, model, 3);
		// final N C1 = getChildAt(B1, model, 0);
		// final N C2 = getChildAt(B1, model, 1);
		// final N C3 = getChildAt(B2, model, 0);
		// final N C4 = getChildAt(B2, model, 1);
		// final N C5 = getChildAt(B3, model, 0);
		// final N C6 = getChildAt(B3, model, 1);
		// final N D1 = getChildAt(C3, model, 0);
		// final N D2 = getChildAt(C4, model, 0);
		// final N D3 = getChildAt(C4, model, 1);
		// final N D4 = getChildAt(C4, model, 2);

		final N X1 = getAttributeAt(A1, model, 0);
		final N X2 = getAttributeAt(A1, model, 1);
		final N X3 = getAttributeAt(A1, model, 2);
		final N X4 = getAttributeAt(A1, model, 3);

		assertTrue(Ordering.isSameNode(B1, model.getFirstChildElement(A1), model));

		assertNotNull(model.getAttribute(A1, nameBridge.empty(), "X1"));
		assertTrue(Ordering.isSameNode(X1, model.getAttribute(A1, nameBridge.empty(), "X1"), model));

		assertNotNull(model.getAttribute(A1, nameBridge.empty(), "X2"));
		assertTrue(Ordering.isSameNode(X2, model.getAttribute(A1, nameBridge.empty(), "X2"), model));

		assertNotNull(model.getAttribute(A1, nameBridge.empty(), "X3"));
		assertTrue(Ordering.isSameNode(X3, model.getAttribute(A1, nameBridge.empty(), "X3"), model));

		assertNotNull(model.getAttribute(A1, "N4", "X4"));
		assertTrue(Ordering.isSameNode(X4, model.getAttribute(A1, "N4", "X4"), model));

		assertNull(model.getAttribute(A1, "WILD", "X1"));
		assertNull(model.getAttribute(A1, "WILD", "X2"));
		assertNull(model.getAttribute(A1, "WILD", "X3"));
		assertNull(model.getAttribute(A1, nameBridge.empty(), "X4"));
		assertNull(model.getAttribute(A1, "WILD", "X4"));

		assertNull(model.getAttribute(A1, nameBridge.empty(), "X5"));

		try
		{
			model.getAttribute(A1, null, "X1");

			fail();
		}
		catch (final Throwable e)
		{
			// Correct, don't allow null for namespaceURI.
		}
	}

	public void testAtomCast()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N node = makeTrivialDocument(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A atom = atomBridge.createString("Hello");

		assertEquals(atom, pcx.atom(atom));
		assertEquals(null, pcx.atom(node));
		assertNull(ctx.node(null));
	}

	public void testAttributeCreate()
	{
		doAttributeCreate(new QName("swim"));
		doAttributeCreate(new QName("http://www.millstriders.org", "bike", XMLConstants.DEFAULT_NS_PREFIX));

		/*
		 * if (RX_BRIDGE.equals(getBridge().getClass())) { doAttributeCreate(new QName("http://www.millstriders.org", "run")); } else if (XI_BRIDGE.equals(getBridge().getClass())) { // ExpandedName in XMLSDK doesn't retain prefix hints. doAttributeCreate(new
		 * QName("http://www.millstriders.org", "run")); } else { doAttributeCreate(new QName("http://www.millstriders.org", "run", "m")); }
		 */
	}

	public void testAxes()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final N D0 = makeAxisDocument(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final TypedModel<N, A> model = pcx.getModel();

		final N A1 = getChildAt(D0, model, 0);
		final N B1 = getChildAt(A1, model, 0);
		final N B2 = getChildAt(A1, model, 1);
		final N B3 = getChildAt(A1, model, 2);
		final N B4 = getChildAt(A1, model, 3);
		final N C1 = getChildAt(B1, model, 0);
		final N C2 = getChildAt(B1, model, 1);
		final N C3 = getChildAt(B2, model, 0);
		final N C4 = getChildAt(B2, model, 1);
		final N C5 = getChildAt(B3, model, 0);
		final N C6 = getChildAt(B3, model, 1);
		final N D1 = getChildAt(C3, model, 0);
		final N D2 = getChildAt(C4, model, 0);
		final N D3 = getChildAt(C4, model, 1);
		final N D4 = getChildAt(C4, model, 2);

		final N X1 = getAttributeAt(A1, model, 0);
		final N X2 = getAttributeAt(A1, model, 1);
		final N X3 = getAttributeAt(A1, model, 2);
		final N X5 = getAttributeAt(C4, model, 0);

		// final N N1 = getNamespaceAt(A1, model, 0);
		// final N N2 = getNamespaceAt(A1, model, 1);
		// final N N3 = getNamespaceAt(A1, model, 2);

		// System.out.println(model.getFirstName(A1));
		// System.out.println(model.getFirstName(B2));
		// System.out.println(model.getFirstName(C4));

		assertEquals(Axis.ANCESTOR, "", model.getAncestorAxis(D0), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "document", model.getAncestorAxis(A1), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(B1), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(B2), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(B3), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(B4), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(B1),element(A1),document", model.getAncestorAxis(C1), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(B1),element(A1),document", model.getAncestorAxis(C2), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(B2),element(A1),document", model.getAncestorAxis(C3), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(B2),element(A1),document", model.getAncestorAxis(C4), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(B3),element(A1),document", model.getAncestorAxis(C5), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(B3),element(A1),document", model.getAncestorAxis(C6), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(C3),element(B2),element(A1),document", model.getAncestorAxis(D1), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(C4),element(B2),element(A1),document", model.getAncestorAxis(D2), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(C4),element(B2),element(A1),document", model.getAncestorAxis(D3), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(C4),element(B2),element(A1),document", model.getAncestorAxis(D4), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(X1), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(X2), model, atomBridge);
		assertEquals(Axis.ANCESTOR, "element(A1),document", model.getAncestorAxis(X3), model, atomBridge);

		assertEquals(Axis.ANCESTOR_OR_SELF, "document", model.getAncestorOrSelfAxis(D0), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(A1),document", model.getAncestorOrSelfAxis(A1), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(B1),element(A1),document", model.getAncestorOrSelfAxis(B1), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(B2),element(A1),document", model.getAncestorOrSelfAxis(B2), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(B3),element(A1),document", model.getAncestorOrSelfAxis(B3), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(B4),element(A1),document", model.getAncestorOrSelfAxis(B4), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(C1),element(B1),element(A1),document", model.getAncestorOrSelfAxis(C1), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(C2),element(B1),element(A1),document", model.getAncestorOrSelfAxis(C2), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(C3),element(B2),element(A1),document", model.getAncestorOrSelfAxis(C3), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(C4),element(B2),element(A1),document", model.getAncestorOrSelfAxis(C4), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(C5),element(B3),element(A1),document", model.getAncestorOrSelfAxis(C5), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(C6),element(B3),element(A1),document", model.getAncestorOrSelfAxis(C6), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(D1),element(C3),element(B2),element(A1),document", model.getAncestorOrSelfAxis(D1), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(D2),element(C4),element(B2),element(A1),document", model.getAncestorOrSelfAxis(D2), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(D3),element(C4),element(B2),element(A1),document", model.getAncestorOrSelfAxis(D3), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "element(D4),element(C4),element(B2),element(A1),document", model.getAncestorOrSelfAxis(D4), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "attribute(X1),element(A1),document", model.getAncestorOrSelfAxis(X1), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "attribute(X2),element(A1),document", model.getAncestorOrSelfAxis(X2), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "attribute(X3),element(A1),document", model.getAncestorOrSelfAxis(X3), model, atomBridge);

		assertEquals(Axis.CHILD, "element(A1)", model.getChildAxis(D0), model, atomBridge);
		assertEquals(Axis.CHILD, "element(B1),element(B2),element(B3),element(B4)", model.getChildAxis(A1), model, atomBridge);
		assertEquals(Axis.CHILD, "element(C1),element(C2)", model.getChildAxis(B1), model, atomBridge);
		assertEquals(Axis.CHILD, "element(C3),element(C4)", model.getChildAxis(B2), model, atomBridge);
		assertEquals(Axis.CHILD, "element(C5),element(C6)", model.getChildAxis(B3), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(B4), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(C1), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(C2), model, atomBridge);
		assertEquals(Axis.CHILD, "element(D1)", model.getChildAxis(C3), model, atomBridge);
		assertEquals(Axis.CHILD, "element(D2),element(D3),element(D4)", model.getChildAxis(C4), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(C5), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(C6), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(D1), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(D2), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(D3), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(D4), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(X1), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(X2), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(X3), model, atomBridge);

		assertEquals(Axis.DESCENDANT, "element(A1),element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getDescendantAxis(D0), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getDescendantAxis(A1), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "element(C1),element(C2)", model.getDescendantAxis(B1), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "element(C3),element(D1),element(C4),element(D2),element(D3),element(D4)", model.getDescendantAxis(B2), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "element(C5),element(C6)", model.getDescendantAxis(B3), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(B4), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(C1), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(C2), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "element(D1)", model.getDescendantAxis(C3), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "element(D2),element(D3),element(D4)", model.getDescendantAxis(C4), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(C5), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(C6), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(D1), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(D2), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(D3), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(D4), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(X1), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(X2), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(X3), model, atomBridge);

		assertEquals(Axis.DESCENDANT_OR_SELF, "document,element(A1),element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model
				.getDescendantOrSelfAxis(D0), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(A1),element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getDescendantOrSelfAxis(A1),
				model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(B1),element(C1),element(C2)", model.getDescendantOrSelfAxis(B1), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4)", model.getDescendantOrSelfAxis(B2), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(B3),element(C5),element(C6)", model.getDescendantOrSelfAxis(B3), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(B4)", model.getDescendantOrSelfAxis(B4), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(C1)", model.getDescendantOrSelfAxis(C1), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(C2)", model.getDescendantOrSelfAxis(C2), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(C3),element(D1)", model.getDescendantOrSelfAxis(C3), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(C4),element(D2),element(D3),element(D4)", model.getDescendantOrSelfAxis(C4), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(C5)", model.getDescendantOrSelfAxis(C5), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(C6)", model.getDescendantOrSelfAxis(C6), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(D1)", model.getDescendantOrSelfAxis(D1), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(D2)", model.getDescendantOrSelfAxis(D2), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(D3)", model.getDescendantOrSelfAxis(D3), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "element(D4)", model.getDescendantOrSelfAxis(D4), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "attribute(X1)", model.getDescendantOrSelfAxis(X1), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "attribute(X2)", model.getDescendantOrSelfAxis(X2), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "attribute(X3)", model.getDescendantOrSelfAxis(X3), model, atomBridge);

		assertEquals(Axis.FOLLOWING, "", model.getFollowingAxis(D0), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "", model.getFollowingAxis(A1), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(B1), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(B2), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B4)", model.getFollowingAxis(B3), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "", model.getFollowingAxis(B4), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(C1), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(C2), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(C3), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(C4), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(C6),element(B4)", model.getFollowingAxis(C5), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B4)", model.getFollowingAxis(C6), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(D1), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(D2), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(D3), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(D4), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(X1), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(X2), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(B1),element(C1),element(C2),element(B2),element(C3),element(D1),element(C4),element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(X3), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "element(D2),element(D3),element(D4),element(B3),element(C5),element(C6),element(B4)", model.getFollowingAxis(X5), model, atomBridge);

		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(D0), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(A1), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(B2),element(B3),element(B4)", model.getFollowingSiblingAxis(B1), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(B3),element(B4)", model.getFollowingSiblingAxis(B2), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(B4)", model.getFollowingSiblingAxis(B3), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(B4), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(C2)", model.getFollowingSiblingAxis(C1), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(C2), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(C4)", model.getFollowingSiblingAxis(C3), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(C4), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(C6)", model.getFollowingSiblingAxis(C5), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(C6), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(D1), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(D3),element(D4)", model.getFollowingSiblingAxis(D2), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "element(D4)", model.getFollowingSiblingAxis(D3), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(D4), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(X1), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(X2), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(X3), model, atomBridge);

		assertEquals(Axis.PARENT, "", model.getParent(D0), model, atomBridge);
		assertEquals(Axis.PARENT, "document", model.getParent(A1), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(B1), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(B2), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(B3), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(B4), model, atomBridge);
		assertEquals(Axis.PARENT, "element(B1)", model.getParent(C1), model, atomBridge);
		assertEquals(Axis.PARENT, "element(B1)", model.getParent(C2), model, atomBridge);
		assertEquals(Axis.PARENT, "element(B2)", model.getParent(C3), model, atomBridge);
		assertEquals(Axis.PARENT, "element(B2)", model.getParent(C4), model, atomBridge);
		assertEquals(Axis.PARENT, "element(B3)", model.getParent(C5), model, atomBridge);
		assertEquals(Axis.PARENT, "element(B3)", model.getParent(C6), model, atomBridge);
		assertEquals(Axis.PARENT, "element(C3)", model.getParent(D1), model, atomBridge);
		assertEquals(Axis.PARENT, "element(C4)", model.getParent(D2), model, atomBridge);
		assertEquals(Axis.PARENT, "element(C4)", model.getParent(D3), model, atomBridge);
		assertEquals(Axis.PARENT, "element(C4)", model.getParent(D4), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(X1), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(X2), model, atomBridge);
		assertEquals(Axis.PARENT, "element(A1)", model.getParent(X3), model, atomBridge);

		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(D0), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(A1), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(B1), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(C2),element(C1),element(B1)", model.getPrecedingAxis(B2), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D4),element(D3),element(D2),element(C4),element(D1),element(C3),element(B2),element(C2),element(C1),element(B1)", model.getPrecedingAxis(B3), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(C6),element(C5),element(B3),element(D4),element(D3),element(D2),element(C4),element(D1),element(C3),element(B2),element(C2),element(C1),element(B1)", model.getPrecedingAxis(B4), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(C1), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(C1)", model.getPrecedingAxis(C2), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(C2),element(C1),element(B1)", model.getPrecedingAxis(C3), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D1),element(C3),element(C2),element(C1),element(B1)", model.getPrecedingAxis(C4), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D4),element(D3),element(D2),element(C4),element(D1),element(C3),element(B2),element(C2),element(C1),element(B1)", model.getPrecedingAxis(C5), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(C5),element(D4),element(D3),element(D2),element(C4),element(D1),element(C3),element(B2),element(C2),element(C1),element(B1)", model.getPrecedingAxis(C6), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(C2),element(C1),element(B1)", model.getPrecedingAxis(D1), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D1),element(C3),element(C2),element(C1),element(B1)", model.getPrecedingAxis(D2), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D2),element(D1),element(C3),element(C2),element(C1),element(B1)", model.getPrecedingAxis(D3), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D3),element(D2),element(D1),element(C3),element(C2),element(C1),element(B1)", model.getPrecedingAxis(D4), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(X1), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(X2), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(X3), model, atomBridge);
		assertEquals(Axis.PRECEDING, "element(D1),element(C3),element(C2),element(C1),element(B1)", model.getPrecedingAxis(X5), model, atomBridge);

		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(D0), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(A1), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(B1), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(B1)", model.getPrecedingSiblingAxis(B2), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(B2),element(B1)", model.getPrecedingSiblingAxis(B3), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(B3),element(B2),element(B1)", model.getPrecedingSiblingAxis(B4), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(C1), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(C1)", model.getPrecedingSiblingAxis(C2), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(C3), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(C3)", model.getPrecedingSiblingAxis(C4), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(C5), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(C5)", model.getPrecedingSiblingAxis(C6), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(D1), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(D2), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(D2)", model.getPrecedingSiblingAxis(D3), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "element(D3),element(D2)", model.getPrecedingSiblingAxis(D4), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(X1), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(X2), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(X3), model, atomBridge);

		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(D0, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "attribute(X1),attribute(X2),attribute(X3),attribute({N4}X4)", model.getAttributeAxis(A1, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(B1, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(B2, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(B3, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(B4, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(C1, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(C2, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(C3, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "attribute(X5)", model.getAttributeAxis(C4, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(C5, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(C6, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(D1, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(D2, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(D3, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(D4, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(X1, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(X2, false), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(X3, false), model, atomBridge);
		/*
		 * if (true) { assertEquals(Axis.NAMESPACE, "namespace(),namespace(P4),namespace(P3)", model.getNamespaceAxis(A1), model, atomBridge); } else { // Not sure why DOM does this, but we don't have much choice. assertEquals(Axis.NAMESPACE,
		 * "namespace(),namespace(P3),namespace(P4)", model.getNamespaceAxis(A1), model, atomBridge); }
		 */

		assertCompare(0, D0, D0, model);
		assertCompare(0, A1, A1, model);
		assertCompare(-1, A1, B1, model);
		assertCompare(-1, C1, B2, model);

		assertCompare(0, D0, D0, model);
		assertCompare(-1, D0, A1, model);
		assertCompare(-1, D0, B1, model);
		assertCompare(-1, D0, C1, model);
		assertCompare(-1, D0, C2, model);
		assertCompare(-1, D0, B2, model);
		assertCompare(-1, D0, C3, model);
		assertCompare(-1, D0, D1, model);
		assertCompare(-1, D0, C4, model);
		assertCompare(-1, D0, D2, model);
		assertCompare(-1, D0, D3, model);
		assertCompare(-1, D0, D4, model);
		assertCompare(-1, D0, B3, model);
		assertCompare(-1, D0, C5, model);
		assertCompare(-1, D0, C6, model);
		assertCompare(-1, D0, B4, model);

		assertCompare(+1, A1, D0, model);
		assertCompare(0, A1, A1, model);
		assertCompare(-1, A1, B1, model);
		assertCompare(-1, A1, C1, model);
		assertCompare(-1, A1, C2, model);
		assertCompare(-1, A1, B2, model);
		assertCompare(-1, A1, C3, model);
		assertCompare(-1, A1, D1, model);
		assertCompare(-1, A1, C4, model);
		assertCompare(-1, A1, D2, model);
		assertCompare(-1, A1, D3, model);
		assertCompare(-1, A1, D4, model);
		assertCompare(-1, A1, B3, model);
		assertCompare(-1, A1, C5, model);
		assertCompare(-1, A1, C6, model);
		assertCompare(-1, A1, B4, model);

		assertCompare(+1, B1, D0, model);
		assertCompare(+1, B1, A1, model);
		assertCompare(0, B1, B1, model);
		assertCompare(-1, B1, C1, model);
		assertCompare(-1, B1, C2, model);
		assertCompare(-1, B1, B2, model);
		assertCompare(-1, B1, C3, model);
		assertCompare(-1, B1, D1, model);
		assertCompare(-1, B1, C4, model);
		assertCompare(-1, B1, D2, model);
		assertCompare(-1, B1, D3, model);
		assertCompare(-1, B1, D4, model);
		assertCompare(-1, B1, B3, model);
		assertCompare(-1, B1, C5, model);
		assertCompare(-1, B1, C6, model);
		assertCompare(-1, B1, B4, model);

		assertCompare(+1, C1, D0, model);
		assertCompare(+1, C1, A1, model);
		assertCompare(+1, C1, B1, model);
		assertCompare(0, C1, C1, model);
		assertCompare(-1, C1, C2, model);
		assertCompare(-1, C1, B2, model);
		assertCompare(-1, C1, C3, model);
		assertCompare(-1, C1, D1, model);
		assertCompare(-1, C1, C4, model);
		assertCompare(-1, C1, D2, model);
		assertCompare(-1, C1, D3, model);
		assertCompare(-1, C1, D4, model);
		assertCompare(-1, C1, B3, model);
		assertCompare(-1, C1, C5, model);
		assertCompare(-1, C1, C6, model);
		assertCompare(-1, C1, B4, model);

		assertCompare(+1, C2, D0, model);
		assertCompare(+1, C2, A1, model);
		assertCompare(+1, C2, B1, model);
		assertCompare(+1, C2, C1, model);
		assertCompare(0, C2, C2, model);
		assertCompare(-1, C2, B2, model);
		assertCompare(-1, C2, C3, model);
		assertCompare(-1, C2, D1, model);
		assertCompare(-1, C2, C4, model);
		assertCompare(-1, C2, D2, model);
		assertCompare(-1, C2, D3, model);
		assertCompare(-1, C2, D4, model);
		assertCompare(-1, C2, B3, model);
		assertCompare(-1, C2, C5, model);
		assertCompare(-1, C2, C6, model);
		assertCompare(-1, C2, B4, model);

		assertCompare(+1, B2, D0, model);
		assertCompare(+1, B2, A1, model);
		assertCompare(+1, B2, B1, model);
		assertCompare(+1, B2, C1, model);
		assertCompare(+1, B2, C2, model);
		assertCompare(0, B2, B2, model);
		assertCompare(-1, B2, C3, model);
		assertCompare(-1, B2, D1, model);
		assertCompare(-1, B2, C4, model);
		assertCompare(-1, B2, D2, model);
		assertCompare(-1, B2, D3, model);
		assertCompare(-1, B2, D4, model);
		assertCompare(-1, B2, B3, model);
		assertCompare(-1, B2, C5, model);
		assertCompare(-1, B2, C6, model);
		assertCompare(-1, B2, B4, model);

		assertCompare(+1, C3, D0, model);
		assertCompare(+1, C3, A1, model);
		assertCompare(+1, C3, B1, model);
		assertCompare(+1, C3, C1, model);
		assertCompare(+1, C3, C2, model);
		assertCompare(+1, C3, B2, model);
		assertCompare(0, C3, C3, model);
		assertCompare(-1, C3, D1, model);
		assertCompare(-1, C3, C4, model);
		assertCompare(-1, C3, D2, model);
		assertCompare(-1, C3, D3, model);
		assertCompare(-1, C3, D4, model);
		assertCompare(-1, C3, B3, model);
		assertCompare(-1, C3, C5, model);
		assertCompare(-1, C3, C6, model);
		assertCompare(-1, C3, B4, model);
	}

	public void testAxesFromEmptyOrigin()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		// final N document = makeAxisDocument(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final TypedModel<N, A> model = pcx.getModel();

		assertEquals(Axis.ANCESTOR, "", model.getAncestorAxis(null), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "", model.getAncestorOrSelfAxis(null), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(null), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildElements(null), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildElementsByName(null, null, null), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(null), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "", model.getDescendantOrSelfAxis(null), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "", model.getFollowingAxis(null), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(null), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(null), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(null), model, atomBridge);
		assertEquals(Axis.ATTRIBUTE, "", model.getAttributeAxis(null, false), model, atomBridge);
	}

	public void testCommentCreate()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		builder.comment("XYZ");
		final N node = builder.getNodes().get(0);

		assertEquals("dm:node-kind", NodeKind.COMMENT, model.getNodeKind(node));
		assertNameEq("dm:node-name", null, node, model, pcx);
		assertEquals("dm:string-value", "XYZ", model.getStringValue(node));
		assertEquals("dm:typed-value", "xs:string('XYZ')", node, model, pcx);
	}

	public void testDocumentCreate()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N document = makeTrivialDocument(pcx);

		final TypedModel<N, A> model = pcx.getModel();

		final NodeKind nodeKind = model.getNodeKind(document);

		assertEquals("dm:node-kind", NodeKind.DOCUMENT, nodeKind);

		try
		{
			if (ctx.isSupported(Feature.DOCUMENT_URI))
			{
				assertEquals(new URI("http://www.trivial.com"), model.getDocumentURI(document));
			}
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError(e);
		}
	}

	public void testElementCreate()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final N owner = makeTrivialDocument(pcx);

		doElementCreate(owner, new QName("swim"));
		doElementCreate(owner, new QName("http://www.millstriders.org", "bike", XMLConstants.DEFAULT_NS_PREFIX));

		/*
		 * if (RX_BRIDGE.equals(getBridge().getClass())) { // ExpandedName in XMLSDK doesn't retain prefix hints. doElementCreate(owner, new QName("http://www.millstriders.org", "run")); } else if (XI_BRIDGE.equals(getBridge().getClass())) { doElementCreate(owner, new
		 * QName("http://www.millstriders.org", "run")); } else { doElementCreate(owner, new QName("http://www.millstriders.org", "run", "m")); }
		 */
	}

	public void testFibonacci() throws GxmlException, SAXException
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		@SuppressWarnings("unused")
		final N document = makeFibonacci(5, pcx);
	}

	public void testIsNode()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N node = makeTrivialDocument(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A atom = atomBridge.createString("Hello");

		assertTrue(ctx.isNode(node));
		assertFalse(ctx.isNode(atom));
		assertFalse(ctx.isNode(null));
	}

	public void testNamespaceAxis0010()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N D0 = make0010Document(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		
		final TypedModel<N, A> model = pcx.getModel();

		final N R1 = getChildAt(D0, model, 0);

		if (ctx.isSupported(Feature.NAMESPACE_AXIS))
		{
			assertEquals(Axis.NAMESPACE, "", model.getNamespaceAxis(D0, true), model, atomBridge);

			final Iterator<N> namespaces = model.getNamespaceAxis(R1, true).iterator();
			assertTrue(namespaces.hasNext());
			final N namespace = namespaces.next();
			assertFalse(namespaces.hasNext());

			assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
			assertEquals("xml", model.getLocalName(namespace));
			assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(namespace));
			// this is *not* true for Axiom.
//			assertTrue(model.isSameNode(R1, model.getParent(namespace)));
		}
	}

	public void testNamespaceAxis0020()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N D0 = make0020Document(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final TypedModel<N, A> model = pcx.getModel();

		final N R1 = getChildAt(D0, model, 0);

		if (ctx.isSupported(Feature.NAMESPACE_AXIS))
		{
			assertEquals(Axis.NAMESPACE, "", model.getNamespaceAxis(D0, true), model, atomBridge);

			final Map<String, N> namespaces = mapNamespaceBindings(model.getNamespaceAxis(R1, true), model);
			assertEquals(2, namespaces.size());
			assertTrue(namespaces.containsKey("xml"));
			assertTrue(namespaces.containsKey("x"));
			{
				final N namespace = namespaces.get("xml");

				assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
				assertEquals("xml", model.getLocalName(namespace));
				assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(namespace));
//				assertTrue(model.isSameNode(R1, model.getParent(namespace)));
			}
			{
				final N namespace = namespaces.get("x");

				assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
				assertEquals("x", model.getLocalName(namespace));
				assertEquals("http://www.x.com", model.getStringValue(namespace));
				assertTrue(Ordering.isSameNode(R1, model.getParent(namespace), model));
			}
		}
	}

	public void testNamespaceAxis0030()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N D0 = make0030Document(pcx);

		final TypedModel<N, A> model = pcx.getModel();

		final N R1 = getChildAt(D0, model, 0);
		final N A = getChildAt(R1, model, 0);

		if (ctx.isSupported(Feature.NAMESPACE_AXIS))
		{
			final Map<String, N> namespaces = mapNamespaceBindings(model.getNamespaceAxis(A, true), model);
			assertEquals(3, namespaces.size());
			assertTrue(namespaces.containsKey("xml"));
			assertTrue(namespaces.containsKey("x"));
			assertTrue(namespaces.containsKey("p"));
			{
				final N namespace = namespaces.get("xml");

				assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
				assertEquals("xml", model.getLocalName(namespace));
				assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(namespace));
//				assertTrue(model.isSameNode(A, model.getParent(namespace)));
			}
			{
				final N namespace = namespaces.get("x");

				assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
				assertEquals("x", model.getLocalName(namespace));
				assertEquals("http://www.x.com", model.getStringValue(namespace));
				assertTrue(Ordering.isSameNode(A, model.getParent(namespace), model));
			}
			{
				final N namespace = namespaces.get("p");

				assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
				assertEquals("p", model.getLocalName(namespace));
				assertEquals("http://www.p.com", model.getStringValue(namespace));
				assertTrue(Ordering.isSameNode(A, model.getParent(namespace), model));
			}
		}
	}

	public void testNamespaceAxis0040()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N D0 = make0040Document(pcx);

		final TypedModel<N, A> model = pcx.getModel();

		final N R1 = getChildAt(D0, model, 0);
		final N A = getChildAt(R1, model, 0);

		if (ctx.isSupported(Feature.NAMESPACE_AXIS))
		{
			final Map<String, N> namespaces = mapNamespaceBindings(model.getNamespaceAxis(A, true), model);
//			assertEquals(1, namespaces.size());
			assertTrue(namespaces.containsKey("xml"));
			{
				final N namespace = namespaces.get("xml");

				assertEquals(NodeKind.NAMESPACE, model.getNodeKind(namespace));
				assertEquals("xml", model.getLocalName(namespace));
				assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(namespace));
//				assertTrue(model.isSameNode(A, model.getParent(namespace)));
			}
		}
	}

	private Map<String, N> mapNamespaceBindings(final Iterable<N> namespaces, final TypedModel<N, A> model)
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

	public void testNamespaceCreate001()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		final String prefix = "m";

		final String namespaceURI = "http://www.millstriders.org";

		builder.namespace(prefix, namespaceURI);
		final N node = builder.getNodes().get(0);

		assertEquals("dm:node-kind", NodeKind.NAMESPACE, model.getNodeKind(node));
		assertNameEq("dm:node-name", new QName("m"), node, model, pcx);
		assertEquals("dm:string-value", namespaceURI, model.getStringValue(node));
		assertEquals("dm:typed-value", "xs:string('" + namespaceURI.toString() + "')", node, model, pcx);

		assertEquals("local-name", "m", model.getLocalName(node));
	}

	public void testNamespaceCreate002()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final NameSource nameBridge = pcx.getAtomBridge().getNameBridge();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		final String prefix = "";

		final String namespaceURI = "http://www.millstriders.org";

		builder.namespace(prefix, namespaceURI);
		final N node = builder.getNodes().get(0);

		assertEquals("dm:node-kind", NodeKind.NAMESPACE, model.getNodeKind(node));
		assertNameEq("dm:node-name", new QName(nameBridge.empty()), node, model, pcx);
		assertEquals("dm:string-value", namespaceURI.toString(), model.getStringValue(node));
		assertEquals("dm:typed-value", "xs:string('" + namespaceURI.toString() + "')", node, model, pcx);

//		assertSame("namespace-uri", nameBridge.empty(), model.getNamespaceURI(node));
//		assertSame("local-name", nameBridge.empty(), model.getLocalName(node));
	}

	public void testNID()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final N document = makeAxisDocument(pcx);

		TypedModel<N, A> model = pcx.getModel();

		final HashMap<Object, N> map = new HashMap<Object, N>();

		int size = 0;

		for (final N node : model.getDescendantOrSelfAxis(document))
		{
			final Object nid = model.getNodeId(node);

			map.put(nid, node);

			size++;
		}

		assertEquals(size, map.size());
	}

	public void testNodeCast()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final TypedModel<N, A> model = pcx.getModel();

		final N node = makeTrivialDocument(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A atom = atomBridge.createString("Hello");

		assertTrue(model.getNodeKind(node).name(), Ordering.isSameNode(node, ctx.node(node), model));
		assertEquals(null, ctx.node(atom));
		assertNull(ctx.node(null));
	}

	public void testProcessingInstructionCreate()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		builder.processingInstruction("makeHandle", "bar");
		final N node = builder.getNodes().get(0);

		assertEquals("dm:node-kind", NodeKind.PROCESSING_INSTRUCTION, model.getNodeKind(node));
		assertNameEq("dm:node-name", new QName("makeHandle"), node, model, pcx);
		assertEquals("dm:string-value", "bar", model.getStringValue(node));
		assertEquals("dm:typed-value", "xs:string('bar')", node, model, pcx);
	}

	public void testTextCreate()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final TypedModel<N, A> model = pcx.getModel();
		final SequenceBuilder<N, A> builder = pcx.newSequenceBuilder();

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final String content = "Loop Quantum Gravity";

		builder.text(atomBridge.wrapAtom(atomBridge.createString(content)));
		final N node = builder.getNodes().get(0);

		assertEquals("dm:node-kind", NodeKind.TEXT, model.getNodeKind(node));
		assertNameEq("dm:node-name", null, node, model, pcx);
		assertEquals("dm:string-value", content, model.getStringValue(node));
		// assertEquals("dm:typed-value", "xs:string('" + content + "')", node, model);
	}

	public void testTrivialAxes()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final N trivial = makeTrivialDocument(pcx);

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final TypedModel<N, A> model = pcx.getModel();

		assertEquals(Axis.ANCESTOR, "", model.getAncestorAxis(trivial), model, atomBridge);
		assertEquals(Axis.ANCESTOR_OR_SELF, "document", model.getAncestorOrSelfAxis(trivial), model, atomBridge);
		assertEquals(Axis.CHILD, "", model.getChildAxis(trivial), model, atomBridge);
		assertEquals(Axis.DESCENDANT, "", model.getDescendantAxis(trivial), model, atomBridge);
		assertEquals(Axis.DESCENDANT_OR_SELF, "document", model.getDescendantOrSelfAxis(trivial), model, atomBridge);
		assertEquals(Axis.FOLLOWING, "", model.getFollowingAxis(trivial), model, atomBridge);
		assertEquals(Axis.FOLLOWING_SIBLING, "", model.getFollowingSiblingAxis(trivial), model, atomBridge);
		assertEquals(Axis.PARENT, "", model.getParent(trivial), model, atomBridge);
		assertEquals(Axis.PRECEDING, "", model.getPrecedingAxis(trivial), model, atomBridge);
		assertEquals(Axis.PRECEDING_SIBLING, "", model.getPrecedingSiblingAxis(trivial), model, atomBridge);
	}

	/*
	 * public void testDocumentURI() { final GxBridge<N, A> bridge = getBridge();
	 * 
	 * final N document = bridge.newDocument("xyz");
	 * 
	 * assertNotNull(document);
	 * 
	 * final GxModel<N, A> model = bridge.getModel(document);
	 * 
	 * assertEquals("xyz", model.getDocumentURI(document)); }
	 */

	/*
	 * public void testConcurrencyAxisChild() { final GxBridge<N, A> bridge = getBridge(); final GxDocumentBuilderFactory<N> dbf = bridge.newDocumentBuilderFactory(); final GxDocumentBuilder<N> db = dbf.newDocumentBuilder();
	 * 
	 * final N D0 = db.newDocument(); final GxModel<N, A> model = bridge.getModel(D0);
	 * 
	 * final N E0 = model.createElement(nameBridge.empty(), "E0");
	 * 
	 * model.appendChild(D0, E0);
	 * 
	 * final Iterable<? extends N> childAxisE0 = model.getChildAxis(E0); assertEquals(Axis.CHILD, "", childAxisE0, model);
	 * 
	 * final N E1 = model.createElement(nameBridge.empty(), "E1");
	 * 
	 * model.appendChild(E0, E1);
	 * 
	 * assertEquals(Axis.CHILD, "element(E1)", childAxisE0, model); }
	 */
}
