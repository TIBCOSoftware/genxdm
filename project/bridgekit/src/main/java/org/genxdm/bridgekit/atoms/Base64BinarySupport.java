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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * base64Binary using the base 64 algorithm.
 * <p/>
 * This class provides a number of static utility functions useful in encoding and decoding materials received in a MIME
 * message. This codec handles Base64.
 * 
 */
public final class Base64BinarySupport
{
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
     * Warning: the supplied string must be valid base 64 (a subset of US-ASCII, correctly padded). The algorithm is forgiving about line length and line ending style, however.
     * 
     * @param base64
     *            the properly aligned and padded string to be decoded.
     * @param enc
     *            the encoding name to use for transforming the decoded bytes into a String.
     * @return a decoded String, if possible, based on the specified encoding
     * @throws IllegalArgumentException
     *             if the String cannot be parsed as base64 (contains illegal bytes, is not padded to a multiple of four bytes, etc).
     */
    public static String decodeBase64(String base64, String enc) throws UnsupportedEncodingException
    {
        return new String(decodeBase64(base64), enc);
    }

    /**
     * Warning: the supplied string must be valid base 64 (a subset of US-ASCII, correctly padded). The algorithm is forgiving about line length and line ending style, however.
     * 
     * @param base64
     *            the properly aligned and padded string to be decoded.
     * @return a byte array containing the decoded bytes
     * @throws IllegalArgumentException
     *             if the String cannot be parsed as base64 (contains illegal bytes, is not padded to a multiple of four bytes, etc).
     */
    public static byte[] decodeBase64(String base64)
    {
        byte[] riff = new byte[3];
        int range = 0;
        //int index = 0;
        // probably wrong, but it will be too big by a little.
        ByteArrayOutputStream baos = new ByteArrayOutputStream(base64.length()/4*3); //arrayLength(base64));
        //byte [] result = new byte[arrayLength(base64)];
        for (int i = 0; i < base64.length(); i++)
        {
            char c = base64.charAt(i);
            if (c == BASE64_PAD)
            {
                // one to two of the bytes are
                // valid. If there are two pad
                // characters, range will be 2,
                // and only one byte is valid.
                // if there is only one pad character,
                // then range should be 3, and
                // the first two bytes are valid.
                if (range < 2)
                    throw new IllegalArgumentException("Short block at offset " + i);
                for (int j = 0; j < range - 1; j++)
                    baos.write(riff[j]);
//                {
//                    result[index] = riff[j];
//                    index++;
//                }
                // ensure that decoded characters haven't spilled over -- sign of illegal encodings
                for (int j = range - 1; j < 3; j++)
                {
                    if (0 != riff[j])
                    {
                        i -= range - 1;
                        c = base64.charAt(i);
                        throw new IllegalArgumentException("Illegal character " + c + " at offset " + i);
                    }
                }
                if (2 == range)
                {// second PAD expected
                    ++i;
                    char cc = base64.charAt(i);
                    if (isWhiteChar(cc))
                        ++i;
                    if (i >= base64.length() || BASE64_PAD != base64.charAt(i))
                        throw new IllegalArgumentException("Short padding at offset " + i);
                }
                // range is now reached; no need to complain
                range = 0;
                // when we encounter padding, stop -- make sure the stream ended here
                while (++i < base64.length())
                {
                    c = base64.charAt(i);
                    if (!isWhiteChar(c))
                        throw new IllegalArgumentException("Illegal characters after padding '" + c + "'");
                }
                break;
            }
            // ignore whitespace...
            if (isWhiteChar(c))
                continue;
            // ...not the rest
            byte index2 = getBase64Value(c);
            if (index2 >= 0)
            {
                if (range == 0)
                {
                    range++;
                    riff[0] = (byte)(index2 << 2);
                }
                else if (range == 1)
                {
                    range++;
                    riff[0] |= (index2 >> 4);
                    riff[1] = (byte)(index2 << 4);
                }
                else if (range == 2)
                {
                    range++;
                    riff[1] |= (index2 >> 2);
                    riff[2] = (byte)(index2 << 6);
                }
                else
                {
                    range = 0;
                    riff[2] |= index2;
                    for (int j = 0; j < riff.length; j++)
                    {
//                        result[index] = riff[j];
//                        index++;
                        baos.write(riff[j]);
                        riff[j] = 0;// reset after use !
                    }
                }
            }
            else
                throw new IllegalArgumentException("Illegal character '" + c + "' at offset " + i);
        }
        if (range != 0)
            throw new IllegalArgumentException("Encoded data not in multiples of four");
//        return result;
        return baos.toByteArray();
    }
    
    // nah. too likely to be wrong to spend time doing it.
    /**
     * Implementation detail: package access. Get the base length
     * by dividing by 4, multiplying by 3; subtract 0 if there's no pad
     * character, 1 if there's 1 pad character, and two if there's two
     * pad characters, but throw an exception if the third-from last
     * character is a pad.
     * @return length of the expected output
     */
//    static int arrayLength(String base64)
//    {
//        final int length = base64.length();
//        if (base64.charAt(length - 1) == BASE64_PAD) // -1
//        {
//            if (base64.charAt(length - 2) == BASE64_PAD) // -2
//            {
//                if (base64.charAt(length - 3) == BASE64_PAD) // oops
//                    throw new IllegalArgumentException("More than two trailing pad characters");
//                // implicit else: -2
//                return (length / 4 * 3) - 2;
//            }
//            // implicit else: -1
//            return (length / 4 * 3) - 1;
//        }
//        // implicit else: last character not pad
//        return length / 4 * 3;
//    }

    /**
     * Implementation detail: package access. AS has shown by profiling that these two methods are faster than table
     * lookup. Note that the Base64 Table remains defined in the constants, as it is useful as documentation (if nothing
     * else).
     * 
     * @param sixBit
     *            an integer in the six-bit (0-63) range. Only six bits should be set.
     * @return the character corresponding to this value in the base64 algorithm.
     */
    static char getBase64Char(int sixBit)
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
     * lookup.
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

    private static final char BASE64_PAD = '=';
}
