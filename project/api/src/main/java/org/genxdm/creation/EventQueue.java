package org.genxdm.creation;

import java.util.List;

/** A factory for events and a storage medium for the generated queue of
 * events when creating parts of a tree out of document order or conditionally.
 *
 * In general, an EventQueue implementation is expected to implement some interface
 * that allows the creation of the queue, typically ContentHelper. The interface
 * definition does not actually <em>require</em> this, however. It is just the source
 * for the events, however created.
 */
public interface EventQueue
{
    /** Get the queue, of balanced events (an endSomething() for every startSomething()),
     * in order.
     * 
     * @return the generated list of ContentEvents, never null, but possibly empty.
     */
    List<ContentEvent> getQueue();
}
