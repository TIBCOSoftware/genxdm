package org.genxdm.bridge.axiom.tests.typed;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.SequenceBuilderBase;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.ValidatorFactory;
import org.genxdm.xs.SchemaParser;

public class SequenceBuilderTest
    extends SequenceBuilderBase<Object, XmlAtom>
{
    @Override
    public AxiomProcessingContext newProcessingContext()
    {
        return new AxiomProcessingContext(new OMLinkedListImplFactory());
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        @SuppressWarnings("unchecked")
        ValidatorFactory<XmlAtom> factory = new org.genxdm.processor.w3c.xs.validation.ValidatorFactory<Object, XmlAtom>(context.getTypedContext(getCache()));
        return factory.newXdmContentValidator();
    }

    @Override
    public SchemaParser getSchemaParser()
    {
        return new W3cXmlSchemaParser();
    }


}
