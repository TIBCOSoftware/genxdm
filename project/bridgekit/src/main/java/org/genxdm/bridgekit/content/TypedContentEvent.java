package org.genxdm.bridgekit.content;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

public class TypedContentEvent<A>
    extends ContentEvent
{
    // endDocument/endElement
    public TypedContentEvent(EventKind kind)
    {
        super(kind);
        type = null;
        data = null;
    }
    
    // startDocument
    public TypedContentEvent(URI sysId, String internal)
    {
        super(sysId, internal);
        type = null;
        data = null;
    }
    
    // comment (NOT text)
    public TypedContentEvent(EventKind kind, String value)
    {
        super(kind, value);
        type = null;
        data = null;
    }
    
    // namespace, pi
    public TypedContentEvent(EventKind kind, String name, String value)
    {
        super(kind, name, value);
        type = null;
        data = null;
    }
    
    // startElement
    public TypedContentEvent(String ns, String nm, String pr, QName ty)
    {
        super(EventKind.START_TYPED_ELEMENT, (ns == null ? "" : ns), nm, (pr == null ? "" : pr));
        type = ty;
        
        data = null;
    }
    
    // attribute (simple)
    public TypedContentEvent(String nm, List<? extends A> value, QName ty)
    {
        this("", nm, "", value, ty);
    }
    
    // attribute (complete)
    public TypedContentEvent(String ns, String nm, String pr, List<? extends A> value, QName ty)
    {
        super(EventKind.ATTRIBUTE_TYPED, (ns == null ? "" : ns), nm, (pr == null ? "" : pr));
        data = value;
        type = ty;
    }
    
    // text
    public TypedContentEvent(List<? extends A> value)
    {
        super(EventKind.TEXT_TYPED);
        data = value;
        
        type = null;
    }
    
    public List<? extends A> getValue() { return data; }
    
    public QName getType() { return type; }
    
    private final List<? extends A> data;
    private final QName type;
}
