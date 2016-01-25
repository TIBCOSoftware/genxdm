package org.genxdm.bridge.cx.tests;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgetest.io.ContentHelperBase;

public class ContentHelperTest
    extends ContentHelperBase<XmlNode>
{

    @Override
    public XmlNodeContext newProcessingContext()
    {
        return new XmlNodeContext();
    }
}
