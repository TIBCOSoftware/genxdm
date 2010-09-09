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

import java.text.ChoiceFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gxml.i18n.LocalizableMessage;
import org.gxml.i18n.MessageLocalizer;
import org.gxml.i18n.ResourceBundleMessage;

@SuppressWarnings("serial")
public final class FileCountException extends Exception implements LocalizableMessage
{
	private static final String BUNDLE_NAME = "org.gxml.tests.i18n.FileCount"; //$NON-NLS-1$

	private int numFiles;
	private String diskName;

	FileCountException(final int numFiles, final String diskName)
	{
		super("There are " + numFiles + " on the disk " + diskName + "."); //$NON-NLS-1$
		this.numFiles = numFiles;
		this.diskName = diskName;
	}

	public int getNumFiles()
	{
		return numFiles;
	}

	public String getDiskName()
	{
		return diskName;
	}

	// Using an inner class here so that we don't pollute the outer API.
	private class FileCountMessage implements ResourceBundleMessage
	{
		public String getPatternKey()
		{
			return "files";
		}

		public Object[] getArguments(final ResourceBundle messages)
		{
			return new Object[] { numFiles, diskName, numFiles };
		}

		public Format[] getFormat(final ResourceBundle messages)
		{
			final double[] limits = { 0, 1, 2 };
			final String[] formats = { messages.getString("noFiles"), messages.getString("oneFile"), messages.getString("multipleFiles") };

			final ChoiceFormat cf = new ChoiceFormat(limits, formats);

			return new Format[] { cf, null, NumberFormat.getInstance() };
		}
	}

	@Override
	public String getLocalizedMessage()
	{
		return getLocalizedMessage(Locale.getDefault());
	}

	public String getLocalizedMessage(final Locale locale)
	{
		return MessageLocalizer.getLocalizedMessage(new FileCountMessage(), locale, ResourceBundle.getBundle(BUNDLE_NAME, locale));
	}
}
