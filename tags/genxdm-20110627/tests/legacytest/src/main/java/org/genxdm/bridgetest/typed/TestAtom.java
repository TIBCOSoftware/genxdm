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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.genxdm.bridgetest.WhiteSpaceMangler;
import org.genxdm.xs.types.NativeType;

/**
 * The class members are primarily a data structure for representing an atom. There are also static methods which create
 * a collection of these atoms for more convenient testing:
 * <ul>
 * <li>getTestAtoms()
 * <li>getTestAtoms(UberType, boolean)
 * </ul>
 */
final class TestAtom
{
	public static final String NOT_APPLICABLE = "N/A";
	public final NativeType builtInType;
	public final String strVal;
	public final boolean isValid;
	public final String c14nValue;

	public TestAtom(final NativeType uType, final String lexical, final boolean validity, final String canonical)
	{
		builtInType = uType;
		strVal = lexical;
		isValid = validity;
		if (canonical == null && !validity)
		{
			this.c14nValue = NOT_APPLICABLE;
		}
		else
		{
			this.c14nValue = canonical;
		}
	}

	public TestAtom(final NativeType uType, final String value, final boolean validity)
	{
		this(uType, value, validity, value);
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(builtInType.toQName()).append("; ");
		sb.append("value = \"").append(strVal).append("\"").append("; ");
		if (isValid)
		{
			sb.append("valid");
		}
		else
		{
			sb.append("invalid");
		}
		return sb.toString();
	}

	static private ArrayList<TestAtom> c_testAtoms;

	// -------------------------------------------------------------------------
	// public, static methods
	// -------------------------------------------------------------------------
	public static Iterable<TestAtom> getTestAtoms()
	{
		if (c_testAtoms != null)
		{
			return c_testAtoms;
		}
		c_testAtoms = new ArrayList<TestAtom>();

		// xs:anyURI
		c_testAtoms.add(new TestAtom(NativeType.ANY_URI, "http://www.example.com", true, "http://www.example.com"));

		// xs:base64Binary
		final String canonical;
		try
		{
			canonical = Base64Codec.encodeBase64("1-2-3", true);
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new AssertionError(e);
		}
		final String lexical = WhiteSpaceMangler.collapseWhiteSpace(canonical);
		c_testAtoms.add(new TestAtom(NativeType.BASE64_BINARY, lexical, true, canonical));

		// xs:hexBinary
		final String encoded = HexCodec.encodeHex("1-2-3".getBytes());
		c_testAtoms.add(new TestAtom(NativeType.HEX_BINARY, encoded, true, encoded));

		// xs:boolean
		c_testAtoms.add(new TestAtom(NativeType.BOOLEAN, "1", true, "true"));
		c_testAtoms.add(new TestAtom(NativeType.BOOLEAN, "0", true, "false"));
		c_testAtoms.add(new TestAtom(NativeType.BOOLEAN, "true", true, "true"));
		c_testAtoms.add(new TestAtom(NativeType.BOOLEAN, "false", true, "false"));
		c_testAtoms.add(new TestAtom(NativeType.BOOLEAN, "invalid", false));

		// xs:byte
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "-128", true, "-128"));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "127", true, "127"));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "27", true, "27"));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "-34", true, "-34"));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "+105", true, "105"));

		c_testAtoms.add(new TestAtom(NativeType.BYTE, "-129", false));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "128", false));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "0A", false));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "1524", false));
		c_testAtoms.add(new TestAtom(NativeType.BYTE, "INF", false));

		// xs:date
		c_testAtoms.add(new TestAtom(NativeType.DATE, "2001-10-26", true, "2001-10-26"));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "2001-10-26+02:00", true, "2001-10-26+02:00"));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "2001-10-26Z", true, "2001-10-26Z"));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "2001-10-26+00:00", true, "2001-10-26Z"));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "-2001-10-26", true, "-2001-10-26"));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "-20000-04-01", true, "-20000-04-01"));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "2002-13-10", false));
		c_testAtoms.add(new TestAtom(NativeType.DATE, "2002-02-29", false));

		// xs:dateTime
		c_testAtoms.add(new TestAtom(NativeType.DATETIME, "2001-10-26T21:32:52", true, "2001-10-26T21:32:52"));
		c_testAtoms.add(new TestAtom(NativeType.DATETIME, "2002-10-10T12:00:00-05:00", true));
		c_testAtoms.add(new TestAtom(NativeType.DATETIME, "2002-10-10T17:00:00Z", true));
		c_testAtoms.add(new TestAtom(NativeType.DATETIME, "2002-10-10T12:00:00Z", true));

		// xs:dayTimeDuration
		c_testAtoms.add(new TestAtom(NativeType.DURATION_DAYTIME, "PT0S", true, "PT0S"));
		c_testAtoms.add(new TestAtom(NativeType.DURATION_DAYTIME, "P3DT10H30M", true));

		// xs:decimal
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "1.23456789012345678901234567890", true, "1.2345678901234567890123456789"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "5.0", true, "5.0"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "1.0", true, "1.0"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "0.0", true, "0.0"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "-1.0", true, "-1.0"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "-5.0", true, "-5.0"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "0", true, "0.0"));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "0.01", true, "0.01"));
		String maxDoubleValue = "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0";
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, maxDoubleValue, true, maxDoubleValue));
		c_testAtoms.add(new TestAtom(NativeType.DECIMAL, "foo", false));

		// xs:double
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), true, "1.7976931348623157E308"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), true, "4.9E-324"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "123.456", true, "1.23456E2"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "+1234.456", true, "1.234456E3"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "-1.2344e56", true, "-1.2344E56"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "-.45E-6", true, "-4.5E-7"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "INF", true, "INF"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "-INF", true, "-INF"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "NaN", true, "NaN"));

		// DOUBLE
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "-1E4", true, "-1.0E4"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "1267.43233E12", true, "1.26743233E15"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "12.78e-2", true, "1.278E-1"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "0", true, "0.0E0"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "-0", true, "0.0E0"));
		c_testAtoms.add(new TestAtom(NativeType.DOUBLE, "-0.0", true, "0.0E0"));

		// xs:duration
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "PT130S", true, "PT2M10S"));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "PT2M10S", true, "PT2M10S"));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "-P1Y", true, "-P1Y"));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P1Y2M3DT5H20M30.123S", true, "P1Y2M3DT5H20M30.123S"));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "1Y", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P1S", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P-1Y", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P1M2Y", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P1Y-1M", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "1Y2M3DT10H30M", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P1Y2M3DT10H30M", true));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P120D", true));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "P-1347M", false));
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "-P1347M", true, "-P112Y3M"));

		// JDK implementation blows up with this value. What to do?
		// TODO: our own implementation returns "P11622DT16H10M59S" -- is that correct?
		c_testAtoms.add(new TestAtom(NativeType.DURATION, "PT1004199059S", true, "P11622DT16H10M59S")); // "PT1004199059S"));

		// xs:ENTITY
		c_testAtoms.add(new TestAtom(NativeType.ENTITY, "Snoopy", true, "Snoopy"));
		c_testAtoms.add(new TestAtom(NativeType.ENTITY, "CMS", true, "CMS"));
		c_testAtoms.add(new TestAtom(NativeType.ENTITY, "_1950-10-04_10-00", true, "_1950-10-04_10-00"));
		c_testAtoms.add(new TestAtom(NativeType.ENTITY, "083621742", false));
		c_testAtoms.add(new TestAtom(NativeType.ENTITY, "_1950-10-04_10:00", false));
		c_testAtoms.add(new TestAtom(NativeType.ENTITY, "bold:brash", false));

		// xs:float
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), true, "3.4028235E38"));
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), true, "1.4E-45"));
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, "123.456", true, "1.23456E2"));
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, "+1234.456", true, "1.234456E3"));
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, Double.toString(Double.MAX_VALUE), false));
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, Double.toString(Double.MIN_VALUE), true, "0.0E0"));
		c_testAtoms.add(new TestAtom(NativeType.FLOAT, "1267.43233E12", true, "1.26743237E15"));

		// xs:gDay
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "---01", true, "---01"));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "---21", true));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "21", false));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "---01", true));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "---1", false));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "---41", false));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "0", false));
		c_testAtoms.add(new TestAtom(NativeType.GDAY, "33", false));

		// xs:gMonth
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "--05", true, "--05"));
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "--12", true));
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "0", false));
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "--13", false));
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "12", false));
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "--1", false));
		c_testAtoms.add(new TestAtom(NativeType.GMONTH, "--01", true));

		// xs:gMonthDay
		c_testAtoms.add(new TestAtom(NativeType.GMONTHDAY, "--05-01", true, "--05-01"));
		c_testAtoms.add(new TestAtom(NativeType.GMONTHDAY, "--10-10", true));
		c_testAtoms.add(new TestAtom(NativeType.GMONTHDAY, "--10-32", false));

		// xs:gYear
		c_testAtoms.add(new TestAtom(NativeType.GYEAR, "2001", true, "2001"));
		c_testAtoms.add(new TestAtom(NativeType.GYEAR, "1999", true));
		c_testAtoms.add(new TestAtom(NativeType.GYEAR, "19990", true));
		c_testAtoms.add(new TestAtom(NativeType.GYEAR, "-1999", true));
		c_testAtoms.add(new TestAtom(NativeType.GYEAR, "-19990", true));

		// xs:gYearMonth
		c_testAtoms.add(new TestAtom(NativeType.GYEARMONTH, "2001-10", true, "2001-10"));
		c_testAtoms.add(new TestAtom(NativeType.GYEARMONTH, "1999-05", true));
		c_testAtoms.add(new TestAtom(NativeType.GYEARMONTH, "1999-13", false));
		c_testAtoms.add(new TestAtom(NativeType.GYEARMONTH, "1999-00", false));

		// xs:ID
		c_testAtoms.add(new TestAtom(NativeType.ID, "Snoopy", true, "Snoopy"));
		c_testAtoms.add(new TestAtom(NativeType.ID, "CMS", true, "CMS"));
		c_testAtoms.add(new TestAtom(NativeType.ID, "_1950-10-04_10-00", true, "_1950-10-04_10-00"));
		c_testAtoms.add(new TestAtom(NativeType.ID, "083621742", false));
		c_testAtoms.add(new TestAtom(NativeType.ID, "_1950-10-04_10:00", false));
		c_testAtoms.add(new TestAtom(NativeType.ID, "bold:brash", false));

		// xs:IDREF
		c_testAtoms.add(new TestAtom(NativeType.IDREF, "Snoopy", true, "Snoopy"));
		c_testAtoms.add(new TestAtom(NativeType.IDREF, "CMS", true, "CMS"));
		c_testAtoms.add(new TestAtom(NativeType.IDREF, "_1950-10-04_10-00", true, "_1950-10-04_10-00"));
		c_testAtoms.add(new TestAtom(NativeType.IDREF, "083621742", false));
		c_testAtoms.add(new TestAtom(NativeType.IDREF, "_1950-10-04_10:00", false));
		c_testAtoms.add(new TestAtom(NativeType.IDREF, "bold:brash", false));

		// xs:int
		c_testAtoms.add(new TestAtom(NativeType.INT, "-2147483648", true, "-2147483648"));
		c_testAtoms.add(new TestAtom(NativeType.INT, "-1", true, "-1"));
		c_testAtoms.add(new TestAtom(NativeType.INT, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.INT, "1", true, "1"));
		c_testAtoms.add(new TestAtom(NativeType.INT, "2147483647", true, "2147483647"));
		c_testAtoms.add(new TestAtom(NativeType.INT, "-2147483649", false));
		c_testAtoms.add(new TestAtom(NativeType.INT, "2147483648", false));

		// xs:integer
		c_testAtoms.add(new TestAtom(NativeType.INTEGER, "1", true, "1"));
		c_testAtoms.add(new TestAtom(NativeType.INTEGER, "1.2", false));

		// xs:language
		c_testAtoms.add(new TestAtom(NativeType.LANGUAGE, "en", true, "en"));
		c_testAtoms.add(new TestAtom(NativeType.LANGUAGE, "en-US", true, "en-US"));
		c_testAtoms.add(new TestAtom(NativeType.LANGUAGE, "fr", true, "fr"));
		c_testAtoms.add(new TestAtom(NativeType.LANGUAGE, "fr-FR", true, "fr-FR"));

		// xs:long
		c_testAtoms.add(new TestAtom(NativeType.LONG, "1", true, "1"));
		c_testAtoms.add(new TestAtom(NativeType.LONG, "-9223372036854775809", false));
		c_testAtoms.add(new TestAtom(NativeType.LONG, "-9223372036854775808", true, "-9223372036854775808"));
		c_testAtoms.add(new TestAtom(NativeType.LONG, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.LONG, "9223372036854775807", true, "9223372036854775807"));
		c_testAtoms.add(new TestAtom(NativeType.LONG, "9223372036854775808", false));

		// xs:Name
		c_testAtoms.add(new TestAtom(NativeType.NAME, "Snoopy", true, "Snoopy"));
		c_testAtoms.add(new TestAtom(NativeType.NAME, "CMS", true, "CMS"));
		c_testAtoms.add(new TestAtom(NativeType.NAME, "_1950-10-04_10:00", true, "_1950-10-04_10:00"));
		c_testAtoms.add(new TestAtom(NativeType.NAME, "083621742", false));
		c_testAtoms.add(new TestAtom(NativeType.NAME, "bold,brash", false));

		// xs:NCName
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "Snoopy", true, "Snoopy"));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "CMS", true, "CMS"));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "_1950-10-04_10-00", true, "_1950-10-04_10-00"));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "083621742", false));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "_1950-10-04_10:00", false));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "bold:brash", false));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "#ncname", false));
		c_testAtoms.add(new TestAtom(NativeType.NCNAME, "ncname value", false));

		// xs:negativeInteger
		c_testAtoms.add(new TestAtom(NativeType.NEGATIVE_INTEGER, "-123456789012345678901234567890", true, "-123456789012345678901234567890"));
		c_testAtoms.add(new TestAtom(NativeType.NEGATIVE_INTEGER, "-1", true, "-1"));
		c_testAtoms.add(new TestAtom(NativeType.NEGATIVE_INTEGER, "-000000000000000000000005", true, "-5"));
		c_testAtoms.add(new TestAtom(NativeType.NEGATIVE_INTEGER, "0", false));
		c_testAtoms.add(new TestAtom(NativeType.NEGATIVE_INTEGER, "1", false));

		// xs:NMTOKEN
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "Snoopy", true, "Snoopy"));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "CMS", true, "CMS"));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "1950-10-04", true, "1950-10-04"));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "0836217462", true, "0836217462"));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "brought classical music to the Peanuts strip", false));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "bold, brash", false));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "nmtoken cannot have whitespace", false));
		c_testAtoms.add(new TestAtom(NativeType.NMTOKEN, "nmtoken_can_use_underscores", true));

		// xs:nonNegativeInteger
		c_testAtoms.add(new TestAtom(NativeType.NON_NEGATIVE_INTEGER, "-1", false));
		c_testAtoms.add(new TestAtom(NativeType.NON_NEGATIVE_INTEGER, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.NON_NEGATIVE_INTEGER, "1", true, "1"));
		c_testAtoms.add(new TestAtom(NativeType.NON_NEGATIVE_INTEGER, "18446744073709551616", true));

		// xs:nonPositiveInteger
		c_testAtoms.add(new TestAtom(NativeType.NON_POSITIVE_INTEGER, "-1", true, "-1"));
		c_testAtoms.add(new TestAtom(NativeType.NON_POSITIVE_INTEGER, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.NON_POSITIVE_INTEGER, "1", false));
		c_testAtoms.add(new TestAtom(NativeType.NON_POSITIVE_INTEGER, "-18446744073709551616", true, "-18446744073709551616"));

		// xs:normalizedString
		c_testAtoms.add(new TestAtom(NativeType.NORMALIZED_STRING, "\t normalized\n  string\r   ", true, "  normalized   string    "));
		c_testAtoms.add(new TestAtom(NativeType.NORMALIZED_STRING, "normalized string", true));
		c_testAtoms.add(new TestAtom(NativeType.NORMALIZED_STRING, "normalized \n string", true, "normalized   string")); // not
		// valid,
		// but
		// whitespace
		// should
		// be
		// replaced
		c_testAtoms.add(new TestAtom(NativeType.NORMALIZED_STRING, "normalized \r string", true, "normalized   string")); // not
		// valid,
		// but
		// whitespace
		// should
		// be
		// replaced
		c_testAtoms.add(new TestAtom(NativeType.NORMALIZED_STRING, "normalized \t string", true, "normalized   string")); // not
		// valid,
		// but
		// whitespace
		// should
		// be
		// replaced

		// NOTATION
		// TODO: add valid notation test
		// c_testAtoms.add(new TestAtom(UberType.NOTATION, "1", true));

		// xs:positiveInteger
		c_testAtoms.add(new TestAtom(NativeType.POSITIVE_INTEGER, "-1", false));
		c_testAtoms.add(new TestAtom(NativeType.POSITIVE_INTEGER, "0", false));
		c_testAtoms.add(new TestAtom(NativeType.POSITIVE_INTEGER, "1", true, "1"));
		c_testAtoms.add(new TestAtom(NativeType.POSITIVE_INTEGER, "18446744073709551615", true));
		c_testAtoms.add(new TestAtom(NativeType.POSITIVE_INTEGER, "18446744073709551616", true));

		// xs:QName
		// TODO: xs:QName a bit problematic, but this should work (James Clark notation).
		// c_testAtoms.add(new TestAtom(UberType.QNAME, "{".concat(XMLConstants.XML_NS_URI).concat("}").concat("space"),
		// true, "xml:space"));

		// xs:short
		c_testAtoms.add(new TestAtom(NativeType.SHORT, "1", true, "1"));
		c_testAtoms.add(new TestAtom(NativeType.SHORT, "-32769", false));
		c_testAtoms.add(new TestAtom(NativeType.SHORT, "-32768", true, "-32768"));
		c_testAtoms.add(new TestAtom(NativeType.SHORT, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.SHORT, "32767", true, "32767"));
		c_testAtoms.add(new TestAtom(NativeType.SHORT, "32768", false));

		// xs:string
		c_testAtoms.add(new TestAtom(NativeType.STRING, "some string", true, "some string"));

		// xs:time
		c_testAtoms.add(new TestAtom(NativeType.TIME, "21:32:52", true, "21:32:52"));
		c_testAtoms.add(new TestAtom(NativeType.TIME, "12:00:00-05:00", true));
		c_testAtoms.add(new TestAtom(NativeType.TIME, "17:00:00Z", true));
		c_testAtoms.add(new TestAtom(NativeType.TIME, "12:00:00Z", true));

		// xs:token
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token", true, "token"));
		// The following are not valid token values, but whitespace should be removed by implementation.
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token\n", true, "token"));
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token\t", true, "token"));
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token\r", true, "token"));
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, " token", true, "token"));
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token ", true, "token"));
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token token", true, "token token"));
		c_testAtoms.add(new TestAtom(NativeType.TOKEN, "token     token", true, "token token"));

		// xs:unsignedByte
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_BYTE, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_BYTE, "255", true, "255"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_BYTE, "-1", false));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_BYTE, "256", false));

		// xs:unsignedInt
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_INT, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_INT, "4294967295", true, "4294967295"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_INT, "-1", false));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_INT, "4294967296", false));

		// xs:unsignedLong
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_LONG, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_LONG, "18446744073709551615", true, "18446744073709551615"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_LONG, "-1", false));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_LONG, "18446744073709551616", false));

		// xs:unsignedShort
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_SHORT, "0", true, "0"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_SHORT, "65535", true, "65535"));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_SHORT, "-1", false));
		c_testAtoms.add(new TestAtom(NativeType.UNSIGNED_SHORT, "65536", false));

		// xs:untypedAtomic
		c_testAtoms.add(new TestAtom(NativeType.UNTYPED_ATOMIC, "some value", true, "some value"));

		// xs:yearMonthDuration
		c_testAtoms.add(new TestAtom(NativeType.DURATION_YEARMONTH, "P4Y", true, "P4Y"));
		c_testAtoms.add(new TestAtom(NativeType.DURATION_YEARMONTH, "P1Y2M", true));
		c_testAtoms.add(new TestAtom(NativeType.DURATION_YEARMONTH, "P2M", true));

		return c_testAtoms;
	}

	public static Iterable<TestAtom> includeTestAtoms(final NativeType[] includedDataTypes, boolean onlyValid, boolean onlyInvalid)
	{
		final ArrayList<TestAtom> filteredAtoms = new ArrayList<TestAtom>();

		for (final TestAtom candidate : getTestAtoms())
		{
			if (isInSet(includedDataTypes, candidate.builtInType))
			{
				if ((!onlyValid && !onlyInvalid) || (onlyValid && candidate.isValid) || (onlyInvalid && !candidate.isValid))
				{
					filteredAtoms.add(candidate);
				}
			}
		}
		return filteredAtoms;
	}

	public static Iterable<TestAtom> excludeTestAtoms(final NativeType[] excludedDataTypes, boolean onlyValid, boolean onlyInvalid)
	{
		final ArrayList<TestAtom> filteredAtoms = new ArrayList<TestAtom>();

		for (final TestAtom candidate : getTestAtoms())
		{
			if (!isInSet(excludedDataTypes, candidate.builtInType))
			{
				if ((!onlyValid && !onlyInvalid) || (onlyValid && candidate.isValid) || (onlyInvalid && !candidate.isValid))
				{
					filteredAtoms.add(candidate);
				}
			}
		}
		return filteredAtoms;
	}

	private static boolean isInSet(final NativeType[] theSet, final NativeType testValue)
	{
		for (NativeType uType : theSet)
		{
			if (uType == testValue)
			{
				return true;
			}
		}
		return false;
	}
}
