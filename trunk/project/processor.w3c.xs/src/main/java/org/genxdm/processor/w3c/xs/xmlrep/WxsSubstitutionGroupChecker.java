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
package org.genxdm.processor.w3c.xs.xmlrep;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;



final class WxsSubstitutionGroupChecker
{
    public static  boolean detectCycles(final XMLElement element)
    {
        XMLElement current = element.substitutionGroup;
        while (null != current)
        {
            if (equal(current.getName(), element.getName()))
            {
                return true;
            }
            current = current.substitutionGroup;
        }
        return false;
    }

    public static  List<QName> computeCycles(final XMLElement element)
    {
        final List<QName> names = new LinkedList<QName>();
        boolean detected = false;

        XMLElement current = element.substitutionGroup;
        while (null != current)
        {
            names.add(current.getName());

            if (equal(current.getName(), element.getName()))
            {
                detected = true;
                break;
            }
            current = current.substitutionGroup;
        }

        if (!detected)
        {
            names.clear();
        }

        return names;
    }

    private static boolean equal(final QName x, final QName y)
    {
        return (null == x) ? (null == y) : x.equals(y);
    }
}
