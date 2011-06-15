/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.processor.input;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.genxdm.exceptions.XdmMarshalException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

public class XmlEventVisitor
    implements XMLStreamConstants
{
    public XmlEventVisitor(final XMLEventReader reader, final ContentHandler handler)
    {
        this.reader = PreCondition.assertNotNull(reader, "reader");
        this.handler = PreCondition.assertNotNull(handler, "handler");
    }
    
    public void setSystemId(URI systemId)
    {
        this.docURI = systemId;
    }
    
    /** This guarantees that a document event will be fired.  Thus, there will always
     * be a single node returned by a FragmentBuilder.
     * 
     */
    public void parse()
        throws IOException, XdmMarshalException
    {
        // TODO: this probably makes it impossible to read an external parsed entity (fragment).
        // that needs some consideration.
        boolean manualStart = false;
        try
        {
            if (reader.hasNext())
            {
                if (!reader.peek().isStartDocument())
                {
                    storeEvent(new StoredStartDoc(docURI == null ? null : docURI.toString()));
                    manualStart = true;
                }
            }
        }
        catch (XMLStreamException xse)
        {
            throw new XdmMarshalException(xse);
        }
        boolean endedDocument = parseFragment();
        if (manualStart && !endedDocument)
            handler.endDocument();
    }
    
    /** This does the interesting work, overall.
     * 
     */
    public boolean parseFragment()
        throws IOException, XdmMarshalException
    {
        // TODO: you can't actually call this, right now.
        // figure out a good way to enable fragment parsing.
        boolean ended = false;
        while (reader.hasNext())
        {
            try
            {
                XMLEvent event = reader.nextEvent();
                if (inProlog)
                {
                    // "inProlog" isn't quite accurate; we just want the prolog that extends
                    // from the start of the document through the dtd.  anything after that
                    // can be handled in order, without queueing.
                    if (event.isStartElement() || (event.getEventType() == DTD))
                        processQueue(event);
                    else
                        storeEvent(event);
                }
                else if (event.isStartDocument() || event.isCharacters())
                    storeEvent(event);
                else if (!eventQueue.isEmpty())
                    processQueue(event);
                else
                {
                    if (processEvent(event))
                        ended = true;
                }
            }
            catch (XMLStreamException xse)
            {
                throw new XdmMarshalException(xse);
            }
        }
        return ended;
    }
    
    @SuppressWarnings("unchecked")
    private boolean processEvent(XMLEvent event)
        throws XMLStreamException
    {
        int eventType = event.getEventType();
        switch (eventType)
        {
            case START_DOCUMENT :
            {
                StartDocument sdoc = (StartDocument)event;
                URI uri = null;
                try
                {
                    if (sdoc.getSystemId() != null)
                        uri = new URI(sdoc.getSystemId());
                }
                catch (URISyntaxException mu)
                {
                    throw new XMLStreamException(mu);
                }
                if (uri == null)
                    uri = docURI;
                handler.startDocument(uri, null);
                break;
            }
            case PROCESSING_INSTRUCTION :
            {
                ProcessingInstruction pi = (ProcessingInstruction)event;
                handler.processingInstruction(pi.getTarget(), pi.getData());
                break;
            }
            case COMMENT :
            {
                Comment comment = (Comment)event;
                handler.comment(comment.getText());
                break;
            }
            case START_ELEMENT :
            {
                depth++;
                StartElement selem = event.asStartElement();
                handler.startElement(selem.getName().getNamespaceURI(), selem.getName().getLocalPart(), selem.getName().getPrefix());
                // unchecked cast from Iterator to Iterator<Namespace> (with type erasure back)
                Iterator<Namespace> namespaces = selem.getNamespaces();
                while (namespaces.hasNext())
                {
                    Namespace namespace = namespaces.next();
                    handler.namespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                // unchecked cast from Iterator to Iterator<Attribute> (with type erasure back)
                Iterator<Attribute> attributes = selem.getAttributes();
                while (attributes.hasNext())
                {
                    Attribute attribute = attributes.next();
                    String localPart = attribute.getName().getLocalPart();
                    // This special case seems to be necessary for some StAX parsers that have a bug,
                    // and report the namespace to be "" when it in fact is the canonical namespace for "xml:"
                    if (localPart.startsWith("xml:")) {
                        // TODO - figure out why this work-around is necessary!
                        handler.attribute(XMLConstants.XML_NS_URI, localPart.substring(4), "xml", attribute.getValue(), DtdAttributeKind.get(attribute.getDTDType()));
                    }
                    else {
                        handler.attribute(attribute.getName().getNamespaceURI(), localPart, attribute.getName().getPrefix(), attribute.getValue(), DtdAttributeKind.get(attribute.getDTDType()));
                    }
                }
                break;
            }
            // these two don't happen; even though this is supposed to be an
            // event iteration api; attributes and namespaces are bastard children
            // in stax, beaten when they aren't ignored.
            // why the events exist at all is something of a mystery.
//            case ATTRIBUTE :
//            {
//                Attribute attr = (Attribute)event;
//                handler.attribute(attr.getName().getNamespaceURI(), attr.getName().getLocalPart(), attr.getName().getPrefix(), attr.getValue(), DtdAttributeKind.get(attr.getDTDType()));
//                break;
//            }
//            case NAMESPACE :
//            {
//                Namespace ns = (Namespace)event;
//                handler.namespace(ns.getPrefix(), ns.getNamespaceURI());
//                break;
//            }
            case CDATA :
            case SPACE :
            case CHARACTERS :
            {
                if (depth > 0) // don't even try to create text nodes in a document (many apis break)
                {
                    Characters chars = event.asCharacters();
                    handler.text(chars.getData());
                }
                break;
            }
            case END_ELEMENT :
            {
                depth--;
//                EndElement eelem = (EndElement)event;
                handler.endElement();
                break;
            }
            case END_DOCUMENT :
            {
//                EndDocument edoc = (EndDocument)event;
                handler.endDocument();
                return true;
            }
            default :
                // entity and notation declarations, entity references if unresolved.
                throw new XMLStreamException("Unknown event type " + eventType);
        }
        return false;
    }
    
    private void processQueue(XMLEvent event)
        throws XMLStreamException
    {
        if (eventQueue.peek().isStartDocument()) // fortunately, startDocument can't be out of order
        {
            // the only place that a DTD can be is as the current event.
            if (event.getEventType() == DTD)
            {
                // if the current event is a DTD, handle startdoc + dtd in one call
                URI uri = null;
                StartDocument sdoc = (StartDocument)eventQueue.poll();
                try
                {
                    if ( sdoc.getSystemId() != null )
                        uri = new URI(sdoc.getSystemId());
                }
                catch (URISyntaxException mu)
                {
                    throw new XMLStreamException(mu);
                }
                if (uri == null)
                    uri = docURI;
                handler.startDocument(uri, ((javax.xml.stream.events.DTD)event).getDocumentTypeDeclaration());
                while (!eventQueue.isEmpty())
                {
                     processEvent(eventQueue.poll());
                }
            }
            else
            {
                // otherwise, handle as normal:
                do {
                    processEvent(eventQueue.poll());
                } while (!eventQueue.isEmpty());
                processEvent(event);
            }
            inProlog = false;
        }
        else // must be characters ...
        {
            Characters c = null;
            while (!eventQueue.isEmpty())
            {
                c = coalesce(c, eventQueue.poll());
            }
            processEvent(c);
            // we know that whatever the current event is, it isn't characters.
            processEvent(event);
        }
    }
    
    private Characters coalesce(Characters first, XMLEvent next)
    {
        if (first != null)
        {
            StoredCharacters f = (StoredCharacters)first;
            StoredCharacters n = (StoredCharacters)next;
            // all three booleans can only be true if both sources are true.
            f.ws = f.ws && n.ws;
            f.igws = f.igws && n.igws;
            f.cd = f.cd && n.cd;
            f.data += n.data;
        }
        else
            first = (Characters)next;
        return first;
    }
    
    private void storeEvent(XMLEvent event)
        throws XMLStreamException
    {
        if (event.isCharacters() && !inProlog)
        {
            eventQueue.add(new StoredCharacters(event));
        }
        else
        {
            if (event.isStartDocument())
            {
                eventQueue.add(new StoredStartDoc(event));
                inProlog = true;
            }
            else if (event.isProcessingInstruction())
                eventQueue.add(new StoredPI(event));
            else if (event.getEventType() == COMMENT)
                eventQueue.add(new StoredComment(event));
        }
    }
    
    private abstract class StoredEvent
        implements XMLEvent
    {
        StoredEvent(XMLEvent unstoredEvent)
        {
            this.type = unstoredEvent.getEventType();
            this.location = unstoredEvent.getLocation();
        }
        StoredEvent(int t)
        {
            this.type = t;
        }
        public Characters asCharacters()
        {
            throw new ClassCastException();
        }
        public EndElement asEndElement()
        {
            throw new ClassCastException();
        }
        public StartElement asStartElement()
        {
            throw new ClassCastException();
        }
        public int getEventType()
        {
            return type;
        }
        public Location getLocation()
        {
            return location;
        }
        public QName getSchemaType()
        {
            return schemaType;
        }
        public boolean isAttribute()
        {
            return false;
        }
        public boolean isCharacters()
        {
            return false;
        }
        public boolean isEndDocument()
        {
            return false;
        }
        public boolean isEndElement()
        {
            return false;
        }
        public boolean isEntityReference()
        {
            return false;
        }
        public boolean isNamespace()
        {
            return false;
        }
        public boolean isProcessingInstruction()
        {
            return false;
        }
        public boolean isStartDocument()
        {
            return false;
        }
        public boolean isStartElement()
        {
            return false;
        }
        public void writeAsEncodedUnicode(Writer writer)
            throws XMLStreamException
        {
            // oh, barf.
            // TODO: should we implement this?  no one else should see these.
        }
        private int type;
        private Location location;
        private QName schemaType;
    }
    
    private class StoredStartDoc
        extends StoredEvent
        implements StartDocument
    {
        StoredStartDoc(XMLEvent event)
        {
            super(event);
            StartDocument sd = (StartDocument)event;
            this.version = sd.getVersion();
            this.encoding = sd.getCharacterEncodingScheme();
            this.systemId = sd.getSystemId();
            this.sa = sd.isStandalone();
            this.saSet = sd.standaloneSet();
            this.encSet = sd.encodingSet();
        }
        StoredStartDoc(String sysID)
        {
            super(START_DOCUMENT);
            this.version = "1.0";
            this.systemId = sysID;
            this.encSet = false;
            this.saSet = false;
        }
        public boolean isStartDocument()
        {
            return true;
        }
        public boolean encodingSet()
        {
            return encSet;
        }
        public String getCharacterEncodingScheme()
        {
            return encoding;
        }
        public String getSystemId()
        {
            return systemId;
        }
        public String getVersion()
        {
            return version;
        }
        public boolean isStandalone()
        {
            return sa;
        }
        public boolean standaloneSet()
        {
            return saSet;
        }
        private String version;
        private String encoding;
        private String systemId;
        private boolean saSet;
        private boolean encSet;
        private boolean sa;
    }
    
    private class StoredPI
        extends StoredEvent
        implements ProcessingInstruction
    {
        StoredPI(XMLEvent event)
        {
            super(event);
            ProcessingInstruction pi = (ProcessingInstruction)event;
            this.data = pi.getData();
            this.target = pi.getTarget();
        }
        public String getData()
        {
            return data;
        }
        public String getTarget()
        {
            return target;
        }
        private String data;
        private String target;
    }
    
    private class StoredComment
        extends StoredEvent
        implements Comment
    {
        StoredComment(XMLEvent event)
        {
            super(event);
            Comment comment = (Comment)event;
            this.text = comment.getText();
        }
        public String getText()
        {
            return text;
        }
        private String text;
    }
    
    private class StoredCharacters
        extends StoredEvent
        implements Characters
    {
        StoredCharacters(XMLEvent event)
        {
            super(event);
            Characters source = (Characters)event;
            this.ws = source.isWhiteSpace();
            this.igws = source.isIgnorableWhiteSpace();
            this.cd = source.isCData();
            this.data = source.getData();
        }
        public Characters asCharacters()
        {
            return this;
        }
        public boolean isCharacters()
        {
            return true;
        }
        public String getData()
        {
            return data;
        }
        public boolean isCData()
        {
            return cd;
        }
        public boolean isIgnorableWhiteSpace()
        {
            return igws;
        }
        public boolean isWhiteSpace()
        {
            return ws;
        }
        boolean ws;
        boolean igws;
        boolean cd;
        String data;
    }
    
    private final XMLEventReader reader;
    private final ContentHandler handler;
    private final Queue<XMLEvent> eventQueue = new LinkedList<XMLEvent>();
    private URI docURI;
    private int depth = 0;
    private boolean inProlog = false;
}
