package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.mutable.MutableContextBase;
import org.w3c.dom.Node;

public class MutableContextTest
    extends MutableContextBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
