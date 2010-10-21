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

import org.genxdm.bridgetest.GxTestBase;
import org.genxdm.exceptions.GxmlAtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.SpillagePolicy;
import org.genxdm.names.NameSource;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.typed.types.Emulation;
import org.genxdm.xs.types.NativeType;

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

	private static <A> void assertCastFail(final NativeType sourceType, final String sourceString, final NativeType targetType, final QName code, final CastingContext<A> castingContext, final AtomBridge<A> atomBridge)
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

	private static <A> void assertCastGood(final NativeType sourceType, final String sourceString, final NativeType targetType, final String targetString, final CastingContext<A> castingContext, final AtomBridge<A> atomBridge)
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
	 * Type-safe comparison of {@link NativeType}.
	 */
	private static boolean equals(NativeType one, NativeType two)
	{
		return one == two;
	}

	private void assertManagerLookupByAtom(final NativeType nativeType, final String strval, final AtomBridge<A> atomBridge)
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
		final NativeType targetBuiltInType = NativeType.getType(targetType);

		// Create the source atom.
		A sourceAtom = null;
		try
		{
			sourceAtom = atomBridge.compile(sourceValue, NativeType.getType(sourceType));
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
				final NativeType resultType = atomBridge.getNativeType(targetAtom);
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

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertFalse(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertFalse(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertFalse(nativeType.isA(NativeType.NAME));
			assertFalse(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Hello World";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.NORMALIZED_STRING, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertFalse(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertFalse(nativeType.isA(NativeType.NAME));
			assertFalse(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Hello World";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.TOKEN, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertFalse(nativeType.isA(NativeType.NAME));
			assertFalse(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "en";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.LANGUAGE, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertTrue(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertFalse(nativeType.isA(NativeType.NAME));
			assertFalse(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.NMTOKEN, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertTrue(nativeType.isA(NativeType.NMTOKEN));
			assertFalse(nativeType.isA(NativeType.NAME));
			assertFalse(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.NAME, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertTrue(nativeType.isA(NativeType.NAME));
			assertFalse(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.NCNAME, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertTrue(nativeType.isA(NativeType.NAME));
			assertTrue(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.ID, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertTrue(nativeType.isA(NativeType.NAME));
			assertTrue(nativeType.isA(NativeType.NCNAME));
			assertTrue(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.IDREF, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertTrue(nativeType.isA(NativeType.NAME));
			assertTrue(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertTrue(nativeType.isA(NativeType.IDREF));
			assertFalse(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final String original = "Snoopy";

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createString(original), NativeType.ENTITY, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}
			assertEquals(original, atomBridge.getString(atom));

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.STRING));
			assertTrue(nativeType.isA(NativeType.NORMALIZED_STRING));
			assertTrue(nativeType.isA(NativeType.TOKEN));
			assertFalse(nativeType.isA(NativeType.LANGUAGE));
			assertFalse(nativeType.isA(NativeType.NMTOKEN));
			assertTrue(nativeType.isA(NativeType.NAME));
			assertTrue(nativeType.isA(NativeType.NCNAME));
			assertFalse(nativeType.isA(NativeType.ID));
			assertFalse(nativeType.isA(NativeType.IDREF));
			assertTrue(nativeType.isA(NativeType.ENTITY));

			assertFalse(nativeType.isA(NativeType.UNTYPED_ATOMIC));
			assertTrue(nativeType.isA(NativeType.ANY_ATOMIC_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_SIMPLE_TYPE));
			assertTrue(nativeType.isA(NativeType.ANY_TYPE));
		}
		{
			final byte original = (byte)123;

			final A atom = atomBridge.createByte(original);

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertTrue(nativeType.isA(NativeType.BYTE));
			assertTrue(nativeType.isA(NativeType.SHORT));
			assertTrue(nativeType.isA(NativeType.INT));
			assertTrue(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

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

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertTrue(nativeType.isA(NativeType.SHORT));
			assertTrue(nativeType.isA(NativeType.INT));
			assertTrue(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(Short.valueOf(original).shortValue(), atomBridge.getShort(atom));
			assertEquals(Integer.valueOf(original).intValue(), atomBridge.getInt(atom));
			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final int original = (int)123;

			final A atom = atomBridge.createInt(original);

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertTrue(nativeType.isA(NativeType.INT));
			assertTrue(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(Integer.valueOf(original).intValue(), atomBridge.getInt(atom));
			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)123;

			final A atom = atomBridge.createLong(original);

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertTrue(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(Long.valueOf(original).longValue(), atomBridge.getLong(atom));
			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), NativeType.POSITIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(NativeType.POSITIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), NativeType.NON_NEGATIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(NativeType.NON_NEGATIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)-123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), NativeType.NON_POSITIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(NativeType.NON_POSITIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final long original = (long)-123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createLong(original), NativeType.NEGATIVE_INTEGER, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(NativeType.NEGATIVE_INTEGER, nativeType);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), NativeType.UNSIGNED_BYTE, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertEquals(NativeType.UNSIGNED_BYTE, nativeType);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), NativeType.UNSIGNED_SHORT, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), NativeType.UNSIGNED_INT, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final byte original = (byte)123;

			final A atom;
			try
			{
				atom = atomBridge.castAs(atomBridge.createByte(original), NativeType.UNSIGNED_LONG, this);
			}
			catch (final GxmlAtomCastException e)
			{
				throw new AssertionError(e);
			}

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(BigInteger.valueOf(original), atomBridge.getInteger(atom));
			assertEquals(BigDecimal.valueOf(original), atomBridge.getDecimal(atom));
		}
		{
			final BigInteger original = BigInteger.valueOf(123);

			final A atom = atomBridge.createInteger(123);

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertTrue(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

			assertEquals(original, atomBridge.getInteger(atom));
			assertEquals(new BigDecimal(original), atomBridge.getDecimal(atom));
		}
		{
			final BigDecimal original = BigDecimal.valueOf(123);

			final A atom = atomBridge.createDecimal(original);

			final NativeType nativeType = atomBridge.getNativeType(atom);
			assertFalse(nativeType.isA(NativeType.BYTE));
			assertFalse(nativeType.isA(NativeType.SHORT));
			assertFalse(nativeType.isA(NativeType.INT));
			assertFalse(nativeType.isA(NativeType.LONG));
			assertFalse(nativeType.isA(NativeType.INTEGER));
			assertTrue(nativeType.isA(NativeType.DECIMAL));

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

		assertCastGood(NativeType.DECIMAL, "0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "+0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "0.0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "0.0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.STRING, "0.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.STRING, "0.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.STRING, "3.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.STRING, "-3.0", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.UNTYPED_ATOMIC, "0.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.UNTYPED_ATOMIC, "0.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.UNTYPED_ATOMIC, "3.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.UNTYPED_ATOMIC, "-3.0", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.INTEGER, "-3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3.1456", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.INTEGER, "-17", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.DECIMAL, "3", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(NativeType.DECIMAL, "3.1456", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.NON_POSITIVE_INTEGER, "-17", this, atomBridge);

		assertCastFail(NativeType.DECIMAL, "0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.DECIMAL, "-0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.DECIMAL, "3", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(NativeType.DECIMAL, "3.1456", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.NEGATIVE_INTEGER, "-17", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.LONG, "-3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3.1456", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.LONG, "-17", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.INT, "-3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3.1456", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.INT, "-17", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.SHORT, "-3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3.1456", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.SHORT, "-17", this, atomBridge);

		assertCastGood(NativeType.DECIMAL, "0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-3", NativeType.BYTE, "-3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "3.1456", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.DECIMAL, "-17.89", NativeType.BYTE, "-17", this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(NativeType.DECIMAL, source.builtInType) && source.isValid)
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

		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.DOUBLE, "1.7976931348623157E308", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.DOUBLE, "4.9E-324", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "NaN", NativeType.DOUBLE, "NaN", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-INF", NativeType.DOUBLE, "-INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "INF", NativeType.DOUBLE, "INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.FLOAT, "INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "NaN", NativeType.FLOAT, "NaN", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-INF", NativeType.FLOAT, "-INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "INF", NativeType.FLOAT, "INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "NaN", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-INF", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "INF", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "+0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0.0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0.0E0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.STRING, "1.7976931348623157E308", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.STRING, "4.9E-324", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "NaN", NativeType.STRING, "NaN", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-INF", NativeType.STRING, "-INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "INF", NativeType.STRING, "INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0", NativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.STRING, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.STRING, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.UNTYPED_ATOMIC, "1.7976931348623157E308", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.UNTYPED_ATOMIC, "4.9E-324", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "NaN", NativeType.UNTYPED_ATOMIC, "NaN", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-INF", NativeType.UNTYPED_ATOMIC, "-INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "INF", NativeType.UNTYPED_ATOMIC, "INF", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0", NativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.UNTYPED_ATOMIC, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.UNTYPED_ATOMIC, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.DECIMAL, "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.DECIMAL, "0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000049", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(NativeType.DOUBLE, "0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.INTEGER, "-3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3.1456", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.INTEGER, "-17", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.INTEGER, "179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.DOUBLE, "0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "3", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "3.1456", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.NON_POSITIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastFail(NativeType.DOUBLE, "0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "3", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "3.1456", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.NEGATIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.DOUBLE, "0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.LONG, "-3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3.1456", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.LONG, "-17", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.LONG, "9223372036854775807", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.LONG, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.LONG, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.DOUBLE, "0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.INT, "-3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3.1456", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.INT, "-17", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.INT, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.INT, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.INT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.DOUBLE, "0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.SHORT, "-3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3.1456", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.SHORT, "-17", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.SHORT, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.SHORT, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.SHORT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.DOUBLE, "0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-3", NativeType.BYTE, "-3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "3.1456", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.DOUBLE, "-17.89", NativeType.BYTE, "-17", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, Double.toString(Double.MAX_VALUE), NativeType.BYTE, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.DOUBLE, Double.toString(Double.MIN_VALUE), NativeType.BYTE, "0", this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "NaN", NativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "-INF", NativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.DOUBLE, "INF", NativeType.BYTE, Err("FOCA0002"), this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(NativeType.DOUBLE, source.builtInType) && source.isValid)
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

		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.DOUBLE, "3.4028234663852886E38", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.DOUBLE, "1.401298464324817E-45", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "NaN", NativeType.DOUBLE, "NaN", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-INF", NativeType.DOUBLE, "-INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "INF", NativeType.DOUBLE, "INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.FLOAT, "3.4028235E38", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.FLOAT, "1.4E-45", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "NaN", NativeType.FLOAT, "NaN", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-INF", NativeType.FLOAT, "-INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "INF", NativeType.FLOAT, "INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "NaN", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-INF", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "INF", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "+0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0.0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0.0E0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.STRING, "3.4028235E38", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.STRING, "1.4E-45", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "NaN", NativeType.STRING, "NaN", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-INF", NativeType.STRING, "-INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "INF", NativeType.STRING, "INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0", NativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.STRING, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.STRING, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.STRING, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.UNTYPED_ATOMIC, "3.4028235E38", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.UNTYPED_ATOMIC, "1.4E-45", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "NaN", NativeType.UNTYPED_ATOMIC, "NaN", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-INF", NativeType.UNTYPED_ATOMIC, "-INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "INF", NativeType.UNTYPED_ATOMIC, "INF", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0", NativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.UNTYPED_ATOMIC, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.UNTYPED_ATOMIC, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.UNTYPED_ATOMIC, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.DECIMAL, "340282346638528860000000000000000000000.0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.DECIMAL, "0.000000000000000000000000000000000000000000001401298464324817", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.DECIMAL, Err("FOCA0002"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, "0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(NativeType.FLOAT, "0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.INTEGER, "-3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3.1456", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.INTEGER, "-17", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.INTEGER, "9223372036854775807", this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.FLOAT, "0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "3", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "3.1456", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.NON_POSITIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.NON_POSITIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastFail(NativeType.FLOAT, "0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "3", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "3.1456", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.NEGATIVE_INTEGER, "-17", this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.NEGATIVE_INTEGER, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.FLOAT, "0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.LONG, "-3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3.1456", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.LONG, "-17", this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.INT, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.INT, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.LONG, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.LONG, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.FLOAT, "0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.INT, "-3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3.1456", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.INT, "-17", this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.INT, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.INT, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.INT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.INT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.FLOAT, "0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.SHORT, "-3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3.1456", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.SHORT, "-17", this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.SHORT, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.SHORT, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.SHORT, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.SHORT, Err("FOCA0002"), this, atomBridge);

		assertCastGood(NativeType.FLOAT, "0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-3", NativeType.BYTE, "-3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "3.1456", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.FLOAT, "-17.89", NativeType.BYTE, "-17", this, atomBridge);
		assertCastFail(NativeType.FLOAT, Float.toString(Float.MAX_VALUE), NativeType.BYTE, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.FLOAT, Float.toString(Float.MIN_VALUE), NativeType.BYTE, "0", this, atomBridge);
		assertCastFail(NativeType.FLOAT, "NaN", NativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "-INF", NativeType.BYTE, Err("FOCA0002"), this, atomBridge);
		assertCastFail(NativeType.FLOAT, "INF", NativeType.BYTE, Err("FOCA0002"), this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(NativeType.FLOAT, source.builtInType) && source.isValid)
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

		assertCastGood(NativeType.INT, "0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INT, "+0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INT, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.STRING, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.STRING, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.STRING, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.STRING, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.UNTYPED_ATOMIC, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.UNTYPED_ATOMIC, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.INTEGER, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.INT, "3", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);

		assertCastFail(NativeType.INT, "0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.INT, "-0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.INT, "3", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.LONG, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.INT, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.SHORT, "-3", this, atomBridge);

		assertCastGood(NativeType.INT, "0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "-0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.INT, "3", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.INT, "-3", NativeType.BYTE, "-3", this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(NativeType.INT, source.builtInType) && source.isValid)
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

		assertCastGood(NativeType.INTEGER, "0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.DOUBLE, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.DOUBLE, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.DOUBLE, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.FLOAT, "0.0E0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.FLOAT, "3.0E0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.FLOAT, "-3.0E0", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "+0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "0", NativeType.BOOLEAN, "false", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.BOOLEAN, "true", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.BOOLEAN, "true", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.STRING, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.STRING, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.STRING, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.STRING, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.UNTYPED_ATOMIC, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.UNTYPED_ATOMIC, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.UNTYPED_ATOMIC, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.DECIMAL, "0.0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.DECIMAL, "3.0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.DECIMAL, "-3.0", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.INTEGER, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.INTEGER, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.NON_POSITIVE_INTEGER, "0", this, atomBridge);
		assertCastFail(NativeType.INTEGER, "3", NativeType.NON_POSITIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.NON_POSITIVE_INTEGER, "-3", this, atomBridge);

		assertCastFail(NativeType.INTEGER, "0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.INTEGER, "-0", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastFail(NativeType.INTEGER, "3", NativeType.NEGATIVE_INTEGER, Err("FORG0001"), this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.NEGATIVE_INTEGER, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.LONG, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.LONG, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.LONG, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.INT, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.INT, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.INT, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.SHORT, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.SHORT, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.SHORT, "-3", this, atomBridge);

		assertCastGood(NativeType.INTEGER, "0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-0", NativeType.BYTE, "0", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "3", NativeType.BYTE, "3", this, atomBridge);
		assertCastGood(NativeType.INTEGER, "-3", NativeType.BYTE, "-3", this, atomBridge);

		for (final TestAtom source : TestAtom.getTestAtoms())
		{
			if (equals(NativeType.INTEGER, source.builtInType) && source.isValid)
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
				assertCastGood(NativeType.STRING, target.strVal, target.builtInType, target.c14nValue, this, atomBridge);
			}
		}
	}

	public void testCastingFromUntypedAtomic()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// Casting from xs:untypedAtomic to all other types (excepting QName & Notation, which are not legal casts for
		// untypedAtomic).
		for (final TestAtom target : TestAtom.excludeTestAtoms(new NativeType[] { NativeType.QNAME, NativeType.NOTATION }, false, false))
		{
			if (target.isValid)
			{
				assertCastGood(NativeType.UNTYPED_ATOMIC, target.strVal, target.builtInType, target.c14nValue, this, atomBridge);
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

			assertEquals(NativeType.BOOLEAN, atomBridge.getNativeType(actual));
			assertEquals("true", atomBridge.getC14NForm(actual));
		}

		// Mainline using boolean "false".
		{
			final A actual = atomBridge.createBoolean(false);

			assertEquals(NativeType.BOOLEAN, atomBridge.getNativeType(actual));
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

			assertEquals(NativeType.UNTYPED_ATOMIC, atomBridge.getNativeType(actual));
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
			final A example = atomBridge.compile("1964-04-21", NativeType.DATE);

			assertEquals(NativeType.DATE, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}

		try
		{
			final A dateAtom = atomBridge.compile("1964-04-21-00:00", NativeType.DATE);

			assertNotNull(dateAtom);

			final A dateString = atomBridge.castAs(dateAtom, NativeType.STRING.toQName(), this);

			assertEquals("1964-04-21Z", atomBridge.getString(dateString));

			assertEquals(1964, atomBridge.getYear(dateAtom));
			assertEquals(4, atomBridge.getMonth(dateAtom));
			assertEquals(21, atomBridge.getDayOfMonth(dateAtom));

			assertEquals(NativeType.DATE, atomBridge.getNativeType(dateAtom));
			assertEquals(NativeType.DATE.toQName(), atomBridge.getDataType(dateAtom));

			// Casting xs:date to xs:dateTime
			{
				final A dateTimeAtom = atomBridge.castAs(dateAtom, NativeType.DATETIME.toQName(), this);
				assertNotNull(dateTimeAtom);
				final A dateTimeString = atomBridge.castAs(dateTimeAtom, NativeType.STRING.toQName(), this);
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

		final QName xsStringName = NativeType.STRING.toQName();

		try
		{
			final A dateTimeAtom = atomBridge.compile("1964-04-21T10:00:00-00:00", NativeType.DATETIME);
			assertNotNull(dateTimeAtom);
			final A dateTimeString = atomBridge.castAs(dateTimeAtom, xsStringName, this);

			assertEquals("1964-04-21T10:00:00Z", atomBridge.getString(dateTimeString));

			assertEquals(1964, atomBridge.getYear(dateTimeAtom));
			assertEquals(4, atomBridge.getMonth(dateTimeAtom));
			assertEquals(21, atomBridge.getDayOfMonth(dateTimeAtom));

			assertEquals(NativeType.DATETIME, atomBridge.getNativeType(dateTimeAtom));
			assertEquals(NativeType.DATETIME.toQName(), atomBridge.getDataType(dateTimeAtom));

			// Casting xs:dateTime to xs:date
			final A dateAtom = atomBridge.castAs(dateTimeAtom, NativeType.DATE.toQName(), this);
			assertNotNull(dateAtom);
			final A dateString = atomBridge.castAs(dateAtom, xsStringName, this);
			assertEquals("1964-04-21Z", atomBridge.getString(dateString));

			// Casting xs:dateTime to xs:time
			final A timeAtom = atomBridge.castAs(dateTimeAtom, NativeType.TIME.toQName(), new org.genxdm.bridgetest.typed.CastingContext<N, A>(pcx));
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
			final A durationAtom = atomBridge.compile("PT0S", NativeType.DURATION_DAYTIME);

			assertNotNull(durationAtom);
			assertEquals(NativeType.DURATION_DAYTIME, atomBridge.getNativeType(durationAtom));
			assertEquals(NativeType.DURATION_DAYTIME.toQName(), atomBridge.getDataType(durationAtom));

			final A stringAtom = atomBridge.castAs(durationAtom, NativeType.STRING.toQName(), this);

			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

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

		final QName xsStringName = NativeType.STRING.toQName();

		try
		{
			final A decimalAtom = atomBridge.compile("123", NativeType.DECIMAL);

			assertNotNull(decimalAtom);
			assertEquals(NativeType.DECIMAL, atomBridge.getNativeType(decimalAtom));
			assertEquals(NativeType.DECIMAL.toQName(), atomBridge.getDataType(decimalAtom));

			final A stringAtom = atomBridge.castAs(decimalAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

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

		final QName xsStringName = NativeType.STRING.toQName();

		try
		{
			final A doubleAtom = atomBridge.compile("1234567", NativeType.DOUBLE);

			assertNotNull(doubleAtom);
			assertEquals(NativeType.DOUBLE, atomBridge.getNativeType(doubleAtom));
			assertEquals(NativeType.DOUBLE.toQName(), atomBridge.getDataType(doubleAtom));

			A stringAtom = atomBridge.castAs(doubleAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("1.234567E6", atomBridge.getString(stringAtom));

			stringAtom = atomBridge.castAs(doubleAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

			assertEquals("1.234567E6", atomBridge.getString(stringAtom));

			stringAtom = atomBridge.castAs(doubleAtom, xsStringName, this);
			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

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
			final A hexAtom = atomBridge.compile("00", NativeType.HEX_BINARY);

			assertNotNull(hexAtom);

			final A strval = atomBridge.castAs(hexAtom, NativeType.STRING.toQName(), this);

			assertEquals("00", atomBridge.getString(strval));

			/*
			 * try { assertEquals(new BigInteger(""), hexBridge.getHexBinaryValue(hexAtom)); } catch (final
			 * GxAtomCastException e) { fail(); }
			 */

			assertEquals(NativeType.HEX_BINARY, atomBridge.getNativeType(hexAtom));
			assertEquals(NativeType.HEX_BINARY.toQName(), atomBridge.getDataType(hexAtom));
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
			final A intAtom = atomBridge.compile("123", NativeType.INT);

			assertNotNull(intAtom);
			assertEquals(NativeType.INT, atomBridge.getNativeType(intAtom));
			assertEquals(NativeType.INT.toQName(), atomBridge.getDataType(intAtom));

			final A stringAtom = atomBridge.castAs(intAtom, NativeType.STRING.toQName(), this);

			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

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

		final QName xsStringName = NativeType.STRING.toQName();

		try
		{
			final A integerAtom = atomBridge.compile("123", NativeType.INTEGER);

			assertNotNull(integerAtom);
			assertEquals(NativeType.INTEGER, atomBridge.getNativeType(integerAtom));
			assertEquals(NativeType.INTEGER.toQName(), atomBridge.getDataType(integerAtom));

			final A stringAtom = atomBridge.castAs(integerAtom, xsStringName, this);

			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

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

		final QName xsStringName = NativeType.STRING.toQName();

		try
		{
			final A longAtom = atomBridge.compile("123", NativeType.LONG);

			assertNotNull(longAtom);
			assertEquals(NativeType.LONG, atomBridge.getNativeType(longAtom));
			assertEquals(NativeType.LONG.toQName(), atomBridge.getDataType(longAtom));

			final A stringAtom = atomBridge.castAs(longAtom, xsStringName, this);

			assertNotNull(stringAtom);
			assertEquals(NativeType.STRING, atomBridge.getNativeType(stringAtom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(stringAtom));

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

		final QName STRING = NativeType.STRING.toQName();

		try
		{
			final A monthAtom = atomBridge.compile("--05", NativeType.GMONTH);
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
			final A example = atomBridge.compile("abc", NativeType.STRING);

			assertEquals(NativeType.STRING, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail("?");
		}

		try
		{
			final A atom = atomBridge.compile("Hello", NativeType.STRING);

			assertNotNull(atom);

			assertEquals("Hello", atomBridge.getString(atom));
			assertEquals(NativeType.STRING, atomBridge.getNativeType(atom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(atom));
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
		for (final NativeType builtInType : NativeType.values())
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
			final A example = atomBridge.compile("abc", NativeType.STRING);

			assertEquals(NativeType.STRING, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail("?");
		}

		try
		{
			final A atom = atomBridge.compile("Hello", NativeType.STRING);

			assertNotNull(atom);

			assertEquals("Hello", atomBridge.getString(atom));
			assertEquals(NativeType.STRING, atomBridge.getNativeType(atom));
			assertEquals(NativeType.STRING.toQName(), atomBridge.getDataType(atom));
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
			final A example = atomBridge.compile("10:05:23", NativeType.TIME);

			assertEquals(NativeType.TIME, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
		try
		{
			final A timeAtom = atomBridge.compile("10:05:23", NativeType.TIME);

			assertNotNull(timeAtom);

			final A timeString = atomBridge.castAs(timeAtom, NativeType.STRING.toQName(), this);

			assertEquals("10:05:23", atomBridge.getString(timeString));

			assertEquals(1970, atomBridge.getYear(timeAtom));
			assertEquals(1, atomBridge.getMonth(timeAtom));
			assertEquals(1, atomBridge.getDayOfMonth(timeAtom));
			assertEquals(10, atomBridge.getHourOfDay(timeAtom));
			assertEquals(5, atomBridge.getMinute(timeAtom));
			assertEquals(BigDecimal.valueOf(23), atomBridge.getSecondsAsBigDecimal(timeAtom));

			assertEquals(NativeType.TIME, atomBridge.getNativeType(timeAtom));
			assertEquals(NativeType.TIME.toQName(), atomBridge.getDataType(timeAtom));
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
			final A example = atomBridge.compile("P4Y3M", NativeType.DURATION_YEARMONTH);

			assertEquals(NativeType.DURATION_YEARMONTH, atomBridge.getNativeType(example));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}

		try
		{
			final A atom = atomBridge.compile("P4Y1M", NativeType.DURATION_YEARMONTH);

			assertNotNull(atom);

			final A strval = atomBridge.castAs(atom, NativeType.STRING.toQName(), this);

			assertEquals("P4Y1M", atomBridge.getString(strval));

			assertEquals(49, atomBridge.getDurationTotalMonths(atom));

			assertEquals(NativeType.DURATION_YEARMONTH, atomBridge.getNativeType(atom));
			assertEquals(NativeType.DURATION_YEARMONTH.toQName(), atomBridge.getDataType(atom));
		}
		catch (final GxmlAtomCastException e)
		{
			fail();
		}
	}
}
