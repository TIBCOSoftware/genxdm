package org.genxdm.bridge.dom.tests.typed;

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.processor.w3c.xs.validation.ValidatorFactory;
import org.genxdm.processor.w3c.xs.validationtest.SAXValidationBase;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;
import org.w3c.dom.Node;

public class POSaxValidationTest
    extends SAXValidationBase<Node, XmlAtom>
{

    @Override
    public DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }

    @Override
    public SAXValidator<XmlAtom> getSAXValidator()
    {
        ValidatorFactory<Node, XmlAtom> factory = new ValidatorFactory<Node, XmlAtom>(newProcessingContext().getTypedContext());
        return factory.newSAXContentValidator();
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        ValidatorFactory<Node, XmlAtom> factory = new ValidatorFactory<Node, XmlAtom>(newProcessingContext().getTypedContext());
        return factory.newXdmContentValidator();
    }

}
