package org.genxdm.samples.performance.bridges;

public interface MutationPerfTestConstants {
	static final int MUTATATION_LEVELS = 3;
	static final String MUTATE_NS = "http://com.tibco.com/test";
	static final String MUTATE_PREFIX = "ns";
	static final String MUTATE_ROOT_NAME = "root";
	static final String[] MUTATE_CHILD_NAMES = {
		"child0","child1", "child2", "child3", "child4", "child5", 
		"child6", "child7", "child8", "child9"
	};
	static final String[][] MUTATE_ATTS = {
		{"att0","0.0"}, {"att1","1.0"}, {"att2","2.0"}, {"att3","string3"},
			{"att4","string4"}, {"att5","2010-12-10T14:43:55+04:00"}
	};
	static final String[] MUTATE_TEXT_VALUES = {
		"Hello"," ", "world!", "\n" 
	};
}
