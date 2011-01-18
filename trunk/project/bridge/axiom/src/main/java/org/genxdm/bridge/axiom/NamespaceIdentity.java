package org.genxdm.bridge.axiom;

import org.apache.axiom.om.OMNamespace;

public class NamespaceIdentity
{
    NamespaceIdentity(OMNamespace ns)
    {
        this.namespace = ns;
    }
    
    // this is the reason for this to exist
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof NamespaceIdentity)
            return ((NamespaceIdentity)o).namespace == namespace;
        else if (o instanceof OMNamespace)
            return (OMNamespace)o == namespace;
        return false;
    }
    
    // by the rules, we need to override hashcode, too.
    // however, we *want* the Object hashcode.

    private final OMNamespace namespace;

}
