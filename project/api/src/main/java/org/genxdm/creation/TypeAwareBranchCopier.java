package org.genxdm.creation;

import org.genxdm.typed.io.SequenceGenerator;

/** A type aware version of the BranchCopier, used to copy an existing 
 * in-memory XML tree, or more commonly a sequence of content events (method calls 
 * on a SequenceHandler) such as an implementation of TypedEventQueue, 
 * into some other tree (or target that exposes a SequenceHandler interface).
 *
 * TypeAwareBranchCopier is typically a secondary interface implemented by a
 * class that implements BinaryContentHelper.
 * 
 * @param <A> the base class of the implementations representing typed values.
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
