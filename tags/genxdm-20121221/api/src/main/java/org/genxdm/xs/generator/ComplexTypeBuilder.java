package org.genxdm.xs.generator;

// used for complex types with complex content, or complex types by composition
public interface ComplexTypeBuilder
    extends AttributeGroupBuilder, GroupBuilder
{
    // for global complex type. don't permit the abstract attribute, or block, or final.
    // the mixed and name attributes are assigned by the creator call.
    // simpleContent | complexContent | (, (group | all | choice | sequence)?, attributes* )
    
    //void startComplexContentParticle(QName base);
    // content model for complexContent extension: (, (group | all | choice | sequence)?, attributes* )
    // which is the same as the trailing model for complexType overall.
    // call endParticle() to complete
}
