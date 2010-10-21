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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.names.NameSource;
import org.genxdm.xs.types.NativeType;

// TODO: this test should not be in bridgetest.
// at this point, it only tests NameSource.  Move it or lose it.
public abstract class NameBridgeTestBase<N> 
    extends GxTestBase<N>
{
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

	public void testEmpty()
	{
		final NameSource nameBridge = new NameSource();

		final String myEmptySymbol = " ".trim();

		{
			final String empty = nameBridge.empty();
			assertNotNull(empty);
			assertEquals("", empty.toString());
			assertTrue(nameBridge.isEmpty(empty));
			assertEquals(nameBridge.empty(), myEmptySymbol);
		}

		{
			final String empty = "";
			assertNotNull(empty);
			assertTrue(nameBridge.isEmpty(empty));
			assertEquals(empty, nameBridge.empty());
			assertEquals("", empty.toString());
		}
	}

	public void testNativeTypeConversion()
	{
		final NameSource nameBridge = new NameSource();

		for (final NativeType nativeType : NativeType.values())
		{
			switch (nativeType)
			{
			case IDREFS:
			case NMTOKENS:
			case ENTITIES:
			{

			}
				break;
			default:
			{
				assertEquals(nameBridge.nativeType(nativeType), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, nativeType.toQName()
						.getLocalPart()));
				assertEquals(nativeType.toQName().getLocalPart(), nativeType, nameBridge.nativeType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
						nativeType.toQName().getLocalPart())));
			}
			}
		}
		assertNull(nameBridge.nativeType(new QName("foo", "bar")));
	}

	public void testPrefix()
	{
		final NameSource nameBridge = new NameSource();

		{
			assertEquals("", nameBridge.getPrefix("", true));
			assertNull(nameBridge.getPrefix("", false));
			assertEquals("xml", nameBridge.getPrefix("http://www.w3.org/XML/1998/namespace", true));
			assertEquals("xml", nameBridge.getPrefix("http://www.w3.org/XML/1998/namespace", false));
			assertEquals("xmlns", nameBridge.getPrefix("http://www.w3.org/2000/xmlns/", true));
			assertEquals("xmlns", nameBridge.getPrefix("http://www.w3.org/2000/xmlns/", false));
			assertNull(nameBridge.getPrefix("foo", true));
			assertNull(nameBridge.getPrefix("foo", false));
		}
	}

	public void testProlog()
	{
		final NameSource nameBridge = new NameSource();

		assertNotNull(nameBridge);

		final String dog = copy("dog");
		assertEquals("dog", dog.toString());
	}

	/**
	 * We don't want the semantics of comparing object references to be thrown by <code>null>/code>.
	 */
	public void testSanity()
	{
		assertTrue(null == null);
	}

}
