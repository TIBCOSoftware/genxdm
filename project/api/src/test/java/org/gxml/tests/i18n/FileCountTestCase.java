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

import junit.framework.TestCase;

import java.util.Locale;

import org.gxml.tests.i18n.FileCountException;


public class FileCountTestCase extends TestCase
{
    public void testMe()
    {
        final Locale localeUS = new Locale("en", "US");
        final Locale localeFR = new Locale("fr", "FR");
        final Locale localeDE = new Locale("de", "DE");

//      String baseName = StripQualifiers.getPackageName(Localize.class) + ".Messages";
//      System.out.println("baseName = " + baseName);

        try
        {
            throw new FileCountException(0, "C:");
        }
        catch (final FileCountException e)
        {
            compare("", "There are no files on C:.", e.getLocalizedMessage());
            compare("", "There are no files on C:.", e.getLocalizedMessage(localeUS));
            compare("", "Il n'y a pas des fichiers sur C:.", e.getLocalizedMessage(localeFR));
            compare("", "There are no files on C:.", e.getLocalizedMessage(localeDE));
        }

        try
        {
            throw new FileCountException(1, "D:");
        }
        catch (final FileCountException e)
        {
            compare("", "There is one file on D:.", e.getLocalizedMessage());
            compare("", "There is one file on D:.", e.getLocalizedMessage(localeUS));
            compare("", "Il y a un fichier sur D:.", e.getLocalizedMessage(localeFR));
            compare("", "There is one file on D:.", e.getLocalizedMessage(localeDE));
        }

        try
        {
            throw new FileCountException(2, "E:");
        }
        catch (final FileCountException e)
        {
            compare("", "There are 2 files on E:.", e.getLocalizedMessage());
            compare("", "There are 2 files on E:.", e.getLocalizedMessage(localeUS));
            compare("", "Il y a 2 fichiers sur E:.", e.getLocalizedMessage(localeFR));
            compare("", "There are 2 files on E:.", e.getLocalizedMessage(localeDE));
        }
    }

    private void compare(final String message, final String expect, final String actual)
    {
        if (!expect.equals(actual))
        {
            System.out.println("expect=" + expect);
            System.out.println("actual=" + actual);
        }
        assertEquals(message, expect, actual);
    }
}
