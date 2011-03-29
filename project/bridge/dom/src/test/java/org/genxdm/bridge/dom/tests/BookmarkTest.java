package org.genxdm.bridge.dom.tests;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgetest.nodes.BookmarkBase;
import org.w3c.dom.Node;

public class BookmarkTest
    extends BookmarkBase<Node>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
