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
package org.genxdm.processor.w3c.xs.tests;

import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SimpleSchemaParse
{

    @Test
    public void parseSimpleSchema()
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        //parser.parse(URI (location), 
        //             InputStream, URI (sysid), 
        //             SchemaExceptionHandler, 
        //             SchemaLoadOptions, 
        //             ComponentProvider)
        // stream and component provider must not be null.
        // and ... what do we do?
        assertTrue(true);
    }
}
