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
    START_DOCUMENT, // startDocument()
    START_ELEMENT, // ContentHandler.startElement()
    START_TYPED_ELEMENT, //SequenceHandler.startElement() (includes type)
    
    NAMESPACE, // namespace()
    ATTRIBUTE, // ContentHandler.attribute()
    ATTRIBUTE_TYPED, // SequenceHandler.attribute() (includes type and typed value)
    
    COMMENT, // comment()
    PROCESSING_INSTRUCTION, //processingInstruction()
    TEXT, // ContentHandler.text()
    TEXT_TYPED, // SequenceHandler.text() (includes typed value)
    
    END_ELEMENT, // endElement()
    END_DOCUMENT; // endDocument()
}
