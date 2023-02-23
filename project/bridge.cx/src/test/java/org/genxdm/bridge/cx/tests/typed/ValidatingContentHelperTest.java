package org.genxdm.bridge.cx.tests.typed;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.ValidatingContentHelperBase;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.ValidatorFactory;
import org.genxdm.xs.SchemaParser;

public class ValidatingContentHelperTest
    extends ValidatingContentHelperBase<XmlNode, XmlAtom>
{

    @Override
    public XmlNodeContext newProcessingContext()
    {
        return new XmlNodeContext();
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        @SuppressWarnings("unchecked")
        ValidatorFactory<XmlAtom> factory = new org.genxdm.processor.w3c.xs.validation.ValidatorFactory<XmlNode, XmlAtom>(context.getTypedContext(getCache()));
        return factory.newXdmContentValidator();
    }

    @Override
    public SchemaParser getSchemaParser()
    {
        return new W3cXmlSchemaParser();
    }

}
