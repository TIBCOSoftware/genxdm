package org.genxdm.bridgekit.validation;

import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.io.SequenceHandler;

public interface ValidatingCursor<N, A>
    extends TypedCursor<N, A>, SequenceHandler<A>
{

}
