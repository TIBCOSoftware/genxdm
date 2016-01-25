package org.genxdm.bridgekit.content;

public enum EventKind
{
    START_DOCUMENT,
    START_ELEMENT,
    START_TYPED_ELEMENT,
    
    NAMESPACE,
    ATTRIBUTE,
    ATTRIBUTE_TYPED,
    
    COMMENT,
    PROCESSING_INSTRUCTION,
    TEXT,
    TEXT_TYPED,
    
    END_ELEMENT,
    END_DOCUMENT;
}
