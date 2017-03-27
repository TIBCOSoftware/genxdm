package org.genxdm.creation;

import java.util.List;

import javax.xml.namespace.QName;

/** Extends ContentEvent with information about typed events.
 * 
 *
 * @param <A> the base class of the implementations representing typed values.
 */
public interface TypedContentEvent<A>
    extends ContentEvent
{
    /** Get the typed value of this event
     * 
     * Valid only for typed text and typed attribute events.
     * 
     * @return the typed value of this event, if appropriate for the event kind,
     *         thus possibly empty but never null; null if called for an inappropriate
     *         kind.
     */
    List<? extends A> getValue();
    
    /** Get the type annotation associated with this event.
     * 
     * Valid only for typed element and typed attribute events.
     * 
     * @return the type of this event, if appropriate for the kind, never null;
     *         for inappropriate kinds, always null.
     */
    QName getType();

}
