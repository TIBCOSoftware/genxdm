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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.gxml.base.ProcessingContext;
import org.gxml.bridgetest.GxTestBase;
import org.gxml.exceptions.GxmlAtomCastException;
import org.gxml.exceptions.SpillagePolicy;
import org.gxml.typed.TypedContext;
import org.gxml.typed.types.AtomBridge;
import org.gxml.typed.types.CastingContext;
import org.gxml.typed.types.Emulation;
import org.gxml.xs.types.SmNativeType;

/**
 * TODO: This should be expanded for all native types.
 */
public abstract class ForeignAtomTestBase<N, A> 
    extends GxTestBase<N>
{
	private CastingContext<A> castingContext()
	{
		return new CastingContext<A>()
		{
			public Emulation getEmulation()
			{
				return Emulation.C14N;
			}

			public SpillagePolicy getSpillagePolicy()
			{
				return SpillagePolicy.DO_THE_RIGHT_THING;
			}
		};
	}

	public void checkBoolean(final boolean value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createBoolean(value);
		assertEquals(SmNativeType.BOOLEAN, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.BOOLEAN, atomBridge.getNativeType(derivedAtom));
		assertEquals(value, atomBridge.getBoolean(derivedAtom));

		assertEquals(value, atomBridge.getBoolean(derivedAtom));

		assertEquals(atomBridge.createBoolean(value), atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.BOOLEAN, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkByte(final byte value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createByte(value);
		assertEquals(SmNativeType.BYTE, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.BYTE, atomBridge.getNativeType(shoeSize));

		assertEquals(value, atomBridge.getByte(shoeSize));

		assertEquals(atomBridge.createByte(value), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.BYTE, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkDateTime(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createDateTime(2009, 10, 18, 8, 3, 19, 0, BigDecimal.ZERO, 0);
		assertEquals(SmNativeType.DATETIME, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.DATETIME, atomBridge.getNativeType(derivedAtom));
		assertEquals("2009-10-18T08:03:19Z", atomBridge.getC14NForm(derivedAtom));
		assertEquals(atomBridge.getYear(nativeAtom), atomBridge.getYear(derivedAtom));
		assertEquals(atomBridge.getMonth(nativeAtom), atomBridge.getMonth(derivedAtom));
		assertEquals(atomBridge.getDayOfMonth(nativeAtom), atomBridge.getDayOfMonth(derivedAtom));
		assertEquals(atomBridge.getHourOfDay(nativeAtom), atomBridge.getHourOfDay(derivedAtom));
		assertEquals(atomBridge.getMinute(nativeAtom), atomBridge.getMinute(derivedAtom));
		assertEquals(atomBridge.getIntegralSecondPart(nativeAtom), atomBridge.getIntegralSecondPart(derivedAtom));
		assertEquals(atomBridge.getFractionalSecondPart(nativeAtom), atomBridge.getFractionalSecondPart(derivedAtom));
		assertEquals(atomBridge.getSecondsAsBigDecimal(nativeAtom), atomBridge.getSecondsAsBigDecimal(derivedAtom));
		assertEquals(atomBridge.getGmtOffset(nativeAtom), atomBridge.getGmtOffset(derivedAtom));

		assertEquals(atomBridge.createDateTime(2009, 10, 18, 8, 3, 19, 0, BigDecimal.ZERO, 0), atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.DATETIME, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkDecimal(final BigDecimal value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createDecimal(value);
		assertEquals(SmNativeType.DECIMAL, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.DECIMAL, atomBridge.getNativeType(shoeSize));

		assertEquals(value, atomBridge.getDecimal(shoeSize));

		assertEquals(atomBridge.getC14NForm(number), atomBridge.getC14NForm(shoeSize));
		assertEquals(atomBridge.getXPath10Form(number), atomBridge.getXPath10Form(shoeSize));
		assertEquals(atomBridge.getXQuery10Form(number), atomBridge.getXQuery10Form(shoeSize));

		assertEquals(atomBridge.createDecimal(value), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.DECIMAL, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkDouble(final double value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createDouble(value);
		assertEquals(SmNativeType.DOUBLE, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.DOUBLE, atomBridge.getNativeType(shoeSize));

		assertEquals(value, atomBridge.getDouble(shoeSize));

		assertEquals(atomBridge.getC14NForm(number), atomBridge.getC14NForm(shoeSize));
		assertEquals(atomBridge.getXPath10Form(number), atomBridge.getXPath10Form(shoeSize));
		assertEquals(atomBridge.getXQuery10Form(number), atomBridge.getXQuery10Form(shoeSize));

		assertEquals(atomBridge.createDouble(value), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.DOUBLE, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkDuration(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createDuration(3, BigDecimal.valueOf(13));
		assertEquals(SmNativeType.DURATION, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.DURATION, atomBridge.getNativeType(derivedAtom));

		assertEquals(atomBridge.getDurationTotalMonths(nativeAtom), atomBridge.getDurationTotalMonths(derivedAtom));
		assertEquals(atomBridge.getDurationTotalSeconds(nativeAtom), atomBridge.getDurationTotalSeconds(derivedAtom));

		assertEquals(atomBridge.getC14NForm(nativeAtom), atomBridge.getC14NForm(derivedAtom));
		assertEquals(atomBridge.getXPath10Form(nativeAtom), atomBridge.getXPath10Form(derivedAtom));
		assertEquals(atomBridge.getXQuery10Form(nativeAtom), atomBridge.getXQuery10Form(derivedAtom));

		assertEquals(nativeAtom, atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));
		assertEquals(atomBridge.isWhiteSpace(nativeAtom), atomBridge.isWhiteSpace(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.DURATION, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkFloat(final float value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createFloat(value);
		assertEquals(SmNativeType.FLOAT, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.FLOAT, atomBridge.getNativeType(shoeSize));

		assertEquals(value, atomBridge.getFloat(shoeSize));

		assertEquals(atomBridge.getC14NForm(number), atomBridge.getC14NForm(shoeSize));
		assertEquals(atomBridge.getXPath10Form(number), atomBridge.getXPath10Form(shoeSize));
		assertEquals(atomBridge.getXQuery10Form(number), atomBridge.getXQuery10Form(shoeSize));

		assertEquals(atomBridge.createFloat(value), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.FLOAT, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkInt(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createInt(10);
		assertEquals(SmNativeType.INT, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.INT, atomBridge.getNativeType(shoeSize));

		final int x = atomBridge.getInt(shoeSize);

		assertEquals(x, 10);

		assertEquals(atomBridge.createInt(10), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.INT, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkInteger(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createInteger(10);
		assertEquals(SmNativeType.INTEGER, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.INTEGER, atomBridge.getNativeType(shoeSize));
		assertEquals("10", atomBridge.getC14NForm(shoeSize));

		final BigInteger x = atomBridge.getInteger(shoeSize);

		assertEquals(x, BigInteger.TEN);

		assertEquals(atomBridge.createInteger(10), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.INTEGER, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkIntegerDerived(final BigInteger value, final SmNativeType nativeType, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createIntegerDerived(value, nativeType);
		assertEquals(nativeType, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(nativeType, atomBridge.getNativeType(shoeSize));

		final BigInteger x = atomBridge.getInteger(shoeSize);

		assertEquals(value, x);

		assertEquals(atomBridge.createIntegerDerived(value, nativeType), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, nativeType, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkLong(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createLong(10);
		assertEquals(SmNativeType.LONG, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.LONG, atomBridge.getNativeType(shoeSize));

		final long x = atomBridge.getLong(shoeSize);

		assertEquals(x, 10);

		assertEquals(atomBridge.createLong(10), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.LONG, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkQName(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createQName("http://www.x.com", "foo", "x");
		assertEquals(SmNativeType.QNAME, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.QNAME, atomBridge.getNativeType(derivedAtom));
		assertEquals("x:foo", atomBridge.getC14NForm(derivedAtom));

		assertEquals(atomBridge.getNamespaceFromQName(nativeAtom), atomBridge.getNamespaceFromQName(derivedAtom));
		assertEquals(atomBridge.getLocalNameFromQName(nativeAtom), atomBridge.getLocalNameFromQName(derivedAtom));
		assertEquals(atomBridge.getPrefixFromQName(nativeAtom), atomBridge.getPrefixFromQName(derivedAtom));
		assertEquals(atomBridge.getQName(nativeAtom), atomBridge.getQName(derivedAtom));

		final QName x = atomBridge.getQName(derivedAtom);

		assertEquals(new QName("http://www.x.com", "foo", "x"), x);

		assertEquals(atomBridge.createQName("http://www.x.com", "foo", "x"), atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.QNAME, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkNOTATION(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createNOTATION("http://www.x.com", "foo", "x");
		assertEquals(SmNativeType.NOTATION, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.NOTATION, atomBridge.getNativeType(derivedAtom));
		assertEquals("x:foo", atomBridge.getC14NForm(derivedAtom));

		assertEquals(atomBridge.getNotation(nativeAtom), atomBridge.getNotation(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));
	}

	public void checkShort(final short value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createShort(value);
		assertEquals(SmNativeType.SHORT, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.SHORT, atomBridge.getNativeType(shoeSize));

		assertEquals(value, atomBridge.getShort(shoeSize));

		assertEquals(atomBridge.createShort(value), atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.SHORT, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkHexBinary(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createHexBinary("Hello".getBytes());
		assertEquals(SmNativeType.HEX_BINARY, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.HEX_BINARY, atomBridge.getNativeType(derivedAtom));

		assertEquals(atomBridge.getHexBinary(nativeAtom), atomBridge.getHexBinary(derivedAtom));
		assertEquals(nativeAtom, atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));
		assertEquals(atomBridge.isWhiteSpace(nativeAtom), atomBridge.isWhiteSpace(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.HEX_BINARY, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkBase64Binary(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createBase64Binary("Hello".getBytes());
		assertEquals(SmNativeType.BASE64_BINARY, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.BASE64_BINARY, atomBridge.getNativeType(derivedAtom));

		assertEquals(atomBridge.getBase64Binary(nativeAtom), atomBridge.getBase64Binary(derivedAtom));
		assertEquals(nativeAtom, atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));
		assertEquals(atomBridge.isWhiteSpace(nativeAtom), atomBridge.isWhiteSpace(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.BASE64_BINARY, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkUnsignedShort(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createIntegerDerived(5, SmNativeType.UNSIGNED_SHORT);
		assertEquals(SmNativeType.UNSIGNED_SHORT, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.UNSIGNED_SHORT, atomBridge.getNativeType(shoeSize));

		assertEquals(5, atomBridge.getUnsignedShort(shoeSize));

		assertEquals(number, atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.UNSIGNED_SHORT, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkUnsignedInt(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createIntegerDerived(5, SmNativeType.UNSIGNED_INT);
		assertEquals(SmNativeType.UNSIGNED_INT, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.UNSIGNED_INT, atomBridge.getNativeType(shoeSize));

		assertEquals(5, atomBridge.getUnsignedInt(shoeSize));

		assertEquals(number, atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.UNSIGNED_INT, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkUnsignedByte(final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A number = atomBridge.createIntegerDerived(5, SmNativeType.UNSIGNED_BYTE);
		assertEquals(SmNativeType.UNSIGNED_BYTE, atomBridge.getNativeType(number));

		final A shoeSize = atomBridge.makeForeignAtom(new QName("shoe-size"), number);

		assertEquals(SmNativeType.UNSIGNED_BYTE, atomBridge.getNativeType(shoeSize));

		assertEquals(5, atomBridge.getUnsignedByte(shoeSize));

		assertEquals(number, atomBridge.upCast(shoeSize));

		assertFalse(atomBridge.isForeignAtom(number));
		assertTrue(atomBridge.isForeignAtom(shoeSize));
		assertEquals(atomBridge.isWhiteSpace(number), atomBridge.isWhiteSpace(shoeSize));

		try
		{
			final A actual = atomBridge.castAs(shoeSize, SmNativeType.UNSIGNED_BYTE, castingContext());
			assertEquals(number.getClass() + ".equals(" + actual.getClass() + ")", number, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkString(final String value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createString(value);
		assertEquals(SmNativeType.STRING, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.STRING, atomBridge.getNativeType(derivedAtom));
		assertEquals(value, atomBridge.getString(derivedAtom));
		assertEquals(value, atomBridge.getC14NForm(derivedAtom));
		assertEquals(value, atomBridge.getXPath10Form(derivedAtom));
		assertEquals(value, atomBridge.getXQuery10Form(derivedAtom));

		assertEquals(value, atomBridge.getString(derivedAtom));

		assertEquals(atomBridge.createString(value), atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.STRING, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void checkURI(final URI value, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A nativeAtom = atomBridge.createURI(value);
		assertEquals(SmNativeType.ANY_URI, atomBridge.getNativeType(nativeAtom));

		final A derivedAtom = atomBridge.makeForeignAtom(new QName("derived"), nativeAtom);

		assertEquals(SmNativeType.ANY_URI, atomBridge.getNativeType(derivedAtom));
		assertEquals(value, atomBridge.getURI(derivedAtom));

		assertEquals(atomBridge.createURI(value), atomBridge.upCast(derivedAtom));

		assertFalse(atomBridge.isForeignAtom(nativeAtom));
		assertTrue(atomBridge.isForeignAtom(derivedAtom));

		try
		{
			final A actual = atomBridge.castAs(derivedAtom, SmNativeType.ANY_URI, castingContext());
			assertEquals(nativeAtom.getClass() + ".equals(" + actual.getClass() + ")", nativeAtom, actual);
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testAll()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		checkInteger(pcx);
		checkLong(pcx);
		checkInt(pcx);

		checkShort((short)+1, pcx);
		checkShort((short)0, pcx);
		checkShort((short)-1, pcx);

		checkByte((byte)+1, pcx);
		checkByte((byte)0, pcx);
		checkByte((byte)-1, pcx);

		checkString("Hello", pcx);

		checkQName(pcx);
		checkDuration(pcx);

		checkBoolean(true, pcx);
		checkBoolean(false, pcx);

		checkDouble(+1, pcx);
		checkDouble(0, pcx);
		checkDouble(-1, pcx);

		checkFloat(+1, pcx);
		checkFloat(0, pcx);
		checkFloat(-1, pcx);

		checkDecimal(BigDecimal.ZERO, pcx);
		checkDecimal(BigDecimal.ONE, pcx);
		checkDecimal(BigDecimal.TEN, pcx);

		checkIntegerDerived(BigInteger.valueOf(-1), SmNativeType.NON_POSITIVE_INTEGER, pcx);
		checkIntegerDerived(BigInteger.valueOf(-1), SmNativeType.NEGATIVE_INTEGER, pcx);
		checkIntegerDerived(BigInteger.valueOf(+1), SmNativeType.UNSIGNED_LONG, pcx);
		checkIntegerDerived(BigInteger.valueOf(+1), SmNativeType.UNSIGNED_INT, pcx);
		checkIntegerDerived(BigInteger.valueOf(+1), SmNativeType.UNSIGNED_SHORT, pcx);
		checkIntegerDerived(BigInteger.valueOf(+1), SmNativeType.UNSIGNED_BYTE, pcx);
		checkIntegerDerived(BigInteger.valueOf(+1), SmNativeType.NON_NEGATIVE_INTEGER, pcx);
		checkIntegerDerived(BigInteger.valueOf(+1), SmNativeType.POSITIVE_INTEGER, pcx);

		checkUnsignedShort(pcx);
		checkUnsignedInt(pcx);
		checkUnsignedByte(pcx);

		try
		{
			checkURI(new URI("http://www.example.com"), pcx);
		}
		catch (final URISyntaxException e)
		{
			throw new AssertionError();
		}

		checkDateTime(pcx);
		checkBase64Binary(pcx);
		checkHexBinary(pcx);
		checkNOTATION(pcx);
	}
}