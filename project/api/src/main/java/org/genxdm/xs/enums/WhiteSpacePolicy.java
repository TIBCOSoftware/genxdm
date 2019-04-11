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
package org.genxdm.xs.enums;

import org.genxdm.exceptions.PreCondition;

/**
 * An enumeration of the whitespace policy for a Simple Type Definition.
 */
public enum WhiteSpacePolicy
{
    COLLAPSE
    {
        /**
         * <b>whitespace collapsing</b> <br>
         * The action of applying whitespace replacement, trimming the leading and trailing spaces, and replacing all the sequences of contiguous whitespaces by a single space between the parsed and lexical spaces. Most of the simple datatypes apply whitespace
         * collapsing.
         */
        public String apply(final String initialValue)
        {
            return collapseWhiteSpace(initialValue);
        }
    },
    PRESERVE
    {
        public String apply(final String initialValue)
        {
            return preserveWhiteSpace(initialValue);
        }
    },
    REPLACE
    {
        /**
         * <b>whitespace replacement</b> <br>
         * The action of replacing all the occurrences of the characters #x9 (tab), #xA (line-feed), and #xD (carriage return) by a #x20 (space) between the parsed and the lexical space. Whitespace replacement does not change the lenth of the string. xs:normalizedString
         * and the user-defined simple types derived from xs:string and xs:normalizedString (for which the xs:whitespace facet is "replace") are the only datatypes that apply whitespace replacement without collapsing.
         */
        public String apply(final String initialValue)
        {
            return replaceWhiteSpace(initialValue);
        }
    };

    public boolean isCollapse()
    {
        return this == COLLAPSE;
    }

    public boolean isPreserve()
    {
        return this == PRESERVE;
    }

    public boolean isReplace()
    {
        return this == REPLACE;
    }

    public abstract String apply(String initialValue);

    public static String collapseWhiteSpace(String initialValue)
    {
        PreCondition.assertArgumentNotNull(initialValue, "initialValue");
        // Note. We make no distinction between control characters less than ASCII 32
        // because we don't expect them in XML strings. We assume they are not there,
        // taken out by parsing. If we had to behave otherwise, we would end up
        // with annoying exception semantics and slow code.

        // trim() is our secret weapon #1. It costs virtually nothing because String
        // uses a special constructor to share the char[] value, so we only pay for the
        // String wrapper - pretty cheap by comparison. From now on, we only have to worry
        // about embedded whitespace, which allows the mainline (non whitespace) part of
        // the loop to be devoid of tests.
        final String trimmed = initialValue.trim();

        int trimLength = trimmed.length();

        if (trimLength > 0)
        {
            if (trimLength < 3)
            {
                return trimmed;
            }
            else
            {
                // Fall through to handle (possibly) embedded whitespace.
            }
        }
        else
        {
            // It's all whitespace
            return "";
        }

        // Assume that this transformation is a no-op unless we discover otherwise.
        boolean noop = true;

        // Keep track of the number of characters required for the new char[] buffer.
        // Count down from the trimmed length to get this operation out of the mainline.
        int newLength = trimLength;

        // Used to detect consecutive whitespace characters.
        boolean inWhite = false;

        // For this loop, we only need to iterate over index > 0, index < trimLength -1
        // because we know that the first and last character are not whitespace.
        final int endIndex = trimLength - 1;

        for (int index = 1; index < endIndex; index++)
        {
            char c = trimmed.charAt(index);
            if (c <= ' ')
            {
                if (inWhite)
                {
                    // Detected consecutive (embedded) whitespace characters that must be skipped.
                    noop = false;

                    newLength--;
                }
                else
                {
                    if (c < ' ')
                    {
                        // Detected an (embedded) whitespace character that needs replacing.
                        noop = false;
                    }

                    inWhite = true;
                }
            }
            else
            {
                inWhite = false;
            }
        }

        if (noop)
        {
            return trimmed;
        }
        else
        {
            char[] sb = new char[newLength];

            // The first character is not whitespace, so if we pre-process it,...
            sb[0] = trimmed.charAt(0);

            // 1. We are not in whitespace
            inWhite = false;

            // 2. Count begins at One.
            // Be careful, this count also acts as an index in this loop.
            int count = 1;

            // 3. Iterate over trimmed characters excluding the first and last.
            for (int index = 1; index < endIndex; index++)
            {
                char c = trimmed.charAt(index);
                if (c <= ' ')
                {
                    if (inWhite)
                    {
                        // Skip
                    }
                    else
                    {
                        sb[count++] = ' ';
                        inWhite = true;
                    }
                }
                else
                {
                    inWhite = false;
                    sb[count++] = c;
                }
            }

            // The last character is also not whitespace.
            sb[count++] = trimmed.charAt(endIndex);

            return new String(sb, 0, count);
        }
    }

    public static String preserveWhiteSpace(String initialValue)
    {
        PreCondition.assertArgumentNotNull(initialValue, "initialValue");
        return initialValue;
    }

    public static String replaceWhiteSpace(String initialValue)
    {
        PreCondition.assertArgumentNotNull(initialValue, "initialValue");
        return initialValue.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
