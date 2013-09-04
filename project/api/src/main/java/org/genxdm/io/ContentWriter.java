package org.genxdm.io;

import org.genxdm.exceptions.GenXDMException;

public interface ContentWriter
{
    /**
     * Support for streaming the current node to a {@link ContentHandler}.
     * 
     * @param writer
     *            The handler for events generated.
     * 
     * @throws GenXDMException
     */
    void write(ContentHandler writer) throws GenXDMException;

}
