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
package org.genxdm.xs.types;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

/**
 * Symbolic constants representing the built-in types in XML Schema Part 2:Datatypes. <br/>
 * The enumeration captures both the XPath 2.0 and XQuery 1.0 Type Hierarchy as well as the special promotions that occur for XPath (xs:decimal -> xs:float, xs:float -> xs:double, xs:anyURI -> xs:string). This effectively results in two hierarchies.
 */
public enum NativeType
{
    /**
     * xs:anyType
     */
    ANY_TYPE("anyType")
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return true;
        }
    },

    /**
     * xs:anySimpleType
     */
    ANY_SIMPLE_TYPE("anySimpleType", ANY_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return true;
        }
    },

    /**
     * xs:anyAtomicType
     */
    ANY_ATOMIC_TYPE("anyAtomicType", ANY_SIMPLE_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return true;
        }
    },

    /**
     * xs:untyped
     */
    UNTYPED("untyped", ANY_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:untypedAtomic
     */
    UNTYPED_ATOMIC("untypedAtomic", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:string
     */
    STRING("string", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:boolean
     */
    BOOLEAN("boolean", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:date
     */
    DATE("date", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:dateTime
     */
    DATETIME("dateTime", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:time
     */
    TIME("time", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:gYearMonth
     */
    GYEARMONTH("gYearMonth", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:gYear
     */
    GYEAR("gYear", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:gMonthDay
     */
    GMONTHDAY("gMonthDay", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:gDay
     */
    GDAY("gDay", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:gMonth
     */
    GMONTH("gMonth", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return true;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:duration
     */
    DURATION("duration", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:base64Binary
     */
    BASE64_BINARY("base64Binary", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:hexBinary
     */
    HEX_BINARY("hexBinary", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:anyURI
     */
    ANY_URI("anyURI", ANY_ATOMIC_TYPE, STRING)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:QName
     */
    QNAME("QName", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:NOTATION
     */
    NOTATION("NOTATION", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:double
     */
    DOUBLE("double", ANY_ATOMIC_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:float
     */
    FLOAT("float", ANY_ATOMIC_TYPE, DOUBLE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:decimal
     */
    DECIMAL("decimal", ANY_ATOMIC_TYPE, FLOAT)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:integer
     */
    INTEGER("integer", DECIMAL)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:nonPositiveInteger
     */
    NON_POSITIVE_INTEGER("nonPositiveInteger", INTEGER)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:negativeInteger
     */
    NEGATIVE_INTEGER("negativeInteger", NON_POSITIVE_INTEGER)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:long
     */
    LONG("long", INTEGER)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:int
     */
    INT("int", LONG)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:short
     */
    SHORT("short", INT)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:byte
     */
    BYTE("byte", SHORT)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:nonNegativeInteger
     */
    NON_NEGATIVE_INTEGER("nonNegativeInteger", INTEGER)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:unsignedLong
     */
    UNSIGNED_LONG("unsignedLong", NON_NEGATIVE_INTEGER)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:unsignedInt
     */
    UNSIGNED_INT("unsignedInt", UNSIGNED_LONG)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:unsignedShort
     */
    UNSIGNED_SHORT("unsignedShort", UNSIGNED_INT)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:unsignedByte
     */
    UNSIGNED_BYTE("unsignedByte", UNSIGNED_SHORT)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:positiveInteger
     */
    POSITIVE_INTEGER("positiveInteger", NON_NEGATIVE_INTEGER)
    {
        public boolean isDecimal()
        {
            return true;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return true;
        }

        public boolean isNumeric()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:normalizedString
     */
    NORMALIZED_STRING("normalizedString", STRING)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:token
     */
    TOKEN("token", NORMALIZED_STRING)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:language
     */
    LANGUAGE("language", TOKEN)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:NMTOKEN
     */
    NMTOKEN("NMTOKEN", TOKEN)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:Name
     */
    NAME("Name", TOKEN)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:NCName
     */
    NCNAME("NCName", NAME)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:ID
     */
    ID("ID", NCNAME)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:IDREF
     */
    IDREF("IDREF", NCNAME)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:ENTITY
     */
    ENTITY("ENTITY", NCNAME)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return true;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:yearMonthDuration
     */
    DURATION_YEARMONTH("yearMonthDuration", DURATION)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:dayTimeDuration
     */
    DURATION_DAYTIME("dayTimeDuration", DURATION)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:IDREFS
     */
    IDREFS("IDREFS", ANY_SIMPLE_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:NMTOKENS
     */
    NMTOKENS("NMTOKENS", ANY_SIMPLE_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    },

    /**
     * xs:ENTITIES
     */
    ENTITIES("ENTITIES", ANY_SIMPLE_TYPE)
    {
        public boolean isDecimal()
        {
            return false;
        }

        public boolean isGregorian()
        {
            return false;
        }

        public boolean isInteger()
        {
            return false;
        }

        public boolean isNumeric()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public boolean isString()
        {
            return false;
        }

        public boolean isUrType()
        {
            return false;
        }
    };

    /**
     * Returns the common ancestor type of two types. The computation may be conditioned to include the promotions that occur in XPath.
     * 
     * @param lhs
     *            The LHS type.
     * @param rhs
     *            The RHS type.
     * @param promotions
     *            Determines whether promotions are included in computation.
     * @return The common ancestor type or <code>null</code> if one does not exist.
     */
    public static NativeType computeCommonAncestorSelf(NativeType lhs, NativeType rhs, final boolean promotions)
    {
        PreCondition.assertArgumentNotNull(lhs, "lhs");
        PreCondition.assertArgumentNotNull(rhs, "rhs");
        if (lhs == rhs)
        {
            return lhs;
        }
        else
        {
            final int hierarchy = promotions ? 1 : 0;

            while (lhs.m_depth[hierarchy] > rhs.m_depth[hierarchy])
            {
                lhs = lhs.m_parent[hierarchy];
                if (null == lhs)
                {
                    return null;
                }
            }

            while (rhs.m_depth[hierarchy] > lhs.m_depth[hierarchy])
            {
                rhs = rhs.m_parent[hierarchy];
                if (null == rhs)
                {
                    return null;
                }
            }

            while (true)
            {
                if (lhs == rhs)
                {
                    return lhs;
                }
                else
                {
                    lhs = lhs.m_parent[hierarchy];
                    if (null == lhs)
                    {
                        return null;
                    }
                    rhs = rhs.m_parent[hierarchy];
                    if (null == rhs)
                    {
                        return null;
                    }
                }
            }
        }
    }

    public static NativeType getType(final String localName)
    {
        if (localName != null)
        {
            for (final NativeType type : NativeType.values())
            {
                if (localName.equals(type.getLocalName()))
                    return type;
            }
        }
        return null;
    }

    private final NativeType[] m_parent = new NativeType[2];
    private final int[] m_depth = new int[2];

    private final QName name;

    private NativeType(final String localName)
    {
        this(localName, null, null);
    }

    /**
     * The parent and promotion hierarchies are the same.
     */
    private NativeType(final String name, final NativeType parent)
    {
        this(name, parent, parent);
    }

    private NativeType(final String localName, final NativeType parent, final NativeType promotion)
    {
        // The prefix should be inconsequential so let's not bother to set it.
        this.name = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, localName, "");
        this.m_parent[0] = parent;
        if (null != parent)
        {
            m_depth[0] = parent.m_depth[0] + 1;
        }
        else
        {
            m_depth[0] = 0;
        }

        this.m_parent[1] = promotion;
        if (null != promotion)
        {
            m_depth[1] = promotion.m_depth[1] + 1;
        }
        else
        {
            if (null != parent)
            {
                m_depth[1] = parent.m_depth[1] + 1;
            }
            else
            {
                m_depth[1] = 0;
            }
        }
    }

    /**
     * Returns an ordered collection of types, starting from the receiver type, and traversing up the hierarchy. The collection may be extended to include the promotions that occur in XPath: xs:decimal -> xs:float, xs:float -> xs:double, and xs:anyURI -> xs:string
     * 
     * @param promotions
     *            Determines whether promotions are included in the returned collection.
     */
    public Iterable<NativeType> getAncestorOrSelf(final boolean promotions)
    {
        return new UberTypeArgumentOrSelfIterable(this, promotions);
    }

    public String getLocalName()
    {
        return name.getLocalPart();
    }

    public String getNamespaceURI()
    {
        return name.getNamespaceURI();
    }

    public NativeType getParent()
    {
        return m_parent[0];
    }

    public NativeType getPromotion()
    {
        return m_parent[1];
    }

    /**
     * Determines whether this type is equal to or derived from the specified type.
     * 
     * @param other
     *            The specified type.
     */
    public boolean isA(final NativeType other)
    {
        if (this == other)
        {
            return true;
        }
        else
        {
            if (null != m_parent[0])
            {
                return m_parent[0].isA(other);
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * <code>true</code> if this type is ultimately derived from <code>decimal</code>, otherwise <code>false</code>.
     */
    public abstract boolean isDecimal();

    /**
     * <code>true</code> if this type is ultimately derived from one of the Gregorian types, otherwise <code>false</code>.
     */
    public abstract boolean isGregorian();

    /**
     * <code>true</code> if this type is ultimately derived from <code>int</code>, otherwise <code>false</code>.
     */
    public boolean isInt()
    {
        return isA(INT);
    }

    /**
     * <code>true</code> if this type is ultimately derived from <code>integer</code>, otherwise <code>false</code>.
     */
    public abstract boolean isInteger();

    /**
     * <code>true</code> if this type is ultimately derived from xs:double, xs:float or xs:decimal, otherwise <code>false</code>.
     */
    public abstract boolean isNumeric();

    /**
     * <code>true</code> if this type is one of the built-in primitive types, otherwise <code>false</code>.
     * <p>
     * The primitive types are:
     * <ul>
     * <li>string</li>
     * <li>untypedAtomic</li>
     * <li>duration</li>
     * <li>dateTime</li>
     * <li>date</li>
     * <li>time</li>
     * <li>gYearMonth</li>
     * <li>gYear</li>
     * <li>gMonth</li>
     * <li>gDay</li>
     * <li>gMonth</li>
     * <li>boolean</li>
     * <li>base64Binary</li>
     * <li>hexBinary</li>
     * <li>float</li>
     * <li>double</li>
     * <li>decimal</li>
     * <li>anyURI</li>
     * <li>QName</li>
     * <li>NOTATION</li>
     * <ul>
     * </p>
     */
    public abstract boolean isPrimitive();

    /**
     * <code>true</code> if this type is ultimately derived from xs:string, otherwise <code>false</code>.
     */
    public abstract boolean isString();

    /**
     * <code>true</code> if this type is ultimately derived from <code>token</code>, otherwise <code>false</code>.
     */
    public boolean isToken()
    {
        return isA(TOKEN);
    }

    /**
     * Returns <code>true</code> if the type is one of xs:anyType, xs:anySimpleType or xs:anyAtomicType.
     */
    public abstract boolean isUrType();

    /**
     * Determines whether this type is equal to or derived from the specified type when promotions are included.
     * 
     * @param other
     *            The specified type.
     */
    public boolean promotesTo(final NativeType other)
    {
        if (this == other)
        {
            return true;
        }
        else
        {
            if (null != m_parent[1])
            {
                return m_parent[1].promotesTo(other);
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Returns the name of the type.
     * 
     * @return The name of the type as a {@link QName}.
     */
    public QName toQName()
    {
        return name;
    }
}
