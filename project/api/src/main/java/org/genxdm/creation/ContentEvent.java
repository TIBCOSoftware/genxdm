package org.genxdm.creation;

import java.net.URI;

public interface ContentEvent
{
    EventKind getKind();
    
    URI getURI();
    
    String getNamespace();
    
    String getName();
    
    String getPrefix();
    
    String getText();

}
