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
package org.gxml.tests.i18n;

import java.text.Format;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gxml.i18n.LocalizableException;
import org.gxml.i18n.ResourceBundleMessage;

/**
 * @see <a href="http://java.sun.com/docs/books/tutorial/i18n/format/messageFormat.html"></a>
 */
@SuppressWarnings("serial")
public final class SpaceshipException extends LocalizableException
{
	// Using an inner class here so that we don't pollute the outer API.
	private final class SpaceshipMessage implements ResourceBundleMessage
	{
		public Object[] getArguments(final ResourceBundle messages)
		{
			return new Object[] { messages.getString(m_where), m_howMany, m_when };
		}

		public Format[] getFormat(final ResourceBundle messages)
		{
			return null;
		}

		public String getPatternKey()
		{
			// This corresponds to the key in the resource file.
			return "template";
		}
	}

	// This would normally go in a base class for a bunch of exceptions
	// sharing the same properties file.
	private static final String BUNDLE_NAME = "org.gxml.tests.i18n.Spaceship"; //$NON-NLS-1$

	private int m_howMany;

	private Date m_when;

	private String m_where;

	/**
	 * Initializer for a Spaceship sighting.
	 * 
	 * @param when
	 *            The date and time of the sighting.
	 * @param howMany
	 *            How many spaceships were sighted.
	 * @param where
	 *            The planet in English and lower case.
	 */
	public SpaceshipException(final Date when, final int howMany, final String where)
	{
		this.m_when = when;
		this.m_howMany = howMany;
		this.m_where = where;
	}

	public int getHowMany()
	{
		return m_howMany;
	}

	protected ResourceBundle getResourceBundle(final Locale locale)
	{
		return ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	protected ResourceBundleMessage getResourceBundleMessage()
	{
		return new SpaceshipMessage();
	}

	public Date getWhen()
	{
		return m_when;
	}

	public String getWhere()
	{
		return m_where;
	}
}