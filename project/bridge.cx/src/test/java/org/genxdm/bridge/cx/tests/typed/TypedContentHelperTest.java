package org.genxdm.bridge.cx.tests.typed;

import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgetest.typed.io.TypedContentHelperBase;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.typed.ValidationHandler;
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

}
