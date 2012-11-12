package org.genxdm.bridge.axiom.tests.typed;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.SequenceBuilderBase;

public class SequenceBuilderTest
    extends SequenceBuilderBase<Object, XmlAtom>
{

    @Override
    public AxiomProcessingContext newProcessingContext()
    {
        return new AxiomProcessingContext(new OMLinkedListImplFactory());
    }

}
