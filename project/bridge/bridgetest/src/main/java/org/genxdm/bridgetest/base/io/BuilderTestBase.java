package org.genxdm.bridgetest.base.io;

import org.genxdm.base.io.FragmentBuilder;

import org.genxdm.bridgetest.TestBase;

import org.junit.Test;

// Note: this *also* handles the testing of ContentHandler and NodeSource,
// since FragmentBuilder is the canonical implementation of both.
public abstract class BuilderTestBase<N>
    extends TestBase<N>
{
    @Test
    public void constructionWF()
    {
        // verify we can construct things well-formed
    }
    
    @Test
    public void identities()
    {
        // verify that we can get different documents from the same
        // event stream, even if we use the same builder
    }
    
    @Test
    public void illFormed()
    {
        // test various ways of supplying bad data
    }
}
