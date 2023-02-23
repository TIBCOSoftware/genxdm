package org.genxdm.bridgetest.typed.io;

import org.genxdm.Model;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.io.ContentHandler;

// this is so that anything that looks like N + Model<N> can be wrapped
// up as ContentGenerator without implementing the rest of Cursor. Mostly
// unnecessary, but avoids having to get a cursor from the context
public final class SimplestContentGenerator<N>
    implements ContentGenerator
{
    public SimplestContentGenerator(final N node, final Model<N> model)
    {
        current = PreCondition.assertNotNull(node, "node");
        this.model = PreCondition.assertNotNull(model, "model");
    }
    
    public N getCurrent()
    {
        return current;
    }
    
    @Override
    public boolean isElement()
    {
        return model.isElement(current);
    }
    
    @Override
    public void write(final ContentHandler target) throws GenXDMException
    {
        model.stream(current, target);
    }
    
    private N current;
    private Model<N> model;
}