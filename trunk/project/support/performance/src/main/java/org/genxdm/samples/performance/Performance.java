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
package org.genxdm.samples.performance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public class Performance {
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("No performance properties file specified.");
			System.exit(0);
		}
		// Let each test variation execute setup, so that we don't have a failure
		// on the last run & waste time.
		ArrayList<Performance> pList = new ArrayList<Performance>();
		for(String propFile : args)
		{
			pList.add(new Performance(propFile));
		}
		for(Performance p : pList)
		{
			try {
				p.runPerfTest();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static final Boolean DEBUG = false;
	public static final String TEST_CNT_PROP_NAME = "base.testCnt";
	public static final String TEST_MEMORY_PROP_NAME = "base.testMemoryUsage";
	public static final String EXCLUDE_FIRST_RUN_NAME = "base.excludeOneRun";
	public static final String INCLUDE_SUBTASK_LEVELS = "base.includeSubtaskLevels";
	protected long m_testCnt;
	protected boolean m_testMemoryUsage = false;
	protected boolean m_excludeFirstRun = false;
	protected int m_includeSubtaskLevels = 0;
	private final ArrayList<PerfModule> m_modules = new ArrayList<PerfModule>();
	private final Properties m_props;
	private final String m_propFilename;

	public Performance(String propFile) {
		m_propFilename = propFile;
		m_props = new Properties();
		try {
			m_props.load(new FileInputStream(propFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		m_testCnt = Long.parseLong(m_props.getProperty(TEST_CNT_PROP_NAME, "1"));
		if(m_testCnt <= 0)
		{
			m_testCnt = 1;
		}
		m_testMemoryUsage = Boolean.parseBoolean(m_props.getProperty(TEST_MEMORY_PROP_NAME, "false"));
		m_excludeFirstRun = Boolean.parseBoolean(m_props.getProperty(EXCLUDE_FIRST_RUN_NAME, "false"));
		String value = m_props.getProperty(INCLUDE_SUBTASK_LEVELS);
		if(value != null && value.length() > 0)
		{
			m_includeSubtaskLevels = Integer.parseInt(value);
		}
		String moduleList = m_props.getProperty("modules");
		if(moduleList != null)
		{
			StringTokenizer st = new StringTokenizer(moduleList, ",");
			while(st.hasMoreTokens())
			{
				String moduleClass = st.nextToken();
				try {
					Class<?> clazz = Performance.class.getClassLoader().loadClass(moduleClass);
					PerfModule module = (PerfModule)clazz.newInstance();
					m_modules.add(module);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	/**
	 * Performs the tests.
	 * 
	 * @throws Exception because any uncaught exception should stop the test
	 */
	public final void runPerfTest() throws Exception {
		
	    TaskTimer ttTotal = new TaskTimer("BridgePerf: number of test runs = " + m_testCnt);
	    ttTotal.addNote("property file = " + m_propFilename);
	    ttTotal.addNote("timestamp = " + new Timestamp(System.currentTimeMillis()).toString());
	    
		for(PerfModule module : m_modules)
		{
		    ttTotal.addNote("module: " + module.getClass().getName());
			module.setup(m_props);
		    TaskTimer ttModule = ttTotal.newChild(module.getName());
			for(PerfTest test : module.getTests())
			{
			    TaskTimer ttTest = ttModule.newChild(test.getName());
			    if(m_excludeFirstRun)
			    {
			    	test.iterativeSetup();
			    	test.execute();
			    	test.iterativeTeardown();
			    }
			    for(int icnt = 0; icnt < m_testCnt; icnt++)
			    {
			    	TaskTimer ttRun = ttTest.newChild("run[" + icnt + "]");
			    	test.iterativeSetup();
			        ttRun.startTimer();
			        test.execute();
			        ttRun.stopTimer();
			    	Iterable<String> notes = test.iterativeTeardown();
			    	if(notes != null)
			    	{
			    		for(String note : notes)
			    		{
			    			ttRun.addNote(note);
			    		}
			    	}
			    }
			}
		}
		System.out.println("----------------------------------------------------");
		System.out.println(ttTotal.toPrettyStringMillis("", m_includeSubtaskLevels));
		System.out.println("----------------------------------------------------");
	}
}