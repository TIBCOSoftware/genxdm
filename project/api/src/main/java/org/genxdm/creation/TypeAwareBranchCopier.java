package org.genxdm.creation;

import org.genxdm.typed.io.SequenceGenerator;

/** A tool to copy an existing in-memory XML tree into some other tool,
 * presumptively one of the ContentHelper tools in this package.
 * 
 * This interface expects a SequenceGenerator rather than a ContentGenerator,
 * and so is expected to drive a SequenceHandler, with its typed values and
 * type annotations, rather than a ContentHandler.
 * 
 */
public interface TypeAwareBranchCopier<A>
    extends BranchCopier
{
    /** Couple the SequenceGenerator's write() method to a SequenceHandler
     * of some sort in order to copy the content of the branch into the
     * target, with type annotations and typed values.
     * 
     * @param generator the generator of method calls into SequenceHandler;
     *        must not be null
     */
    void copyTypedTreeAt(SequenceGenerator<A> generator);
}
