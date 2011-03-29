package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.axes.NodeNavigatorBase;
import org.w3c.dom.Node;

public class NodeNavigatorTest
    extends NodeNavigatorBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
