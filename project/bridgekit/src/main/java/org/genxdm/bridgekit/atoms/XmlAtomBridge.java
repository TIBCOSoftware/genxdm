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
package org.genxdm.bridgekit.atoms;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public final class XmlAtomBridge implements AtomBridge<XmlAtom>
{
    public XmlAtomBridge(final SchemaComponentCache schema)
    {
        this.schema = PreCondition.assertNotNull(schema, "schema");
        this.nameBridge = NameSource.SINGLETON;
    }

    public void setProcessingContext(final SchemaComponentCache schema)
    {
        this.schema = PreCondition.assertArgumentNotNull(schema, "schema");
    }

    @Override
    public XmlAtom atom(final Object object)
    {
        if (object instanceof XmlAtom)
            return (XmlAtom)object;
        return null;
    }

    @Override
    public XmlAtom[] atomArray(final int size)
    {
        return new XmlAtom[size];
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
    public XmlAtom upCast(final XmlAtom foreignAtom)
    {
        if (foreignAtom instanceof XmlForeignAtom)
            return ((XmlForeignAtom)foreignAtom).baseAtom;
        PreCondition.assertArgumentNotNull(foreignAtom, "foreignAtom");
        throw new AssertionError("baseAtomFromForeignAtom(" + foreignAtom.getClass() + ")");
    }

    @Override
    public XmlAtom castAs(final XmlAtom sourceAtom, final QName targetType, final CastingContext castingContext) throws AtomCastException
    {
        return CastingSupport.castAs(sourceAtom, targetType, castingContext, schema.getComponentProvider(), this);
    }

    @Override
    public XmlAtom castAs(final XmlAtom sourceAtom, final NativeType targetType, final CastingContext castingContext) throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(castingContext, "castingContext");
        return CastingSupport.castAs(getNativeAtom(sourceAtom), targetType, castingContext, schema.getComponentProvider(), this);
    }

    @Override
    public XmlAtom compile(final String sourceValue, final NativeType targetType) throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(sourceValue, "sourceValue");
        PreCondition.assertArgumentNotNull(targetType, "targetType");
        final Type type = schema.getComponentProvider().getTypeDefinition(targetType);
        if (type != null)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<XmlAtom> atoms = atomicType.validate(sourceValue, this);
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
                    throw new AtomCastException(sourceValue, e.getType(), FORG0001, e);
                }
            }
            else
                throw new IllegalArgumentException(targetType + " dataType is not an atomic type.");
        }
        throw new IllegalArgumentException(targetType + " dataType could not be found in the processing context.");
    }

    @Override
    public XmlAtom compile(final String sourceValue, final NativeType targetType, final PrefixResolver resolver) throws AtomCastException
    {
        // use base unless this has a resolver and is a QName
        if ( (resolver == null) || (targetType != NativeType.QNAME) )
            return compile(sourceValue, targetType);

        PreCondition.assertArgumentNotNull(sourceValue, "sourceValue");
        PreCondition.assertArgumentNotNull(targetType, "targetType");
        final Type type = schema.getComponentProvider().getTypeDefinition(targetType);
        if (type != null)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<XmlAtom> atoms = atomicType.validate(sourceValue, resolver, this);
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
                    throw new AtomCastException(sourceValue, e.getType(), FORG0001, e);
                }
            }
            else
                throw new IllegalArgumentException(targetType + " dataType is not an atomic type.");
        }
        throw new IllegalArgumentException(targetType + " dataType could not be found in the processing context.");
    }

    @Override
    public XmlBase64Binary createBase64Binary(final byte[] base64BinaryValue)
    {
        return new XmlBase64Binary(base64BinaryValue);
    }

    @Override
    public XmlBoolean createBoolean(final boolean value)
    {
        return XmlBoolean.valueOf(value);
    }

    @Override
    public XmlByte createByte(final byte byteValue)
    {
        return new XmlByte(byteValue);
    }

    @Override
    public XmlGregorian createDate(final int year, final int month, final int dayOfMonth, final int timezone)
    {
        return new XmlGregorian(year, month, dayOfMonth, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.DATE);
    }

    @Override
    public XmlGregorian createDateTime(final int year, final int month, final int dayOfMonth, final int hour, final int minute, final int second, final int millis, final BigDecimal remainderSecond, final int offsetInMinutes)
    {
        return new XmlGregorian(year, month, dayOfMonth, hour, minute, second, remainderSecond, offsetInMinutes, NativeType.DATETIME);
    }

    @Override
    public XmlGregorian createDay(final int dayOfMonth, final int timezone)
    {
        return new XmlGregorian(EPOCH_YEAR, EPOCH_MONTH, dayOfMonth, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GDAY);
    }

    @Override
    public XmlDayTimeDuration createDayTimeDuration(final BigDecimal seconds)
    {
        return new XmlDayTimeDuration(seconds);
    }

    @Override
    public XmlDecimal createDecimal(final BigDecimal decimalValue)
    {
        return XmlDecimal.valueOf(decimalValue);
    }

    @Override
    public XmlDecimal createDecimal(final long decimalValue)
    {
        return XmlDecimal.valueOf(decimalValue);
    }

    @Override
    public XmlDouble createDouble(final double value)
    {
        return new XmlDouble(value);
    }

    @Override
    public XmlDuration createDuration(final int yearMonthDuration, final BigDecimal dayTimeDuration)
    {
        return new XmlDuration(yearMonthDuration, dayTimeDuration);
    }

    @Override
    public XmlFloat createFloat(final float floatValue)
    {
        return new XmlFloat(floatValue);
    }

    @Override
    public XmlHexBinary createHexBinary(final byte[] hexBinaryValue)
    {
        return new XmlHexBinary(hexBinaryValue);
    }

    @Override
    public XmlInt createInt(final int intValue)
    {
        return new XmlInt(intValue);
    }

    @Override
    public XmlInteger createInteger(final BigInteger value)
    {
        return XmlInteger.valueOf(value);
    }

    @Override
    public XmlInteger createInteger(final long value)
    {
        return XmlInteger.valueOf(value);
    }

    @Override
    public XmlIntegerDerived createIntegerDerived(final BigInteger value, final NativeType nativeType)
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
    public XmlAtom createIntegerDerived(final long value, final NativeType nativeType)
    {
        switch (nativeType)
        {
            case INTEGER:
                return XmlInteger.valueOf(value);
            case NON_POSITIVE_INTEGER:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case NEGATIVE_INTEGER:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case NON_NEGATIVE_INTEGER:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case UNSIGNED_LONG:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case UNSIGNED_INT:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case UNSIGNED_SHORT:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case UNSIGNED_BYTE:
                return XmlIntegerDerived.valueOf(value, nativeType);
            case POSITIVE_INTEGER:
                return XmlIntegerDerived.valueOf(value, nativeType);
            default:
                throw new AssertionError(nativeType);
        }
    }

    @Override
    public XmlLong createLong(final long longValue)
    {
        return new XmlLong(longValue);
    }

    @Override
    public XmlGregorian createMonth(final int month, final int timezone)
    {
        return new XmlGregorian(EPOCH_YEAR, month, EPOCH_DAY, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GMONTH);
    }

    @Override
    public XmlGregorian createMonthDay(final int month, final int dayOfMonth, final int timezone)
    {
        return new XmlGregorian(EPOCH_YEAR, month, dayOfMonth, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GMONTHDAY);
    }

    @Override
    public XmlNOTATION createNOTATION(final String namespaceURI, final String localName, final String prefix)
    {
        return new XmlNOTATION(namespaceURI, localName, prefix);
    }

    @Override
    public XmlQName createQName(final String namespaceURI, final String localName, final String prefix)
    {
        PreCondition.assertArgumentNotNull(prefix, "prefix");
        return new XmlQName(namespaceURI, localName, prefix);
    }

    @Override
    public XmlShort createShort(final short shortValue)
    {
        return new XmlShort(shortValue);
    }

    @Override
    public XmlString createString(final String strval)
    {
        if (strval != null)
            return new XmlString(strval);
        return null;
    }

    @Override
    public XmlAtom createStringDerived(final String initialValue, final NativeType nativeType)
    {
        if (initialValue != null)
        {
            PreCondition.assertArgumentNotNull(nativeType, "nativeType");
            final String normalized = normalize(initialValue, nativeType);
            switch (nativeType)
            {
                case NORMALIZED_STRING:
                    return new XmlNormalizedString(normalized);
                case TOKEN:
                    return new XmlToken(normalized);
                case LANGUAGE:
                    return new XmlLanguage(normalized);
                case NAME:
                    return new XmlName(normalized);
                case NMTOKEN:
                    return new XmlNMTOKEN(normalized);
                case NCNAME:
                    return new XmlNCName(normalized);
                case ID:
                    return new XmlID(normalized);
                case IDREF:
                    return new XmlIDREF(normalized);
                case ENTITY:
                    return new XmlENTITY(normalized);
                default:
                    throw new AssertionError("createStringDerived('" + normalized + "', " + nativeType + ")");
            }
        }
        return null;
    }

    @Override
    public XmlGregorian createTime(final int hourOfDay, final int minute, final int second, final int millis, final BigDecimal fractionalSecond, final int timezone)
    {
        return new XmlGregorian(1970, 1, 1, hourOfDay, minute, second, fractionalSecond, timezone, NativeType.TIME);
    }

    @Override
    public XmlUntypedAtomic createUntypedAtomic(final String strval)
    {
        return new XmlUntypedAtomic(strval);
    }

    @Override
    public XmlAnyURI createURI(final URI uri)
    {
        if (uri != null)
            return new XmlAnyURI(uri);
        return null;
    }

    @Override
    public XmlGregorian createYear(final int year, final int timezone)
    {
        return new XmlGregorian(year, EPOCH_MONTH, EPOCH_DAY, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GYEAR);
    }

    @Override
    public XmlGregorian createYearMonth(final int year, final int month, final int timezone)
    {
        return new XmlGregorian(year, month, EPOCH_DAY, 0, 0, 0, BigDecimal.ZERO, timezone, NativeType.GYEARMONTH);
    }

    @Override
    public XmlYearMonthDuration createYearMonthDuration(final int months)
    {
        return XmlYearMonthDuration.valueOf(months);
    }
    
    @Override
    public Iterable<XmlAtom> emptySequence()
    {
        return EMPTY_ATOM_SEQUENCE;
    }

    @Override
    public byte[] getBase64Binary(final XmlAtom atom)
    {
        if (atom instanceof XmlBase64Binary)
            return ((XmlBase64Binary)atom).getByteArrayValue();
        else if (isForeignAtom(atom))
            return getBase64Binary(getNativeAtom(atom));
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlBase64Binary, "atom instance of xs:base64Binary");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public boolean getBoolean(final XmlAtom atom)
    {
        if (atom instanceof XmlBoolean)
            return ((XmlBoolean)atom).getBooleanValue();
        else if (isForeignAtom(atom))
            return getBoolean(getNativeAtom(atom));
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlBoolean, "atom instance of xs:boolean");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public XmlBoolean getBooleanFalse()
    {
        return XmlBoolean.FALSE;
    }

    @Override
    public XmlBoolean getBooleanTrue()
    {
        return XmlBoolean.TRUE;
    }

    @Override
    public byte getByte(final XmlAtom atom)
    {
        if (atom instanceof XmlByte)
            return ((XmlByte)atom).getByteValue();
        else if (isForeignAtom(atom))
            return getByte(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getByte(" + atom.getClass() + ")");
        }
    }

    @Override
    public String getC14NForm(final XmlAtom atom)
    {
        return atom.getC14NForm();
    }

    @Override
    public String getC14NString(final List<? extends XmlAtom> atoms)
    {
        final int size = (atoms == null) ? 0 : atoms.size();
        if (size > 0)
        {
            if (size == 1)
                return getC14NForm(atoms.get(0));
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
            throw new IllegalArgumentException("atoms.size() must be greater than or equal to zero.");
        return "";
    }

    @Override
    public QName getDataType(final XmlAtom atom)
    {
        return nameBridge.nativeType(atom.getNativeType());
    }

    @Override
    public int getDayOfMonth(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getDayOfMonth();
        else if (isForeignAtom(gregorian))
            return getDayOfMonth(getNativeAtom(gregorian));
        else
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getDayOfMonth(" + gregorian.getClass().getName() + ")");
    }

    @Override
    public BigDecimal getDecimal(final XmlAtom atom)
    {
        if (atom instanceof XmlDecimal)
            return ((XmlDecimal)atom).getBigDecimalValue();
        else if (atom instanceof XmlInteger)
            return new BigDecimal(((XmlInteger)atom).getBigIntegerValue());
        else if (atom instanceof XmlLong)
            return BigDecimal.valueOf(((XmlLong)atom).getLongValue());
        else if (atom instanceof XmlInt)
            return BigDecimal.valueOf(((XmlInt)atom).getIntValue());
        else if (atom instanceof XmlShort)
            return BigDecimal.valueOf(((XmlShort)atom).getShortValue());
        else if (atom instanceof XmlByte)
            return BigDecimal.valueOf(((XmlByte)atom).getByteValue());
        else if (atom instanceof XmlIntegerDerived)
            return new BigDecimal(((XmlIntegerDerived)atom).integerValue());
        else if (isForeignAtom(atom))
            return getDecimal(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getDecimal(" + atom.getClass() + ")");
        }
    }

    @Override
    public double getDouble(final XmlAtom atom)
    {
        if (atom instanceof XmlDouble)
            return ((XmlDouble)atom).getDoubleValue();
        else if (isForeignAtom(atom))
            return getDouble(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("TODO: getDouble(" + atom.getClass() + ")");
        }
    }

    @Override
    public int getDurationTotalMonths(final XmlAtom duration)
    {
        if (duration instanceof XmlYearMonthDuration)
            return ((XmlYearMonthDuration)duration).getTotalMonthsValue();
        else if (duration instanceof XmlDuration)
            return ((XmlDuration)duration).getTotalMonthsValue();
        else if (isForeignAtom(duration))
            return getDurationTotalMonths(getNativeAtom(duration));
        else
        {
            PreCondition.assertNotNull(duration, "duration");
            PreCondition.assertTrue(duration instanceof XmlYearMonthDuration, "atom instance of xs:yearMonthDuration");
            throw new AssertionError(duration.getClass());
        }
    }

    @Override
    public BigDecimal getDurationTotalSeconds(final XmlAtom duration)
    {
        if (duration instanceof XmlDayTimeDuration)
            return ((XmlDayTimeDuration)duration).getTotalSecondsValue();
        else if (duration instanceof XmlDuration)
            return ((XmlDuration)duration).getTotalSecondsValue();
        else if (isForeignAtom(duration))
            return getDurationTotalSeconds(getNativeAtom(duration));
        else
        {
            PreCondition.assertNotNull(duration, "duration");
            PreCondition.assertTrue(false, "atom instance of xs:duration");
            throw new AssertionError(duration.getClass());
        }
    }

    @Override
    public float getFloat(final XmlAtom atom)
    {
        if (atom instanceof XmlFloat)
            return ((XmlFloat)atom).getFloatValue();
        else if (isForeignAtom(atom))
            return getFloat(getNativeAtom(atom));
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlFloat, "atom instance of xs:float");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public BigDecimal getFractionalSecondPart(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getFractionalSecond();
        else if (isForeignAtom(gregorian))
            return getFractionalSecondPart(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getFractionalSecond(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public int getGmtOffset(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getGmtOffset();
        else if (isForeignAtom(gregorian))
            return getGmtOffset(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getGmtOffset(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public byte[] getHexBinary(final XmlAtom atom)
    {
        if (atom instanceof XmlHexBinary)
            return ((XmlHexBinary)atom).getByteArrayValue();
        else if (isForeignAtom(atom))
            return getHexBinary(getNativeAtom(atom));
        else
        {
            PreCondition.assertNotNull(atom, "atom");
            PreCondition.assertTrue(atom instanceof XmlHexBinary, "atom instance of xs:hexBinary");
            throw new AssertionError(atom.getClass());
        }
    }

    @Override
    public int getHourOfDay(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getHourOfDay();
        else if (isForeignAtom(gregorian))
            return getHourOfDay(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getHourOfDay(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public int getInt(final XmlAtom atom)
    {
        if (atom instanceof XmlInt)
            return ((XmlInt)atom).getIntValue();
        else if (atom instanceof XmlShort)
            return ((XmlShort)atom).getShortValue();
        else if (atom instanceof XmlByte)
            return ((XmlByte)atom).getByteValue();
        else if (isForeignAtom(atom))
            return getInt(upCast(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getInt(" + atom.getClass() + ")");
        }
    }

    @Override
    public BigInteger getInteger(final XmlAtom atom)
    {
        if (atom instanceof XmlInteger)
            return ((XmlInteger)atom).getBigIntegerValue();
        else if (atom instanceof XmlLong)
            return BigInteger.valueOf(((XmlLong)atom).getLongValue());
        else if (atom instanceof XmlInt)
            return BigInteger.valueOf(((XmlInt)atom).getIntValue());
        else if (atom instanceof XmlShort)
            return BigInteger.valueOf(((XmlShort)atom).getShortValue());
        else if (atom instanceof XmlByte)
            return BigInteger.valueOf(((XmlByte)atom).getByteValue());
        else if (atom instanceof XmlIntegerDerived)
            return ((XmlIntegerDerived)atom).integerValue();
        else if (atom instanceof XmlForeignAtom)
            return getInteger(((XmlForeignAtom)atom).baseAtom);
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getInteger(" + atom.getClass() + ")");
        }
    }

    @Override
    public int getIntegralSecondPart(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getSecond();
        else if (isForeignAtom(gregorian))
            return getIntegralSecondPart(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getSecond(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public String getLocalNameFromQName(final XmlAtom atom)
    {
        if (atom instanceof XmlQName)
            return ((XmlQName)atom).getLocalName();
        else if (isForeignAtom(atom))
            return getLocalNameFromQName(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getLocalNameFromQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public long getLong(final XmlAtom atom)
    {
        if (atom instanceof XmlLong)
            return ((XmlLong)atom).getLongValue();
        else if (atom instanceof XmlInt)
            return ((XmlInt)atom).getIntValue();
        else if (atom instanceof XmlShort)
            return ((XmlShort)atom).getShortValue();
        else if (atom instanceof XmlByte)
            return ((XmlByte)atom).getByteValue();
        else if (isForeignAtom(atom))
            return getLong(upCast(atom));
        else
        {
            if (atom != null)
                throw new AssertionError("getLong(" + atom.getClass().getName() + ")");
            else
                // Consistent with Unboxing.
                throw new NullPointerException();
        }
    }

    @Override
    public int getMinute(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getMinute();
        else if (isForeignAtom(gregorian))
            return getMinute(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getMinute(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public int getMonth(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getMonth();
        else if (isForeignAtom(gregorian))
            return getMonth(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getMonth(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public String getNamespaceFromQName(final XmlAtom atom)
    {
        if (atom instanceof XmlQName)
            return ((XmlQName)atom).getNamespaceURI();
        else if (isForeignAtom(atom))
            return getNamespaceFromQName(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getNamespaceFromQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public XmlAtom getNativeAtom(final XmlAtom atom)
    {
        if (isForeignAtom(atom))
            return getNativeAtom(upCast(atom));
        return atom;
    }

    @Override
    public NativeType getNativeType(final XmlAtom atom)
    {
        return atom.getNativeType();
    }

    @Override
    public QName getNotation(final XmlAtom atom)
    {
        if (atom instanceof XmlNOTATION)
        {
            final XmlNOTATION name = (XmlNOTATION)atom;
            return new QName(name.getNamespaceURI().toString(), name.getLocalName().toString(), name.getPrefix());
        }
        else if (isForeignAtom(atom))
            return getNotation(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getNotation(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public String getPrefixFromQName(final XmlAtom atom)
    {
        if (atom instanceof XmlQName)
            return ((XmlQName)atom).getPrefix();
        else if (isForeignAtom(atom))
            return getPrefixFromQName(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getPrefixFromQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public QName getQName(final XmlAtom atom)
    {
        if (atom instanceof XmlQName)
        {
            final XmlQName qname = (XmlQName)atom;
            return new QName(qname.getNamespaceURI().toString(), qname.getLocalName().toString(), qname.getPrefix());
        }
        else if (isForeignAtom(atom))
            return getQName(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getQName(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public BigDecimal getSecondsAsBigDecimal(final XmlAtom gregorian)
    {
        final BigDecimal isp = BigDecimal.valueOf(getIntegralSecondPart(gregorian));
        final BigDecimal fsp = getFractionalSecondPart(gregorian);
        if (fsp != null)
            return isp.add(fsp);
        return isp;
    }

    @Override
    public short getShort(final XmlAtom atom)
    {
        if (atom instanceof XmlShort)
            return ((XmlShort)atom).getShortValue();
        else if (atom instanceof XmlByte)
            return ((XmlByte)atom).getByteValue();
        else if (isForeignAtom(atom))
            return getShort(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getShort(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public String getString(final XmlAtom atom)
    {
        if (atom instanceof XmlString)
            return atom.getC14NForm();
        else if (atom instanceof XmlNormalizedString)
            return atom.getC14NForm();
        else if (atom instanceof XmlToken)
            return atom.getC14NForm();
        else if (atom instanceof XmlLanguage)
            return atom.getC14NForm();
        else if (atom instanceof XmlName)
            return atom.getC14NForm();
        else if (atom instanceof XmlNMTOKEN)
            return atom.getC14NForm();
        else if (atom instanceof XmlNCName)
            return atom.getC14NForm();
        else if (atom instanceof XmlID)
            return atom.getC14NForm();
        else if (atom instanceof XmlIDREF)
            return atom.getC14NForm();
        else if (atom instanceof XmlENTITY)
            return atom.getC14NForm();
        else if (isForeignAtom(atom))
            return getString(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getString(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public short getUnsignedByte(final XmlAtom atom)
    {
        if (atom instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived integer = (XmlIntegerDerived)atom;
            if (integer.getNativeType() == NativeType.UNSIGNED_BYTE)
                return integer.shortValue();
            throw new AssertionError(atom.getClass());
        }
        else if (isForeignAtom(atom))
            return getUnsignedByte(getNativeAtom(atom));
        throw new AssertionError(atom.getClass());
    }

    @Override
    public long getUnsignedInt(final XmlAtom atom)
    {
        if (atom instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived integer = (XmlIntegerDerived)atom;
            if (integer.getNativeType().isA(NativeType.UNSIGNED_INT))
                return integer.longValue();
            throw new AssertionError(atom.getClass());
        }
        else if (isForeignAtom(atom))
            return getUnsignedInt(getNativeAtom(atom));
        throw new AssertionError(atom.getClass());
    }

    @Override
    public int getUnsignedShort(final XmlAtom atom)
    {
        if (atom instanceof XmlIntegerDerived)
        {
            final XmlIntegerDerived integer = (XmlIntegerDerived)atom;
            if (integer.getNativeType().isA(NativeType.UNSIGNED_SHORT))
                return integer.intValue();
            throw new AssertionError(atom.getClass());
        }
        else if (isForeignAtom(atom))
            return getUnsignedShort(getNativeAtom(atom));
        throw new AssertionError(atom.getClass());
    }

    @Override
    public URI getURI(final XmlAtom atom)
    {
        if (atom instanceof XmlAnyURI)
            return ((XmlAnyURI)atom).getURI();
        else if (isForeignAtom(atom))
            return getURI(getNativeAtom(atom));
        else
        {
            PreCondition.assertArgumentNotNull(atom, "atom");
            throw new AssertionError("getURI(" + atom.getClass().getName() + ")");
        }
    }

    @Override
    public String getXPath10Form(final XmlAtom atom)
    {
        final XmlAtom nativeAtom = getNativeAtom(atom);
        if (nativeAtom instanceof XmlDouble)
            return NumericSupport.formatDoubleXPath10(((XmlDouble)nativeAtom).getDoubleValue());
        else if (nativeAtom instanceof XmlFloat)
            return NumericSupport.formatFloatXPath10(((XmlFloat)nativeAtom).getFloatValue());
        else if (nativeAtom instanceof XmlDecimal)
            return NumericSupport.formatDecimalXPath10(((XmlDecimal)nativeAtom).getBigDecimalValue());
        return nativeAtom.getC14NForm();
    }

    @Override
    public String getXQuery10Form(final XmlAtom atom)
    {
        final XmlAtom nativeAtom = getNativeAtom(atom);
        if (nativeAtom instanceof XmlDouble)
            return NumericSupport.formatDoubleXQuery10(((XmlDouble)nativeAtom).getDoubleValue());
        else if (nativeAtom instanceof XmlFloat)
            return NumericSupport.formatFloatXQuery10(((XmlFloat)nativeAtom).getFloatValue());
        else if (nativeAtom instanceof XmlDecimal)
            return NumericSupport.formatDecimalXQuery10(((XmlDecimal)nativeAtom).getBigDecimalValue());
        return nativeAtom.getC14NForm();
    }

    @Override
    public int getYear(final XmlAtom gregorian)
    {
        if (gregorian instanceof XmlGregorian)
            return ((XmlGregorian)gregorian).getYear();
        else if (isForeignAtom(gregorian))
            return getYear(getNativeAtom(gregorian));
        else
        {
            PreCondition.assertArgumentNotNull(gregorian, "gregorian");
            throw new AssertionError("getYear(" + gregorian.getClass().getName() + ")");
        }
    }

    @Override
    public boolean isAtom(final Object object)
    {
        return object instanceof XmlAtom;
    }

    @Override
    public boolean isForeignAtom(final XmlAtom atom)
    {
        return (atom instanceof XmlForeignAtom);
    }

    @Override
    public boolean isWhiteSpace(final XmlAtom atom)
    {
        return atom.isWhiteSpace();
    }

    @Override
    public XmlAtom makeForeignAtom(final QName atomType, final XmlAtom baseAtom)
    {
        return new XmlForeignAtom(atomType, baseAtom);
    }

    @Override
    public List<XmlAtom> wrapAtom(final XmlAtom atom)
    {
        if (atom != null)
            return atom;
        return Collections.emptyList();
    }

    private String normalize(final String initialValue, final NativeType nativeType)
    {
        final SimpleType simpleType = (SimpleType)schema.getComponentProvider().getTypeDefinition(nativeType);
        return simpleType.normalize(initialValue);
    }

    private static final int EPOCH_DAY = 1; // 1st
    private static final int EPOCH_MONTH = 1; // January
    private static final int EPOCH_YEAR = 1970;
    private static final QName FORG0001 = new QName("http://www.w3.org/2005/xqt-errors/", "FORG0001", "err");
    private static final Iterable<XmlAtom> EMPTY_ATOM_SEQUENCE = new UnaryIterable<XmlAtom>(null);

    private final NameSource nameBridge;
    private SchemaComponentCache schema;
}
