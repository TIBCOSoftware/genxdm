package org.genxdm.typed;

import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.xs.Schema;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;

public interface Validator<N, A>
{
    SchemaExceptionHandler getSchemaExceptionHandler();
    
    SequenceBuilder<N, A> getSequenceBuilder();
    
    void reset();
    
    void setSchema(Schema<A> cache);
    
    void setSchemaExceptionHandler(SchemaExceptionHandler errors);
    
    void setSequenceBuilder(SequenceBuilder<N, A> builder);

}
