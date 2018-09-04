package org.genxdm.processor.w3c.xs.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.genxdm.bridgekit.xs.DefaultCatalog;
import org.genxdm.bridgekit.xs.DefaultSchemaCatalog;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.Catalog;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.genxdm.xs.resolve.CatalogResolver;
import org.genxdm.xs.resolve.SchemaCatalog;
import org.junit.Test;

public class RedefinesTest
{

    @Test
    public void simpleTypeRestrictionRedefine()
        throws AbortException
    {
        String schema = "SimpleType_Restriction_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine SimpleType/Restriction schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void complexTypeRestrictionRedefine()
        throws AbortException
    {
        String schema = "ComplexType_Restriction_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine ComplexType/Restriction schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void complexTypeExtensionRedefine()
        throws AbortException
    {
        String schema = "ComplexType_Extension_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine ComplexType/Extension schema components (splat!)");
        Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void subsetModelGroupRedefine()
        throws AbortException
    {
        String schema = "SubSet_ModelGroup_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine ModelGroup/SubSet schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void supersetModelGroupRedefine()
        throws AbortException
    {
        String schema = "SuperSet_ModelGroup_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine ModelGroup/SuperSet schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void supersetModelGroupReferenceRedefine()
        throws AbortException
    {
        String schema = "SuperSet_ModelGroup_withRef_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine ModelGroup/SuperSet (with reference) schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void subsetAttGroupRedefine()
        throws AbortException
    {
        String schema = "SubSet_AttGroup_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine AttributeGroup/SubSet schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void supersetAttGroupRedefine()
        throws AbortException
    {
        String schema = "SuperSet_AttGroup_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine AttributeGroup/SuperSet schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    @Test
    public void supersetAttGroupReferenceRedefine()
        throws AbortException
    {
        String schema = "SuperSet_AttGroup_Reference_Redef.xsd";
        try
        {
            ComponentBag components = parseSchema(schema);
            System.out.println("Redefine Attribute Group/SuperSet (with reference) schema components (splat!)");
            Utilities.displayComponents(components);
        }
        catch (AbortException ae)
        {
            ae.printStackTrace();
        }
    }
    
    private ComponentBag parseSchema(String schema)
        throws AbortException
    {
        W3cXmlSchemaParser parser = new W3cXmlSchemaParser();
        
        Catalog cat = new DefaultCatalog();
        SchemaCatalog scat = new DefaultSchemaCatalog(cat);
        CatalogResolver resolver = new RedefinesResolver();//DefaultCatalogResolver.SINGLETON;
        ComponentProvider bootstrap = new SchemaCacheFactory().newSchemaCache().getComponentProvider();
        
        // initialize the catalog for the schemas we want to read.
        parser.setCatalogResolver(resolver, scat);
        parser.setComponentProvider(bootstrap);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream(REDEF_DIR+schema);
        return parser.parse(null, stream, null, SchemaExceptionThrower.SINGLETON);
    }
    
    private class RedefinesResolver implements CatalogResolver
    {
        @Override
        public InputStream resolveInputStream(final URI uri) 
            throws IOException
        {
            // known pattern in all schemas under test: each uses a relative
            // path (just the filename, in fact) to refer to the base schema.
            // resolution is thus easy, using the resource loader and base path
            PreCondition.assertArgumentNotNull(uri, "uri");
            InputStream stream = getClass().getClassLoader().getResourceAsStream(REDEF_DIR+uri);
//System.out.println(((stream == null) ? "Could not resolve: " : "Resolved: " )+REDEF_DIR+uri);
            return stream;
        }
    }
    
    private static final String REDEF_DIR = "redefines/";
}
