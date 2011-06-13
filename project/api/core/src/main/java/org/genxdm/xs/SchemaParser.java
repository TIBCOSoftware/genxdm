package org.genxdm.xs;

import java.io.InputStream;
import java.net.URI;

import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.facets.SchemaRegExCompiler;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;

public interface SchemaParser
{
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
    ComponentBag parse(final URI schemaLocation, final InputStream istream,
            final URI systemId, final SchemaExceptionHandler errors)
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
