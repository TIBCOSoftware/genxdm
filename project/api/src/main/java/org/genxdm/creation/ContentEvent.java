package org.genxdm.creation;

import java.net.URI;

/** Represents a stored/generated event corresponding to a ContentHandler/SequenceHandler
 * method as created and stored inside an EventQueue.
 * 
 *
 */
public interface ContentEvent
{
    /** The 'type' of this event, corresponding to the method that will
     * ultimately be called when the event is fired.
     * 
     * @return the kind of the event, never null.
     */
    EventKind getKind();
    
    /** Valid only for startDocument() events/invocations.
     * 
     * @return the URI supplied to the event as the systemID, often null
     */
    URI getURI();
    
    /** The namespace with which this event is associated.
     * 
     * Valid for startElement() and attribute(), typed and untyped.
     * 
     * @return the namespace if appropriate for the kind, which will consequently
     *         never be null, or null for event kinds for which this is irrelevant
     */
    String getNamespace();
    
    /** The name of this event.
     * 
     * Elements, attributes, namespaces (the prefix-binding part of a namespace is its name),
     * and processing instructions.
     * 
     * end events, documents, text, and comment events are unnamed.
     * 
     * @return non-null and non-empty for named events; null for unnamed events.
     */
    String getName();
    
    /** The prefix associated with this event.
     * 
     * Only elements and attributes have prefixes associated; they always do.
     * True for both typed and untyped startElement() and attribute() events.
     * 
     * @return the prefix associated with this startelement or attribute event, never
     *         null; always null for other event kinds
     */
    String getPrefix();
    
    /** The text value of the event if appropriate for the kind
     * 
     * startdocument, startelement, endelement, enddocument do not have text value;
     * the text value of a namespace is its namespace URI; of an attribute is its
     * value, of a text node similarly, and for a comment the comment text. A processing
     * instruction distinguishes between its target and its value; the text is its value.
     * 
     * typed text and attribute events may return the canonicalization of their
     * typed value or null; don't invoke this method on them.
     * 
     * @return the text value of the event, if any, or null if it has none
     */
    String getText();

}
