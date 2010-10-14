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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;

import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceBinding;

public abstract class NamespaceBindTestBase<N> 
    extends GxTestBase<N>
{
	public void test00001()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = makeDocument(pcx);

		final Model<N> model = pcx.getModel();

		final N root = model.getFirstChild(D0);
		final Iterator<String> names = model.getNamespaceNames(root, true).iterator();
		assertTrue(names.hasNext());
		final String a = names.next();
		assertEquals("a", a);
		assertTrue(names.hasNext());
		final String b = names.next();
		assertEquals("b", b);
		assertTrue(names.hasNext());
		final String c = names.next();
		assertEquals("c", c);
		assertFalse(names.hasNext());
	}

	public void test00002()
	{
		final ProcessingContext<N> pcx = newProcessingContext();

		final N D0 = makeDocument(pcx);

		final Model<N> model = pcx.getModel();

		final N root = model.getFirstChild(D0);

		final Map<String, String> map = new HashMap<String, String>();

		for (final NamespaceBinding binding : model.getNamespaceBindings(root))
		{
			map.put(binding.getPrefix(), binding.getNamespaceURI());
		}

		assertEquals(3, map.size());
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
		assertEquals("http://www.Y.com", map.get("a"));
		assertEquals("http://www.Z.com", map.get("b"));
		assertEquals("http://www.X.com", map.get("c"));
	}

	private N makeDocument(final ProcessingContext<N> pcx)
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
					builder.namespace("c", "http://www.X.com");
					builder.namespace("a", "http://www.Y.com");
					builder.namespace("b", "http://www.Z.com");
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