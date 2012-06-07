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
package org.genxdm.processor.w3c.xs.impl;

import java.io.InputStream;
import java.net.URI;

import org.genxdm.bridgekit.xs.ComponentBagImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.impl.xmlrep.XMLComponentLocator;
import org.genxdm.processor.w3c.xs.impl.xmlrep.XMLSccExceptionAdapter;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaLoadOptions;
import org.genxdm.xs.SchemaParser;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.facets.SchemaRegExCompiler;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;

final public class XMLParserImpl implements SchemaParser
{
    public XMLParserImpl(final ComponentProvider cache)
    {
        this.cache = PreCondition.assertArgumentNotNull(cache, "cache");
    }

    public ComponentBag parse(final URI schemaLocation, final InputStream istream, final URI systemId) 
        throws SchemaException
    {
        // This convenience routine is implemented in terms of the more general routine.
        final SchemaExceptionCatcher errors = new SchemaExceptionCatcher();
        try
        {
            final ComponentBag components = parse(schemaLocation, istream, systemId, errors);
            if (errors.size() > 0)
            {
                // Only the first error is reported.
                throw errors.getFirst();
            }
            else
            {
                return components;
            }
        }
        catch (final AbortException e)
        {
            // This should not happen because the errors are reported to a catcher.
            throw new AssertionError(e);
        }
    }

    public ComponentBag parse(final URI schemaLocation, final InputStream istream, final URI systemId, final SchemaExceptionHandler errors) 
        throws AbortException
    {
        PreCondition.assertArgumentNotNull(istream, "istream");
        // PreCondition.assertArgumentNotNull(systemId, "systemId");
        // PreCondition.assertArgumentNotNull(errors, "errors");

        // The cache holds an in-memory model of the XML representation.
        final XMLSchemaCache cache = new XMLSchemaCache();

        // The top-level module acts as a parent for includes, imports and redefines.
        final XMLSchemaModule module = new XMLSchemaModule(null, schemaLocation, systemId);

        // Catch the reported exceptions in order to maximize the amount of feedback.
        final SchemaExceptionCatcher caught = new SchemaExceptionCatcher();

        // Delegate the parsing into an XML representation
        final XMLSchemaParser parser = new XMLSchemaParser(this.cache, caught, m_catalog, m_resolver, processRepeatedNamespaces());

        parser.parse(systemId, istream, cache, module);

        if (caught.size() > 0)
        {
            reportErrors(caught, errors);
            return null;
        }

        // Convert the XML representation into the compiled schema.
        final Pair<ComponentBagImpl, XMLComponentLocator> converted = convert(cache, caught);

        if (caught.size() == 0)
        {
            final XMLSccExceptionAdapter scc = new XMLSccExceptionAdapter(caught, converted.getSecond());

            SchemaConstraintChecker checker = new SchemaConstraintChecker(converted.getFirst(), this.cache);
            checker.checkSchemaComponentConstraints(scc);

            if (caught.size() == 0)
            {
                return converted.getFirst();
            }
            else
            {
                reportErrors(caught, errors);
                return null;
            }
        }
        else
        {
            reportErrors(caught, errors);
            return null;
        }
    }

    private void reportErrors(final SchemaExceptionCatcher caught, final SchemaExceptionHandler errors) throws AbortException
    {
        for (final SchemaException error : caught)
        {
            if (null != errors)
            {
                errors.error(error);
            }
            else
            {
                throw new AbortException(error);
            }
        }
    }

    /**
     * Converts the XML cache into a compiled schema.
     */
    private Pair<ComponentBagImpl, XMLComponentLocator> convert(final XMLSchemaCache cache, final SchemaExceptionHandler errors) throws AbortException
    {
        try
        {
            cache.checkReferences();
        }
        catch (final SchemaException e)
        {
            errors.error(e);
            return null;
        }

        return XMLSchemaConverter.convert(m_regexc, this.cache, cache, errors);
    }

    public void setCatalogResolver(final CatalogResolver resolver, final SchemaCatalog catalog)
    {
        m_resolver = resolver;
        m_catalog = catalog;
    }

    public CatalogResolver getResolver()
    {
        return m_resolver;
    }

    public SchemaCatalog getCatalog()
    {
        return m_catalog;
    }

    public void setComponentProvider(final ComponentProvider provider)
    {
        this.cache = provider;
    }
    
    public void setRegExCompiler(final SchemaRegExCompiler regexc)
    {
        m_regexc = regexc;
    }

    public SchemaRegExCompiler getRegExCompiler()
    {
        return m_regexc;
    }

    public void setProcessRepeatedNamespaces(final boolean processRepeatedNamespaces)
    {
        m_processRepeatedNamespaces = processRepeatedNamespaces;
    }

    public boolean processRepeatedNamespaces()
    {
        return m_processRepeatedNamespaces;
    }
    
    public void setSchemaLoadOptions(final SchemaLoadOptions options)
    {
        // TODO: not implemented
    }

    /**
     * Injected during initialization, already contains native and xsi schema components.
     */
    private ComponentProvider cache;

    private SchemaCatalog m_catalog;
    private CatalogResolver m_resolver;
    private SchemaRegExCompiler m_regexc;
    private boolean m_processRepeatedNamespaces = true;

}
