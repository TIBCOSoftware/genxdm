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
package org.genxdm.bridgekit.xs.simple;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;

public class GregorianType extends AbstractAtomType
{
    private final NativeType nativeType;

    public GregorianType(final NativeType nativeType, final QName name, final SimpleType baseType)
    {
        super(name, baseType);
        this.nativeType = PreCondition.assertArgumentNotNull(nativeType, "nativeType");
    }

    public void accept(SequenceTypeVisitor visitor)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Iterable<EnumerationDefinition> getEnumerations()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Facet getFacetOfKind(FacetKind facetKind)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Iterable<Facet> getFacets()
    {
        return Collections.emptyList();
    }

    public Set<DerivationMethod> getFinal()
    {
        return Collections.emptySet();
    }

    public NativeType getNativeType()
    {
        return nativeType;
    }

    public Iterable<Pattern> getPatterns()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public ScopeExtent getScopeExtent()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public WhiteSpacePolicy getWhiteSpacePolicy()
    {
        return WhiteSpacePolicy.COLLAPSE;
    }

    public boolean hasEnumerations()
    {
        return false;
    }

    public boolean hasFacetOfKind(final FacetKind facetKind)
    {
        return false;
    }

    public boolean hasFacets()
    {
        return false;
    }

    public boolean hasPatterns()
    {
        return false;
    }

    public boolean isAbstract()
    {
        return false;
    }

    public boolean isID()
    {
        return false;
    }

    public boolean isIDREF()
    {
        return false;
    }

    public <A> List<A> validate(final String initialValue, AtomBridge<A> atomBridge) throws DatatypeException
    {
        final String normalized = normalize(initialValue);

        final XMLGregorianCalendar calendar = castAsGregorian(normalized, nativeType, atomBridge);

        final int year = calendar.getYear();
        final int month = calendar.getMonth();
        final int dayOfMonth = calendar.getDay();
        final int hour = calendar.getHour();
        final int minute = calendar.getMinute();
        final int second = calendar.getSecond();
        final int millis = 0;
        final BigDecimal remainderSecond = calendar.getFractionalSecond();
        final int offsetInMinutes = calendar.getTimezone();

        switch (nativeType)
        {
            case DATETIME:
            {
                checkForLeapSecond(normalized, second);
                return atomBridge.wrapAtom(atomBridge.createDateTime(year, month, dayOfMonth, hour, minute, second, millis, remainderSecond, offsetInMinutes));
            }
            case DATE:
            {
                return atomBridge.wrapAtom(atomBridge.createDate(year, month, dayOfMonth, offsetInMinutes));
            }
            case TIME:
            {
                checkForLeapSecond(normalized, second);
                return atomBridge.wrapAtom(atomBridge.createTime(hour, minute, second, millis, remainderSecond, offsetInMinutes));
            }
            case GDAY:
            {
                return atomBridge.wrapAtom(atomBridge.createDay(dayOfMonth, offsetInMinutes));
            }
            case GMONTH:
            {
                return atomBridge.wrapAtom(atomBridge.createMonth(month, offsetInMinutes));
            }
            case GMONTHDAY:
            {
                return atomBridge.wrapAtom(atomBridge.createMonthDay(month, dayOfMonth, offsetInMinutes));
            }
            case GYEAR:
            {
                return atomBridge.wrapAtom(atomBridge.createYear(year, offsetInMinutes));
            }
            case GYEARMONTH:
            {
                return atomBridge.wrapAtom(atomBridge.createYearMonth(year, month, offsetInMinutes));
            }
            default:
            {
                throw new AssertionError(nativeType);
            }
        }
    }

    private int checkForLeapSecond(final String normalized, final int second) throws DatatypeException
    {
        if (second >= 0 && second <= 59)
        {
            return second;
        }
        else
        {
            // Leap seconds often encountered.
            throw new DatatypeException(normalized, this);
        }
    }

    public <A> List<A> validate(String initialValue, PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException
    {
        return validate(initialValue, bridge);
    }

    private <A> XMLGregorianCalendar castAsGregorian(final String strval, final NativeType dataType, AtomBridge<A> atomBridge) throws DatatypeException
    {
        final String trimmed = strval.trim();
        // ISO8601.parseGregorian(trimmed, atomBridge, this);
        try
        {
            final XMLGregorianCalendar calendar = datatypeFactory.newXMLGregorianCalendar(trimmed);

            final NameSource nameBridge = NameSource.SINGLETON;

            final QName sourceName = calendar.getXMLSchemaType();
            final NativeType sourceType = nameBridge.nativeType(sourceName);
            if (dataType == sourceType)
            {
                if (DatatypeConstants.FIELD_UNDEFINED != calendar.getTimezone())
                {
                    if (trimmed.endsWith("60"))
                    {
                        throw new DatatypeException(strval, this);
                    }
                }

                if (dataType == NativeType.TIME)
                {
                    if (calendar.getHour() == 24)
                    {
                        // We could validate that minute and hour components are zero,
                        // but that seems like over-specification. Leave that to W3C.
                        calendar.setHour(0);
                    }
                }
                return calendar;
            }
            else
            {
                throw new DatatypeException(strval, this);
            }
        }
        catch (final IllegalArgumentException e)
        {
            throw new DatatypeException(strval, this);
        }
    }

    private static DatatypeFactory datatypeFactory;
    
    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
