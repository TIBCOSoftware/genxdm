package org.genxdm.samples.performance.bridges.cx;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.ProcessingContextFactory;

public class CxProcessingContextFactory implements ProcessingContextFactory<XmlNode> {
	public ProcessingContext<XmlNode> newProcessingContext() {
		return new XmlNodeContext();
	}
}
