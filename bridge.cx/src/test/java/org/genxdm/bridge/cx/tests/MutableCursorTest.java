package org.genxdm.bridge.cx.tests;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgetest.mutable.MutableCursorBase;

public class MutableCursorTest
    extends MutableCursorBase<XmlNode>
{
    @Override
    public XmlNodeContext newProcessingContext()
    {
        return new XmlNodeContext();
    }
}
