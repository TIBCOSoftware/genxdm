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
package org.genxdm.processor.output;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Stack;

import org.genxdm.DtdAttributeKind;
import org.genxdm.base.io.ContentHandler;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.io.WhitespaceEmitter;

public class ContentWriter
    implements ContentHandler, WhitespaceEmitter
{
    
    public ContentWriter(final Writer output)
    {
        this.output = PreCondition.assertNotNull(output, "output");
    }

    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GxmlException
    {
        try
        {
            output.write(" " + getQName(prefix, localName) + EQ + QU + encoder.encodeCData(value) + QU);
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    public void comment(String value)
        throws GxmlException
    {
        try
        {
            finishStart();
            // no escaping possible
            output.write(SCOM + value + ECOM);
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    public void endDocument()
        throws GxmlException
    {
        // TODO : ??
    }

    public void endElement()
        throws GxmlException
    {
        try
        {
            String tagName = tags.pop();
            if (openedTag)
                output.write(" " + SLASH + GT);
            else
                output.write(LT + SLASH + tagName + GT);
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    public void namespace(String prefix, String namespaceURI)
        throws GxmlException
    {
        try
        {
            output.write(" xmlns" + (prefix.length() > 0 ? COLON + prefix : "") + EQ + QU + encoder.encodeCData(namespaceURI) + QU);
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }
    
    public void newline()
    {
        try
        {
            finishStart();
            output.write("\n");
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    public void processingInstruction(String target, String data)
        throws GxmlException
    {
        try
        {
            finishStart();
            // no escaping possible
            output.write(SPI + target + " " + data + EPI);
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    public void startDocument(final URI documentURI, final String docTypeDecl)
        throws GxmlException
    {
        // TODO Auto-generated method stub

    }

    public void startElement(String namespaceURI, String localName, String prefix)
        throws GxmlException
    {
        // TODO Auto-generated method stub
        tags.push(getQName(prefix, localName));
        openedTag = true;
    }

    public void text(String data)
        throws GxmlException
    {
        try
        {
            finishStart();
            output.write(encoder.encodePCData(data));
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    public void close()
        throws IOException
    {
        output.close();
    }

    public void flush()
        throws IOException
    {
        output.flush();
    }
    
    public void whitespace(int nSpaces)
    {
        try
        {
            finishStart();
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }
    
    private void finishStart()
        throws IOException
    {
        if (openedTag)
            output.write(GT);
        openedTag = false;
    }
    
    private String getQName(String prefix, String localName)
    {
        if ( (prefix != null) && !prefix.equals("") )
            return prefix + COLON + localName;
        return localName;
    }

    private boolean openedTag = false;
    private final Writer output;
    private final XmlEncoder encoder = new XmlEncoder();
    private Stack<String> tags = new Stack<String>();

    private static final String LT = "<";
    private static final String GT = ">";
    private static final String SLASH = "/";
    private static final String EQ = "=";
    private static final String QU = "\"";
    private static final String COLON = ":";
    
    private static final String SCOM = "<!--";
    private static final String ECOM = "-->";
    private static final String SPI = "<?";
    private static final String EPI = "?>";
}
