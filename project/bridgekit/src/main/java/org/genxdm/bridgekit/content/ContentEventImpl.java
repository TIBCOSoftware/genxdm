package org.genxdm.bridgekit.content;

import java.net.URI;

import org.genxdm.creation.ContentEvent;
import org.genxdm.creation.EventKind;
import org.genxdm.exceptions.PreCondition;

public class ContentEventImpl
    implements ContentEvent
{
    // use for endDocument/endElement
    public ContentEventImpl(EventKind eventKind)
    {
        kind = PreCondition.assertNotNull(eventKind, "event kind");
        
        uri = null;
        text = namespace = name = prefix = null;
    }
    
    // use for startDocument
    public ContentEventImpl(URI sysId, String internal)
    {
        kind = EventKind.START_DOCUMENT;
        uri = sysId;
        text = internal;
        
        namespace = name = prefix = null;
    }
    
    // use for comment, text
    public ContentEventImpl(EventKind eventKind, String value)
    {
        PreCondition.assertNotNull(eventKind, "event kind");
        if ( (eventKind == EventKind.COMMENT) || (eventKind == EventKind.TEXT) )
        {
            kind = eventKind;
            text = value;
        }
        else
            throw new IllegalArgumentException("Illegal EventKind "+eventKind+" for two-argument constructor");
        uri = null;
        namespace = name = prefix = null;
    }
    
    // use for namespace, simple attribute?, processing instruction 
    public ContentEventImpl(EventKind eventKind, String name, String value)
    {
        PreCondition.assertNotNull(eventKind, "event kind");
        PreCondition.assertNotNull(name, "name");
        switch (eventKind)
        {
            case ATTRIBUTE :
                kind = eventKind;
                this.name = name;
                text = value;
                namespace = prefix = "";
                break;
            case NAMESPACE :
            case PROCESSING_INSTRUCTION :
                kind = eventKind;
                this.name = name;
                text = (value == null) ? "" : value;
                namespace = prefix = null;
                break;
            default :
                throw new IllegalArgumentException("Invalid EventKind "+eventKind+" for three-argument constructor");
        }
        uri = null;
    }
    
    // startElement
    public ContentEventImpl(String ns, String nm, String pr)
    {
        name = PreCondition.assertNotNull(nm, "name");
        kind = EventKind.START_ELEMENT;
        namespace = (ns == null) ? "" : ns;
        prefix = (pr == null) ? "" : pr;
        
        uri = null;
        text = null;
    }
    
    // attribute (note: ASSUMES DtdAttributeKind.CDATA)
    public ContentEventImpl(String ns, String nm, String pr, String value)
    {
        name = PreCondition.assertNotNull(nm, "name");
        kind = EventKind.ATTRIBUTE;
        namespace = (ns == null) ? "" : ns;
        prefix = (pr == null) ? "" : pr;
        text = value;
        
        uri = null;
    }
    
    // used by typed element and typed attribute subclass
    protected ContentEventImpl(EventKind k, String ns, String nm, String pr)
    {
        kind = k;
        namespace = PreCondition.assertNotNull(ns, "namespace");
        name = PreCondition.assertNotNull(nm, "name");
        if (kind == EventKind.START_TYPED_ELEMENT)
            prefix = PreCondition.assertNotNull(pr, "prefix");
        else
            prefix = null;
        if (kind == EventKind.ATTRIBUTE_TYPED)
            text = (pr == null) ? "" : pr;
        else
            text = null;
        
        uri = null;
    }
    
    public EventKind getKind() { return kind; }
    
    public URI getURI() { return uri; }
    
    public String getNamespace() { return namespace; }
    
    public String getName() { return name; }
    
    public String getPrefix() { return prefix; }
    
    public String getText() { return text; }
    
    private final EventKind kind;
    
    private final URI uri;
    
    private final String namespace;
    private final String name; // also used for PI target, namespace prefix
    private final String prefix;

    private final String text; // value of comment, text, pi, namespace, attribute
}
