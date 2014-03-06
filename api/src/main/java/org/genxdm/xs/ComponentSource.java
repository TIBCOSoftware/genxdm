package org.genxdm.xs;

import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;

public interface ComponentSource
{
    ComponentBag resolve(ComponentProvider prerequisites, SchemaExceptionHandler errors)
        throws AbortException;
    
    /** Set the schema catalog resolver for this source instance.
     * 
     * The resolver will be used, with the catalog, to resolve includes and
     * imports found during resolution.
     * 
     * Callers should <em>always</em> invoke this with a working resolver
     * and catalog pair prior to resolution.  Schemas containing includes or
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

}
