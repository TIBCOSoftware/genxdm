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

    // TODO: document based on implementation contract
    /**
     * Returns an xs:date atomic value.
     * 
     * @param year For what year do we wish to create a date?
     * @param month For what month?
     * @param dayOfMonth    For what day of the month?
     * @param timezone  In what timezone
     * @return  An atomic value corresponding to the parameters passed.
     */
    A createDate(int year, int month, int dayOfMonth, int timezone);

    // TODO: document based on implementation contract
    /**
     * Returns an xs:dateTime atomic value.
     */
    A createDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second, int millis, BigDecimal remainderSecond, int offsetInMinutes);

    // TODO: document based on implementation contract
    /**
     * Returns an xs:gDay based upon the value.
     */
    A createDay(int dayOfMonth, int timezone);

    // TODO: document based on implementation contract
    /**
     * Returns an xs:dayTimeDuration based upon the seconds value.
     * 
     * @param seconds
     *            The total number of seconds in the duration.
     */
    A createDayTimeDuration(BigDecimal seconds);

    // TODO: document based on implementation contract
    /**
     * Returns an xs:decimal based upon the value.
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

    // TODO: document based on implementation contract
    /**
     * Returns an xs:duration based upon the total months and seconds.
     * 
     * @param yearMonthDuration
     *            The total months.
     * @param dayTimeDuration
     *            The total seconds.
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

    // TODO: document based on implementation contract
    /**
     * Returns an xs:gMonth based upon the value.
     */
    A createMonth(int month, int timezone);

    // TODO: document based on implementation contract
    /**
     * Returns an xs:gMonthDay based upon the value.
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

    /**
     * Creates an implementation of a native type derived from xs:string.
     * <p>
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
     * </p>
     * <p>
     * Whitespace is normalized in a way that is appropriate for the specified native type.
     * </p>
     * <p>
     * Returns <code>null</code> if the argument is <code>null</code>.
     * </p>
     * 
     * @param strval
     *            The {@link String} value of the created atomic value.
     * @param nativeType
     *            The native type, one of {@link NativeType#NORMALIZED_STRING} etc.
     */
    A createStringDerived(String strval, NativeType nativeType);

    // TODO: document based on implementation contract
    /**
     * Creates an xs:time atomic value.
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

    // TODO: document based on implementation contract
    /**
     * Returns an xs:gYear based upon the value.
     */
    A createYear(int year, int timezone);

    // TODO: document based on implementation contract
    /**
     * Returns an xs:gYearMonth based upon the value.
     */
    A createYearMonth(int year, int month, int timezone);

    // TODO: document based on implementation contract
    /**
     * Creates an xs:yearMonthDuration atom based upon the number of calendar months.
     * 
     * @param months
     *            The total number of months in the duration.
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
     * <p>
     * The canonical representation is the concatenation of each atom with a single space separator.
     * </p>
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

    NativeType getNativeType(A atom);

    /**
     * Convert an atom known to be a the internal representation of an xs:NOTATION to {@link QName}. <br/>
     * If the argument is <code>null</code>, this function returns <code>null</code>.
     * 
     * @param notation
     *            The internal representation; may be <code>null</code>.
     */
    QName getNotation(A notation);

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

    /**
     * Determines whether the object is an atom.
     * <p/>
     * If the object is <code>null</code>, the return value is <code>false</code>.
     * </p>
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

    A makeForeignAtom(QName atomType, A baseAtom);

    // get an atom from an iterable<atom>, but only if there's only one.
    A unwrapAtom(Iterable<? extends A> sequence) throws AtomCastException;
    
    /**
     * Promotes a single atom into an {@link Iterable} sequence containing the same single atom. <br/>
     * Note this method is provided for performance reasons. Some implementations may implement {@link Iterable} on their atoms to avoid physically wrapping the atom.
     * 
     * @param atom
     *            The atom to be wrapped. May be <code>null</code>. If <code>null</code> is passed as an argument, the implementation <em>must</em> return an empty {@link Iterable} . This provides consistent semantics for the
     */
    List<A> wrapAtom(A atom);
}
