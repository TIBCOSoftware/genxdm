package org.genxdm.bridgekit.content;

import java.net.URI;

import org.genxdm.creation.EventKind;
import org.genxdm.creation.TypedContentEvent;

// this shouldn't be called a TypedContentEvent in the API, or TypedContentEventImpl here.
// it's not typed; typing happens when the queue is run, not before.
public class TypedContentEventImpl
    extends ContentEventImpl
    implements TypedContentEvent
{
    // endDocument/endElement
    public TypedContentEventImpl(EventKind kind)
    {
        super(kind);
        data = null;
    }
    
    // startDocument (unusual)
    public TypedContentEventImpl(URI sysId, String internal)
    {
        super(sysId, internal);
        data = null;
    }
    
    // comment and non-binary text
    public TypedContentEventImpl(EventKind kind, String value)
    {
        super(kind, value);
        data = null;
    }
    
    // binary text node
    public TypedContentEventImpl(byte [] value)
    {
        super(EventKind.TEXT_BINARY);
        data = value;
    }
    
    // non-binary simple attribute, namespace, pi
    public TypedContentEventImpl(EventKind kind, String name, String value)
    {
        super(kind, name, value);
        data = null;
    }
    
    // startElement
    public TypedContentEventImpl(String ns, String nm, String pr)
    {
        super((ns == null ? "" : ns), nm, (pr == null ? "" : pr));
        data = null;
    }
    
    // non-binary complete attribute
    public TypedContentEventImpl(String ns, String nm, String pr, String value)
    {
        super(ns, nm, pr, value);
        data = null;
    }

    // binary attribute (simple)
    public TypedContentEventImpl(String nm, byte [] value)
    {
        this("", nm, "", value);
    }
    
    // binary attribute (complete)
    public TypedContentEventImpl(String ns, String nm, String pr, byte [] value)
    {
        super(EventKind.ATTRIBUTE_BINARY, (ns == null ? "" : ns), nm, (pr == null ? "" : pr));
        data = value;
    }
    
    @Override
    public byte [] getValue() { return data; }
    
    private final byte [] data;
}
