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
package org.genxdm.xs;

import java.io.InputStream;

import org.genxdm.Cursor;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.facets.SchemaRegExCompiler;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;

public interface SchemaParser
{
    ComponentBag parse(final String schemaLocation, final Cursor tree,
            final String systemId, final SchemaExceptionHandler errors)
        throws AbortException;
    
    /** Parse and validate a schema, returning a bag of the components
     * acquired.
     * 
     * Given a resolver, will process includes an imports (without a
     * resolver, no such processing may happen).
     * 
     * @param schemaLocation the 'base URI' for this schema; it may
     * be used during resolution of imports and includes; may be null
     * @param istream the actual stream representing the file or
     * network connection; may not be null
     * @param systemId the canonical identifier for this schema; may
     * be null
     * @param errors the error handler for problems encountered during
     * parsing. if null, the parser may handle errors encountered in
     * any fashion it pleases, including aborting
     * @return a ComponentBag implementation representing the content
     * of the parsed schema
     * @throws AbortException
     */
    ComponentBag parse(final String schemaLocation, final InputStream istream,
            final String systemId, final SchemaExceptionHandler errors)
        throws AbortException;
    
    /** Set the regular expression compiler to be used during parsing.
     *
     * A parser implementation <em>must</em> provide some form of regular
     * expression handling during parse, and must use the caller-supplied
     * compiler when this method has been called prior to parse.
     * 
     * @param regexc the compiler to use; if null, the parser should
     * reset to its default behavior.
     */
    void setRegExCompiler(final SchemaRegExCompiler regexc);
    
    /** Set the schema catalog resolver for this parser instance.
     * 
     * The resolver will be used, with the catalog, to resolve includes and
     * imports found during parsing.
     * 
     * Callers should <em>always</em> invoke this with a working resolver
     * and catalog pair prior to parsing.  Schemas containing includes or
     * imports will produce undefined (but probably unpleasant) results if
     * either of these are null.
     * 
     * @param resolver the resolver to use for includes and imports.
     * If null, disable processing of includes and imports.
     * @param catalog the catalog to be used by the catalog resolver.
     * If null, the resolver may be handled whatever URI the parser
     * implementation deems interesting, including null.
     */
    void setCatalogResolver(final CatalogResolver resolver, final SchemaCatalog catalog);
    
    /** Set the bootstrap component provider
     * 
     * @param provider a bootstrap ComponentProvider containing at
     * least the schema-defined base types, which will be used to resolve
     * the definitions found during parsing
     */
    void setComponentProvider(final ComponentProvider provider);
    
    /** Set the schema load options.
     * 
     * @param options a SchemaLoadOptions container
     */
    void setSchemaLoadOptions(final SchemaLoadOptions options);
}
