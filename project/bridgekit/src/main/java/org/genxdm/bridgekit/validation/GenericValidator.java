package org.genxdm.bridgekit.validation;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.genxdm.Cursor;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SequenceBuilder;

public class GenericValidator<N, A>
{
    public GenericValidator(TypedContext<N, A> context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
    }
    
    public N validate(N source, ValidationHandler<A> handler, QName type)
    {
        PreCondition.assertNotNull(source, "source node");
        if (type != null)
            PreCondition.assertTrue(context.getModel().isElement(source));
        SequenceBuilder<N, A> builder = context.newSequenceBuilder();
        handler.setSchema(context.getSchema());
        handler.setInitialElementType(type); // usually null, but setting null is a no-op
        handler.setSequenceHandler(builder);
        context.getModel().stream(source, handler);
        try 
        {
            handler.flush();
        }
        catch (IOException ioe)
        {
            // oh, get real
            throw new RuntimeException(ioe);
        }

        return builder.getNode();
    }

    public N validate(Cursor source, ValidationHandler<A> handler, QName type)
    {
        PreCondition.assertNotNull(source, "source cursor");
        // if we didn't do this check, we could change the source argument to contentwriter
        if (type != null)
            PreCondition.assertTrue(source.isElement());
        SequenceBuilder<N, A> builder = context.newSequenceBuilder();
        handler.setSchema(context.getSchema());
        handler.setInitialElementType(type); // usually null, but setting null is a no-op
        handler.setSequenceHandler(builder);
        source.write(handler);
        try 
        {
            handler.flush();
        }
        catch (IOException ioe)
        {
            // oh, get real
            throw new RuntimeException(ioe);
        }

        return builder.getNode();
    }

    private final TypedContext<N, A> context;
}
