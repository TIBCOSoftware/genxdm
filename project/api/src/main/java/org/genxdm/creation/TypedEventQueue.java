package org.genxdm.creation;

import java.util.List;

import org.genxdm.io.Stateful;
import org.genxdm.typed.io.SequenceGenerator;

/** A factory for events and a storage medium for the generated queue of
 * events when creating parts of a tree out of document order or conditionally,
 * with typed values and type annotations in the typed events.
 *
 * In general, a TypedEventQueue implementation is expected to implement some interface
 * that allows the creation of the queue, typically ContentHelper. The interface
 * definition does not actually <em>require</em> this, however. It is just the source
 * for the events, however created.
 *
 * The EventQueue interacts with a BinaryContentHelper (the primary document builder) that
 * also implements TypeAwareBranchCopier. This allows the implementation of this interface to
 * collect events (elements, attributes, text) while deferring their analysis until
 * the main helper reaches the insertion point for this subtree. At that point, the
 * TypeAwareBranchCopier.copyTreeAt(SequeceGenerator) method may be called with an argument
 * supplied by this.getGenerator().
 *
 * Extends the Stateful interface, which is intended to allow (thread-local/thread-bound,
 * not thread-safe) reuse of the artifact by reinitializing all of its internal state.
 *
 * @param <A> the base class of the classes implementing typed values
 */
public interface TypedEventQueue<A>
    extends Stateful
{
    /** No longer supported; this was the original means of acquiring a queue of events.
     * @return null, always
     * @deprecated since 1.8.1 ; use {@link #getTyepdGenerator()} instead
     */
    @Deprecated
    List<TypedContentEvent> getQueue();
    
    /** Get the SequenceGenerator<A> for this Queue.
     *
     * Does not implicitly call reset(), and reusing the event queue while using the
     * the generator may cause concurrent modification exceptions to be raised.
     * @return a SequenceGenerator wrapped around the list of typed content events, which
     *         can be used to write() to a target SequenceHandler<A>; never null
     */
    SequenceGenerator<A> getTypedGenerator();
}
