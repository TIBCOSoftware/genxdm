package org.genxdm.creation;

import org.genxdm.typed.io.SequenceGenerator;

public interface TypeAwareBranchCopier<A>
    extends BranchCopier
{
    void copyTypedTreeAt(SequenceGenerator<A> generator);
}
