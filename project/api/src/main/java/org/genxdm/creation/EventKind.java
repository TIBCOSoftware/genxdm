package org.genxdm.creation;

/** An enumeration representing possible kinds of events.
 * 
 * ContentEvent-s have different possible parameters; if we describe 
 * them all as being ContentEvent-s, then we can distinguish between them
 * with a type indicator, this EventKind. Note that there are three
 * 'typed' forms included.
 * 
 * These events effectively correspond to methods in ContentHandler/SequenceHandler
 *
 */
public enum EventKind
{
    START_DOCUMENT,
    START_ELEMENT,
    
    NAMESPACE,
    ATTRIBUTE,
    ATTRIBUTE_BINARY,
    
    COMMENT,
    PROCESSING_INSTRUCTION,
    TEXT,
    TEXT_BINARY,
    
    END_ELEMENT,
    END_DOCUMENT;
}
