package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.processor.io.tests.RoundTripBase;
import org.w3c.dom.Node;

public class RoundTripTest
    extends RoundTripBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
