package org.genxdm.samples.performance.bridges.axiom;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;

public class AxiomProcessingContextFactory implements ProcessingContextFactory<Object> {
    public final AxiomProcessingContext newProcessingContext()
    {
        return new AxiomProcessingContext(new OMLinkedListImplFactory());
    }
}
