package org.genxdm.bridgekit.content;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.creation.ContentEvent;
import org.genxdm.creation.EventKind;
import org.genxdm.creation.TypedContentEvent;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SequenceGenerator;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

/** needs class javadoc
 */
final class TypedQueueGenerator<A>
    implements SequenceGenerator<A>
{
    // class and constructor both package access; not for instantiation by others
    TypedQueueGenerator(final List<TypedContentEvent> actualQueue)
    {
        m_queue = PreCondition.assertNotNull(actualQueue, "queue");
    }
    
    @Override
    public boolean isElement()
    {
        // this is actually an invariant in the current sole impl
        TypedContentEvent firstEvent = m_queue.get(0);
        if (firstEvent.getKind() == EventKind.START_ELEMENT)
            return true;
        return false;
    }
    
    @Override
    public void write(final SequenceHandler<A> handler, final boolean bogus)
    {
        
        if(!(handler instanceof Promoter))
        {
            ValidationHandler<A> validationHalder = (ValidationHandler<A>)handler;
            for(TypedContentEvent event : m_queue)
            {
                EventKind kind = event.getKind();
                switch (kind)
                {
                    case START_DOCUMENT : // should never happen; why is there a document in a queue?
                    {
                        if (ALLOW_DOCUMENT)
                            validationHalder.startDocument(event.getURI(), event.getText());
                        break;
                    }
                    case START_ELEMENT :
                    {
                        validationHalder.startElement(event.getNamespace(), event.getName(), event.getPrefix());
                        break;
                    }
                    case NAMESPACE :
                    {
                        validationHalder.namespace(event.getName(), event.getText());
                        break;
                    }
                    case ATTRIBUTE :
                    {
                        validationHalder.attribute(event.getNamespace(), event.getName(), event.getPrefix(), event.getText(), DtdAttributeKind.CDATA);
                        break;
                    }
                    case ATTRIBUTE_BINARY :
                    {
                        boolean hexed = false;
                        if (m_hexAttributes != null)
                        {
                            final QName attrQ = new QName(event.getNamespace(), event.getName());
                            if (m_hexAttributes.contains(attrQ))
                            {
                                A atom = m_atomBridge.createHexBinary(event.getValue());
                                validationHalder.attribute(event.getNamespace(), event.getName(), event.getPrefix(), m_atomBridge.wrapAtom(atom), null);
                                hexed = true;
                            }
                        }
                        if (!hexed)
                        {
                            A atom = m_atomBridge.createBase64Binary(event.getValue());
                            validationHalder.attribute(event.getNamespace(), event.getName(), event.getPrefix(), m_atomBridge.wrapAtom(atom), null);
                        }
                        break;
                    }
                    case TEXT :
                    {
                        validationHalder.text(event.getText());
                        break;
                    }
                    case TEXT_BINARY :
                    {
                        boolean hexed = false;
                        if (m_hexElements != null)
                        {
                            final QName eQ = new QName(event.getNamespace(), event.getName());
                            if (m_hexElements.contains(eQ))
                            {
                                A atom = m_atomBridge.createHexBinary(event.getValue());
                                validationHalder.text(m_atomBridge.wrapAtom(atom));
                                hexed = true;
                            }
                        }
                        if (!hexed)
                        {
                            A atom = m_atomBridge.createBase64Binary(event.getValue());
                            validationHalder.text(m_atomBridge.wrapAtom(atom));
                        }
                        break;
                    }
                    case COMMENT :
                    {
                        validationHalder.comment(event.getText());
                        break;
                    }
                    case PROCESSING_INSTRUCTION :
                    {
                        validationHalder.processingInstruction(event.getName(), event.getText());
                        break;
                    }
                    case END_ELEMENT :
                    {
                        validationHalder.endElement();
                        break;
                    }
                    case END_DOCUMENT :
                    {
                        if (ALLOW_DOCUMENT)
                            validationHalder.endDocument();
                        break;
                    }
                    default :
                    {
                        // TODO: no more event kinds (I hope)
                    }
                }
                
            }
        }
        else
        {
            // this is a down-and-to-the-side cast    
            Promoter promoter = (Promoter)handler;
            for (TypedContentEvent event : m_queue)
            {
                EventKind kind = event.getKind();
                switch (kind)
                {
                    case START_DOCUMENT : // should never happen; why is there a document in a queue?
                    {
                        if (ALLOW_DOCUMENT)
                            promoter.startDocument(event.getURI(), event.getText());
                        break;
                    }
                    case START_ELEMENT :
                    {
                        promoter.startElement(event.getNamespace(), event.getName(), event.getPrefix());
                        break;
                    }
                    case NAMESPACE :
                    {
                        promoter.namespace(event.getName(), event.getText());
                        break;
                    }
                    case ATTRIBUTE :
                    {
                        handler.attribute(event.getNamespace(), event.getName(), event.getPrefix(), event.getText(), DtdAttributeKind.CDATA);
                        break;
                    }
                    case ATTRIBUTE_BINARY :
                    {
                        promoter.binaryAttribute(event.getNamespace(), event.getName(), event.getPrefix(), event.getValue());
                        break;
                    }
                    case TEXT :
                    {
                        promoter.text(event.getText());
                        break;
                    }
                    case TEXT_BINARY :
                    {
                        promoter.binaryText(event.getValue());
                        break;
                    }
                    case COMMENT :
                    {
                        promoter.comment(event.getText());
                        break;
                    }
                    case PROCESSING_INSTRUCTION :
                    {
                        promoter.processingInstruction(event.getName(), event.getText());
                        break;
                    }
                    case END_ELEMENT :
                    {
                        promoter.endElement();
                        break;
                    }
                    case END_DOCUMENT :
                    {
                        if (ALLOW_DOCUMENT)
                            promoter.endDocument();
                        break;
                    }
                    default :
                    {
                        // TODO: no more event kinds (I hope)
                    }
                }
            }
        }

    }
    
    @Override
    public void write(final ContentHandler handler)
    {
        // this isn't actually used, and since we're package access, no one outside the
        // package should be able to use it. impl here for completeness, but i have
        // doubts about allowing it.
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
    
    public void injectAtomBridge(AtomBridge<A> m_atomBridge)
    {
        this.m_atomBridge = m_atomBridge;
    }
    
    public void injectHexLists(Set<QName> hexElements, Set<QName> hexAtts)
    {
        this.m_hexAttributes = hexAtts;
        this.m_hexElements = hexElements;
    }
    
    private final List<TypedContentEvent> m_queue;
    private AtomBridge<A> m_atomBridge;
    private Set<QName> m_hexElements;
    private Set<QName> m_hexAttributes;
    // not used here
//    private static final boolean ERROR_ON_DOCUMENT = false; // throw an exception if a document is encountered
    private static final boolean ALLOW_DOCUMENT = false; // process the startDocument/endDocument events (will usually cause a fault
    private static final boolean ERROR_ON_BINARY = false;
}