package org.genxdm.bridgetest.typed.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.genxdm.Model;
import org.genxdm.bridgekit.content.ValidatingContentHelper;
import org.genxdm.bridgetest.typed.TypedTestBase;
import org.genxdm.io.DocumentHandler;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;
import org.junit.Before;
import org.junit.Test;

public abstract class ValidatingContentHelperBase<N, A>
    extends TypedTestBase<N, A>
{
    @Test
    public void createBLOBContent() // another successful test, this one with binary content (not really large, though?
    {
        N untypedInput = null;
        try
        {
            untypedInput = parseInstance(context.newDocumentHandler(), BASE_DIR, BLOB_INSTANCE);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        assertNotNull("Failed to parse "+BASE_DIR+BLOB_INSTANCE, untypedInput);
        ValidationHandler<A> validator = getValidationHandler();
        assertNotNull("Must supply validator", validator);
        validator.setSchema(schema);
        // maybe change this to the catcher to see what actually happens in use? most validation uses catcher
        validator.setSchemaExceptionHandler(SchemaExceptionThrower.SINGLETON);
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        ValidatingContentHelper<N, A> vch = new ValidatingContentHelper<N, A>(validator, tc, builder); 
        HandlerToHelper<N, A> hth = new HandlerToHelper<N, A>(vch);
        Model<N> model = context.getModel();
        model.stream(untypedInput, hth);
        N validTree = builder.getNode();
        assertNotNull("Validation appears to have failed: null tree", validTree);
    }
    
    @Test
    public void createSimpleContent()
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
        ValidationHandler<A> validator = getValidationHandler();
        assertNotNull("Must supply validator", validator);
        validator.setSchema(schema);
        // maybe change this to the catcher to see what actually happens in use? most validation uses catcher
        validator.setSchemaExceptionHandler(SchemaExceptionThrower.SINGLETON);
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        ValidatingContentHelper<N, A> vch = new ValidatingContentHelper<N, A>(validator, tc, builder); 
        HandlerToHelper<N, A> hth = new HandlerToHelper<N, A>(vch);
        Model<N> model = context.getModel();
        model.stream(untypedInput, hth);
        N validTree = builder.getNode();
        assertNotNull("Validation appears to have failed: null tree", validTree);
    }
    
    @Test
    public void createInvalidSimpleContent() // has one or more cardinality issues
    {
        N untypedInput = null;
        try
        {
            untypedInput = parseInstance(context.newDocumentHandler(), BASE_DIR, INVALID_INSTANCE);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        assertNotNull("Failed to parse "+BASE_DIR+INVALID_INSTANCE, untypedInput);
        ValidationHandler<A> validator = getValidationHandler();
        assertNotNull("Must supply validator", validator);
        validator.setSchema(schema);
        // maybe change this to the catcher to see what actually happens in use? most validation uses catcher
        SchemaExceptionCatcher catcher = new SchemaExceptionCatcher();
        validator.setSchemaExceptionHandler(catcher);
        SequenceBuilder<N, A> builder = tc.newSequenceBuilder();
        ValidatingContentHelper<N, A> vch = new ValidatingContentHelper<N, A>(validator, tc, builder); 
        HandlerToHelper<N, A> hth = new HandlerToHelper<N, A>(vch);
        Model<N> model = context.getModel();
        model.stream(untypedInput, hth);
        N validTree = builder.getNode();
        assertNotNull("Validation appears to have failed: null tree", validTree); //never triggers.
        // this displays the document returned from the validator, which is, unexpectedly,
        // complete except for the case in which we provided text content for an element-only-content element,
        // where it shows up as empty instead
        try
        {
            StringWriter writer = new StringWriter();
            DocumentHandler<N> docHandler = context.newDocumentHandler();
            docHandler.write(writer, validTree);
            System.out.println("Output document:");
            System.out.println(writer.toString());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        if (!catcher.isEmpty())
        {
            int count = 1;
            for (final SchemaException exception : catcher)
            {
                System.out.println("Exception "+count+" of "+catcher.size());
                exception.printStackTrace();
                count++;
            }
            // in the target application, we would throw an exception here, after
            // showing (a subset of?) stack traces. here, let's congratulate ourselves
            // instead. we can vary the failures; right now, there are four: one
            // duplicate element (name in the first row), one out-of-sequence (missing
            // all the required content, instead starting with the optional description
            // in the second row), and two exceptions for the third row, which has simple
            // content instead of element-only, which produces non-whitespace text
            // and then premature end of content. the only thing elided, though, is the
            // non-whitespace content in row three. prolly a lot of it isn't validated,
            // but i can't tell in this pattern of examination (there are other patterns
            // that could introspect the output tree).
            
            // note that the robustness with respect to document creation is slightly
            // unexpected, but is a natural consequence of having an already-created
            // (if untyped) input document, which actually defeats the purpose of
            // programmatic generation, arguably. However, we need our programmatic
            // generation to have predictable results, and that means something like
            // the handler to helper tool after untyped parsing. actual users of this
            // will be directly writing to the helper interface, but i don't want to
            // write that; it would mean writing something to consume non-xml input
            // and generate xml from it, and the possibilities for what the input
            // look like are large, and unfamiliar to me.
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

    private static final String BASE_DIR = "createcontent/";
    private static final String TABLE_SCHEMA = "tableContent.xsd";
    private static final String SIMPLE_INSTANCE = "simplevalid-1row-0opt.xml";
    private static final String BLOB_INSTANCE = "blobvalid-1row-1opt.xml";
    private static final String INVALID_INSTANCE = "invalid-1row.xml";
}
