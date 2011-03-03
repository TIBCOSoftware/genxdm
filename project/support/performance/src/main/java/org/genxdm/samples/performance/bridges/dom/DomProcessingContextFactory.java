package org.genxdm.samples.performance.bridges.dom;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.w3c.dom.Node;

public class DomProcessingContextFactory implements ProcessingContextFactory<Node> {
    public final DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }
}
