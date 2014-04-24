package org.genxdm.samples.performance.bridges.cx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.samples.performance.VersionedProcessingContextFactory;

public class CxProcessingContextFactory implements VersionedProcessingContextFactory<XmlNode> {
	public ProcessingContext<XmlNode> newProcessingContext() {
		return new XmlNodeContext();
	}
	private Properties m_props;

	@Override
	public String getBridgeVersion() {
		return getProps().getProperty("version");
	}
	@Override
	public String getBridgeName() {
		return getProps().getProperty("artifactId");
	}
	private Properties getProps()
	{
		if(m_props == null)
		{
		    InputStream inputStream = 
		    	getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.genxdm/bridge-cx/pom.properties");  		
		    m_props = new Properties();
		    try {
				m_props.load(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return m_props;
	}
}
