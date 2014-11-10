package org.genxdm.bridge.axiom.tests.typed;

import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.processor.w3c.xs.validation.ValidatorFactory;
import org.genxdm.processor.w3c.xs.validationtest.TypedAnonymousValidationBase;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;

public class TypedAnonymousValidationTest
    extends TypedAnonymousValidationBase<Object, XmlAtom>
{

    @Override
    public AxiomProcessingContext newProcessingContext()
    {
        return new AxiomProcessingContext(new OMLinkedListImplFactory());
    }

    @Override
    public SAXValidator<XmlAtom> getSAXValidator()
    {
        ValidatorFactory<Object, XmlAtom> factory = new ValidatorFactory<Object, XmlAtom>(newProcessingContext().getTypedContext(null));
        return factory.newSAXContentValidator();
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        ValidatorFactory<Object, XmlAtom> factory = new ValidatorFactory<Object, XmlAtom>(newProcessingContext().getTypedContext(null));
        return factory.newXdmContentValidator();
    }

}
