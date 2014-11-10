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
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.SpillagePolicy;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public final class CastingSupport
{
    private static final BigInteger BYTE_MAX_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);
    private static final BigInteger BYTE_MIN_VALUE = BigInteger.valueOf(Byte.MIN_VALUE);
    private static final BigInteger DOUBLE_MAX_VALUE = new BigInteger(new BigDecimal(Double.MAX_VALUE).toPlainString());
    private static final BigDecimal DOUBLE_MAX_VALUE_AS_DECIMAL = new BigDecimal(Double.toString(Double.MAX_VALUE));
    private static final BigInteger DOUBLE_MIN_VALUE = new BigInteger(new BigDecimal(Double.MAX_VALUE).negate().toPlainString());
    private static final BigDecimal DOUBLE_MIN_VALUE_AS_DECIMAL = new BigDecimal(Double.toString(Double.MIN_VALUE));
    private static final BigInteger FLOAT_MAX_VALUE = new BigInteger(new BigDecimal(Float.MAX_VALUE).toPlainString());
    private static final BigDecimal FLOAT_MAX_VALUE_AS_DECIMAL = new BigDecimal(Float.toString(Float.MAX_VALUE));
    private static final BigInteger FLOAT_MIN_VALUE = new BigInteger(new BigDecimal(Float.MAX_VALUE).negate().toPlainString());
    private static final BigDecimal FLOAT_MIN_VALUE_AS_DECIMAL = new BigDecimal(Float.toString(Float.MIN_VALUE));
    private static final QName FOAR0002 = new QName("http://www.w3.org/2005/xqt-errors/", "FOAR0002", "err");

    /**
     * Invalid lexical value.
     */
    private static final QName FOCA0002 = new QName("http://www.w3.org/2005/xqt-errors/", "FOCA0002", "err");
    /**
     * Input value too large for integer.
     */
    private static final QName FOCA0003 = new QName("http://www.w3.org/2005/xqt-errors/", "FOCA0003", "err");
    /**
     * Cast fails for other reasons.
     */
    private static final QName FORG0001 = new QName("http://www.w3.org/2005/xqt-errors/", "FORG0001", "err");
    private static final BigInteger INT_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger INT_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

    private static final BigInteger SHORT_MAX_VALUE = BigInteger.valueOf(Short.MAX_VALUE);
    private static final BigInteger SHORT_MIN_VALUE = BigInteger.valueOf(Short.MIN_VALUE);

    private static final BigInteger UNSIGNED_BYTE_MAX_VALUE = BigInteger.valueOf(((short)Byte.MAX_VALUE - (short)Byte.MIN_VALUE));
    private static final long UNSIGNED_INT_MAX_VALUE_AS_LONG = (long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE;
    private static final BigInteger UNSIGNED_INT_MAX_VALUE = BigInteger.valueOf(4294967295L);
    private static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger("18446744073709551615");
    private static final BigInteger UNSIGNED_SHORT_MAX_VALUE = BigInteger.valueOf(65535);
    private static final QName XPTY0004 = new QName("http://www.w3.org/2005/xqt-errors/", "XPTY0004", "err");

    private static void assertDoubleIsNumber(final double dblval, final NativeType target) throws AtomCastException
    {
        if (Double.isNaN(dblval))
        {
            throw new AtomCastException(NumericSupport.formatDoubleC14N(dblval), target.toQName(), FOCA0002);
        }
    }

    private static void assertDoubleNotInfinite(final double dblval, final NativeType target) throws AtomCastException
    {
        if (Double.isInfinite(dblval))
        {
            throw new AtomCastException(NumericSupport.formatDoubleC14N(dblval), target.toQName(), FOCA0002);
        }
    }

    private static void assertFloatIsNumber(final float dblval, final NativeType target) throws AtomCastException
    {
        if (Float.isNaN(dblval))
        {
            throw new AtomCastException(NumericSupport.formatFloatC14N(dblval), target.toQName(), FOCA0002);
        }
    }

    private static void assertFloatNotInfinite(final float fltval, final NativeType target) throws AtomCastException
    {
        if (Float.isInfinite(fltval))
        {
            throw new AtomCastException(NumericSupport.formatFloatC14N(fltval), target.toQName(), FOCA0002);
        }
    }

    public static <A> A castAs(final A sourceAtom, final QName targetType, final CastingContext castingContext, final ComponentProvider pcx, final AtomBridge<A> atomBridge) throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(sourceAtom, "sourceAtom");
        PreCondition.assertArgumentNotNull(targetType, "targetType");
        PreCondition.assertArgumentNotNull(castingContext, "castingContext");
        PreCondition.assertArgumentNotNull(pcx, "pcx");
        final NameSource nameBridge = NameSource.SINGLETON;
        final NativeType nativeType = nameBridge.nativeType(targetType);
        if (null != nativeType)
        {
            return castAs(sourceAtom, nativeType, castingContext, pcx, atomBridge);
        }
        else
        {
            PreCondition.assertTrue(false, targetType.toString());
        }

        final Type type = pcx.getTypeDefinition(targetType);
        if (null != type)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<A> atoms = atomicType.validate(atomBridge.wrapAtom(sourceAtom), atomBridge);
                    final int size = atoms.size();
                    if (1 == size)
                    {
                        return atoms.get(0);
                    }
                    else if (0 == size)
                    {
                        return null;
                    }
                    else
                    {
                        // Atomic type should not be yielding multiple atoms.
                        throw new AssertionError();
                    }
                }
                catch (final DatatypeException e)
                {
                    throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), e.getType(), FORG0001, e);
                }
            }
            else
            {
                throw new IllegalArgumentException(targetType + " dataType is not an atomic type.");
            }
        }
        else
        {
            throw new IllegalArgumentException(targetType + " dataType could not be found in the processing context.");
        }
    }

    public static <A> A castAsOrErrors(final A sourceAtom, final NativeType sourceType, final NativeType targetType, final ComponentProvider pcx, final AtomBridge<A> atomBridge, final NameSource nameBridge)
            throws AtomCastException
    {
        if (sourceType.isString() || sourceType == NativeType.UNTYPED_ATOMIC)
        {
            return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
        }
        else
        {
            throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
        }

    }

    public static <A> A castAs(final A sourceAtom, final NativeType targetType, final CastingContext castingContext, final ComponentProvider pcx, final AtomBridge<A> atomBridge) throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(sourceAtom, "sourceAtom");
        PreCondition.assertArgumentNotNull(targetType, "targetType");
        PreCondition.assertArgumentNotNull(castingContext, "castingContext");
        PreCondition.assertArgumentNotNull(pcx, "pcx");
        final NameSource nameBridge = NameSource.SINGLETON;
        final NativeType sourceType = atomBridge.getNativeType(sourceAtom);

        final SpillagePolicy spillagePolicy = castingContext.getSpillagePolicy();
        final boolean checkCapacity = spillagePolicy.checkCapacity();
        final boolean raiseError = spillagePolicy.raiseError();

        switch (targetType)
        {
            case BOOLEAN:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    case STRING:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case FLOAT:
                    {
                        final float floatValue = atomBridge.getFloat(sourceAtom);
                        return atomBridge.createBoolean((0 != floatValue && !Float.isNaN(floatValue)));
                    }
                    case DOUBLE:
                    {
                        // TODO; NaN() would be a useful optimization for numerics?
                        final double doubleValue = atomBridge.getDouble(sourceAtom);
                        return atomBridge.createBoolean((0 != doubleValue && !Double.isNaN(doubleValue)));
                    }
                    case DECIMAL:
                    {
                        final BigDecimal decimalValue = atomBridge.getDecimal(sourceAtom);
                        return atomBridge.createBoolean(decimalValue.signum() != 0);
                    }
                    case INTEGER:
                    {
                        // TODO; signum() would be a useful optimization for numerics?
                        final BigInteger integerValue = atomBridge.getInteger(sourceAtom);
                        return atomBridge.createBoolean(integerValue.signum() != 0);
                    }
                    case NON_POSITIVE_INTEGER:
                    case NEGATIVE_INTEGER:
                    {
                        // TODO; signum() would be a useful optimization for numerics?
                        final BigInteger integerValue = atomBridge.getInteger(sourceAtom);
                        return atomBridge.createBoolean(integerValue.signum() != 0);
                    }
                    case LONG:
                    {
                        return atomBridge.createBoolean(atomBridge.getLong(sourceAtom) != 0);
                    }
                    case INT:
                    {
                        return atomBridge.createBoolean(atomBridge.getInt(sourceAtom) != 0);
                    }
                    case SHORT:
                    {
                        return atomBridge.createBoolean(atomBridge.getShort(sourceAtom) != 0);
                    }
                    case BYTE:
                    {
                        return atomBridge.createBoolean(atomBridge.getByte(sourceAtom) != 0);
                    }
                    case NON_NEGATIVE_INTEGER:
                    {
                        // TODO; signum() would be a useful optimization for numerics?
                        final BigInteger integerValue = atomBridge.getInteger(sourceAtom);
                        return atomBridge.createBoolean(integerValue.signum() != 0);
                    }
                    case UNSIGNED_LONG:
                    {
                        // TODO; signum() would be a useful optimization for numerics?
                        final BigInteger integerValue = atomBridge.getInteger(sourceAtom);
                        return atomBridge.createBoolean(integerValue.signum() != 0);
                    }
                    case UNSIGNED_INT:
                    {
                        return atomBridge.createBoolean(atomBridge.getUnsignedInt(sourceAtom) != 0);
                    }
                    case UNSIGNED_SHORT:
                    {
                        return atomBridge.createBoolean(atomBridge.getUnsignedShort(sourceAtom) != 0);
                    }
                    case UNSIGNED_BYTE:
                    {
                        return atomBridge.createBoolean(atomBridge.getUnsignedByte(sourceAtom) != 0);
                    }
                    case POSITIVE_INTEGER:
                    {
                        // TODO; signum() would be a useful optimization for numerics?
                        final BigInteger integerValue = atomBridge.getInteger(sourceAtom);
                        return atomBridge.createBoolean(integerValue.signum() != 0);
                    }
                    case BOOLEAN:
                    {
                        return sourceAtom;
                    }
                    case DURATION:
                    case DURATION_YEARMONTH:
                    case DURATION_DAYTIME:
                    case DATETIME:
                    case TIME:
                    case DATE:
                    case GYEARMONTH:
                    case GYEAR:
                    case GMONTHDAY:
                    case GDAY:
                    case GMONTH:
                    case BASE64_BINARY:
                    case HEX_BINARY:
                    case ANY_URI:
                    case QNAME:
                    case NOTATION:
                    {
                        throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
                    }
                    default:
                    {
                        // PreCondition.assertTrue(false, targetType.toString());
                        throw new AssertionError(sourceType + " => " + targetType);
                    }
                }
            }
            case DOUBLE:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return sourceAtom;
                    }
                    case FLOAT:
                    {
                        return atomBridge.createDouble(atomBridge.getFloat(sourceAtom));
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createDouble(castDecimalAsDouble(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError));
                    }
                    case INTEGER:
                    {
                        return atomBridge.createDouble(castIntegerAsDouble(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case LONG:
                    {
                        return atomBridge.createDouble(atomBridge.getLong(sourceAtom));
                    }
                    case INT:
                    {
                        return atomBridge.createDouble(atomBridge.getInt(sourceAtom));
                    }
                    case SHORT:
                    {
                        return atomBridge.createDouble(atomBridge.getShort(sourceAtom));
                    }
                    case BYTE:
                    {
                        return atomBridge.createDouble(atomBridge.getByte(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case FLOAT:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createFloat(castDoubleAsFloat(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError));
                    }
                    case FLOAT:
                    {
                        return sourceAtom;
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createFloat(castDecimalAsFloat(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError));
                    }
                    case INTEGER:
                    {
                        return atomBridge.createFloat(castIntegerAsFloat(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case LONG:
                    {
                        return atomBridge.createFloat(atomBridge.getLong(sourceAtom));
                    }
                    case INT:
                    {
                        return atomBridge.createFloat(atomBridge.getInt(sourceAtom));
                    }
                    case SHORT:
                    {
                        return atomBridge.createFloat(atomBridge.getShort(sourceAtom));
                    }
                    case BYTE:
                    {
                        return atomBridge.createFloat(atomBridge.getByte(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case DECIMAL:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createDecimal(castDoubleAsDecimal(atomBridge.getDouble(sourceAtom)));
                    }
                    case FLOAT:
                    {
                        return atomBridge.createDecimal(castFloatAsDecimal(atomBridge.getFloat(sourceAtom)));
                    }
                    case DECIMAL:
                    {
                        return sourceAtom;
                    }
                    case INTEGER:
                    {
                        return atomBridge.createDecimal(new BigDecimal(atomBridge.getInteger(sourceAtom)));
                    }
                    case LONG:
                    {
                        return atomBridge.createDecimal(atomBridge.getLong(sourceAtom));
                    }
                    case INT:
                    {
                        return atomBridge.createDecimal(atomBridge.getInt(sourceAtom));
                    }
                    case SHORT:
                    {
                        return atomBridge.createDecimal(atomBridge.getShort(sourceAtom));
                    }
                    case BYTE:
                    {
                        return atomBridge.createDecimal(atomBridge.getByte(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case INTEGER:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createInteger(castDoubleAsInteger(atomBridge.getDouble(sourceAtom)));
                    }
                    case FLOAT:
                    {
                        return atomBridge.createInteger(castFloatAsInteger(atomBridge.getFloat(sourceAtom)));
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createInteger(castDecimalAsInteger(atomBridge.getDecimal(sourceAtom)));
                    }
                    case INTEGER:
                    {
                        return sourceAtom;
                    }
                    case LONG:
                    {
                        return atomBridge.createInteger(atomBridge.getLong(sourceAtom));
                    }
                    case INT:
                    {
                        return atomBridge.createInteger(atomBridge.getInt(sourceAtom));
                    }
                    case SHORT:
                    {
                        return atomBridge.createInteger(atomBridge.getShort(sourceAtom));
                    }
                    case BYTE:
                    {
                        return atomBridge.createInteger(atomBridge.getByte(sourceAtom));
                    }
                    case NON_POSITIVE_INTEGER:
                    {
                        return atomBridge.createInteger(atomBridge.getInteger(sourceAtom));
                    }
                    case NEGATIVE_INTEGER:
                    {
                        return atomBridge.createInteger(atomBridge.getInteger(sourceAtom));
                    }
                    case NON_NEGATIVE_INTEGER:
                    {
                        return atomBridge.createInteger(atomBridge.getInteger(sourceAtom));
                    }
                    case UNSIGNED_LONG:
                    {
                        return atomBridge.createInteger(atomBridge.getInteger(sourceAtom));
                    }
                    case UNSIGNED_SHORT:
                    {
                        return atomBridge.createInteger(atomBridge.getUnsignedShort(sourceAtom));
                    }
                    case POSITIVE_INTEGER:
                    {
                        return atomBridge.createInteger(atomBridge.getInteger(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case LONG:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createLong(castDoubleAsLong(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError));
                    }
                    case FLOAT:
                    {
                        return atomBridge.createLong(castFloatAsLong(atomBridge.getFloat(sourceAtom), checkCapacity, raiseError));
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createLong(castDecimalAsLong(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError));
                    }
                    case INTEGER:
                    case NON_POSITIVE_INTEGER:
                    case NEGATIVE_INTEGER:
                    {
                        return atomBridge.createLong(castIntegerAsLong(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case LONG:
                    {
                        return sourceAtom;
                    }
                    case INT:
                    {
                        return atomBridge.createLong(atomBridge.getInt(sourceAtom));
                    }
                    case SHORT:
                    {
                        return atomBridge.createLong(atomBridge.getShort(sourceAtom));
                    }
                    case BYTE:
                    {
                        return atomBridge.createLong(atomBridge.getShort(sourceAtom));
                    }
                    case NON_NEGATIVE_INTEGER:
                    case POSITIVE_INTEGER:
                    case UNSIGNED_LONG:
                    {
                        return atomBridge.createLong(castIntegerAsLong(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case UNSIGNED_INT:
                    {
                        return atomBridge.createLong(atomBridge.getUnsignedInt(sourceAtom));
                    }
                    case UNSIGNED_SHORT:
                    {
                        return atomBridge.createLong(atomBridge.getUnsignedShort(sourceAtom));
                    }
                    case UNSIGNED_BYTE:
                    {
                        return atomBridge.createLong(atomBridge.getUnsignedByte(sourceAtom));
                    }
                    case BOOLEAN:
                    {
                        return atomBridge.createLong(atomBridge.getBoolean(sourceAtom) ? 1 : 0);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case INT:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createInt(castDoubleAsInt(atomBridge.getDouble(sourceAtom)));
                    }
                    case FLOAT:
                    {
                        return atomBridge.createInt(castFloatAsInt(atomBridge.getFloat(sourceAtom)));
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createInt(castDecimalAsInt(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError));
                    }
                    case INTEGER:
                    {
                        return atomBridge.createInt(castIntegerAsInt(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case LONG:
                    {
                        return atomBridge.createInt(castLongAsInt(atomBridge.getLong(sourceAtom), checkCapacity, raiseError));
                    }
                    case INT:
                    {
                        return sourceAtom;
                    }
                    case SHORT:
                    {
                        return atomBridge.createInt(atomBridge.getShort(sourceAtom));
                    }
                    case BYTE:
                    {
                        return atomBridge.createInt(atomBridge.getByte(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case SHORT:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createShort(castDoubleAsShort(atomBridge.getDouble(sourceAtom)));
                    }
                    case FLOAT:
                    {
                        return atomBridge.createShort(castFloatAsShort(atomBridge.getFloat(sourceAtom)));
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createShort(castDecimalAsShort(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError));
                    }
                    case INTEGER:
                    {
                        return atomBridge.createShort(castIntegerAsShort(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case LONG:
                    {
                        return atomBridge.createShort(castLongAsShort(atomBridge.getLong(sourceAtom), checkCapacity, raiseError));
                    }
                    case INT:
                    {
                        return atomBridge.createShort(castIntAsShort(atomBridge.getInt(sourceAtom), checkCapacity, raiseError));
                    }
                    case SHORT:
                    {
                        return sourceAtom;
                    }
                    case BYTE:
                    {
                        return atomBridge.createShort(atomBridge.getByte(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case BYTE:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    case STRING:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    {
                        return atomBridge.createByte(castDoubleAsByte(atomBridge.getDouble(sourceAtom)));
                    }
                    case FLOAT:
                    {
                        return atomBridge.createByte(castFloatAsByte(atomBridge.getFloat(sourceAtom)));
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createByte(castDecimalAsByte(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError));
                    }
                    case INTEGER:
                    {
                        return atomBridge.createByte(castIntegerAsByte(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError));
                    }
                    case LONG:
                    {
                        return atomBridge.createByte(castLongAsByte(atomBridge.getLong(sourceAtom), checkCapacity, raiseError));
                    }
                    case INT:
                    {
                        return atomBridge.createByte(castIntAsByte(atomBridge.getInt(sourceAtom), checkCapacity, raiseError));
                    }
                    case SHORT:
                    {
                        return atomBridge.createByte(castShortAsByte(atomBridge.getShort(sourceAtom), checkCapacity, raiseError));
                    }
                    case BYTE:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case STRING:
            {
                switch (sourceType)
                {
                    case STRING:
                    {
                        return sourceAtom;
                    }
                    case DOUBLE:
                    {
                        switch (castingContext.getEmulation())
                        {
                            case C14N:
                            {
                                return atomBridge.createString(atomBridge.getC14NForm(sourceAtom));
                            }
                            case MODERN:
                            {
                                return atomBridge.createString(NumericSupport.formatDoubleXQuery10(atomBridge.getDouble(sourceAtom)));
                            }
                            case LEGACY:
                            {
                                return atomBridge.createString(NumericSupport.formatDoubleXPath10(atomBridge.getDouble(sourceAtom)));
                            }
                            default:
                            {
                                throw new AssertionError(castingContext.getEmulation());
                            }
                        }
                    }
                    case FLOAT:
                    {
                        switch (castingContext.getEmulation())
                        {
                            case C14N:
                            {
                                return atomBridge.createString(atomBridge.getC14NForm(sourceAtom));
                            }
                            case MODERN:
                            {
                                return atomBridge.createString(NumericSupport.formatFloatXQuery10(atomBridge.getFloat(sourceAtom)));
                            }
                            case LEGACY:
                            {
                                return atomBridge.createString(NumericSupport.formatFloatXPath10(atomBridge.getFloat(sourceAtom)));
                            }
                            default:
                            {
                                throw new AssertionError(castingContext.getEmulation());
                            }
                        }
                    }
                    case DECIMAL:
                    {
                        switch (castingContext.getEmulation())
                        {
                            case C14N:
                            {
                                return atomBridge.createString(atomBridge.getC14NForm(sourceAtom));
                            }
                            case MODERN:
                            {
                                return atomBridge.createString(NumericSupport.formatDecimalXQuery10(atomBridge.getDecimal(sourceAtom)));
                            }
                            case LEGACY:
                            {
                                return atomBridge.createString(NumericSupport.formatDecimalXPath10(atomBridge.getDecimal(sourceAtom)));
                            }
                            default:
                            {
                                throw new AssertionError(castingContext.getEmulation());
                            }
                        }
                    }
                    default:
                    {
                        return atomBridge.createString(atomBridge.getC14NForm(sourceAtom));
                    }
                }
            }
            case NORMALIZED_STRING:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case NORMALIZED_STRING:
                    {
                        return sourceAtom;
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType + " => " + targetType);
                    }
                }
            }
            case TOKEN:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case TOKEN:
                    {
                        return sourceAtom;
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType + " => " + targetType);
                    }
                }
            }
            case LANGUAGE:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType);
                    }
                }
            }
            case NMTOKEN:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType);
                    }
                }
            }
            case NAME:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType);
                    }
                }
            }
            case NCNAME:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType);
                    }
                }
            }
            case ID:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case SHORT:
                    case INT:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
                    }
                }
            }
            case IDREF:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        throw new AssertionError(sourceType);
                    }
                }
            }
            case ENTITY:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castWithinBranchAs(sourceAtom, targetType, pcx, atomBridge);
                    }
                    case DOUBLE:
                    case FLOAT:
                    case DECIMAL:
                    case INTEGER:
                    case LONG:
                    case SHORT:
                    case INT:
                    {
                        return castThroughString(sourceAtom, targetType, castingContext, pcx, atomBridge);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case POSITIVE_INTEGER:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsPositiveInteger(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsPositiveInteger(atomBridge.getFloat(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsPositiveInteger(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsPositiveInteger(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsPositiveInteger(atomBridge.getLong(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case POSITIVE_INTEGER:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case NON_NEGATIVE_INTEGER:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsNonNegativeInteger(atomBridge.getDouble(sourceAtom)), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsNonNegativeInteger(atomBridge.getFloat(sourceAtom)), targetType);
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsNonNegativeInteger(atomBridge.getDecimal(sourceAtom)), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsNonNegativeInteger(atomBridge.getInteger(sourceAtom)), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsNonNegativeInteger(atomBridge.getLong(sourceAtom)), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsNonNegativeInteger(atomBridge.getInt(sourceAtom)), targetType);
                    }
                    case SHORT:
                    {
                        return atomBridge.createIntegerDerived(castShortAsNonNegativeInteger(atomBridge.getShort(sourceAtom)), targetType);
                    }
                    case BYTE:
                    {
                        return atomBridge.createIntegerDerived(castByteAsNonNegativeInteger(atomBridge.getByte(sourceAtom)), targetType);
                    }
                    case NON_NEGATIVE_INTEGER:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case NON_POSITIVE_INTEGER:
            {
                switch (sourceType)
                {
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsNonPositiveInteger(atomBridge.getDecimal(sourceAtom)), targetType);
                    }
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsNonPositiveInteger(atomBridge.getDouble(sourceAtom)), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsNonPositiveInteger(atomBridge.getFloat(sourceAtom)), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsNonPositiveInteger(atomBridge.getInteger(sourceAtom)), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsNonPositiveInteger(atomBridge.getLong(sourceAtom)), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsNonPositiveInteger(atomBridge.getInt(sourceAtom)), targetType);
                    }
                    case SHORT:
                    {
                        return atomBridge.createIntegerDerived(castShortAsNonPositiveInteger(atomBridge.getShort(sourceAtom)), targetType);
                    }
                    case BYTE:
                    {
                        return atomBridge.createIntegerDerived(castByteAsNonPositiveInteger(atomBridge.getByte(sourceAtom)), targetType);
                    }
                    case NON_POSITIVE_INTEGER:
                    {
                        return sourceAtom;
                    }
                    case NEGATIVE_INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsNonPositiveInteger(atomBridge.getInteger(sourceAtom)), targetType);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case NEGATIVE_INTEGER:
            {
                switch (sourceType)
                {
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsNegativeInteger(atomBridge.getDecimal(sourceAtom)), targetType);
                    }
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsNegativeInteger(atomBridge.getDouble(sourceAtom)), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsNegativeInteger(atomBridge.getFloat(sourceAtom)), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsNegativeInteger(atomBridge.getInteger(sourceAtom)), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsNegativeInteger(atomBridge.getLong(sourceAtom)), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsNegativeInteger(atomBridge.getInt(sourceAtom)), targetType);
                    }
                    case NEGATIVE_INTEGER:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case UNSIGNED_LONG:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsUnsignedLong(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsUnsignedLong(atomBridge.getFloat(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsUnsignedLong(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsUnsignedLong(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsUnsignedLong(atomBridge.getLong(sourceAtom)), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsUnsignedLong(atomBridge.getInt(sourceAtom)), targetType);
                    }
                    case SHORT:
                    {
                        return atomBridge.createIntegerDerived(castShortAsUnsignedLong(atomBridge.getShort(sourceAtom)), targetType);
                    }
                    case BYTE:
                    {
                        return atomBridge.createIntegerDerived(castByteAsUnsignedLong(atomBridge.getByte(sourceAtom)), targetType);
                    }
                    case UNSIGNED_LONG:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case UNSIGNED_INT:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsUnsignedInt(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsUnsignedInt(atomBridge.getFloat(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsUnsignedInt(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsUnsignedInt(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case UNSIGNED_INT:
                    {
                        return sourceAtom;
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsUnsignedInt(atomBridge.getLong(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsUnsignedInt(atomBridge.getInt(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case SHORT:
                    {
                        return atomBridge.createIntegerDerived(castShortAsUnsignedInt(atomBridge.getShort(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case BYTE:
                    {
                        return atomBridge.createIntegerDerived(castByteAsUnsignedInt(atomBridge.getByte(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case UNSIGNED_SHORT:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsUnsignedShort(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsUnsignedShort(atomBridge.getFloat(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsUnsignedShort(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsUnsignedShort(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsUnsignedShort(atomBridge.getLong(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsUnsignedShort(atomBridge.getInt(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case SHORT:
                    {
                        return atomBridge.createIntegerDerived(castShortAsUnsignedShort(atomBridge.getShort(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case BYTE:
                    {
                        return atomBridge.createIntegerDerived(castByteAsUnsignedShort(atomBridge.getByte(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case UNSIGNED_SHORT:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case UNSIGNED_BYTE:
            {
                switch (sourceType)
                {
                    case DOUBLE:
                    {
                        return atomBridge.createIntegerDerived(castDoubleAsUnsignedByte(atomBridge.getDouble(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case FLOAT:
                    {
                        return atomBridge.createIntegerDerived(castFloatAsUnsignedByte(atomBridge.getFloat(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case DECIMAL:
                    {
                        return atomBridge.createIntegerDerived(castDecimalAsUnsignedByte(atomBridge.getDecimal(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INTEGER:
                    {
                        return atomBridge.createIntegerDerived(castIntegerAsUnsignedByte(atomBridge.getInteger(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case LONG:
                    {
                        return atomBridge.createIntegerDerived(castLongAsUnsignedByte(atomBridge.getLong(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case INT:
                    {
                        return atomBridge.createIntegerDerived(castIntAsUnsignedByte(atomBridge.getInt(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case SHORT:
                    {
                        return atomBridge.createIntegerDerived(castShortAsUnsignedByte(atomBridge.getShort(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case BYTE:
                    {
                        return atomBridge.createIntegerDerived(castByteAsUnsignedByte(atomBridge.getByte(sourceAtom), checkCapacity, raiseError), targetType);
                    }
                    case UNSIGNED_BYTE:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case UNTYPED_ATOMIC:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return sourceAtom;
                    }
                    case DOUBLE:
                    {
                        switch (castingContext.getEmulation())
                        {
                            case C14N:
                            {
                                return atomBridge.createUntypedAtomic(atomBridge.getC14NForm(sourceAtom));
                            }
                            case MODERN:
                            {
                                return atomBridge.createUntypedAtomic(NumericSupport.formatDoubleXQuery10(atomBridge.getDouble(sourceAtom)));
                            }
                            case LEGACY:
                            {
                                return atomBridge.createUntypedAtomic(NumericSupport.formatDoubleXPath10(atomBridge.getDouble(sourceAtom)));
                            }
                            default:
                            {
                                throw new AssertionError(castingContext.getEmulation());
                            }
                        }
                    }
                    case FLOAT:
                    {
                        switch (castingContext.getEmulation())
                        {
                            case C14N:
                            {
                                return atomBridge.createUntypedAtomic(atomBridge.getC14NForm(sourceAtom));
                            }
                            case MODERN:
                            {
                                return atomBridge.createUntypedAtomic(NumericSupport.formatFloatXQuery10(atomBridge.getFloat(sourceAtom)));
                            }
                            case LEGACY:
                            {
                                return atomBridge.createUntypedAtomic(NumericSupport.formatFloatXPath10(atomBridge.getFloat(sourceAtom)));
                            }
                            default:
                            {
                                throw new AssertionError(castingContext.getEmulation());
                            }
                        }
                    }
                    case DECIMAL:
                    {
                        switch (castingContext.getEmulation())
                        {
                            case C14N:
                            {
                                return atomBridge.createUntypedAtomic(atomBridge.getC14NForm(sourceAtom));
                            }
                            case MODERN:
                            {
                                return atomBridge.createUntypedAtomic(NumericSupport.formatDecimalXQuery10(atomBridge.getDecimal(sourceAtom)));
                            }
                            case LEGACY:
                            {
                                return atomBridge.createUntypedAtomic(NumericSupport.formatDecimalXPath10(atomBridge.getDecimal(sourceAtom)));
                            }
                            default:
                            {
                                throw new AssertionError(castingContext.getEmulation());
                            }
                        }
                    }
                    default:
                    {
                        return atomBridge.createUntypedAtomic(atomBridge.getC14NForm(sourceAtom));
                    }
                }
            }
            case ANY_URI:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case STRING:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getString(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case ANY_URI:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
                    }
                }
            }
            case BASE64_BINARY:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    case STRING:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case BASE64_BINARY:
                    {
                        return sourceAtom;
                    }
                    case HEX_BINARY:
                    {
                        return atomBridge.createBase64Binary(atomBridge.getHexBinary(sourceAtom));
                    }
                    default:
                    {
                        throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
                    }
                }
            }
            case HEX_BINARY:
            {
                switch (sourceType)
                {
                    case UNTYPED_ATOMIC:
                    case STRING:
                    {
                        return castFromStringOrUntypedAtomic(atomBridge.getC14NForm(sourceAtom), targetType, pcx, atomBridge);
                    }
                    case BASE64_BINARY:
                    {
                        return atomBridge.createHexBinary(atomBridge.getBase64Binary(sourceAtom));
                    }
                    case HEX_BINARY:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
                    }
                }
            }
            case DATE:
            {
                switch (sourceType)
                {
                    case DATE:
                    {
                        return sourceAtom;
                    }
                    case DATETIME:
                    {
                        return atomBridge.createDate(atomBridge.getYear(sourceAtom), atomBridge.getMonth(sourceAtom), atomBridge.getDayOfMonth(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case DATETIME:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    {
                        return sourceAtom;
                    }
                    case DATE:
                    {
                        return atomBridge.createDateTime(atomBridge.getYear(sourceAtom), atomBridge.getMonth(sourceAtom), atomBridge.getDayOfMonth(sourceAtom), 0, 0, 0, 0, BigDecimal.ZERO, atomBridge.getGmtOffset(sourceAtom));
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case DURATION_DAYTIME:
            {
                switch (sourceType)
                {
                    case DURATION:
                    {
                        return atomBridge.createDayTimeDuration(atomBridge.getDurationTotalSeconds(sourceAtom));
                    }
                    case DURATION_DAYTIME:
                    {
                        return sourceAtom;
                    }
                    case DURATION_YEARMONTH:
                    {
                        return atomBridge.createDayTimeDuration(BigDecimal.ZERO);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case DURATION_YEARMONTH:
            {
                switch (sourceType)
                {
                    case DURATION:
                    {
                        return atomBridge.createYearMonthDuration(atomBridge.getDurationTotalMonths(sourceAtom));
                    }
                    case DURATION_YEARMONTH:
                    {
                        return sourceAtom;
                    }
                    case DURATION_DAYTIME:
                    {
                        return atomBridge.createYearMonthDuration(0);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case DURATION:
            {
                switch (sourceType)
                {
                    case DURATION:
                    {
                        return sourceAtom;
                    }
                    case DURATION_DAYTIME:
                    {
                        return atomBridge.createDuration(0, atomBridge.getDurationTotalSeconds(sourceAtom));
                    }
                    case DURATION_YEARMONTH:
                    {
                        return atomBridge.createDuration(atomBridge.getDurationTotalMonths(sourceAtom), BigDecimal.ZERO);
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case GDAY:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    case DATE:
                    {
                        return atomBridge.createDay(atomBridge.getDayOfMonth(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    case GDAY:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case GMONTH:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    case DATE:
                    {
                        return atomBridge.createMonth(atomBridge.getMonth(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    case GMONTH:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case GMONTHDAY:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    case DATE:
                    {
                        return atomBridge.createMonthDay(atomBridge.getMonth(sourceAtom), atomBridge.getDayOfMonth(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    case GMONTHDAY:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case GYEAR:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    case DATE:
                    {
                        return atomBridge.createYear(atomBridge.getYear(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    case GYEAR:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case GYEARMONTH:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    case DATE:
                    {
                        return atomBridge.createYearMonth(atomBridge.getYear(sourceAtom), atomBridge.getMonth(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    case GYEARMONTH:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case TIME:
            {
                switch (sourceType)
                {
                    case DATETIME:
                    {
                        return atomBridge.createTime(atomBridge.getHourOfDay(sourceAtom), atomBridge.getMinute(sourceAtom), atomBridge.getIntegralSecondPart(sourceAtom), 0, atomBridge.getFractionalSecondPart(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
                    }
                    case TIME:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        return castAsOrErrors(sourceAtom, sourceType, targetType, pcx, atomBridge, nameBridge);
                    }
                }
            }
            case QNAME:
            {
                switch (sourceType)
                {
                    case QNAME:
                    {
                        return sourceAtom;
                    }
                    default:
                    {
                        // Interestingly, casting from xs:untypedAtomic is not allowed but xs:string is allowed.
                        if (sourceType.isString())
                        {
                        }
                        else
                        {
                            throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
                        }
                    }
                }
            }
            case ANY_TYPE:
            case ANY_SIMPLE_TYPE:
            case ANY_ATOMIC_TYPE:
            case UNTYPED:
            case NOTATION:
            case IDREFS:
            case NMTOKENS:
            case ENTITIES:
            {
                throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), sourceType.toQName(), nameBridge.nativeType(targetType), XPTY0004);
            }
            default:
            {
                throw new AssertionError(sourceType + "=>" + targetType);
            }
        }
    }

    private static byte castByteAsNonNegativeInteger(final byte value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Byte.toString(value), NativeType.BYTE.toQName(), NativeType.NON_NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static byte castByteAsNonPositiveInteger(final byte value) throws AtomCastException
    {
        if (value <= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Byte.toString(value), NativeType.BYTE.toQName(), NativeType.NON_POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static byte castByteAsUnsignedByte(final byte value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Byte.toString(value), NativeType.BYTE.toQName(), NativeType.UNSIGNED_BYTE.toQName(), FORG0001);
        }
    }

    private static byte castByteAsUnsignedInt(final byte value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Byte.toString(value), NativeType.BYTE.toQName(), NativeType.UNSIGNED_INT.toQName(), FORG0001);
        }
    }

    private static byte castByteAsUnsignedLong(final byte value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Byte.toString(value), NativeType.BYTE.toQName(), NativeType.UNSIGNED_LONG.toQName(), FORG0001);
        }
    }

    private static byte castByteAsUnsignedShort(final byte value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Byte.toString(value), NativeType.BYTE.toQName(), NativeType.UNSIGNED_SHORT.toQName(), FORG0001);
        }
    }

    private static byte castDecimalAsByte(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsByte(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static double castDecimalAsDouble(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            final BigDecimal abs = decval.abs();
            if (abs.compareTo(DOUBLE_MAX_VALUE_AS_DECIMAL) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(constructionString(decval), NativeType.DOUBLE.toQName(), FOAR0002);
                }
                return Double.MAX_VALUE;
            }
            if (abs.signum() != 0 && abs.compareTo(DOUBLE_MIN_VALUE_AS_DECIMAL) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(constructionString(decval), NativeType.DOUBLE.toQName(), FOAR0002);
                }
                return Double.MIN_VALUE;
            }
        }
        return decval.doubleValue();
    }

    private static float castDecimalAsFloat(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            final BigDecimal abs = decval.abs();
            if (abs.compareTo(FLOAT_MAX_VALUE_AS_DECIMAL) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(constructionString(decval), NativeType.FLOAT.toQName(), FOAR0002);
                }
                return Float.MAX_VALUE;
            }
            if (abs.signum() != 0 && abs.compareTo(FLOAT_MIN_VALUE_AS_DECIMAL) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(constructionString(decval), NativeType.FLOAT.toQName(), FOAR0002);
                }
                return Float.MIN_VALUE;
            }
        }
        return decval.floatValue();
    }

    private static int castDecimalAsInt(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsInt(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static BigInteger castDecimalAsInteger(final BigDecimal decval)
    {
        return decval.toBigInteger();
    }

    private static long castDecimalAsLong(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsLong(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static BigInteger castDecimalAsNegativeInteger(final BigDecimal decval) throws AtomCastException
    {
        return castIntegerAsNegativeInteger(castDecimalAsInteger(decval));
    }

    private static BigInteger castDecimalAsNonNegativeInteger(final BigDecimal decval) throws AtomCastException
    {
        return castIntegerAsNonNegativeInteger(castDecimalAsInteger(decval));
    }

    private static BigInteger castDecimalAsNonPositiveInteger(final BigDecimal decval) throws AtomCastException
    {
        return castIntegerAsNonPositiveInteger(castDecimalAsInteger(decval));
    }

    private static BigInteger castDecimalAsPositiveInteger(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsPositiveInteger(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static short castDecimalAsShort(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsShort(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static short castDecimalAsUnsignedByte(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsUnsignedByte(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static long castDecimalAsUnsignedInt(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsUnsignedInt(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static BigInteger castDecimalAsUnsignedLong(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsUnsignedLong(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static int castDecimalAsUnsignedShort(final BigDecimal decval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        return castIntegerAsUnsignedShort(castDecimalAsInteger(decval), checkCapacity, raiseError);
    }

    private static byte castDoubleAsByte(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.BYTE);
        assertDoubleNotInfinite(dblval, NativeType.BYTE);
        final long lval = (long)dblval;
        if (lval <= Byte.MAX_VALUE && lval >= Byte.MIN_VALUE)
        {
            return (byte)lval;
        }
        else
        {
            throw new AtomCastException(NumericSupport.formatDoubleC14N(dblval), NativeType.BYTE.toQName(), FORG0001);
        }
    }

    private static BigDecimal castDoubleAsDecimal(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.DECIMAL);
        assertDoubleNotInfinite(dblval, NativeType.DECIMAL);
        return BigDecimal.valueOf(dblval);
    }

    private static float castDoubleAsFloat(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (Double.isNaN(dblval))
        {
            return Float.NaN;
        }
        if (Double.isInfinite(dblval))
        {
            return (float)dblval;
        }
        if (checkCapacity)
        {
            // Overflow
            if (raiseError && Math.abs(dblval) > Float.MAX_VALUE)
            {
                throw new AtomCastException(Double.toString(dblval), NativeType.FLOAT.toQName(), FOAR0002);
            }
            // Underflow
            if (raiseError && (dblval != 0.0) && Math.abs(dblval) < Float.MIN_VALUE)
            {
                throw new AtomCastException(Double.toString(dblval), NativeType.FLOAT.toQName(), FOAR0002);
            }
        }
        return (float)dblval;
    }

    private static int castDoubleAsInt(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.INT);
        assertDoubleNotInfinite(dblval, NativeType.INT);
        final long lval = (long)dblval;
        if (lval <= Integer.MAX_VALUE && lval >= Integer.MIN_VALUE)
        {
            return (int)lval;
        }
        else
        {
            throw new AtomCastException(NumericSupport.formatDoubleC14N(dblval), NativeType.INT.toQName(), FORG0001);
        }
    }

    private static BigInteger castDoubleAsInteger(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.INTEGER);
        assertDoubleNotInfinite(dblval, NativeType.INTEGER);
        return BigDecimal.valueOf(dblval).toBigInteger();
    }

    private static long castDoubleAsLong(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        final BigInteger ival;
        try
        {
            ival = castDoubleAsInteger(dblval);
        }
        catch (final AtomCastException e)
        {
            throw new AtomCastException(e.getSourceValue(), NativeType.LONG.toQName(), e.getErrorCode(), e.getCause());
        }
        return castIntegerAsLong(ival, checkCapacity, raiseError);
    }

    private static BigInteger castDoubleAsNegativeInteger(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.NEGATIVE_INTEGER);
        assertDoubleNotInfinite(dblval, NativeType.NEGATIVE_INTEGER);
        return castIntegerAsNegativeInteger(BigInteger.valueOf((long)dblval));
    }

    private static BigInteger castDoubleAsNonNegativeInteger(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.NON_NEGATIVE_INTEGER);
        assertDoubleNotInfinite(dblval, NativeType.NON_NEGATIVE_INTEGER);
        return castIntegerAsNonNegativeInteger(BigInteger.valueOf((long)dblval));
    }

    private static BigInteger castDoubleAsNonPositiveInteger(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.NON_POSITIVE_INTEGER);
        assertDoubleNotInfinite(dblval, NativeType.NON_POSITIVE_INTEGER);
        return castIntegerAsNonPositiveInteger(BigDecimal.valueOf(dblval).toBigInteger());
    }

    private static BigInteger castDoubleAsPositiveInteger(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.POSITIVE_INTEGER);
        assertDoubleNotInfinite(dblval, NativeType.POSITIVE_INTEGER);
        return castIntegerAsPositiveInteger(BigInteger.valueOf((long)dblval), checkCapacity, raiseError);
    }

    private static short castDoubleAsShort(final double dblval) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.SHORT);
        assertDoubleNotInfinite(dblval, NativeType.SHORT);
        final long lval = (long)dblval;
        if (lval <= Short.MAX_VALUE && lval >= Short.MIN_VALUE)
        {
            return (short)lval;
        }
        else
        {
            throw new AtomCastException(NumericSupport.formatDoubleC14N(dblval), NativeType.SHORT.toQName(), FORG0001);
        }
    }

    private static short castDoubleAsUnsignedByte(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.UNSIGNED_BYTE);
        assertDoubleNotInfinite(dblval, NativeType.UNSIGNED_BYTE);
        return castIntegerAsUnsignedByte(BigInteger.valueOf((long)dblval), checkCapacity, raiseError);
    }

    private static long castDoubleAsUnsignedInt(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.UNSIGNED_INT);
        assertDoubleNotInfinite(dblval, NativeType.UNSIGNED_INT);
        return castIntegerAsUnsignedInt(BigInteger.valueOf((long)dblval), checkCapacity, raiseError);
    }

    private static BigInteger castDoubleAsUnsignedLong(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.UNSIGNED_LONG);
        assertDoubleNotInfinite(dblval, NativeType.UNSIGNED_LONG);
        return castIntegerAsUnsignedLong(BigInteger.valueOf((long)dblval), checkCapacity, raiseError);
    }

    private static int castDoubleAsUnsignedShort(final double dblval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertDoubleIsNumber(dblval, NativeType.UNSIGNED_SHORT);
        assertDoubleNotInfinite(dblval, NativeType.UNSIGNED_SHORT);
        return castIntegerAsUnsignedShort(BigInteger.valueOf((long)dblval), checkCapacity, raiseError);
    }

    private static byte castFloatAsByte(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.BYTE);
        assertFloatNotInfinite(fltval, NativeType.BYTE);
        final long lval = (long)fltval;
        if (lval <= Byte.MAX_VALUE && lval >= Byte.MIN_VALUE)
        {
            return (byte)lval;
        }
        else
        {
            throw new AtomCastException(NumericSupport.formatFloatC14N(fltval), NativeType.BYTE.toQName(), FORG0001);
        }
    }

    private static BigDecimal castFloatAsDecimal(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.DECIMAL);
        assertFloatNotInfinite(fltval, NativeType.DECIMAL);
        return BigDecimal.valueOf(fltval);
    }

    private static int castFloatAsInt(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.INT);
        assertFloatNotInfinite(fltval, NativeType.INT);
        final long lval = (long)fltval;
        if (lval <= Integer.MAX_VALUE && lval >= Integer.MIN_VALUE)
        {
            return (int)lval;
        }
        else
        {
            throw new AtomCastException(NumericSupport.formatFloatC14N(fltval), NativeType.INT.toQName(), FORG0001);
        }
    }

    private static BigInteger castFloatAsInteger(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.INTEGER);
        assertFloatNotInfinite(fltval, NativeType.INTEGER);
        return BigInteger.valueOf((long)fltval);
    }

    private static long castFloatAsLong(final float fltval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        final BigInteger ival;
        try
        {
            ival = castFloatAsInteger(fltval);
        }
        catch (final AtomCastException e)
        {
            throw new AtomCastException(e.getSourceValue(), NativeType.LONG.toQName(), e.getErrorCode(), e.getCause());
        }
        return castIntegerAsInt(ival, checkCapacity, raiseError);
    }

    private static BigInteger castFloatAsNegativeInteger(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.NEGATIVE_INTEGER);
        assertFloatNotInfinite(fltval, NativeType.NEGATIVE_INTEGER);
        return castIntegerAsNegativeInteger(BigInteger.valueOf((long)fltval));
    }

    private static BigInteger castFloatAsNonNegativeInteger(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.NON_NEGATIVE_INTEGER);
        assertFloatNotInfinite(fltval, NativeType.NON_NEGATIVE_INTEGER);
        return castIntegerAsNonNegativeInteger(BigInteger.valueOf((long)fltval));
    }

    private static BigInteger castFloatAsNonPositiveInteger(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.NON_POSITIVE_INTEGER);
        assertFloatNotInfinite(fltval, NativeType.NON_POSITIVE_INTEGER);
        return castIntegerAsNonPositiveInteger(BigInteger.valueOf((long)fltval));
    }

    private static BigInteger castFloatAsPositiveInteger(final float fltval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.POSITIVE_INTEGER);
        assertFloatNotInfinite(fltval, NativeType.POSITIVE_INTEGER);
        return castIntegerAsPositiveInteger(BigInteger.valueOf((long)fltval), checkCapacity, raiseError);
    }

    private static short castFloatAsShort(final float fltval) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.SHORT);
        assertFloatNotInfinite(fltval, NativeType.SHORT);
        final long lval = (long)fltval;
        if (lval <= Short.MAX_VALUE && lval >= Short.MIN_VALUE)
        {
            return (short)lval;
        }
        else
        {
            throw new AtomCastException(NumericSupport.formatFloatC14N(fltval), NativeType.SHORT.toQName(), FORG0001);
        }
    }

    private static short castFloatAsUnsignedByte(final float fltval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.UNSIGNED_BYTE);
        assertFloatNotInfinite(fltval, NativeType.UNSIGNED_BYTE);
        return castIntegerAsUnsignedByte(BigInteger.valueOf((long)fltval), checkCapacity, raiseError);
    }

    private static long castFloatAsUnsignedInt(final float fltval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.UNSIGNED_INT);
        assertFloatNotInfinite(fltval, NativeType.UNSIGNED_INT);
        return castIntegerAsUnsignedInt(BigInteger.valueOf((long)fltval), checkCapacity, raiseError);
    }

    private static BigInteger castFloatAsUnsignedLong(final float fltval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.UNSIGNED_LONG);
        assertFloatNotInfinite(fltval, NativeType.UNSIGNED_LONG);
        return castIntegerAsUnsignedLong(BigInteger.valueOf((long)fltval), checkCapacity, raiseError);
    }

    private static int castFloatAsUnsignedShort(final float fltval, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        assertFloatIsNumber(fltval, NativeType.UNSIGNED_SHORT);
        assertFloatNotInfinite(fltval, NativeType.UNSIGNED_SHORT);
        return castIntegerAsUnsignedShort(BigInteger.valueOf((long)fltval), checkCapacity, raiseError);
    }

    private static <A> A castFromStringOrUntypedAtomic(final String sourceAtom, final NativeType targetType, final ComponentProvider pcx, AtomBridge<A> bridge) throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(sourceAtom, "sourceAtom");
        PreCondition.assertArgumentNotNull(targetType, "targetType");

        final Type type = pcx.getTypeDefinition(targetType);
        if (null != type)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<A> atoms = atomicType.validate(sourceAtom, bridge);
                    final int size = atoms.size();
                    if (1 == size)
                    {
                        return atoms.get(0);
                    }
                    else if (0 == size)
                    {
                        return null;
                    }
                    else
                    {
                        // Atomic type should not be yielding multiple atoms.
                        throw new AssertionError();
                    }
                }
                catch (final DatatypeException e)
                {
                    throw new AtomCastException(sourceAtom, e.getType(), FORG0001, e);
                }
            }
            else
            {
                throw new IllegalArgumentException(targetType + " dataType is not an atomic type.");
            }
        }
        else
        {
            throw new IllegalArgumentException(targetType + " dataType could not be found in the processing context.");
        }
    }

    private static byte castIntAsByte(final int ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (ival > Byte.MAX_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Integer.toString(ival), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MAX_VALUE;
                }
            }
            if (ival < Byte.MIN_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Integer.toString(ival), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MIN_VALUE;
                }
            }
        }
        return (byte)ival;
    }

    private static int castIntAsNegativeInteger(final int ival) throws AtomCastException
    {
        if (ival < 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(Integer.toString(ival), NativeType.INT.toQName(), NativeType.NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static int castIntAsNonNegativeInteger(final int value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Integer.toString(value), NativeType.INT.toQName(), NativeType.NON_NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static int castIntAsNonPositiveInteger(final int ival) throws AtomCastException
    {
        if (ival <= 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(Integer.toString(ival), NativeType.INT.toQName(), NativeType.NON_POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static short castIntAsShort(final int ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (ival > Short.MAX_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Integer.toString(ival), NativeType.SHORT.toQName(), FORG0001);
                }
                else
                {
                    return Short.MAX_VALUE;
                }
            }
            if (ival < Short.MIN_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Integer.toString(ival), NativeType.SHORT.toQName(), FORG0001);
                }
                else
                {
                    return Short.MIN_VALUE;
                }
            }
        }
        return (short)ival;
    }

    private static short castIntAsUnsignedByte(final int value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        // TODO: Bounds checking
        if (value >= 0)
        {
            return (short)value;
        }
        else
        {
            throw new AtomCastException(Integer.toString(value), NativeType.INT.toQName(), NativeType.UNSIGNED_BYTE.toQName(), FORG0001);
        }
    }

    private static int castIntAsUnsignedInt(final int value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Integer.toString(value), NativeType.INT.toQName(), NativeType.UNSIGNED_INT.toQName(), FORG0001);
        }
    }

    private static int castIntAsUnsignedLong(final int ival) throws AtomCastException
    {
        if (ival >= 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(Integer.toString(ival), NativeType.INT.toQName(), NativeType.UNSIGNED_LONG.toQName(), FORG0001);
        }
    }

    private static int castIntAsUnsignedShort(final int value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Integer.toString(value), NativeType.INT.toQName(), NativeType.UNSIGNED_SHORT.toQName(), FORG0001);
        }
    }

    private static byte castIntegerAsByte(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (ival.compareTo(BYTE_MAX_VALUE) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MAX_VALUE;
                }
            }
            else if (ival.compareTo(BYTE_MIN_VALUE) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MIN_VALUE;
                }
            }
        }
        return ival.byteValue();
    }

    private static double castIntegerAsDouble(final BigInteger integer, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (integer.compareTo(DOUBLE_MAX_VALUE) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(integer.toString(), NativeType.DOUBLE.toQName(), FOAR0002);
                }
                return Double.MAX_VALUE;
            }
            if (integer.compareTo(DOUBLE_MIN_VALUE) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(integer.toString(), NativeType.DOUBLE.toQName(), FOAR0002);
                }
                return Double.MIN_VALUE;
            }
        }
        return integer.doubleValue();
    }

    private static float castIntegerAsFloat(final BigInteger integer, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (integer.compareTo(FLOAT_MAX_VALUE) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(integer.toString(), NativeType.FLOAT.toQName(), FOAR0002);
                }
                return Float.MAX_VALUE;
            }
            if (integer.compareTo(FLOAT_MIN_VALUE) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(integer.toString(), NativeType.FLOAT.toQName(), FOAR0002);
                }
                return Float.MIN_VALUE;
            }
        }
        return integer.floatValue();
    }

    private static int castIntegerAsInt(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (ival.compareTo(INT_MAX_VALUE) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.INT.toQName(), FORG0001);
                }
                else
                {
                    return Integer.MAX_VALUE;
                }
            }
            else if (ival.compareTo(INT_MIN_VALUE) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.INT.toQName(), FORG0001);
                }
                else
                {
                    return Integer.MIN_VALUE;
                }
            }
        }
        return ival.intValue();

    }

    private static long castIntegerAsLong(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (ival.compareTo(LONG_MAX_VALUE) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.LONG.toQName(), FOCA0003);
                }
                else
                {
                    return Long.MAX_VALUE;
                }
            }
            if (ival.compareTo(LONG_MIN_VALUE) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.LONG.toQName(), FOCA0003);
                }
                else
                {
                    return Long.MIN_VALUE;
                }
            }
            return ival.longValue();
        }
        else
        {
            return ival.longValue();
        }
    }

    private static BigInteger castIntegerAsNegativeInteger(final BigInteger ival) throws AtomCastException
    {
        if (ival.signum() < 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static BigInteger castIntegerAsNonNegativeInteger(final BigInteger ival) throws AtomCastException
    {
        if (ival.signum() >= 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.NON_NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static BigInteger castIntegerAsNonPositiveInteger(final BigInteger ival) throws AtomCastException
    {
        if (ival.signum() <= 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.NON_POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static BigInteger castIntegerAsPositiveInteger(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (ival.signum() > 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static short castIntegerAsShort(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (ival.compareTo(SHORT_MAX_VALUE) > 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.SHORT.toQName(), FORG0001);
                }
                else
                {
                    return Short.MAX_VALUE;
                }
            }
            else if (ival.compareTo(SHORT_MIN_VALUE) < 0)
            {
                if (raiseError)
                {
                    throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.SHORT.toQName(), FORG0001);
                }
                else
                {
                    return Short.MIN_VALUE;
                }
            }
        }
        return ival.shortValue();
    }

    private static short castIntegerAsUnsignedByte(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (ival.signum() >= 0 && ival.compareTo(UNSIGNED_BYTE_MAX_VALUE) <= 0)
        {
            return ival.shortValue();
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.UNSIGNED_BYTE.toQName(), FORG0001);
        }
    }

    private static long castIntegerAsUnsignedInt(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (ival.signum() >= 0 && ival.compareTo(UNSIGNED_INT_MAX_VALUE) <= 0)
        {
            return ival.longValue();
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.UNSIGNED_INT.toQName(), FORG0001);
        }
    }

    private static BigInteger castIntegerAsUnsignedLong(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (ival.signum() >= 0 && ival.compareTo(UNSIGNED_LONG_MAX_VALUE) <= 0)
        {
            return ival;
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.UNSIGNED_LONG.toQName(), FORG0001);
        }
    }

    private static int castIntegerAsUnsignedShort(final BigInteger ival, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (ival.signum() >= 0 && ival.compareTo(UNSIGNED_SHORT_MAX_VALUE) <= 0)
        {
            return ival.intValue();
        }
        else
        {
            throw new AtomCastException(ival.toString(), NativeType.INTEGER.toQName(), NativeType.UNSIGNED_SHORT.toQName(), FORG0001);
        }
    }

    private static byte castLongAsByte(final long val, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (val > Byte.MAX_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Long.toString(val), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MAX_VALUE;
                }
            }
            if (val < Byte.MIN_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Long.toString(val), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MIN_VALUE;
                }
            }
        }
        return (byte)val;
    }

    private static int castLongAsInt(final long val, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (val > Integer.MAX_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Long.toString(val), NativeType.INT.toQName(), FORG0001);
                }
                else
                {
                    return Integer.MAX_VALUE;
                }
            }
            if (val < Integer.MIN_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Long.toString(val), NativeType.INT.toQName(), FORG0001);
                }
                else
                {
                    return Integer.MIN_VALUE;
                }
            }
        }
        return (int)val;
    }

    private static long castLongAsNegativeInteger(final long value) throws AtomCastException
    {
        if (value < 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static long castLongAsNonNegativeInteger(final long value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.NON_NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static long castLongAsNonPositiveInteger(final long value) throws AtomCastException
    {
        if (value <= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.NON_POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static long castLongAsPositiveInteger(final long value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value > 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static short castLongAsShort(final long val, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (val > Short.MAX_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Long.toString(val), NativeType.SHORT.toQName(), FORG0001);
                }
                else
                {
                    return Short.MAX_VALUE;
                }
            }
            if (val < Short.MIN_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Long.toString(val), NativeType.SHORT.toQName(), FORG0001);
                }
                else
                {
                    return Short.MIN_VALUE;
                }
            }
        }
        return (short)val;
    }

    private static short castLongAsUnsignedByte(final long value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        // TODO: Bounds checking
        if (value >= 0)
        {
            return (short)value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.UNSIGNED_BYTE.toQName(), FORG0001);
        }
    }

    private static long castLongAsUnsignedInt(final long value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0 && value <= UNSIGNED_INT_MAX_VALUE_AS_LONG)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.UNSIGNED_INT.toQName(), FORG0001);
        }
    }

    private static long castLongAsUnsignedLong(final long value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.UNSIGNED_LONG.toQName(), FORG0001);
        }
    }

    private static int castLongAsUnsignedShort(final long value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        // TODO: Bounds checking
        if (value >= 0)
        {
            return (int)value;
        }
        else
        {
            throw new AtomCastException(Long.toString(value), NativeType.LONG.toQName(), NativeType.UNSIGNED_SHORT.toQName(), FORG0001);
        }
    }

    private static byte castShortAsByte(final short val, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (checkCapacity)
        {
            if (val > Byte.MAX_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Short.toString(val), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MAX_VALUE;
                }
            }
            if (val < Byte.MIN_VALUE)
            {
                if (raiseError)
                {
                    throw new AtomCastException(Short.toString(val), NativeType.BYTE.toQName(), FORG0001);
                }
                else
                {
                    return Byte.MIN_VALUE;
                }
            }
        }
        return (byte)val;
    }

    private static short castShortAsNonNegativeInteger(final short value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Short.toString(value), NativeType.SHORT.toQName(), NativeType.NON_NEGATIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static short castShortAsNonPositiveInteger(final short value) throws AtomCastException
    {
        if (value <= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Short.toString(value), NativeType.SHORT.toQName(), NativeType.NON_POSITIVE_INTEGER.toQName(), FORG0001);
        }
    }

    private static short castShortAsUnsignedByte(final short value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Short.toString(value), NativeType.SHORT.toQName(), NativeType.UNSIGNED_BYTE.toQName(), FORG0001);
        }
    }

    private static short castShortAsUnsignedInt(final short value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Short.toString(value), NativeType.SHORT.toQName(), NativeType.UNSIGNED_INT.toQName(), FORG0001);
        }
    }

    private static short castShortAsUnsignedLong(final short value) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Short.toString(value), NativeType.SHORT.toQName(), NativeType.UNSIGNED_LONG.toQName(), FORG0001);
        }
    }

    private static short castShortAsUnsignedShort(final short value, final boolean checkCapacity, final boolean raiseError) throws AtomCastException
    {
        if (value >= 0)
        {
            return value;
        }
        else
        {
            throw new AtomCastException(Short.toString(value), NativeType.SHORT.toQName(), NativeType.UNSIGNED_SHORT.toQName(), FORG0001);
        }
    }

    /**
     * First cast to xs:string then cast to the target type.
     */
    private static <A> A castThroughString(final A sourceAtom, final NativeType targetType, final CastingContext castingContext, final ComponentProvider pcx, final AtomBridge<A> atomBridge) throws AtomCastException
    {
        try
        {
            return castAs(castAs(sourceAtom, NativeType.STRING, castingContext, pcx, atomBridge), targetType, castingContext, pcx, atomBridge);
        }
        catch (final AtomCastException e)
        {
            final QName sourceType = atomBridge.getDataType(sourceAtom);
            throw new AtomCastException(e.getSourceValue(), sourceType, targetType.toQName(), FORG0001, e);
        }
    }

    private static <A> A castWithinBranchAs(final A sourceAtom, final NativeType targetType, final ComponentProvider pcx, final AtomBridge<A> atomBridge) throws AtomCastException
    {
        PreCondition.assertArgumentNotNull(sourceAtom, "sourceAtom");
        PreCondition.assertArgumentNotNull(targetType, "targetType");

        final Type type = pcx.getTypeDefinition(targetType);
        if (null != type)
        {
            if (type.isAtomicType())
            {
                final SimpleType atomicType = (SimpleType)type;
                try
                {
                    final List<A> atoms = atomicType.validate(atomBridge.wrapAtom(sourceAtom), atomBridge);
                    final int size = atoms.size();
                    if (1 == size)
                    {
                        return atoms.get(0);
                    }
                    else if (0 == size)
                    {
                        return null;
                    }
                    else
                    {
                        // Atomic type should not be yielding multiple atoms.
                        throw new AssertionError();
                    }
                }
                catch (final DatatypeException e)
                {
                    throw new AtomCastException(atomBridge.getC14NForm(sourceAtom), atomBridge.getDataType(sourceAtom), e.getType(), FORG0001, e);
                }
            }
            else
            {
                throw new IllegalArgumentException(targetType + " dataType is not an atomic type.");
            }
        }
        else
        {
            throw new IllegalArgumentException(targetType + " dataType could not be found in the processing context.");
        }
    }

    /**
     * Returns the decimal as {http://www.w3.org/2001/XMLSchema}decimal('canonical')
     */
    private static String constructionString(final BigDecimal decval)
    {
        return "{".concat(XMLConstants.W3C_XML_SCHEMA_NS_URI).concat("}decimal('").concat(NumericSupport.formatDecimalC14N(decval)).concat("')");
    }

    private CastingSupport()
    {
        throw new AssertionError();
    }
}
