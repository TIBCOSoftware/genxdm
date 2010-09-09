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

import java.util.Date;
import java.util.Locale;

/**
 * JUnit test case for the {@link SpaceshipException} message.
 */
public class SpaceshipTestCase extends TestCase
{
    public void testMe()
    {
        final Locale localeUS = new Locale("en", "US");
        final Locale localeFR = new Locale("fr", "FR");

        try
        {
            throw new SpaceshipException(new Date(0L), 7, "mars");
        }
        catch (final SpaceshipException e)
        {
            assertTrue(e.getLocalizedMessage().contains("December 31, 1969 we detected 7 spaceships on the planet Mars."));
            assertTrue(e.getLocalizedMessage(localeUS).contains("on December 31, 1969 we detected 7 spaceships on the planet Mars."));
            assertTrue(e.getLocalizedMessage(localeFR).contains("de 31 d\u00e9cembre 1969, d\u00e9tections nous 7 spaceships sur le planet Mars."));
        }
    }
}
