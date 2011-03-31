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
package org.genxdm.apitest.xs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.genxdm.xs.types.NativeType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class NativeTypeTestCase
{
	private static final int EXPECTED_TOP_LEVEL = 20;

	private List<NativeType> child(final NativeType parent)
	{
		final ArrayList<NativeType> top = new ArrayList<NativeType>();

		for (final NativeType candidate : NativeType.values())
		{
			if (!candidate.isUrType())
			{
				if (candidate.getParent() == parent)
				{
					top.add(candidate);
				}
			}
		}
		return top;
	}

	private Map<NativeType, NativeType> isA()
	{
		final HashMap<NativeType, NativeType> isA = new HashMap<NativeType, NativeType>();
		for (final NativeType lhs : NativeType.values())
		{
			for (final NativeType rhs : NativeType.values())
			{
				if ((lhs != rhs) && lhs.isA(rhs))
				{
					if (isA.containsKey(lhs))
					{
						final NativeType existing = isA.get(lhs);
						if (rhs.isA(existing))
						{
							isA.put(lhs, rhs);
						}
						else
						{
							// Don't replace
						}
					}
					else
					{
						isA.put(lhs, rhs);
					}
				}
			}
		}
		return Collections.unmodifiableMap(isA);
	}

	private void println(final Iterable<NativeType> types)
	{
		final StringBuilder sb = new StringBuilder();
		for (final NativeType type : types)
		{
			sb.setLength(0);
			sb.append(type.name());
			if (type.isUrType())
			{
				sb.append(", isUrType");
			}
			if (type.isGregorian())
			{
				sb.append(", isGregorian");
			}
			if (type.isNumeric())
			{
				sb.append(", isNumeric");
			}
			System.out.println(sb.toString());
		}
	}

	@Test
	public void testCommonType()
	{
		// assertEquals(UberType.DOUBLE, UberType.commonType(UberType.INTEGER, UberType.DOUBLE));
		// assertEquals(UberType.DOUBLE, UberType.commonType(UberType.DOUBLE, UberType.INTEGER));

		assertEquals(NativeType.ANY_ATOMIC_TYPE, NativeType.computeCommonAncestorSelf(NativeType.DURATION, NativeType.DOUBLE, true));
		assertEquals(NativeType.ANY_ATOMIC_TYPE, NativeType.computeCommonAncestorSelf(NativeType.DURATION, NativeType.DOUBLE, true));
		assertEquals(NativeType.DURATION, NativeType.computeCommonAncestorSelf(NativeType.DURATION, NativeType.DURATION, true));
		assertEquals(NativeType.DURATION, NativeType.computeCommonAncestorSelf(NativeType.DURATION_YEARMONTH, NativeType.DURATION, true));
		assertEquals(NativeType.DURATION, NativeType.computeCommonAncestorSelf(NativeType.DURATION, NativeType.DURATION_YEARMONTH, true));
		assertEquals(NativeType.DURATION, NativeType.computeCommonAncestorSelf(NativeType.DURATION_DAYTIME, NativeType.DURATION, true));
		assertEquals(NativeType.DURATION, NativeType.computeCommonAncestorSelf(NativeType.DURATION, NativeType.DURATION_DAYTIME, true));
		assertEquals(NativeType.DURATION_YEARMONTH, NativeType.computeCommonAncestorSelf(NativeType.DURATION_YEARMONTH, NativeType.DURATION_YEARMONTH, true));
		assertEquals(NativeType.DURATION_DAYTIME, NativeType.computeCommonAncestorSelf(NativeType.DURATION_DAYTIME, NativeType.DURATION_DAYTIME, true));

		assertEquals(NativeType.ANY_TYPE, NativeType.computeCommonAncestorSelf(NativeType.ANY_TYPE, NativeType.ANY_TYPE, true));
		assertEquals(NativeType.ANY_TYPE, NativeType.computeCommonAncestorSelf(NativeType.ANY_TYPE, NativeType.ANY_SIMPLE_TYPE, false));
		assertEquals(NativeType.ANY_TYPE, NativeType.computeCommonAncestorSelf(NativeType.ANY_SIMPLE_TYPE, NativeType.ANY_TYPE, false));
		assertEquals(NativeType.ANY_TYPE, NativeType.computeCommonAncestorSelf(NativeType.ANY_TYPE, NativeType.ANY_SIMPLE_TYPE, true));
		assertEquals(NativeType.ANY_TYPE, NativeType.computeCommonAncestorSelf(NativeType.ANY_SIMPLE_TYPE, NativeType.ANY_TYPE, true));

		for (final NativeType lhs : NativeType.values())
		{
			for (final NativeType rhs : NativeType.values())
			{
				assertSame("Symmetry of commonType", NativeType.computeCommonAncestorSelf(lhs, rhs, true), NativeType.computeCommonAncestorSelf(rhs, lhs, true));
			}
		}
	}

	@Test
	public void testGetName()
	{
		assertEquals(XMLConstants.W3C_XML_SCHEMA_NS_URI, NativeType.ANY_TYPE.toQName().getNamespaceURI());
		assertEquals("anyType", NativeType.ANY_TYPE.toQName().getLocalPart());
	}

	@Test
	public void testGetType()
	{
		assertEquals(NativeType.ANY_TYPE, NativeType.getType("anyType"));
		assertEquals(NativeType.ANY_SIMPLE_TYPE, NativeType.getType("anySimpleType"));
		assertEquals(NativeType.ANY_ATOMIC_TYPE, NativeType.getType("anyAtomicType"));
		assertEquals(NativeType.DOUBLE, NativeType.getType("double"));
		assertEquals(NativeType.STRING, NativeType.getType("string"));
	}

	@Test
	public void testIsA()
	{
		assertTrue(NativeType.ANY_TYPE.isA(NativeType.ANY_TYPE));
		assertFalse(NativeType.ANY_TYPE.isA(NativeType.UNTYPED));
		assertFalse(NativeType.ANY_TYPE.isA(NativeType.ANY_SIMPLE_TYPE));
		assertFalse(NativeType.ANY_TYPE.isA(NativeType.ANY_ATOMIC_TYPE));
		assertFalse(NativeType.ANY_TYPE.isA(NativeType.UNTYPED_ATOMIC));
		assertFalse(NativeType.ANY_TYPE.isA(NativeType.STRING));

		assertTrue(NativeType.ANY_SIMPLE_TYPE.isA(NativeType.ANY_TYPE));
		assertFalse(NativeType.ANY_SIMPLE_TYPE.isA(NativeType.UNTYPED));
		assertTrue(NativeType.ANY_SIMPLE_TYPE.isA(NativeType.ANY_SIMPLE_TYPE));
		assertFalse(NativeType.ANY_SIMPLE_TYPE.isA(NativeType.ANY_ATOMIC_TYPE));
		assertFalse(NativeType.ANY_SIMPLE_TYPE.isA(NativeType.UNTYPED_ATOMIC));
		assertFalse(NativeType.ANY_SIMPLE_TYPE.isA(NativeType.STRING));

		assertTrue(NativeType.ANY_ATOMIC_TYPE.isA(NativeType.ANY_TYPE));
		assertFalse(NativeType.ANY_ATOMIC_TYPE.isA(NativeType.UNTYPED));
		assertTrue(NativeType.ANY_ATOMIC_TYPE.isA(NativeType.ANY_SIMPLE_TYPE));
		assertTrue(NativeType.ANY_ATOMIC_TYPE.isA(NativeType.ANY_ATOMIC_TYPE));
		assertFalse(NativeType.ANY_ATOMIC_TYPE.isA(NativeType.UNTYPED_ATOMIC));
		assertFalse(NativeType.ANY_ATOMIC_TYPE.isA(NativeType.STRING));

		assertTrue(NativeType.UNTYPED_ATOMIC.isA(NativeType.ANY_TYPE));
		assertFalse(NativeType.UNTYPED_ATOMIC.isA(NativeType.UNTYPED));
		assertTrue(NativeType.UNTYPED_ATOMIC.isA(NativeType.ANY_SIMPLE_TYPE));
		assertTrue(NativeType.UNTYPED_ATOMIC.isA(NativeType.ANY_ATOMIC_TYPE));
		assertTrue(NativeType.UNTYPED_ATOMIC.isA(NativeType.UNTYPED_ATOMIC));
		assertFalse(NativeType.UNTYPED_ATOMIC.isA(NativeType.STRING));
	}

	@Test
	public void testIsDecimal()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case DECIMAL:
				case INTEGER:
				case POSITIVE_INTEGER:
				case NEGATIVE_INTEGER:
				case NON_POSITIVE_INTEGER:
				case NON_NEGATIVE_INTEGER:
				case LONG:
				case INT:
				case SHORT:
				case BYTE:
				case UNSIGNED_LONG:
				case UNSIGNED_INT:
				case UNSIGNED_SHORT:
				case UNSIGNED_BYTE:
				{
					assertTrue(candidate.name(), candidate.isDecimal());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isDecimal());
				}
			}
		}
	}

	@Test
	public void testIsGregorian()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case DATETIME:
				case DATE:
				case TIME:
				case GYEARMONTH:
				case GYEAR:
				case GMONTHDAY:
				case GMONTH:
				case GDAY:
				{
					assertTrue(candidate.name(), candidate.isGregorian());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isGregorian());
				}
			}
		}
	}

	@Test
	public void testIsInt()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case INT:
				case SHORT:
				case BYTE:
				{
					assertTrue(candidate.name(), candidate.isInt());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isInt());
				}
			}
		}
	}

	@Test
	public void testIsInteger()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case INTEGER:
				case POSITIVE_INTEGER:
				case NEGATIVE_INTEGER:
				case NON_POSITIVE_INTEGER:
				case NON_NEGATIVE_INTEGER:
				case LONG:
				case INT:
				case SHORT:
				case BYTE:
				case UNSIGNED_LONG:
				case UNSIGNED_INT:
				case UNSIGNED_SHORT:
				case UNSIGNED_BYTE:
				{
					assertTrue(candidate.name(), candidate.isInteger());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isInteger());
				}
			}
		}
	}

	@Test
	public void testIsNumeric()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case DOUBLE:
				case FLOAT:
				case DECIMAL:
				case INTEGER:
				case POSITIVE_INTEGER:
				case NEGATIVE_INTEGER:
				case NON_POSITIVE_INTEGER:
				case NON_NEGATIVE_INTEGER:
				case LONG:
				case INT:
				case SHORT:
				case BYTE:
				case UNSIGNED_LONG:
				case UNSIGNED_INT:
				case UNSIGNED_SHORT:
				case UNSIGNED_BYTE:
				{
					assertTrue(candidate.name(), candidate.isNumeric());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isNumeric());
				}
			}
		}
	}

	@Test
	public void testIsPrimitive()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case UNTYPED_ATOMIC:
				case STRING:
				case ANY_URI:
				case NOTATION:
				case QNAME:
				{
					assertTrue(candidate.name(), candidate.isPrimitive());
				}
				break;
				case BOOLEAN:
				case HEX_BINARY:
				case BASE64_BINARY:
				{
					assertTrue(candidate.name(), candidate.isPrimitive());
				}
				break;
				case DOUBLE:
				case FLOAT:
				case DECIMAL:
				{
					assertTrue(candidate.name(), candidate.isPrimitive());
				}
				break;
				case DURATION:
				{
					assertTrue(candidate.name(), candidate.isPrimitive());
				}
				break;
				case DATETIME:
				case DATE:
				case TIME:
				case GYEARMONTH:
				case GYEAR:
				case GMONTHDAY:
				case GMONTH:
				case GDAY:
				{
					assertTrue(candidate.name(), candidate.isPrimitive());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isPrimitive());
				}
			}
		}
	}

	@Test
	public void testIsString()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
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
					assertTrue(candidate.name(), candidate.isString());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isString());
				}
			}
		}
	}

	@Test
	public void testIsToken()
	{
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case TOKEN:
				case LANGUAGE:
				case NAME:
				case NMTOKEN:
				case NCNAME:
				case ID:
				case IDREF:
				case ENTITY:
				{
					assertTrue(candidate.name(), candidate.isToken());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isToken());
				}
			}
		}
	}

	@Test
	public void testIsUrType()
	{
		assertTrue(NativeType.ANY_TYPE.isUrType());
		assertTrue(NativeType.ANY_SIMPLE_TYPE.isUrType());
		assertTrue(NativeType.ANY_ATOMIC_TYPE.isUrType());
		assertFalse(NativeType.UNTYPED_ATOMIC.isUrType());
		for (final NativeType candidate : NativeType.values())
		{
			switch (candidate)
			{
				case ANY_TYPE:
				case ANY_SIMPLE_TYPE:
				case ANY_ATOMIC_TYPE:
				{
					assertTrue(candidate.name(), candidate.isUrType());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isUrType());
				}
			}
		}
	}

	@Test
	public void testTopLevel()
	{
		// Use the full set of Ur-types and the isA method to calculate the relationships.
		// Then check that all the relationships exist and are correct.
		final Map<NativeType, NativeType> isA = isA();

		final List<NativeType> top = child(NativeType.ANY_ATOMIC_TYPE);

		for (@SuppressWarnings("unused")
		final NativeType builtInType : NativeType.values())
		{
		}

		if (EXPECTED_TOP_LEVEL != top.size())
		{
			println(top);
		}
		assertEquals("Number of top-level types.", EXPECTED_TOP_LEVEL, top.size());

		assertTrue(top.contains(NativeType.STRING)); // 1
		assertTrue(top.contains(NativeType.UNTYPED_ATOMIC)); // 2
		assertTrue(top.contains(NativeType.DATETIME)); // 3
		assertTrue(top.contains(NativeType.DATE)); // 4
		assertTrue(top.contains(NativeType.TIME)); // 5
		assertTrue(top.contains(NativeType.DURATION)); // 6
		assertTrue(top.contains(NativeType.FLOAT)); // 7
		assertTrue(top.contains(NativeType.DOUBLE)); // 8
		assertTrue(top.contains(NativeType.DECIMAL)); // 9
		assertTrue(top.contains(NativeType.GYEARMONTH)); // 10
		assertTrue(top.contains(NativeType.GYEAR)); // 11
		assertTrue(top.contains(NativeType.GMONTHDAY)); // 12
		assertTrue(top.contains(NativeType.GDAY)); // 13
		assertTrue(top.contains(NativeType.GMONTH)); // 14
		assertTrue(top.contains(NativeType.BOOLEAN)); // 15
		assertTrue(top.contains(NativeType.BASE64_BINARY)); // 16
		assertTrue(top.contains(NativeType.HEX_BINARY)); // 17
		assertTrue(top.contains(NativeType.ANY_URI)); // 18
		assertTrue(top.contains(NativeType.QNAME)); // 19
		assertTrue(top.contains(NativeType.NOTATION)); // 20

		// Every type except the complex UrType (xs:anyType) should be represented.
		assertEquals("Number of types derived from others.", NativeType.values().length - 1, isA.size());

		assertEquals(NativeType.STRING, isA.get(NativeType.NORMALIZED_STRING)); // 1
		assertEquals(NativeType.NORMALIZED_STRING, isA.get(NativeType.TOKEN)); // 2
		assertEquals(NativeType.TOKEN, isA.get(NativeType.LANGUAGE)); // 3
		assertEquals(NativeType.TOKEN, isA.get(NativeType.NMTOKEN)); // 4
		assertEquals(NativeType.TOKEN, isA.get(NativeType.NAME)); // 5
		assertEquals(NativeType.NAME, isA.get(NativeType.NCNAME)); // 6
		assertEquals(NativeType.NCNAME, isA.get(NativeType.ID)); // 7
		assertEquals(NativeType.NCNAME, isA.get(NativeType.IDREF)); // 8
		assertEquals(NativeType.NCNAME, isA.get(NativeType.ENTITY)); // 9
		assertEquals(NativeType.DURATION, isA.get(NativeType.DURATION_YEARMONTH)); // 10
		assertEquals(NativeType.DURATION, isA.get(NativeType.DURATION_DAYTIME)); // 11
		assertEquals(NativeType.DECIMAL, isA.get(NativeType.INTEGER)); // 12
		assertEquals(NativeType.INTEGER, isA.get(NativeType.NON_POSITIVE_INTEGER)); // 13
		assertEquals(NativeType.NON_POSITIVE_INTEGER, isA.get(NativeType.NEGATIVE_INTEGER)); // 14
		assertEquals(NativeType.INTEGER, isA.get(NativeType.LONG)); // 15
		assertEquals(NativeType.LONG, isA.get(NativeType.INT)); // 16
		assertEquals(NativeType.INT, isA.get(NativeType.SHORT)); // 17
		assertEquals(NativeType.SHORT, isA.get(NativeType.BYTE)); // 18
		assertEquals(NativeType.INTEGER, isA.get(NativeType.NON_NEGATIVE_INTEGER)); // 19
		assertEquals(NativeType.NON_NEGATIVE_INTEGER, isA.get(NativeType.UNSIGNED_LONG)); // 20
		assertEquals(NativeType.UNSIGNED_LONG, isA.get(NativeType.UNSIGNED_INT)); // 21
		assertEquals(NativeType.UNSIGNED_INT, isA.get(NativeType.UNSIGNED_SHORT)); // 22
		assertEquals(NativeType.UNSIGNED_SHORT, isA.get(NativeType.UNSIGNED_BYTE)); // 23
		assertEquals(NativeType.NON_NEGATIVE_INTEGER, isA.get(NativeType.POSITIVE_INTEGER)); // 24
	}
}
