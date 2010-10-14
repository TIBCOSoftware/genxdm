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
package org.gxml.processor.input;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.XMLConstants;

import org.genxdm.base.io.ContentHandler;
import org.genxdm.exceptions.GxmlException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SaxContentHandlerOnContentHandler
    extends DefaultHandler
    implements LexicalHandler
{
    public SaxContentHandlerOnContentHandler(final ContentHandler output)
    {
        this.output = output;
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        try
        {
            output.text(new String(ch, start, length));
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    public void comment(char[] ch, int start, int length)
        throws SAXException
    {
        try
        {
            output.comment(new String(ch, start, length));
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    public void endCDATA()
        throws SAXException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void endDocument()
        throws SAXException
    {
        try
        {
            output.endDocument();
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    public void endDTD()
        throws SAXException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
        try
        {
            output.endElement();
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    public void endEntity(String name)
        throws SAXException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void endPrefixMapping(String prefix)
        throws SAXException
    {
    }

    @Override
    public void error(SAXParseException e)
        throws SAXException
    {
        super.error(e);
    }

    @Override
    public void fatalError(SAXParseException e)
        throws SAXException
    {
        super.fatalError(e);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
        // TODO: which of these is preferred?
        //output.text(new String(ch, start, length));
        super.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId)
        throws SAXException
    {
        super.notationDecl(name, publicId, systemId);
    }

    @Override
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        try
        {
            output.processingInstruction(target, data);
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
        throws IOException, SAXException
    {
        return super.resolveEntity(publicId, systemId);
    }

    @Override
    public void setDocumentLocator(Locator locator)
    {
        super.setDocumentLocator(locator);
    }

    @Override
    public void skippedEntity(String name)
        throws SAXException
    {
        super.skippedEntity(name);
    }

    public void startCDATA()
        throws SAXException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startDocument()
        throws SAXException
    {
        // TODO: fix this, by saving it up.  We want the startDTD/internal subset bit;
        // we can fire startDocument(null, [doctypedecl]) then.
        try
        {
            output.startDocument(null, null);
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    public void startDTD(String name, String publicId, String systemId)
        throws SAXException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
        try
        {
            output.startElement(uri, localName, prefixFromQName(qName));
            for (int j = 0; j < pfxs.size(); j++)
            {
                output.namespace(pfxs.get(j), uris.get(j));
            }
            pfxs.clear();
            uris.clear();

            if (null != attributes)
            {
                final int length = attributes.getLength();
                for (int index = 0; index < length; index++)
                {
                    final String uriSymbol = attributes.getURI(index);
                    final String localNameSymbol = attributes.getLocalName(index);
                    output.attribute(uriSymbol, localNameSymbol, prefixFromQName(attributes.getQName(index)), attributes.getValue(index), null);
                }
            }
        }
        catch (final GxmlException e)
        {
            throw new SAXException(e);
        }
    }

    public void startEntity(String name)
        throws SAXException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
        pfxs.add(prefix);
        uris.add(uri);
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
        throws SAXException
    {
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public void warning(SAXParseException e)
        throws SAXException
    {
        super.warning(e);
    }

    private String prefixFromQName(final String name)
    {
        final int indexOfColon = name.indexOf(':');
        if (indexOfColon >= 0)
        {
            return name.substring(0, indexOfColon);
        }
        else
        {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
    }

   private final ContentHandler output;
   private final ArrayList<String> pfxs = new ArrayList<String>();
   private final ArrayList<String> uris = new ArrayList<String>();

}