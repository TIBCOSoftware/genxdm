/**
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridgetest;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.FragmentBuilder;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

// this tests very little; see the tests for node informer, node navigator, and node axis navigator.
// all three of those should be passing when this one is turned on.
public abstract class ModelTestBase<N>
    extends TestBase<N>
{
    @Test
    public void streaming()
    {
        ProcessingContext<N> context = newProcessingContext();
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        assertNotNull(builder);
    }
    
    private boolean matchNode(N node, NodeKind kind, String ns, String name, String pf, String value)
    {
        return false;
    }
//    void stream(N node, boolean copyNamespaces, ContentHandler handler) throws GxmlException;
}
