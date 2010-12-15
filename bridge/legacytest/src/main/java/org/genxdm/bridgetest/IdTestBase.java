/**
 * Copyright (c) 2010 TIBCO Software Inc.
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

import java.io.IOException;
import java.io.InputStream;

import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.DocumentHandler;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.exceptions.GxmlMarshalException;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * Base test class for verifying that "ID" based methods are implemented
 * properly by the bridge. 
 *
 * @param <N>
 */
public abstract class IdTestBase<N> extends TestCase
    implements ProcessingContextFactory<N>
{
    protected DocumentHandler<N> m_handler;
    protected Model<N> m_model;
    
    public void setUp() {
        ProcessingContext<N> pcx = newProcessingContext(); 
        m_handler = pcx.newDocumentHandler();
        m_model = pcx.getModel();
    }
    
    /**
     * Verifies that {@link Model#getElementById(Object, String)} works correctly.
     */
    public void testIdLookup() throws GxmlMarshalException, IOException {

        InputStream stream = IdTestBase.class.getResourceAsStream("docWithId.xml");
        InputSource source = new InputSource(stream);
        N document = m_handler.parse(source, null);
        assertNotNull("Failed to parse document", document);
        
        N elem = m_model.getElementById(document, "E3");
        assertNotNull("Failed to find object with getElementById()", elem);
        assertEquals("e3", m_model.getLocalName(elem) );
    }
}
