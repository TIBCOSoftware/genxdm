package org.genxdm.typed.io;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.SchemaComponentCache;

public interface SequenceFilter<A>
    extends SequenceHandler<A>
{
    void setOutputSequenceHandler(SequenceHandler<A> output);
    
    void setAtomBridge(AtomBridge<A> bridge);
    
    void setSchema(SchemaComponentCache schema);
}
