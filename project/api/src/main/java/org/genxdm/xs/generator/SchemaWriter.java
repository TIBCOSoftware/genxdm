package org.genxdm.xs.generator;

import java.io.IOException;
import java.io.Writer;

// only actually interesting when implemented as part of schemabuilder.
// this is an output-only interface, after all. :-)
public interface SchemaWriter
{
    void write(Writer writer) 
        throws IOException;

}
