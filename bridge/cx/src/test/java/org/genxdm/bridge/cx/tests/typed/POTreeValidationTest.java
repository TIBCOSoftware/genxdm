package org.genxdm.bridge.cx.tests.typed;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.processor.w3c.xs.validation.ValidatorFactory;
import org.genxdm.processor.w3c.xs.validationtest.TreeValidationBase;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;

public class POTreeValidationTest
    extends TreeValidationBase<XmlNode, XmlAtom>
{

    @Override
    public XmlNodeContext newProcessingContext()
    {
        return new XmlNodeContext();
    }


    @Override
    public SAXValidator<XmlAtom> getSAXValidator()
    {
        ValidatorFactory<XmlNode, XmlAtom> factory = new ValidatorFactory<XmlNode, XmlAtom>(newProcessingContext().getTypedContext());
        return factory.newSAXContentValidator();
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        ValidatorFactory<XmlNode, XmlAtom> factory = new ValidatorFactory<XmlNode, XmlAtom>(newProcessingContext().getTypedContext());
        return factory.newXdmContentValidator();
    }

}
