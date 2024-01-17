package org.genxdm.bridgetest.typed.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genxdm.Model;
import org.genxdm.bridgekit.content.Attr;
import org.genxdm.bridgekit.content.TypedContentHelper;
import org.genxdm.bridgetest.typed.TypedTestBase;
import org.genxdm.creation.Attrib;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DocumentHandler;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.exceptions.AbortException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// TODO: this doesn't actually need to implement TypedTestBase
// at present. Maybe we should fix that first? Because this class
// wants schema (a ComponentProvider), an atom bridge, and a
// SequenceHandler (the SequenceBuilder) for its output. A set
// of unit tests for this would be quite useful. For that, we
// need to drop the enum idiocy in TypedTestBase and add getSchema()
// and getValidatorFactory() as abstract methods, with some useful
// implementation built into these tests.

public abstract class TypedContentHelperBase<N, A>
    extends TypedTestBase<N, A>
{
    @Ignore
    @Test
    public void createSimpleContentUsingHelper()
    {
        N untypedInput = null;
        try
        {
            untypedInput = parseInstance(context.newDocumentHandler(), BASE_DIR, SIMPLE_INSTANCE);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        assertNotNull("Failed to parse "+BASE_DIR+SIMPLE_INSTANCE, untypedInput);
        try
        {
            StringWriter writer = new StringWriter();
            DocumentHandler<N> docHandler = context.newDocumentHandler();
            docHandler.write(writer, untypedInput);
            System.out.println("Untyped Output document of createSimpleContentUsingHelper:" + "\n");
            System.out.println(writer.toString());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        //TypedContext<N, A> tcx = pc.getXMLTypedContext();
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        TypedContentHelper<A> tch = new TypedContentHelper<A>(builder,tc.getSchema().getComponentProvider(), tc.getAtomBridge());
        HandlerToHelper<N, A> hth = new HandlerToHelper<N, A>(tch);
        Model<N> model = context.getModel();
        model.stream(untypedInput, hth);
        N validTree = builder.getNode();
        assertNotNull("Validation appears to have failed: null tree", validTree);
    }
    
    @Test
    public void nilledSimpleTypeUsingAPI()
    {
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        TypedContentHelper<A> tch = new TypedContentHelper<A>(builder,tc.getSchema().getComponentProvider(), tc.getAtomBridge());
        tch.start();
        String tns = "http://www.example.com/typedContentWithNillAttr";
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("", tns);
        bindings.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        Attrib att1=new Attr("", "a1", "6");
        Attrib att2=new Attr("", "a2", "a2val");
        List<Attrib> attList=new ArrayList<Attrib>();
        attList.add(att1);
        attList.add(att2);
        tch.startComplex(tns, "table", bindings, attList); // start Output
        attList.clear();
        att1=new Attr("", "r1", "6");
        att2=new Attr("", "r2", "r2val");
        attList.add(att1);
        attList.add(att2);
        tch.startComplex(tns, "row", bindings, attList); // start complex "Row"
        attList.clear();
        tch.simpleElement(tns, "id", "xyzzy");
        tch.simpleElement(tns, "name", "word");
        tch.simpleElement(tns, "dateValue", "1980-04-15");
        tch.simpleElement(tns, "timeValue", "12:34:56.78");
        tch.simpleElement(tns, "countValue", "17");
        Attrib att=new Attr("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
        attList.add(att);
        tch.simplexElement(tns, "description", null, attList,null );
        tch.endComplex(); // end Row
        tch.endComplex(); // end output
        tch.end();
        N outputTree = builder.getNode();
        try
        {
            StringWriter writer = new StringWriter();
            DocumentHandler<N> docHandler = context.newDocumentHandler();
            docHandler.write(writer, outputTree);
            System.out.println("Output document of nilledSimpleTypeUsingAPI :");
            System.out.println(writer.toString() + "\n");
            assertNotNull("The output XML of nilledSimpleTypeUsingAPI is empty", writer);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    @Test
    public void nilledComplexTypeUsingAPI()
    {
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        TypedContentHelper<A> tch = new TypedContentHelper<A>(builder,tc.getSchema().getComponentProvider(), tc.getAtomBridge());
        tch.start();
        String tns = "http://www.example.com/typedContentWithNillAttr";
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("", tns);
        bindings.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        Attrib att1=new Attr("", "a1", "6");
        Attrib att2=new Attr("", "a2", "a2val");
        List<Attrib> attList=new ArrayList<Attrib>();
        attList.add(att1);
        attList.add(att2);
        tch.startComplex(tns, "table", bindings, attList); // start Output
        attList.clear();
        att1=new Attr("", "r1", "6");
        att2=new Attr("", "r2", "r2val");
        attList.add(att1);
        attList.add(att2);
        tch.startComplex(tns, "row", bindings, attList); // start complex "Row"
        attList.clear();
        tch.simpleElement(tns, "id", "xyzzy");
        tch.simpleElement(tns, "name", "word");
        tch.simpleElement(tns, "dateValue", "1980-04-15");
        tch.simpleElement(tns, "timeValue", "12:34:56.78");
        tch.simpleElement(tns, "countValue", "17");
        Attrib att=new Attr("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
        attList.add(att);
        tch.simplexElement(tns, "description", null, attList,null );
        tch.endComplex(); // end Row
        attList.clear();
        att1=new Attr("", "errattr1", "someeerrval");
        att2=new Attr("", "errattr2", "4");
        attList.add(att1);
        attList.add(att2);
        attList.add(att);
        tch.startComplex(tns, "errors", bindings, attList); // start complex "errors"
        tch.endComplex(); // end errors
        tch.endComplex(); // end output
        tch.end();
        N outputTree = builder.getNode();
        try
        {
            StringWriter writer = new StringWriter();
            DocumentHandler<N> docHandler = context.newDocumentHandler();
            docHandler.write(writer, outputTree);
            System.out.println("Output document of nilledComplexTypeUsingAPI using helper:");
            System.out.println(writer.toString() + "\n");
            assertNotNull("The output XML of nilledComplexTypeUsingAPI is empty", writer);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    @Test
    public void missingMandatoryAttributeErrChk()
    {
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        TypedContentHelper<A> tch = new TypedContentHelper<A>(builder,tc.getSchema().getComponentProvider(), tc.getAtomBridge());
        tch.start();
        String tns = "http://www.example.com/typedContentWithNillAttr";
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("", tns);
        bindings.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        Attrib att1=new Attr("", "a1", "6");
        Attrib att2=new Attr("", "a2", "a2val");
        List<Attrib> attList=new ArrayList<Attrib>();
        attList.add(att1);
        attList.add(att2);
        tch.startComplex(tns, "table", bindings, attList); // start Output
        attList.clear();
        att1=new Attr("", "r1", "6");
        att2=new Attr("", "r2", "r2val");
        attList.add(att1);
        attList.add(att2);
        tch.startComplex(tns, "row", bindings, attList); // start complex "Row"
        attList.clear();
        tch.simpleElement(tns, "id", "xyzzy");
        tch.simpleElement(tns, "name", "word");
        tch.simpleElement(tns, "dateValue", "1980-04-15");
        tch.simpleElement(tns, "timeValue", "12:34:56.78");
        tch.simpleElement(tns, "countValue", "17");
        Attrib att=new Attr("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
        attList.add(att);
        tch.simplexElement(tns, "description", null, attList,null );
        tch.endComplex(); // end Row
        tch.startComplex(tns, "errors", bindings, attList); // start complex "errors"
        attList.clear();
        att1=new Attr("", "errattr1", "someeerrval");
        //att2=new Attr("", "errattr2", "4"); commented out on purpose to trigger errror
        attList.add(att1);
        attList.add(att2);
        attList.add(att);
        try
        {
            tch.endComplex(); // end errors
        }
        catch(GenXDMException genxdmException)
        {
            System.out.println("The GenXDMException as expected in missingMandatoryAttributeErrChk: "+genxdmException.getMessage()+"\n");
            assertNotNull("Expected a GenXDMException in missingMandatoryAttributeErrChk ", genxdmException.getMessage());
            return;
        }
        tch.endComplex(); // end output
        tch.end();
        N outputTree = builder.getNode();
        try
        {
            StringWriter writer = new StringWriter();
            DocumentHandler<N> docHandler = context.newDocumentHandler();
            docHandler.write(writer, outputTree);
            System.out.println("Output document of nilledComplexTypeUsingAPI using helper:" + "\n");
            System.out.println(writer.toString());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    @Before
    public void initCache()
    {
        parser = getSchemaParser();
        ComponentBag bag = null;
        try
        {
            bag = parseSchema(BASE_DIR, TABLE_SCHEMA);
        } 
        catch (AbortException ae)
        {
            ae.printStackTrace();
        } 
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        if (bag != null)
            schema.register(bag);
        else
            fail("Could not locate schema resource: "+BASE_DIR+TABLE_SCHEMA);
    }
    
    private static final String BASE_DIR = "tchcontent/";
    private static final String TABLE_SCHEMA = "tableContentWithNillableAttr.xsd";
    private static final String SIMPLE_INSTANCE = "simplevalid_withattr.xml";
    //private static final String BLOB_INSTANCE = "blobvalid-1row-1opt.xml";
    //private static final String INVALID_INSTANCE = "invalid-1row.xml";
}
