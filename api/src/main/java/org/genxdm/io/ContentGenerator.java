package org.genxdm.io;

import org.genxdm.exceptions.GenXDMException;

public interface ContentGenerator
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

    /**
     * Test whether this node is an element node.
     * 
     * @return true for element nodes, false for all other node types.
     */
    boolean isElement();
}
