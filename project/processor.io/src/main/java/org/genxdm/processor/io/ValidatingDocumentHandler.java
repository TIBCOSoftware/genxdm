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
import org.genxdm.typed.ValidatorFactory;
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

    /**
     *@deprecated since 1.5
     */
    public ValidatingDocumentHandler(final TypedContext<N, A> typedContext, final SAXValidator<A> validator, final XMLReporter rep, final Resolver res)
    {
        super(PreCondition.assertNotNull(typedContext, "context").getModel());
        context = typedContext;
        resolver = res;
        reporter = rep;
        spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        this.validator = PreCondition.assertNotNull(validator, "validator");
        this.validator.setSchema(context.getSchema());
        valFactory = null;
    }
    
    public ValidatingDocumentHandler(final TypedContext<N, A> typedContext, final ValidatorFactory<A> factory, final XMLReporter rep, final Resolver res)
    {
        super(PreCondition.assertNotNull(typedContext, "context").getModel());
        context = typedContext;
        resolver = res;
        reporter = rep;
        spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        valFactory = PreCondition.assertNotNull(factory, "factory");
        validator = null;
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
            final SequenceBuilder<N, A> builder;
            if (valFactory == null)
                builder = context.newSequenceBuilder();
            else
            {
                builder = getBuilder();
                builder.reset();
            }
            final SAXValidator valley;
            if (valFactory != null)
                valley = getLocalValidator();
            else
                valley = validator;
            valley.reset();
            valley.setSchema(context.getSchema());
            valley.setSequenceHandler(builder);
            reader.setContentHandler(validator);
            // TODO
//            reader.setErrorHandler(new ErrorHandlerToXMLReporterAdapter(reporter));
            reader.parse(source);
            return builder.getNode();
        } 
        catch (SAXException saxy)
        {
            if (valFactory != null)
            {
                validators.remove();
                builders.remove();
            }
            throw new XdmMarshalException(saxy);
        } 
        catch (ParserConfigurationException pce)
        {
            if (valFactory != null)
            {
                validators.remove();
                builders.remove();
            }
            throw new XdmMarshalException(pce);
        }
    }

    @Override
    public Validator<A> getValidator()
    {
        if (valFactory == null)
            return validator;
        return getLocalValidator();
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
    
    @Override
    public void setResolver(Resolver resolver)
    {
        this.resolver = resolver;
    }
    
    @Override
    public void setReporter(XMLReporter reporter)
    {
        this.reporter = reporter; // doesn't actually work usefully, though.
    }
    
    private SequenceBuilder<N, A> getBuilder()
    {
        SequenceBuilder<N, A> seqBer = builders.get();
        if (seqBer == null)
        {
            seqBer = context.newSequenceBuilder();
            builders.set(seqBer);
        }
        return seqBer;
    }
    
    private SAXValidator<A> getLocalValidator()
    {
        if (valFactory == null)
            return validator;
        SAXValidator<A> val = validators.get();
        if (val == null)
        {
            val = valFactory.newSAXContentValidator();
            validators.set(val);
        }
        return val;
    }

    private final TypedContext<N, A> context;
    // only used in non-thread-safe mode
    private final SAXValidator<A> validator;
    
    private Resolver resolver;
    private XMLReporter reporter;
    
    private final SAXParserFactory spf;
    private final ValidatorFactory<A> valFactory;
    
    private ThreadLocal<SequenceBuilder<N, A>> builders = new ThreadLocal<SequenceBuilder<N, A>>();
    private ThreadLocal<SAXValidator<A>> validators = new ThreadLocal<SAXValidator<A>>();
}
