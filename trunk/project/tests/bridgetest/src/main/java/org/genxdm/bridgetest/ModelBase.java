/*
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

import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.filters.NamespaceFixupFilter;
import org.genxdm.bridgetest.utilities.Events;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.FragmentBuilder;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public abstract class ModelBase<N>
    extends TestBase<N>
{
    @Test
    public void streaming()
        throws GenXDMException
    {
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder(new NamespaceFixupFilter());
        assertNotNull(builder);
        // the events recorder/matcher does the hard work here, in case you were wondering.
        Events<N> matcher = new Events<N>(builder);
        if (!context.isSupported(Feature.DOCUMENT_URI))
            matcher.ignoreDocumentURI();
        if (!context.isSupported(Feature.NAMESPACE_AXIS))
            matcher.ignoreExtraNamespaceDeclarations();
        matcher.record();
        N doc = createComplexTestDocument(matcher);
        assertNotNull(doc);

        Model<N> model = context.getModel();
        assertNotNull(model);

        matcher.match();
        model.stream(doc, true, matcher);
        
        // TODO: more comparisons?
    }
    
    @Test
    public void comparisons()
    {
        // TODO
    }
    
}
