package org.genxdm.bridgetest.utilities;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.genxdm.exceptions.GxmlException;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.EventKind;
import org.genxdm.io.FragmentBuilder;

/** Abstraction of the events that occur inside a content handler.
 * 
 * The events must occur in a specific order for most of the parts of an
 * XML document; attributes and namespaces may occur in any order.
 *
 * Usage: first play a well-known document into this ContentHandler, in record mode.
 * Then play the same document (sequence of ContentHandler calls) into
 * a FragmentBuilder.  Then use either Model.stream or Cursor.write, supplying
 * this ContentHandler, in match mode.  The initial stream of events will be
 * checked against the stored sequence, with namespaces and attributes permitted
 * to fire 'out of order', so long as the set of each is correct (and namespaces
 * must fire before attributes).
 * 
 * Warning: this is a little lossy.  We lose prefix hints (for attributes and
 * elements) and DTD Attribute Kinds (for attributes).
 * 
 */
public class Events<N> implements FragmentBuilder<N>
{
    public Events(FragmentBuilder<N> handler)
    {
        this.handler = handler;
    }

    public void ignoreExtraNamespaceDeclarations()
    {
        ignoreExtraNamespaces = true;
    }
    
    public void ignoreDocumentURI()
    {
        ignoreDocURI = true;
    }
    
    public void record()
    {
        reset();
        mode = Mode.RECORD;
    }
    
    public void match()
    {
        mode = Mode.MATCH;
    }
    
    @Override
    public List<N> getNodes()
    {
        return handler.getNodes();
    }

    @Override
    public N getNode()
    {
        return handler.getNode();
    }

    @Override
    public void reset()
    {
        handler.reset();
        recorded.clear();
    }
    
    @Override
    public void close()
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void flush()
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.ATTRIBUTE, namespaceURI, localName, value);
                recorded.add(e);
                handler.attribute(namespaceURI, localName, prefix, value, type);
                break;
            }
            case MATCH:
            {
                if (!namespacesComplete)
                {
                    checkNamespaces();
                }
                while (recorded.element().kind == EventKind.ATTRIBUTE)
                {
                    toMatch.add(recorded.remove());
                }
                Event e = new Event(EventKind.ATTRIBUTE, namespaceURI, localName, value);
                matches.add(e);
                attributesComplete = false;
            }
        }
    }

    @Override
    public void comment(String value)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.COMMENT, value);
                recorded.add(e);
                handler.comment(value);
                break;
            }
            case MATCH:
            {
                childPreMatch();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.COMMENT) || !e.value.equals(value) )
                    throw new GxmlException("Mismatch. Expected: " + e.toString()  + " ; Found: COMMENT{}{" + value + "}");
            }
        }
    }

    @Override
    public void endDocument()
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.END_DOCUMENT);
                recorded.add(e);
                handler.endDocument();
                break;
            }
            case MATCH:
            {
                Event e = recorded.remove();
                if (e.kind != EventKind.END_DOCUMENT)
                    throw new GxmlException("Mismatch. Expected: " + e.toString() + " ; Found: END_DOCUMENT{}{}");
            }
        }
    }

    @Override
    public void endElement()
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.END_ELEMENT);
                recorded.add(e);
                handler.endElement();
                break;
            }
            case MATCH:
            {
                childPreMatch();
                Event e = recorded.remove();
                if (e.kind != EventKind.END_ELEMENT)
                    throw new GxmlException("Mismatch. Expected: " + e.toString() + " ; Found: END_ELEMENT{}{}");
            }
        }
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.NAMESPACE, prefix, namespaceURI);
                recorded.add(e);
                handler.namespace(prefix, namespaceURI);
                break;
            }
            case MATCH:
            {
                while (recorded.element().kind == EventKind.NAMESPACE)
                {
                    toMatch.add(recorded.remove());
                }
                Event e = new Event(EventKind.NAMESPACE, prefix, namespaceURI);
                matches.add(e);
                namespacesComplete = false;
            }
        }
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.PROCESSING_INSTRUCTION, target, data);
                recorded.add(e);
                handler.processingInstruction(target, data);
                break;
            }
            case MATCH:
            {
                childPreMatch();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.PROCESSING_INSTRUCTION) || !e.name.equals(target) || !e.value.equals(data) )
                    throw new GxmlException("Mismatch. Expected: " + e.toString() + " ; Found: PROCESSING_INSTRUCTION{}" + target + "{" + data + "}");
            }
        }
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.START_DOCUMENT, documentURI.toString(), null, docTypeDecl);
                recorded.add(e);
                handler.startDocument(documentURI, docTypeDecl);
                break;
            }
            case MATCH:
            {
                // TODO: handle lack of subset support better?
                String dtd = docTypeDecl == null ? "" : docTypeDecl;
                String uri = documentURI == null ? "" : documentURI.toString();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.START_DOCUMENT) || !e.value.equals(dtd) 
                     || ( !ignoreDocURI && !e.namespace.equals(uri) ) )
                    throw new GxmlException("Mismatch. Expected: " + e.toString() + " ; Found: START_DOCUMENT{" + uri + "}" + "{" + dtd + "}");
            }
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.START_ELEMENT, namespaceURI, localName, null);
                recorded.add(e);
                handler.startElement(namespaceURI, localName, prefix);
                break;
            }
            case MATCH:
            {
                childPreMatch();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.START_ELEMENT) || !e.namespace.equals(namespaceURI) || !e.name.equals(localName) )
                    throw new GxmlException("Mismatch. Expected: " + e.toString() + " ; Found: START_ELEMENT{" + namespaceURI + "}" + localName + "{}");
                namespacesComplete = true;
                attributesComplete = true;
            }
        }
    }

    @Override
    public void text(String data)
        throws GxmlException
    {
        switch (mode) {
            case RECORD:
            {
                Event e = new Event(EventKind.CHARACTERS, data);
                recorded.add(e);
                handler.text(data);
                break;
            }
            case MATCH:
            {
                childPreMatch();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.CHARACTERS) || !e.value.equals(data) )
                    throw new GxmlException("Mismatch. Expected: " + e.toString() + " ; Found: CHARACTERS{}{" + data + "}");
            }
        }
    }
    
    private void childPreMatch()
        throws GxmlException
    {
        if (!namespacesComplete)
            checkNamespaces();
        if (!attributesComplete)
            checkAttributes();
    }
    
    private void checkNamespaces()
        throws GxmlException
    {
        // TODO: iterate over both sets
        // TODO: handle ignoreExtraNamespaces
        toMatch.clear();
        matches.clear();
        namespacesComplete = true;
    }
    
    private void checkAttributes()
        throws GxmlException
    {
        // TODO: iterate over both sets
        toMatch.clear();
        matches.clear();
        attributesComplete = true;
    }

    private final FragmentBuilder<N> handler;
    private boolean ignoreExtraNamespaces = false;
    private boolean ignoreDocURI = false;
    private boolean namespacesComplete = false;
    private boolean attributesComplete = false;
    private Mode mode; 
    private Queue<Event> recorded = new LinkedList<Event>();
    
    // these two are used to store, and then compare, namespaces and attributes.
    private Set<Event> toMatch = new HashSet<Event>();
    private Set<Event> matches = new HashSet<Event>();
    
    private static enum Mode { RECORD, MATCH }
    
    static class Event
    {
        // startdocument should call kind, namespace, null, value
        // startelement should call kind, namespace, name, null
        // attributes in namespaces call kind, namespace, name, value
        Event(EventKind kind, String namespace, String name, String value) 
        { this.kind = kind; this.namespace = (namespace == null ? "" : namespace); this.name = (name == null ? "" : name); this.value = (value == null ? "" : value); }
        
        // shortcut for processing instructions, and for namespaces
        Event(EventKind kind, String name, String value) { this(kind, null, name, value); }
        
        // use for text and comments
        Event(EventKind kind, String value) { this(kind, null, null, value); }
        
        // use for end events
        Event(EventKind kind) { this(kind, null, null, null); }
        
        public String toString()
        {
            return kind.toString() + "{" + namespace + "}" + name + "{" + value + "}";
        }
        
        final EventKind kind;
        final String namespace;
        final String name;
        final String value;
    }

}
