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
package org.genxdm.bridgekit.xs;

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
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmEnumeration;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmPattern;
import org.genxdm.xs.resolve.SmPrefixResolver;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;

public class GregorianType<A> extends AbstractAtomType<A>
{
	private final SmNativeType nativeType;

	public GregorianType(final SmNativeType nativeType, final QName name, final SmSimpleType<A> baseType, final AtomBridge<A> atomBridge)
	{
		super(name, baseType, atomBridge);
		this.nativeType = PreCondition.assertArgumentNotNull(nativeType, "nativeType");
	}

	public void accept(SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean derivedFrom(String namespace, String name, Set<SmDerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmEnumeration<A>> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmFacet<A> getFacetOfKind(SmFacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmFacet<A>> getFacets()
	{
		return Collections.emptyList();
	}

	public Set<SmDerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	public SmNativeType getNativeType()
	{
		return nativeType;
	}

	public Iterable<SmPattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		return SmWhiteSpacePolicy.COLLAPSE;
	}

	public boolean hasEnumerations()
	{
		return false;
	}

	public boolean hasFacetOfKind(final SmFacetKind facetKind)
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

	public List<A> validate(final String initialValue) throws SmDatatypeException
	{
		final String normalized = normalize(initialValue);

		final XMLGregorianCalendar calendar = castAsGregorian(normalized, nativeType);

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

	private int checkForLeapSecond(final String normalized, final int second) throws SmDatatypeException
	{
		if (second >= 0 && second <= 59)
		{
			return second;
		}
		else
		{
			// Leap seconds often encountered.
			throw new SmDatatypeException(normalized, this);
		}
	}

	public List<A> validate(String initialValue, SmPrefixResolver resolver) throws SmDatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	private XMLGregorianCalendar castAsGregorian(final String strval, final SmNativeType dataType) throws SmDatatypeException
	{
		final String trimmed = strval.trim();
		// ISO8601.parseGregorian(trimmed, atomBridge, this);
		try
		{
			final DatatypeFactory factory = getFactory();
			final XMLGregorianCalendar calendar = factory.newXMLGregorianCalendar(trimmed);

			final NameSource nameBridge = atomBridge.getNameBridge();

			final QName sourceName = calendar.getXMLSchemaType();
			final SmNativeType sourceType = nameBridge.nativeType(sourceName);
			if (dataType == sourceType)
			{
				if (DatatypeConstants.FIELD_UNDEFINED != calendar.getTimezone())
				{
					if (trimmed.endsWith("60"))
					{
						throw new SmDatatypeException(strval, this);
					}
				}

				if (dataType == SmNativeType.TIME)
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
				throw new SmDatatypeException(strval, this);
			}
		}
		catch (final IllegalArgumentException e)
		{
			throw new SmDatatypeException(strval, this);
		}
	}

	private static DatatypeFactory getFactory()
	{
		try
		{
			return DatatypeFactory.newInstance();
		}
		catch (final DatatypeConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
