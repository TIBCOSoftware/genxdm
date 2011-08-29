package org.genxdm.bridgekit;

import java.util.HashMap;
import java.util.Map;

import org.genxdm.ProcessingContext;

/** A simple-minded utility for using multiple processing contexts in a single
 * application.
 */
public class ProcessingContextRegistry
{
    
    @SuppressWarnings("unchecked")
    public <N> ProcessingContext<N> getContext(String discriminator)
    {
        return (ProcessingContext<N>)contexts.get(discriminator);
    }
   
    public <N> void addContext(String discriminator, ProcessingContext<N> context)
    {
        contexts.put(discriminator, context);
    }
    
    private Map<String, ProcessingContext<?>> contexts = new HashMap<String, ProcessingContext<?>>();
}
