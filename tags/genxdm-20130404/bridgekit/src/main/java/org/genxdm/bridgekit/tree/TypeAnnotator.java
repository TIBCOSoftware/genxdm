package org.genxdm.bridgekit.tree;

import javax.xml.namespace.QName;

public interface TypeAnnotator<N>
{
    /** Associate a node Id with a type name.
     * 
     * @param nodeId The node; may not be null.
     * @param typeName The type name.
     */
    void annotate(N document, Object nodeID, QName typeName);
}
