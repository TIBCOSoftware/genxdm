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
package org.genxdm.bridgekit.xs.simple;

import org.genxdm.bridgekit.xs.BuiltInSchema;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.SimpleType;

/**
 * Encoding and decoding in base 16.
 */
final class HexCodec
{
    public static String encodeHex(final byte[] values)
    {
        final char[] buf = new char[(values.length << 1)];
        final int len = values.length;
        for (int i = 0; i < len; i++)
        {
            int b = values[i];
            if (b < 0)
                b = 256 + b;
            buf[(i << 1)] = hexchar(b >> 4);
            buf[(i << 1) + 1] = hexchar(b % 16);
        }
        return new String(buf);
    }
    
    public static byte [] decodeHex(final String s)
        throws DatatypeException
    {
        return parseHexBinary(s, BuiltInSchema.SINGLETON.HEX_BINARY);
    }

    public static byte[] parseHexBinary(final String s, final SimpleType type) 
        throws DatatypeException
    {
        final char[] buf = s.toCharArray();

        if (buf.length % 2 != 0)
            throw new DatatypeException(s, type);

        final byte[] b = new byte[buf.length >> 1];
        final int len = buf.length;

        for (int i = 0; i < len; i += 2)
        {
            final int hi = hexval(buf[i]);
            if (hi < 0)
                throw new DatatypeException(s, type);
            final int lo = hexval(buf[i + 1]);
            if (lo < 0)
                throw new DatatypeException(s, type);
            b[i >> 1] = (byte)((hi << 4) + lo);
        }
        return b;
    }

    private static int hexval(final char c)
    {
        if ((c >= '0') && (c <= '9'))
            return c - '0';
        if ((c >= 'A') && (c <= 'F'))
            return c - ('A' - 10);
        if ((c >= 'a') && (c <= 'f'))
            // according to schema, section 3.2.15hexBinary, lower case a-f are ok, too.
            return c - ('a' - 10);
        return -1;
    }

    private static char hexchar(final int value)
    {
        if (value <= 9)
            return (char)('0' + (char)value);
        return (char)(('A' - 10) + (char)value);
    }
}
