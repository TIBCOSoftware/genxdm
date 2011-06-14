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

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.DefaultCatalog;
import org.genxdm.processor.w3c.xs.DefaultCatalogResolver;
import org.genxdm.processor.w3c.xs.DefaultSchemaCatalog;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.junit.Test;

public class SimpleSchemaParseTest
{

    @Test
    public void parsePrimerPOSchema()
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = DefaultCatalogResolver.SINGLETON;
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("po.xsd");
        ComponentBag components = parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
        
        // okay.  now, in resources we have po.xsd, ipo.xsd + address.xsd, and report.xsd.
        // these are taken from the schema primer, so we can treat them as a basic
        // test suite.  might need to revisit the primer to adjust the various
        // schemas (the primer suggests changes to the bases, but doesn't present
        // the whole changed schema which means they have to be edited by hand).
        
        // and ... what do we do?
        assertTrue(true);
    }
    
    public void parsePrimerIPOSchema()
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = DefaultCatalogResolver.SINGLETON;
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("ipo.xsd");
        ComponentBag components = parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
    }
    
    public void parsePrimerReportSchema()
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = DefaultCatalogResolver.SINGLETON;
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("report.xsd");
        ComponentBag components = parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
    }
}
