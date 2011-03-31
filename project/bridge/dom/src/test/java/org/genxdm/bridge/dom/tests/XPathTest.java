package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.processor.xpath.v10.tests.XPathBase;
import org.w3c.dom.Node;

public class XPathTest
    extends XPathBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
