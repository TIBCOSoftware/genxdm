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
package org.genxdm.processor.w3c.xs.validation.regex.impl.string;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPatternInput;

/**
 * CharPatternInput is a PatternInput that treats each
 * character of a string as a separate token.
 * FIXME need to check unicode escapes.
 */
final class StringRegExPatternInput implements RegExPatternInput<StringRegExPatternTerm, String>
{
    private String text;        // the original text
    private int position;        // our position within text
    private String nextChar;    // the char returned by peek
    private boolean escaped;    // whether the char is escaped or not

    /**
     * creates a <@link StringPatternInput} from the given string
     */
    public StringRegExPatternInput(final String s)
    {
        text = s;
        position = 0;
        cueNext();
    }

    /**
     * internal method to handle character escaping
     */
    private void cueNext()
    {
        escaped = false;
        if (position < text.length())
        {
            nextChar = text.substring(position, position + 1);
        }
    }

    /**
     * returns the next token without advancing passed it
     */
    public String peek()
    {
        return nextChar;
    }

    /**
     * returns the next token and advances passed it
     */
    public String next()
    {
        final String cur = nextChar;
        position += escaped ? 2 : 1;
        cueNext();
        return cur;
    }

    /**
     * returns if there are any more tokens.
     */
    public boolean hasNext()
    {
        return position < text.length();
    }

    /**
     * Tells the input which PatternTerms matched the last
     * item provided by peekNext.
     *
     * @param terms Iterable list of PatternTerm.
     */
    public void matchedPeek(final Iterable<StringRegExPatternTerm> terms)
    {
        // don't care about matchers
        /*
        for (final PatternTerm<String> term : terms)
        {
            System.out.println(term);
        }
        */
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}

