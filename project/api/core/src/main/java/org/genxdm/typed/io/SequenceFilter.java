package org.genxdm.typed.io;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.Schema;

public interface SequenceFilter<A>
    extends SequenceHandler<A>
{
    void setOutputSequenceHandler(SequenceHandler<A> output);
    
    void setAtomBridge(AtomBridge<A> bridge);
    
    void setSchema(Schema schema);
}
