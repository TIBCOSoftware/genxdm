package org.genxdm.xs.generator;

import javax.xml.namespace.QName;

import org.genxdm.xs.enums.ProcessContentsMode;

public interface AttributeGroupBuilder
{
    // this is a local definition of an attribute; we're not supporting global attributes right now.
    // we also aren't going to support locally-defined types for the attribute; name it to use it.
    // we also don't permit use=prohibited; instead we distinguish between required and optional.
    // to have a fixed value, set the defaultValue to non-null, and set fixed to true. to have a
    // non-fixed default, set defaultValue and leave fixed false.
    void attribute(String name, QName type, boolean required, String defaultValue, boolean fixed);
    
    void attributeGroup(QName ref);
    
    void anyAttribute(ProcessContentsMode processContents, Iterable<String> namespaces);

}
