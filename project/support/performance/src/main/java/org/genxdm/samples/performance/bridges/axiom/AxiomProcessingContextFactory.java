package org.genxdm.samples.performance.bridges.axiom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.samples.performance.VersionedProcessingContextFactory;

public class AxiomProcessingContextFactory implements VersionedProcessingContextFactory<Object> {
    public final AxiomProcessingContext newProcessingContext()
    {
        return new AxiomProcessingContext(new OMLinkedListImplFactory());
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
		    	getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.genxdm/bridge-axiom/pom.properties");  		
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
