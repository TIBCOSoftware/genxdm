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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.DefaultCatalog;
import org.genxdm.processor.w3c.xs.DefaultCatalogResolver;
import org.genxdm.processor.w3c.xs.DefaultSchemaCatalog;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;
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
        
        // Note: this is a very simple schema, the first one presented in
        // full in the schema primer.  It has no target namespace, so everything
        // is in the default/global namespace.  It has no attribute definitions,
        // no attribute groups, no model groups, no id constraints, no notations.
        // it doesn't do imports.  very simple.  here, we just count the things
        // that should be empty, and assert that they are empty.
        // for the things that are more complex, we do a little more.
        InputStream stream = getClass().getClassLoader().getResourceAsStream("po.xsd");
        ComponentBag components = parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
        
        Iterable<AttributeDefinition> atts = components.getAttributes();
        Iterable<AttributeGroupDefinition> attGroups = components.getAttributeGroups();
        Iterable<ElementDefinition> elems = components.getElements();
        
        Iterable<ModelGroup> models = components.getModelGroups();
        Iterable<SimpleType> simps = components.getSimpleTypes();
        Iterable<ComplexType> comps = components.getComplexTypes();

        Iterable<IdentityConstraint> ids = components.getIdentityConstraints();
        Iterable<NotationDefinition> nots = components.getNotations();

        assertEquals(0, count(atts));
        assertEquals(0, count(attGroups));
        for (ElementDefinition e : elems)
        {
            String name = e.getLocalName();
            assertNotNull(name);
            assertTrue(name.equals("purchaseOrder") || name.equals("comment"));
            if (name.equals("purchaseOrder"))
                assertEquals("PurchaseOrderType", e.getType().getLocalName());
            if (name.equals("comment"))
                assertTrue(e.getType().isAtomicType());
        }
        
        // only one global simple type is defined,
        // but there's a second one.  that one has a generated name.
        // fun.
        for (SimpleType type : simps)
        {
System.out.println(type.getLocalName());
        }
        
        for (ComplexType type: comps)
        {
System.out.println(type.getLocalName());
        }
        
        assertEquals(0, count(models));
        assertEquals(0, count(ids));
        assertEquals(0, count(nots));
        // okay.  now, in resources we have po.xsd, ipo.xsd + address.xsd, and report.xsd.
        // these are taken from the schema primer, so we can treat them as a basic
        // test suite.  might need to revisit the primer to adjust the various
        // schemas (the primer suggests changes to the bases, but doesn't present
        // the whole changed schema which means they have to be edited by hand).
        
        // and ... what do we do?
        assertTrue(true);
    }
    
    @Test
    public void parsePrimerIPOSchema()
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = new ResourceResolver();
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("ipo.xsd");
        ComponentBag components = parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
        
        Iterable<AttributeDefinition> atts = components.getAttributes();
        Iterable<AttributeGroupDefinition> attGroups = components.getAttributeGroups();
        Iterable<ElementDefinition> elems = components.getElements();
        
        Iterable<ModelGroup> models = components.getModelGroups();
        Iterable<SimpleType> simps = components.getSimpleTypes();
        Iterable<ComplexType> comps = components.getComplexTypes();

        Iterable<IdentityConstraint> ids = components.getIdentityConstraints();
        Iterable<NotationDefinition> nots = components.getNotations();
    }
    
    @Test
    public void parsePrimerReportSchema()
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = new ResourceResolver();
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache();
        
        // initialize the catalog for the schemas we want to read.
        
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("report.xsd");
        ComponentBag components = parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
        
        Iterable<AttributeDefinition> atts = components.getAttributes();
        Iterable<AttributeGroupDefinition> attGroups = components.getAttributeGroups();
        Iterable<ElementDefinition> elems = components.getElements();
        
        Iterable<ModelGroup> models = components.getModelGroups();
        Iterable<SimpleType> simps = components.getSimpleTypes();
        Iterable<ComplexType> comps = components.getComplexTypes();

        Iterable<IdentityConstraint> ids = components.getIdentityConstraints();
        Iterable<NotationDefinition> nots = components.getNotations();
    }
    
    private <E> int count(Iterable<E> it)
    {
        int i = 0;
        for (E e : it) i++;
        return i;
    }
    
    // hack.
    private class ResourceResolver implements CatalogResolver
    {

        @Override
        public InputStream resolveInputStream(URI catalogURI)
            throws IOException
        {
            if (catalogURI.toString().equals(IPO_URI))
                return getClass().getClassLoader().getResourceAsStream("ipo.xsd");
            if (catalogURI.toString().equals(ADDRESS_URI))
                return getClass().getClassLoader().getResourceAsStream("address.xsd");
            return DefaultCatalogResolver.SINGLETON.resolveInputStream(catalogURI);
        }
    }
    
    private static String IPO_URI = "http://www.example.com/IPO";
    private static String ADDRESS_URI = "http://www.example.com/schemas/address.xsd";
}
