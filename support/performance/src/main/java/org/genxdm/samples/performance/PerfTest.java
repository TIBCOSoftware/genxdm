package org.genxdm.samples.performance;

import java.util.Properties;

public interface PerfTest {
	String getName();
	void execute();
	void initialSetup(Properties props);
	void iterativeSetup();
	Iterable<String> iterativeTeardown();
	void finalTeardown();
}
