package org.genxdm.processor.w3c.xs.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlForeignAtom;
import org.genxdm.exceptions.GxmlAtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.components.ComponentProvider;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.NativeType;

public class CanonicalAtomBridge
    implements AtomBridge<XmlAtom>
{

    CanonicalAtomBridge(ComponentProvider provider)
    {
        this.components = provider;
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
        throws GxmlAtomCastException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom castAs(XmlAtom sourceAtom, NativeType targetType, CastingContext castingContext)
        throws GxmlAtomCastException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom compile(String srcval, NativeType dataType)
        throws GxmlAtomCastException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom compile(String srcval, NativeType dataType, PrefixResolver resolver)
        throws GxmlAtomCastException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createBase64Binary(byte[] base64BinaryValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createBoolean(boolean booleanValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createByte(byte byteValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDate(int year, int month, int dayOfMonth, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDateTime(int year, int month, int dayOfMonth,
                                  int hour, int minute, int second, int millis,
                                  BigDecimal remainderSecond, int offsetInMinutes)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDay(int dayOfMonth, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDayTimeDuration(BigDecimal seconds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDecimal(BigDecimal decimalValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDecimal(long decimalValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDouble(double value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createDuration(int yearMonthDuration, BigDecimal dayTimeDuration)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createFloat(float floatValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createHexBinary(byte[] hexBinaryValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createInt(int intValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createInteger(BigInteger value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createInteger(long value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createIntegerDerived(BigInteger value, NativeType nativeType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createIntegerDerived(long value, NativeType nativeType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createLong(long longValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createMonth(int month, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createMonthDay(int month, int dayOfMonth, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createNOTATION(String namespaceURI, String localName, String prefix)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createQName(String namespaceURI, String localName, String prefix)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createShort(short shortValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createString(String strval)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createStringDerived(String strval, NativeType nativeType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createTime(int hourOfDay, int minute, int second,
                              int millis, BigDecimal fractionalSecond, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createUntypedAtomic(String strval)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createURI(URI uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createYear(int year, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createYearMonth(int year, int month, int timezone)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom createYearMonthDuration(int months)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBase64Binary(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getBoolean(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public XmlAtom getBooleanFalse()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom getBooleanTrue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte getByte(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getC14NForm(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getC14NString(List<? extends XmlAtom> atoms)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QName getDataType(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getDayOfMonth(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BigDecimal getDecimal(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDouble(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDurationTotalMonths(XmlAtom duration)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BigDecimal getDurationTotalSeconds(XmlAtom duration)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getFloat(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BigDecimal getFractionalSecondPart(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getGmtOffset(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] getHexBinary(XmlAtom arg)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getHourOfDay(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInt(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BigInteger getInteger(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getIntegralSecondPart(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getLocalNameFromQName(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLong(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMinute(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMonth(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public NameSource getNameBridge()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNamespaceFromQName(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XmlAtom getNativeAtom(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NativeType getNativeType(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QName getNotation(XmlAtom notation)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPrefixFromQName(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QName getQName(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getSecondsAsBigDecimal(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getShort(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getString(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getUnsignedByte(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getUnsignedInt(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUnsignedShort(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public URI getURI(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXPath10Form(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXQuery10Form(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getYear(XmlAtom gregorian)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isAtom(Object object)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isForeignAtom(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWhiteSpace(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public XmlAtom makeForeignAtom(QName atomType, XmlAtom baseAtom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<XmlAtom> wrapAtom(XmlAtom atom)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private final ComponentProvider components;
}
