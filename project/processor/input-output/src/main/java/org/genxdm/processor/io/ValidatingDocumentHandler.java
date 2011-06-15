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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.xml.stream.XMLReporter;

import org.genxdm.exceptions.XdmMarshalException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolved;
import org.genxdm.io.Resolver;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.Validator;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.xml.sax.InputSource;

public class ValidatingDocumentHandler<N, A>
    implements TypedDocumentHandler<N, A>
{

    public ValidatingDocumentHandler(final TypedContext<N, A> context, final Validator<A> validator, final XMLReporter reporter, final Resolver resolver)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        this.validator = PreCondition.assertNotNull(validator, "validator");
        this.resolver = resolver;
        this.reporter = reporter;
    }
    
    @Override
    public N parse(InputStream byteStream, URI systemId)
        throws IOException, XdmMarshalException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public N parse(Reader characterStream, URI systemId)
        throws IOException, XdmMarshalException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public N parse(InputSource source, URI systemId)
        throws IOException, XdmMarshalException
    {
        if (source.getCharacterStream() != null)
            return parse(source.getCharacterStream(), systemId);
        if (source.getByteStream() != null)
            return parse(source.getByteStream(), systemId);
        if (resolver != null)
        {
            // TODO: this might break, actually.
            // also, this indicates that we're being lame with the resolver.
            Resolved<Reader> rdr = resolver.resolveReader(URI.create(source.getSystemId())); 
            return parse(rdr.getResource(), systemId);
        }
        return null;
    }

    @Override
    public void write(OutputStream byteStream, N source, String encoding)
        throws IOException, XdmMarshalException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(Writer characterStream, N source)
        throws IOException, XdmMarshalException
    {
        // TODO Auto-generated method stub

    }
    
    @Override
    public Validator<A> getValidator()
    {
        return validator;
    }
    
    private final TypedContext<N, A> context;
    private final Validator<A> validator;
    private Resolver resolver;
    private XMLReporter reporter;
}
