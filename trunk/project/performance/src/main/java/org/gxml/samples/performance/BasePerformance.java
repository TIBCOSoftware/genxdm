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
package org.gxml.samples.performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

abstract public class BasePerformance {

	public static final String TEST_CNT_PROP_NAME = "testCnt";
	public static final String DOC_FILE_PROP_NAME = "document";
	public static final String SCHEMA_FILE_PROP_NAME = "schema";
	public static final String BASE_URI_PROP_NAME = "baseURI";
	public static final String TEST_PARSING_PROP_NAME = "testParsing";
	public static final String TEST_SERIALIZATION_PROP_NAME = "testSerialization";
	public static final String TEST_NAVIGATION_PROP_NAME = "testNavigation";
	public static final String TEST_VALIDATION_PROP_NAME = "testValidation";
	public static final String TEST_MEMORY_PROP_NAME = "testMemoryUsage";
	protected long m_testCnt;
	protected String m_docFile;
	protected String m_schemaFile;
	protected String m_baseURI;
	protected boolean m_testParsing = true;
	protected boolean m_testSerializing = true;
	protected boolean m_testNavigation = true;
	protected boolean m_testValidation = false;
	protected boolean m_testMemoryUsage = false;

	public BasePerformance(String propFile) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		m_testCnt = Long.parseLong(props.getProperty(TEST_CNT_PROP_NAME, "1"));
		if(m_testCnt <= 0)
		{
			m_testCnt = 1;
		}
		m_docFile = props.getProperty(DOC_FILE_PROP_NAME);
		if(m_docFile == null)
		{
			throw new IllegalStateException("Input document must be specified.");
		}
		m_baseURI = props.getProperty(BASE_URI_PROP_NAME);
		if(m_baseURI == null)
		{
			throw new IllegalStateException("Base URI must be specified.");
		}
		m_testParsing = Boolean.parseBoolean(props.getProperty(TEST_PARSING_PROP_NAME, "true"));
		m_testSerializing = Boolean.parseBoolean(props.getProperty(TEST_SERIALIZATION_PROP_NAME, "true"));
		m_testNavigation = Boolean.parseBoolean(props.getProperty(TEST_NAVIGATION_PROP_NAME, "true"));
		m_testValidation = Boolean.parseBoolean(props.getProperty(TEST_VALIDATION_PROP_NAME, "false"));
		m_testMemoryUsage = Boolean.parseBoolean(props.getProperty(TEST_MEMORY_PROP_NAME, "false"));
		m_schemaFile = props.getProperty(SCHEMA_FILE_PROP_NAME);
		if(m_testValidation && m_schemaFile == null)
		{
			throw new IllegalStateException("Validation testing requires schema file location.");
		}
	}
	abstract void setupTesting();
	abstract void setupParsing();
	abstract void setupSerialization();
	abstract void setupNavigation();
	abstract void setupValidation();
	abstract void parse(String docFilePath);
	abstract void serialize();
	abstract void navigate(TaskTimer ttTimer);
	abstract void validate(TaskTimer ttTimer);
	
	/**
	 * Performs the tests.
	 * 
	 * @throws Exception because any uncaught exception should stop the test
	 */
	public final void runPerfTest() throws Exception {
		// Check that source document exists.
	    String docFilePath = m_baseURI + "/" + m_docFile;
	    final File iFile = new File(docFilePath);
	    if(!iFile.exists())
	    {
	    	throw new IllegalStateException("File \"" + docFilePath + "\" does not exist.");
	    }
		setupTesting();
		
	    // Initialize top level timer.
	    TaskTimer ttTotal = new TaskTimer("BridgePerf: number of test runs = " + m_testCnt);
	    ttTotal.addNote("timestamp = " + new Timestamp(System.currentTimeMillis()).toString());
	    ttTotal.addNote("input = " + docFilePath);
	    ttTotal.addNote("concrete test class = " + getClass().getName());
	
	    // Parsing...
	    setupParsing();
	    TaskTimer ttPerfParse = new TaskTimer("Parsing", m_testMemoryUsage);
	    long parseCnt = 1; // Must parse document once, even if we're not testing parse performance
	    if(m_testParsing)
	    {
	        ttTotal.addTask(ttPerfParse);
	        parseCnt = m_testCnt;
	    }
	    for(int icnt = 0; icnt < parseCnt; icnt++)
	    {
	    	TaskTimer ttParse = ttPerfParse.newChild("parse[" + icnt + "]");
	        ttParse.startTimer();
	        parse(docFilePath);
	        ttParse.stopTimer();
	    }
	    // Serialization...
	    if(m_testSerializing)
	    {
		    setupSerialization();
	        TaskTimer ttPerfSerialize = ttTotal.newChild("Serialization");
	        for(int icnt = 0; icnt < m_testCnt; icnt++)
	        {
	        	TaskTimer ttSerialize = ttPerfSerialize.newChild("serialize[" + icnt + "]");
	        	ttSerialize.startTimer();
	        	serialize();
	    		ttSerialize.stopTimer();
	        }
	    }
	    // Navigation...
	    if(m_testNavigation)
	    {
		    setupNavigation();
	        TaskTimer ttPerfNavigate = ttTotal.newChild("Navigation");
	        for(int icnt = 0; icnt < m_testCnt; icnt++)
	        {
	        	TaskTimer ttNavigate = ttPerfNavigate.newChild("navigate[" + icnt + "]");
	            navigate(ttNavigate);
	        }
	    }
	    // Validation...
	    if(m_testValidation)
	    {
	    	setupValidation();
	        TaskTimer ttPerfValidate = ttTotal.newChild("Validation");
	        
	        for(int icnt = 0; icnt < m_testCnt; icnt++)
	        {
	        	TaskTimer ttValidate = ttPerfValidate.newChild("validate[" + icnt + "]");
	            ttValidate.startTimer();
	            validate(ttValidate);
	            ttValidate.stopTimer();
	        }
	    }
		System.out.println("----------------------------------------------------");
		System.out.println(ttTotal.toPrettyStringMillis(""));
		System.out.println("----------------------------------------------------");
	}

}