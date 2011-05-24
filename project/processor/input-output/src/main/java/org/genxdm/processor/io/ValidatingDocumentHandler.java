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

import org.genxdm.exceptions.GxmlMarshalException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolver;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.xml.sax.InputSource;

public class ValidatingDocumentHandler<N, A>
    implements TypedDocumentHandler<N, A>
{

    public ValidatingDocumentHandler(final TypedContext<N, A> context, final ValidationHandler<N, A> validator, final XMLReporter reporter, final Resolver resolver)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        this.validator = PreCondition.assertNotNull(validator, "validator");
        this.resolver = resolver;
        this.reporter = reporter;
    }
    
    @Override
    public N parse(InputStream byteStream, URI systemId)
        throws IOException, GxmlMarshalException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public N parse(Reader characterStream, URI systemId)
        throws IOException, GxmlMarshalException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public N parse(InputSource source, URI systemId)
        throws IOException, GxmlMarshalException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void write(OutputStream byteStream, N source, String encoding)
        throws IOException, GxmlMarshalException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(Writer characterStream, N source)
        throws IOException, GxmlMarshalException
    {
        // TODO Auto-generated method stub

    }
    
    @Override
    public ValidationHandler<N, A> getValidator()
    {
        return validator;
    }
    
    private final TypedContext<N, A> context;
    private final ValidationHandler<N, A> validator;
    private Resolver resolver;
    private XMLReporter reporter;
}
