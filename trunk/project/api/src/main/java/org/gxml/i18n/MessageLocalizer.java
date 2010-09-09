/**
 * Copyright (c) 2004-2010 TIBCO Software Inc.
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
package org.gxml.i18n;

import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for obtaining a localized message given a {@link ResourceBundleMessage}.
 */
public final class MessageLocalizer
{
    /**
     * Localizes a locale-independent message using the specified locale and a resource bundle.
     *
     * @param msg      The locale-independent message.
     * @param locale   The locale to use for pattern substitution and argument
     *                 formatting.
     * @param messages The resource bundle.
     * @return A <CODE>String</CODE> representing the localized message.
     */
    public static String getLocalizedMessage(final ResourceBundleMessage msg, final Locale locale, final ResourceBundle messages)
    {
        // Create a MessageFormat object. I'm not sure why using an empty string works;
        // The Sun Java Tutorial uses this technique.
        final MessageFormat formatter = new MessageFormat("");

        // Set the Locale because we may be using objects that need to be formatted in a locale-sensitive manner.
        formatter.setLocale(locale);

        // The ResourceBundleMessage provides the message key, arguments and formats; How Object Oriented is that?
        final String patternKey = msg.getPatternKey();

        try
        {
            formatter.applyPattern(messages.getString(patternKey));
        }
        catch (final MissingResourceException e)
        {
            // TODO: Logging. There's something rotten in Denmark.
            return patternKey;
        }

        final Format[] formats = msg.getFormat(messages);
        if (null != formats)
        {
            formatter.setFormats(formats);
        }

        // Do this in two lines so that the datatypes are manifest.
        final Object[] messageArguments = msg.getArguments(messages);
        return formatter.format(messageArguments);
    }
}
