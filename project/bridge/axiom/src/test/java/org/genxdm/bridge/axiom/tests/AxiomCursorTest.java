package org.genxdm.bridge.axiom.tests;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgetest.CursorBase;

public class AxiomCursorTest
    extends CursorBase<Object>
{

    @Override
    public AxiomProcessingContext newProcessingContext()
    {
        return new AxiomProcessingContext(new OMLinkedListImplFactory());
    }

}
