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

import java.io.StringWriter;

import javax.xml.XMLConstants;

import junit.framework.TestCase;

import org.genxdm.Resolver;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.ContentHandler;
import org.genxdm.base.mutable.MutableModel;
import org.genxdm.base.mutable.NodeFactory;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;

public abstract class MutateTestBase<N> 
    extends TestCase
    implements ProcessingContextFactory<N>
{
	private void assertEquals(final String message, final String expected, final N docNode, final Model<N> model, final ProcessingContext<N> pcx)
	{
		final String actual = WhiteSpaceMangler.collapseWhiteSpace(serialize(docNode, model, pcx));
		final String expect = WhiteSpaceMangler.collapseWhiteSpace(expected);

		if (!expect.equals(actual))
		{
			System.out.println("message=" + message);
			System.out.println("expect=" + expect);
			System.out.println("actual=" + actual);
		}

		assertEquals(message, expect, actual);
	}

	private void assertEquals(final String message, final String expected, final N docNode, final ProcessingContext<N> pcx)
	{
		final Model<N> model = pcx.getMutableContext().getModel();

		assertEquals(message, expected, docNode, model, pcx);
	}

	private String serialize(final N docNode, final Model<N> model, final ProcessingContext<N> pcx)
	{
		final StringWriter sw = new StringWriter();

		final ContentHandler handler = new SimplestSerializer<N>(sw);

		try
		{
			model.stream(docNode, true, handler);
		}
		catch (final GxmlException e)
		{
			throw new RuntimeException(e);
		}

		return sw.toString();
	}

	public void testAdoptAttribute()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N docSource = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N source = model.getOwner(docSource);

		assertEquals("", "document { }", docSource, pcx);

		final N foo = factory.createElement(source, nameBridge.empty(), "foo", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docSource, foo);

		final N a = factory.createAttribute(source, nameBridge.empty(), "a", "", "xyz");

		model.setAttribute(foo, a);

		assertEquals("", "document { element foo { attribute a { xyz } } }", docSource, pcx);

		final N docTarget = factory.createDocument(null, null);

		final N bar = factory.createElement(docTarget, nameBridge.empty(), "bar", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docTarget, bar);

		model.adoptNode(docTarget, a);

		model.setAttribute(bar, a);

		assertEquals("", "document { element foo { } }", docSource, pcx);
		assertEquals("", "document { element bar { attribute a { xyz } } }", docTarget, pcx);
	}

	public void testAdoptComment()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();

		final N docSource = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(docSource);

		assertEquals("", "document { }", docSource, pcx);

		final N e = factory.createComment(owner, "Hello");

		model.appendChild(docSource, e);

		assertEquals("", "document { comment { 'Hello' } }", docSource, pcx);

		final N docTarget = factory.createDocument(null, null);

		model.adoptNode(docTarget, e);

		model.appendChild(docTarget, e);

		assertEquals("", "document { }", docSource, pcx);
		assertEquals("", "document { comment { 'Hello' } }", docTarget, pcx);
	}

	public void testAdoptElement()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N source = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(source);

		assertEquals("", "document { }", source, pcx);

		final N oldNode = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		assertTrue(Ordering.isSameNode(source, model.getOwner(oldNode), model));

		model.appendChild(source, oldNode);

		assertEquals("", "document { element root { } }", source, pcx);

		final N target = factory.createDocument(null, null);

		final N newNode = model.adoptNode(target, oldNode);

		assertTrue(Ordering.isSameNode(target, model.getOwner(newNode), model));

		model.appendChild(target, newNode);

		assertEquals("", "document { }", source, pcx);
		assertEquals("", "document { element root { } }", target, pcx);
	}

	public void testAdoptNamespace()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N docSource = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N source = model.getOwner(docSource);

		assertEquals("", "document { }", docSource, pcx);

		final N foo = factory.createElement(source, nameBridge.empty(), "foo", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docSource, foo);

		final N namespace = factory.createNamespace(source, "x", "http://www.example.com");

		model.setNamespace(foo, namespace);

		assertEquals("", "document { element foo { namespace { 'x' , 'http://www.example.com' } } }", docSource, pcx);

		final N docTarget = factory.createDocument(null, null);

		final N bar = factory.createElement(docTarget, nameBridge.empty(), "bar", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docTarget, bar);

		model.adoptNode(docTarget, namespace);

		model.setNamespace(bar, namespace);

		assertEquals("", "document { element foo { } }", docSource, pcx);
		assertEquals("", "document { element bar { namespace { 'x' , 'http://www.example.com' } } }", docTarget, pcx);
	}

	public void testAdoptProcessingInstruction()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();

		final N docSource = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(docSource);

		assertEquals("", "document { }", docSource, pcx);

		final N e = factory.createProcessingInstruction(owner, "printer", "xyz");

		model.appendChild(docSource, e);

		assertEquals("", "document { processing-instruction { 'printer' , 'xyz' } }", docSource, pcx);

		final N docTarget = factory.createDocument(null, null);

		model.adoptNode(docTarget, e);

		model.appendChild(docTarget, e);

		assertEquals("", "document { }", docSource, pcx);
		assertEquals("", "document { processing-instruction { 'printer' , 'xyz' } }", docTarget, pcx);
	}

	public void testAppendAttributeWithStringValue()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final String uri = "http://www.example.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, uri, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		final N attribute = factory.createAttribute(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A");

		model.setAttribute(root, attribute);

		assertEquals("", "document { element x:root { attribute a { A } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "B");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "X");

		assertEquals("", "document { element x:root { attribute a { X } attribute b { B } } }", doc, pcx);
	}

	public void testAppendAttributeWithValue()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final String uri = "http://www.example.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, uri, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		final N attribute = factory.createAttribute(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A");

		model.setAttribute(root, attribute);

		assertEquals("", "document { element x:root { attribute a { A } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "B");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "X");

		assertEquals("", "document { element x:root { attribute a { X } attribute b { B } } }", doc, pcx);
	}

	public void testAppendComment()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N comment = factory.createComment(owner, "Hello");

		model.appendChild(root, comment);

		assertEquals("", "document { element root { comment { 'Hello' } } }", doc, pcx);
	}

	public void testAppendNamespace()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();

		final String xyz = "http://www.xyz.com";
		final String pqr = "http://www.pqr.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, xyz, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		model.setNamespace(root, "p", pqr);

		assertEquals("", "document { element x:root { namespace { 'p' , 'http://www.pqr.com' } } }", doc, pcx);

		final N namespace = factory.createNamespace(doc, "x", xyz);

		model.setNamespace(root, namespace);

		assertEquals("", "document { element x:root { namespace { 'p' , 'http://www.pqr.com' } namespace { 'x' , 'http://www.xyz.com' } } }", doc, pcx);
	}

	public void testAppendProcessingInstruction()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N pi = factory.createProcessingInstruction(owner, "ttt", "ddd");

		model.appendChild(root, pi);

		assertEquals("", "document { element root { processing-instruction { 'ttt' , 'ddd' } } }", doc, pcx);
	}

	public void testAppendTextWithStringValue()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N text = factory.createText(owner, "Hello");

		model.appendChild(root, text);

		assertEquals("", "document { element root { text { Hello } } }", doc, pcx);
	}

	public void testAppendTextWithValue()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N hello = factory.createText(owner, "Hello");

		model.appendChild(root, hello);

		assertEquals("", "document { element root { text { Hello } } }", doc, pcx);

		final N world = factory.createText(owner, "World");

		model.appendChild(root, world);

        model.normalize(doc);

		assertEquals("", "document { element root { text { HelloWorld } } }", doc, pcx);
	}

	public void testElementMutation()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
		final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N b = factory.createElement(owner, nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(root, b);

		assertEquals("", "document { element root { element b { } } }", doc, pcx);

		final N d = factory.createElement(owner, nameBridge.empty(), "d", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(root, d);

		assertEquals("", "document { element root { element b { } element d { } } }", doc, pcx);

		final N a = factory.createElement(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX);

		model.insertBefore(root, a, b);

		assertEquals("", "document { element root { element a { } element b { } element d { } } }", doc, pcx);

		final N c = factory.createElement(owner, nameBridge.empty(), "c", XMLConstants.DEFAULT_NS_PREFIX);

		model.insertBefore(root, c, d);

		assertEquals("", "document { element root { element a { } element b { } element c { } element d { } } }", doc, pcx);

		final N e = factory.createElement(owner, nameBridge.empty(), "e", XMLConstants.DEFAULT_NS_PREFIX);

		model.insertBefore(root, e, null);

		assertEquals("", "document { element root { element a { } element b { } element c { } element d { } element e { } } }", doc, pcx);

		model.removeChild(root, e);

		assertEquals("", "document { element root { element a { } element b { } element c { } element d { } } }", doc, pcx);

		model.removeChild(root, a);

		assertEquals("", "document { element root { element b { } element c { } element d { } } }", doc, pcx);

		model.removeChild(root, c);

		assertEquals("", "document { element root { element b { } element d { } } }", doc, pcx);

		for (final N child : model.getChildAxis(root))
		{
			model.removeChild(root, child);
		}

		assertEquals("", "document { element root { } }", doc, pcx);
	}

	public void testImportAttribute()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N docSource = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N source = model.getOwner(docSource);

		assertEquals("", "document { }", docSource, pcx);

		final N foo = factory.createElement(source, nameBridge.empty(), "foo", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docSource, foo);

		final N a = factory.createAttribute(source, nameBridge.empty(), "a", "", "xyz");

		model.setAttribute(foo, a);

		assertEquals("", "document { element foo { attribute a { xyz } } }", docSource, pcx);

		final N docTarget = factory.createDocument(null, null);

		final N target = model.getOwner(docTarget);

		final N bar = factory.createElement(target, nameBridge.empty(), "bar", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docTarget, bar);

		final N copy = model.importNode(docTarget, a, true);

		model.setAttribute(bar, copy);

		assertEquals("", "document { element foo { attribute a { xyz } } }", docSource, pcx);
		assertEquals("", "document { element bar { attribute a { xyz } } }", docTarget, pcx);
	}

	public void testImportElement()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N source = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		assertEquals("", "document { }", source, pcx);

		final N oldNode = factory.createElement(source, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(source, oldNode);

		assertEquals("", "document { element root { } }", source, pcx);

		final N target = factory.createDocument(null, null);

		final N newNode = model.importNode(target, oldNode, true);

		assertFalse(Ordering.isSameNode(oldNode, newNode, model));
		assertTrue(Ordering.isSameNode(source, model.getOwner(oldNode), model));
		assertTrue(Ordering.isSameNode(target, model.getOwner(newNode), model));

		model.appendChild(target, newNode);

		assertEquals("", "document { element root { } }", source, pcx);
		assertEquals("", "document { element root { } }", target, pcx);
	}

	public void testNormalize001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N hello = factory.createText(owner, "Hello");

		model.appendChild(root, hello);

		final N space = factory.createText(owner, " ");

		model.appendChild(root, space);

		final N world = factory.createText(owner, "World");

		model.appendChild(root, world);

		model.normalize(root);

		assertEquals("", "document { element root { text { Hello World } } }", doc, pcx);
	}

	public void testNormalize002()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		final N comment = factory.createComment(owner, "Break");

		model.appendChild(root, comment);

		final N hello = factory.createText(owner, "Hello");

		model.appendChild(root, hello);

		final N space = factory.createText(owner, " ");

		model.appendChild(root, space);

		final N world = factory.createText(owner, "World");

		model.appendChild(root, world);

		model.normalize(root);

		assertEquals("", "document { element root { comment { 'Break' } text { Hello World } } }", doc, pcx);
	}

	public void testNormalize003()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		model.appendChild(root, factory.createText(owner, "Hello"));
		model.appendChild(root, factory.createComment(owner, "Break"));
		model.appendChild(root, factory.createText(owner, " "));
		model.appendChild(root, factory.createText(owner, "World"));

		model.normalize(root);

		assertEquals("", "document { element root { text { Hello } comment { 'Break' } text {  World } } }", doc, pcx);
	}

	public void testNormalize004()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		model.appendChild(root, factory.createText(owner, "Hello"));
		model.appendChild(root, factory.createText(owner, " "));
		model.appendChild(root, factory.createComment(owner, "Break"));
		model.appendChild(root, factory.createText(owner, "World"));

		model.normalize(root);

		assertEquals("", "document { element root { text { Hello  } comment { 'Break' } text { World } } }", doc, pcx);
	}

	public void testNormalize005()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		model.appendChild(root, factory.createText(owner, "Hello"));
		model.appendChild(root, factory.createText(owner, " "));
		model.appendChild(root, factory.createText(owner, "World"));
		model.appendChild(root, factory.createComment(owner, "Break"));

		model.normalize(root);

		assertEquals("", "document { element root { text { Hello World } comment { 'Break' } } }", doc, pcx);
	}

	public void testNormalize006()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		model.appendChild(root, factory.createText(owner, "Hello"));
		model.appendChild(root, factory.createText(owner, " "));
		model.appendChild(root, factory.createText(owner, "World"));
		model.appendChild(root, factory.createComment(owner, "Break"));

		model.normalize(root);

		assertEquals("", "document { element root { text { Hello World } comment { 'Break' } } }", doc, pcx);
	}

	public void testNormalize007()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } } ", doc, pcx);

		model.appendChild(root, factory.createText(owner, "Hello"));
		model.appendChild(root, factory.createText(owner, " "));
		model.appendChild(root, factory.createText(owner, "World"));
		model.appendChild(root, factory.createComment(owner, "Break"));
		model.appendChild(root, factory.createComment(owner, "!"));

		model.normalize(root);

		assertEquals("", "document { element root { text { Hello World } comment { 'Break' } comment { '!' } } }", doc, pcx);
	}

	public void testOwnership()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertTrue(Ordering.isSameNode(doc, owner, model));

		assertEquals("", "document { }", doc, pcx);

		final N root = factory.createElement(owner, nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(doc, root);

		assertEquals("", "document { element root { } }", doc, pcx);

		final N a = factory.createElement(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(root, a);

		assertEquals("", "document { element root { element a { } } }", doc, pcx);

		model.removeChild(root, a);

		assertEquals("", "document { element root { } }", doc, pcx);
	}

	public void testRemoveAttributeFirst()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final String uri = "http://www.example.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, uri, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		final N attribute = factory.createAttribute(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A");

		model.setAttribute(root, attribute);

		assertEquals("", "document { element x:root { attribute a { A } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "B");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "c", XMLConstants.DEFAULT_NS_PREFIX, "C");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } attribute c { C } } }", doc, pcx);

		model.removeAttribute(root, nameBridge.empty(), "a");

		assertEquals("", "document { element x:root { attribute b { B } attribute c { C } } }", doc, pcx);
	}

	public void testRemoveAttributeLast()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final String uri = "http://www.example.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, uri, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		final N attribute = factory.createAttribute(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A");

		model.setAttribute(root, attribute);

		assertEquals("", "document { element x:root { attribute a { A } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "B");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "c", XMLConstants.DEFAULT_NS_PREFIX, "C");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } attribute c { C } } }", doc, pcx);

		model.removeAttribute(root, nameBridge.empty(), "c");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } } }", doc, pcx);
	}

	public void testRemoveAttributeMiddle()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final String uri = "http://www.example.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, uri, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		final N attribute = factory.createAttribute(owner, nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, "A");

		model.setAttribute(root, attribute);

		assertEquals("", "document { element x:root { attribute a { A } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "b", XMLConstants.DEFAULT_NS_PREFIX, "B");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } } }", doc, pcx);

		model.setAttribute(root, nameBridge.empty(), "c", XMLConstants.DEFAULT_NS_PREFIX, "C");

		assertEquals("", "document { element x:root { attribute a { A } attribute b { B } attribute c { C } } }", doc, pcx);

		model.removeAttribute(root, nameBridge.empty(), "b");

		assertEquals("", "document { element x:root { attribute a { A } attribute c { C } } }", doc, pcx);
	}

	public void testRemoveNamespace()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();

		final String xyz = "http://www.xyz.com";
		final String pqr = "http://www.pqr.com";

		final N doc = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(doc);

		assertEquals("", "document {  } ", doc, pcx);

		final N root = factory.createElement(owner, xyz, "root", "x");

		model.appendChild(doc, root);

		assertEquals("", "document { element x:root { } } ", doc, pcx);

		model.setNamespace(root, "p", pqr);
		model.setNamespace(root, "x", xyz);

		assertEquals("", "document { element x:root { namespace { 'p' , 'http://www.pqr.com' } namespace { 'x' , 'http://www.xyz.com' } } }", doc, pcx);

		model.removeNamespace(root, "p");
		model.removeNamespace(root, "x");

		assertEquals("", "document { element x:root { } } ", doc, pcx);
	}

	public void testReplaceChild()
	{
		final ProcessingContext<N> pcx = newProcessingContext();
        final NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		final NameSource nameBridge = new NameSource();

		final N docSource = factory.createDocument(null, null);

		final MutableModel<N> model = pcx.getMutableContext().getModel();

		final N owner = model.getOwner(docSource);

		assertEquals("", "document { }", docSource, pcx);

		final N foo = factory.createElement(owner, nameBridge.empty(), "foo", XMLConstants.DEFAULT_NS_PREFIX);

		model.appendChild(docSource, foo);

		assertEquals("", "document { element foo { } }", docSource, pcx);

		final N bar = factory.createElement(owner, nameBridge.empty(), "bar", XMLConstants.DEFAULT_NS_PREFIX);

		model.replaceChild(docSource, bar, foo);

		assertEquals("", "document { element bar { } }", docSource, pcx);
	}
	
    public Resolver getResolver()
    {
        throw new UnsupportedOperationException("getResolver");
    }
}
