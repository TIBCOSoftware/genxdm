/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridgetest.utilities;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.genxdm.bridgetest.io.EventKind;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DtdAttributeKind;
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
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record attribute");
                Event e = new Event(EventKind.ATTRIBUTE, namespaceURI, localName, value);
                recorded.add(e);
                handler.attribute(namespaceURI, localName, prefix, value, type);
                break;
            }
            case MATCH:
            {
//System.out.println("match attribute");
                if (!namespacesComplete)
                {
                    checkNamespaces();
                }
                while (recorded.element().kind == EventKind.ATTRIBUTE)
                {
                    toMatch.add(recorded.remove());
                }
                Event e = new Event(EventKind.ATTRIBUTE, namespaceURI, localName, value);
                matches.put(e, value);
                attributesComplete = false;
            }
        }
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record comment");
                Event e = new Event(EventKind.COMMENT, value);
                recorded.add(e);
                handler.comment(value);
                break;
            }
            case MATCH:
            {
//System.out.println("match comment");
                closeStartTag();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.COMMENT) || !e.value.equals(value) )
                    throw new GenXDMException("Mismatch. Expected: " + e.toString()  + " ; Found: COMMENT{}{" + value + "}");
            }
        }
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record end document");
                Event e = new Event(EventKind.END_DOCUMENT);
                recorded.add(e);
                handler.endDocument();
                break;
            }
            case MATCH:
            {
//System.out.println("match end document");
                Event e = recorded.remove();
                if (e.kind != EventKind.END_DOCUMENT)
                    throw new GenXDMException("Mismatch. Expected: " + e.toString() + " ; Found: END_DOCUMENT{}{}");
            }
        }
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record end element");
                Event e = new Event(EventKind.END_ELEMENT);
                recorded.add(e);
                handler.endElement();
                break;
            }
            case MATCH:
            {
//System.out.println("match end element");
                closeStartTag();
                Event e = recorded.remove();
                if (e.kind != EventKind.END_ELEMENT)
                    throw new GenXDMException("Mismatch. Expected: " + e.toString() + " ; Found: END_ELEMENT{}{}");
            }
        }
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        // TODO: if namespaces and attributes are (incorrectly)
        // mixed, as might happen if one calls the contenthandler api
        // directly, then recording will break.  Figure out a fix.
        switch (mode) {
            case RECORD:
            {
//System.out.println("record namespace");
                Event e = new Event(EventKind.NAMESPACE, prefix, namespaceURI);
                recorded.add(e);
                handler.namespace(prefix, namespaceURI);
                break;
            }
            case MATCH:
            {
//System.out.println("match namespace");
                while (recorded.element().kind == EventKind.NAMESPACE)
                {
                    toMatch.add(recorded.remove());
                }
                Event e = new Event(EventKind.NAMESPACE, prefix, namespaceURI);
                matches.put(e, namespaceURI);
                namespacesComplete = false;
            }
        }
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record processing instruction");
                Event e = new Event(EventKind.PROCESSING_INSTRUCTION, target, data);
                recorded.add(e);
                handler.processingInstruction(target, data);
                break;
            }
            case MATCH:
            {
//System.out.println("match processing instruction");
                closeStartTag();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.PROCESSING_INSTRUCTION) || !e.name.equals(target) || !e.value.equals(data) )
                    throw new GenXDMException("Mismatch. Expected: " + e.toString() + " ; Found: PROCESSING_INSTRUCTION{}" + target + "{" + data + "}");
            }
        }
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record start document");
                Event e = new Event(EventKind.START_DOCUMENT, documentURI.toString(), null, docTypeDecl);
                recorded.add(e);
                handler.startDocument(documentURI, docTypeDecl);
                break;
            }
            case MATCH:
            {
//System.out.println("match start document");
                // TODO: handle lack of subset support better?
                String dtd = docTypeDecl == null ? "" : docTypeDecl;
                String uri = documentURI == null ? "" : documentURI.toString();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.START_DOCUMENT) || !e.value.equals(dtd) 
                     || ( !ignoreDocURI && !e.namespace.equals(uri) ) )
                    throw new GenXDMException("Mismatch. Expected: " + e.toString() + " ; Found: START_DOCUMENT{" + uri + "}" + "{" + dtd + "}");
            }
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record start element");
                Event e = new Event(EventKind.START_ELEMENT, namespaceURI, localName, null);
                recorded.add(e);
                handler.startElement(namespaceURI, localName, prefix);
                break;
            }
            case MATCH:
            {
//System.out.println("match start element");
                closeStartTag();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.START_ELEMENT) || !e.namespace.equals(namespaceURI) || !e.name.equals(localName) )
                    throw new GenXDMException("Mismatch. Expected: " + e.toString() + " ; Found: START_ELEMENT{" + namespaceURI + "}" + localName + "{}");
                namespacesComplete = true;
                attributesComplete = true;
            }
        }
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        switch (mode) {
            case RECORD:
            {
//System.out.println("record text");
                Event e = new Event(EventKind.CHARACTERS, data);
                recorded.add(e);
                handler.text(data);
                break;
            }
            case MATCH:
            {
//System.out.println("match text");
                closeStartTag();
                Event e = recorded.remove();
                if ( (e.kind != EventKind.CHARACTERS) || !e.value.equals(data) )
                    throw new GenXDMException("Mismatch. Expected: " + e.toString() + " ; Found: CHARACTERS{}{" + data + "}");
            }
        }
    }
    
    private void closeStartTag()
        throws GenXDMException
    {
//System.out.println("close start tag");
        if (!namespacesComplete)
            checkNamespaces();
        if (!attributesComplete)
            checkAttributes();
    }
    
    private void checkNamespaces()
        throws GenXDMException
    {
//System.out.println("check namespaces");
        for (Event e : toMatch)
        {
            if (!matches.keySet().contains(e))
                throw new GenXDMException("Namespace missing. No match for " + e.toString());
            if (!e.value.equals(matches.get(e)))
                throw new GenXDMException("Namespace value mismatch for " + e.toString() + " :: " + matches.get(e));
//System.out.println("Removing namespace " + e.toString());
            matches.remove(e);
        }
        if (!matches.isEmpty() && !ignoreExtraNamespaces)
        {
            String message = "Extra namespace(s):";
            for (Event e : matches.keySet())
                message += " " + e.toString();
            throw new GenXDMException(message);
        }
        toMatch.clear();
        matches.clear();
        namespacesComplete = true;
    }
    
    private void checkAttributes()
        throws GenXDMException
    {
//System.out.println("check attributes");
        for (Event e : toMatch)
        {
//System.out.println("Matching: " + e.toString());
            if (!matches.keySet().contains(e))
                throw new GenXDMException("Attribute missing. No match for " + e.toString());
            if (!e.value.equals(matches.get(e)))
                throw new GenXDMException("Attribute value mismatch for " + e.toString() + " :: " + matches.get(e));
            matches.remove(e);
        }
        if (!matches.isEmpty())
        {
//System.out.println("Matches is not empty");
            String message = "Extra attribute(s):";
            for (Event e : matches.keySet())
                message += " " + e.toString();
            throw new GenXDMException(message);
        }
        toMatch.clear();
        matches.clear();
        attributesComplete = true;
    }

    private final FragmentBuilder<N> handler;
    private boolean ignoreExtraNamespaces = false;
    private boolean ignoreDocURI = false;
    private boolean namespacesComplete = true;
    private boolean attributesComplete = true;
    private Mode mode; 
    private Queue<Event> recorded = new LinkedList<Event>();
    
    // these two are used to store, and then compare, namespaces and attributes.
    private Set<Event> toMatch = new HashSet<Event>();
    private Map<Event, String> matches = new HashMap<Event, String>();
    
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
        
        @Override
        public int hashCode()
        {
            int result = 17 + namespace.hashCode();
            result = 31 * result + name.hashCode();
            return (13 * result + kind.hashCode());
        }
        
        @Override
        public boolean equals(Object e)
        {
            if (e instanceof Event)
                return hashCode() == e.hashCode();
            return false;
        }
        
        @Override
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
