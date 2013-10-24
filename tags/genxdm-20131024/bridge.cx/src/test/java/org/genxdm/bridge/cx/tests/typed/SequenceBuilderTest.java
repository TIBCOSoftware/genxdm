package org.genxdm.bridge.cx.tests.typed;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.SequenceBuilderBase;

public class SequenceBuilderTest
    extends SequenceBuilderBase<XmlNode, XmlAtom>
{

    @Override
    public XmlNodeContext newProcessingContext()
    {
        return new XmlNodeContext();
    }

}
