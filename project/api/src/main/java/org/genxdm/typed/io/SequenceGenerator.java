package org.genxdm.typed.io;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.ContentGenerator;

public interface SequenceGenerator<A>
    extends ContentGenerator
{
    /**
     * Support for streaming the current node to a {@link SequenceHandler}.
     * 
     * @param handler
     *            The typed handler for events generated.
     * @param bogus
     *            A second argument that allows us to distinguish between the
     *            two write() methods. Value is insignificant, so long as there
     *            is either a true or a false here so that we know to use the
     *            handler as a SequenceHandler and not as a ContentHandler.
     * 
     * @throws GenXDMException
     */
    void write(SequenceHandler<A> handler, boolean bogus)
            throws GenXDMException;
}
