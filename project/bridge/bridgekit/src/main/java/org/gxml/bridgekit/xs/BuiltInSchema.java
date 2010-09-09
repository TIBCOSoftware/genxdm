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
package org.gxml.bridgekit.xs;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.gxml.names.NameSource;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.components.SmAttribute;
import org.gxml.xs.components.SmAttributeGroup;
import org.gxml.xs.components.SmComponentBag;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.components.SmModelGroup;
import org.gxml.xs.components.SmNotation;
import org.gxml.xs.constraints.SmAttributeUse;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.types.SmAtomicType;
import org.gxml.xs.types.SmAtomicUrType;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmComplexUrType;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmSimpleUrType;
import org.gxml.xs.types.SmType;

final class BuiltInSchema<A> implements SmComponentBag<A>
{
	private final SmAtomicUrType<A> ANY_ATOMIC_TYPE;
	private final SmSimpleUrType<A> ANY_SIMPLE_TYPE;
	private final SmComplexUrType<A> ANY_COMPLEX_TYPE;
	final SmSimpleType<A> ANY_URI;
	final SmSimpleType<A> BASE64_BINARY;
	final SmAtomicType<A> BOOLEAN;
	final SmSimpleType<A> BYTE;
	final SmSimpleType<A> DATE;
	final SmSimpleType<A> DATETIME;
	final SmSimpleType<A> DECIMAL;
	final SmSimpleType<A> DOUBLE;
	final SmSimpleType<A> DURATION;
	final SmSimpleType<A> DURATION_DAYTIME;
	final SmSimpleType<A> DURATION_YEARMONTH;
	final SmSimpleType<A> ENTITIES;
	final SmSimpleType<A> ENTITY;
	final SmSimpleType<A> FLOAT;
	final SmSimpleType<A> GDAY;
	final SmSimpleType<A> GMONTH;
	final SmSimpleType<A> GMONTHDAY;
	final SmSimpleType<A> GYEAR;
	final SmSimpleType<A> GYEARMONTH;
	final SmSimpleType<A> HEX_BINARY;
	final SmSimpleType<A> ID;
	final SmSimpleType<A> IDREF;
	final SmSimpleType<A> IDREFS;
	final SmSimpleType<A> INT;
	final SmSimpleType<A> INTEGER;
	final SmSimpleType<A> LANGUAGE;
	final SmSimpleType<A> LONG;
	private final HashMap<QName, SmComplexType<A>> m_complexTypes = new HashMap<QName, SmComplexType<A>>();
	private final HashMap<QName, SmSimpleType<A>> m_simpleTypes = new HashMap<QName, SmSimpleType<A>>();
	final SmSimpleType<A> NAME;
	final SmSimpleType<A> NCNAME;
	final SmSimpleType<A> NEGATIVE_INTEGER;
	final SmSimpleType<A> NMTOKEN;
	final SmSimpleType<A> NMTOKENS;
	final SmSimpleType<A> NON_NEGATIVE_INTEGER;
	final SmSimpleType<A> NON_POSITIVE_INTEGER;
	final SmSimpleType<A> NORMALIZED_STRING;
	final SmSimpleType<A> NOTATION;
	final SmSimpleType<A> POSITIVE_INTEGER;
	final SmSimpleType<A> QNAME;
	final SmSimpleType<A> SHORT;
	final SmSimpleType<A> STRING;
	final SmSimpleType<A> TIME;
	final SmSimpleType<A> TOKEN;
	final SmSimpleType<A> UNSIGNED_BYTE;
	final SmSimpleType<A> UNSIGNED_INT;

	final SmSimpleType<A> UNSIGNED_LONG;
	final SmSimpleType<A> UNSIGNED_SHORT;
	final ComplexTypeImpl<A> UNTYPED;

	final SmSimpleType<A> UNTYPED_ATOMIC;

	/**
	 * Constructs the W3C XML Schema native types and atributes.
	 */
	public BuiltInSchema(final String W3C_XML_SCHEMA_NS_URI, final SmCacheImpl<A> cache)
	{
		final AtomBridge<A> atomBridge = cache.getAtomBridge();
		final NameSource nameBridge = atomBridge.getNameBridge();

		ANY_COMPLEX_TYPE = cache.getComplexUrType();
		ANY_SIMPLE_TYPE = cache.getSimpleUrType();
		ANY_ATOMIC_TYPE = cache.getAtomicUrType();

		final Map<QName, SmAttributeUse<A>> EMPTY_ATTRIBUTE_USES = Collections.emptyMap();
		UNTYPED = new ComplexTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "untyped"), true, false, SmScopeExtent.Global, ANY_COMPLEX_TYPE, SmDerivationMethod.Restriction, EMPTY_ATTRIBUTE_USES, ANY_COMPLEX_TYPE.getContentType(), EnumSet
				.noneOf(SmDerivationMethod.class), nameBridge, cache);
		UNTYPED.setAttributeWildcard(ANY_COMPLEX_TYPE.getAttributeWildcard());
		UNTYPED_ATOMIC = new UntypedAtomicType<A>(name(W3C_XML_SCHEMA_NS_URI, "untypedAtomic"), ANY_ATOMIC_TYPE, atomBridge);

		STRING = new StringType<A>(name(W3C_XML_SCHEMA_NS_URI, "string"), ANY_ATOMIC_TYPE, atomBridge);
		NORMALIZED_STRING = new NormalizedStringType<A>(name(W3C_XML_SCHEMA_NS_URI, "normalizedString"), STRING, atomBridge);
		TOKEN = new TokenType<A>(name(W3C_XML_SCHEMA_NS_URI, "token"), NORMALIZED_STRING, atomBridge);
		LANGUAGE = new LanguageType<A>(name(W3C_XML_SCHEMA_NS_URI, "language"), TOKEN, atomBridge);
		NMTOKEN = new NMTOKENType<A>(name(W3C_XML_SCHEMA_NS_URI, "NMTOKEN"), TOKEN, atomBridge);
		NAME = new NameType<A>(name(W3C_XML_SCHEMA_NS_URI, "Name"), TOKEN, atomBridge);
		NCNAME = new NCNameType<A>(name(W3C_XML_SCHEMA_NS_URI, "NCName"), NAME, atomBridge);
		ID = new IDType<A>(name(W3C_XML_SCHEMA_NS_URI, "ID"), NCNAME, atomBridge);
		IDREF = new IDREFType<A>(name(W3C_XML_SCHEMA_NS_URI, "IDREF"), NCNAME, atomBridge);
		ENTITY = new ENTITYType<A>(name(W3C_XML_SCHEMA_NS_URI, "ENTITY"), NCNAME, atomBridge);
		DOUBLE = new DoubleType<A>(name(W3C_XML_SCHEMA_NS_URI, "double"), ANY_ATOMIC_TYPE, atomBridge);
		FLOAT = new FloatType<A>(name(W3C_XML_SCHEMA_NS_URI, "float"), ANY_ATOMIC_TYPE, atomBridge);
		BOOLEAN = new BooleanType<A>(name(W3C_XML_SCHEMA_NS_URI, "boolean"), ANY_ATOMIC_TYPE, atomBridge);
		DECIMAL = new DecimalType<A>(name(W3C_XML_SCHEMA_NS_URI, "decimal"), ANY_ATOMIC_TYPE, atomBridge);
		INTEGER = new IntegerType<A>(name(W3C_XML_SCHEMA_NS_URI, "integer"), DECIMAL, atomBridge);
		LONG = new LongType<A>(name(W3C_XML_SCHEMA_NS_URI, "long"), INTEGER, atomBridge);
		INT = new IntType<A>(name(W3C_XML_SCHEMA_NS_URI, "int"), LONG, atomBridge);
		SHORT = new ShortType<A>(name(W3C_XML_SCHEMA_NS_URI, "short"), INT, atomBridge);
		BYTE = new ByteType<A>(name(W3C_XML_SCHEMA_NS_URI, "byte"), SHORT, atomBridge);
		NON_POSITIVE_INTEGER = new IntegerDerivedType<A>(SmNativeType.NON_POSITIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "nonPositiveInteger"), INTEGER, atomBridge);
		NEGATIVE_INTEGER = new IntegerDerivedType<A>(SmNativeType.NEGATIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "negativeInteger"), NON_POSITIVE_INTEGER, atomBridge);
		NON_NEGATIVE_INTEGER = new IntegerDerivedType<A>(SmNativeType.NON_NEGATIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger"), INTEGER, atomBridge);
		POSITIVE_INTEGER = new IntegerDerivedType<A>(SmNativeType.POSITIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "positiveInteger"), NON_NEGATIVE_INTEGER, atomBridge);
		UNSIGNED_LONG = new IntegerDerivedType<A>(SmNativeType.UNSIGNED_LONG, name(W3C_XML_SCHEMA_NS_URI, "unsignedLong"), NON_NEGATIVE_INTEGER, atomBridge);
		UNSIGNED_INT = new IntegerDerivedType<A>(SmNativeType.UNSIGNED_INT, name(W3C_XML_SCHEMA_NS_URI, "unsignedInt"), UNSIGNED_LONG, atomBridge);
		UNSIGNED_SHORT = new IntegerDerivedType<A>(SmNativeType.UNSIGNED_SHORT, name(W3C_XML_SCHEMA_NS_URI, "unsignedShort"), UNSIGNED_INT, atomBridge);
		UNSIGNED_BYTE = new IntegerDerivedType<A>(SmNativeType.UNSIGNED_BYTE, name(W3C_XML_SCHEMA_NS_URI, "unsignedByte"), UNSIGNED_SHORT, atomBridge);
		DATE = new GregorianType<A>(SmNativeType.DATE, name(W3C_XML_SCHEMA_NS_URI, "date"), ANY_ATOMIC_TYPE, atomBridge);
		DATETIME = new GregorianType<A>(SmNativeType.DATETIME, name(W3C_XML_SCHEMA_NS_URI, "dateTime"), ANY_ATOMIC_TYPE, atomBridge);
		TIME = new GregorianType<A>(SmNativeType.TIME, name(W3C_XML_SCHEMA_NS_URI, "time"), ANY_ATOMIC_TYPE, atomBridge);
		GYEARMONTH = new GregorianType<A>(SmNativeType.GYEARMONTH, name(W3C_XML_SCHEMA_NS_URI, "gYearMonth"), ANY_ATOMIC_TYPE, atomBridge);
		GYEAR = new GregorianType<A>(SmNativeType.GYEAR, name(W3C_XML_SCHEMA_NS_URI, "gYear"), ANY_ATOMIC_TYPE, atomBridge);
		GMONTHDAY = new GregorianType<A>(SmNativeType.GMONTHDAY, name(W3C_XML_SCHEMA_NS_URI, "gMonthDay"), ANY_ATOMIC_TYPE, atomBridge);
		GDAY = new GregorianType<A>(SmNativeType.GDAY, name(W3C_XML_SCHEMA_NS_URI, "gDay"), ANY_ATOMIC_TYPE, atomBridge);
		GMONTH = new GregorianType<A>(SmNativeType.GMONTH, name(W3C_XML_SCHEMA_NS_URI, "gMonth"), ANY_ATOMIC_TYPE, atomBridge);
		HEX_BINARY = new HexBinaryType<A>(name(W3C_XML_SCHEMA_NS_URI, "hexBinary"), ANY_ATOMIC_TYPE, atomBridge);
		BASE64_BINARY = new Base64BinaryType<A>(name(W3C_XML_SCHEMA_NS_URI, "base64Binary"), ANY_ATOMIC_TYPE, atomBridge);
		QNAME = new QNameType<A>(name(W3C_XML_SCHEMA_NS_URI, "QName"), ANY_ATOMIC_TYPE, atomBridge);
		ANY_URI = new AnyURIType<A>(name(W3C_XML_SCHEMA_NS_URI, "anyURI"), ANY_ATOMIC_TYPE, atomBridge);
		NOTATION = new NotationType<A>(name(W3C_XML_SCHEMA_NS_URI, "NOTATION"), ANY_ATOMIC_TYPE, atomBridge);
		DURATION = new DurationType<A>(name(W3C_XML_SCHEMA_NS_URI, "duration"), ANY_ATOMIC_TYPE, atomBridge);
		DURATION_YEARMONTH = new YearMonthDurationType<A>(name(W3C_XML_SCHEMA_NS_URI, "yearMonthDuration"), DURATION, atomBridge);
		DURATION_DAYTIME = new DayTimeDurationType<A>(name(W3C_XML_SCHEMA_NS_URI, "dayTimeDuration"), DURATION, atomBridge);

		// register(ANY_COMPLEX_TYPE);
		register(UNTYPED);
		// register(ANY_SIMPLE_TYPE);
		// register(ANY_ATOMIC_TYPE);
		register(UNTYPED_ATOMIC);
		register(STRING);
		register(NORMALIZED_STRING);
		register(TOKEN);
		register(LANGUAGE);
		register(NMTOKEN);
		register(NAME);
		register(NCNAME);
		register(ID);
		register(IDREF);
		register(ENTITY);
		register(DOUBLE);
		register(FLOAT);
		register(BOOLEAN);
		register(DECIMAL);
		register(INTEGER);
		register(LONG);
		register(INT);
		register(SHORT);
		register(BYTE);
		register(NON_POSITIVE_INTEGER);
		register(NEGATIVE_INTEGER);
		register(NON_NEGATIVE_INTEGER);
		register(POSITIVE_INTEGER);
		register(UNSIGNED_LONG);
		register(UNSIGNED_INT);
		register(UNSIGNED_SHORT);
		register(UNSIGNED_BYTE);
		register(DATE);
		register(DATETIME);
		register(TIME);
		register(GYEARMONTH);
		register(GYEAR);
		register(GMONTHDAY);
		register(GDAY);
		register(GMONTH);
		register(HEX_BINARY);
		register(BASE64_BINARY);
		register(QNAME);
		register(ANY_URI);
		register(NOTATION);
		register(DURATION);
		register(DURATION_YEARMONTH);
		register(DURATION_DAYTIME);

		IDREFS = new ListTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "IDREFS"), false, SmScopeExtent.Global, IDREF, ANY_SIMPLE_TYPE, null, cache.getAtomBridge());
		NMTOKENS = new ListTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "NMTOKENS"), false, SmScopeExtent.Global, NMTOKEN, ANY_SIMPLE_TYPE, null, cache.getAtomBridge());
		ENTITIES = new ListTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "ENTITIES"), false, SmScopeExtent.Global, ENTITY, ANY_SIMPLE_TYPE, null, cache.getAtomBridge());

		register(IDREFS);
		register(NMTOKENS);
		register(ENTITIES);
	}

	public SmSimpleType<A> getAtomicType(final QName name)
	{
		final SmSimpleType<A> simpleType = getSimpleType(name);
		if (simpleType.isAtomicType())
		{
			return simpleType;
		}
		else
		{
			return null;
		}
	}

	public SmAttribute<A> getAttribute(final QName name)
	{
		return null;
	}

	public SmAttributeGroup<A> getAttributeGroup(final QName name)
	{
		return null;
	}

	public Iterable<SmAttributeGroup<A>> getAttributeGroups()
	{
		return Collections.emptyList();
	}

	public Iterable<SmAttribute<A>> getAttributes()
	{
		return Collections.emptyList();
	}

	public SmComplexType<A> getComplexType(final QName name)
	{
		return m_complexTypes.get(name);
	}

	public Iterable<SmComplexType<A>> getComplexTypes()
	{
		return m_complexTypes.values();
	}

	public SmElement<A> getElement(final QName name)
	{
		return null;
	}

	public Iterable<SmElement<A>> getElements()
	{
		return Collections.emptyList();
	}

	public SmIdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return null;
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return Collections.emptyList();
	}

	public SmModelGroup<A> getModelGroup(final QName name)
	{
		return null;
	}

	public Iterable<SmModelGroup<A>> getModelGroups()
	{
		return Collections.emptyList();
	}

	public SmNotation<A> getNotation(final QName name)
	{
		return null;
	}

	public Iterable<SmNotation<A>> getNotations()
	{
		return Collections.emptyList();
	}

	public SmSimpleType<A> getSimpleType(final QName name)
	{
		return m_simpleTypes.get(name);
	}

	public Iterable<SmSimpleType<A>> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public SmType<A> getType(final QName name)
	{
		if (hasSimpleType(name))
		{
			return getSimpleType(name);
		}
		else if (hasComplexType(name))
		{
			return getComplexType(name);
		}
		else
		{
			return null;
		}
	}

	public boolean hasAttribute(final QName name)
	{
		return false;
	}

	public boolean hasAttributeGroup(final QName name)
	{
		return false;
	}

	public boolean hasComplexType(final QName name)
	{
		return m_complexTypes.containsKey(name);
	}

	public boolean hasElement(final QName name)
	{
		return false;
	}

	public boolean hasIdentityConstraint(final QName name)
	{
		return false;
	}

	public boolean hasModelGroup(final QName name)
	{
		return false;
	}

	public boolean hasNotation(final QName name)
	{
		return false;
	}

	public boolean hasSimpleType(final QName name)
	{
		return m_simpleTypes.containsKey(name);
	}

	public boolean hasType(final QName name)
	{
		return hasSimpleType(name) || hasComplexType(name);
	}

	private void register(final SmComplexType<A> complexType)
	{
		m_complexTypes.put(complexType.getName(), complexType);
	}

	private void register(final SmSimpleType<A> simpleType)
	{
		m_simpleTypes.put(simpleType.getName(), simpleType);
	}

	private static QName name(final String namespaceURI, final String localName)
	{
		return new QName(namespaceURI, localName);
	}
}
