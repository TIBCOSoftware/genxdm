package org.genxdm.creation;

/** Extends ContentEvent with information about typed events.
 * 
 *
 * @param <A> the base class of the implementations representing typed values.
 */
public interface TypedContentEvent
    extends ContentEvent
{
    // should we also have this? it only returns true when the event can return
    // binary content (including empty binary content (byte array of length 0)).
    // having it might make finding the binary bits easier, but it's not clear
    // that callers ever need to be using this interface, which is mostly
    // intended for consumption by the TypeAwareBranchCopier.
    //boolean isBinaryContent();
    
    /** Get the binary value of this (binary) event
     * 
     * Valid only for binary text and binary attribute events.
     * 
     * @return the byte array for this event (which exists instead of the string value),
     *         empty but not null if initialized as an instance of such an event kind; 
     *         null if called for an inappropriate kind.
     */
    byte [] getValue();
    
}
