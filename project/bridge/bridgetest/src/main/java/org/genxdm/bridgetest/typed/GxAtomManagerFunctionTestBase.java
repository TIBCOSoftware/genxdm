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

import java.util.ArrayList;

import org.genxdm.bridgetest.GxTestBase;
import org.genxdm.names.NameSource;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.xs.types.SmNativeType;

public abstract class GxAtomManagerFunctionTestBase<N, A, X> 
    extends GxTestBase<N>
{
	TypedContext<N, A> m_pcx;
	AtomBridge<A> m_atomBridge;
	NameSource m_nameBridge;
	MetaBridge<A> m_metaBridge;

	final private ArrayList<TestData> m_totalDigitTests = new ArrayList<TestData>();
	final private ArrayList<TestData> m_fractionDigitTests = new ArrayList<TestData>();
	final private ArrayList<TestData> m_lengthTests = new ArrayList<TestData>();
	final private ArrayList<TestData> m_lengthUOMTests = new ArrayList<TestData>();
	final private ArrayList<TestData> m_isWhitespaceTests = new ArrayList<TestData>();
	final private ArrayList<TestData> m_normalizeTests = new ArrayList<TestData>();

	protected void setUp() throws Exception
	{
        m_pcx = newProcessingContext().getTypedContext();
		m_atomBridge = m_pcx.getAtomBridge();
		m_nameBridge = m_atomBridge.getNameBridge();
		m_metaBridge = m_pcx.getMetaBridge();

		setUpTotalDigitTest();
		setUpFractionDigitTest();
		setUpLengthTest();
		setUpLengthUOMTest();
		setUpIsWhitespaceTest();
		setUpNormalizeTest();
	}

	public void testTotalDigits() throws Exception
	{
		for (@SuppressWarnings("unused") final TestData tdt : m_totalDigitTests)
		{
			// TODO
		}
	}

	public void testFractionDigits() throws Exception
	{
		for (@SuppressWarnings("unused") final TestData tdt : m_fractionDigitTests)
		{
			// TODO
		}
	}

	public void testLength() throws Exception
	{
		// int getLength(Atom)
		for (@SuppressWarnings("unused")
		final TestData tdt : m_lengthTests)
		{
			// TODO
		}
	}

	public void testLengthUOM() throws Exception
	{
		for (@SuppressWarnings("unused") final TestData tdt : m_lengthUOMTests)
		{
			// TODO
		}
	}

	public void testIsWhitespace() throws Exception
	{
		for (@SuppressWarnings("unused") final TestData tdt : m_isWhitespaceTests)
		{
			// TODO
		}
	}

	public void testNormalize() throws Exception
	{
		for (@SuppressWarnings("unused")
		final TestData tdt : m_normalizeTests)
		{
			// TODO
		}
	}

	// -------------------------------------------------------------------------
	// testing data setup & classes
	// -------------------------------------------------------------------------
	protected void setUpTotalDigitTest()
	{
		m_totalDigitTests.add(new TestData(SmNativeType.STRING, "1234567890", SmNativeType.INTEGER, "10", false));
		m_totalDigitTests.add(new TestData(SmNativeType.DECIMAL, "1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.DECIMAL, "1234567890.1", SmNativeType.INTEGER, "11", true));
		m_totalDigitTests.add(new TestData(SmNativeType.DECIMAL, "-0001234567890.10", SmNativeType.INTEGER, "11", true));
		m_totalDigitTests.add(new TestData(SmNativeType.DECIMAL, "1234567890.1000000000000000000000000", SmNativeType.INTEGER, "11", true));
		m_totalDigitTests.add(new TestData(SmNativeType.INTEGER, "1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.LONG, "1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.LONG, "-1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.INT, "-2147483648", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.INT, "2147483647", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.SHORT, "-32768", SmNativeType.INTEGER, "5", true));
		m_totalDigitTests.add(new TestData(SmNativeType.SHORT, "32767", SmNativeType.INTEGER, "5", true));
		m_totalDigitTests.add(new TestData(SmNativeType.BYTE, "127", SmNativeType.INTEGER, "3", true));
		m_totalDigitTests.add(new TestData(SmNativeType.BYTE, "-128", SmNativeType.INTEGER, "3", true));
		m_totalDigitTests.add(new TestData(SmNativeType.NON_POSITIVE_INTEGER, "-1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.NON_NEGATIVE_INTEGER, "1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.NEGATIVE_INTEGER, "-1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.POSITIVE_INTEGER, "1234567890", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.UNSIGNED_LONG, "18446744073709551615", SmNativeType.INTEGER, "20", true));
		m_totalDigitTests.add(new TestData(SmNativeType.UNSIGNED_INT, "4294967295", SmNativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(SmNativeType.UNSIGNED_SHORT, "65535", SmNativeType.INTEGER, "5", true));
		m_totalDigitTests.add(new TestData(SmNativeType.UNSIGNED_BYTE, "255", SmNativeType.INTEGER, "3", true));
	}

	protected void setUpFractionDigitTest()
	{
		m_fractionDigitTests.add(new TestData(SmNativeType.STRING, "1234567890", SmNativeType.INTEGER, "0", false));
		m_fractionDigitTests.add(new TestData(SmNativeType.DECIMAL, "1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.DECIMAL, "1234567890.1", SmNativeType.INTEGER, "1", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.DECIMAL, "-0001234567890.10", SmNativeType.INTEGER, "1", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.DECIMAL, "1234567890.1000000000000000000000000", SmNativeType.INTEGER, "1", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.INTEGER, "1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.LONG, "1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.LONG, "-1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.INT, "-2147483648", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.INT, "2147483647", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.SHORT, "-32768", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.SHORT, "32767", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.BYTE, "127", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.BYTE, "-128", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.NON_POSITIVE_INTEGER, "-1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.NON_NEGATIVE_INTEGER, "1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.NEGATIVE_INTEGER, "-1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.POSITIVE_INTEGER, "1234567890", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.UNSIGNED_LONG, "18446744073709551615", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.UNSIGNED_INT, "4294967295", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.UNSIGNED_SHORT, "65535", SmNativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(SmNativeType.UNSIGNED_BYTE, "255", SmNativeType.INTEGER, "0", true));
	}

	protected void setUpLengthTest()
	{
		m_lengthTests.add(new TestData(SmNativeType.STRING, "1234567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "1234567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.TOKEN, "1234567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.LANGUAGE, "en", SmNativeType.INTEGER, "2", true));
		m_lengthTests.add(new TestData(SmNativeType.NAME, "Foo4567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.NCNAME, "SomeName", SmNativeType.INTEGER, "8", true));
		m_lengthTests.add(new TestData(SmNativeType.NMTOKEN, "SomeNmToken", SmNativeType.INTEGER, "11", true));
		m_lengthTests.add(new TestData(SmNativeType.ID, "Foo4567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.IDREF, "Foo4567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.ENTITY, "Foo4567890", SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, SmNativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(SmNativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, SmNativeType.INTEGER, "8", true));
	}

	protected void setUpLengthUOMTest()
	{
		/* TODO: find out how to use enums are return type. */
		/*
		 * Meanwhile, we'll convert (as we do in the LengthUnitOfMeasureFunctionDynamic xpath fucntion) the
		 * GxLengthFacetUOM enum values to ints, like this: case Characters: 1 case ListItems: 2 case NotApplicable: 3
		 * case Octets: 4
		 */
		m_lengthUOMTests.add(new TestData(SmNativeType.STRING, "1234567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "1234567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.TOKEN, "1234567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.LANGUAGE, "en", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.NAME, "foo4567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.NCNAME, "SomeName", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.NMTOKEN, "SomeNmToken", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.ID, "foo4567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.IDREF, "foo4567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.ENTITY, "foo4567890", SmNativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, SmNativeType.INTEGER, "4", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, SmNativeType.INTEGER, "4", true));
		m_lengthUOMTests.add(new TestData(SmNativeType.DECIMAL, "1234567890", SmNativeType.INTEGER, "3", true));

	}

	protected void setUpIsWhitespaceTest()
	{
		m_isWhitespaceTests.add(new TestData(SmNativeType.STRING, "1234567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.STRING, " ", SmNativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.STRING, "\t", SmNativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.STRING, "\r", SmNativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.STRING, "\n", SmNativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "1234567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.TOKEN, "1234567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.LANGUAGE, "en", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.NAME, "foo4567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.NCNAME, "SomeName", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.NMTOKEN, "SomeNmToken", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.ID, "foo4567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.IDREF, "foo4567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.ENTITY, "foo4567890", SmNativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(SmNativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, SmNativeType.BOOLEAN, "false", false));
		m_isWhitespaceTests.add(new TestData(SmNativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, SmNativeType.BOOLEAN, "false", false));
		m_isWhitespaceTests.add(new TestData(SmNativeType.DECIMAL, "1234567890", SmNativeType.BOOLEAN, "false", false));

	}

	protected void setUpNormalizeTest()
	{
		m_normalizeTests.add(new TestData(SmNativeType.STRING, "1234567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.STRING, " 1234567890 ", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.STRING, "   ", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.STRING, "  \t  ", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.STRING, "  \r  ", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.STRING, "  \n  ", SmNativeType.STRING, null, true));

		m_normalizeTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "1234567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "\r1234567890", SmNativeType.STRING, " 1234567890", true));
		m_normalizeTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "\r\t1234567890", SmNativeType.STRING, "  1234567890", true));
		m_normalizeTests.add(new TestData(SmNativeType.NORMALIZED_STRING, "\r \n1234567890", SmNativeType.STRING, "   1234567890", true));
		m_normalizeTests.add(new TestData(SmNativeType.TOKEN, "1234567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.TOKEN, "\r1234567890", SmNativeType.STRING, "1234567890", true));
		m_normalizeTests.add(new TestData(SmNativeType.TOKEN, "12  34567890", SmNativeType.STRING, "12 34567890", true));
		m_normalizeTests.add(new TestData(SmNativeType.LANGUAGE, "en", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.NAME, "foo4567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.NCNAME, "SomeName", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.NMTOKEN, "SomeNmToken", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.ID, "foo4567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.IDREF, "foo4567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.ENTITY, "foo4567890", SmNativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(SmNativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, SmNativeType.STRING, null, false));
		m_normalizeTests.add(new TestData(SmNativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, SmNativeType.STRING, null, false));
		m_normalizeTests.add(new TestData(SmNativeType.DECIMAL, "1234567890", SmNativeType.STRING, null, false));
	}

	// -------------------------------------------------------------------------
	// testing data setup & classes
	// -------------------------------------------------------------------------
	class TestData
	{
		public TestData(final SmNativeType srcType, final String srcValue, final SmNativeType resultType, final String resultValue, final boolean expectedSuccess)
		{
			m_srcType = srcType;
			m_srcValue = srcValue;
			m_resultType = resultType;
			m_resultValue = resultValue != null ? resultValue : srcValue;
			m_expectedSuccess = expectedSuccess;
		}

		final public SmNativeType m_srcType;
		final public String m_srcValue;
		final public SmNativeType m_resultType;
		final public String m_resultValue;
		final public boolean m_expectedSuccess;
	}
}
