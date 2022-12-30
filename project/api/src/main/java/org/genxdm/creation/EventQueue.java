package org.genxdm.creation;

import java.util.List;

import org.genxdm.io.ContentGenerator;
import org.genxdm.io.Stateful;

/** A factory for events and a storage medium for the generated queue of
 * events when creating parts of a tree out of document order or conditionally.
 *
 * In general, an EventQueue implementation is expected to implement some interface
 * that allows the creation of the queue, typically ContentHelper. The interface
 * definition does not actually <em>require</em> this, however. It is just the source
 * for the events, however created.
 *
 * The EventQueue interacts with a ContentHelper (the primary document builder) that
 * also implements BranchCopier. This allows the implementation of this interface to
 * collect events (elements, attributes, text) while deferring their analysis until
 * the main helper reaches the insertion point for this subtree. At that point, the
 * BranchCopier.copyTreeAt(ContentGenerator) method may be called with an argument
 * supplied by this.getGenerator().
 *
 * Extends the Stateful interface, which is intended to allow (thread-local/thread-bound,
 * not thread-safe) reuse of the artifact by reinitializing all of its internal state.
 */
public interface EventQueue
    extends Stateful
{
    /** No longer supported; this was the initial means of acquiring the queue of events.
     * @return null, always
     * @deprecated since 1.8.1 ; use {@link #getTyepdGenerator()} instead
     */
    @Deprecated
    List<ContentEvent> getQueue();
    
    /** Get the ContentGenerator for this queue.
     *
     * Does not implicitly call reset(), and reusing the event queue while using the
     * the generator may cause concurrent modification exceptions to be raised.
     * @return a ContentGenerator wrapped around the list of content events, which
     *         can be used to write() to a target ContentHandler; never null
     */
    ContentGenerator getGenerator();
}
