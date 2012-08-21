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
package org.genxdm.processor.w3c.xs.regex.impl.string;

final class CharRange
{
    private int first;
    private int last;

    public CharRange(final int c)
    {
        first = c;
        last = c;
    }

    public CharRange(final int first, final int last)
    {
        this.first = first;
        this.last = last;
    }

    public void setLast(final int last)
    {
        this.last = last;
    }

    public int getFirst()
    {
        return first;
    }

    public int getLast()
    {
        return last;
    }

    public boolean matches(final int c)
    {
        return c >= first && c <= last;
    }

    public boolean matches(final String str)
    {
        if (null != str)
        {
            return str.length() == 1 && matches(str.charAt(0));
        }
        else
        {
            // TODO; null is a bit klunky. Could we ask the grammar what it wants to use as a zero-length sequence?
            return false;
        }
    }

    public String toString()
    {
        if (first == last)
        {
            return StringRegExPatternTerm.charString(first);
        }
        else
        {
            return StringRegExPatternTerm.charString(first) + "-" + StringRegExPatternTerm.charString(last);
        }
    }
}
