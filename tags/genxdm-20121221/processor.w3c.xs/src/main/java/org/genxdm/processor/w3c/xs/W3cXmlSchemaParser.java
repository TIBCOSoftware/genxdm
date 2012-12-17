/*
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
package org.genxdm.processor.w3c.xs;

import java.io.InputStream;
import java.net.URI;

import org.genxdm.Cursor;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.impl.XMLParserImpl;
import org.genxdm.processor.w3c.xs.regex.RegExCompilerXSDL;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaLoadOptions;
import org.genxdm.xs.SchemaParser;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.facets.SchemaRegExCompiler;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;

public final class W3cXmlSchemaParser 
    implements SchemaParser
{
    public W3cXmlSchemaParser()
    {
        this.catalog =  new DefaultSchemaCatalog(new DefaultCatalog());
        this.resolver = DefaultCatalogResolver.SINGLETON;
        this.regexc = DEFAULT_REGEX_COMPILER;
    }
    
    @Override 
    public ComponentBag parse(final URI schemaLocation, final Cursor tree,
            final URI systemId, final SchemaExceptionHandler errors)
        throws AbortException
    {
        PreCondition.assertArgumentNotNull(tree, "tree");
        final XMLParserImpl parser = new XMLParserImpl(components);
        parser.setCatalogResolver(resolver, catalog);
        parser.setRegExCompiler(regexc);
        parser.setSchemaLoadOptions(options);

        return parser.parse(schemaLocation, tree, systemId, errors);
    }

    @Override
    public ComponentBag parse(final URI schemaLocation, final InputStream istream,
                                 final URI systemId, final SchemaExceptionHandler errors)
        throws AbortException
    {
        PreCondition.assertArgumentNotNull(istream, "istream");
        final XMLParserImpl parser = new XMLParserImpl(components);

        parser.setCatalogResolver(resolver, catalog);
        parser.setRegExCompiler(regexc);
        parser.setSchemaLoadOptions(options);

        return parser.parse(schemaLocation, istream, systemId, errors);
    }

    /**
     * Override the default (JDK-based) Regular Expression compiler.
     * 
     * @param regexc
     *            The new compiler. May be <code>null</code> to reset to
     *            default.
     */
    @Override
    public void setRegExCompiler(final SchemaRegExCompiler regexc)
    {
        if (null != regexc)
        {
            this.regexc = regexc;
        } 
        else
        {
            this.regexc = DEFAULT_REGEX_COMPILER;
        }
    }
    
    @Override
    public void setCatalogResolver(final CatalogResolver resolver, final SchemaCatalog catalog)
    {
        this.resolver = resolver;
        this.catalog = catalog;
    }
    
    @Override
    public void setComponentProvider(final ComponentProvider provider)
    {
        this.components = provider;
    }
    
    /**
     * At present, these values are ignored by this parser implementation.
     */
    @Override
    public void setSchemaLoadOptions(final SchemaLoadOptions options)
    {
        this.options = options;
    }

    // The default Regular Expression compiler is backed by the JDK.
    private static final SchemaRegExCompiler DEFAULT_REGEX_COMPILER = new RegExCompilerXSDL();

    // The actual Regular Expression compiler may be changed.
    private SchemaRegExCompiler regexc;
    private SchemaCatalog catalog;
    private CatalogResolver resolver;
    private ComponentProvider components;
    private SchemaLoadOptions options;

}
