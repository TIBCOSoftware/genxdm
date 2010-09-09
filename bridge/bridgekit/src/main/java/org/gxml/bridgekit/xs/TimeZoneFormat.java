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
package org.gxml.bridgekit.xs;

final class TimeZoneFormat
{
	private static final int TIMEZONE_LENGTH = 6;
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int MINUTES_PER_HOUR = 60;

	/**
	 * Returns a timezone string with the format Z|(+|-)HH:MM
	 * 
	 * @param offsetInMillis
	 *            The offset expressed in integral milliseconds.
	 * @param zuluEnabled
	 *            Determines whether Z is an allowable representation for zero offset.
	 */
	public static String getTimeZoneString(final int offsetInMillis, final boolean zuluEnabled)
	{
		final int offsetInSeconds = (offsetInMillis) / 1000;

		final int offsetInMinutes = offsetInSeconds / SECONDS_PER_MINUTE;

		final int hours = offsetInMinutes / MINUTES_PER_HOUR;
		final int minutes = offsetInMinutes % MINUTES_PER_HOUR;

		return getTimeZoneString(hours, minutes, zuluEnabled);
	}

	/**
	 * Returns a timezone string with the format Z|(+|-)HH:MM with optional Z.
	 * 
	 * @param hours
	 *            The hours component of the timezone.
	 * @param minutes
	 *            The minutes component of the timezone.
	 * @param zuluEnabled
	 *            Determines whether Z is an allowable representation for zero offset.
	 */
	private static String getTimeZoneString(final int hours, final int minutes, final boolean zuluEnabled)
	{
		return getTimeZoneString(hours, minutes, zuluEnabled, true);
	}

	/**
	 * Returns a timezone string with the format Z|(+|-)HH:MM with optional Z and +.
	 * 
	 * @param hours
	 *            The hours component of the timezone.
	 * @param minutes
	 *            The minutes component of the timezone.
	 * @param zuluEnabled
	 *            Determines whether Z is an allowable representation for zero offset.
	 * @param plusEnabled
	 *            Determines whether + should be displayed for zero and positive offsets.
	 */
	private static String getTimeZoneString(int hours, int minutes, final boolean zuluEnabled, boolean plusEnabled)
	{
		StringBuilder buffer;

		if (hours < 0)
		{
			if (minutes < 0)
			{
				buffer = new StringBuilder(TIMEZONE_LENGTH);

				buffer.append('-');

				hours *= (-1);
				minutes *= (-1);
			}
			else if (minutes == 0)
			{
				buffer = new StringBuilder(TIMEZONE_LENGTH);

				buffer.append('-');

				hours *= (-1);
			}
			else
			{
				throw new RuntimeException();
			}
		}
		else if (hours == 0)
		{
			if (minutes < 0)
			{
				buffer = new StringBuilder(TIMEZONE_LENGTH);

				buffer.append('-');

				minutes *= (-1);
			}
			else if (minutes == 0)
			{
				if (zuluEnabled)
				{
					return "Z";
				}
				else
				{
					buffer = new StringBuilder(TIMEZONE_LENGTH);

					if (plusEnabled)
					{
						buffer.append('+');
					}

					minutes *= (-1);
				}
			}
			else
			{
				buffer = new StringBuilder(TIMEZONE_LENGTH);

				if (plusEnabled)
				{
					buffer.append('+');
				}
			}
		}
		else
		{
			if (minutes < 0)
			{
				throw new RuntimeException();
			}
			else
			{
				buffer = new StringBuilder(TIMEZONE_LENGTH);

				if (plusEnabled)
				{
					buffer.append('+');
				}
			}
		}

		addTwoDigits(hours, buffer);
		buffer.append(':');
		addTwoDigits(minutes, buffer);

		return buffer.toString();
	}

	private static void addTwoDigits(final int posValue, final StringBuilder buffer)
	{
		if (posValue < 10)
		{
			buffer.append('0');
			buffer.append(posValue);
		}
		else if (posValue < 100)
		{
			buffer.append(posValue);
		}
		else
		{
			throw new RuntimeException();
		}
	}
}
