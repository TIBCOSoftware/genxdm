package org.genxdm.samples.performance.bridges.dom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.samples.performance.VersionedProcessingContextFactory;
import org.w3c.dom.Node;

public class DomProcessingContextFactory implements VersionedProcessingContextFactory<Node> {
	
    public final DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }
	private Properties m_props;

	@Override
	public String getBridgeVersion() {
		return getProps().getProperty("version", "0.5");
	}
	@Override
	public String getBridgeName() {
		return getProps().getProperty("artifactId", "bridge-dom");
	}
	private Properties getProps()
	{
		if(m_props == null)
		{
		    InputStream inputStream = 
		    	getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.genxdm/bridge-dom/pom.properties");  		
		    m_props = new Properties();
		    if(inputStream != null)
		    {
			    try {
					m_props.load(inputStream);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		    }
		}
		return m_props;
	}
}
