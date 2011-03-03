package org.genxdm.samples.performance.bridges;

import org.genxdm.ProcessingContext;
import org.genxdm.samples.performance.PerfTest;

public interface BridgePerfTest <N, A> extends PerfTest {
	void setContext(ProcessingContext<N> context);
	Iterable<String> getRequiredFeatures();
}
