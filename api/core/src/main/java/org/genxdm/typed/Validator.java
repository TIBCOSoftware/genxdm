package org.genxdm.typed;

import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.xs.Schema;
import org.genxdm.xs.exceptions.SchemaExceptionCatcher;

public interface Validator<N, A>
    extends SequenceHandler<A>
{
    SchemaExceptionCatcher getSchemaExceptionCatcher();
    
    SequenceBuilder<N, A> getSequenceBuilder();
    
    void reset();
    
    void setSchema(Schema<A> cache);
    
    void setSchemaExceptionCatcher(SchemaExceptionCatcher errors);
    
    void setSequenceBuilder(SequenceBuilder<N, A> builder);

}
