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
package org.genxdm.bridgekit.atoms;

import java.io.UnsupportedEncodingException;

/**
 * base64Binary using the base 64 algorithm.
 * <p/>
 * This class provides a number of static utility functions useful in encoding and decoding materials received in a MIME
 * message. This codec handles Base64.
 * 
 */
final class Base64BinarySupport
{
    private static final char BASE64_PAD = '=';

    // private static final String BASE64_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * Warning: if the supplied string does not work out to a multiple of 3 bytes, then the returned string will contain
     * padding. If it has padding, then it cannot have another string concatenated on the end of it.
     * 
     * @param raw
     *            the String to be encoded
     * @return a String with length a multiple of four, which contains the base64 encoded version of the input with line
     *         breaks when needed and a closing line-break as well. Line breaks are CRLF as per the MIME RFC.
     * @see #encodeBase64(String, boolean) for the option of not breaking lines
     */
    public static String encodeBase64(String raw) throws UnsupportedEncodingException
    {
        return encodeBase64(raw, false);
    }

    /**
     * @param dontBreakLines
     *            if true a single line without line-ending is returned, otherwise lines are folded after the 76th
     *            character and a closing line break (CRLF) is appended at the end as well.
     * @see #encodeBase64(byte[], String) for the option of choosing line endings
     */
    public static String encodeBase64(String raw, boolean dontBreakLines) throws UnsupportedEncodingException
    {
        return encodeBase64(raw.getBytes("US-ASCII"), dontBreakLines);
    }

    /**
     * Base64 encode the bytes and fold the lines with CRLF.
     * 
     * @param raw
     * @return the encoded
     * @see #encodeBase64(byte[], boolean)
     */
    public static String encodeBase64(byte[] raw)
    {
        return encodeBase64(raw, "\r\n");
    }

    /**
     * Warning: if the supplied byte array does not have length a multiple of 3 bytes, then the returned string will
     * contain padding. If it has padding, then it cannot have another string concatenated on the end of it.
     * 
     * @param raw
     *            the byte array
     * @param dontBreakLines
     *            if false line breaks (CRLF) are inserted after 76th character as well as at the end of encoded data
     * @return a String with length a multiple of four, which contains the base64 encoded version of the input
     * @see #encodeBase64(byte[], String) for the option of choosing line endings
     */
    public static String encodeBase64(byte[] raw, boolean dontBreakLines)
    {
        return encodeBase64(raw, dontBreakLines ? null : "\r\n");
    }

    /**
     * Base-64 encode the data using the provided lineEnding
     * 
     * @param lineEnding
     *            to be used if not null -- in which case lines are not wrapped
     * @throws IllegalArgumentException
     *             if lineEnding is none of NEWLINE, CRLF or null/empty string
     * @see #encodeBase64(byte[], boolean)
     */
    public static String encodeBase64(byte[] raw, String lineEnding)
    {
        if (lineEnding == null || lineEnding.length() == 0)
        {
            lineEnding = null;
        }
        else if (!lineEnding.equals("\r\n") && !lineEnding.equals("\n"))
        {
            throw new IllegalArgumentException("Illegal value for lineEnding: \"" + lineEnding + "\"");
        }

        // refactor to remove repetition in remainder/pad handling ?
        StringBuilder sb = new StringBuilder();
        int lineCount = 0;
        for (int i = 0; i + 2 < raw.length; i += 3)
        {
            sb.append(getBase64Char((raw[i] >> 2) & 0x3F));
            sb.append(getBase64Char(((raw[i] & 0x03) << 4) | ((raw[i + 1] >> 4) & 0xF)));
            sb.append(getBase64Char(((raw[i + 1] & 0x0F) << 2) | ((raw[i + 2] >> 6) & 0x3)));
            sb.append(getBase64Char(raw[i + 2] & 0x3F));
            lineCount += 4;
            if (lineCount >= 76)
            {
                if (lineEnding != null)
                    sb.append(lineEnding);
                lineCount = 0;
            }
        }
        int remainder = raw.length % 3; // this many bytes left to encode
        if (remainder == 2)
        {
            sb.append(getBase64Char((raw[raw.length - 2] >> 2) & 0x3F)); // line repeated
            sb.append(getBase64Char(((raw[raw.length - 2] & 0x03) << 4) | ((raw[raw.length - 1] >> 4) & 0xF))); // line
            // repeated
            sb.append(getBase64Char((raw[raw.length - 1] & 0x0F) << 2));
            sb.append(BASE64_PAD);
        }
        else if (remainder == 1)
        {
            sb.append(getBase64Char((raw[raw.length - 1] >> 2) & 0x3F)); // line repeated
            sb.append(getBase64Char((raw[raw.length - 1] & 0x03) << 4));
            sb.append(BASE64_PAD).append(BASE64_PAD);
        }
        if (lineEnding != null)
            sb.append(lineEnding);
        return sb.toString();
    }

    /**
     * Implementation detail: package access. AS has shown by profiling that these two methods are faster than table
     * lookup. Note that the Base64 Table remains defined in the constants, as it is useful as documentation (if nothing
     * else).
     * 
     * @param sixBit
     *            an integer in the six-bit (0-63) range. Only six bits should be set.
     * @return the character corresponding to this value in the base64 algorithm.
     */
    public static char getBase64Char(int sixBit)
    {
        // note: should we change this to throw an exception when out-of-range
        // values appear? Or to return a special value, such as the byte-order mark?
        // (that's about as far out of range as I can imagine ...)
        if (sixBit < 26)
            return (char)('A' + sixBit);
        if (sixBit < 52)
            return (char)(('a' - 26) + sixBit);
        if (sixBit < 62)
            return (char)(('0' - 52) + sixBit);
        if (sixBit == 62)
            return '+';
        return '/';
    }

    /**
     * Implementation detail: package access. AS has shown by profiling that these two methods are faster than table
     * lookup. Note that the Base64 Table remains defined in the constants, as it is useful as documentation (if nothing
     * else).
     * 
     * @param c
     *            a valid character (one of 64 allowed characters) (padding is not decoded) in the base64 encoding
     * @return a value in the range -1..63, which is either six bits of a larger bit pattern (0-63), or an error
     *         indicator (-1).
     */
    static byte getBase64Value(char c)
    {
        if (c >= 'A' && c <= 'Z')
            return (byte)(c - 'A');
        if (c >= 'a' && c <= 'z')
            return (byte)(c - 'a' + 26);
        if (c >= '0' && c <= '9')
            return (byte)(c - '0' + 52);
        if (c == '+')
            return (byte)62;
        if (c == '/')
            return (byte)63;
        // ummm ... returning 0 for the padding character is ... ambiguous
        // it becomes impossible to distinguish final AAAA from AA== from
        // AAA=. None of which are terribly likely, perhaps (well, a final
        // null is probably not that uncommon, which is AA==).
        // if (c == '=') return (byte)0;
        // also, getBase64Char can't create the padding ....
        return (byte)-1; // should this throw an exception instead?
    }

    public static boolean isWhiteChar(char c)
    {
        switch (c)
        {
            case (' '):
            case ('\n'):
            case ('\t'):
            case ('\r'):
                return true;
            default:
                return false;
        }
    }
}
