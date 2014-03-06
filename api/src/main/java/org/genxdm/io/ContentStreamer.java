package org.genxdm.io;

import org.genxdm.exceptions.GenXDMException;

public interface ContentStreamer<N>
{
    /**
     * Support for streaming a node to a {@link ContentHandler}.
     * 
     * @param node
     *            The node to be streamed. May not be null.
     * @param handler
     *            The handler for events generated.
     */
    void stream(N node, ContentHandler handler) throws GenXDMException;

}
