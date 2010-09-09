/**
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
package org.gxml.processor.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public final class XmlEncoder
{    
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
    
    private StringBuilder builder = new StringBuilder();
}
