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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.Type;

abstract class AbstractDurationType<A> extends AbstractAtomType<A>
{
	public static final int HOURS_PER_DAY = 24;
	public static final long HOURS_PER_DAY_LONG = 24;
	public static final int MINUTES_PER_HOUR = 60;
	public static final long MINUTES_PER_HOUR_LONG = 60l;
	public static final int MONTHS_PER_YEAR = 12;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final long SECONDS_PER_MINUTE_LONG = 60l;

	private static int months(boolean negative, int years, int months)
	{
		int normalized = years * MONTHS_PER_YEAR + months;

		if (negative)
		{
			return normalized * -1;
		}
		else
		{
			return normalized;
		}
	}

	/**
	 * Calculate high-precision seconds for dayTimeDuration.
	 */
	private static BigDecimal seconds(final boolean negative, final long days, final long hours, final long minutes, final BigDecimal seconds)
	{
		long value = days * HOURS_PER_DAY_LONG;

		value = (value + hours) * MINUTES_PER_HOUR_LONG;

		value = (value + minutes) * SECONDS_PER_MINUTE_LONG;

		BigDecimal result = seconds != null ? seconds.add(BigDecimal.valueOf(value)) : BigDecimal.valueOf(value);

		if (negative)
		{
			return result.negate();
		}
		else
		{
			return result;
		}
	}

	public AbstractDurationType(final QName name, final Type<A> baseType, final AtomBridge<A> atomBridge)
	{
		super(name, baseType, atomBridge);
	}

	public final void accept(SequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public final boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public final Iterable<EnumerationDefinition<A>> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public final Facet<A> getFacetOfKind(FacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public final Iterable<Facet<A>> getFacets()
	{
		return Collections.emptyList();
	}

	public final Set<DerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	public final Iterable<Pattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public final ScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public final WhiteSpacePolicy getWhiteSpacePolicy()
	{
		return WhiteSpacePolicy.COLLAPSE;
	}

	public final boolean hasEnumerations()
	{
		return false;
	}

	public final boolean hasFacetOfKind(final FacetKind facetKind)
	{
		return false;
	}

	public final boolean hasFacets()
	{
		return false;
	}

	public final boolean hasPatterns()
	{
		return false;
	}

	public final boolean isAbstract()
	{
		return false;
	}

	public final boolean isID()
	{
		return false;
	}

	public final boolean isIDREF()
	{
		return false;
	}

	private final A parseDuration(final String srcval, final NativeType targetType) throws DatatypeException
	{
		PreCondition.assertArgumentNotNull(srcval, "srcval");
		PreCondition.assertArgumentNotNull(targetType, "targetType");
		PreCondition.assertTrue(targetType.isA(getNativeType()), targetType + " instance of " + getNativeType());
		// Cache the length; we'll use it later to see if we have parsed the entire string.
		final int length = srcval.length();
		if (length == 0)
		{
			throw new DatatypeException(srcval, this);
		}

		int fromIndex = 0;

		final int indexSign = srcval.indexOf((int)'-', fromIndex);

		boolean negative;
		int year = 0;
		int month = 0;
		long day = 0L;
		long hour = 0L;
		long minute = 0L;
		BigDecimal second;

		if (indexSign == 0)
		{
			negative = true;

			fromIndex = indexSign + 1;
		}
		else if (indexSign < 0)
		{
			negative = false;
		}
		else
		{
			throw new DatatypeException(srcval, this);
		}

		int indexP = srcval.indexOf((int)'P', fromIndex);

		if (indexP == fromIndex)
		{
			fromIndex = indexP + 1;
		}
		else
		{
			// "must start with 'P' (period)";
			throw new DatatypeException(srcval, this);
		}

		// Determine the position of the date/time separator, if any, so that we can check that
		// the components occur in the appropriate place.
		final int indexT = srcval.indexOf((int)'T', fromIndex);

		final int indexY = srcval.indexOf((int)'Y', fromIndex);

		if (indexY >= 0)
		{
			if (indexT >= 0)
			{
				if (indexY >= indexT)
				{
					// 'Y' designator cannot be in the time area;
					throw new DatatypeException(srcval, this);
				}
			}

			try
			{
				year = Integer.parseInt(srcval.substring(fromIndex, indexY));
			}
			catch (final NumberFormatException e)
			{
				throw new DatatypeException(srcval, this);
			}

			fromIndex = indexY + 1;
		}

		int indexMo = srcval.indexOf((int)'M', fromIndex);

		if (indexMo >= 0)
		{
			if ((indexT >= 0) && (indexMo > indexT))
			{
				// We've just found the 'M' designation corresponding to Minute.
				indexMo = -1;
			}
			else
			{
				try
				{
					month = Integer.parseInt(srcval.substring(fromIndex, indexMo));
				}
				catch (final NumberFormatException e)
				{
					throw new DatatypeException(srcval, this);
				}

				fromIndex = indexMo + 1;
			}
		}

		final int indexD = srcval.indexOf((int)'D', fromIndex);

		if (indexD >= 0)
		{
			if (indexT >= 0)
			{
				if (indexD >= indexT)
				{
					// 'D' designator cannot be in the time area";
					throw new DatatypeException(srcval, this);
				}
			}

			try
			{
				day = Long.parseLong(srcval.substring(fromIndex, indexD));
			}
			catch (final NumberFormatException e)
			{
				throw new DatatypeException(srcval, this);
			}

			fromIndex = indexD + 1;
		}

		if (indexT >= 0)
		{
			fromIndex = indexT + 1;
		}

		final int indexH = srcval.indexOf((int)'H', fromIndex);

		if (indexH >= 0)
		{
			try
			{
				hour = Long.parseLong(srcval.substring(fromIndex, indexH));
			}
			catch (final NumberFormatException e)
			{
				throw new DatatypeException(srcval, this);
			}

			fromIndex = indexH + 1;
		}

		final int indexMi = srcval.indexOf((int)'M', fromIndex);

		if (indexMi >= 0)
		{
			try
			{
				minute = Long.parseLong(srcval.substring(fromIndex, indexMi));
			}
			catch (final NumberFormatException e)
			{
				throw new DatatypeException(srcval, this);
			}

			fromIndex = indexMi + 1;
		}

		final int indexS = srcval.indexOf((int)'S', fromIndex);

		if (indexS >= 0)
		{
			try
			{
				second = new BigDecimal(srcval.substring(fromIndex, indexS));
			}
			catch (final RuntimeException e)
			{
				throw new DatatypeException(srcval, this);
			}

			fromIndex = indexS + 1;
		}
		else
		{
			second = BigDecimal.ZERO;
		}

		// Make sure that we have parsed the entire string.
		if (fromIndex == length)
		{
			final boolean hasHMS = (indexH >= 0) || (indexMi >= 0) || (indexS >= 0);
			// Make sure that if the 'T" separator is present then we have at least one hour,minute or second component.
			if (indexT >= 0 && !hasHMS)
			{
				throw new DatatypeException(srcval, this);
			}
			final boolean hasYearMonth = (indexY >= 0) || (indexMo >= 0);
			final boolean hasDayTime = (indexD >= 0) || hasHMS;

			if (hasYearMonth)
			{
				if (hasDayTime)
				{
					// xs:duration
					switch (targetType)
					{
						case DURATION:
						{
							return atomBridge.createDuration(months(negative, year, month), seconds(negative, day, hour, minute, second));
						}
						case DURATION_DAYTIME:
						case DURATION_YEARMONTH:
						{
							throw new DatatypeException(srcval, this);
						}
						default:
						{
							throw new AssertionError(targetType);
						}
					}
				}
				else
				{
					// xs:yearMonthDuration
					switch (targetType)
					{
						case DURATION_YEARMONTH:
						{
							return atomBridge.createYearMonthDuration(months(negative, year, month));
						}
						case DURATION:
						{
							return atomBridge.createDuration(months(negative, year, month), BigDecimal.ZERO);
						}
						case DURATION_DAYTIME:
						{
							throw new DatatypeException(srcval, this);
						}
						default:
						{
							throw new AssertionError(targetType);
						}
					}
				}
			}
			else
			{
				if (hasDayTime)
				{
					// xs:dayTimeDuration
					switch (targetType)
					{
						case DURATION_DAYTIME:
						{
							return atomBridge.createDayTimeDuration(seconds(negative, day, hour, minute, second));
						}
						case DURATION:
						{
							return atomBridge.createDuration(0, seconds(negative, day, hour, minute, second));
						}
						case DURATION_YEARMONTH:
						{
							throw new DatatypeException(srcval, this);
						}
						default:
						{
							throw new AssertionError(targetType);
						}
					}
				}
				else
				{
					throw new DatatypeException(srcval, this);
				}
			}
		}
		else
		{
			// Gibberish on the end.
			throw new DatatypeException(srcval, this);
		}
	}

	public final List<A> validate(final String initialValue) throws DatatypeException
	{
		try
		{
			// Note that trimming eliminates a leading plus-sign, but leaves leading minus-sign.
			final String trimmed = trim(initialValue);
			return atomBridge.wrapAtom(parseDuration(trimmed, getNativeType()));
		}
		catch (final NumberFormatException e)
		{
			throw new DatatypeException(initialValue, this);
		}
	}

	public final List<A> validate(String initialValue, PrefixResolver resolver) throws DatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
}
