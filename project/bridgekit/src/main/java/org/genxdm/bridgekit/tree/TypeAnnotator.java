package org.genxdm.bridgekit.tree;

import javax.xml.namespace.QName;

public interface TypeAnnotator
{
    void annotate(Object nodeId, QName typeName);
}
