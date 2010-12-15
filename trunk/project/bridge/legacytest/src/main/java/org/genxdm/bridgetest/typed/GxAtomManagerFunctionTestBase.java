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
import org.genxdm.xs.types.NativeType;

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
		m_totalDigitTests.add(new TestData(NativeType.STRING, "1234567890", NativeType.INTEGER, "10", false));
		m_totalDigitTests.add(new TestData(NativeType.DECIMAL, "1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.DECIMAL, "1234567890.1", NativeType.INTEGER, "11", true));
		m_totalDigitTests.add(new TestData(NativeType.DECIMAL, "-0001234567890.10", NativeType.INTEGER, "11", true));
		m_totalDigitTests.add(new TestData(NativeType.DECIMAL, "1234567890.1000000000000000000000000", NativeType.INTEGER, "11", true));
		m_totalDigitTests.add(new TestData(NativeType.INTEGER, "1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.LONG, "1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.LONG, "-1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.INT, "-2147483648", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.INT, "2147483647", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.SHORT, "-32768", NativeType.INTEGER, "5", true));
		m_totalDigitTests.add(new TestData(NativeType.SHORT, "32767", NativeType.INTEGER, "5", true));
		m_totalDigitTests.add(new TestData(NativeType.BYTE, "127", NativeType.INTEGER, "3", true));
		m_totalDigitTests.add(new TestData(NativeType.BYTE, "-128", NativeType.INTEGER, "3", true));
		m_totalDigitTests.add(new TestData(NativeType.NON_POSITIVE_INTEGER, "-1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.NON_NEGATIVE_INTEGER, "1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.NEGATIVE_INTEGER, "-1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.POSITIVE_INTEGER, "1234567890", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.UNSIGNED_LONG, "18446744073709551615", NativeType.INTEGER, "20", true));
		m_totalDigitTests.add(new TestData(NativeType.UNSIGNED_INT, "4294967295", NativeType.INTEGER, "10", true));
		m_totalDigitTests.add(new TestData(NativeType.UNSIGNED_SHORT, "65535", NativeType.INTEGER, "5", true));
		m_totalDigitTests.add(new TestData(NativeType.UNSIGNED_BYTE, "255", NativeType.INTEGER, "3", true));
	}

	protected void setUpFractionDigitTest()
	{
		m_fractionDigitTests.add(new TestData(NativeType.STRING, "1234567890", NativeType.INTEGER, "0", false));
		m_fractionDigitTests.add(new TestData(NativeType.DECIMAL, "1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.DECIMAL, "1234567890.1", NativeType.INTEGER, "1", true));
		m_fractionDigitTests.add(new TestData(NativeType.DECIMAL, "-0001234567890.10", NativeType.INTEGER, "1", true));
		m_fractionDigitTests.add(new TestData(NativeType.DECIMAL, "1234567890.1000000000000000000000000", NativeType.INTEGER, "1", true));
		m_fractionDigitTests.add(new TestData(NativeType.INTEGER, "1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.LONG, "1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.LONG, "-1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.INT, "-2147483648", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.INT, "2147483647", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.SHORT, "-32768", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.SHORT, "32767", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.BYTE, "127", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.BYTE, "-128", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.NON_POSITIVE_INTEGER, "-1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.NON_NEGATIVE_INTEGER, "1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.NEGATIVE_INTEGER, "-1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.POSITIVE_INTEGER, "1234567890", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.UNSIGNED_LONG, "18446744073709551615", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.UNSIGNED_INT, "4294967295", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.UNSIGNED_SHORT, "65535", NativeType.INTEGER, "0", true));
		m_fractionDigitTests.add(new TestData(NativeType.UNSIGNED_BYTE, "255", NativeType.INTEGER, "0", true));
	}

	protected void setUpLengthTest()
	{
		m_lengthTests.add(new TestData(NativeType.STRING, "1234567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.NORMALIZED_STRING, "1234567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.TOKEN, "1234567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.LANGUAGE, "en", NativeType.INTEGER, "2", true));
		m_lengthTests.add(new TestData(NativeType.NAME, "Foo4567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.NCNAME, "SomeName", NativeType.INTEGER, "8", true));
		m_lengthTests.add(new TestData(NativeType.NMTOKEN, "SomeNmToken", NativeType.INTEGER, "11", true));
		m_lengthTests.add(new TestData(NativeType.ID, "Foo4567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.IDREF, "Foo4567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.ENTITY, "Foo4567890", NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, NativeType.INTEGER, "10", true));
		m_lengthTests.add(new TestData(NativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, NativeType.INTEGER, "8", true));
	}

	protected void setUpLengthUOMTest()
	{
		/* TODO: find out how to use enums are return type. */
		/*
		 * Meanwhile, we'll convert (as we do in the LengthUnitOfMeasureFunctionDynamic xpath fucntion) the
		 * GxLengthFacetUOM enum values to ints, like this: case Characters: 1 case ListItems: 2 case NotApplicable: 3
		 * case Octets: 4
		 */
		m_lengthUOMTests.add(new TestData(NativeType.STRING, "1234567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.NORMALIZED_STRING, "1234567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.TOKEN, "1234567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.LANGUAGE, "en", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.NAME, "foo4567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.NCNAME, "SomeName", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.NMTOKEN, "SomeNmToken", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.ID, "foo4567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.IDREF, "foo4567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.ENTITY, "foo4567890", NativeType.INTEGER, "1", true));
		m_lengthUOMTests.add(new TestData(NativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, NativeType.INTEGER, "4", true));
		m_lengthUOMTests.add(new TestData(NativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, NativeType.INTEGER, "4", true));
		m_lengthUOMTests.add(new TestData(NativeType.DECIMAL, "1234567890", NativeType.INTEGER, "3", true));

	}

	protected void setUpIsWhitespaceTest()
	{
		m_isWhitespaceTests.add(new TestData(NativeType.STRING, "1234567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.STRING, " ", NativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(NativeType.STRING, "\t", NativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(NativeType.STRING, "\r", NativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(NativeType.STRING, "\n", NativeType.BOOLEAN, "true", true));
		m_isWhitespaceTests.add(new TestData(NativeType.NORMALIZED_STRING, "1234567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.TOKEN, "1234567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.LANGUAGE, "en", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.NAME, "foo4567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.NCNAME, "SomeName", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.NMTOKEN, "SomeNmToken", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.ID, "foo4567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.IDREF, "foo4567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.ENTITY, "foo4567890", NativeType.BOOLEAN, "false", true));
		m_isWhitespaceTests.add(new TestData(NativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, NativeType.BOOLEAN, "false", false));
		m_isWhitespaceTests.add(new TestData(NativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, NativeType.BOOLEAN, "false", false));
		m_isWhitespaceTests.add(new TestData(NativeType.DECIMAL, "1234567890", NativeType.BOOLEAN, "false", false));

	}

	protected void setUpNormalizeTest()
	{
		m_normalizeTests.add(new TestData(NativeType.STRING, "1234567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.STRING, " 1234567890 ", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.STRING, "   ", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.STRING, "  \t  ", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.STRING, "  \r  ", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.STRING, "  \n  ", NativeType.STRING, null, true));

		m_normalizeTests.add(new TestData(NativeType.NORMALIZED_STRING, "1234567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.NORMALIZED_STRING, "\r1234567890", NativeType.STRING, " 1234567890", true));
		m_normalizeTests.add(new TestData(NativeType.NORMALIZED_STRING, "\r\t1234567890", NativeType.STRING, "  1234567890", true));
		m_normalizeTests.add(new TestData(NativeType.NORMALIZED_STRING, "\r \n1234567890", NativeType.STRING, "   1234567890", true));
		m_normalizeTests.add(new TestData(NativeType.TOKEN, "1234567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.TOKEN, "\r1234567890", NativeType.STRING, "1234567890", true));
		m_normalizeTests.add(new TestData(NativeType.TOKEN, "12  34567890", NativeType.STRING, "12 34567890", true));
		m_normalizeTests.add(new TestData(NativeType.LANGUAGE, "en", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.NAME, "foo4567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.NCNAME, "SomeName", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.NMTOKEN, "SomeNmToken", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.ID, "foo4567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.IDREF, "foo4567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.ENTITY, "foo4567890", NativeType.STRING, null, true));
		m_normalizeTests.add(new TestData(NativeType.HEX_BINARY, ""/* HexCodec.encodeHex("1-2-3".getBytes()) */, NativeType.STRING, null, false));
		m_normalizeTests.add(new TestData(NativeType.BASE64_BINARY, ""/* Base64Codec.encodeBase64("1-2-3") */, NativeType.STRING, null, false));
		m_normalizeTests.add(new TestData(NativeType.DECIMAL, "1234567890", NativeType.STRING, null, false));
	}

	// -------------------------------------------------------------------------
	// testing data setup & classes
	// -------------------------------------------------------------------------
	class TestData
	{
		public TestData(final NativeType srcType, final String srcValue, final NativeType resultType, final String resultValue, final boolean expectedSuccess)
		{
			m_srcType = srcType;
			m_srcValue = srcValue;
			m_resultType = resultType;
			m_resultValue = resultValue != null ? resultValue : srcValue;
			m_expectedSuccess = expectedSuccess;
		}

		final public NativeType m_srcType;
		final public String m_srcValue;
		final public NativeType m_resultType;
		final public String m_resultValue;
		final public boolean m_expectedSuccess;
	}
}
