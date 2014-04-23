/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.processor.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLReporter;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.XdmMarshalException;
import org.genxdm.io.Resolver;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.Validator;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ValidatingDocumentHandler<N, A>
    extends DefaultSerializer<N>
    implements TypedDocumentHandler<N, A>
{

    public ValidatingDocumentHandler(final TypedContext<N, A> context, final SAXValidator<A> validator, final XMLReporter reporter, final Resolver resolver)
    {
        super(PreCondition.assertNotNull(context, "context").getModel());
        this.context = context;
        this.validator = PreCondition.assertNotNull(validator, "validator");
        this.validator.setSchema(this.context.getSchema());
        this.resolver = resolver;
        this.reporter = reporter;
        this.spf = SAXParserFactory.newInstance();
        this.spf.setNamespaceAware(true);
    }
    
    @Override
    public N parse(InputStream byteStream, String systemId)
        throws IOException, XdmMarshalException
    {
        final InputSource source = new InputSource(PreCondition.assertNotNull(byteStream, "byteStream"));
        if (systemId != null)
            source.setSystemId(systemId.toString());
        return parse(source, systemId);
    }

    @Override
    public N parse(Reader characterStream, String systemId)
        throws IOException, XdmMarshalException
    {
        final InputSource source = new InputSource(PreCondition.assertNotNull(characterStream, "characterStream"));
        if (systemId != null)
            source.setSystemId(systemId.toString());
        return parse(source, systemId);
    }

    @Override
    public N parse(InputSource source, String systemId)
        throws IOException, XdmMarshalException
    {
        try
        {
            XMLReader reader = spf.newSAXParser().getXMLReader();
            SequenceBuilder<N, A> builder = context.newSequenceBuilder();
            validator.setSequenceHandler(builder);
            // TODO: query lexical handler?
            // if we want a lexical handler, then SAXValidator should do it.
            reader.setContentHandler(validator);
            // TODO
//            reader.setErrorHandler(new ErrorHandlerToXMLReporterAdapter(reporter));
            reader.parse(source);
            return builder.getNode();
        } 
        catch (SAXException saxy)
        {
            throw new XdmMarshalException(saxy);
        } 
        catch (ParserConfigurationException pce)
        {
            throw new XdmMarshalException(pce);
        }
    }

    @Override
    public Validator<A> getValidator()
    {
        return validator;
    }

    /**
     * Using a SAX parser - just ask the parser if it is parsing securely.
     */
    @Override
    public boolean isSecurelyProcessing() {
        try {
            return spf.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING);
        } catch (Exception e) {
            return false;
        }
    }
    
    private final TypedContext<N, A> context;
    private final SAXValidator<A> validator;
    private Resolver resolver;
    private XMLReporter reporter;
    private SAXParserFactory spf;
}
