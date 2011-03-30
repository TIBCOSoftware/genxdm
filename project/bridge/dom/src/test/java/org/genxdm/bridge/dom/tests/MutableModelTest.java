package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.mutable.MutableModelBase;
import org.w3c.dom.Node;

public class MutableModelTest
    extends MutableModelBase<Node>
{
    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }
}
