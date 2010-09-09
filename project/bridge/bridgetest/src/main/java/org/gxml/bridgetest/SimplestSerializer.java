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
package org.gxml.bridgetest;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

import org.gxml.DtdAttributeKind;
import org.gxml.base.io.ContentHandler;
import org.gxml.exceptions.GxmlException;
import org.gxml.exceptions.GxmlIOException;
import org.gxml.exceptions.PreCondition;

final class SimplestSerializer<N> implements ContentHandler
{
    SimplestSerializer(final Writer writer)
    {
        this.writer = PreCondition.assertArgumentNotNull(writer, "writer");
    }

    public void attribute(final String namespaceURI, final String localName, final String prefix, final String value, DtdAttributeKind type) throws GxmlException
    {
        try
        {
            writer.append("attribute ");
            writer.append(localName.toString());
            writer.append(" { ");
            writer.append(value);
            writer.append(" } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }
    
    public void comment(final String value) throws GxmlException
    {
        try
        {
            writer.append(" comment { '").append(value).append("' } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void endDocument() throws GxmlException, GxmlIOException
    {
        try
        {
            writer.append(" } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void endElement() throws GxmlException
    {
        try
        {
            writer.append(" } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void namespace(final String prefix, final String namespaceURI) throws GxmlException
    {
        try
        {
            writer.append(" namespace { '").append(prefix).append("' , '").append(namespaceURI.toString()).append("' } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void processingInstruction(final String target, final String data) throws GxmlException
    {
        try
        {
            writer.append(" processing-instruction { '").append(target).append("' , '").append(data).append("' } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void startDocument(final URI documentURI, final String docTypeDecl) throws GxmlException
    {
        try
        {
            writer.append("document { ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void startElement(final String namespaceURI, final String localName, final String prefix) throws GxmlException
    {
        try
        {
            writer.append("element ");
            if (prefix.length() > 0)
            {
                writer.append(prefix).append(":");
            }
            writer.append(localName.toString());
            writer.append(" { ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }

    public void text(final String value) throws GxmlException
    {
        try
        {
            writer.append("text");
            writer.append(" { ");
            writer.append(value);
            writer.append(" } ");
        }
        catch (final IOException e)
        {
            throw new GxmlIOException(e);
        }
    }
    
    public void flush() throws IOException
    {
        writer.flush();
    }

    public void close() throws IOException
    {
        writer.close();
    }
    private final Writer writer;

}
