package org.genxdm.typed.io;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;

public interface SequenceFilter<A>
    extends SequenceHandler<A>
{
    void setOutputSequenceHandler(SequenceHandler<A> output);
    
    void setTypesBridge(TypesBridge bridge);
    
    void setAtomBridge(AtomBridge<A> bridge);
}
