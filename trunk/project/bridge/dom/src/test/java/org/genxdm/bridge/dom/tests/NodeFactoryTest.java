package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.mutable.NodeFactoryBase;
import org.w3c.dom.Node;

public class NodeFactoryTest
    extends NodeFactoryBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
