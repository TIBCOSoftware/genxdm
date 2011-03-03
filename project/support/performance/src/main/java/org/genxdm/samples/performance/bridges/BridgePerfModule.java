/**
 * Copyright (c) 2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.samples.performance.bridges;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.io.Resolver;
import org.genxdm.samples.performance.PerfModule;
import org.genxdm.samples.performance.PerfTest;

/**
 * Performance test module implementation for gxml bridges.  
 * 
 * @author jbaysdon
 */
public class BridgePerfModule<N,A> implements PerfModule
{
	public static final String BASE_URI_PROP_NAME = "bridge.baseURI";
	public static final String TEST_PARSING_PROP_NAME = "bridge.testParsing";
	public static final String TEST_SERIALIZATION_PROP_NAME = "bridge.testSerialization";
	public static final String TEST_NAVIGATION_PROP_NAME = "bridge.testNavigation";
	public static final String TEST_VALIDATION_PROP_NAME = "bridge.testValidation";
	public static final String TEST_TRANSFORMATION_PROP_NAME = "bridge.testTransformation";
	public static final String TEST_MODEL_MUTATION_PROP_NAME = "bridge.testModelMutation";
	public static final String TEST_BUILDER_MUTATION_PROP_NAME = "bridge.testBuilderMutation";
	public static final String BRIDGE_FACTORY_CLASS = "bridge.bridgeFactoryClass";

	protected boolean m_testParsing = false;
	protected boolean m_testSerializing = false;
	protected boolean m_testNavigation = false;
	protected boolean m_testValidation = false;
	protected boolean m_testTransformation = false;
	protected boolean m_testModelMutation = false;
	protected boolean m_testBuilderMutation = false;
	
	final protected ArrayList<PerfTest> m_tests = new ArrayList<PerfTest>(); 
	
	private String m_baseURI;
	private ProcessingContextFactory<N> m_pcxFactory;
	private ProcessingContext<N> m_pcx;

    //-------------------------------------------------------------------------
	// Constructor.
	//-------------------------------------------------------------------------
	public BridgePerfModule()	{}
	
	//-------------------------------------------------------------------------
	// processing context factory & related implementation
	//-------------------------------------------------------------------------
	public ProcessingContext<N> newProcessingContext() {
		return m_pcxFactory.newProcessingContext();
	}
	public ProcessingContext<N> getProcessingContext() {
		if(m_pcx == null)
		{
			m_pcx = m_pcxFactory.newProcessingContext();
		}
		return m_pcx;
	}
	public Resolver getResolver()
	{
		try {
			return new SampleResolver(new URI(m_baseURI));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	//-------------------------------------------------------------------------
	// PerfModule implementation
	//-------------------------------------------------------------------------
	@Override
	public String getName() {
		return "Bridge Performance(" + m_pcxFactory.getClass().getName() + ")";
	}

	@SuppressWarnings("unchecked") // for cast of pcxFactory
	@Override
	public void setup(Properties props) {
		m_baseURI = props.getProperty(BASE_URI_PROP_NAME);
		if(m_baseURI == null)
		{
			throw new IllegalStateException("Base URI must be specified.");
		}
		m_testParsing = Boolean.parseBoolean(props.getProperty(TEST_PARSING_PROP_NAME, "true"));
		m_testSerializing = Boolean.parseBoolean(props.getProperty(TEST_SERIALIZATION_PROP_NAME, "true"));
		m_testNavigation = Boolean.parseBoolean(props.getProperty(TEST_NAVIGATION_PROP_NAME, "true"));
		m_testValidation = Boolean.parseBoolean(props.getProperty(TEST_VALIDATION_PROP_NAME, "false"));
		m_testTransformation = Boolean.parseBoolean(props.getProperty(TEST_TRANSFORMATION_PROP_NAME, "false"));
		m_testModelMutation = Boolean.parseBoolean(props.getProperty(TEST_MODEL_MUTATION_PROP_NAME, "false"));
		m_testBuilderMutation = Boolean.parseBoolean(props.getProperty(TEST_BUILDER_MUTATION_PROP_NAME, "false"));

		// Bridge injection.
		String pcxFactoryClassName = props.getProperty(BRIDGE_FACTORY_CLASS);
		if(pcxFactoryClassName != null)
		{
			try {
				Class<?> pcxFactoryClass = this.getClass().getClassLoader().loadClass(pcxFactoryClassName);
				m_pcxFactory = (ProcessingContextFactory<N>) pcxFactoryClass.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		else
		{
			throw new IllegalStateException("No mutable processing context factory specified.");
		}
		
		// Test instantiations.
		ArrayList<BridgePerfTest<N,A>> testCandidates = new ArrayList<BridgePerfTest<N,A>>();
		testCandidates.add(new TestParse<N,A>());
		testCandidates.add(new TestSerialize<N,A>());
		testCandidates.add(new TestNavigate<N,A>());
		testCandidates.add(new TestValidate<N,A>());
		/* TODO: processor not ready yet.	
		testCandidates.add(new TestTransform<N,A>());
		*/
		testCandidates.add(new TestMutateWithModel<N,A>());
		testCandidates.add(new TestMutateWithBuilder<N,A>());
		
		for(BridgePerfTest<N,A> candidate : testCandidates)
		{
			Iterable<String> reqFeatures = candidate.getRequiredFeatures();
			boolean load = true;
			if(reqFeatures != null)
			{
				for(String feature : reqFeatures)
				{
					if(!getProcessingContext().isSupported(feature))
					{
						System.out.println("Processing context does not support the " + feature + " feature, which is required by the " + candidate.getName() + " test.");
						load = false;
					}
				}
			}
			if(load)
			{
				candidate.setContext(getProcessingContext());
				candidate.initialSetup(props);
				m_tests.add(candidate);
			}
		}
	}
	@Override
	public Iterable<String> teardown() { return null; }

	@Override
	public Iterable<PerfTest> getTests() {
		return m_tests;
	}
}
