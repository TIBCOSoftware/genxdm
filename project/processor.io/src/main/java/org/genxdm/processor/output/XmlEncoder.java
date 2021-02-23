/*
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.processor.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

public final class XmlEncoder
{
    // this method and the one with the charset encoder parameter are *preferred*,
    // if an xml encoder is used. however, they can only be used as the final filter
    // before switching from content handler to output (and we use writer only, because
    // we don't need to try coping with the output stream directly)
    // non-static member methods are going to be DEPRECATED, because in the degenerate
    // case (when somebody is trying to encode an entire 1MB XML document without
    // using CDATA sections, which happens with depressing frequency), it lags so
    // hard that it looks locked up.
    public static final void writeEncodedCData(String data, Writer target, boolean isPCData)
        throws IOException
    {
        writeEncodedCData(data, target, null, isPCData);
    }

    public static final void writeEncodedCData(String data, Writer target, CharsetEncoder encoder, boolean isPCData)
        throws IOException
    {
        String entity = null;
        int last = 0;
        final int len = data.length();
        boolean surrogateSkip = false;//if true we need to skip an extra character

        for (int i = 0; i < len; i++) 
        {
            char c = data.charAt(i);
            if (c >= (char) 64 && c <= (char) 127)
                continue;

            if (c < (char) 64)
                switch ( c ) 
                {
                    case '\000':
                    case '\001':
                    case '\002':
                    case '\003':
                    case '\004':
                    case '\005':
                    case '\006':
                    case '\007':
                    case '\010':
                    //case '\011': TAB
                    //case '\012': NL
                    case '\013':
                    case '\014':
//                    case '\r' :moved below
                    case '\016':
                    case '\017':
                    case '\020':
                    case '\021':
                    case '\022':
                    case '\023':
                    case '\024':
                    case '\025':
                    case '\026':
                    case '\027':
                    case '\030':
                    case '\031':
                    case '\032':
                    case '\033':
                    case '\034':
                    case '\035':
                    case '\036':
                    case '\037':
                        illegal(c);
                        break;
                    case '\r' :
                        entity = CR_ENT;
                        break;
                    case '\n': // should be preserved in CDATA (attributes) when not collapsed.
                        if (!isPCData)
                            entity = NL_ENT;
                        break;
                        // otherwise no-op
                    // the next two handle single and double quote in CDATA (attributes)
                    case '\'' :
                        if (!isPCData)
                            entity = APOS_ENT;
                        // otherwise no-op
                        break;
                    case '"' :
                        if (!isPCData)
                            entity = QUOT_ENT;
                        // otherwise no-op
                        break;
                    case '&' :
                        entity = AMP_ENT;
                        break;
                    case '<' :
                        entity = LT_ENT;
                        break;
                    case '>' :
                        entity = GT_ENT;
                        break;
                    default :
                        /* no-op; includes whitespace tab newline */
                }
            else if (isSurrogate(c)) 
            {
                if (isHighSurrogate(c) && i+1 < len && isLowSurrogate(data.charAt(i+1)))
                {
                    if (encoder != null) 
                    {
                        // basically the same test as for high characters below,
                        // only this one's for the two-code-point surrogate string
                        final char low = data.charAt(i+1);
                        char[] cs = {c, low};
                        encoder.reset();
                        if (!encoder.canEncode(new String(cs))) 
                            entity = "&#x" + toHexString(toCodePoint(c, low)) + ';';
                    }
                    if (entity == null)
                        i++;//valid surrogate pair
                    else
                        surrogateSkip = true;
                }
                else // only happens if we don't have high-surrogate low-surrogate pair in order,
                    // either low-surrogate first or bare high-surrogate
                    illegal(c);
            } 
            else if (encoder != null) 
            {
                // if we have an encoder, we can test whether it's safe to output
                // in this encoding. if not, we'll just try (and maybe fail).
                // if we have an encoder and it can't encode, we entity-ize it.
                encoder.reset();
                if (!encoder.canEncode(c)) 
                    entity = "&#x" + toHexString((int) c) + ';';
            }
            // and, finally, we do something:
            if (entity != null) 
            {
                // write all the data to this point that we've continue-d past
                target.write(data, last, i - last);
                // write the entity, which is how we got inside here
                target.write(entity);
                // null it out, so that we don't write it twice
                entity = null;
                if (surrogateSkip) // clean up surrogate handling
                {
                    surrogateSkip = false;
                    i++;
                }
                last = i + 1;
            }
        }
        // and now that we've finished, handle the leftovers (usually not a no-op)
        final int remaining = len - last;
        if (remaining != 0)
            target.write(data, last, remaining);
    }

    public final String encodePCData(String input)
    {
        prepare(input);
        encodePCData();
        
        return builder.toString();
    }

    public final byte[] encodePCData(String input, String encoding)
    {
        prepare(input);
        encodePCData();

        return byteArray(encoding);
    }

    public final String encodeCData(String input)
    {
        prepare(input);
        encodePCData();
        encodeCData();
        
        return builder.toString();
    }

    public final byte[] encodeCData(String input, String encoding)
    {
        prepare(input);
        encodePCData();
        encodeCData();

        return byteArray(encoding);
    }

    private void prepare(String input)
    {
        builder.delete(0, builder.length());
        builder.ensureCapacity(input.length());
        builder.append(input);
   }

    private void encodePCData()
    {
        int index = builder.indexOf("&");
        while (index >= 0)
        {
            int last = index;
            builder.replace(index, index + 1, "&amp;");
            index = builder.indexOf("&", last + 1); 
        }
        index = builder.indexOf("<");
        while (index >= 0)
        {
            builder.replace(index, index + 1, "&lt;");
            index = builder.indexOf("<");
        }
        index = builder.indexOf("]]>");
        while (index >= 0)
        {
            builder.replace(index + 2, index + 3, "&gt;");
            index = builder.indexOf("]]>");
        }
    }

    // assumes that PCData is already done
    private void encodeCData()
    {
        int index = builder.indexOf("\"");
        while (index >= 0)
        {
            builder.replace(index, index + 1, "&quot;");
            index = builder.indexOf("\"");
        }
        index = builder.indexOf("'");
        while (index >= 0)
        {
            builder.replace(index, index + 1, "&apos;");
            index = builder.indexOf("'", index);
        }
    }

    private byte[] byteArray(final String encoding)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(builder.length());
            OutputStreamWriter writer = new OutputStreamWriter(baos, encoding);
            writer.write(builder.toString());
            return baos.toByteArray();
        }
        catch (IOException ioe)
        {
            return null;
        }
    }

    private static void illegal(char c) 
    {
        throw new IllegalArgumentException("Char 0x" + toHexString((int) c) + " cannot be used in XML");
    }

    private static boolean isSurrogate(char c)
    {
        return '\uD800' <= c && c <= '\uDFFF';
    }

    private static boolean isLowSurrogate(char c)
    {
        return '\uDC00' <= c && c <= '\uDFFF';
    }

    private static boolean isHighSurrogate(char c)
    {
        return '\uD800' <= c && c < '\uDC00';
    }

    private static int toCodePoint(final char hi, final char lo)
    {
        return (((int) hi - 0xD800) << 10)
                + ((int) lo - 0xDC00) + 0x010000;
    }

    public static String toHexString(int i)
    {
        final char[] buf = new char[32];
        int charPos = 32;
        do 
        {
            buf[--charPos] = digits[i & 0xF];
            i >>>= 4;
        } while (i != 0);
        return new String(buf, charPos, (32 - charPos));
    }

    private static final String AMP_ENT = "&amp;";
    private static final String LT_ENT = "&lt;";
    private static final String GT_ENT = "&gt;";
    private static final String APOS_ENT = "&apos;";
    private static final String QUOT_ENT = "&quot;";
    private static final String CR_ENT = "&#xD;";
    private static final String NL_ENT = "&#xA;";
    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    
    private StringBuilder builder = new StringBuilder();
}
