package org.genxdm.bridgekit.content;

import java.util.List;

import javax.xml.namespace.QName;

public class TypedContentEvent<A>
    extends ContentEvent
{
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
