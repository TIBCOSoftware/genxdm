package org.genxdm.bridge.dom.tests.typed;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.SequenceBuilderBase;
import org.w3c.dom.Node;

public class SequenceBuilderTest
    extends SequenceBuilderBase<Node, XmlAtom>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

}
