package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.nodes.InformerBase;
import org.w3c.dom.Node;

public class InformerTest
    extends InformerBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        disableIdrefsTests = true;
        return new DomProcessingContext();
    }

}
