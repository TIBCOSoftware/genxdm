package org.genxdm.bridge.axiom;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class AttributeWithIdentity
    implements OMAttribute
{
    
    AttributeWithIdentity(OMAttribute attribute)
    {
        this.attribute = attribute;
    }

    @Override
    public String getAttributeType()
    {
        return attribute.getAttributeType();
    }

    @Override
    public String getAttributeValue()
    {
        return attribute.getAttributeValue();
    }

    @Override
    public String getLocalName()
    {
        return attribute.getLocalName();
    }

    @Override
    public OMNamespace getNamespace()
    {
        return attribute.getNamespace();
    }

    @Override
    public OMFactory getOMFactory()
    {
        return attribute.getOMFactory();
    }

    @Override
    public OMElement getOwner()
    {
        return attribute.getOwner();
    }

    @Override
    public QName getQName()
    {
        return attribute.getQName();
    }

    @Override
    public void setAttributeType(String type)
    {
        attribute.setAttributeType(type);
    }

    @Override
    public void setAttributeValue(String value)
    {
        attribute.setAttributeValue(value);
    }

    @Override
    public void setLocalName(String name)
    {
        attribute.setLocalName(name);
    }

    @Override
    public void setOMNamespace(OMNamespace ns)
    {
        attribute.setOMNamespace(ns);
    }
    
    // this is the reason for this to exist
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AttributeWithIdentity)
            return ((AttributeWithIdentity)o).attribute == attribute;
        else if (o instanceof OMAttribute)
            return (OMAttribute)o == attribute;
        return false;
    }
    
    // by the rules, we need to override hashcode, too.
    // however, we *want* the Object hashcode.

    private final OMAttribute attribute;
}
