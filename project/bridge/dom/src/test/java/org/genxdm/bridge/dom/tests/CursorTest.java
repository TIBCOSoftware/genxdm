package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.CursorBase;
import org.w3c.dom.Node;

public class CursorTest
    extends CursorBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
