package org.genxdm.samples.performance.bridges;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.io.Resolver;

abstract public class BaseBridgePerfTest<N, A> implements BridgePerfTest<N, A> {

	public static final String DOC_FILE_PROP_NAME = "bridge.document";
	private N m_testNode;
	private String m_baseURI;
	private Resolver m_resolver;
	private ProcessingContext<N> m_pcx;
	private FragmentBuilder<N> m_docBuilder;
    private DocumentHandler<N> m_docHandler;
	private Model<N> m_model;

	public BaseBridgePerfTest() {
	}
	
	@Override
	public Iterable<String> getRequiredFeatures() {
		return null;
	}

	@Override
	public void setContext(ProcessingContext<N> pcx) {
		// Ensure that context supports the features we need.
		m_pcx = pcx;
		m_docBuilder = m_pcx.newFragmentBuilder();
		m_model = m_pcx.getModel();
		m_docHandler = m_pcx.newDocumentHandler();
	}
    @Override
	public void initialSetup(Properties props)	{
		String docFile = props.getProperty(DOC_FILE_PROP_NAME);
		if(docFile == null)
		{
			throw new IllegalStateException("Input document must be specified.");
		}
		m_baseURI = props.getProperty(BridgePerfModule.BASE_URI_PROP_NAME);
		if(m_baseURI == null)
		{
			throw new IllegalStateException("Base URI must be specified.");
		}
		try {
			m_testNode = getDocHandler().parse(new FileReader(m_baseURI + "/" + docFile), null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void finalTeardown()	{};
	

	public N getTestNode() 
	{
		return m_testNode;
	}
	public ProcessingContext<N> getPcx() 
	{
		return m_pcx;
	}
	public FragmentBuilder<N> getDocBuilder() 
	{
		return m_docBuilder;
	}
	public DocumentHandler<N> getDocHandler() 
	{
		return m_docHandler;
	}
	public Model<N> getModel() 
	{
		return m_model;
	}
	public String getBaseURI()
	{
		return m_baseURI;
	}
	protected Resolver getResolver()
	{
		if(m_resolver == null)
		{
			try {
				m_resolver = new SampleResolver(new URI(getBaseURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		return m_resolver;
	}
}