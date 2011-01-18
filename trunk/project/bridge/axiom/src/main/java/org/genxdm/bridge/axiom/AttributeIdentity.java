package org.genxdm.bridge.axiom;

import org.apache.axiom.om.OMAttribute;

public class AttributeIdentity
{
    
    AttributeIdentity(OMAttribute attribute)
    {
        this.attribute = attribute;
    }

    // this is the reason for this to exist
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AttributeIdentity)
            return ((AttributeIdentity)o).attribute == attribute;
        else if (o instanceof OMAttribute)
            return (OMAttribute)o == attribute;
        return false;
    }
    
    // by the rules, we need to override hashcode, too.
    // however, we *want* the Object hashcode.

    private final OMAttribute attribute;
}
