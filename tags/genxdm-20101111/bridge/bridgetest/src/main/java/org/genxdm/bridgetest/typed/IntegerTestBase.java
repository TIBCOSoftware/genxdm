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

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.namespace.QName;

import org.genxdm.base.ProcessingContext;
import org.genxdm.bridgetest.GxTestBase;
import org.genxdm.exceptions.GxmlAtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.SpillagePolicy;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.typed.types.Emulation;
import org.genxdm.xs.types.NativeType;

/**
 * These unit tests provide broad coverage of casting of integers.
 * <p>
 * Because of the looping nature, they may be tedious to debug. Problematic cases should be investigated using specific tests that make it easier to set breakpoints..
 * </p>
 */
public abstract class IntegerTestBase<N, A> 
    extends GxTestBase<N>
{
	private static final boolean CHECK_SOURCE_TYPE = false;

	private static final short UNSIGNED_BYTE_MAX_VALUE = (short)((short)Byte.MAX_VALUE - (short)Byte.MIN_VALUE); // 255
	private static final short UNSIGNED_BYTE_MIN_VALUE = (short)0;
	private static final long UNSIGNED_INT_MAX_VALUE = (long)((long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE); // 4294967295
	private static final long UNSIGNED_INT_MIN_VALUE = 0;
	private static final BigInteger UNSIGNED_LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE).subtract(BigInteger.valueOf(Long.MIN_VALUE)); // 18446744073709551615
	private static final BigInteger UNSIGNED_LONG_MIN_VALUE = BigInteger.ZERO;
	private static final int UNSIGNED_SHORT_MAX_VALUE = (int)((int)Short.MAX_VALUE - (int)Short.MIN_VALUE); // 65535
	private static final int UNSIGNED_SHORT_MIN_VALUE = 0;

	/**
	 * Attempt to cast the atom to all of the native types defined by {@link NativeType}.
	 * <p>
	 * Introspect the sourceAtom to determine whether the casting output is reasonable.
	 * </p>
	 */
	public void assertCasting(final A sourceAtom, final TypedContext<N, A> pcx)
	{
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();
		final QName sourceType = atomBridge.getDataType(sourceAtom);
		for (final NativeType targetType : NativeType.values())
		{
			try
			{
				final A atom = atomBridge.castAs(sourceAtom, targetType, castingContext(SpillagePolicy.DO_THE_RIGHT_THING, Emulation.C14N));
				switch (targetType)
				{
					case UNTYPED_ATOMIC:
					{
						assertEquals(getCastAsUntypedAtomic(sourceAtom, atomBridge), atomBridge.getC14NForm(atom));
					}
					break;
					case STRING:
					{
						assertEquals(getCastAsString(sourceAtom, atomBridge), atomBridge.getString(atom));
					}
					break;
					case BOOLEAN:
					{
						assertEquals(getCastAsBoolean(sourceAtom, atomBridge), atomBridge.getBoolean(atom));
					}
					break;
					case DOUBLE:
					{
						assertEquals(getCastAsDouble(sourceAtom, atomBridge), atomBridge.getDouble(atom));
					}
					break;
					case FLOAT:
					{
						assertEquals(getCastAsFloat(sourceAtom, atomBridge), atomBridge.getFloat(atom));
					}
					break;
					case DECIMAL:
					{
						assertEquals(getCastAsDecimal(sourceAtom, atomBridge), atomBridge.getDecimal(atom));
					}
					break;
					case INTEGER:
					{
						assertEquals(getCastAsInteger(sourceAtom, atomBridge), atomBridge.getInteger(atom));
					}
					break;
					case NON_POSITIVE_INTEGER:
					{
						final BigInteger expect = getCastAsInteger(sourceAtom, atomBridge);
						assertEquals(expect, atomBridge.getInteger(atom));
						assertTrue(expect.signum() <= 0);
					}
					break;
					case POSITIVE_INTEGER:
					{
						final BigInteger expect = getCastAsInteger(sourceAtom, atomBridge);
						assertEquals(expect, atomBridge.getInteger(atom));
						assertTrue(expect.signum() > 0);
					}
					break;
					case NEGATIVE_INTEGER:
					{
						final BigInteger expect = getCastAsInteger(sourceAtom, atomBridge);
						assertEquals(expect, atomBridge.getInteger(atom));
						assertTrue(expect.signum() < 0);
					}
					break;
					case LONG:
					{
						assertEquals(getCastAsLong(sourceAtom, atomBridge), atomBridge.getLong(atom));
					}
					break;
					case INT:
					{
						assertEquals(message(sourceAtom, sourceType, targetType), getCastAsInt(sourceAtom, atomBridge), atomBridge.getInt(atom));
					}
					break;
					case SHORT:
					{
						assertEquals(getCastAsShort(sourceAtom, atomBridge), atomBridge.getShort(atom));
					}
					break;
					case BYTE:
					{
						assertEquals(getCastAsByte(sourceAtom, atomBridge), atomBridge.getByte(atom));
					}
					break;
					case NON_NEGATIVE_INTEGER:
					{
						assertEquals(getCastAsInteger(sourceAtom, atomBridge), atomBridge.getInteger(atom));
					}
					break;
					case UNSIGNED_LONG:
					{
						final BigInteger expect = getCastAsUnsignedLong(sourceAtom, atomBridge);
						final BigInteger actual = atomBridge.getInteger(atom);
						assertEquals(expect, actual);
					}
					break;
					case UNSIGNED_INT:
					{
						final long expect = getCastAsUnsignedInt(sourceAtom, atomBridge);
						final long actual = atomBridge.getUnsignedInt(atom);
						assertEquals(expect, actual);
					}
					break;
					case UNSIGNED_SHORT:
					{
						final int expect = getCastAsUnsignedShort(sourceAtom, atomBridge);
						final int actual = atomBridge.getUnsignedShort(atom);
						assertEquals(expect, actual);
					}
					break;
					case UNSIGNED_BYTE:
					{
						final short expect = getCastAsUnsignedByte(sourceAtom, atomBridge);
						final short actual = atomBridge.getUnsignedByte(atom);
						assertEquals(expect, actual);
					}
					break;
					case NORMALIZED_STRING:
					{
						assertEquals(getCastAsString(sourceAtom, atomBridge), atomBridge.getString(atom));
					}
					break;
					case TOKEN:
					{
						assertEquals(getCastAsString(sourceAtom, atomBridge), atomBridge.getString(atom));
					}
					break;
					case NMTOKEN:
					{
						assertEquals(getCastAsString(sourceAtom, atomBridge), atomBridge.getString(atom));
					}
					break;
					default:
					{
						throw new AssertionError(targetType);
					}
				}
				// assertEquals(ZERO, atomBridge.getInteger(atom));
			}
			catch (final GxmlAtomCastException e)
			{
				switch (targetType)
				{
					case ANY_TYPE:
					case ANY_SIMPLE_TYPE:
					case ANY_ATOMIC_TYPE:
					case UNTYPED:
					{

					}
					break;
					case DATE:
					case DATETIME:
					case TIME:
					case GYEARMONTH:
					case GYEAR:
					case GMONTHDAY:
					case GDAY:
					case GMONTH:
					case DURATION:
					case BASE64_BINARY:
					case HEX_BINARY:
					case ANY_URI:
					case QNAME:
					case NOTATION:
					case IDREFS:
					case NMTOKENS:
					case ENTITIES:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
					}
					break;
					case DURATION_DAYTIME:
					case DURATION_YEARMONTH:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
					}
					break;
					case LONG:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0;
								assertTrue(tooBig || tooSmall);
								assertFalse(tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case INT:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							// TODO: Missing LONG, SHORT
							case INTEGER:
							case BYTE:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0;
								assertTrue(message(sourceAtom, sourceType, targetType), tooBig || tooSmall);
								assertFalse(tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case SHORT:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Short.MIN_VALUE)) < 0;
								assertTrue(tooBig || tooSmall);
								assertFalse(tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case BYTE:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Byte.MAX_VALUE)) > 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(Byte.MIN_VALUE)) < 0;
								assertTrue(tooBig || tooSmall);
								assertFalse(tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case NEGATIVE_INTEGER:
					{
						// TODO: Verify that we are here because of the value
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
					}
					break;
					case POSITIVE_INTEGER:
					{
						// TODO: Verify that we are here because of the value
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
					}
					break;
					case NON_POSITIVE_INTEGER:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							case LONG:
							case INT:
							case SHORT:
							case BYTE:
							{
								assertTrue(message(sourceAtom, sourceType, targetType), atomBridge.getInteger(sourceAtom).signum() > 0);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case NON_NEGATIVE_INTEGER:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							{
								assertTrue(atomBridge.getInteger(sourceAtom).signum() < 0);
							}
							break;
							case LONG:
							{
								assertTrue(message(sourceAtom, sourceType, targetType), atomBridge.getLong(sourceAtom) < 0);
							}
							break;
							case INT:
							{
								assertTrue(message(sourceAtom, sourceType, targetType), atomBridge.getInt(sourceAtom) < 0);
							}
							break;
							case SHORT:
							{
								assertTrue(message(sourceAtom, sourceType, targetType), atomBridge.getShort(sourceAtom) < 0);
							}
							break;
							case BYTE:
							{
								assertTrue(message(sourceAtom, sourceType, targetType), atomBridge.getByte(sourceAtom) < 0);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case UNSIGNED_LONG:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(message(sourceAtom, sourceType, targetType), targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							case LONG:
							case INT:
							case SHORT:
							case BYTE:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(UNSIGNED_LONG_MAX_VALUE) >= 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(UNSIGNED_LONG_MIN_VALUE) <= 0;
								assertTrue(missingCast(sourceType, targetType), tooBig || tooSmall);
								assertFalse(message(sourceAtom, sourceType, targetType), tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case UNSIGNED_INT:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							case LONG:
							case INT:
							case SHORT:
							case BYTE:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(UNSIGNED_INT_MAX_VALUE)) >= 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(UNSIGNED_INT_MIN_VALUE)) <= 0;
								assertTrue(missingCast(sourceType, targetType), tooBig || tooSmall);
								assertFalse(message(sourceAtom, sourceType, targetType), tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case UNSIGNED_SHORT:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							case LONG:
							case INT:
							case SHORT:
							case BYTE:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(UNSIGNED_SHORT_MAX_VALUE)) >= 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(UNSIGNED_SHORT_MIN_VALUE)) <= 0;
								assertTrue(missingCast(sourceType, targetType), tooBig || tooSmall);
								assertFalse(message(sourceAtom, sourceType, targetType), tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case UNSIGNED_BYTE:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(message(sourceAtom, sourceType, targetType), targetType.toQName(), e.getTargetType());
						switch (atomBridge.getNativeType(sourceAtom))
						{
							case INTEGER:
							case LONG:
							case INT:
							case SHORT:
							case BYTE:
							{
								final boolean tooBig = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(UNSIGNED_BYTE_MAX_VALUE)) >= 0;
								final boolean tooSmall = atomBridge.getInteger(sourceAtom).compareTo(BigInteger.valueOf(UNSIGNED_BYTE_MIN_VALUE)) <= 0;
								assertTrue(missingCast(sourceType, targetType), tooBig || tooSmall);
								assertFalse(message(sourceAtom, sourceType, targetType), tooBig && tooSmall);
							}
							break;
							default:
							{
								throw new AssertionError(atomBridge.getNativeType(sourceAtom));
							}
						}
					}
					break;
					case LANGUAGE:
					case NAME:
					case NCNAME:
					case ID:
					case IDREF:
					case ENTITY:
					{
						assertSourceType(message(sourceAtom, sourceType, targetType), sourceType, e.getSourceType());
						assertEquals(targetType.toQName(), e.getTargetType());
					}
					break;
					default:
					{
						e.printStackTrace();
						fail(message(sourceAtom, sourceType, targetType) + " : " + e.getMessage());
						throw new AssertionError(targetType);
					}
				}
			}
		}
	}

	private void assertSourceType(final String message, final QName expect, final QName actual)
	{
		if (CHECK_SOURCE_TYPE)
		{
			assertEquals(message, expect, actual);
		}
	}

	private CastingContext<A> castingContext(final SpillagePolicy spillagePolicy, final Emulation emulation)
	{
		PreCondition.assertArgumentNotNull(spillagePolicy, "spillagePolicy");
		PreCondition.assertArgumentNotNull(emulation, "emulation");
		return new CastingContext<A>()
		{
			public Emulation getEmulation()
			{
				return emulation;
			}

			public SpillagePolicy getSpillagePolicy()
			{
				return spillagePolicy;
			}
		};
	}

	private boolean getCastAsBoolean(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).signum() != 0;
			}
			case LONG:
			{
				return atomBridge.getLong(sourceAtom) != 0;
			}
			case INT:
			{
				return atomBridge.getInt(sourceAtom) != 0;
			}
			case SHORT:
			{
				return atomBridge.getShort(sourceAtom) != 0;
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom) != 0;
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private byte getCastAsByte(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				final BigInteger value = atomBridge.getInteger(sourceAtom);
				return value.byteValue();
			}
			case LONG:
			{
				final long value = atomBridge.getLong(sourceAtom);
				assertTrue("Lower bound of xs:byte", value >= Byte.MIN_VALUE);
				assertTrue("Upper bound of xs:byte", value <= Byte.MAX_VALUE);
				return (byte)value;
			}
			case INT:
			{
				final int value = atomBridge.getInt(sourceAtom);
				assertTrue("Lower bound of xs:byte", value >= Byte.MIN_VALUE);
				assertTrue("Upper bound of xs:byte", value <= Byte.MAX_VALUE);
				return (byte)value;
			}
			case SHORT:
			{
				final short value = atomBridge.getShort(sourceAtom);
				assertTrue("Lower bound of xs:byte", value >= Byte.MIN_VALUE);
				assertTrue("Upper bound of xs:byte", value <= Byte.MAX_VALUE);
				return (byte)value;
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom);
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private BigDecimal getCastAsDecimal(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return new BigDecimal(atomBridge.getInteger(sourceAtom));
			}
			case LONG:
			{
				return BigDecimal.valueOf(atomBridge.getLong(sourceAtom));
			}
			case INT:
			{
				return BigDecimal.valueOf(atomBridge.getInt(sourceAtom));
			}
			case SHORT:
			{
				return BigDecimal.valueOf(atomBridge.getShort(sourceAtom));
			}
			case BYTE:
			{
				return BigDecimal.valueOf(atomBridge.getByte(sourceAtom));
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private double getCastAsDouble(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).doubleValue();
			}
			case LONG:
			{
				return atomBridge.getLong(sourceAtom);
			}
			case INT:
			{
				return atomBridge.getInt(sourceAtom);
			}
			case SHORT:
			{
				return atomBridge.getShort(sourceAtom);
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom);
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private float getCastAsFloat(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).floatValue();
			}
			case LONG:
			{
				return atomBridge.getLong(sourceAtom);
			}
			case INT:
			{
				return atomBridge.getInt(sourceAtom);
			}
			case SHORT:
			{
				return atomBridge.getShort(sourceAtom);
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom);
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private int getCastAsInt(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).intValue();
			}
			case LONG:
			{
				final long value = atomBridge.getLong(sourceAtom);
				assertTrue("Lower bound of xs:int", value >= Integer.MIN_VALUE);
				assertTrue("Upper bound of xs:int", value <= Integer.MAX_VALUE);
				return (int)value;
			}
			case INT:
			{
				return atomBridge.getInt(sourceAtom);
			}
			case SHORT:
			{
				return atomBridge.getShort(sourceAtom);
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom);
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private BigInteger getCastAsInteger(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom);
			}
			case LONG:
			{
				return BigInteger.valueOf(atomBridge.getLong(sourceAtom));
			}
			case INT:
			{
				return BigInteger.valueOf(atomBridge.getInt(sourceAtom));
			}
			case SHORT:
			{
				return BigInteger.valueOf(atomBridge.getShort(sourceAtom));
			}
			case BYTE:
			{
				return BigInteger.valueOf(atomBridge.getByte(sourceAtom));
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private long getCastAsLong(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).longValue();
			}
			case LONG:
			{
				return atomBridge.getLong(sourceAtom);
			}
			case INT:
			{
				return atomBridge.getInt(sourceAtom);
			}
			case SHORT:
			{
				return atomBridge.getShort(sourceAtom);
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom);
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private short getCastAsShort(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).shortValue();
			}
			case LONG:
			{
				final long value = atomBridge.getLong(sourceAtom);
				assertTrue("Lower bound of xs:short", value >= Short.MIN_VALUE);
				assertTrue("Upper bound of xs:short", value <= Short.MAX_VALUE);
				return (short)value;
			}
			case INT:
			{
				final int value = atomBridge.getInt(sourceAtom);
				assertTrue("Lower bound of xs:short", value >= Short.MIN_VALUE);
				assertTrue("Upper bound of xs:short", value <= Short.MAX_VALUE);
				return (short)value;
			}
			case SHORT:
			{
				return atomBridge.getShort(sourceAtom);
			}
			case BYTE:
			{
				return atomBridge.getByte(sourceAtom);
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private String getCastAsString(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).toString();
			}
			case LONG:
			{
				return Long.toString(atomBridge.getLong(sourceAtom));
			}
			case INT:
			{
				return Integer.toString(atomBridge.getInt(sourceAtom));
			}
			case SHORT:
			{
				return Short.toString(atomBridge.getShort(sourceAtom));
			}
			case BYTE:
			{
				return Byte.toString(atomBridge.getByte(sourceAtom));
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private short getCastAsUnsignedByte(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		final NativeType sourceType = atomBridge.getNativeType(sourceAtom);
		switch (sourceType)
		{
			case INTEGER:
			case LONG:
			case INT:
			case SHORT:
			case BYTE:
			{
				final BigInteger value = atomBridge.getInteger(sourceAtom);
				assertTrue(lowerBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_BYTE), value.compareTo(BigInteger.valueOf(UNSIGNED_BYTE_MIN_VALUE)) >= 0);
				assertTrue(upperBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_BYTE), value.compareTo(BigInteger.valueOf(UNSIGNED_BYTE_MAX_VALUE)) <= 0);
				return value.shortValue();
			}
			default:
			{
				throw new AssertionError(sourceType);
			}
		}
	}

	private long getCastAsUnsignedInt(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		final NativeType sourceType = atomBridge.getNativeType(sourceAtom);
		switch (sourceType)
		{
			case INTEGER:
			case LONG:
			case INT:
			case SHORT:
			case BYTE:
			{
				final BigInteger value = atomBridge.getInteger(sourceAtom);
				assertTrue(lowerBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_INT), value.compareTo(BigInteger.valueOf(UNSIGNED_INT_MIN_VALUE)) >= 0);
				assertTrue(upperBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_INT), value.compareTo(BigInteger.valueOf(UNSIGNED_INT_MAX_VALUE)) <= 0);
				return value.longValue();
			}
			default:
			{
				throw new AssertionError(sourceType);
			}
		}
	}

	private BigInteger getCastAsUnsignedLong(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		final NativeType sourceType = atomBridge.getNativeType(sourceAtom);
		switch (sourceType)
		{
			case INTEGER:
			case LONG:
			case INT:
			case SHORT:
			case BYTE:
			{
				final BigInteger value = atomBridge.getInteger(sourceAtom);
				assertTrue(lowerBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_LONG), value.compareTo(UNSIGNED_LONG_MIN_VALUE) >= 0);
				assertTrue(upperBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_LONG), value.compareTo(UNSIGNED_LONG_MAX_VALUE) <= 0);
				return value;
			}
			default:
			{
				throw new AssertionError(sourceType);
			}
		}
	}

	private int getCastAsUnsignedShort(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		final NativeType sourceType = atomBridge.getNativeType(sourceAtom);
		switch (sourceType)
		{
			case INTEGER:
			case LONG:
			case INT:
			case SHORT:
			case BYTE:
			{
				final BigInteger value = atomBridge.getInteger(sourceAtom);
				assertTrue(lowerBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_SHORT), value.compareTo(BigInteger.valueOf(UNSIGNED_SHORT_MIN_VALUE)) >= 0);
				assertTrue(upperBoundViolation(sourceAtom, sourceType.toQName(), NativeType.UNSIGNED_SHORT), value.compareTo(BigInteger.valueOf(UNSIGNED_SHORT_MAX_VALUE)) <= 0);
				return value.intValue();
			}
			default:
			{
				throw new AssertionError(sourceType);
			}
		}
	}

	private String getCastAsUntypedAtomic(final A sourceAtom, final AtomBridge<A> atomBridge)
	{
		switch (atomBridge.getNativeType(sourceAtom))
		{
			case INTEGER:
			{
				return atomBridge.getInteger(sourceAtom).toString();
			}
			case LONG:
			{
				return Long.toString(atomBridge.getLong(sourceAtom));
			}
			case INT:
			{
				return Integer.toString(atomBridge.getInt(sourceAtom));
			}
			case SHORT:
			{
				return Short.toString(atomBridge.getShort(sourceAtom));
			}
			case BYTE:
			{
				return Byte.toString(atomBridge.getByte(sourceAtom));
			}
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(sourceAtom));
			}
		}
	}

	private String message(final A sourceAtom, final QName sourceType, final NativeType targetType)
	{
		return sourceAtom + " : " + sourceType.getLocalPart() + " => " + targetType.getLocalName();
	}

	private String lowerBoundViolation(final A sourceAtom, final QName sourceType, final NativeType targetType)
	{
		return "lower-bound violation: " + sourceAtom + ": " + sourceType.getLocalPart() + " => " + targetType.getLocalName();
	}

	private String upperBoundViolation(final A sourceAtom, final QName sourceType, final NativeType targetType)
	{
		return "upper-bound violation: " + sourceAtom + ": " + sourceType.getLocalPart() + " => " + targetType.getLocalName();
	}

	private String missingCast(final QName sourceType, final NativeType targetType)
	{
		return "missing cast from " + sourceType.getLocalPart() + " to " + targetType.getLocalName();
	}

	public void testCastingRanges()
	{
	    final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		// xs:long
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Long.MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Long.MIN_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)), pcx);

		// xs:int
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Integer.MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Integer.MIN_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Integer.MIN_VALUE).subtract(BigInteger.ONE)), pcx);

		// xs:short
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Short.MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Short.MIN_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Short.MAX_VALUE).add(BigInteger.ONE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Short.MIN_VALUE).subtract(BigInteger.ONE)), pcx);

		// xs:byte
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Byte.MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Byte.MIN_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Byte.MAX_VALUE).add(BigInteger.ONE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(Byte.MIN_VALUE).subtract(BigInteger.ONE)), pcx);

		// xs:unsignedLong
		assertEquals(new BigInteger("18446744073709551615"), UNSIGNED_LONG_MAX_VALUE);
		assertCasting(atomBridge.createInteger(UNSIGNED_LONG_MAX_VALUE), pcx);
		assertCasting(atomBridge.createInteger(UNSIGNED_LONG_MAX_VALUE.add(BigInteger.ONE)), pcx);

		// xs:unsignedInt
		assertEquals(4294967295l, UNSIGNED_INT_MAX_VALUE);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(UNSIGNED_INT_MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(UNSIGNED_INT_MAX_VALUE).add(BigInteger.ONE)), pcx);

		// xs:unsignedShort
		assertEquals(65535, UNSIGNED_SHORT_MAX_VALUE);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(UNSIGNED_SHORT_MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(UNSIGNED_SHORT_MAX_VALUE).add(BigInteger.ONE)), pcx);

		// xs:unsignedByte
		assertEquals(255, UNSIGNED_BYTE_MAX_VALUE);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(UNSIGNED_BYTE_MAX_VALUE)), pcx);
		assertCasting(atomBridge.createInteger(BigInteger.valueOf(UNSIGNED_BYTE_MAX_VALUE).add(BigInteger.ONE)), pcx);
	}

	public void testCastingUnity()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCasting(atomBridge.createInteger(+1), pcx);
		assertCasting(atomBridge.createInteger(-1), pcx);
		assertCasting(atomBridge.createLong(+1), pcx);
		assertCasting(atomBridge.createLong(-1), pcx);
		assertCasting(atomBridge.createInt(+1), pcx);
		assertCasting(atomBridge.createInt(-1), pcx);
		assertCasting(atomBridge.createShort((short)+1), pcx);
		assertCasting(atomBridge.createShort((short)-1), pcx);
		assertCasting(atomBridge.createByte((byte)+1), pcx);
		assertCasting(atomBridge.createByte((byte)-1), pcx);
	}

	public void testCastingZero()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();
		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertCasting(atomBridge.createInteger(0), pcx);
		assertCasting(atomBridge.createLong(0), pcx);
		assertCasting(atomBridge.createInt(0), pcx);
		assertCasting(atomBridge.createShort((short)0), pcx);
		assertCasting(atomBridge.createByte((byte)0), pcx);
	}
}
