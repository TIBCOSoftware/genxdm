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
package org.genxdm.typed.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.AtomCastException;
import org.genxdm.names.PrefixResolver;
import org.genxdm.xs.types.NativeType;

/**
 * A part of the bridge that provides access to the system of atomic values.
 * This interface consists of several parts.
 * <ul>
 * <li>Functions that are independent of any particular type.</li>
 * <li>An extensible lookup capability.</li>
 * <li>Access to built in types and conversions.</li>
 * </ul>
 */
public interface AtomBridge<A>
{
    public static final int TIMEZONE_UNDEFINED = Integer.MIN_VALUE;

    /** Safely cast an object to an atom.
     * 
     * If the argument returns true to isAtom(), returns the object cast
     * to the appropriate type.
     * 
     * @param object the object to be tested.
     * @return the supplied object, cast to the AtomBridge's A parameter,
     * if the supplied object is not null and is of the appropriate type;
     * otherwise null.
     */
    A atom(Object object);

    /**
     * Allocates an empty array of atoms.
     * 
     * @param size
     *            The size of the array of atoms. Must be non-negative.
     * @return a container of the specified size for atoms.
     */
    A[] atomArray(int size);

    /**
     * Performs a cast of the atom one level up the type hierarchy.
     * 
     * For primitive atoms, the argument atom is returned.
     * 
     * 
     * @param sourceAtom
     *            The atom to be cast
     * @return the 'parent' atom of the supplied atom; null if null is supplied.
     */
    A upCast(A sourceAtom);

    /**
     * Performs the casting of one atomic value to another.
     * 
     * @param sourceAtom
     *            The source atomic value for the cast. Must not be null.
     * @param targetType
     *            The target type. Must not be null.
     * @param castingContext
     *            The context that provides defaults for casting operations. Must not be null.
     * @return The cast atom; never null.
     * @throws AtomCastException
     *             if a cast does not exist between the two types or fails because of incompatible data.
     */
    A castAs(A sourceAtom, QName targetType, CastingContext castingContext) throws AtomCastException;

    /**
     * Performs the casting of one atomic value to another.
     * 
     * @param sourceAtom
     *            The source atomic value for the cast. Must not be null.
     * @param targetType
     *            The target type. Must not be null.
     * @param castingContext
     *            The context that provides defaults for casting operations.
     *            Must not be null.
     * @return The cast atom; never null.
     * @throws AtomCastException
     *             if a cast does not exist between the two types or fails because of incompatible data.
     */
    A castAs(A sourceAtom, NativeType targetType, CastingContext castingContext) throws AtomCastException;

    /**
     * Compile the atomic value from the lexical representation. 
     * This method is typically used for validation.
     * 
     * @param srcval
     *            The lexical representation of the atomic value;
     *            must not be null, but may be the empty string.
     * @param dataType
     *            The target native data type; must not be null.
     * @throws AtomCastException
     *             If the lexical representation is not valid for the atomic value type.
     */
    A compile(String srcval, NativeType dataType) throws AtomCastException;

    /**
     * Compile the atomic value from the lexical representation. 
     * This method is typically used for validation.
     * 
     * @param srcval
     *            The lexical representation of the atomic value;
     *            must not be null, but may be the empty string.
     * @param dataType
     *            The target native data type; must not be null.
     * @param resolver the resolver used when turning lexical representations
     * of QNames into their atomic values; if null, then QName resolution will fail
     * with an exception (equivalent to the overload of this method with no
     * resolver).
     * @throws AtomCastException
     *             If the lexical representation is not valid for the atomic value type.
     */
    A compile(String srcval, NativeType dataType, PrefixResolver resolver) throws AtomCastException;

    /** Create an xs:base64Binary based on the supplied value.
     * 
     * @param base64BinaryValue an array of bytes to be represented by the
     * return value; must not be null but may be zero-length.
     * @return an atom representing the supplied bytes as a base64Binary value.
     */
    A createBase64Binary(byte[] base64BinaryValue);

    /**
     * Returns an xs:boolean based upon the value.
     * 
     * @param booleanValue the value to be represented.
     * @return an atom representing the supplied boolean as a boolean value.
     */
    A createBoolean(boolean booleanValue);

    /**
     * Returns an xs:byte based upon the value.
     * 
     * @param byteValue the byte to be represented
     * @return an atom representing the supplied byte as a byte value.
     */
    A createByte(byte byteValue);

    /** Returns an xs:date atomic value.
     *
     * Arguments are required primitive values; it is not required that the
     * implementation check the range values, but the caller is encouraged to
     * abide by those restrictions. The month and dayOfMonth arguments are
     * 1-indexed.
     *
     * @param year an integer, using positive numbers for Gregorian era CE, negative for BCE
     * @param month an integer, which should be in the range 1-12
     * @param dayOfMonth an integer, which should be in a range corresponding with the
                         range suitable for the month argument, lower bound always 1,
                         upper bound 28, 29, 30, or 31.
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     * @return  An atomic value corresponding to the parameters passed.
     */
    A createDate(int year, int month, int dayOfMonth, int timezone);

    /** Returns an xs:dateTime atomic value.
     *
     * Arguments are required primitive values; it is not required that the
     * implementation check the range values, but the caller is encouraged to
     * abide by those restrictions. The month and dayOfMonth arguments are
     * 1-indexed. hourOfDay, minute, and second are 0-indexed.
     * millis may be ignored if remainderSecond is non-null; either millis
     * or remainderSecond will be used, but not both, and remainderSecond will be
     * preferred. No errors need be thrown for creation of values in which one or more
     * components are out of range.
     * 
     * @param year an integer, using positive numbers for Gregorian era CE, negative for BCE
     * @param month an integer, which should be in the range 1-12
     * @param dayOfMonth an integer, which should be in a range corresponding with the
                         range suitable for the month argument, lower bound always 1,
                         upper bound 28, 29, 30, or 31.
     * @param hourOfDay an integer, which should be in the range 0-23
     * @param minute an integer, which should be in the range 0-59
     * @param second an integer, which should be in the range 0-59
     * @param millis an integer, which should be in the range 0-999; if fractionalSecond is non-null, it will be ignored
     * @param fractionalSecond a BigDecimal representing the subsecond value of the time, to any supported scale; if null, use the millis value
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second, int millis, BigDecimal remainderSecond, int offsetInMinutes);

    /**
     * Returns an xs:gDay based upon the value.
     * @param dayOfMonth an integer, which should be in the range 0-31
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createDay(int dayOfMonth, int timezone);

    /**
     * Returns an xs:dayTimeDuration based upon the seconds value.
     * 
     * @param seconds
     *            The total number of seconds in the duration.
     */
    A createDayTimeDuration(BigDecimal seconds);

    /**
     * Returns an xs:decimal based upon the value.
     *
     * @param decimalValue a BigDecimal representing the fixed-point numeric value
     */
    A createDecimal(BigDecimal decimalValue);

    /**
     * Returns an xs:decimal based upon the value.
     * 
     * @param decimalValue a long representing the desired value.
     * @return an atom representing the long as a decimal value.
     */
    A createDecimal(long decimalValue);

    /**
     * Returns an xs:double based upon the value.
     * 
     * @param value a double representing the desired value.
     * @return an atom represneting the double as a double value.
     */
    A createDouble(double value);

    /**
     * Returns an xs:duration based upon the total months and seconds.
     * 
     * @param yearMonthDuration an integer representing the total months.
     * @param dayTimeDuration an integer representing the total seconds.
     */
    A createDuration(int yearMonthDuration, BigDecimal dayTimeDuration);

    /**
     * Returns an xs:float based upon the value.
     */
    A createFloat(float floatValue);

    /**
     * Returns an xs:hexBinary based upon the value.
     */
    A createHexBinary(byte[] hexBinaryValue);

    /**
     * Returns an xs:int based upon the value.
     */
    A createInt(int intValue);

    /**
     * Returns an xs:integer based upon the value.
     */
    A createInteger(BigInteger value);

    /**
     * Returns an xs:integer based upon the value.
     */
    A createInteger(long value);

    /**
     * Creates an implementation of a native type derived from xs:integer.
     * <p>
     * This includes the following native types.
     * <ul>
     * <li>xs:unsignedLong</li>
     * <li>xs:unsignedInt</li>
     * <li>xs:unsignedShort</li>
     * <li>xs:unsignedByte</li>
     * </ul>
     * </p>
     * <p>
     * Whitespace is normalized in a way that is appropriate for the specified native type.
     * </p>
     * <p>
     * Returns <code>null</code> if the argument is <code>null</code>.
     * </p>
     * 
     * @param value
     *            The {@link BigInteger} value of the created atomic value.
     * @param nativeType
     *            The native type, one of {@link NativeType#UNSIGNED_LONG} etc.
     */
    A createIntegerDerived(BigInteger value, NativeType nativeType);

    /**
     * Creates an implementation of a native type derived from xs:integer.
     * <p>
     * This includes the following native types.
     * <ul>
     * <li>xs:unsignedLong</li>
     * <li>xs:unsignedInt</li>
     * <li>xs:unsignedShort</li>
     * <li>xs:unsignedByte</li>
     * </ul>
     * </p>
     * <p>
     * Whitespace is normalized in a way that is appropriate for the specified native type.
     * </p>
     * <p>
     * Returns <code>null</code> if the argument is <code>null</code>.
     * </p>
     * 
     * @param value
     *            The value of the created atomic value.
     * @param nativeType
     *            The native type, one of {@link NativeType#UNSIGNED_LONG} etc.
     */
    A createIntegerDerived(long value, NativeType nativeType);

    /**
     * Returns an xs:long based upon the value.
     */
    A createLong(long longValue);

    /**
     * Returns an xs:gMonth based upon the value.
     *
     * @param month an integer in the range 1-12.
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createMonth(int month, int timezone);

    /**
     * Returns an xs:gMonthDay based upon the value.
     *
     * @param month an integer, which should be in the range 1-12
     * @param dayOfMonth an integer, which should be in the range defined with
                         lower bound 1, upper bound dependent upon the value of
                         the month argument, one of 28, 29, 30, or 31
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createMonthDay(int month, int dayOfMonth, int timezone);

    /**
     * Constructs an xs:NOTATION.
     * 
     * @param namespaceURI
     *            The namespace for the xs:NOTATION.
     * @param localName
     *            The local-name part of the xs:NOTATION.
     * @param prefix
     *            The prefix of the xs:NOTATION. <br/>
     *            The prefix parameter is only a hint that may be used to achieve consistency in round-trip situations.
     */
    A createNOTATION(String namespaceURI, String localName, String prefix);

    /**
     * Constructs an expanded-QName.
     * 
     * @param namespaceURI
     *            The namespace for the expanded-QName as a symbol.
     * @param localName
     *            The local-name part of the expanded-QName as a symbol.
     * @param prefix
     *            The prefix of the expanded-QName as a <code>String</code>. <br/>
     *            The prefix parameter is only a hint that may be used to achieve consistency in round-trip situations.
     */
    A createQName(String namespaceURI, String localName, String prefix);

    /**
     * Returns an xs:short based upon the value.
     */
    A createShort(short shortValue);

    /**
     * Creates an implementation xs:string from an {@link String}. <br/>
     * Returns <code>null</code> if the argument is <code>null</code>.
     * 
     * @param strval
     *            The {@link String} value of the created implementation xs:string.
     */
    A createString(String strval);

    /** Creates an implementation of a native type derived from xs:string.
     * 
     * This includes the following native types.
     * <ul>
     * <li>xs:normalizedString</li>
     * <li>xs:token</li>
     * <li>xs:language</li>
     * <li>xs:NMTOKEN</li>
     * <li>xs:Name</li>
     * <li>xs:NCName</li>
     * <li>xs:ID</li>
     * <li>xs:IDREF</li>
     * <li>xs:ENTITY</li>
     * </ul>
     * 
     * Whitespace is normalized in a way that is appropriate for the specified native type.
     * 
     * Returns <code>null</code> if the argument is <code>null</code>.
     * 
     * @param strval
     *            The {@link String} value of the created atomic value.
     * @param nativeType
     *            The native type, one of {@link NativeType#NORMALIZED_STRING} etc.
     */
    A createStringDerived(String strval, NativeType nativeType);

    /**Creates an xs:time atomic value. 
     *
     * Most of the arguments are required primitive
     * values, but millis may be ignored if fractionalSecond is non-null; either millis
     * or fractionalSecond will be used, but not both, and fractionalSecond will be
     * preferred. No errors need be thrown for creation of values in which one or more
     * components are out of range.
     * @param hourOfDay an integer, which should be in the range 0-23
     * @param minute an integer, which should be in the range 0-59
     * @param second an integer, which should be in the range 0-59
     * @param millis an integer, which should be in the range 0-999; if fractionalSecond is non-null, it will be ignored
     * @param fractionalSecond a BigDecimal representing the subsecond value of the time, to any supported scale; if null, use the millis value
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createTime(int hourOfDay, int minute, int second, int millis, BigDecimal fractionalSecond, int timezone);

    /**
     * Creates an implementation xs:untypedAtomic from a {@link String}. <br/>
     * Returns <code>null</code> if the argument is <code>null</code>.
     * 
     * @param strval
     *            The {@link String} value of the created implementation xs:untypedAtomic.
     */
    A createUntypedAtomic(String strval);

    /**
     * Creates an xs:anyURI based on the specified <code>URI</code>.
     */
    A createURI(URI uri);

    /**
     * Returns an xs:gYear based upon the value.
     *
     * @param year an integer representing (if positive) years since the era,
                   or (if negative) before the era. Note that zero is undefined.
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createYear(int year, int timezone);

    /**
     * Returns an xs:gYearMonth based upon the value.
     *
     * @param year an integer representing (if positive) years since the era,
                   or (if negative) before the era. Note that zero is undefined.
     * @param month an integer, which should be in the range 1-12
     * @param timezone an integer representing the timezone offset in minutes, or the
                       constant AtomBridge.TIMEZONE_UNDEFINED.
     */
    A createYearMonth(int year, int month, int timezone);

    /**
     * Creates an xs:yearMonthDuration atom based upon the number of calendar months.
     * 
     * @param months an integer representing total months in this duration.
     */
    A createYearMonthDuration(int months);
    
    /**
     * Create and return (or return a static) empty sequence of Atoms.
     * 
     * Note that this is exactly equivalent to wrapAtom(null).
     */
    Iterable<A> emptySequence();

    /**
     * Returns the bytes for an atom derived from xs:base64Binary.
     */
    byte[] getBase64Binary(A atom);

    /**
     * Returns the <code>boolean</code> for an atom derived from xs:boolean.
     */
    boolean getBoolean(A atom);

    /**
     * Returns an xs:boolean atom equivalent to <code>false</code>.
     */
    A getBooleanFalse();

    /**
     * Returns an xs:boolean atom equivalent to <code>true</code>.
     */
    A getBooleanTrue();

    /**
     * Returns the <code>byte</code> value of an atom derived from xs:byte.
     */
    byte getByte(A atom);

    /**
     * Returns the canonical string representation of the atomic value.
     * 
     * @param atom
     *            The atom to be serialized.
     * @return The serialization format.
     */
    String getC14NForm(A atom);

    /**
     * A convenience for obtaining the canonical string representation of a list of atoms.
     *
     * The canonical representation is the concatenation of each atom with a single space separator.
     *
     */
    String getC14NString(List<? extends A> atoms);

    /**
     * Determine the specific XML data type of an atom managed by this manager.
     * 
     * @param atom
     *            The atomic value for which the data-type is required.
     */
    QName getDataType(A atom);

    /**
     * Returns the day-of-month component of the Gregorian atomic value. <br/>
     * For Gregorian values that do not have a day-of-month component, returns <code>1</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getDayOfMonth(A gregorian);

    /**
     * Returns the <code>BigDecimal</code> value of an atom derived from xs:decimal.
     */
    BigDecimal getDecimal(A atom);

    /**
     * Returns the <code>double</code> value of an atom derived from xs:double.
     */
    double getDouble(A atom);

    /**
     * Returns the total number of months part of an atom derived from xs:duration.
     */
    int getDurationTotalMonths(A duration);

    /**
     * Returns the total number of seconds part of an atom derived from xs:duration.
     */
    BigDecimal getDurationTotalSeconds(A duration);

    /**
     * Returns the <code>float</code> value of an atom derived from xs:float.
     */
    float getFloat(A atom);

    /**
     * Returns the fractional part of the seconds component. <br/>
     * For Gregorian values that do not have a seconds component, returns <code>zero</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    BigDecimal getFractionalSecondPart(A gregorian);

    /**
     * Returns the offset, in minutes, from GMT. <br/>
     * This is an optional component of the Gregorian atomic value. <br/>
     * Returns <code>javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED</code> if the Gregorian value has no timezone.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getGmtOffset(A gregorian);

    /**
     * Returns the bytes for an atom derived from xs:hexBinary.
     */
    byte[] getHexBinary(A arg);

    /**
     * Returns the hours component of the Gregorian atomic value. <br/>
     * For Gregorian values that do not have an hours component, returns <code>0</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getHourOfDay(A gregorian);

    /**
     * Returns an <code>int</code> from an atom that is a subtype of xs:int
     * 
     * @param atom
     *            An atom that is a subtype of xs:int.
     */
    int getInt(A atom);

    /**
     * Returns a <code>BigInteger</code> from an atom that is a subtype of xs:integer.
     * 
     * @param atom
     *            An atom that is a subtype of xs:integer.
     */
    BigInteger getInteger(A atom);

    /**
     * Returns the integral part of the seconds component. <br/>
     * For Gregorian values that do not have a seconds component, returns <code>0</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getIntegralSecondPart(A gregorian);

    /** Given an atom which is of type QName, retrieve its name portion.
     *
     * @param atom the atom representing a QName type which is to be investigated
     * @return the string representing the QName's local name portion
     */
    String getLocalNameFromQName(A atom);

    /**
     * Returns a <code>long</code> from an atom that is a subtype of xs:long (xs:long, xs:short, xs:int or xs:byte).
     * 
     * @param atom
     *            An atom that is a subtype of xs:int.
     */
    long getLong(A atom);

    /**
     * Returns the minutes component of the Gregorian atomic value. <br/>
     * For Gregorian values that do not have a minutes component, returns <code>0</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getMinute(A gregorian);

    /**
     * Returns the month component of the Gregorian atomic value. <br/>
     * For Gregorian values that do not have a month component, returns <code>1</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getMonth(A gregorian);

    /** Given an atom which is of type QName, retrieve its namespace portion.
     *
     * @param atom the atom representing a QName type which is to be investigated
     * @return the string representing the QName's namespace
     */
    String getNamespaceFromQName(A atom);

    /**
     * Casts the specified atom up the hierarchy to the nearest native type.
     * <p>
     * This never fails because it is a cast up the hierarchy.
     * </p>
     * 
     * @param atom
     *            The atom to be cast.
     */
    A getNativeAtom(A atom);

    /**
     * Determine the nearest ancestor Built-in type of an atom managed by this manager.
     * 
     * @param atom
     *            The atomic value for which the data-type is required.
     */

    /** Get the NativeType of the supplied atom
     *
     * NativeType is an enumerated type listing the base primitive types and
     * their relationships and characteristics.
     * @param atom the atom to be queried
     * @return the native type of the queried atom, or null if the atom is null
     */
    NativeType getNativeType(A atom);

    /**
     * Convert an atom known to be a the internal representation of an xs:NOTATION to {@link QName}. <br/>
     * If the argument is <code>null</code>, this function returns <code>null</code>.
     * 
     * @param notation
     *            The internal representation; may be <code>null</code>.
     */
    QName getNotation(A notation);

    /** Given an atom which is of type QName, retrieve its prefix hint portion.
     *
     * @param atom the atom representing a QName type which is to be investigated
     * @return the string representing the atom's understanding of its preferred prefix
     */
    String getPrefixFromQName(A atom);

    /**
     * Convert an atom known to be a the internal representation of an xs:QName to {@link QName}. <br/>
     * If the argument is <code>null</code>, this function returns <code>null</code>.
     * 
     * @param atom
     *            The internal representation; may be <code>null</code>.
     */
    QName getQName(A atom);

    /**
     * Returns the whole of the seconds value. <br/>
     * For Gregorian values that do not have a seconds component, returns <code>null</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    BigDecimal getSecondsAsBigDecimal(A gregorian);

    /**
     * Returns a <code>short</code> from an atom that is a subtype of xs:short (xs:short or xs:byte).
     * 
     * @param atom
     *            An atom that is a subtype of xs:short.
     */
    short getShort(A atom);

    /**
     * Converts the atom, assumed to be an implementation xs:string, to get a string value. <br/>
     * Returns <code>null</code> if the argument is <code>null</code>.
     * 
     * @param atom
     *            The atom that is assumed to represent an xs:string.
     */
    String getString(A atom);

    /**
     * Returns a <code>short</code> from an atom that is a subtype of xs:unsignedByte.
     * 
     * @param atom
     *            An atom that is a subtype of xs:unsignedByte.
     */
    short getUnsignedByte(A atom);

    /**
     * Returns a <code>long</code> from an atom that is a subtype of xs:unsignedInt.
     * 
     * @param atom
     *            An atom that is a subtype of xs:unsignedInt.
     */
    long getUnsignedInt(A atom);

    /**
     * Returns a <code>int</code> from an atom that is a subtype of xs:unsignedShort.
     * 
     * @param atom
     *            An atom that is a subtype of xs:unsignedShort.
     */
    int getUnsignedShort(A atom);

    URI getURI(A atom);

    /**
     * Returns the XPath 1.0 string representation of the atomic value.
     * 
     * @param atom
     *            The atom to be serialized.
     * @return The serialization format.
     */
    String getXPath10Form(A atom);

    /**
     * Returns the XQuery 1.0 string representation of the atomic value.
     * 
     * @param atom
     *            The atom to be serialized.
     * @return The serialization format.
     */
    String getXQuery10Form(A atom);

    /**
     * Returns the year component of the Gregorian atomic value. <br/>
     * For Gregorian values that do not have a year component, returns <code>1970</code>.
     * 
     * @param gregorian
     *            The gregorian atomic value.
     */
    int getYear(A gregorian);

    /** Determines whether the object is an atom.
     *
     * If the object is <code>null</code>, the return value is <code>false</code>.
     * 
     * @param object
     *            The candidate object.
     */
    boolean isAtom(Object object);

    /**
     * Determines whether the specified atom is foreign (non native).
     * 
     * @param atom
     *            The atomic value to be tested.
     * @return <code>true</code> if the atom is foreign, otherwise <code>false</code>.
     */
    boolean isForeignAtom(A atom);

    /**
     * Determines whether the specified atom represents XML white space.
     * 
     * @param atom
     *            The atomic value to be tested for being entirely white space.
     */
    boolean isWhiteSpace(A atom);

    /** Create a 'foreign' atom, which is an atom derived from some base known
     * to this bridge.
     *
     * @param atomType the QName of the derived type; may not be null
     * @param baseAtom the actual value of the atom, as represented in the
     *                 base type
     * @return an atom which contains as its value the base atom, and the
     *         QName annotating its actual type.
     */
    A makeForeignAtom(QName atomType, A baseAtom);

    /** Extract a single atom from a sequence containing a single atom only.
     *
     * @param sequence the sequence from which to extract the atom
     * @return the wrapped atom, if there is only a single atom in the sequence
     * @throw AtomCastException if there is more than one atom in the sequence
     */
    A unwrapAtom(Iterable<? extends A> sequence) throws AtomCastException;
    
    /** Promotes a single atom into an {@link Iterable} sequence containing the same single atom.
     *
     * Note this method is provided for performance reasons. Some implementations may implement {@link Iterable} on their atoms to avoid physically wrapping the atom.
     * 
     * @param atom the atom to be wrapped, which may be null. If null, the
                   implementation must return an empty Iterable, not null,
                   to provide a guarantee of non-null to callers.
     */
    List<A> wrapAtom(A atom);
}
