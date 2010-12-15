package org.genxdm.bridgetest.base;

import org.genxdm.base.ProcessingContext;
import org.genxdm.bridgetest.TestBase;

/** Abstract base tests for the contract of ProcessingContext.
 * 
 * @author aaletal
 *
 * @param <N> the node handle
 */
public abstract class ContextTestBase<N> 
    extends TestBase<N> 
{
    
	public void testBookmark()
	{
	    ProcessingContext<N> context = newProcessingContext();
	    
	    N testDoc = createTestDocument1(context.newFragmentBuilder()); 
	}
}
