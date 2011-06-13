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
        
        // be sure to set the resolver, catalog, and component provider.
        
        //parser.parse(URI (schema location), 
        //             InputStream, URI (sysid), 
        //             SchemaExceptionHandler)
        // stream must not be null.
        
        // okay.  now, in resources we have po.xsd, ipo.xsd + address.xsd, and report.xsd.
        // these are taken from the schema primer, so we can treat them as a basic
        // test suite.  might need to revisit the primer to adjust the various
        // schemas (the primer suggests changes to the bases, but doesn't present
        // the whole changed schema which means they have to be edited by hand).
        
        // and ... what do we do?
        assertTrue(true);
    }
}
