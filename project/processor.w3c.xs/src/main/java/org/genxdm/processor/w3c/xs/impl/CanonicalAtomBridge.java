/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.processor.w3c.xs.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.atoms.CastingSupport;
import org.genxdm.bridgekit.atoms.NumericSupport;
import org.genxdm.bridgekit.atoms.XmlAnyURI;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlBase64Binary;
import org.genxdm.bridgekit.atoms.XmlBoolean;
import org.genxdm.bridgekit.atoms.XmlByte;
import org.genxdm.bridgekit.atoms.XmlDayTimeDuration;
import org.genxdm.bridgekit.atoms.XmlDecimal;
import org.genxdm.bridgekit.atoms.XmlDouble;
import org.genxdm.bridgekit.atoms.XmlDuration;
import org.genxdm.bridgekit.atoms.XmlENTITY;
import org.genxdm.bridgekit.atoms.XmlFloat;
import org.genxdm.bridgekit.atoms.XmlForeignAtom;
import org.genxdm.bridgekit.atoms.XmlGregorian;
import org.genxdm.bridgekit.atoms.XmlHexBinary;
import org.genxdm.bridgekit.atoms.XmlID;
import org.genxdm.bridgekit.atoms.XmlIDREF;
import org.genxdm.bridgekit.atoms.XmlInt;
import org.genxdm.bridgekit.atoms.XmlInteger;
import org.genxdm.bridgekit.atoms.XmlIntegerDerived;
import org.genxdm.bridgekit.atoms.XmlLanguage;
import org.genxdm.bridgekit.atoms.XmlLong;
import org.genxdm.bridgekit.atoms.XmlNCName;
import org.genxdm.bridgekit.atoms.XmlNMTOKEN;
import org.genxdm.bridgekit.atoms.XmlNOTATION;
import org.genxdm.bridgekit.atoms.XmlName;
import org.genxdm.bridgekit.atoms.XmlNormalizedString;
import org.genxdm.bridgekit.atoms.XmlQName;
import org.genxdm.bridgekit.atoms.XmlShort;
import org.genxdm.bridgekit.atoms.XmlString;
import org.genxdm.bridgekit.atoms.XmlToken;
import org.genxdm.bridgekit.atoms.XmlUntypedAtomic;
import org.genxdm.bridgekit.atoms.XmlYearMonthDuration;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public class CanonicalAtomBridge
    implements AtomBridge<XmlAtom>
{

    CanonicalAtomBridge(ComponentProvider provider)
    {
        this.components = PreCondition.assertNotNull(provider, "provider");
    }
    
    @Override
    public XmlAtom atom(Object object)
    {
        if (object instanceof XmlAtom)
            return (XmlAtom)object;
        return null;
    }

    @Override
    public XmlAtom[] atomArray(int size)
    {
        return new XmlAtom[size];
    }

    @Override
    public XmlAtom upCast(XmlAtom sourceAtom)
    {
        PreCondition.assertNotNull(sourceAtom, "sourceAtom");
        if (sourceAtom instanceof XmlForeignAtom)
            return ((XmlForeignAtom)sourceAtom).baseAtom;
        throw new AssertionError("baseAtomFromForeignAtom(" + sourceAtom.getClass() + ")");
    }

    @Override
    public XmlAtom castAs(XmlAtom sourceAtom, QName targetType, CastingContext castingContext)
        throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(castingContext, "castingContext");
        if (sourceAtom == null)
            return null;
        final NativeType nativeType = NameSource.SINGLETON.nativeType(targetType);
        if (nativeType != null)
            return castAs(sourceAtom, nativeType, castingContext);
        else
            throw new AtomCastException(getC14NForm(sourceAtom), targetType, FORG0006);
    }

    @Override
    public XmlAtom castAs(XmlAtom sourceAtom, NativeType targetType, CastingContext castingContext)
        throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(castingContext, "castingContext");
        return CastingSupport.castAs(getNativeAtom(sourceAtom), targetType, castingContext, components, this);
    }

    @Override
    public XmlAtom compile(String srcval, NativeType dataType)
        throws AtomCastException
    {
        PreCondition.assertNotNull(srcval, "srcval");
        PreCondition.assertNotNull(dataType, "dataType");
        final Type type = components.getTypeDefinition(dataType);
        if (type != null)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<XmlAtom> atoms = atomicType.validate(srcval, this);
                    if (atoms.size() == 0)
                        return null;
                    else if (atoms.size() == 1)
                        return atoms.get(0);
                    // TODO: better exception?
                    throw new AssertionError(); // because a native type should return a single value
                }
                catch (DatatypeException dte)
                {
                    throw new AtomCastException(srcval, dte.getType(), FORG0001, dte);
                }
            }
            throw new IllegalArgumentException("Datatype '" + dataType + "' is not an atomic type");
        }
        throw new IllegalArgumentException("Datatype '" + dataType + "' could not be found in the schema context.");
    }

    @Override
    public XmlAtom compile(String srcval, NativeType dataType, PrefixResolver resolver)
        throws AtomCastException
    {
        // use base unless this has a resolver and is a QName
        if ( (resolver == null) || (dataType != NativeType.QNAME) )
            return compile(srcval, dataType);

        PreCondition.assertNotNull(srcval, "sourceValue");
        PreCondition.assertNotNull(dataType, "targetType");
        final Type type = components.getTypeDefinition(dataType);
        if (type != null)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<XmlAtom> atoms = atomicType.validate(srcval, resolver, this);
                    final int size = atoms.size();
                    if (size == 1)
                        return atoms.get(0);
                    else if (size == 0)
                        return null;
                    else
                        // Atomic type should not be yielding multiple atoms.
                        throw new AssertionError("Non-list type results in multiple atoms");
                }
                catch (final DatatypeException e)
                {
                    throw new AtomCastException(srcval, e.getType(), FORG0001, e);
                }
            }
            throw new AtomCastException(srcval, dataType.toQName(), FORG0006);
        }
        throw new AtomCastException(srcval, dataType.toQName(), FORG0006);
    }

    @Override
    public XmlAtom createBase64Binary(byte[] base64BinaryValue)
    {
        return new XmlBase64Binary(base64BinaryValue);
    }

    @Override
    public XmlAtom createBoolean(boolean booleanValue)
    {
        return XmlBoolean.valueOf(booleanValue);
    }

    @Override
    public XmlAtom createByte(byte byteValue)
    {
        return new XmlByte(byteValue);
    }

    @Override
    public XmlAtom createDate(int year, int month, int dayOfMonth, int timezone)
    {
        return new XmlGregorian(year, month, dayOfMonth, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.DATE);
    }

    @Override
    public XmlAtom createDateTime(int year, int month, int dayOfMonth,
                                  int hour, int minute, int second, int millis,
                                  BigDecimal remainderSecond, int offsetInMinutes)
    {
        return new XmlGregorian(year, month, dayOfMonth, hour, minute, second, remainderSecond, offsetInMinutes, NativeType.DATETIME);
    }

    @Override
    public XmlAtom createDay(int dayOfMonth, int timezone)
    {
        // 1 Jan 1970 is the epoch
        return new XmlGregorian(1970, 1, dayOfMonth, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GDAY);
    }

    @Override
    public XmlAtom createDayTimeDuration(BigDecimal seconds)
    {
        return new XmlDayTimeDuration(seconds);
    }

    @Override
    public XmlAtom createDecimal(BigDecimal decimalValue)
    {
        return XmlDecimal.valueOf(decimalValue);
    }

    @Override
    public XmlAtom createDecimal(long decimalValue)
    {
        return XmlDecimal.valueOf(decimalValue);
    }

    @Override
    public XmlAtom createDouble(double value)
    {
        return new XmlDouble(value);
    }

    @Override
    public XmlAtom createDuration(int yearMonthDuration, BigDecimal dayTimeDuration)
    {
        return new XmlDuration(yearMonthDuration, dayTimeDuration);
    }

    @Override
    public XmlAtom createFloat(float floatValue)
    {
        return new XmlFloat(floatValue);
    }

    @Override
    public XmlAtom createHexBinary(byte[] hexBinaryValue)
    {
        return new XmlHexBinary(hexBinaryValue);
    }

    @Override
    public XmlAtom createInt(int intValue)
    {
        return new XmlInt(intValue);
    }

    @Override
    public XmlAtom createInteger(BigInteger value)
    {
        return XmlInteger.valueOf(value);
    }

    @Override
    public XmlAtom createInteger(long value)
    {
        return XmlInteger.valueOf(value);
    }

    @Override
    public XmlAtom createIntegerDerived(BigInteger value, NativeType nativeType)
    {
        switch (nativeType)
        {
            case NON_POSITIVE_INTEGER:
            case NEGATIVE_INTEGER:
            case NON_NEGATIVE_INTEGER:
            case POSITIVE_INTEGER:
            case UNSIGNED_LONG:
            case UNSIGNED_INT:
            case UNSIGNED_SHORT:
            case UNSIGNED_BYTE:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            default:
            {
                throw new AssertionError(nativeType);
            }
        }
    }

    @Override
    public XmlAtom createIntegerDerived(long value, NativeType nativeType)
    {
        switch (nativeType)
        {
            case INTEGER:
            {
                return XmlInteger.valueOf(value);
            }
            case NON_POSITIVE_INTEGER:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case NEGATIVE_INTEGER:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case NON_NEGATIVE_INTEGER:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case UNSIGNED_LONG:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case UNSIGNED_INT:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case UNSIGNED_SHORT:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case UNSIGNED_BYTE:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            case POSITIVE_INTEGER:
            {
                return XmlIntegerDerived.valueOf(value, nativeType);
            }
            default:
            {
                throw new AssertionError(nativeType);
            }
        }
    }

    @Override
    public XmlAtom createLong(long longValue)
    {
        return new XmlLong(longValue);
    }

    @Override
    public XmlAtom createMonth(int month, int timezone)
    {
        // 1 jan 1970 is the epoch
        return new XmlGregorian(1970, month, 1, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GMONTH);
    }

    @Override
    public XmlAtom createMonthDay(int month, int dayOfMonth, int timezone)
    {
        return new XmlGregorian(1970, month, dayOfMonth, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GMONTHDAY);
    }

    @Override
    public XmlAtom createNOTATION(String namespaceURI, String localName, String prefix)
    {
        return new XmlNOTATION(namespaceURI, localName, prefix);
    }

    @Override
    public XmlAtom createQName(String namespaceURI, String localName, String prefix)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        PreCondition.assertNotNull(localName, "localName");
        return new XmlQName(namespaceURI, localName, prefix);
    }

    @Override
    public XmlAtom createShort(short shortValue)
    {
        return new XmlShort(shortValue);
    }

    @Override
    public XmlAtom createString(String strval)
    {
        if (strval != null)
            return new XmlString(strval);
        return null;
    }

    @Override
    public XmlAtom createStringDerived(String strval, NativeType nativeType)
    {
        if (strval != null)
        {
            PreCondition.assertNotNull(nativeType, "nativeType");
            final SimpleType simpleType = (SimpleType)components.getTypeDefinition(nativeType);
            final String normalized =  simpleType.normalize(strval);
            switch (nativeType)
            {
                case NORMALIZED_STRING:
                {
                    return new XmlNormalizedString(normalized);
                }
                case TOKEN:
                {
                    return new XmlToken(normalized);
                }
                case LANGUAGE:
                {
                    return new XmlLanguage(normalized);
                }
                case NAME:
                {
                    return new XmlName(normalized);
                }
                case NMTOKEN:
                {
                    return new XmlNMTOKEN(normalized);
                }
                case NCNAME:
                {
                    return new XmlNCName(normalized);
                }
                case ID:
                {
                    return new XmlID(normalized);
                }
                case IDREF:
                {
                    return new XmlIDREF(normalized);
                }
                case ENTITY:
                {
                    return new XmlENTITY(normalized);
                }
                default:
                {
                    throw new AssertionError("createStringDerived('" + normalized + "', " + nativeType + ")");
                }
            }
        }
        return null;
    }

    @Override
    public XmlAtom createTime(int hourOfDay, int minute, int second,
                              int millis, BigDecimal fractionalSecond, int timezone)
    {
        // 1 january 1970 is the epoch
        return new XmlGregorian(1970, 1, 1, hourOfDay, minute, second, fractionalSecond, timezone, NativeType.TIME);
    }

    @Override
    public XmlAtom createUntypedAtomic(String strval)
    {
        return new XmlUntypedAtomic(strval);
    }

    @Override
    public XmlAtom createURI(URI uri)
    {
        if (uri != null)
            return new XmlAnyURI(uri);
        return null;
    }

    @Override
    public XmlAtom createYear(int year, int timezone)
    {
        // 1 january 1970 is the epoch
        return new XmlGregorian(year, 1, 1, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GYEAR);
    }

    @Override
    public XmlAtom createYearMonth(int year, int month, int timezone)
    {
        // 1 jan 1970 is the epoch
        return new XmlGregorian(year, month, 1, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GYEARMONTH);
    }

    @Override
    public XmlAtom createYearMonthDuration(int months)
    {
        return XmlYearMonthDuration.valueOf(months);
    }
    
    @Override
    public Iterable<XmlAtom> emptySequence()
    {
        return EMPTY_ATOM_SEQUENCE;
    }

    @Override
    public byte[] getBase64Binary(XmlAtom atom)
    {
        if (atom instanceof XmlBase64Binary)
        {
            return ((XmlBase64Binary)atom).getByteArrayValue();
        }
        else if (isForeignAtom(atom))
        {
            return getBase64Binary(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlBase64Binary, "atom instance of xs:base64Binary");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public boolean getBoolean(XmlAtom atom)
    {
        if (atom instanceof XmlBoolean)
        {
            return ((XmlBoolean)atom).getBooleanValue();
        }
        else if (isForeignAtom(atom))
        {
            return getBoolean(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlBoolean, "atom instance of xs:boolean");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public XmlAtom getBooleanFalse()
    {
        return XmlBoolean.FALSE;
    }

    @Override
    public XmlAtom getBooleanTrue()
    {
        return XmlBoolean.TRUE;
    }

    @Override
    public byte getByte(XmlAtom atom)
    {
        if (atom instanceof XmlByte)
        {
            return ((XmlByte)atom).getByteValue();
        }
        else if (isForeignAtom(atom))
        {
            return getByte(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getByte(" + atom.getClass() + ")");
        }
    }

    @Override
    public String getC14NForm(XmlAtom atom)
    {
        return atom.getC14NForm();
    }

    @Override
    public String getC14NString(List<? extends XmlAtom> atoms)
    {
        final int size = atoms.size();
        if (size > 0)
        {
            if (size == 1)
            {
                return getC14NForm(atoms.get(0));
            }
            else
            {
                final StringBuilder sb = new StringBuilder();
                sb.append(getC14NForm(atoms.get(0)));
                for (int i = 1; i < size; i++)
                {
                    sb.append(" ");
                    sb.append(getC14NForm(atoms.get(i)));
                }
                return sb.toString();
            }
        }
        else if (size < 0)
        {
            throw new IllegalArgumentException("atoms.size() must be greater than or equal to zero.");
        }
        return "";
    }

    @Override
    public QName getDataType(XmlAtom atom)
    {
        return NameSource.SINGLETON.nativeType(atom.getNativeType());
    }

    @Override
    public int getDayOfMonth(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getDayOfMonth();
        }
        else if (isForeignAtom(gregorian))
        {
            return getDayOfMonth(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getDayOfMonth(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public BigDecimal getDecimal(XmlAtom atom)
    {
        if (atom instanceof XmlDecimal)
        {
            return ((XmlDecimal)atom).getBigDecimalValue();
        }
        else if (atom instanceof XmlInteger)
        {
            return new BigDecimal(((XmlInteger)atom).getBigIntegerValue());
        }
        else if (atom instanceof XmlLong)
        {
            return BigDecimal.valueOf(((XmlLong)atom).getLongValue());
        }
        else if (atom instanceof XmlInt)
        {
            return BigDecimal.valueOf(((XmlInt)atom).getIntValue());
        }
        else if (atom instanceof XmlShort)
        {
            return BigDecimal.valueOf(((XmlShort)atom).getShortValue());
        }
        else if (atom instanceof XmlByte)
        {
            return BigDecimal.valueOf(((XmlByte)atom).getByteValue());
        }
        else if (atom instanceof XmlIntegerDerived)
        {
            return new BigDecimal(((XmlIntegerDerived)atom).integerValue());
        }
        else if (isForeignAtom(atom))
        {
            return getDecimal(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getDecimal(" + atom.getClass() + ")");
        }
    }

    @Override
    public double getDouble(XmlAtom atom)
    {
        if (atom instanceof XmlDouble)
        {
            return ((XmlDouble)atom).getDoubleValue();
        }
        else if (isForeignAtom(atom))
        {
            return getDouble(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getDouble(" + atom.getClass() + ")");
        }
    }

    @Override
    public int getDurationTotalMonths(XmlAtom duration)
    {
        if (duration instanceof XmlYearMonthDuration)
        {
            return ((XmlYearMonthDuration)duration).getTotalMonthsValue();
        }
        else if (duration instanceof XmlDuration)
        {
            return ((XmlDuration)duration).getTotalMonthsValue();
        }
        else if (isForeignAtom(duration))
        {
            return getDurationTotalMonths(getNativeAtom(duration));
        }
        else
        {
            PreCondition.assertNotNull(duration, "duration");
            PreCondition.assertTrue(duration instanceof XmlYearMonthDuration, "atom instance of xs:yearMonthDuration");
            throw new AssertionError(duration.getClass());
        }
    }

    @Override
    public BigDecimal getDurationTotalSeconds(XmlAtom duration)
    {
        if (duration instanceof XmlDayTimeDuration)
        {
            return ((XmlDayTimeDuration)duration).getTotalSecondsValue();
        }
        else if (duration instanceof XmlDuration)
        {
            return ((XmlDuration)duration).getTotalSecondsValue();
        }
        else if (isForeignAtom(duration))
        {
            return getDurationTotalSeconds(getNativeAtom(duration));
        }
        else
        {
            PreCondition.assertNotNull(duration, "duration");
            PreCondition.assertTrue(false, "atom instance of xs:duration");
            throw new AssertionError(duration.getClass());
        }
    }

    @Override
    public float getFloat(XmlAtom atom)
    {
        if (atom instanceof XmlFloat)
        {
            return ((XmlFloat)atom).getFloatValue();
        }
        else if (isForeignAtom(atom))
        {
            return getFloat(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlFloat, "atom instance of xs:float");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public BigDecimal getFractionalSecondPart(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getFractionalSecond();
        }
        else if (isForeignAtom(gregorian))
        {
            return getFractionalSecondPart(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getFractionalSecond(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public int getGmtOffset(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getGmtOffset();
        }
        else if (isForeignAtom(gregorian))
        {
            return getGmtOffset(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getGmtOffset(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public byte[] getHexBinary(XmlAtom arg)
    {
        if (arg instanceof XmlHexBinary)
        {
            return ((XmlHexBinary)arg).getByteArrayValue();
        }
        else if (isForeignAtom(arg))
        {
            return getHexBinary(getNativeAtom(arg));
        }
        else
        {
            PreCondition.assertNotNull(arg, "atom");
            PreCondition.assertTrue(arg instanceof XmlHexBinary, "atom instance of xs:hexBinary");
            throw new AssertionError(arg.getClass());
        }
    }

    @Override
    public int getHourOfDay(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getHourOfDay();
        }
        else if (isForeignAtom(gregorian))
        {
            return getHourOfDay(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getHourOfDay(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public int getInt(XmlAtom atom)
    {
        if (atom instanceof XmlInt)
        {
            return ((XmlInt)atom).getIntValue();
        }
        else if (atom instanceof XmlShort)
        {
            return ((XmlShort)atom).getShortValue();
        }
        else if (atom instanceof XmlByte)
        {
            return ((XmlByte)atom).getByteValue();
        }
        else if (isForeignAtom(atom))
        {
            return getInt(upCast(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getInt(" + atom.getClass() + ")");
        }
    }

    @Override
    public BigInteger getInteger(XmlAtom atom)
    {
        if (atom instanceof XmlInteger)
        {
            return ((XmlInteger)atom).getBigIntegerValue();
        }
        else if (atom instanceof XmlLong)
        {
            return BigInteger.valueOf(((XmlLong)atom).getLongValue());
        }
        else if (atom instanceof XmlInt)
        {
            return BigInteger.valueOf(((XmlInt)atom).getIntValue());
        }
        else if (atom instanceof XmlShort)
        {
            return BigInteger.valueOf(((XmlShort)atom).getShortValue());
        }
        else if (atom instanceof XmlByte)
        {
            return BigInteger.valueOf(((XmlByte)atom).getByteValue());
        }
        else if (atom instanceof XmlIntegerDerived)
        {
            return ((XmlIntegerDerived)atom).integerValue();
        }
        else if (atom instanceof XmlForeignAtom)
        {
            return getInteger(((XmlForeignAtom)atom).baseAtom);
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getInteger(" + atom.getClass() + ")");
        }
    }

    @Override
    public int getIntegralSecondPart(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getSecond();
        }
        else if (isForeignAtom(gregorian))
        {
            return getIntegralSecondPart(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getSecond(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public String getLocalNameFromQName(XmlAtom atom)
    {
        if (atom instanceof XmlQName)
        {
            return ((XmlQName)atom).getLocalName();
        }
        else if (isForeignAtom(atom))
        {
            return getLocalNameFromQName(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getLocalNameFromQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public long getLong(XmlAtom atom)
    {
        if (atom instanceof XmlLong)
        {
            return ((XmlLong)atom).getLongValue();
        }
        else if (atom instanceof XmlInt)
        {
            return ((XmlInt)atom).getIntValue();
        }
        else if (atom instanceof XmlShort)
        {
            return ((XmlShort)atom).getShortValue();
        }
        else if (atom instanceof XmlByte)
        {
            return ((XmlByte)atom).getByteValue();
        }
        else if (isForeignAtom(atom))
        {
            return getLong(upCast(atom));
        }
        else
        {
            if (null != atom)
            {
                throw new AssertionError("getLong(" + atom.getClass().getName() + ")");
            }
            else
            {
                // Consistent with Unboxing.
                throw new NullPointerException();
            }
        }
    }

    @Override
    public int getMinute(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getMinute();
        }
        else if (isForeignAtom(gregorian))
        {
            return getMinute(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getMinute(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public int getMonth(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getMonth();
        }
        else if (isForeignAtom(gregorian))
        {
            return getMonth(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getMonth(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public String getNamespaceFromQName(XmlAtom atom)
    {
        if (atom instanceof XmlQName)
        {
            return ((XmlQName)atom).getNamespaceURI();
        }
        else if (isForeignAtom(atom))
        {
            return getNamespaceFromQName(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getNamespaceFromQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public XmlAtom getNativeAtom(XmlAtom atom)
    {
        while (isForeignAtom(atom))
        {
            return getNativeAtom(upCast(atom));
        }
        return atom;
    }

    @Override
    public NativeType getNativeType(XmlAtom atom)
    {
        return atom.getNativeType();
    }

    @Override
    public QName getNotation(XmlAtom atom)
    {
        if (atom instanceof XmlNOTATION)
        {
            final XmlNOTATION name = (XmlNOTATION)atom;
            return new QName(name.getNamespaceURI().toString(), name.getLocalName().toString(), name.getPrefix());
        }
        else if (isForeignAtom(atom))
        {
            return getNotation(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getNotation(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public String getPrefixFromQName(XmlAtom atom)
    {
        if (atom instanceof XmlQName)
        {
            return ((XmlQName)atom).getPrefix();
        }
        else if (isForeignAtom(atom))
        {
            return getPrefixFromQName(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getPrefixFromQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public QName getQName(XmlAtom atom)
    {
        if (atom instanceof XmlQName)
        {
            final XmlQName qname = (XmlQName)atom;
            return new QName(qname.getNamespaceURI().toString(), qname.getLocalName().toString(), qname.getPrefix());
        }
        else if (isForeignAtom(atom))
        {
            return getQName(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public BigDecimal getSecondsAsBigDecimal(XmlAtom gregorian)
    {
        final BigDecimal isp = BigDecimal.valueOf(getIntegralSecondPart(gregorian));
        final BigDecimal fsp = getFractionalSecondPart(gregorian);
        if (null != fsp)
        {
            return isp.add(fsp);
        }
        else
        {
            return isp;
        }
    }

    @Override
    public short getShort(XmlAtom atom)
    {
        if (atom instanceof XmlShort)
        {
            return ((XmlShort)atom).getShortValue();
        }
        else if (atom instanceof XmlByte)
        {
            return ((XmlByte)atom).getByteValue();
        }
        else if (isForeignAtom(atom))
        {
            return getShort(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getShort(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public String getString(XmlAtom atom)
    {
        if (atom instanceof XmlString)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlNormalizedString)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlToken)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlLanguage)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlName)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlNMTOKEN)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlNCName)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlID)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlIDREF)
        {
            return atom.getC14NForm();
        }
        else if (atom instanceof XmlENTITY)
        {
            return atom.getC14NForm();
        }
        else if (isForeignAtom(atom))
        {
            return getString(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getString(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public short getUnsignedByte(XmlAtom atom)
    {
        if (atom instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived integer = (XmlIntegerDerived)atom;
            if (integer.getNativeType() == NativeType.UNSIGNED_BYTE)
            {
                return integer.shortValue();
            }
            else
            {
                throw new AssertionError(atom.getClass());
            }
        }
        else if (isForeignAtom(atom))
        {
            return getUnsignedByte(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getUnsignedByte(" + atom.getClass() + ")");
        }
    }

    @Override
    public long getUnsignedInt(XmlAtom atom)
    {
        if (atom instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived integer = (XmlIntegerDerived)atom;
            if (integer.getNativeType().isA(NativeType.UNSIGNED_INT))
            {
                return integer.longValue();
            }
            else
            {
                throw new AssertionError(atom.getClass());
            }
        }
        else if (isForeignAtom(atom))
        {
            return getUnsignedInt(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getUnsignedInt(" + atom.getClass() + ")");
        }
    }

    @Override
    public int getUnsignedShort(XmlAtom atom)
    {
        if (atom instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived integer = (XmlIntegerDerived)atom;
            if (integer.getNativeType().isA(NativeType.UNSIGNED_SHORT))
            {
                return integer.intValue();
            }
            else
            {
                throw new AssertionError(atom.getClass());
            }
        }
        else if (isForeignAtom(atom))
        {
            return getUnsignedShort(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getUnsignedShort(" + atom.getClass() + ")");
        }
    }

    @Override
    public URI getURI(XmlAtom atom)
    {
        if (atom instanceof XmlAnyURI)
        {
            return ((XmlAnyURI)atom).getURI();
        }
        else if (isForeignAtom(atom))
        {
            return getURI(getNativeAtom(atom));
        }
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getURI(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public String getXPath10Form(XmlAtom atom)
    {
        final XmlAtom nativeAtom = getNativeAtom(atom);
        if (nativeAtom instanceof XmlDouble)
        {
            return NumericSupport.formatDoubleXPath10(((XmlDouble)nativeAtom).getDoubleValue());
        }
        else if (nativeAtom instanceof XmlFloat)
        {
            return NumericSupport.formatFloatXPath10(((XmlFloat)nativeAtom).getFloatValue());
        }
        else if (nativeAtom instanceof XmlDecimal)
        {
            return NumericSupport.formatDecimalXPath10(((XmlDecimal)nativeAtom).getBigDecimalValue());
        }
        else
        {
            return nativeAtom.getC14NForm();
        }
    }

    @Override
    public String getXQuery10Form(XmlAtom atom)
    {
        final XmlAtom nativeAtom = getNativeAtom(atom);
        if (nativeAtom instanceof XmlDouble)
        {
            return NumericSupport.formatDoubleXQuery10(((XmlDouble)nativeAtom).getDoubleValue());
        }
        else if (nativeAtom instanceof XmlFloat)
        {
            return NumericSupport.formatFloatXQuery10(((XmlFloat)nativeAtom).getFloatValue());
        }
        else if (nativeAtom instanceof XmlDecimal)
        {
            return NumericSupport.formatDecimalXQuery10(((XmlDecimal)nativeAtom).getBigDecimalValue());
        }
        else
        {
            return nativeAtom.getC14NForm();
        }
    }

    @Override
    public int getYear(XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
        {
            return ((XmlGregorian)gregorian).getYear();
        }
        else if (isForeignAtom(gregorian))
        {
            return getYear(getNativeAtom(gregorian));
        }
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getYear(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public boolean isAtom(Object object)
    {
        return object instanceof XmlAtom;
    }

    @Override
    public boolean isForeignAtom(XmlAtom atom)
    {
        return (atom instanceof XmlForeignAtom);
    }

    @Override
    public boolean isWhiteSpace(XmlAtom atom)
    {
        return atom.isWhiteSpace();
    }

    @Override
    public XmlAtom makeForeignAtom(QName atomType, XmlAtom baseAtom)
    {
        return new XmlForeignAtom(atomType, baseAtom);
    }

    @Override
    public XmlAtom unwrapAtom(final Iterable<? extends XmlAtom> sequence)
        throws AtomCastException
    {
        Iterator<? extends XmlAtom> it = sequence.iterator();
        if (it.hasNext())
        {
            XmlAtom first = it.next();
            if (!it.hasNext())
                return first;
        }
        // TODO: throw an atom cast exception if we have zero or >1 atoms.
        //throw new AtomCastException();
        throw new RuntimeException("invalid unwrap");
    }

    @Override
    public List<XmlAtom> wrapAtom(XmlAtom atom)
    {
        if (atom != null)
            return atom;
        return Collections.emptyList();
    }

    private final ComponentProvider components;

    // the name is from the functions and operators spec
    private static final QName FORG0001 = new QName("http://www.w3.org/2005/xqt-errors/", "FORG0001", "err");
    private static final QName FORG0006 = new QName("http://www.w3.org/2005/xqt-errors/", "FORG0006", "err"); // invalid argument type
    private static final Iterable<XmlAtom> EMPTY_ATOM_SEQUENCE = new UnaryIterable<XmlAtom>(null);
}
