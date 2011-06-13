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
            {
                b = 256 + b;
            }
            buf[(i << 1)] = hexchar(b >> 4);
            buf[(i << 1) + 1] = hexchar(b % 16);
        }
        return new String(buf);
    }

    private static char hexchar(final int value)
    {
        if (value <= 9)
        {
            return (char)('0' + (char)value);
        }
        return (char)(('A' - 10) + (char)value);
    }
}
