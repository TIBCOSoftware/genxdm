package org.genxdm.bridgekit.content;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.creation.ContentEvent;
import org.genxdm.creation.EventKind;
import org.genxdm.creation.TypedContentEvent;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceHandler;

public class EventQueueDriver
{
    private EventQueueDriver() {}
    
    // TODO: maybe actually check for errors and stuff?
    
    public static void driveQueue(Iterable<ContentEvent> queue, ContentHandler untypedOutput)
    {
        for (ContentEvent event : queue)
        {
            EventKind kind = event.getKind();
            switch (kind)
            {
                case START_DOCUMENT :
                {
                    untypedOutput.startDocument(event.getURI(), event.getText());
                }
                case START_ELEMENT :
                {
                    untypedOutput.startElement(event.getNamespace(), event.getName(), event.getPrefix());
                }
                case NAMESPACE :
                {
                    untypedOutput.namespace(event.getName(), event.getText());
                }
                case ATTRIBUTE :
                {
                    untypedOutput.attribute(event.getNamespace(), event.getName(), event.getPrefix(), event.getText(), DtdAttributeKind.CDATA);
                }
                case TEXT :
                {
                    untypedOutput.text(event.getText());
                }
                case COMMENT :
                {
                    untypedOutput.comment(event.getText());
                }
                case PROCESSING_INSTRUCTION :
                {
                    untypedOutput.processingInstruction(event.getName(), event.getText());
                }
                case END_ELEMENT :
                {
                    untypedOutput.endElement();
                }
                case END_DOCUMENT :
                {
                    untypedOutput.endDocument();
                }
                default :
                {
                    // throw an exception.
                }
            }
        }
    }
    
    public static <A> void driveTypedQueue(Iterable<TypedContentEvent<A>> queue, SequenceHandler<A> typedOutput)
    {
        for (TypedContentEvent<A> event : queue)
        {
            EventKind kind = event.getKind();
            switch (kind)
            {
                case START_DOCUMENT :
                {
                    typedOutput.startDocument(event.getURI(), event.getText());
                }
                case START_TYPED_ELEMENT :
                {
                    typedOutput.startElement(event.getNamespace(), event.getName(), event.getPrefix(), event.getType());
                }
                case NAMESPACE :
                {
                    typedOutput.namespace(event.getName(), event.getText());
                }
                case ATTRIBUTE_TYPED :
                {
                    typedOutput.attribute(event.getNamespace(), event.getName(), event.getPrefix(), event.getValue(), event.getType());
                }
                case TEXT_TYPED :
                {
                    typedOutput.text(event.getValue());
                }
                case COMMENT :
                {
                    typedOutput.comment(event.getText());
                }
                case PROCESSING_INSTRUCTION :
                {
                    typedOutput.processingInstruction(event.getName(), event.getText());
                }
                case END_ELEMENT :
                {
                    typedOutput.endElement();
                }
                case END_DOCUMENT :
                {
                    typedOutput.endDocument();
                }
                // untyped in this context is an error
                case START_ELEMENT :
                case ATTRIBUTE :
                case TEXT :
                default :
                {
                    // TODO: throw an exception
                }
            }
        }
    }
    
    public static List<ContentEvent> newUntypedQueue()
    {
        return new ArrayList<ContentEvent>();
    }
    
    public static <A> List<TypedContentEvent<A>> newTypedQueue()
    {
        return new ArrayList<TypedContentEvent<A>>();
    }
}
