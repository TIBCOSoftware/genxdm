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
import javax.xml.namespace.QName;

import org.gxml.bridgekit.tree.Ordering;
import org.gxml.NodeKind;
import org.gxml.base.Model;
import org.gxml.base.ProcessingContext;
import org.gxml.base.io.FragmentBuilder;
import org.gxml.names.NameSource;

public abstract class GettingStartedTestBase<N> 
    extends GxTestBase<N>
{
	/**
	 * The application should act as a factory for the processing context.
	 */
	public void test00001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		assertNotNull("The application must return an instance of ".concat(ProcessingContext.class.getSimpleName()), pcx);
	}

	/**
	 * The processing context handles generic array creation of nodes.
	 */
	public void test00004()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		for (int size = 0; size < 10; size++)
		{
			final N[] nodes = pcx.nodeArray(size);
			assertNotNull(nodes);
			assertEquals(size, nodes.length);
		}

		try
		{
			pcx.nodeArray(-1);

			fail();
		}
		catch (final NegativeArraySizeException e)
		{
			// OK
		}
	}

	// TODO: move all of these to a typed getting started.
//	/**
//	 * The processing context supplies an atom bridge.
//	 */
//	public void test00005()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//		if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//		{
//		    final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//		    assertNotNull(atomBridge);
//		}
//	}
//
//	/**
//	 * The atom bridge provides generic array creation of atoms.
//	 */
//	public void test00006()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//        if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//        {
//            final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//    		for (int size = 0; size < 10; size++)
//    		{
//    			final A[] atoms = atomBridge.atomArray(size);
//    			assertNotNull(atoms);
//    			assertEquals(size, atoms.length);
//    		}
//    
//    		try
//    		{
//    			atomBridge.atomArray(-1);
//    
//    			fail();
//    		}
//    		catch (final NegativeArraySizeException e)
//    		{
//    			// OK
//    		}
//        }
//	}
//
//	/**
//	 * The atom bridge provides creation of xs:untypedAtomic atoms.
//	 */
//	public void test00007()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//        if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//        {
//            final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//    		final A atom = atomBridge.createUntypedAtomic("abc");
//    		assertNotNull(atom);
//    		final String abc = atomBridge.getC14NForm(atom);
//    		assertEquals("abc", abc);
//        }
//	}
//
//	/**
//	 * The atom bridge allows an atom to be wrapped into a singleton list.
//	 */
//	public void test00008()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//        if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//        {
//            final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//    		final A original = atomBridge.createUntypedAtomic("abc");
//    		final List<A> atoms = atomBridge.wrapAtom(original);
//    
//    		{
//    			assertNotNull(atoms);
//    			assertEquals(1, atoms.size());
//    			final A atom = atoms.get(0);
//    			assertNotNull(atom);
//    			final String abc = atomBridge.getC14NForm(atom);
//    			assertEquals("abc", abc);
//    
//    			assertFalse(atoms.isEmpty());
//    		}
//    
//    		{
//    			final Iterator<A> it = atoms.iterator();
//    			assertNotNull(it);
//    			assertTrue(it.hasNext());
//    			final A atom = it.next();
//    			final String abc = atomBridge.getC14NForm(atom);
//    			assertEquals("abc", abc);
//    			assertFalse(it.hasNext());
//    		}
//        }
//	}
//
//	/**
//	 * When wrapping a <code>null</code> atom, the result is a non-null empty
//	 * sequence.
//	 */
//	public void test00009()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//        if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//        {
//            final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//    		final List<A> atoms = atomBridge.wrapAtom(null);
//    		assertNotNull(atoms);
//    		assertTrue(atoms.isEmpty());
//    		final Iterator<A> it = atoms.iterator();
//    		assertNotNull(it);
//    		assertFalse(it.hasNext());
//        }
//	}

	/**
	 * The processing context provides a name bridge for the creation of
	 * symbols.
	 */
	public void test00010()
	{
		final NameSource nameBridge = new NameSource();
		assertNotNull(nameBridge);

		final String symbol = "xyz";
		assertNotNull(symbol);

		final String strval = symbol.toString();
		assertNotNull(strval);
		assertEquals("xyz", strval);

		assertNull(null);
	}

	/**
	 * The "empty" symbol corresponds to a zero-length string.
	 */
	public void test00011()
	{
		final NameSource nameBridge = new NameSource();
		assertNotNull(nameBridge);

		final String symbol = nameBridge.empty();
		assertNotNull(symbol);

		final String strval = symbol.toString();
		assertNotNull(strval);
		assertEquals("", strval);

		assertTrue(nameBridge.isEmpty(symbol));
		assertFalse(nameBridge.isEmpty("junk"));

		assertEquals(nameBridge.isEmpty(null), "" == null);
		assertEquals(nameBridge.isEmpty(nameBridge.empty()), "" == nameBridge.empty());
		assertEquals(nameBridge.isEmpty("a"), "" == "a");
	}

	/**
	 * Commonly used symbols for namespaces are cached at the name bridge level.
	 */
	public void test00012()
	{
		final NameSource nameBridge = new NameSource();
		assertNotNull(nameBridge);

		assertTrue(nameBridge.isXmlNamespaceURI("http://www.w3.org/XML/1998/namespace"));
		assertTrue(nameBridge.isXmlNamespaceURI(XMLConstants.XML_NS_URI));
		assertFalse(nameBridge.isXmlNamespaceURI(XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertFalse(nameBridge.isXmlNamespaceURI(null));
	}

	/**
	 * Symbols are tested for equality using address comparison.
	 */
	public void test00013()
	{
		final NameSource nameBridge = new NameSource();
		assertNotNull(nameBridge);

		final String a = "a";
		final String b = "b";

		assertSame(a, a);
		assertNotSame(a, b);
		assertNotSame(a, null);
		assertNotSame(null, a);
		assertSame(null, null);
	}

	/**
	 * The name bridge provides a convenient conversion of {@link QName} to
	 * {@link QName}.
	 * TODO: please note how boneheaded the doc sounds, since refactoring.  *sigh*
	 */
	public void test00014()
	{
		final NameSource nameBridge = new NameSource();
		assertNotNull(nameBridge);

		{
			final QName name = new QName("a", "b", "c");
			assertNotNull(name);
			assertEquals("a", name.getNamespaceURI());
			assertEquals("b", name.getLocalPart());
			assertEquals("c", name.getPrefix());
		}

		// Do we really want to do this?  
		{
			final QName name = new QName("*", "*", "*");
			assertNotNull(name);
			assertEquals("*", name.getNamespaceURI());
			assertEquals("*", name.getLocalPart());
			assertEquals("*", name.getPrefix());
		}
		{
		    final String ESCAPE = "\u001B";
			final QName wild = new QName(ESCAPE, ESCAPE, ESCAPE);
			final QName qname = wild;
			assertEquals(ESCAPE, qname.getNamespaceURI());
			assertEquals(ESCAPE, qname.getLocalPart());
			assertEquals(ESCAPE, qname.getPrefix());
		}
	}

	// TODO: move to typed gettingstarted
//	/**
//	 * The processing context allows an item to be introspected to determine
//	 * whether it is a node or an atom.
//	 */
//	public void test00015()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//        if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//        {
//            final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//    		final I item = atomBridge.createUntypedAtomic("abc");
//    		assertNotNull(item);
//    
//    		assertTrue(pcx.isAtom(item));
//    		assertFalse(pcx.isNode(item));
//    
//    		assertFalse(pcx.isAtom(null));
//    		assertFalse(pcx.isAtom(null));
//    
//    		final A atom = pcx.atom(item);
//    		assertNotNull(atom);
//    		assertEquals("abc", atomBridge.getC14NForm(atom));
//        }
//	}

	/**
	 * A document is constructed by streaming events into a builder.
	 */
	public void test00016()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();
		assertNotNull(builder);

		for (int i = 0; i < 10; i++)
		{
			builder.reset();

			for (int j = 0; j < i; j++)
			{
				builder.startDocument(null, null);
				builder.endDocument();
			}

			final List<N> nodes = builder.getNodes();
			assertNotNull(nodes);
			assertEquals(i, nodes.size());

			for (int j = 0; j < i; j++)
			{
				final N node = nodes.get(j);
				assertNotNull(node);
			}
		}
	}

	/**
	 * The processing context provides a model bridge for navigating and
	 * accessing a tree of nodes.
	 */
	public void test00017()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		assertNotNull(model);
	}

	/**
	 * The model bridge can be used to determine the dm:node-kind of a node.
	 */
	public void test00018()
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
		assertFalse(model.isElement(node));
		assertFalse(model.isAttribute(node));
		assertFalse(model.isNamespace(node));
		assertFalse(model.isText(node));
		assertNull(model.getNamespaceURI(node));
		assertNull(model.getLocalName(node));
		assertNull(model.getPrefix(node));
	}

	/**
	 * Building a standalone comment node and accessing its dm:node-kind and
	 * dm:string-value.
	 */
	public void test00019()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.comment("Hello");

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.COMMENT, nodeKind);
		assertFalse(model.isElement(node));
		assertFalse(model.isAttribute(node));
		assertFalse(model.isNamespace(node));
		assertFalse(model.isText(node));
		assertEquals("Hello", model.getStringValue(node));
		assertNull(model.getNamespaceURI(node));
		assertNull(model.getLocalName(node));
		assertNull(model.getPrefix(node));
	}

	/**
	 * Building a standalone text node and accessing its dm:node-kind and
	 * dm:string-value.
	 */
	public void test00020()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.text("Hello");

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.TEXT, nodeKind);
		assertFalse(model.isElement(node));
		assertFalse(model.isAttribute(node));
		assertFalse(model.isNamespace(node));
		assertTrue(model.isText(node));
		assertEquals("Hello", model.getStringValue(node));
		assertNull(model.getNamespaceURI(node));
		assertNull(model.getLocalName(node));
		assertNull(model.getPrefix(node));
	}

	/**
	 * Building a standalone processing-instruction node and accessing its
	 * dm:node-kind, dm:name and dm:string-value.
	 */
	public void test00021()
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
		assertFalse(model.isElement(node));
		assertFalse(model.isAttribute(node));
		assertFalse(model.isNamespace(node));
		assertFalse(model.isText(node));
		assertEquals("target", model.getLocalName(node));
		assertEquals("data", model.getStringValue(node));

		assertEquals(nameBridge.empty(), model.getNamespaceURI(node));
		assertEquals("", model.getNamespaceURI(node));
		assertEquals(copy("target"), model.getLocalName(node));
		assertEquals("", model.getPrefix(node));
	}

	/**
	 * Building a standalone namespace node and accessing its dm:node-kind,
	 * dm:name and dm:string-value.
	 */
	public void test00022()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.namespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);

		final List<N> nodes = builder.getNodes();

		final N node = nodes.get(0);

		final NodeKind nodeKind = model.getNodeKind(node);
		assertEquals(NodeKind.NAMESPACE, nodeKind);
		assertFalse(model.isElement(node));
		assertFalse(model.isAttribute(node));
		assertTrue(model.isNamespace(node));
		assertFalse(model.isText(node));
		assertEquals(XMLConstants.XML_NS_PREFIX, model.getLocalName(node));
		assertEquals(XMLConstants.XML_NS_URI, model.getStringValue(node));
	}

	/**
	 * Building a standalone attribute node and accessing its dm:node-kind,
	 * dm:name and dm:string-value.
	 */
	public void test00023()
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
		assertFalse(model.isElement(node));
		assertFalse(model.isNamespace(node));
		assertTrue(model.isAttribute(node));
		assertFalse(model.isText(node));
		assertEquals(nameBridge.empty(), model.getNamespaceURI(node));
		assertEquals("a", model.getLocalName(node));
		assertEquals("", model.getPrefix(node));
		assertEquals("A", model.getStringValue(node));
	}

	/**
	 * Building a standalone element node and accessing its dm:node-kind,
	 * dm:name and dm:string-value.
	 */
	public void test00024()
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
		assertTrue(model.isElement(node));
		assertFalse(model.isNamespace(node));
		assertFalse(model.isAttribute(node));
		assertFalse(model.isText(node));
		assertEquals("http://www.x.com", model.getNamespaceURI(node));
		assertEquals("foo", model.getLocalName(node));
		assertEquals("x", model.getPrefix(node));
		assertEquals("", model.getStringValue(node));

	}

	/**
	 * Building a document containing an element and a text node.
	 */
	public void test00025()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.startDocument(null, null);
		builder.startElement("http://www.x.com", "foo", "x");
		builder.text("Hello, World!");
		builder.endElement();
		builder.endDocument();

		final List<N> nodes = builder.getNodes();

		final N document = nodes.get(0);

		assertEquals(NodeKind.DOCUMENT, model.getNodeKind(document));

		final N foo = model.getFirstChild(document);
		assertNotNull(foo);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(foo));
		assertNotNull(model.getParent(foo));
		assertTrue(Ordering.isSameNode(document, model.getParent(foo), model));

		final N text = model.getFirstChild(foo);
		assertNotNull(text);
		assertEquals(NodeKind.TEXT, model.getNodeKind(text));
		assertNotNull(model.getParent(text));
		assertTrue(Ordering.isSameNode(foo, model.getParent(text), model));
	}

	/**
	 * Build a more complicated document and check the model bridge navigation.
	 */
	public void test00026()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.startDocument(null, null);
		try
		{
			builder.startElement("http://www.x.com", "root", "x");
			try
			{
				builder.startElement("http://www.x.com", "foo", "x");
				try
				{
					builder.text("Hello");
				}
				finally
				{
					builder.endElement();
				}
				builder.startElement("http://www.x.com", "bar", "x");
				try
				{
					builder.text("World");
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

		final List<N> nodes = builder.getNodes();

		final N document = nodes.get(0);

		assertEquals(NodeKind.DOCUMENT, model.getNodeKind(document));
		assertFalse(model.hasParent(document));
		assertFalse(model.hasPreviousSibling(document));
		assertNull(model.getPreviousSibling(document));

		final N root = model.getFirstChild(document);
		assertNotNull(root);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(root));
		assertNotNull(model.getParent(root));
		assertTrue(Ordering.isSameNode(document, model.getParent(root), model));
		assertTrue(model.hasParent(root));

		final N foo = model.getFirstChild(root);
		assertNotNull(foo);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(foo));
		assertNotNull(model.getParent(foo));
		assertTrue(Ordering.isSameNode(root, model.getParent(foo), model));
		assertTrue(model.hasNextSibling(foo));
		assertFalse(model.hasPreviousSibling(foo));

		final N bar = model.getNextSibling(foo);
		assertNotNull(bar);
		assertEquals(NodeKind.ELEMENT, model.getNodeKind(bar));
		assertNotNull(model.getParent(bar));
		assertTrue(Ordering.isSameNode(root, model.getParent(bar), model));
		assertFalse(model.hasNextSibling(bar));
		assertTrue(model.hasPreviousSibling(bar));

		assertNotNull(model.getPreviousSibling(bar));
		assertTrue(Ordering.isSameNode(foo, model.getPreviousSibling(bar), model));
		assertTrue(Ordering.isSameNode(bar, model.getNextSibling(foo), model));

		assertNotNull(model.getLastChild(root));
		assertTrue(Ordering.isSameNode(bar, model.getLastChild(root), model));

		assertTrue(Ordering.isSameNode(document, model.getRoot(foo), model));
		assertTrue(Ordering.isSameNode(document, model.getRoot(bar), model));
		assertTrue(Ordering.isSameNode(document, model.getRoot(root), model));
	}

	// TODO: new typed getting started
//	public void test000027()
//	{
//		final ProcessingContext<N> pcx = newProcessingContext();
//        if (pcx instanceof TypedProcessingContext<?, ?, ?, ?>)
//        {
//            final AtomBridge<A> atomBridge = ((TypedProcessingContext<N, A>)pcx).getAtomBridge();
//    		final A atom = atomBridge.createString("abc");
//    		assertNotNull(atom);
//    		final String abc = atomBridge.getString(atom);
//    		assertEquals("abc", abc);
//    		assertEquals(atomBridge.getString(atom), atomBridge.getC14NForm(atom));
//        }
//	}

	/**
	 * Fragment containing an element.
	 */
	public void test00027()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final Model<N> model = pcx.getModel();
		final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

		builder.startElement("http://www.x.com", "root", "x");
		try
		{
			builder.text("Hello");
		}
		finally
		{
			builder.endElement();
		}

		final List<N> nodes = builder.getNodes();

		final N element = nodes.get(0);

		assertEquals(NodeKind.ELEMENT, model.getNodeKind(element));
		assertFalse(model.hasPreviousSibling(element));
		assertNull(model.getPreviousSibling(element));
		assertFalse(model.hasNextSibling(element));
		assertNull(model.getNextSibling(element));
		assertFalse(model.hasParent(element));
		assertNull(model.getParent(element));
		assertEquals("Hello", model.getStringValue(element));
	}

	public void test00030()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		for (int size = 0; size < 10; size++)
		{
			final N[] nodes = pcx.nodeArray(size);
			assertNotNull(nodes);
			assertEquals(size, nodes.length);
		}

		try
		{
			pcx.nodeArray(-1);

			fail();
		}
		catch (final NegativeArraySizeException e)
		{
			// OK
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
	public String copy(final String original)
	{
		final String copy = original.concat("junk").substring(0, original.length());
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
