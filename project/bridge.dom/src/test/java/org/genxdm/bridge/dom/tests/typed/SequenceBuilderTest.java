package org.genxdm.bridge.dom.tests.typed;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.SequenceBuilderBase;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.ValidatorFactory;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.xs.SchemaParser;
import org.w3c.dom.Node;

public class SequenceBuilderTest
    extends SequenceBuilderBase<Node, XmlAtom>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        @SuppressWarnings("unchecked")
        ValidatorFactory<XmlAtom> factory = new org.genxdm.processor.w3c.xs.validation.ValidatorFactory<Node, XmlAtom>(context.getTypedContext(getCache()));
        return factory.newXdmContentValidator();
    }
    
    @Override
    public SchemaParser getSchemaParser()
    {
        return new W3cXmlSchemaParser();
    }
    
}
