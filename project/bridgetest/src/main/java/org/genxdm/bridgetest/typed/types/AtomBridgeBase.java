package org.genxdm.bridgetest.typed.types;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.genxdm.bridgekit.xs.CanonicalCastingContext;
import org.genxdm.bridgetest.typed.TypedTestBase;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.types.NativeType;
import org.junit.Test;

public abstract class AtomBridgeBase<N, A>
    extends TypedTestBase<N, A>
{
    @Test
    public void createDerivedIntegers()
    {
        final NativeType [] derivedIntTypes = { NativeType.NON_NEGATIVE_INTEGER, NativeType.UNSIGNED_LONG,
              NativeType.UNSIGNED_INT, NativeType.UNSIGNED_SHORT, NativeType.UNSIGNED_BYTE,
              NativeType.POSITIVE_INTEGER, NativeType.NEGATIVE_INTEGER, NativeType.NON_POSITIVE_INTEGER };
        // we want this to fail when they're given values out of range.
        AtomBridge<A> bridge = getTypedContext(null).getAtomBridge();
        for (final NativeType type : derivedIntTypes)
        {
            // iterate over NON_NEGATIVE_INTEGER and the four unsigned: test -1.
            // test upper bound of four unsigned types
            // POSITIVE_INTEGER: test 0
            // NEGATIVE_INTEGER: test 0
            // NON_POSITIVE_INTEGER: test 1
            // smallest bounds:
            switch (type)
            {
                case NON_NEGATIVE_INTEGER:
                case UNSIGNED_LONG:
                case UNSIGNED_INT:
                case UNSIGNED_SHORT:
                case UNSIGNED_BYTE:
                    testCreateDerivedInteger(bridge, type, BigInteger.ONE.negate());
                    break;
                case POSITIVE_INTEGER:
                case NEGATIVE_INTEGER:
                    testCreateDerivedInteger(bridge, type, BigInteger.ZERO);
                    break;
                case NON_POSITIVE_INTEGER:
                    testCreateDerivedInteger(bridge, type, BigInteger.ONE);
                    break;
                default:
            }
            // largest bounds:
            switch (type)
            {
                case UNSIGNED_LONG:
                    testCreateDerivedInteger(bridge, type, UNSIGNED_LONG_MAX_VALUE.add(BigInteger.ONE));
                    break;
                case UNSIGNED_INT:
                    testCreateDerivedInteger(bridge, type, UNSIGNED_INT_MAX_VALUE.add(BigInteger.ONE));
                    break;
                case UNSIGNED_SHORT:
                    testCreateDerivedInteger(bridge, type, UNSIGNED_SHORT_MAX_VALUE.add(BigInteger.ONE));
                    break;
                case UNSIGNED_BYTE:
                    testCreateDerivedInteger(bridge, type, UNSIGNED_BYTE_MAX_VALUE.add(BigInteger.ONE));
                    break;
                default:
                    // do nothing
            }
        }
    }

    @Test
    public void castingNumeric()
    {
        // test numeric casting: every numeric type to every other numeric type
        // pay attention at the boundaries: min, min-1, max, max+1 for target type
        // optional (maybe later): other types? with numeric as target only, though
        AtomBridge<A> bridge = getTypedContext(null).getAtomBridge();
        for (final NativeType targetType : numericTypes)
        {
            for (final NativeType sourceType : numericTypes)
            {
                testCast(bridge, sourceType, targetType);
            }
        }
    }
    
    @Test
    public void castingString()
    {
        // test string casting: every string type to every other string type
        // pay attention to the constraints. this is all less common than numbers
        // optional/later: untypedatomic (or include in main bit here?) and
        // other things being cast TO string
    }
    
    @Test
    public void castingBoolean()
    {
        // test casting of every other type to boolean
        // maybe/later: test casting of boolean to other types (esp numeric)
    }
    
    @Test
    public void castingMiscellaneous()
    {
        // possibly including duration, dates, qname, anyuri, binary unsure
    }
    
    // other test cases: creation of each type
    private void testCreateDerivedInteger(final AtomBridge<A> bridge, final NativeType type, final BigInteger badValue)
    {
        try
        {
            A value = bridge.createIntegerDerived(badValue, type);
            fail("unexpectedly able to create a derived integer of type "+type+" with value '"+bridge.getC14NForm(value)+"' from BigInteger("+badValue.toString()+")");
        }
        catch (GenXDMException gxe)
        {
            // do nothing: expected
        }
    }
    // get()-ing each type
    // the atom() method, and wrap/unwrap?
    
    private void testCast(final AtomBridge<A> bridge, final NativeType sourceType, final NativeType targetType)
    {
        final CastingContext cc = new CanonicalCastingContext();
        for (final A sourceValue : generateGoodNumericTestValues(bridge, sourceType, targetType))
        {
            try
            {
                final A targetValue = bridge.castAs(sourceValue, targetType, cc);
                assertNotNull("good cast failed (null return) from "+sourceType+"("+bridge.getC14NForm(sourceValue)+") to "+targetType, targetValue);
//                System.out.println("good cast succeeded from "+sourceType+"("+bridge.getC14NForm(sourceValue)+") to "+targetType+"("+bridge.getC14NForm(targetValue)+")");
            }
            catch (AtomCastException ace)
            {
                ace.printStackTrace();
                fail("good cast failed with atom cast exception: "+ace.getMessage());
            }
        }
        for (final A sourceValue : generateBadNumericTestValues(bridge, sourceType, targetType))
        {
            try
            {
                final A targetValue = bridge.castAs(sourceValue, targetType, cc);
                fail("bad cast succeeded unexpectedly from "+sourceType+"("+bridge.getC14NForm(sourceValue)+") to "+targetType+"("+bridge.getC14NForm(targetValue)+")");
            }
            catch (AtomCastException ace)
            {
//                ace.printStackTrace();
//                System.out.println("bad cast failed from "+sourceType+"("+bridge.getC14NForm(sourceValue)+") to "+targetType);
            }
        }
    }
    
    private Iterable<A> generateGoodNumericTestValues(final AtomBridge<A> bridge, final NativeType sourceType, final NativeType targetType)
    {
        // target type gives us the constraints that we want to test (the boundaries or other edge cases)
        // for this method, return ones just inside boundaries
        // source type tells us the type to create, which gives us the method and arguments to use
        // TODO: there has *got* to be a more elegant way to do this shit
        List<A> result = new ArrayList<A>();
        switch (targetType)
        {
            case FLOAT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.negate().floatValue()));
                        break;
                    case DOUBLE:
                        // these two fail; apparently we can't quite get double to not be overprecise?
//                        result.add(bridge.createDouble(FLOAT_MIN_VALUE_AS_DECIMAL.doubleValue()));
//                        result.add(bridge.createDouble(FLOAT_MIN_VALUE_AS_DECIMAL.negate().doubleValue()));
                        // and these two. double doesn't represent boundary values for float well.
//                        result.add(bridge.createDouble(FLOAT_MAX_VALUE_AS_DECIMAL.doubleValue()));
//                        result.add(bridge.createDouble(FLOAT_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(FLOAT_MIN_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(FLOAT_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(FLOAT_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(FLOAT_MAX_VALUE_AS_DECIMAL.negate()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(FLOAT_MAX_VALUE.negate()));
                        result.add(bridge.createInteger(FLOAT_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(FLOAT_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(FLOAT_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(FLOAT_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(FLOAT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case DOUBLE:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.negate().floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.negate().doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.negate()));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case DECIMAL: // everything is representable; check boundaries
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.negate().floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.negate().doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        break;
                    case DECIMAL: // *shrug*
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.negate()));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case INTEGER: // everything is representable with fractions dropped: check boundaries
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.negate().floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.negate().doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.negate()));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case NON_POSITIVE_INTEGER: // integer 0 to -inf
                switch (sourceType)
                {
                    case LONG:
                        result.add(bridge.createLong(BigInteger.ZERO.longValue()));
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(BigInteger.ZERO.intValue()));
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(BigInteger.ZERO.shortValue()));
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BigInteger.ZERO.byteValue()));
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(BigDecimal.ZERO.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.negate().doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        result.add(bridge.createDouble(BigDecimal.ZERO.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.negate()));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case POSITIVE_INTEGER: // no overlap
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case NEGATIVE_INTEGER: // integer -1 to -inf
                switch (sourceType)
                {
                    case LONG:
                        result.add(bridge.createLong(MINUS_ONE.longValue()));
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(MINUS_ONE.intValue()));
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(MINUS_ONE.shortValue()));
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(MINUS_ONE.byteValue()));
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.negate().floatValue()));
                        result.add(bridge.createFloat(BigDecimal.ONE.negate().floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        result.add(bridge.createDouble(BigDecimal.ONE.negate().doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(BigDecimal.ONE.negate()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(MINUS_ONE));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.negate()));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER: // no overlap
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate(), sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // no overlap
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case POSITIVE_INTEGER: // integer 1 to inf
                switch (sourceType)
                {
                    case LONG:
                        result.add(bridge.createLong(BigInteger.ONE.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(BigInteger.ONE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(BigInteger.ONE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BigInteger.ONE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(BigDecimal.ONE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(BigDecimal.ONE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(BigDecimal.ONE));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ONE));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                    case NEGATIVE_INTEGER: // no overlap
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case NON_NEGATIVE_INTEGER: // integer 0 to inf : supercase of register unsigned
                switch (sourceType)
                {
                    case LONG:
                        result.add(bridge.createLong(BigInteger.ZERO.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(BigInteger.ZERO.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(BigInteger.ZERO.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BigInteger.ZERO.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(FLOAT_MIN_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(FLOAT_MAX_VALUE_AS_DECIMAL.floatValue()));
                        result.add(bridge.createFloat(BigDecimal.ZERO.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(DOUBLE_MIN_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(DOUBLE_MAX_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(BigDecimal.ZERO.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER: // no overlap
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case LONG:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(LONG_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(LONG_MIN_VALUE.floatValue()));
                        result.add(bridge.createFloat(LONG_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        // double loses sufficient precision at this value's magnitude that a double created with long_min_value is out of range when cast
//                        result.add(bridge.createDouble(LONG_MIN_VALUE.doubleValue()));
                        // so is max value. *sigh*
//                        result.add(bridge.createDouble(LONG_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(LONG_MIN_VALUE.longValue()));
                        result.add(bridge.createDecimal(LONG_MAX_VALUE.longValue()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(LONG_MIN_VALUE));
                        result.add(bridge.createInteger(LONG_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(LONG_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(LONG_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(LONG_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // two cases that can be combined!
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case INT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(INT_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(INT_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(INT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(INT_MIN_VALUE.floatValue()));
                        // doesn't work, because the lossy conversion returns a value one greater than int_max_value. weird, huh?
//                        result.add(bridge.createFloat(INT_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(INT_MIN_VALUE.doubleValue()));
                        result.add(bridge.createDouble(INT_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(INT_MIN_VALUE.longValue()));
                        result.add(bridge.createDecimal(INT_MAX_VALUE.longValue()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(INT_MIN_VALUE));
                        result.add(bridge.createInteger(INT_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(INT_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(INT_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(INT_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // three cases that can be combined!
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case SHORT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(SHORT_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(SHORT_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(SHORT_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(SHORT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(SHORT_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(SHORT_MIN_VALUE.floatValue()));
                        result.add(bridge.createFloat(SHORT_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(SHORT_MIN_VALUE.doubleValue()));
                        result.add(bridge.createDouble(SHORT_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(SHORT_MIN_VALUE.longValue()));
                        result.add(bridge.createDecimal(SHORT_MAX_VALUE.longValue()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(SHORT_MIN_VALUE));
                        result.add(bridge.createInteger(SHORT_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(SHORT_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(SHORT_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(SHORT_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // four cases that can be combined!
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case BYTE:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(BYTE_MIN_VALUE.longValue()));
                        result.add(bridge.createLong(BYTE_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(BYTE_MIN_VALUE.intValue()));
                        result.add(bridge.createInt(BYTE_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(BYTE_MIN_VALUE.shortValue()));
                        result.add(bridge.createShort(BYTE_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BYTE_MIN_VALUE.byteValue()));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(BYTE_MIN_VALUE.floatValue()));
                        result.add(bridge.createFloat(BYTE_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BYTE_MIN_VALUE.doubleValue()));
                        result.add(bridge.createDouble(BYTE_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BYTE_MIN_VALUE.longValue()));
                        result.add(bridge.createDecimal(BYTE_MAX_VALUE.longValue()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BYTE_MIN_VALUE));
                        result.add(bridge.createInteger(BYTE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BYTE_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BYTE_MIN_VALUE, sourceType));
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(BYTE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // five cases that can be combined!
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_LONG:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(0l));
                        result.add(bridge.createLong(LONG_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(0));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort((short)0));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte((byte)0));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(BigInteger.ZERO.floatValue()));
                        result.add(bridge.createFloat(UNSIGNED_LONG_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigInteger.ZERO.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_LONG_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_LONG_MAX_VALUE)));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        result.add(bridge.createInteger(UNSIGNED_LONG_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER: // no overlap from negative integer to unsigned anything
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // two cases combined
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_INT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(0l));
                        result.add(bridge.createLong(UNSIGNED_INT_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(0));
                        result.add(bridge.createInt(INT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort((short)0));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte((byte)0));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(BigInteger.ZERO.floatValue()));
                        // float fails by not allowing unsigned int max value rather than allowing bigger things (prolly)
//                        result.add(bridge.createFloat(UNSIGNED_INT_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigInteger.ZERO.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_INT_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_INT_MAX_VALUE)));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        result.add(bridge.createInteger(UNSIGNED_INT_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER: // no overlap from negative integer to unsigned anything
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // three cases can be combined
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_SHORT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(0l));
                        result.add(bridge.createLong(UNSIGNED_SHORT_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(0));
                        result.add(bridge.createInt(UNSIGNED_SHORT_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort((short)0));
                        result.add(bridge.createShort(SHORT_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte((byte)0));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(BigInteger.ZERO.floatValue()));
                        result.add(bridge.createFloat(UNSIGNED_SHORT_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigInteger.ZERO.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_SHORT_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_SHORT_MAX_VALUE)));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        result.add(bridge.createInteger(UNSIGNED_SHORT_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER: // no overlap from negative integer to unsigned anything
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // four cases can be combined
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE, sourceType));
                        break;
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_BYTE:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(0l));
                        result.add(bridge.createLong(UNSIGNED_BYTE_MAX_VALUE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(0));
                        result.add(bridge.createInt(UNSIGNED_BYTE_MAX_VALUE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort((short)0));
                        result.add(bridge.createShort(UNSIGNED_BYTE_MAX_VALUE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte((byte)0));
                        result.add(bridge.createByte(BYTE_MAX_VALUE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(BigInteger.ZERO.floatValue()));
                        result.add(bridge.createFloat(UNSIGNED_BYTE_MAX_VALUE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigInteger.ZERO.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_BYTE_MAX_VALUE.doubleValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_BYTE_MAX_VALUE)));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        result.add(bridge.createInteger(UNSIGNED_BYTE_MAX_VALUE));
                        break;
                    case NON_POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    case NEGATIVE_INTEGER: // no overlap from negative integer to unsigned anything
                        break;
                    case POSITIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // five cases can be combined
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            default:
                fail("Unexpected target type: "+targetType);
        }
        return result;
    }
    
    private Iterable<A> generateBadNumericTestValues(final AtomBridge<A> bridge, final NativeType sourceType, final NativeType targetType)
    {
        // target type gives us the constraints that we want to test (the boundaries or other edge cases)
        // for this method, return ones just OUTside boundaries
        // source type tells us the type to create, which gives us the method and arguments to use
        List<A> result = new ArrayList<A>();
        switch (targetType)
        {
            case FLOAT:
                switch (sourceType)
                {
                    // primitive types first: none have un-representable-as-float values
                    case LONG:
                    case INT:
                    case SHORT:
                    case BYTE:
                    case FLOAT: // and neither does float
                        break;
                    case DOUBLE: // on the other hand, just the very boundaries of float are unrepresentable as double when cast back to float:
                        // these two fail; apparently we can't quite get double to not be overprecise?
                        result.add(bridge.createDouble(FLOAT_MIN_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(FLOAT_MIN_VALUE_AS_DECIMAL.negate().doubleValue()));
                        // and these two. double doesn't represent boundary values for float well.
                        result.add(bridge.createDouble(FLOAT_MAX_VALUE_AS_DECIMAL.doubleValue()));
                        result.add(bridge.createDouble(FLOAT_MAX_VALUE_AS_DECIMAL.negate().doubleValue()));
                        break;
                    case DECIMAL: // and of course, if float min/max fail when cast through double, we can expect double min/max to fail when passed as decimal
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate()));
                        break;
                    case INTEGER:
                        // might not fail by just adding/subtracting one
                        result.add(bridge.createInteger(FLOAT_MAX_VALUE.negate().subtract(BigInteger.ONE)));
                        result.add(bridge.createInteger(FLOAT_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER:
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(FLOAT_MAX_VALUE.negate().subtract(BigInteger.ONE), sourceType));
                        break;
                    case POSITIVE_INTEGER:
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(FLOAT_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_LONG: // register types are too small to overflow
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case DOUBLE:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG: // register types don't have the range to not be represented as double (lossily, but still)
                    case INT:
                    case SHORT:
                    case BYTE:
                    case FLOAT: // and float upcasts
                    case DOUBLE: // and double is double; it can't represent a value out of its own range
                        break;
                    case DECIMAL:
                        // we should do even more microscopic values?
//                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL));
//                        result.add(bridge.createDecimal(DOUBLE_MIN_VALUE_AS_DECIMAL.negate()));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.add(BigDecimal.ONE)));
                        result.add(bridge.createDecimal(DOUBLE_MAX_VALUE_AS_DECIMAL.negate().subtract(BigDecimal.ONE)));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.negate().subtract(BigInteger.ONE)));
                        result.add(bridge.createInteger(DOUBLE_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER:
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.negate().subtract(BigInteger.ONE), sourceType));
                        break;
                    case POSITIVE_INTEGER:
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(DOUBLE_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_LONG: // register types are too small to overflow
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case DECIMAL: // there is no way I can think of to present an unrepresentable-as-bigdecimal value to a cast-to-bigdecimal method. it's the bigliest biggun.
            case INTEGER: // similarly, there is no (integer) value that cannot be represented by biginteger, and casting to biginteger discards fractions.
                // so do nothing; there is no cast-fails test
                break;
            case NON_POSITIVE_INTEGER: // positive values break: test 1
                switch (sourceType)
                {
                    case FLOAT:
                        result.add(bridge.createFloat(BigDecimal.ONE.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigDecimal.ONE.doubleValue()));
                        break;
                    case LONG:
                        result.add(bridge.createLong(BigInteger.ONE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(BigInteger.ONE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(BigInteger.ONE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BigInteger.ONE.byteValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ONE));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ONE));
                        break;
                    case NON_POSITIVE_INTEGER: // nothing
                    case NEGATIVE_INTEGER: // also nothing
                        break;
                    case NON_NEGATIVE_INTEGER:
                    case POSITIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ONE, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case NEGATIVE_INTEGER: // non-negative values break: test 0
            case POSITIVE_INTEGER: // non-positive values break: test 0
                switch (sourceType)
                {
                    case FLOAT:
                        result.add(bridge.createFloat(BigDecimal.ZERO.floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigDecimal.ZERO.doubleValue()));
                        break;
                    case LONG:
                        result.add(bridge.createLong(BigInteger.ZERO.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(BigInteger.ZERO.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(BigInteger.ZERO.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(BigInteger.ZERO.byteValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ZERO));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BigInteger.ZERO));
                        break;
                    case NEGATIVE_INTEGER: // nothing
                    case POSITIVE_INTEGER:
                        break;
                    case NON_POSITIVE_INTEGER:
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BigInteger.ZERO, sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case NON_NEGATIVE_INTEGER: // negative values break: test -1
                switch (sourceType)
                {
                    case FLOAT:
                        result.add(bridge.createFloat(BigDecimal.ONE.negate().floatValue()));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BigDecimal.ONE.negate().doubleValue()));
                        break;
                    case LONG:
                        result.add(bridge.createLong(MINUS_ONE.longValue()));
                        break;
                    case INT:
                        result.add(bridge.createInt(MINUS_ONE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(MINUS_ONE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(MINUS_ONE.byteValue()));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BigDecimal.ONE.negate()));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(MINUS_ONE));
                        break;
                    case NON_POSITIVE_INTEGER:
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case NON_NEGATIVE_INTEGER: // do nothing: nothing can break
                    case POSITIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case LONG:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG: // nothing to be done for these three; they can't contain values out of range
                    case INT: 
                    case SHORT:
                    case BYTE:
                        break;
                    case FLOAT:
                        // float can't represent long min value accurately; subtract one and it's still in range
//                        result.add(bridge.createFloat(LONG_MIN_VALUE.floatValue() - (float)1.0));
                        // also can't represent long max value, but unlike int, here we are low, not high, so max+1 is still in range.
//                        result.add(bridge.createFloat(LONG_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(LONG_MIN_VALUE.doubleValue() - 1.0));
                        result.add(bridge.createDouble(LONG_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(new BigDecimal(LONG_MIN_VALUE.subtract(BigInteger.ONE))));
                        result.add(bridge.createDecimal(new BigDecimal(LONG_MAX_VALUE.add(BigInteger.ONE))));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(LONG_MIN_VALUE.subtract(BigInteger.ONE)));
                        result.add(bridge.createInteger(LONG_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine (no testable upper bound violation):
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(LONG_MIN_VALUE.subtract(BigInteger.ONE), sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine three cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                        result.add(bridge.createIntegerDerived(LONG_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_INT: // completely contained
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case INT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(INT_MIN_VALUE.longValue() - 1));
                        result.add(bridge.createLong(INT_MAX_VALUE.longValue() + 1));
                        break;
                    case INT: // nothing to be done for these three; they can't contain values out of range
                    case SHORT:
                    case BYTE:
                        break;
                    case FLOAT:
                        // okay, float is weird, because *this one* doesn't work either; it passes because subtracting 1.0 doesn't make it smaller than int_min_value
//                        result.add(bridge.createFloat(INT_MIN_VALUE.floatValue() - (float)1.0));
                        result.add(bridge.createFloat(INT_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(INT_MIN_VALUE.doubleValue() - 1.0));
                        result.add(bridge.createDouble(INT_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(INT_MIN_VALUE.longValue() - 1));
                        result.add(bridge.createDecimal(INT_MAX_VALUE.longValue() + 1));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(INT_MIN_VALUE.subtract(BigInteger.ONE)));
                        result.add(bridge.createInteger(INT_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine:
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(INT_MIN_VALUE.subtract(BigInteger.ONE), sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine four cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(INT_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_SHORT: // two cases completely contained:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case SHORT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(SHORT_MIN_VALUE.longValue() - 1));
                        result.add(bridge.createLong(SHORT_MAX_VALUE.longValue() + 1));
                        break;
                    case INT:
                        result.add(bridge.createInt(SHORT_MIN_VALUE.intValue() - 1));
                        result.add(bridge.createInt(SHORT_MAX_VALUE.intValue() + 1));
                    case SHORT: // nothing to be done for these three; they can't contain values out of range
                    case BYTE:
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(SHORT_MIN_VALUE.floatValue() - (float)1.0));
                        result.add(bridge.createFloat(SHORT_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(SHORT_MIN_VALUE.doubleValue() - 1.0));
                        result.add(bridge.createDouble(SHORT_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(SHORT_MIN_VALUE.longValue() - 1));
                        result.add(bridge.createDecimal(INT_MAX_VALUE.longValue() + 1));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(SHORT_MIN_VALUE.subtract(BigInteger.ONE)));
                        result.add(bridge.createInteger(SHORT_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine:
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(SHORT_MIN_VALUE.subtract(BigInteger.ONE), sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine five cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(SHORT_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_BYTE: // completely contained
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case BYTE:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(BYTE_MIN_VALUE.longValue() - 1));
                        result.add(bridge.createLong(BYTE_MAX_VALUE.longValue() + 1));
                        break;
                    case INT:
                        result.add(bridge.createInt(BYTE_MIN_VALUE.intValue() - 1));
                        result.add(bridge.createInt(BYTE_MAX_VALUE.intValue() + 1));
                        break;
                    case SHORT:
                        result.add(bridge.createShort((short)(BYTE_MIN_VALUE.shortValue() - 1)));
                        result.add(bridge.createShort((short)(BYTE_MAX_VALUE.shortValue() + 1)));
                        break;
                    case BYTE: 
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(BYTE_MIN_VALUE.floatValue() - (float)1.0));
                        result.add(bridge.createFloat(BYTE_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(BYTE_MIN_VALUE.doubleValue() - 1.0));
                        result.add(bridge.createDouble(BYTE_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(BYTE_MIN_VALUE.longValue() - 1));
                        result.add(bridge.createDecimal(BYTE_MAX_VALUE.longValue() + 1));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(BYTE_MIN_VALUE.subtract(BigInteger.ONE)));
                        result.add(bridge.createInteger(BYTE_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine:
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(BYTE_MIN_VALUE.subtract(BigInteger.ONE), sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine six cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        result.add(bridge.createIntegerDerived(BYTE_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_LONG:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(MINUS_ONE.longValue()));
                        break;
                    case INT: 
                        result.add(bridge.createInt(MINUS_ONE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(MINUS_ONE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(MINUS_ONE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(MINUS_ONE.floatValue()));
                        // float can't represent long max value, but unlike int, here we are low, not high, so max+1 is still in range.
//                        result.add(bridge.createFloat(UNSIGNED_LONG_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(MINUS_ONE.doubleValue()));
                        // double has lost precision and lets this one past, too.
//                        result.add(bridge.createDouble(UNSIGNED_LONG_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(new BigDecimal(MINUS_ONE)));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_LONG_MAX_VALUE.add(BigInteger.ONE))));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(MINUS_ONE));
                        result.add(bridge.createInteger(UNSIGNED_LONG_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine (-1 is in both sources, but not in the target)
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine two cases!
                    case NON_NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(UNSIGNED_LONG_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_LONG: // completely contained
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_INT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(MINUS_ONE.longValue()));
                        result.add(bridge.createLong(UNSIGNED_INT_MAX_VALUE.longValue() + 1l));
                        break;
                    case INT: 
                        result.add(bridge.createInt(MINUS_ONE.intValue()));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(MINUS_ONE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(MINUS_ONE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(MINUS_ONE.floatValue()));
                        // float can't represent long max value, but unlike int, here we are low, not high, so max+1 is still in range.
                        result.add(bridge.createFloat(UNSIGNED_INT_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(MINUS_ONE.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_INT_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(new BigDecimal(MINUS_ONE)));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_INT_MAX_VALUE.add(BigInteger.ONE))));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(MINUS_ONE));
                        result.add(bridge.createInteger(UNSIGNED_INT_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine (-1 is in both sources, but not in the target)
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine three cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG: 
                        result.add(bridge.createIntegerDerived(UNSIGNED_INT_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_INT: // completely contained
                    case UNSIGNED_SHORT:
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_SHORT:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(MINUS_ONE.longValue()));
                        result.add(bridge.createLong(UNSIGNED_SHORT_MAX_VALUE.longValue() + 1l));
                        break;
                    case INT: 
                        result.add(bridge.createInt(MINUS_ONE.intValue()));
                        result.add(bridge.createInt(UNSIGNED_SHORT_MAX_VALUE.intValue() + 1));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(MINUS_ONE.shortValue()));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(MINUS_ONE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(MINUS_ONE.floatValue()));
                        result.add(bridge.createFloat(UNSIGNED_SHORT_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(MINUS_ONE.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_SHORT_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(new BigDecimal(MINUS_ONE)));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_SHORT_MAX_VALUE.add(BigInteger.ONE))));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(MINUS_ONE));
                        result.add(bridge.createInteger(UNSIGNED_SHORT_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine (-1 is in both sources, but not in the target)
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine four cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                        result.add(bridge.createIntegerDerived(UNSIGNED_SHORT_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_SHORT: // completely contained
                    case UNSIGNED_BYTE:
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            case UNSIGNED_BYTE:
                switch (sourceType)
                {
                    // primitive types first
                    case LONG:
                        result.add(bridge.createLong(MINUS_ONE.longValue()));
                        result.add(bridge.createLong(UNSIGNED_BYTE_MAX_VALUE.longValue() + 1l));
                        break;
                    case INT: 
                        result.add(bridge.createInt(MINUS_ONE.intValue()));
                        result.add(bridge.createInt(UNSIGNED_BYTE_MAX_VALUE.intValue() + 1));
                        break;
                    case SHORT:
                        result.add(bridge.createShort(MINUS_ONE.shortValue()));
                        result.add(bridge.createShort((short)(UNSIGNED_BYTE_MAX_VALUE.shortValue() + 1)));
                        break;
                    case BYTE:
                        result.add(bridge.createByte(MINUS_ONE.byteValue()));
                        break;
                    case FLOAT:
                        result.add(bridge.createFloat(MINUS_ONE.floatValue()));
                        result.add(bridge.createFloat(UNSIGNED_BYTE_MAX_VALUE.floatValue() + (float)1.0));
                        break;
                    case DOUBLE:
                        result.add(bridge.createDouble(MINUS_ONE.doubleValue()));
                        result.add(bridge.createDouble(UNSIGNED_BYTE_MAX_VALUE.doubleValue() + 1.0));
                        break;
                    case DECIMAL:
                        result.add(bridge.createDecimal(new BigDecimal(MINUS_ONE)));
                        result.add(bridge.createDecimal(new BigDecimal(UNSIGNED_BYTE_MAX_VALUE.add(BigInteger.ONE))));
                        break;
                    case INTEGER:
                        result.add(bridge.createInteger(MINUS_ONE));
                        result.add(bridge.createInteger(UNSIGNED_BYTE_MAX_VALUE.add(BigInteger.ONE)));
                        break;
                    case NON_POSITIVE_INTEGER: // two cases combine (-1 is in both sources, but not in the target)
                    case NEGATIVE_INTEGER:
                        result.add(bridge.createIntegerDerived(MINUS_ONE, sourceType));
                        break;
                    case POSITIVE_INTEGER: // combine five cases!
                    case NON_NEGATIVE_INTEGER:
                    case UNSIGNED_LONG:
                    case UNSIGNED_INT:
                    case UNSIGNED_SHORT:
                        result.add(bridge.createIntegerDerived(UNSIGNED_BYTE_MAX_VALUE.add(BigInteger.ONE), sourceType));
                        break;
                    case UNSIGNED_BYTE: // completely contained
                        break;
                    default:
                        fail("Unexpected source type: "+sourceType);
                }
                break;
            default:
                fail("Unexpected target type: "+targetType);
        }
        return result;
    }
    
    private final NativeType [] numericTypes = { NativeType.FLOAT, NativeType.DOUBLE, NativeType.DECIMAL,
            NativeType.INTEGER, NativeType.NON_POSITIVE_INTEGER, NativeType.NEGATIVE_INTEGER,
            NativeType.LONG, NativeType.INT, NativeType.SHORT, NativeType.BYTE,
            NativeType.NON_NEGATIVE_INTEGER, NativeType.POSITIVE_INTEGER,
            NativeType.UNSIGNED_LONG, NativeType.UNSIGNED_INT, NativeType.UNSIGNED_SHORT, NativeType.UNSIGNED_BYTE };

    private static final BigInteger DOUBLE_MAX_VALUE = new BigInteger(new BigDecimal(Double.MAX_VALUE).toPlainString());
    private static final BigDecimal DOUBLE_MAX_VALUE_AS_DECIMAL = new BigDecimal(Double.toString(Double.MAX_VALUE));
    private static final BigInteger DOUBLE_MIN_VALUE = new BigInteger(new BigDecimal(Double.MAX_VALUE).negate().toPlainString());
    private static final BigDecimal DOUBLE_MIN_VALUE_AS_DECIMAL = new BigDecimal(Double.toString(Double.MIN_VALUE));
    private static final BigInteger FLOAT_MAX_VALUE = new BigInteger(new BigDecimal(Float.MAX_VALUE).toPlainString());
    private static final BigDecimal FLOAT_MAX_VALUE_AS_DECIMAL = new BigDecimal(Float.toString(Float.MAX_VALUE));
    private static final BigInteger FLOAT_MIN_VALUE = new BigInteger(new BigDecimal(Float.MAX_VALUE).negate().toPlainString());
    private static final BigDecimal FLOAT_MIN_VALUE_AS_DECIMAL = new BigDecimal(Float.toString(Float.MIN_VALUE));

    private static final BigInteger BYTE_MAX_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);
    private static final BigInteger BYTE_MIN_VALUE = BigInteger.valueOf(Byte.MIN_VALUE);
    private static final BigInteger SHORT_MAX_VALUE = BigInteger.valueOf(Short.MAX_VALUE);
    private static final BigInteger SHORT_MIN_VALUE = BigInteger.valueOf(Short.MIN_VALUE);
    private static final BigInteger INT_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger INT_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

    private static final BigInteger UNSIGNED_BYTE_MAX_VALUE = BigInteger.valueOf(((short)Byte.MAX_VALUE - (short)Byte.MIN_VALUE));
    private static final BigInteger UNSIGNED_INT_MAX_VALUE = BigInteger.valueOf(4294967295L);
    private static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger("18446744073709551615");
    private static final BigInteger UNSIGNED_SHORT_MAX_VALUE = BigInteger.valueOf(65535);
    private static final BigInteger MINUS_ONE = BigInteger.ONE.negate();
}
