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

import java.io.UnsupportedEncodingException;

import org.genxdm.bridgekit.atoms.Base64BinarySupport;

/**
 * see Base64BinarySupport in org.genxdm.bridgekit.atoms
 * 
 */

@Deprecated
public final class Base64Codec
{
    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static String encodeBase64(String raw) throws UnsupportedEncodingException
    {
        return encodeBase64(raw, false);
    }

    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static String encodeBase64(String raw, boolean dontBreakLines) throws UnsupportedEncodingException
    {
        return encodeBase64(raw.getBytes("US-ASCII"), dontBreakLines);
    }

    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static String encodeBase64(byte[] raw)
    {
        return encodeBase64(raw, "\r\n");
    }

    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static String encodeBase64(byte[] raw, boolean dontBreakLines)
    {
        return encodeBase64(raw, dontBreakLines ? null : "\r\n");
    }

    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static String encodeBase64(byte[] raw, String lineEnding)
    {
        return Base64BinarySupport.encodeBase64(raw, lineEnding);
    }

    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static String decodeBase64(String base64, String enc) throws UnsupportedEncodingException
    {
        return new String(decodeBase64(base64), enc);
    }

    /**
     * see Base64BinarySupport in org.genxdm.bridgekit.atoms
     */
    public static byte[] decodeBase64(String base64)
    {
        return Base64BinarySupport.decodeBase64(base64);
    }
}
