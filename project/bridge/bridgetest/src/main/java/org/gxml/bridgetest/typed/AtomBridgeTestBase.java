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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.gxml.bridgetest.GxTestBase;
import org.gxml.exceptions.GxmlAtomCastException;
import org.gxml.exceptions.PreCondition;
import org.gxml.exceptions.SpillagePolicy;
import org.gxml.names.NameSource;
import org.gxml.typed.TypedContext;
import org.gxml.typed.types.AtomBridge;
import org.gxml.typed.types.CastingContext;
import org.gxml.typed.types.Emulation;
import org.gxml.xs.types.SmNativeType;

/**
 * Unit Testing for {@link AtomBridge}.
 */
public abstract class AtomBridgeTestBase<N, A> 
    extends GxTestBase<N> 
    implements CastingContext<A>
{
	private static final String DEFAULT_MASTER_CAST_FILE = "masterCast.lst";
	private static final String METHOD_CREATE_UNTYPED_ATOMIC = "createUntypedAtomic(String)";
	/**
	 * {@link AtomBridge} methods.
	 */
	private static final String METHOD_GET_NAME_BRIDGE = "getNameBridge()";

	private static final String METHOD_WRAP_ATOM = "wrapAtom(A)";

	private static <A> void assertCastFail(final SmNativeType sourceType, final String sourceString, final SmNativeType targetType, final QName code, final CastingContext<A> castingContext, final AtomBridge<A> atomBridge)
	{
		PreCondition.assertArgumentNotNull(code, "code");
		final String message = sourceType.toQName().getLocalPart() + "('" + sourceString + "') -> " + targetType.toQName().getLocalPart();

		try
		{
			final A sourceAtom = atomBridge.compile(sourceString, sourceType);
			try
			{
				atomBridge.castAs(sourceAtom, targetType, castingContext);

				fail(message);
			}
			catch (final GxmlAtomCastException e)
			{
				final NameSource nameBridge = atomBridge.getNameBridge();
				assertEquals(message, nameBridge.nativeType(targetType), e.getTargetType());
				assertEquals(message, code, e.getErrorCode());
			}
		}
		catch (final GxmlAtomCastException e)
		{
			fail(message);
		}
	}

	private static <A> void assertCastGood(final SmNativeType sourceType, final String sourceString, final SmNativeType targetType, final String targetString, final CastingContext<A> castingContext, final AtomBridge<A> atomBridge)
	{
		final String message = sourceType.toQName().getLocalPart() + "('" + sourceString + "') -> " + targetType.toQName().getLocalPart() + "('" + targetString + "')";

		try
		{
			final A sourceAtom = atomBridge.compile(sourceString, sourceType);
			try
			{
				final A targetAtom = atomBridge.castAs(sourceAtom, targetType, castingContext);

				final String actual = atomBridge.getC14NForm(targetAtom);
				if (!TestAtom.NOT_APPLICABLE.equals(targetString))
				{
					if (!targetString.equals(actual))
					{
						assertEquals(message, "<" + targetString + ">", "<" + actual + ">");
					}
				}
				assertEquals(message, targetType, atomBridge.getNativeType(targetAtom));
			}
			catch (final GxmlAtomCastException e)
			{
				fail(message + " : " + e.getMessage());
			}
		}
		catch (final GxmlAtomCastException e)
		{
			fail(message);
		}
	}

	/**
	 * Type-safe comparison of {@link SmNativeType}.
	 */
	private static boolean equals(SmNativeType one, SmNativeType two)
	{
		return one == two;
	}

	private void assertManagerLookupByAtom(final SmNativeType nativeType, final String strval, final AtomBridge<A> atomBridge)
	{
		try
		{
			atomBridge.compile(strval, nativeType);
		}
		catch (final GxmlAtomCastException e)
		{
			throw new AssertionError(e.getMessage());
		}
	}

    public void DO_NOT_testCasting()
	{
		final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		// Read the master file, which contains lists of test files.
		try
		{
			File file = new File(DEFAULT_MASTER_CAST_FILE);
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);

			// Read each test file.
			String testFileName = reader.readLine();
			while (testFileName != null)
			{
				System.out.println("testFileName = " + testFileName);
				testFileName = testFileName.trim();
				if (!testFileName.startsWith("#"))
				{
					BufferedReader testReader = new BufferedReader(new FileReader(testFileName));
					String testParams = testReader.readLine().trim();
					while (testParams != null)
					{
						System.out.println("   testParams = " + testParams);
						testParams = testParams.trim();
						if (!testParams.startsWith("#"))
						{
							// Get the comma separated tokens: sourceType, sourceValue, targetType, y || n, targetValue
							// || expected errorCode
							StringTokenizer tokenizer = new StringTokenizer(testParams, ",", false);
							String sourceType = tokenizer.nextToken().trim();
							String sourceValue = makeValue(tokenizer.nextToken().trim());
							String targetType = tokenizer.nextToken().trim();
							boolean expectedSuccess = "Y".equalsIgnoreCase(tokenizer.nextToken().trim());
							String targetValue = null;
							String errorCode = null;
							if (expectedSuccess)
							{
								targetValue = makeValue(tokenizer.nextToken().trim());
							}
							else
							{
								errorCode = makeValue(tokenizer.nextToken().trim());
							}
							performCastTest(pcx, sourceType, sourceValue, targetType, expectedSuccess, targetValue, errorCode);
						}
						testParams = testReader.readLine();
					}
					testReader.close();
				}
				testFileName = reader.readLine();
			}
			reader.close();
		}
		catch (FileNotFoundException ex)
		{
			fail("FileNotFoundException: " + ex.getMessage());
		}
		catch (IOException ex)
		{
			fail("IOException: " + ex.getMessage());
		}
	}

	private QName Err(final String localName)
	{
		return new QName("http://www.w3.org/2005/xqt-errors/", localName, "err");
	}

	public A getCurrentDateTime()
	{
		// If you need thsi functionality, implement your own casting context.
		// This is just here to be lazy.
		// You also can't create a xs:dateTime because it won't be in the right processing context.
		throw new AssertionError("TODO");
	}

	public Emulation getEmulation()
	{
		return Emulation.C14N;
	}

	public SpillagePolicy getSpillagePolicy()
	{
		return SpillagePolicy.SWEEP_UNDER_THE_RUG;
	}

	private String makeValue(String sourceValue)
	{
		int firstQuote = sourceValue.indexOf('\"');
		if (firstQuote >= 0)
		{
			int lastQuote = sourceValue.lastIndexOf('\"');
			if (lastQuote > firstQuote)
			{
				return sourceValue.substring(firstQuote + 1, lastQuote);
			}
			else
			{
				fail("Invalid test parameter value: " + sourceValue);
			}
		}
		return sourceValue;
	}

	private void performCastTest(TypedContext<N, A> pcx, String sourceType, String sourceValue, String targetType, boolean expectedSuccess, String targetValue, String errorCode)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		assertNotNull(atomBridge);

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		// Get the UberTypes
		final SmNativeType targetBuiltInType = SmNativeType.getType(targetType);

		// Create the source atom.
		A sourceAtom = null;
		try
		{
			sourceAtom = atomBridge.compile(sourceValue, SmNativeType.getType(sourceType));
		}
		catch (GxmlAtomCastException e)
		{
			fail("Unable to compile source atom: " + e.getMessage());
		}

		try
		{
			final A targetAtom = atomBridge.castAs(sourceAtom, targetBuiltInType, this);

			if (expectedSuccess)
			{
				final SmNativeType resultType = atomBridge.getNativeType(targetAtom);
				if (resultType != targetBuiltInType)
				{
					// Failed.
					fail("Cast resulted in unexpected atomic type: " + sourceType + ", " + sourceValue + ", " + targetType + ", " + resultType);
				}

				// Check the value.
				final String resultValue = atomBridge.getC14NForm(targetAtom);
				if ((targetValue == null && resultValue != null) || !targetValue.equals(resultValue))
				{
					// Failed.
					fail("Cast resulted in unexpected result: " + sourceType + ", " + sourceValue + ", " + targetType + ", " + resultValue);
				}
			}
			else
			{
				fail("Expected cast to fail, but it did not: " + sourceType + ", " + sourceValue + ", " + targetType);
			}
		}
		catch (GxmlAtomCastException ex)
		{
			if (expectedSuccess)
			{
				// Failed.
				fail("Unexpected cast failure: " + ex.getMessage());
			}
			// An expected failure. Check the error code.
			if (ex.getErrorCode() != null)
			{
				QName errorCodeQName = Err(errorCode);
				if (ex.getErrorCode().equals(errorCodeQName))
				{
					// Ok
				}
				else
				{
					// Failed.
					fail("Expected cast exception, but incorrect error code:  expected \"" + errorCodeQName + "\", but got \"" + ex.getErrorCode());
				}
			}
		}
	}

	public void testAccess()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		{
			final String original = "Hello";

			final A atom = atomBridge.createString(original);
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertFalse(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertFalse(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertFalse(nativeType.isA(SmNativeType.NAME));
			assertFalse(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Hello World";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.NORMALIZED_STRING, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertFalse(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertFalse(nativeType.isA(SmNativeType.NAME));
			assertFalse(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Hello World";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.TOKEN, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertFalse(nativeType.isA(SmNativeType.NAME));
			assertFalse(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "en";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.LANGUAGE, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertTrue(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertFalse(nativeType.isA(SmNativeType.NAME));
			assertFalse(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.NMTOKEN, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertTrue(nativeType.isA(SmNativeType.NMTOKEN));
			assertFalse(nativeType.isA(SmNativeType.NAME));
			assertFalse(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.NAME, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertTrue(nativeType.isA(SmNativeType.NAME));
			assertFalse(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.NCNAME, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertTrue(nativeType.isA(SmNativeType.NAME));
			assertTrue(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.ID, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertTrue(nativeType.isA(SmNativeType.NAME));
			assertTrue(nativeType.isA(SmNativeType.NCNAME));
			assertTrue(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.IDREF, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertTrue(nativeType.isA(SmNativeType.NAME));
			assertTrue(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertTrue(nativeType.isA(SmNativeType.IDREF));
			assertFalse(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), SmNativeType.ENTITY, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.STRING));
			assertTrue(nativeType.isA(SmNativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(SmNativeType.TOKEN));
			assertFalse(nativeType.isA(SmNativeType.LANGUAGE));
			assertFalse(nativeType.isA(SmNativeType.NMTOKEN));
			assertTrue(nativeType.isA(SmNativeType.NAME));
			assertTrue(nativeType.isA(SmNativeType.NCNAME));
			assertFalse(nativeType.isA(SmNativeType.ID));
			assertFalse(nativeType.isA(SmNativeType.IDREF));
			assertTrue(nativeType.isA(SmNativeType.ENTITY));

			assertFalse(nativeType.isA(SmNativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(SmNativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(SmNativeType.ANY_TYPE));
		}
		{
			final byte original = (byte)123;

			final A atom = atomBridge.createByte(original);

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(SmNativeType.BYTE));
			assertTrue(nativeType.isA(SmNativeType.SHORT));
			assertTrue(nativeType.isA(SmNativeType.INT));
			assertTrue(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(Byte.valueOf(original).byteValue(), atomBridge.getByte(atom));
			assertEquals(Short.valueOf(original).shortValue(), atomBridge.getShort(atom));
			assertEquals(Integer.valueOf(original).intValue(), atomBridge.getInt(atom));
			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final short original = (short)123;

			final A atom = atomBridge.createShort(original);

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertTrue(nativeType.isA(SmNativeType.SHORT));
			assertTrue(nativeType.isA(SmNativeType.INT));
			assertTrue(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(Short.valueOf(original).shortValue(), atomBridge.getShort(atom));
			assertEquals(Integer.valueOf(original).intValue(), atomBridge.getInt(atom));
			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final int original = (int)123;

			final A atom = atomBridge.createInt(original);

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertTrue(nativeType.isA(SmNativeType.INT));
			assertTrue(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(Integer.valueOf(original).intValue(), atomBridge.getInt(atom));
			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)123;

			final A atom = atomBridge.createLong(original);

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertTrue(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), SmNativeType.POSITIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(SmNativeType.POSITIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), SmNativeType.NON_NEGATIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(SmNativeType.NON_NEGATIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)-123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), SmNativeType.NON_POSITIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(SmNativeType.NON_POSITIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)-123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), SmNativeType.NEGATIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(SmNativeType.NEGATIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), SmNativeType.UNSIGNED_BYTE, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(SmNativeType.UNSIGNED_BYTE, nativeType);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), SmNativeType.UNSIGNED_SHORT, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), SmNativeType.UNSIGNED_INT, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), SmNativeType.UNSIGNED_LONG, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final BigInteger original = BigInteger.valueOf(123);

			final A atom = atomBridge.createInteger(123);

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertTrue(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(original, atomBridge.getInteger(atom));
			assertEquals(new BigDecimal(original), atomBridge.getDecimal(atom));
		}
		{
			final BigDecimal original = BigDecimal.valueOf(123);

			final A atom = atomBridge.createDecimal(original);

			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(SmNativeType.BYTE));
			assertFalse(nativeType.isA(SmNativeType.SHORT));
			assertFalse(nativeType.isA(SmNativeType.INT));
			assertFalse(nativeType.isA(SmNativeType.LONG));
			assertFalse(nativeType.isA(SmNativeType.INTEGER));
			assertTrue(nativeType.isA(SmNativeType.DECIMAL));

			assertEquals(original, atomBridge.getDecimal(atom));
		}
	}

	public void testBase64Binary()
	{
	}

	public void testBoolean()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertEquals("true", atomBridge.getC14NForm(atomBridge.createBoolean(true)));
		assertEquals("false", atomBridge.getC14NForm(atomBridge.createBoolean(false)));
		assertTrue(atomBridge.getBoolean(atomBridge.createBoolean(true)));
		assertFalse(atomBridge.getBoolean(atomBridge.createBoolean(false)));

		assertTrue(atomBridge.getBoolean(atomBridge.getBooleanTrue()));
		assertFalse(atomBridge.getBoolean(atomBridge.getBooleanFalse()));
	}

	public void testCastingFromDecimal()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "+0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "0.0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "0.0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.STRING, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.STRING, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.STRING, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.STRING, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.UNTYPED_ATOMIC, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.UNTYPED_ATOMIC, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.UNTYPED_ATOMIC, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.UNTYPED_ATOMIC, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.INTEGER, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3.1456", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.INTEGER, "-17", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.DECIMAL, "3", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(SmNativeType.DECIMAL, "3.1456", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.NON_POSITIVE_INTEGER, "-17", this, atomBridge);

		assertCastFail(SmNativeType.DECIMAL, "0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.DECIMAL, "-0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.DECIMAL, "3", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(SmNativeType.DECIMAL, "3.1456", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.NEGATIVE_INTEGER, "-17", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.LONG, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3.1456", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.LONG, "-17", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.INT, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3.1456", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.INT, "-17", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.SHORT, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3.1456", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.SHORT, "-17", this, atomBridge);

		assertCastGood(SmNativeType.DECIMAL, "0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-3", SmNativeType.BYTE, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "3.1456", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.DECIMAL, "-17.89", SmNativeType.BYTE, "-17", this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(SmNativeType.DECIMAL, source.builtInType) && source.isValid)
			{
				for (final TestAtom target : TestAtom.getTestAtoms())
				{
					switch (target.builtInType)
					{
						case DOUBLE:
						case FLOAT:
						case DECIMAL:
						case INTEGER:
						case NON_POSITIVE_INTEGER:
						case NEGATIVE_INTEGER:
						case LONG:
						case INT:
						case SHORT:
						case BYTE:
						case NON_NEGATIVE_INTEGER:
						case UNSIGNED_LONG:
						case UNSIGNED_INT:
						case UNSIGNED_SHORT:
						case UNSIGNED_BYTE:
						case POSITIVE_INTEGER:

						case BOOLEAN:
						case UNTYPED_ATOMIC:
						case STRING:
						case NORMALIZED_STRING:
						case TOKEN:
						case LANGUAGE:
						case NAME:
						case NMTOKEN:
						case NCNAME:
						case ID:
						case IDREF:
						case ENTITY:
						{
							// Handled explicitly.
						}
						break;
						default:
						{
							assertCastFail(source.builtInType, source.strVal, target.builtInType, Err("XPTY0004"), this, atomBridge);
						}
					}
				}
			}
		}
	}

	public void testCastingFromDouble()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.DOUBLE, "1.7976931348623157E308", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.DOUBLE, "4.9E-324", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "NaN", SmNativeType.DOUBLE, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-INF", SmNativeType.DOUBLE, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "INF", SmNativeType.DOUBLE, "INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.FLOAT, "INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "NaN", SmNativeType.FLOAT, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-INF", SmNativeType.FLOAT, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "INF", SmNativeType.FLOAT, "INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "NaN", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-INF", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "INF", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "+0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0.0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0.0E0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.STRING, "1.7976931348623157E308", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.STRING, "4.9E-324", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "NaN", SmNativeType.STRING, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-INF", SmNativeType.STRING, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "INF", SmNativeType.STRING, "INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.STRING, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.STRING, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.UNTYPED_ATOMIC, "1.7976931348623157E308", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.UNTYPED_ATOMIC, "4.9E-324", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "NaN", SmNativeType.UNTYPED_ATOMIC, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-INF", SmNativeType.UNTYPED_ATOMIC, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "INF", SmNativeType.UNTYPED_ATOMIC, "INF", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.UNTYPED_ATOMIC, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.UNTYPED_ATOMIC, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.DECIMAL, "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.DECIMAL, "0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.INTEGER, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3.1456", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.INTEGER, "-17", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.INTEGER, "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "3", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "3.1456", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.NON_POSITIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastFail(SmNativeType.DOUBLE, "0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "3", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "3.1456", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.NEGATIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.LONG, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3.1456", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.LONG, "-17", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.LONG, "9223372036854775807", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.LONG, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.LONG, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.INT, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3.1456", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.INT, "-17", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.INT, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.INT, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.INT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.SHORT, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3.1456", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.SHORT, "-17", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.SHORT, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.SHORT, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.SHORT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.DOUBLE, "0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-3", SmNativeType.BYTE, "-3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "3.1456", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, "-17.89", SmNativeType.BYTE, "-17", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, Double.toString(Double.MAX_VALUE), SmNativeType.BYTE, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.DOUBLE, Double.toString(Double.MIN_VALUE), SmNativeType.BYTE, "0", this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "NaN", SmNativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "-INF", SmNativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.DOUBLE, "INF", SmNativeType.BYTE, Err("FOCA0002"), this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(SmNativeType.DOUBLE, source.builtInType) && source.isValid)
			{
				for (final TestAtom target : TestAtom.getTestAtoms())
				{
					switch (target.builtInType)
					{
						case DOUBLE:
						case FLOAT:
						case DECIMAL:
						case INTEGER:
						case NON_POSITIVE_INTEGER:
						case NEGATIVE_INTEGER:
						case LONG:
						case INT:
						case SHORT:
						case BYTE:
						case NON_NEGATIVE_INTEGER:
						case UNSIGNED_LONG:
						case UNSIGNED_INT:
						case UNSIGNED_SHORT:
						case UNSIGNED_BYTE:
						case POSITIVE_INTEGER:

						case BOOLEAN:
						case UNTYPED_ATOMIC:
						case STRING:
						case NORMALIZED_STRING:
						case TOKEN:
						case LANGUAGE:
						case NAME:
						case NMTOKEN:
						case NCNAME:
						case ID:
						case IDREF:
						case ENTITY:
						{
							// Handled explicitly.
						}
						break;
						default:
						{
							assertCastFail(source.builtInType, source.strVal, target.builtInType, Err("XPTY0004"), this, atomBridge);
						}
					}
				}
			}
		}
	}

	public void testCastingFromFloat()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.DOUBLE, "3.4028234663852886E38", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.DOUBLE, "1.401298464324817E-45", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "NaN", SmNativeType.DOUBLE, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-INF", SmNativeType.DOUBLE, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "INF", SmNativeType.DOUBLE, "INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.FLOAT, "3.4028235E38", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.FLOAT, "1.4E-45", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "NaN", SmNativeType.FLOAT, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-INF", SmNativeType.FLOAT, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "INF", SmNativeType.FLOAT, "INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "NaN", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-INF", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "INF", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "+0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0.0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0.0E0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.STRING, "3.4028235E38", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.STRING, "1.4E-45", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "NaN", SmNativeType.STRING, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-INF", SmNativeType.STRING, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "INF", SmNativeType.STRING, "INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.STRING, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.STRING, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.UNTYPED_ATOMIC, "3.4028235E38", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.UNTYPED_ATOMIC, "1.4E-45", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "NaN", SmNativeType.UNTYPED_ATOMIC, "NaN", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-INF", SmNativeType.UNTYPED_ATOMIC, "-INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "INF", SmNativeType.UNTYPED_ATOMIC, "INF", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.UNTYPED_ATOMIC, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.UNTYPED_ATOMIC, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.DECIMAL, "340282346638528860000000000000000000000.0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.DECIMAL, "0.000000000000000000000000000000000000000000001401298464324817", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.INTEGER, "-3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3.1456", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.INTEGER, "-17", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.INTEGER, "9223372036854775807", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "3", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "3.1456", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.NON_POSITIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastFail(SmNativeType.FLOAT, "0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "3", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "3.1456", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.NEGATIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.LONG, "-3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3.1456", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.LONG, "-17", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.INT, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.INT, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.LONG, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.INT, "-3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3.1456", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.INT, "-17", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.INT, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.INT, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.INT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.SHORT, "-3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3.1456", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.SHORT, "-17", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.SHORT, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.SHORT, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.SHORT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(SmNativeType.FLOAT, "0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-3", SmNativeType.BYTE, "-3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "3.1456", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, "-17.89", SmNativeType.BYTE, "-17", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, Float.toString(Float.MAX_VALUE), SmNativeType.BYTE, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.FLOAT, Float.toString(Float.MIN_VALUE), SmNativeType.BYTE, "0", this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "NaN", SmNativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "-INF", SmNativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(SmNativeType.FLOAT, "INF", SmNativeType.BYTE, Err("FOCA0002"), this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(SmNativeType.FLOAT, source.builtInType) && source.isValid)
			{
				for (final TestAtom target : TestAtom.getTestAtoms())
				{
					switch (target.builtInType)
					{
						case DOUBLE:
						case FLOAT:
						case DECIMAL:
						case INTEGER:
						case NON_POSITIVE_INTEGER:
						case NEGATIVE_INTEGER:
						case LONG:
						case INT:
						case SHORT:
						case BYTE:
						case NON_NEGATIVE_INTEGER:
						case UNSIGNED_LONG:
						case UNSIGNED_INT:
						case UNSIGNED_SHORT:
						case UNSIGNED_BYTE:
						case POSITIVE_INTEGER:

						case BOOLEAN:
						case UNTYPED_ATOMIC:
						case STRING:
						case NORMALIZED_STRING:
						case TOKEN:
						case LANGUAGE:
						case NAME:
						case NMTOKEN:
						case NCNAME:
						case ID:
						case IDREF:
						case ENTITY:
						{
							// Handled explicitly.
						}
						break;
						default:
						{
							assertCastFail(source.builtInType, source.strVal, target.builtInType, Err("XPTY0004"), this, atomBridge);
						}
					}
				}
			}
		}
	}

	public void testCastingFromInt()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCastGood(SmNativeType.INT, "0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INT, "+0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INT, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.STRING, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.STRING, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.STRING, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.STRING, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.UNTYPED_ATOMIC, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.UNTYPED_ATOMIC, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.INTEGER, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.INT, "3", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);

		assertCastFail(SmNativeType.INT, "0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.INT, "-0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.INT, "3", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.LONG, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.INT, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.SHORT, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INT, "0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.INT, "3", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.INT, "-3", SmNativeType.BYTE, "-3", this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(SmNativeType.INT, source.builtInType) && source.isValid)
			{
				for (final TestAtom target : TestAtom.getTestAtoms())
				{
					switch (target.builtInType)
					{
						case DOUBLE:
						case FLOAT:
						case DECIMAL:
						case INTEGER:
						case NON_POSITIVE_INTEGER:
						case NEGATIVE_INTEGER:
						case LONG:
						case INT:
						case SHORT:
						case BYTE:
						case NON_NEGATIVE_INTEGER:
						case UNSIGNED_LONG:
						case UNSIGNED_INT:
						case UNSIGNED_SHORT:
						case UNSIGNED_BYTE:
						case POSITIVE_INTEGER:

						case BOOLEAN:
						case UNTYPED_ATOMIC:
						case STRING:
						case NORMALIZED_STRING:
						case TOKEN:
						case LANGUAGE:
						case NAME:
						case NMTOKEN:
						case NCNAME:
						case ID:
						case IDREF:
						case ENTITY:
						{
							// Handled explicitly.
						}
						break;
						default:
						{
							assertCastFail(source.builtInType, source.strVal, target.builtInType, Err("XPTY0004"), this, atomBridge);
						}
					}
				}
			}
		}
	}

	public void testCastingFromInteger()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "+0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.STRING, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.STRING, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.STRING, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.STRING, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.UNTYPED_ATOMIC, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.UNTYPED_ATOMIC, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.INTEGER, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(SmNativeType.INTEGER, "3", SmNativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);

		assertCastFail(SmNativeType.INTEGER, "0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.INTEGER, "-0", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(SmNativeType.INTEGER, "3", SmNativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.LONG, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.LONG, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.LONG, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.INT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.INT, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.INT, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.SHORT, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.SHORT, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.SHORT, "-3", this, atomBridge);

		assertCastGood(SmNativeType.INTEGER, "0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-0", SmNativeType.BYTE, "0", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "3", SmNativeType.BYTE, "3", this, atomBridge);
		assertCastGood(SmNativeType.INTEGER, "-3", SmNativeType.BYTE, "-3", this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(SmNativeType.INTEGER, source.builtInType) && source.isValid)
			{
				for (final TestAtom target : TestAtom.getTestAtoms())
				{
					switch (target.builtInType)
					{
						case DOUBLE:
						case FLOAT:
						case DECIMAL:
						case INTEGER:
						case NON_POSITIVE_INTEGER:
						case NEGATIVE_INTEGER:
						case LONG:
						case INT:
						case SHORT:
						case BYTE:
						case NON_NEGATIVE_INTEGER:
						case UNSIGNED_LONG:
						case UNSIGNED_INT:
						case UNSIGNED_SHORT:
						case UNSIGNED_BYTE:
						case POSITIVE_INTEGER:

						case BOOLEAN:
						case UNTYPED_ATOMIC:
						case STRING:
						case NORMALIZED_STRING:
						case TOKEN:
						case LANGUAGE:
						case NAME:
						case NMTOKEN:
						case NCNAME:
						case ID:
						case IDREF:
						case ENTITY:
						{
							// Handled explicitly.
						}
						break;
						default:
						{
							assertCastFail(source.builtInType, source.strVal, target.builtInType, Err("XPTY0004"), this, atomBridge);
						}
					}
				}
			}
		}
	}

	public void testCastingFromString()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// Casting from xs:string to all other types.
		for (final TestAtom target : TestAtom.getTestAtoms())
		{
			if (target.isValid)
			{
				assertCastGood(SmNativeType.STRING, target.strVal, target.builtInType, target.c14nValue, this, atomBridge);
			}
		}
	}

	public void testCastingFromUntypedAtomic()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// Casting from xs:untypedAtomic to all other types (excepting QName & Notation, which are not legal casts for
		// untypedAtomic).
		for (final TestAtom target : TestAtom.excludeTestAtoms(new SmNativeType[] { SmNativeType.QNAME, SmNativeType.NOTATION }, false, false))
		{
			if (target.isValid)
			{
				assertCastGood(SmNativeType.UNTYPED_ATOMIC, target.strVal, target.builtInType, target.c14nValue, this, atomBridge);
			}
		}
	}

	public void testCreateBoolean()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// Mainline using boolean "true".
		{
			final A actual = atomBridge.createBoolean(true);

			assertEquals(SmNativeType.BOOLEAN, atomBridge.getNativeType(actual));
			assertEquals("true", atomBridge.getC14NForm(actual));
		}

		// Mainline using boolean "false".
		{
			final A actual = atomBridge.createBoolean(false);

			assertEquals(SmNativeType.BOOLEAN, atomBridge.getNativeType(actual));
			assertEquals("false", atomBridge.getC14NForm(actual));
		}
	}

	public void testCreateUntypedAtomic()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// Mainline is to pass a normal String.
		{
			final Iterable<? extends A> atoms = atomBridge.wrapAtom(atomBridge.createUntypedAtomic("Hello"));
			assertNotNull(METHOD_CREATE_UNTYPED_ATOMIC, atoms);
			final Iterator<? extends A> it = atoms.iterator();
			assertTrue(it.hasNext());
			final A actual = it.next();

			assertEquals(SmNativeType.UNTYPED_ATOMIC, atomBridge.getNativeType(actual));
			assertEquals("Hello", atomBridge.getC14NForm(actual));
			assertFalse(it.hasNext());
		}

		// Check that passing a null argument does not blow up, but returns an empty sequence.
		{
			try
			{
				atomBridge.createUntypedAtomic(null);

				fail(METHOD_CREATE_UNTYPED_ATOMIC);
			}
			catch (final Throwable e)
			{
				// Expected
			}
		}
	}

	public void testDate()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);
		try
		{
			final A example = atomBridge.compile("1964-04-21", SmNativeType.DATE);

			assertEquals(SmNativeType.DATE, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}

		try
		{
			final A dateAtom = atomBridge.compile("1964-04-21-00:00", SmNativeType.DATE);

			assertNotNull(dateAtom);

			final A dateString = atomBridge.castAs(dateAtom, SmNativeType.STRING.toQName(), this);

			assertEquals("1964-04-21Z", atomBridge.getString(dateString));

			assertEquals(1964, atomBridge.getYear(dateAtom));
			assertEquals(4, atomBridge.getMonth(dateAtom));
			assertEquals(21, atomBridge.getDayOfMonth(dateAtom));

			assertEquals(SmNativeType.DATE, atomBridge.getNativeType(dateAtom));
			assertEquals(SmNativeType.DATE.toQName(), atomBridge.getDataType(dateAtom));

			// Casting xs:date to xs:dateTime
			{
				final A dateTimeAtom = atomBridge.castAs(dateAtom, SmNativeType.DATETIME.toQName(), this);
				assertNotNull(dateTimeAtom);
				final A dateTimeString = atomBridge.castAs(dateTimeAtom, SmNativeType.STRING.toQName(), this);
				final String expect = "1964-04-21T00:00:00Z";
				final String actual = atomBridge.getString(dateTimeString);

				if (!expect.equals(actual))
				{
					System.out.println("expect=" + expect);
					System.out.println("actual=" + actual);
				}

				assertEquals(expect, actual);
			}
		}
		catch (final GxmlAtomCastException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testDateTime()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		final QName xsStringName = SmNativeType.STRING.toQName();

		try
		{
			final A dateTimeAtom = atomBridge.compile("1964-04-21T10:00:00-00:00", SmNativeType.DATETIME);
			assertNotNull(dateTimeAtom);
			final A dateTimeString = atomBridge.castAs(dateTimeAtom, xsStringName, this);

			assertEquals("1964-04-21T10:00:00Z", atomBridge.getString(dateTimeString));

			assertEquals(1964, atomBridge.getYear(dateTimeAtom));
			assertEquals(4, atomBridge.getMonth(dateTimeAtom));
			assertEquals(21, atomBridge.getDayOfMonth(dateTimeAtom));

			assertEquals(SmNativeType.DATETIME, atomBridge.getNativeType(dateTimeAtom));
			assertEquals(SmNativeType.DATETIME.toQName(), atomBridge.getDataType(dateTimeAtom));

			// Casting xs:dateTime to xs:date
			final A dateAtom = atomBridge.castAs(dateTimeAtom, SmNativeType.DATE.toQName(), this);
			assertNotNull(dateAtom);
			final A dateString = atomBridge.castAs(dateAtom, xsStringName, this);
			assertEquals("1964-04-21Z", atomBridge.getString(dateString));

			// Casting xs:dateTime to xs:time
			final A timeAtom = atomBridge.castAs(dateTimeAtom, SmNativeType.TIME.toQName(), new org.gxml.bridgetest.typed.CastingContext<N, A>(pcx));
			assertNotNull(timeAtom);
			final A timeString = atomBridge.castAs(timeAtom, xsStringName, this);
			assertEquals("10:00:00Z", atomBridge.getString(timeString));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testDayTimeDuration()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		try
		{
			final A durationAtom = atomBridge.compile("PT0S", SmNativeType.DURATION_DAYTIME);

			assertNotNull(durationAtom);
			assertEquals(SmNativeType.DURATION_DAYTIME, atomBridge.getNativeType(durationAtom));
			assertEquals(SmNativeType.DURATION_DAYTIME.toQName(), atomBridge.getDataType(durationAtom));

			final A stringAtom = atomBridge.castAs(durationAtom, SmNativeType.STRING.toQName(), this);

			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("PT0S", atomBridge.getString(stringAtom));

			assertEquals(BigDecimal.ZERO, atomBridge.getDurationTotalSeconds(durationAtom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testDecimal()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();

		assertNotNull(nameBridge);

		final QName xsStringName = SmNativeType.STRING.toQName();

		try
		{
			final A decimalAtom = atomBridge.compile("123", SmNativeType.DECIMAL);

			assertNotNull(decimalAtom);
			assertEquals(SmNativeType.DECIMAL, atomBridge.getNativeType(decimalAtom));
			assertEquals(SmNativeType.DECIMAL.toQName(), atomBridge.getDataType(decimalAtom));

			final A stringAtom = atomBridge.castAs(decimalAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("123.0", atomBridge.getString(atomBridge.castAs(decimalAtom, xsStringName, this)));

			try
			{
				assertEquals(new BigDecimal("123"), atomBridge.getDecimal(decimalAtom));
			}
			catch (final RuntimeException e)
			{
				fail(atomBridge.getClass().getName() + ".getDecimalValue(" + decimalAtom + ")");
			}
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testDouble()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();

		assertNotNull(nameBridge);

		final QName xsStringName = SmNativeType.STRING.toQName();

		try
		{
			final A doubleAtom = atomBridge.compile("1234567", SmNativeType.DOUBLE);

			assertNotNull(doubleAtom);
			assertEquals(SmNativeType.DOUBLE, atomBridge.getNativeType(doubleAtom));
			assertEquals(SmNativeType.DOUBLE.toQName(), atomBridge.getDataType(doubleAtom));

			A stringAtom = atomBridge.castAs(doubleAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("1.234567E6", atomBridge.getString(stringAtom));

			stringAtom = atomBridge.castAs(doubleAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("1.234567E6", atomBridge.getString(stringAtom));

			stringAtom = atomBridge.castAs(doubleAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("1.234567E6", atomBridge.getString(stringAtom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	/**
	 * Tests the following method from GxAtomManager interface: <br>
	 * GxName getDataType(A atom);
	 */
	public void testGxAtomManager_compile_getDataType()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		assertNotNull(atomBridge);

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		// Iterate over table of valid atoms.
		final Iterable<TestAtom> testValues = TestAtom.getTestAtoms();
		for (TestAtom testValue : testValues)
		{
			try
			{
				final A theAtom = atomBridge.compile(testValue.strVal, testValue.builtInType);
				final QName name = atomBridge.getDataType(theAtom);
				assertEquals(testValue.builtInType.toQName(), name);

				// Same compilation test, but use uberType.
				final A theAtom2 = atomBridge.compile(testValue.strVal, testValue.builtInType);
				final QName name2 = atomBridge.getDataType(theAtom2);
				assertEquals(testValue.builtInType.toQName(), name2);
			}
			catch (GxmlAtomCastException ex)
			{
				// Ok, if test value expected to be invalid.
				if (testValue.isValid)
				{
					fail("Valid value could not be compiled: " + testValue.toString());
				}
			}
			catch (AssertionError error)
			{
				// Ok, if test value expected to be invalid.
				if (testValue.isValid)
				{
					fail("Valid value could not be compiled: " + testValue.toString());
				}
			}
			/*
			 * catch(IllegalArgumentException ex) { // Ok, if test value expected to be invalid. if(testValue.isValid) {
			 * fail(ex.getMessage() + testValue.toString()); } }
			 */
		}
	}

	public void testHexBinary()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		try
		{
			final A hexAtom = atomBridge.compile("00", SmNativeType.HEX_BINARY);

			assertNotNull(hexAtom);

			final A strval = atomBridge.castAs(hexAtom, SmNativeType.STRING.toQName(), this);

			assertEquals("00", atomBridge.getString(strval));

			/*
			 * try { assertEquals(new BigInteger(""), hexBridge.getHexBinaryValue(hexAtom)); } catch (final
			 * GxAtomCastException e) { fail(); }
			 */

			assertEquals(SmNativeType.HEX_BINARY, atomBridge.getNativeType(hexAtom));
			assertEquals(SmNativeType.HEX_BINARY.toQName(), atomBridge.getDataType(hexAtom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testInt()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		try
		{
			final A intAtom = atomBridge.compile("123", SmNativeType.INT);

			assertNotNull(intAtom);
			assertEquals(SmNativeType.INT, atomBridge.getNativeType(intAtom));
			assertEquals(SmNativeType.INT.toQName(), atomBridge.getDataType(intAtom));

			final A stringAtom = atomBridge.castAs(intAtom, SmNativeType.STRING.toQName(), this);

			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("123", atomBridge.getString(stringAtom));

			try
			{
				assertEquals(new Integer("123"), Integer.valueOf(atomBridge.getInt(intAtom)));
			}
			catch (final RuntimeException e)
			{
				fail(atomBridge.getClass().getName() + ".getIntValue(" + intAtom + ")");
			}
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testInteger()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();

		assertNotNull(nameBridge);

		final QName xsStringName = SmNativeType.STRING.toQName();

		try
		{
			final A integerAtom = atomBridge.compile("123", SmNativeType.INTEGER);

			assertNotNull(integerAtom);
			assertEquals(SmNativeType.INTEGER, atomBridge.getNativeType(integerAtom));
			assertEquals(SmNativeType.INTEGER.toQName(), atomBridge.getDataType(integerAtom));

			final A stringAtom = atomBridge.castAs(integerAtom, xsStringName, this);

			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("123", atomBridge.getString(stringAtom));

			try
			{
				assertEquals(new BigInteger("123"), atomBridge.getInteger(integerAtom));
			}
			catch (final RuntimeException e)
			{
				fail(atomBridge.getClass().getName() + ".getInteger(" + integerAtom + ")");
			}
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testLong()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();

		assertNotNull(nameBridge);

		final QName xsStringName = SmNativeType.STRING.toQName();

		try
		{
			final A longAtom = atomBridge.compile("123", SmNativeType.LONG);

			assertNotNull(longAtom);
			assertEquals(SmNativeType.LONG, atomBridge.getNativeType(longAtom));
			assertEquals(SmNativeType.LONG.toQName(), atomBridge.getDataType(longAtom));

			final A stringAtom = atomBridge.castAs(longAtom, xsStringName, this);

			assertNotNull(stringAtom);
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("123", atomBridge.getString(stringAtom));

			// try
			// {
			// assertEquals(new Integer("123"), intBridge.getIntValue(longAtom));
			// }
			// catch (final GxAtomCastException e)
			// {
			// fail();
			// }
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}

	public void testManagerLookupByAtom()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		for (final TestAtom testAtom : TestAtom.getTestAtoms())
		{
			if (testAtom.isValid)
			{
				assertManagerLookupByAtom(testAtom.builtInType, testAtom.strVal, atomBridge);
			}
		}
	}

	public void testMonth()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		final QName STRING = SmNativeType.STRING.toQName();

		try
		{
			final A monthAtom = atomBridge.compile("--05", SmNativeType.GMONTH);
			assertNotNull(monthAtom);
			final A monthString = atomBridge.castAs(monthAtom, STRING, this);
			assertEquals(5, atomBridge.getMonth(monthAtom));
			assertEquals("--05", atomBridge.getString(monthString));
		}
		catch (final GxmlAtomCastException e)
		{
			fail(e.getMessage());
		}
	}

	public void testNormalizedString()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();

		assertNotNull(nameBridge);

		try
		{
			final A example = atomBridge.compile("abc", SmNativeType.STRING);

			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail("?");
		}

		try
		{
			final A atom = atomBridge.compile("Hello", SmNativeType.STRING);

			assertNotNull(atom);

			assertEquals("Hello", atomBridge.getString(atom));
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(atom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(atom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}

	}

	public void testProlog()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		assertNotNull(atomBridge);

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(METHOD_GET_NAME_BRIDGE, nameBridge);

		// Check that there is a manager for each built in atomic type by boty UberType and GxName.
		for (final SmNativeType builtInType : SmNativeType.values())
		{
			switch (builtInType)
			{
				case ANY_TYPE:
				case ANY_SIMPLE_TYPE:
				case ANY_ATOMIC_TYPE:
				case UNTYPED:
				case IDREFS:
				case NMTOKENS:
				case ENTITIES:
				{
					// Ignore
				}
				break;
				default:
				{
				}
			}
		}
	}

	public void testQName()
	{

	}

	/**
	 * Verify the common xs:integer values (-16 to 16) are being reused.
	 */
	public void testStashingInteger()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		for (int i = -16; i <= 16; i++)
		{
			final A x = atomBridge.createInteger(i);
			final A y = atomBridge.createInteger(i);

			assertSame(Integer.toString(i), x, y);
		}

		final A one = atomBridge.createInteger(1);
		final A six = atomBridge.createInteger(6);
		final List<A> wrapped = atomBridge.wrapAtom(six);
		assertTrue(wrapped.contains(six));
		assertFalse(wrapped.contains(one));
		assertFalse(wrapped.contains(null));

		assertEquals(0, wrapped.indexOf(six));
		assertEquals(-1, wrapped.indexOf(one));
		assertEquals(-1, wrapped.indexOf(null));

		assertEquals(0, wrapped.lastIndexOf(six));
		assertEquals(-1, wrapped.lastIndexOf(one));
		assertEquals(-1, wrapped.lastIndexOf(null));
	}

	/**
	 * Verify the common xs:decimal values (0 to 10) are being reused.
	 */
	public void testStashingDecimal()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// TODO: Statshing xs:decimal
		// for (int i = 0; i <= 10; i++)
		// {
		// final A x = atomBridge.createDecimal(i);
		// final A y = atomBridge.createDecimal(i);
		//
		// assertSame(Integer.toString(i), x, y);
		// }

		final A one = atomBridge.createDecimal(1);
		final A six = atomBridge.createDecimal(6);
		final List<A> wrapped = atomBridge.wrapAtom(six);
		assertTrue(wrapped.contains(six));
		assertFalse(wrapped.contains(one));
		assertFalse(wrapped.contains(null));

		assertEquals(0, wrapped.indexOf(six));
		assertEquals(-1, wrapped.indexOf(one));
		assertEquals(-1, wrapped.indexOf(null));

		assertEquals(0, wrapped.lastIndexOf(six));
		assertEquals(-1, wrapped.lastIndexOf(one));
		assertEquals(-1, wrapped.lastIndexOf(null));
	}

	public void testString()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();

		assertNotNull(nameBridge);

		try
		{
			final A example = atomBridge.compile("abc", SmNativeType.STRING);

			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail("?");
		}

		try
		{
			final A atom = atomBridge.compile("Hello", SmNativeType.STRING);

			assertNotNull(atom);

			assertEquals("Hello", atomBridge.getString(atom));
			assertEquals(SmNativeType.STRING, atomBridge.getNativeType(atom));
			assertEquals(SmNativeType.STRING.toQName(), atomBridge.getDataType(atom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}

	}

	public void testTime()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);
		try
		{
			final A example = atomBridge.compile("10:05:23", SmNativeType.TIME);

			assertEquals(SmNativeType.TIME, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
		try
		{
			final A timeAtom = atomBridge.compile("10:05:23", SmNativeType.TIME);

			assertNotNull(timeAtom);

			final A timeString = atomBridge.castAs(timeAtom, SmNativeType.STRING.toQName(), this);

			assertEquals("10:05:23", atomBridge.getString(timeString));

			assertEquals(1970, atomBridge.getYear(timeAtom));
			assertEquals(1, atomBridge.getMonth(timeAtom));
			assertEquals(1, atomBridge.getDayOfMonth(timeAtom));
			assertEquals(10, atomBridge.getHourOfDay(timeAtom));
			assertEquals(5, atomBridge.getMinute(timeAtom));
			assertEquals(BigDecimal.valueOf(23), atomBridge.getSecondsAsBigDecimal(timeAtom));

			assertEquals(SmNativeType.TIME, atomBridge.getNativeType(timeAtom));
			assertEquals(SmNativeType.TIME.toQName(), atomBridge.getDataType(timeAtom));
		}
		catch (final GxmlAtomCastException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testWrapAtom()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final A expect = atomBridge.createString("Hello");

		// Mainline is to pass a normal atom.
		{
			final Iterable<? extends A> atoms = atomBridge.wrapAtom(expect);
			assertNotNull(METHOD_WRAP_ATOM, atoms);
			final Iterator<? extends A> it = atoms.iterator();
			assertTrue(it.hasNext());
			final A actual = it.next();
			assertEquals(expect, actual);
			assertFalse(it.hasNext());
		}

		// Check that passing a null argument does not blow up, but returns an empty sequence.
		{
			final Iterable<? extends A> atoms = atomBridge.wrapAtom(null);
			assertNotNull(METHOD_WRAP_ATOM, atoms);
			final Iterator<? extends A> it = atoms.iterator();
			assertFalse(it.hasNext());
		}
	}

	public void testYearMonthDuration()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		final NameSource nameBridge = atomBridge.getNameBridge();
		assertNotNull(nameBridge);

		try
		{
			final A example = atomBridge.compile("P4Y3M", SmNativeType.DURATION_YEARMONTH);

			assertEquals(SmNativeType.DURATION_YEARMONTH, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}

		try
		{
			final A atom = atomBridge.compile("P4Y1M", SmNativeType.DURATION_YEARMONTH);

			assertNotNull(atom);

			final A strval = atomBridge.castAs(atom, SmNativeType.STRING.toQName(), this);

			assertEquals("P4Y1M", atomBridge.getString(strval));

			assertEquals(49, atomBridge.getDurationTotalMonths(atom));

			assertEquals(SmNativeType.DURATION_YEARMONTH, atomBridge.getNativeType(atom));
			assertEquals(SmNativeType.DURATION_YEARMONTH.toQName(), atomBridge.getDataType(atom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}
}
