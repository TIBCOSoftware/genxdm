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
package org.gxml.tests.xs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import junit.framework.TestCase;

import org.gxml.xs.types.SmNativeType;

public final class SmNativeTypeTestCase extends TestCase
{
	private static final int EXPECTED_TOP_LEVEL = 20;

	private List<SmNativeType> child(final SmNativeType parent)
	{
		final ArrayList<SmNativeType> top = new ArrayList<SmNativeType>();

		for (final SmNativeType candidate : SmNativeType.values())
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

	private Map<SmNativeType, SmNativeType> isA()
	{
		final HashMap<SmNativeType, SmNativeType> isA = new HashMap<SmNativeType, SmNativeType>();
		for (final SmNativeType lhs : SmNativeType.values())
		{
			for (final SmNativeType rhs : SmNativeType.values())
			{
				if ((lhs != rhs) && lhs.isA(rhs))
				{
					if (isA.containsKey(lhs))
					{
						final SmNativeType existing = isA.get(lhs);
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

	private void println(final Iterable<SmNativeType> types)
	{
		final StringBuilder sb = new StringBuilder();
		for (final SmNativeType type : types)
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

	public void testCommonType()
	{
		// assertEquals(UberType.DOUBLE, UberType.commonType(UberType.INTEGER, UberType.DOUBLE));
		// assertEquals(UberType.DOUBLE, UberType.commonType(UberType.DOUBLE, UberType.INTEGER));

		assertEquals(SmNativeType.ANY_ATOMIC_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION, SmNativeType.DOUBLE, true));
		assertEquals(SmNativeType.ANY_ATOMIC_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION, SmNativeType.DOUBLE, true));
		assertEquals(SmNativeType.DURATION, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION, SmNativeType.DURATION, true));
		assertEquals(SmNativeType.DURATION, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION_YEARMONTH, SmNativeType.DURATION, true));
		assertEquals(SmNativeType.DURATION, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION, SmNativeType.DURATION_YEARMONTH, true));
		assertEquals(SmNativeType.DURATION, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION_DAYTIME, SmNativeType.DURATION, true));
		assertEquals(SmNativeType.DURATION, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION, SmNativeType.DURATION_DAYTIME, true));
		assertEquals(SmNativeType.DURATION_YEARMONTH, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION_YEARMONTH, SmNativeType.DURATION_YEARMONTH, true));
		assertEquals(SmNativeType.DURATION_DAYTIME, SmNativeType.computeCommonAncestorSelf(SmNativeType.DURATION_DAYTIME, SmNativeType.DURATION_DAYTIME, true));

		assertEquals(SmNativeType.ANY_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.ANY_TYPE, SmNativeType.ANY_TYPE, true));
		assertEquals(SmNativeType.ANY_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.ANY_TYPE, SmNativeType.ANY_SIMPLE_TYPE, false));
		assertEquals(SmNativeType.ANY_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.ANY_SIMPLE_TYPE, SmNativeType.ANY_TYPE, false));
		assertEquals(SmNativeType.ANY_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.ANY_TYPE, SmNativeType.ANY_SIMPLE_TYPE, true));
		assertEquals(SmNativeType.ANY_TYPE, SmNativeType.computeCommonAncestorSelf(SmNativeType.ANY_SIMPLE_TYPE, SmNativeType.ANY_TYPE, true));

		for (final SmNativeType lhs : SmNativeType.values())
		{
			for (final SmNativeType rhs : SmNativeType.values())
			{
				assertSame("Symmetry of commonType", SmNativeType.computeCommonAncestorSelf(lhs, rhs, true), SmNativeType.computeCommonAncestorSelf(rhs, lhs, true));
			}
		}
	}

	public void testGetName()
	{
		assertEquals(XMLConstants.W3C_XML_SCHEMA_NS_URI, SmNativeType.ANY_TYPE.toQName().getNamespaceURI());
		assertEquals("anyType", SmNativeType.ANY_TYPE.toQName().getLocalPart());
	}

	public void testGetType()
	{
		assertEquals(SmNativeType.ANY_TYPE, SmNativeType.getType("anyType"));
		assertEquals(SmNativeType.ANY_SIMPLE_TYPE, SmNativeType.getType("anySimpleType"));
		assertEquals(SmNativeType.ANY_ATOMIC_TYPE, SmNativeType.getType("anyAtomicType"));
		assertEquals(SmNativeType.DOUBLE, SmNativeType.getType("double"));
		assertEquals(SmNativeType.STRING, SmNativeType.getType("string"));
	}

	public void testIsA()
	{
		assertTrue(SmNativeType.ANY_TYPE.isA(SmNativeType.ANY_TYPE));
		assertFalse(SmNativeType.ANY_TYPE.isA(SmNativeType.UNTYPED));
		assertFalse(SmNativeType.ANY_TYPE.isA(SmNativeType.ANY_SIMPLE_TYPE));
		assertFalse(SmNativeType.ANY_TYPE.isA(SmNativeType.ANY_ATOMIC_TYPE));
		assertFalse(SmNativeType.ANY_TYPE.isA(SmNativeType.UNTYPED_ATOMIC));
		assertFalse(SmNativeType.ANY_TYPE.isA(SmNativeType.STRING));

		assertTrue(SmNativeType.ANY_SIMPLE_TYPE.isA(SmNativeType.ANY_TYPE));
		assertFalse(SmNativeType.ANY_SIMPLE_TYPE.isA(SmNativeType.UNTYPED));
		assertTrue(SmNativeType.ANY_SIMPLE_TYPE.isA(SmNativeType.ANY_SIMPLE_TYPE));
		assertFalse(SmNativeType.ANY_SIMPLE_TYPE.isA(SmNativeType.ANY_ATOMIC_TYPE));
		assertFalse(SmNativeType.ANY_SIMPLE_TYPE.isA(SmNativeType.UNTYPED_ATOMIC));
		assertFalse(SmNativeType.ANY_SIMPLE_TYPE.isA(SmNativeType.STRING));

		assertTrue(SmNativeType.ANY_ATOMIC_TYPE.isA(SmNativeType.ANY_TYPE));
		assertFalse(SmNativeType.ANY_ATOMIC_TYPE.isA(SmNativeType.UNTYPED));
		assertTrue(SmNativeType.ANY_ATOMIC_TYPE.isA(SmNativeType.ANY_SIMPLE_TYPE));
		assertTrue(SmNativeType.ANY_ATOMIC_TYPE.isA(SmNativeType.ANY_ATOMIC_TYPE));
		assertFalse(SmNativeType.ANY_ATOMIC_TYPE.isA(SmNativeType.UNTYPED_ATOMIC));
		assertFalse(SmNativeType.ANY_ATOMIC_TYPE.isA(SmNativeType.STRING));

		assertTrue(SmNativeType.UNTYPED_ATOMIC.isA(SmNativeType.ANY_TYPE));
		assertFalse(SmNativeType.UNTYPED_ATOMIC.isA(SmNativeType.UNTYPED));
		assertTrue(SmNativeType.UNTYPED_ATOMIC.isA(SmNativeType.ANY_SIMPLE_TYPE));
		assertTrue(SmNativeType.UNTYPED_ATOMIC.isA(SmNativeType.ANY_ATOMIC_TYPE));
		assertTrue(SmNativeType.UNTYPED_ATOMIC.isA(SmNativeType.UNTYPED_ATOMIC));
		assertFalse(SmNativeType.UNTYPED_ATOMIC.isA(SmNativeType.STRING));
	}

	public void testIsDecimal()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsGregorian()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsInt()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsInteger()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsNumeric()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsPrimitive()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsString()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsToken()
	{
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testIsUrType()
	{
		assertTrue(SmNativeType.ANY_TYPE.isUrType());
		assertTrue(SmNativeType.ANY_SIMPLE_TYPE.isUrType());
		assertTrue(SmNativeType.ANY_ATOMIC_TYPE.isUrType());
		assertFalse(SmNativeType.UNTYPED_ATOMIC.isUrType());
		for (final SmNativeType candidate : SmNativeType.values())
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

	public void testTopLevel()
	{
		// Use the full set of Ur-types and the isA method to calculate the relationships.
		// Then check that all the relationships exist and are correct.
		final Map<SmNativeType, SmNativeType> isA = isA();

		final List<SmNativeType> top = child(SmNativeType.ANY_ATOMIC_TYPE);

		for (@SuppressWarnings("unused")
		final SmNativeType builtInType : SmNativeType.values())
		{
		}

		if (EXPECTED_TOP_LEVEL != top.size())
		{
			println(top);
		}
		assertEquals("Number of top-level types.", EXPECTED_TOP_LEVEL, top.size());

		assertTrue(top.contains(SmNativeType.STRING)); // 1
		assertTrue(top.contains(SmNativeType.UNTYPED_ATOMIC)); // 2
		assertTrue(top.contains(SmNativeType.DATETIME)); // 3
		assertTrue(top.contains(SmNativeType.DATE)); // 4
		assertTrue(top.contains(SmNativeType.TIME)); // 5
		assertTrue(top.contains(SmNativeType.DURATION)); // 6
		assertTrue(top.contains(SmNativeType.FLOAT)); // 7
		assertTrue(top.contains(SmNativeType.DOUBLE)); // 8
		assertTrue(top.contains(SmNativeType.DECIMAL)); // 9
		assertTrue(top.contains(SmNativeType.GYEARMONTH)); // 10
		assertTrue(top.contains(SmNativeType.GYEAR)); // 11
		assertTrue(top.contains(SmNativeType.GMONTHDAY)); // 12
		assertTrue(top.contains(SmNativeType.GDAY)); // 13
		assertTrue(top.contains(SmNativeType.GMONTH)); // 14
		assertTrue(top.contains(SmNativeType.BOOLEAN)); // 15
		assertTrue(top.contains(SmNativeType.BASE64_BINARY)); // 16
		assertTrue(top.contains(SmNativeType.HEX_BINARY)); // 17
		assertTrue(top.contains(SmNativeType.ANY_URI)); // 18
		assertTrue(top.contains(SmNativeType.QNAME)); // 19
		assertTrue(top.contains(SmNativeType.NOTATION)); // 20

		// Every type except the complex UrType (xs:anyType) should be represented.
		assertEquals("Number of types derived from others.", SmNativeType.values().length - 1, isA.size());

		assertEquals(SmNativeType.STRING, isA.get(SmNativeType.NORMALIZED_STRING)); // 1
		assertEquals(SmNativeType.NORMALIZED_STRING, isA.get(SmNativeType.TOKEN)); // 2
		assertEquals(SmNativeType.TOKEN, isA.get(SmNativeType.LANGUAGE)); // 3
		assertEquals(SmNativeType.TOKEN, isA.get(SmNativeType.NMTOKEN)); // 4
		assertEquals(SmNativeType.TOKEN, isA.get(SmNativeType.NAME)); // 5
		assertEquals(SmNativeType.NAME, isA.get(SmNativeType.NCNAME)); // 6
		assertEquals(SmNativeType.NCNAME, isA.get(SmNativeType.ID)); // 7
		assertEquals(SmNativeType.NCNAME, isA.get(SmNativeType.IDREF)); // 8
		assertEquals(SmNativeType.NCNAME, isA.get(SmNativeType.ENTITY)); // 9
		assertEquals(SmNativeType.DURATION, isA.get(SmNativeType.DURATION_YEARMONTH)); // 10
		assertEquals(SmNativeType.DURATION, isA.get(SmNativeType.DURATION_DAYTIME)); // 11
		assertEquals(SmNativeType.DECIMAL, isA.get(SmNativeType.INTEGER)); // 12
		assertEquals(SmNativeType.INTEGER, isA.get(SmNativeType.NON_POSITIVE_INTEGER)); // 13
		assertEquals(SmNativeType.NON_POSITIVE_INTEGER, isA.get(SmNativeType.NEGATIVE_INTEGER)); // 14
		assertEquals(SmNativeType.INTEGER, isA.get(SmNativeType.LONG)); // 15
		assertEquals(SmNativeType.LONG, isA.get(SmNativeType.INT)); // 16
		assertEquals(SmNativeType.INT, isA.get(SmNativeType.SHORT)); // 17
		assertEquals(SmNativeType.SHORT, isA.get(SmNativeType.BYTE)); // 18
		assertEquals(SmNativeType.INTEGER, isA.get(SmNativeType.NON_NEGATIVE_INTEGER)); // 19
		assertEquals(SmNativeType.NON_NEGATIVE_INTEGER, isA.get(SmNativeType.UNSIGNED_LONG)); // 20
		assertEquals(SmNativeType.UNSIGNED_LONG, isA.get(SmNativeType.UNSIGNED_INT)); // 21
		assertEquals(SmNativeType.UNSIGNED_INT, isA.get(SmNativeType.UNSIGNED_SHORT)); // 22
		assertEquals(SmNativeType.UNSIGNED_SHORT, isA.get(SmNativeType.UNSIGNED_BYTE)); // 23
		assertEquals(SmNativeType.NON_NEGATIVE_INTEGER, isA.get(SmNativeType.POSITIVE_INTEGER)); // 24
	}
}
