package org.genxdm.xs.generator;

import javax.xml.namespace.QName;

public interface ComplexTypeBuilder
    extends AttributeGroupBuilder, GroupBuilder
{
    // for global complex type. don't permit the abstract attribute, or block, or final.
    // the mixed and name attributes are assigned by the creator call.
    // simpleContent | complexContent | (, (group | all | choice | sequence)?, attributes* )
    
    void startComplexContent(QName base);
    // content model for complexContent extension: (, (group | all | choice | sequence)?, attributes* )
    // which is the same as the trailing model for complexType overall.
    void endComplexContent();
    
    void startSimpleContent(QName base);
    // for derivation by extension, only attributes.
    void endSimpleContent();
}
