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
package org.genxdm.bridgekit.xs;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

final class BuiltInSchema<A> implements ComponentBag<A>
{
	private final AtomicUrType<A> ANY_ATOMIC_TYPE;
	private final SimpleUrType<A> ANY_SIMPLE_TYPE;
	private final ComplexUrType<A> ANY_COMPLEX_TYPE;
	final SimpleType<A> ANY_URI;
	final SimpleType<A> BASE64_BINARY;
	final AtomicType<A> BOOLEAN;
	final SimpleType<A> BYTE;
	final SimpleType<A> DATE;
	final SimpleType<A> DATETIME;
	final SimpleType<A> DECIMAL;
	final SimpleType<A> DOUBLE;
	final SimpleType<A> DURATION;
	final SimpleType<A> DURATION_DAYTIME;
	final SimpleType<A> DURATION_YEARMONTH;
	final SimpleType<A> ENTITIES;
	final SimpleType<A> ENTITY;
	final SimpleType<A> FLOAT;
	final SimpleType<A> GDAY;
	final SimpleType<A> GMONTH;
	final SimpleType<A> GMONTHDAY;
	final SimpleType<A> GYEAR;
	final SimpleType<A> GYEARMONTH;
	final SimpleType<A> HEX_BINARY;
	final SimpleType<A> ID;
	final SimpleType<A> IDREF;
	final SimpleType<A> IDREFS;
	final SimpleType<A> INT;
	final SimpleType<A> INTEGER;
	final SimpleType<A> LANGUAGE;
	final SimpleType<A> LONG;
	private final HashMap<QName, ComplexType<A>> m_complexTypes = new HashMap<QName, ComplexType<A>>();
	private final HashMap<QName, SimpleType<A>> m_simpleTypes = new HashMap<QName, SimpleType<A>>();
	final SimpleType<A> NAME;
	final SimpleType<A> NCNAME;
	final SimpleType<A> NEGATIVE_INTEGER;
	final SimpleType<A> NMTOKEN;
	final SimpleType<A> NMTOKENS;
	final SimpleType<A> NON_NEGATIVE_INTEGER;
	final SimpleType<A> NON_POSITIVE_INTEGER;
	final SimpleType<A> NORMALIZED_STRING;
	final SimpleType<A> NOTATION;
	final SimpleType<A> POSITIVE_INTEGER;
	final SimpleType<A> QNAME;
	final SimpleType<A> SHORT;
	final SimpleType<A> STRING;
	final SimpleType<A> TIME;
	final SimpleType<A> TOKEN;
	final SimpleType<A> UNSIGNED_BYTE;
	final SimpleType<A> UNSIGNED_INT;

	final SimpleType<A> UNSIGNED_LONG;
	final SimpleType<A> UNSIGNED_SHORT;
	final ComplexTypeImpl<A> UNTYPED;

	final SimpleType<A> UNTYPED_ATOMIC;

	/**
	 * Constructs the W3C XML Schema native types and atributes.
	 */
	public BuiltInSchema(final String W3C_XML_SCHEMA_NS_URI, final SchemaCacheImpl<A> cache)
	{
		final AtomBridge<A> atomBridge = cache.getAtomBridge();
		final NameSource nameBridge = atomBridge.getNameBridge();

		ANY_COMPLEX_TYPE = cache.getComplexUrType();
		ANY_SIMPLE_TYPE = cache.getSimpleUrType();
		ANY_ATOMIC_TYPE = cache.getAtomicUrType();

		final Map<QName, AttributeUse<A>> EMPTY_ATTRIBUTE_USES = Collections.emptyMap();
		UNTYPED = new ComplexTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "untyped"), true, false, ScopeExtent.Global, ANY_COMPLEX_TYPE, DerivationMethod.Restriction, EMPTY_ATTRIBUTE_USES, ANY_COMPLEX_TYPE.getContentType(), EnumSet
				.noneOf(DerivationMethod.class), nameBridge, cache);
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
		NON_POSITIVE_INTEGER = new IntegerDerivedType<A>(NativeType.NON_POSITIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "nonPositiveInteger"), INTEGER, atomBridge);
		NEGATIVE_INTEGER = new IntegerDerivedType<A>(NativeType.NEGATIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "negativeInteger"), NON_POSITIVE_INTEGER, atomBridge);
		NON_NEGATIVE_INTEGER = new IntegerDerivedType<A>(NativeType.NON_NEGATIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger"), INTEGER, atomBridge);
		POSITIVE_INTEGER = new IntegerDerivedType<A>(NativeType.POSITIVE_INTEGER, name(W3C_XML_SCHEMA_NS_URI, "positiveInteger"), NON_NEGATIVE_INTEGER, atomBridge);
		UNSIGNED_LONG = new IntegerDerivedType<A>(NativeType.UNSIGNED_LONG, name(W3C_XML_SCHEMA_NS_URI, "unsignedLong"), NON_NEGATIVE_INTEGER, atomBridge);
		UNSIGNED_INT = new IntegerDerivedType<A>(NativeType.UNSIGNED_INT, name(W3C_XML_SCHEMA_NS_URI, "unsignedInt"), UNSIGNED_LONG, atomBridge);
		UNSIGNED_SHORT = new IntegerDerivedType<A>(NativeType.UNSIGNED_SHORT, name(W3C_XML_SCHEMA_NS_URI, "unsignedShort"), UNSIGNED_INT, atomBridge);
		UNSIGNED_BYTE = new IntegerDerivedType<A>(NativeType.UNSIGNED_BYTE, name(W3C_XML_SCHEMA_NS_URI, "unsignedByte"), UNSIGNED_SHORT, atomBridge);
		DATE = new GregorianType<A>(NativeType.DATE, name(W3C_XML_SCHEMA_NS_URI, "date"), ANY_ATOMIC_TYPE, atomBridge);
		DATETIME = new GregorianType<A>(NativeType.DATETIME, name(W3C_XML_SCHEMA_NS_URI, "dateTime"), ANY_ATOMIC_TYPE, atomBridge);
		TIME = new GregorianType<A>(NativeType.TIME, name(W3C_XML_SCHEMA_NS_URI, "time"), ANY_ATOMIC_TYPE, atomBridge);
		GYEARMONTH = new GregorianType<A>(NativeType.GYEARMONTH, name(W3C_XML_SCHEMA_NS_URI, "gYearMonth"), ANY_ATOMIC_TYPE, atomBridge);
		GYEAR = new GregorianType<A>(NativeType.GYEAR, name(W3C_XML_SCHEMA_NS_URI, "gYear"), ANY_ATOMIC_TYPE, atomBridge);
		GMONTHDAY = new GregorianType<A>(NativeType.GMONTHDAY, name(W3C_XML_SCHEMA_NS_URI, "gMonthDay"), ANY_ATOMIC_TYPE, atomBridge);
		GDAY = new GregorianType<A>(NativeType.GDAY, name(W3C_XML_SCHEMA_NS_URI, "gDay"), ANY_ATOMIC_TYPE, atomBridge);
		GMONTH = new GregorianType<A>(NativeType.GMONTH, name(W3C_XML_SCHEMA_NS_URI, "gMonth"), ANY_ATOMIC_TYPE, atomBridge);
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

		IDREFS = new ListTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "IDREFS"), false, ScopeExtent.Global, IDREF, ANY_SIMPLE_TYPE, null, cache.getAtomBridge());
		NMTOKENS = new ListTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "NMTOKENS"), false, ScopeExtent.Global, NMTOKEN, ANY_SIMPLE_TYPE, null, cache.getAtomBridge());
		ENTITIES = new ListTypeImpl<A>(name(W3C_XML_SCHEMA_NS_URI, "ENTITIES"), false, ScopeExtent.Global, ENTITY, ANY_SIMPLE_TYPE, null, cache.getAtomBridge());

		register(IDREFS);
		register(NMTOKENS);
		register(ENTITIES);
	}

	public SimpleType<A> getAtomicType(final QName name)
	{
		final SimpleType<A> simpleType = getSimpleType(name);
		if (simpleType.isAtomicType())
		{
			return simpleType;
		}
		else
		{
			return null;
		}
	}

	public AttributeDefinition<A> getAttribute(final QName name)
	{
		return null;
	}

	public AttributeGroupDefinition<A> getAttributeGroup(final QName name)
	{
		return null;
	}

	public Iterable<AttributeGroupDefinition<A>> getAttributeGroups()
	{
		return Collections.emptyList();
	}

	public Iterable<AttributeDefinition<A>> getAttributes()
	{
		return Collections.emptyList();
	}

	public ComplexType<A> getComplexType(final QName name)
	{
		return m_complexTypes.get(name);
	}

	public Iterable<ComplexType<A>> getComplexTypes()
	{
		return m_complexTypes.values();
	}

	public ElementDefinition<A> getElement(final QName name)
	{
		return null;
	}

	public Iterable<ElementDefinition<A>> getElements()
	{
		return Collections.emptyList();
	}

	public IdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return null;
	}

	public Iterable<IdentityConstraint<A>> getIdentityConstraints()
	{
		return Collections.emptyList();
	}

	public ModelGroup<A> getModelGroup(final QName name)
	{
		return null;
	}

	public Iterable<ModelGroup<A>> getModelGroups()
	{
		return Collections.emptyList();
	}

	public NotationDefinition<A> getNotation(final QName name)
	{
		return null;
	}

	public Iterable<NotationDefinition<A>> getNotations()
	{
		return Collections.emptyList();
	}

	public SimpleType<A> getSimpleType(final QName name)
	{
		return m_simpleTypes.get(name);
	}

	public Iterable<SimpleType<A>> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public Type<A> getType(final QName name)
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

	private void register(final ComplexType<A> complexType)
	{
		m_complexTypes.put(complexType.getName(), complexType);
	}

	private void register(final SimpleType<A> simpleType)
	{
		m_simpleTypes.put(simpleType.getName(), simpleType);
	}

	private static QName name(final String namespaceURI, final String localName)
	{
		return new QName(namespaceURI, localName);
	}
}
