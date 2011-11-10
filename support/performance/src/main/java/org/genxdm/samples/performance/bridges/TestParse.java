package org.genxdm.samples.performance.bridges;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TestParse<N,A> extends BaseBridgePerfTest<N,A>
{
	String m_baseURI;
	String m_docFilePath;
	
	@Override
	public String getName() {
		return "Parse";
	}
	@Override
	public void initialSetup(Properties props)	{
		super.initialSetup(props);
		String docFile = props.getProperty(DOC_FILE_PROP_NAME);
		if(docFile == null)
		{
			throw new IllegalStateException("Input document must be specified.");
		}
	    m_docFilePath = getBaseURI() + "/" + docFile;
	}
	
	@Override
	public void iterativeSetup() {
	}
	@Override
	public void execute() {
		try {
			getDocHandler().parse(new FileReader(m_docFilePath), null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
}
