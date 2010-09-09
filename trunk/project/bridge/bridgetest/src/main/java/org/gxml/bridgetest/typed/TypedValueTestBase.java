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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.gxml.Feature;
import org.gxml.base.ProcessingContext;
import org.gxml.bridgetest.GxTestBase;
import org.gxml.exceptions.GxmlException;
import org.gxml.names.NameSource;
import org.gxml.typed.TypedContext;
import org.gxml.typed.TypedModel;
import org.gxml.typed.io.SequenceBuilder;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.types.SmNativeType;

public abstract class TypedValueTestBase<N, A> 
    extends GxTestBase<N>
{
	public void test00001()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final TypedModel<N, A> model = pcx.getModel();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		final NameSource nameBridge = new NameSource();

		final N document = makeDocument(ctx);

		final N root = model.getFirstChildElement(document);
		{
			if (ctx.isSupported(Feature.TYPED_VALUE))
			{
				final List<A> value = makeList(model.getValue(root));
				assertEquals(1, value.size());
				final A atom = value.get(0);
				final QName dataType = atomBridge.getDataType(atom);
				assertEquals("1.23E2", atomBridge.getC14NForm(atom));
				assertEquals("double", dataType.getLocalPart().toString());
			}
			else
			{
				final String value = model.getStringValue(root);
				assertEquals("1.23E2", value);
			}
		}

		final N attribute = model.getAttribute(root, nameBridge.empty(), "a");
		{
			if (ctx.isSupported(Feature.TYPED_VALUE))
			{
				final List<A> value = makeList(model.getValue(attribute));
				assertEquals(1, value.size());
				final A atom = value.get(0);
				final QName dataType = atomBridge.getDataType(atom);
				assertEquals("1.0E0", atomBridge.getC14NForm(atom));
				assertEquals("double", dataType.getLocalPart().toString());
			}
			else
			{
				final String value = model.getStringValue(attribute);
				assertEquals("1.0E0", value);
			}
		}
		assertEquals("1.0E0", model.getAttributeStringValue(root, nameBridge.empty(), "a"));
		{
			if (ctx.isSupported(Feature.TYPED_VALUE))
			{
				final List<A> value = makeList(model.getAttributeValue(root, nameBridge.empty(), "a"));
				assertEquals(1, value.size());
				final A atom = value.get(0);
				final QName dataType = atomBridge.getDataType(atom);
				assertEquals("1.0E0", atomBridge.getC14NForm(atom));
				assertEquals("double", dataType.getLocalPart().toString());
			}
			else
			{
				final List<A> value = makeList(model.getAttributeValue(root, nameBridge.empty(), "a"));
				assertEquals(1, value.size());
				final A atom = value.get(0);
				final QName dataType = atomBridge.getDataType(atom);
				assertEquals("1.0E0", atomBridge.getC14NForm(atom));
				assertEquals("untypedAtomic", dataType.getLocalPart().toString());
			}
		}
		// TODO: Enable...
		// assertEquals("double", model.getAttributeTypeName(root, nameBridge.empty(),
		// nameBridge.symbolize("a")).toQName().getLocalPart());
		// The following should not blow up because the calls are transitive.
		assertNull(model.getAttributeStringValue(root, nameBridge.empty(), "b"));
		assertNull(model.getAttributeValue(root, nameBridge.empty(), "b"));
		if (ctx.isSupported(Feature.TYPE_ANNOTATION))
		{
			assertNull(model.getAttributeTypeName(root, nameBridge.empty(), "b"));
		}
	}

	private N makeDocument(final ProcessingContext<N> pcx)
	{
	    final TypedContext<N, A> tc = pcx.getTypedContext();
		final SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
		final AtomBridge<A> atomBridge = tc.getAtomBridge();
		final NameSource nameBridge = new NameSource();
		try
		{
			builder.startDocument(new URI("http://www.minimal.com"), null);
			try
			{
				builder.startElement(nameBridge.empty(), "root", XMLConstants.DEFAULT_NS_PREFIX, nameBridge.nativeType(SmNativeType.DOUBLE));
				try
				{
					builder.attribute(nameBridge.empty(), "a", XMLConstants.DEFAULT_NS_PREFIX, atomBridge.wrapAtom(atomBridge.createDouble(1)), nameBridge.nativeType(SmNativeType.DOUBLE));
					builder.text(atomBridge.wrapAtom(atomBridge.createDouble(123)));
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
	
	private List<A> makeList(Iterable<? extends A> iterable)
	{
	    ArrayList<A> list = new ArrayList<A>();
	    for (A item : iterable)
	    {
	        list.add(item);
	    }
	    return list;
	}
}