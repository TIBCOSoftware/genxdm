package org.genxdm.bridgekit.content;

import java.util.List;

import org.genxdm.creation.ContentEvent;
import org.genxdm.creation.EventKind;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

final class QueueContentGenerator
    implements ContentGenerator
{
    QueueContentGenerator(final List<ContentEvent> queue)
    {
        m_queue = PreCondition.assertNotNull(queue, "queue");
    }
    
    @Override
    public boolean isElement()
    {
        ContentEvent firstEvent = m_queue.get(1);
        if (firstEvent.getKind() == EventKind.START_ELEMENT)
            return true;
        return false;
    }

    @Override
    public void write(final ContentHandler handler)
    {
        for (ContentEvent event : m_queue)
        {
            EventKind kind = event.getKind();
            switch (kind)
            {
                case START_DOCUMENT : // should never happen; why is there a document in a queue?
                {
                    if (ALLOW_DOCUMENT)
                        handler.startDocument(event.getURI(), event.getText());
                    break;
                }
                case START_ELEMENT :
                {
                    handler.startElement(event.getNamespace(), event.getName(), event.getPrefix());
                    break;
                }
                case NAMESPACE :
                {
                    handler.namespace(event.getName(), event.getText());
                    break;
                }
                case ATTRIBUTE :
                {
                    handler.attribute(event.getNamespace(), event.getName(), event.getPrefix(), event.getText(), DtdAttributeKind.CDATA);
                    break;
                }
                case TEXT :
                {
                    handler.text(event.getText());
                    break;
                }
                case COMMENT :
                {
                    handler.comment(event.getText());
                    break;
                }
                case PROCESSING_INSTRUCTION :
                {
                    handler.processingInstruction(event.getName(), event.getText());
                    break;
                }
                case END_ELEMENT :
                {
                    handler.endElement();
                    break;
                }
                case END_DOCUMENT :
                {
                    if (ALLOW_DOCUMENT)
                        handler.endDocument();
                    break;
                }
                default : // ATTRIBUTE_BINARY or TEXT_BINARY
                {
                    if (ERROR_ON_BINARY)
                        throw new GenXDMException("Unexpected binary content in simple (text-only) queue");
                }
            }
        }
    }
    
    private final List<ContentEvent> m_queue;
    
    private static final boolean ERROR_ON_DOCUMENT = false; // throw an exception if a document is encountered
    private static final boolean ALLOW_DOCUMENT = false; // process the startDocument/endDocument events (will usually cause a fault
    private static final boolean ERROR_ON_BINARY = false;
}