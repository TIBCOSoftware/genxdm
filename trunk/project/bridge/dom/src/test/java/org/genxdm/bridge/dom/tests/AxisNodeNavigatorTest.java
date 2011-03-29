package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.axes.AxisNodeNavigatorBase;
import org.w3c.dom.Node;

public class AxisNodeNavigatorTest
    extends AxisNodeNavigatorBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
