package org.genxdm.creation;

import java.util.List;

import javax.xml.namespace.QName;

public interface TypedContentEvent<A>
    extends ContentEvent
{
    List<? extends A> getValue();
    
    QName getType();

}
