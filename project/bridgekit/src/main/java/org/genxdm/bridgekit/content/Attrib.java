package org.genxdm.bridgekit.content;

import org.genxdm.exceptions.PreCondition;

// shouldn't be used outside the package, but *shrug*
// what if someone wants to implement contenthelper themselves?
public final class Attrib
{
    public Attrib(String name, String value)
    {
        this("", name, value);
    }
    
    public Attrib(String namespace, String name, String value)
    {
       ns = PreCondition.assertNotNull(namespace, "namespace");
       n = PreCondition.assertNotNull(name, "name");
       v = value;
    }
    
    public String getNamespace() { return ns; }
    public String getName() { return n; }
    public String getValue() { return v; }
    
    private final String ns;
    private final String n;
    private final String v;
}
