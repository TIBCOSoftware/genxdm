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
package org.genxdm.bridgetest.typed;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.GxTestBase;
import org.genxdm.bridgetest.xs.MyElementDeclaration;
import org.genxdm.bridgetest.xs.MyIntegerType;
import org.genxdm.names.NameSource;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

public abstract class SchemaTestBase<N, A> 
    extends GxTestBase<N>
{
	/**
	 * A sandbox for ad-hoc testing.
	 */
	public void testSchemaLessNamespaces()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final Set<String> namespaces = new HashSet<String>();
		int count = 0;
		for (final String namespace : pcx.getNamespaces())
		{
			count++;
			namespaces.add(namespace);
		}
		// Ensure that there is no redundancy.
		assertEquals(count, namespaces.size());

		assertTrue(namespaces.contains(XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertTrue(namespaces.contains(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(namespaces.contains(XMLConstants.XML_NS_URI));
		assertFalse(namespaces.contains("http://www.example.com"));
	}

	public void testSchemaLessAttributes()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final Map<QName, AttributeDefinition<A>> attributes = new HashMap<QName, AttributeDefinition<A>>();
		int count = 0;
		for (final AttributeDefinition<A> attribute : pcx.getAttributes())
		{
			count++;
			attributes.put(attribute.getName(), attribute);
		}
		// Ensure that there is no redundancy.
		assertEquals(6, attributes.size());
		assertTrue(attributes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "noNamespaceSchemaLocation")));
		assertTrue(attributes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation")));
		assertTrue(attributes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type")));
		assertTrue(attributes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil")));
		assertTrue(attributes.containsKey(new QName(XMLConstants.XML_NS_URI, "base")));
		assertTrue(attributes.containsKey(new QName(XMLConstants.XML_NS_URI, "lang")));
		assertFalse(attributes.containsKey(new QName("http://www.example.com", "foo")));
	}

	public void testSchemaLessElements()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final Map<QName, ElementDefinition<A>> elements = new HashMap<QName, ElementDefinition<A>>();
		for (final ElementDefinition<A> element : pcx.getElements())
		{
			elements.put(element.getName(), element);
		}
		// Ensure we have what we expect.
		assertEquals(0, elements.size());
	}

	public void testSchemaLessComplexTypes()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final Map<QName, ComplexType<A>> complexTypes = new HashMap<QName, ComplexType<A>>();
		int count = 0;
		for (final ComplexType<A> complexType : pcx.getComplexTypes())
		{
			complexTypes.put(complexType.getName(), complexType);
			count++;
		}
		// Ensure that there is no redundancy.
		assertEquals(count, complexTypes.size());
		// Ensure we have what we expect.
		assertEquals(2, complexTypes.size());
		assertTrue(complexTypes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType")));
		assertTrue(complexTypes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untyped")));
	}

	public void testSchemaLessSimpleTypes()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final Map<QName, SimpleType<A>> simpleTypes = new HashMap<QName, SimpleType<A>>();
		int count = 0;
		int anons = 0;
		for (final SimpleType<A> simpleType : pcx.getSimpleTypes())
		{
			simpleTypes.put(simpleType.getName(), simpleType);
			if (!simpleType.isAnonymous())
			{
				count++;
			}
			else
			{
				anons++;
			}
		}
		// Ensure that there is no redundancy.
		assertEquals(count + anons, simpleTypes.size());
		// Ensure we have what we expect.
		assertEquals(49, count);
		assertEquals(1, anons);
		assertEquals(50, simpleTypes.size());
		assertTrue(simpleTypes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType")));
		assertTrue(simpleTypes.containsKey(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyAtomicType")));
	}

	public void testDerivationByRestriction0001()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final MyIntegerType<A> atomicType = new MyIntegerType<A>(copy("http://www.example.com"), copy("myInteger"), pcx, atomBridge);
		// final AtomicType<A> atomicType = type;

		assertEquals(DerivationMethod.Restriction, atomicType.getDerivationMethod());
		// {name}
		assertEquals(copy("myInteger"), atomicType.getLocalName());
		// {target namespace}
		assertEquals(copy("http://www.example.com"), atomicType.getTargetNamespace());
		// Combined expanded-QName.
// fails.  prefix comparison?
//		assertEquals(nameBridge.name(new QName("http://www.example.com", "myInteger")), atomicType.getName());
		// {facets}
		assertTrue(atomicType.hasFacets());
		for (final Facet<A> facet : atomicType.getFacets())
		{
			switch (facet.getKind())
			{
				case MinInclusive:
				{
					final Limit<A> minInclusive = (Limit<A>)facet;
					final A limit = minInclusive.getLimit();
					assertNotNull(limit);
					assertTrue(atomBridge.getNativeType(limit).isInteger());
					assertEquals(BigInteger.valueOf(-2), atomBridge.getInteger(limit));
				}
				break;
				case MaxExclusive:
				{
					final Limit<A> maxExclusive = (Limit<A>)facet;
					final A limit = maxExclusive.getLimit();
					assertNotNull(limit);
					assertTrue(atomBridge.getNativeType(limit).isInteger());
					assertEquals(BigInteger.valueOf(5), atomBridge.getInteger(limit));
				}
				break;
				default:
				{
					throw new AssertionError(facet.getKind());
				}
			}
		}
		// {fundamental facets}
		// TODO: Does anyone care?
		// {final}
		final Set<DerivationMethod> f = atomicType.getFinal();
		assertNotNull(f);
		// {variety}
		assertTrue(atomicType.isAtomicType());
		assertFalse(atomicType.isListType());
		assertFalse(atomicType.isUnionType());

		// Now for something more interesting...
		try
		{
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(-2)));
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(-1)));
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(0)));
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(1)));
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(2)));
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(3)));
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(4)));
		}
		catch (final DatatypeException e)
		{
			fail();
		}
		try
		{
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(-3)));
			fail();
		}
		catch (final DatatypeException e)
		{
			// Expected
		}
		try
		{
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createInteger(5)));
			fail();
		}
		catch (final DatatypeException e)
		{
			// Expected
		}
		try
		{
			atomicType.validate(atomBridge.wrapAtom(atomBridge.createUntypedAtomic("-2")));
		}
		catch (final DatatypeException e)
		{
			fail();
		}
		try
		{
			atomicType.validate("-2");
		}
		catch (final DatatypeException e)
		{
			fail();
		}
		assertEquals("-2", atomicType.normalize("  -2              "));
	}

	/**
	 * Do anything to manufacture a String that is equal, but not identical (the same), as the original.
	 * <p>
	 * This method has the post-condition that the strings are equal but not the same.
	 * </p>
	 * 
	 * @param original
	 *            The original.
	 * @return A copy of the original string.
	 */
	private static String copy(final String original)
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

	public void testRegistration()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		final NameSource nameBridge = new NameSource();

		final ElementDefinition<A> e = new MyElementDeclaration<A>(nameBridge.empty(), "root", pcx, atomBridge);
		final SimpleType<A> st = new MyIntegerType<A>(copy("http://www.example.com"), copy("myInteger"), pcx, atomBridge);

		pcx.declareElement(e);
		pcx.defineSimpleType(st);
	}
}
