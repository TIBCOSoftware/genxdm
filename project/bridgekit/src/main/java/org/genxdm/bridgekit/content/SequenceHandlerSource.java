package org.genxdm.bridgekit.content;

import org.genxdm.typed.io.SequenceHandler;

public interface SequenceHandlerSource<A>
{
    SequenceHandler<A> getSequenceHandler();
}
