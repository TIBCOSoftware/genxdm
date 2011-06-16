/*
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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.complex.ComplexTypeImpl;
import org.genxdm.bridgekit.xs.complex.ComplexUrTypeImpl;
import org.genxdm.bridgekit.xs.complex.ListTypeImpl;
import org.genxdm.bridgekit.xs.simple.AnyURIType;
import org.genxdm.bridgekit.xs.simple.AtomicUrTypeImpl;
import org.genxdm.bridgekit.xs.simple.Base64BinaryType;
import org.genxdm.bridgekit.xs.simple.BooleanType;
import org.genxdm.bridgekit.xs.simple.ByteType;
import org.genxdm.bridgekit.xs.simple.DayTimeDurationType;
import org.genxdm.bridgekit.xs.simple.DecimalType;
import org.genxdm.bridgekit.xs.simple.DoubleType;
import org.genxdm.bridgekit.xs.simple.DurationType;
import org.genxdm.bridgekit.xs.simple.ENTITYType;
import org.genxdm.bridgekit.xs.simple.FloatType;
import org.genxdm.bridgekit.xs.simple.GregorianType;
import org.genxdm.bridgekit.xs.simple.HexBinaryType;
import org.genxdm.bridgekit.xs.simple.IDREFType;
import org.genxdm.bridgekit.xs.simple.IDType;
import org.genxdm.bridgekit.xs.simple.IntType;
import org.genxdm.bridgekit.xs.simple.IntegerDerivedType;
import org.genxdm.bridgekit.xs.simple.IntegerType;
import org.genxdm.bridgekit.xs.simple.LanguageType;
import org.genxdm.bridgekit.xs.simple.LongType;
import org.genxdm.bridgekit.xs.simple.NCNameType;
import org.genxdm.bridgekit.xs.simple.NMTOKENType;
import org.genxdm.bridgekit.xs.simple.NameType;
import org.genxdm.bridgekit.xs.simple.NormalizedStringType;
import org.genxdm.bridgekit.xs.simple.NotationType;
import org.genxdm.bridgekit.xs.simple.QNameType;
import org.genxdm.bridgekit.xs.simple.ShortType;
import org.genxdm.bridgekit.xs.simple.SimpleUrTypeImpl;
import org.genxdm.bridgekit.xs.simple.StringType;
import org.genxdm.bridgekit.xs.simple.TokenType;
import org.genxdm.bridgekit.xs.simple.UntypedAtomicType;
import org.genxdm.bridgekit.xs.simple.YearMonthDurationType;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
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

final class BuiltInSchema implements ComponentBag
{
    public static final BuiltInSchema SINGLETON = new BuiltInSchema();
    
    /**
     * Constructs the W3C XML Schema native types and attributes.
     */
    private BuiltInSchema()
    {

        ANY_COMPLEX_TYPE = new ComplexUrTypeImpl(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ANY_SIMPLE_TYPE = new SimpleUrTypeImpl(XMLConstants.W3C_XML_SCHEMA_NS_URI, ANY_COMPLEX_TYPE);
        ANY_ATOMIC_TYPE = new AtomicUrTypeImpl(XMLConstants.W3C_XML_SCHEMA_NS_URI, ANY_SIMPLE_TYPE);

        final Map<QName, AttributeUse> EMPTY_ATTRIBUTE_USES = Collections.emptyMap();
        UNTYPED_ATOMIC = new UntypedAtomicType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untypedAtomic"), ANY_ATOMIC_TYPE);
        UNTYPED = new ComplexTypeImpl(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untyped"), true, false, ScopeExtent.Global, ANY_COMPLEX_TYPE, DerivationMethod.Restriction, EMPTY_ATTRIBUTE_USES, ANY_COMPLEX_TYPE.getContentType(), EnumSet
                .noneOf(DerivationMethod.class), UNTYPED_ATOMIC);
        UNTYPED.setAttributeWildcard(ANY_COMPLEX_TYPE.getAttributeWildcard());

        STRING = new StringType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"), ANY_ATOMIC_TYPE);
        NORMALIZED_STRING = new NormalizedStringType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "normalizedString"), STRING);
        TOKEN = new TokenType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "token"), NORMALIZED_STRING);
        LANGUAGE = new LanguageType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "language"), TOKEN);
        NMTOKEN = new NMTOKENType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NMTOKEN"), TOKEN);
        NAME = new NameType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "Name"), TOKEN);
        NCNAME = new NCNameType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NCName"), NAME);
        ID = new IDType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ID"), NCNAME);
        IDREF = new IDREFType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "IDREF"), NCNAME);
        ENTITY = new ENTITYType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ENTITY"), NCNAME);
        DOUBLE = new DoubleType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "double"), ANY_ATOMIC_TYPE);
        FLOAT = new FloatType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "float"), ANY_ATOMIC_TYPE);
        BOOLEAN = new BooleanType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "boolean"), ANY_ATOMIC_TYPE);
        DECIMAL = new DecimalType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal"), ANY_ATOMIC_TYPE);
        INTEGER = new IntegerType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer"), DECIMAL);
        LONG = new LongType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "long"), INTEGER);
        INT = new IntType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int"), LONG);
        SHORT = new ShortType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "short"), INT);
        BYTE = new ByteType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "byte"), SHORT);
        NON_POSITIVE_INTEGER = new IntegerDerivedType(NativeType.NON_POSITIVE_INTEGER, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonPositiveInteger"), INTEGER);
        NEGATIVE_INTEGER = new IntegerDerivedType(NativeType.NEGATIVE_INTEGER, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "negativeInteger"), NON_POSITIVE_INTEGER);
        NON_NEGATIVE_INTEGER = new IntegerDerivedType(NativeType.NON_NEGATIVE_INTEGER, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "nonNegativeInteger"), INTEGER);
        POSITIVE_INTEGER = new IntegerDerivedType(NativeType.POSITIVE_INTEGER, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "positiveInteger"), NON_NEGATIVE_INTEGER);
        UNSIGNED_LONG = new IntegerDerivedType(NativeType.UNSIGNED_LONG, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedLong"), NON_NEGATIVE_INTEGER);
        UNSIGNED_INT = new IntegerDerivedType(NativeType.UNSIGNED_INT, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedInt"), UNSIGNED_LONG);
        UNSIGNED_SHORT = new IntegerDerivedType(NativeType.UNSIGNED_SHORT, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedShort"), UNSIGNED_INT);
        UNSIGNED_BYTE = new IntegerDerivedType(NativeType.UNSIGNED_BYTE, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "unsignedByte"), UNSIGNED_SHORT);
        DATE = new GregorianType(NativeType.DATE, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "date"), ANY_ATOMIC_TYPE);
        DATETIME = new GregorianType(NativeType.DATETIME, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dateTime"), ANY_ATOMIC_TYPE);
        TIME = new GregorianType(NativeType.TIME, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "time"), ANY_ATOMIC_TYPE);
        GYEARMONTH = new GregorianType(NativeType.GYEARMONTH, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYearMonth"), ANY_ATOMIC_TYPE);
        GYEAR = new GregorianType(NativeType.GYEAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYear"), ANY_ATOMIC_TYPE);
        GMONTHDAY = new GregorianType(NativeType.GMONTHDAY, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonthDay"), ANY_ATOMIC_TYPE);
        GDAY = new GregorianType(NativeType.GDAY, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gDay"), ANY_ATOMIC_TYPE);
        GMONTH = new GregorianType(NativeType.GMONTH, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonth"), ANY_ATOMIC_TYPE);
        HEX_BINARY = new HexBinaryType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "hexBinary"), ANY_ATOMIC_TYPE);
        BASE64_BINARY = new Base64BinaryType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "base64Binary"), ANY_ATOMIC_TYPE);
        QNAME = new QNameType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "QName"), ANY_ATOMIC_TYPE);
        ANY_URI = new AnyURIType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyURI"), ANY_ATOMIC_TYPE);
        NOTATION = new NotationType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NOTATION"), ANY_ATOMIC_TYPE);
        DURATION = new DurationType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "duration"), ANY_ATOMIC_TYPE);
        DURATION_YEARMONTH = new YearMonthDurationType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "yearMonthDuration"), DURATION);
        DURATION_DAYTIME = new DayTimeDurationType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dayTimeDuration"), DURATION);

        register(ANY_COMPLEX_TYPE);
        register(UNTYPED);
        register(ANY_SIMPLE_TYPE);
        register(ANY_ATOMIC_TYPE);
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

        IDREFS = new ListTypeImpl(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "IDREFS"), false, ScopeExtent.Global, IDREF, ANY_SIMPLE_TYPE, null);
        NMTOKENS = new ListTypeImpl(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "NMTOKENS"), false, ScopeExtent.Global, NMTOKEN, ANY_SIMPLE_TYPE, null);
        ENTITIES = new ListTypeImpl(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ENTITIES"), false, ScopeExtent.Global, ENTITY, ANY_SIMPLE_TYPE, null);

        register(IDREFS);
        register(NMTOKENS);
        register(ENTITIES);
    }

    public Iterable<AttributeGroupDefinition> getAttributeGroups()
    {
        return Collections.emptyList();
    }

    public Iterable<AttributeDefinition> getAttributes()
    {
        return Collections.emptyList();
    }

    public Iterable<ComplexType> getComplexTypes()
    {
        return m_complexTypes.values();
    }

    public Iterable<ElementDefinition> getElements()
    {
        return Collections.emptyList();
    }

    public Iterable<IdentityConstraint> getIdentityConstraints()
    {
        return Collections.emptyList();
    }

    public Iterable<ModelGroup> getModelGroups()
    {
        return Collections.emptyList();
    }

    public Iterable<NotationDefinition> getNotations()
    {
        return Collections.emptyList();
    }

    public Iterable<SimpleType> getSimpleTypes()
    {
        return m_simpleTypes.values();
    }

    private void register(final ComplexType complexType)
    {
        m_complexTypes.put(complexType.getName(), complexType);
    }

    private void register(final SimpleType simpleType)
    {
        m_simpleTypes.put(simpleType.getName(), simpleType);
    }

    final AtomicUrType ANY_ATOMIC_TYPE;
    final SimpleUrType ANY_SIMPLE_TYPE;
    final ComplexUrType ANY_COMPLEX_TYPE;
    final SimpleType ANY_URI;
    final SimpleType BASE64_BINARY;
    final AtomicType BOOLEAN;
    final SimpleType BYTE;
    final SimpleType DATE;
    final SimpleType DATETIME;
    final SimpleType DECIMAL;
    final SimpleType DOUBLE;
    final SimpleType DURATION;
    final SimpleType DURATION_DAYTIME;
    final SimpleType DURATION_YEARMONTH;
    final SimpleType ENTITIES;
    final SimpleType ENTITY;
    final SimpleType FLOAT;
    final SimpleType GDAY;
    final SimpleType GMONTH;
    final SimpleType GMONTHDAY;
    final SimpleType GYEAR;
    final SimpleType GYEARMONTH;
    final SimpleType HEX_BINARY;
    final SimpleType ID;
    final SimpleType IDREF;
    final SimpleType IDREFS;
    final SimpleType INT;
    final SimpleType INTEGER;
    final SimpleType LANGUAGE;
    final SimpleType LONG;
    private final HashMap<QName, ComplexType> m_complexTypes = new HashMap<QName, ComplexType>();
    private final HashMap<QName, SimpleType> m_simpleTypes = new HashMap<QName, SimpleType>();
    final SimpleType NAME;
    final SimpleType NCNAME;
    final SimpleType NEGATIVE_INTEGER;
    final SimpleType NMTOKEN;
    final SimpleType NMTOKENS;
    final SimpleType NON_NEGATIVE_INTEGER;
    final SimpleType NON_POSITIVE_INTEGER;
    final SimpleType NORMALIZED_STRING;
    final SimpleType NOTATION;
    final SimpleType POSITIVE_INTEGER;
    final SimpleType QNAME;
    final SimpleType SHORT;
    final SimpleType STRING;
    final SimpleType TIME;
    final SimpleType TOKEN;
    final SimpleType UNSIGNED_BYTE;
    final SimpleType UNSIGNED_INT;

    final SimpleType UNSIGNED_LONG;
    final SimpleType UNSIGNED_SHORT;
    final ComplexTypeImpl UNTYPED;

    final AtomicType UNTYPED_ATOMIC;
}
