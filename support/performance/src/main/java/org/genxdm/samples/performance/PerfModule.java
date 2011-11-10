package org.genxdm.samples.performance;

import java.util.Properties;

public interface PerfModule {
	String getName();
	void setup(Properties propsFile);
	Iterable<String> teardown();
	Iterable<PerfTest> getTests();
}
