package org.genxdm.bridgekit.atoms;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.typed.TypedModel;
import org.genxdm.typed.types.AtomBridge;

public final class XsiNil
{
    public static <N, A> boolean isNilledElement(TypedModel<N, A> model, N element, AtomBridge<A> bridge)
    {
        if (model.isElement(element))
        {
            N attr = model.getAttribute(element, xsiNil.getNamespaceURI(), xsiNil.getLocalPart());
            if (attr != null)
            {
                Iterable<? extends A> value = model.getValue(attr);
                try
                {
                    if (bridge.unwrapAtom(value) == bridge.getBooleanTrue())
                        return true;
                }
                catch (Exception e) // change: catch more than just ACE; *don't* throw
                {
                    // fall through to return false.
                    // xsi:nil should be typed and should have a boolean value.
                    // if it doesn't, then fuck it.
                }
            }
        }
        return false;
    }
    
    private static final QName xsiNil = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
}
