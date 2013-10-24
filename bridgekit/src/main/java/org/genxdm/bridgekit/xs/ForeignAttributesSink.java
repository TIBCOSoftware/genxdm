package org.genxdm.bridgekit.xs;

import javax.xml.namespace.QName;

public interface ForeignAttributesSink
{
    void putForeignAttribute(QName name, String value);

}
