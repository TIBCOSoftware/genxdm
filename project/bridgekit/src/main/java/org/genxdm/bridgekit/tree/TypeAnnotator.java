package org.genxdm.bridgekit.tree;

import javax.xml.namespace.QName;

public interface TypeAnnotator
{
    /** Associate a node Id with a type name.
     * 
     * @param nodeId The node ID (from Model.getNodeId(node); may not be null.
     * @param typeName The type name.
     */
    void annotate(Object nodeId, QName typeName);
}
