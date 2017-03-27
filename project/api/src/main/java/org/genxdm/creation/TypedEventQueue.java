package org.genxdm.creation;

import java.util.List;

/** A factory for events and a storage medium for the generated queue of
 * events when creating parts of a tree out of document order or conditionally,
 * with typed values and type annotations in the typed events.
 *
 * In general, a TypedEventQueue implementation is expected to implement some interface
 * that allows the creation of the queue, typically ContentHelper. The interface
 * definition does not actually <em>require</em> this, however. It is just the source
 * for the events, however created.
 * @param <A> the base class of the classes implementing typed values
 */
public interface TypedEventQueue<A>
{
    /** Get the queue, of balanced events (an endSomething() for every startSomething()),
     * in order.
     * 
     * @return the generated list of TypedContentEvents, never null, but possibly empty.
     */
    List<TypedContentEvent<A>> getQueue();
}
