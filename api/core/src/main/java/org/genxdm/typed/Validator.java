package org.genxdm.typed;

import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.xs.Schema;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;

public interface Validator<A>
{
    SchemaExceptionHandler getSchemaExceptionHandler();
    
    SequenceHandler<A> getSequenceHandler();
    
    void reset();
    
    void setSchema(Schema cache);
    
    void setSchemaExceptionHandler(SchemaExceptionHandler errors);
    
    void setSequenceHandler(SequenceHandler<A> handler);

}
