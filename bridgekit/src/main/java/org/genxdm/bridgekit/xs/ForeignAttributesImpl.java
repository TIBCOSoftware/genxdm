package org.genxdm.bridgekit.xs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.ForeignAttributes;

public class ForeignAttributesImpl
    implements ForeignAttributes, ForeignAttributesSink
{

    @Override
    public Iterable<QName> getForeignAttributeNames()
    {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    @Override
    public String getForeignAttributeValue(QName name)
    {
        return attributes.get(name);
    }

    @Override
    public void putForeignAttribute(QName name, String value)
    {
        attributes.put(name, value);
    }

    private Map<QName, String> attributes = new HashMap<QName, String>();
}
