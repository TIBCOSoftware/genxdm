package org.genxdm.xs;

import java.io.InputStream;
import java.net.URI;

import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.facets.SchemaRegExCompiler;

public interface SchemaParser
{
    ComponentBag parse(final URI schemaLocation, final InputStream istream,
            final URI systemId, final SchemaExceptionHandler errors,
            final SchemaLoadOptions args, final ComponentProvider components)
        throws AbortException;
    
    void setRegExCompiler(final SchemaRegExCompiler regexc);
}
