package org.genxdm.bridge.cx.tests.typed;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.TypedContentHelperBase;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.processor.w3c.xs.validation.ValidatorFactory;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.genxdm.xs.SchemaParser;

public class TypedContentHelperTest<N, A> extends TypedContentHelperBase<XmlNode, XmlAtom>
{

    @Override
    public XmlNodeContext newProcessingContext()
    {
        return new XmlNodeContext();
    }

    @Override
    public ValidationHandler<XmlAtom> getValidationHandler()
    {
        return null;
    }

    @Override
    public SchemaParser getSchemaParser()
    {
        return new W3cXmlSchemaParser();
    }

    private ValidatorFactory<XmlNode,XmlAtom> getValidatorFactory()
    {
        ValidatorFactory<XmlNode, XmlAtom> factory = new ValidatorFactory<XmlNode, XmlAtom>(tc);
        return factory;
    }
    
    public TypedDocumentHandler<XmlNode, XmlAtom> getTypedDocumentHandler()
    {
        TypedDocumentHandler<XmlNode, XmlAtom> typedDocHandler  = tc.newDocumentHandler(getValidatorFactory(), null, null);
        return typedDocHandler;
    }

}
