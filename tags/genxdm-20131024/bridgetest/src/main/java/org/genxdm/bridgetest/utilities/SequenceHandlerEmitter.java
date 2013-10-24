package org.genxdm.bridgetest.utilities;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

/** A debugging tool for SequenceHandler.
 *
 * This thing just spurts out information to stdout. Pass it to something that
 * expects to see a SequenceHandler in order to get the calls splattered all over
 * the console. Useful for old-school folks who get annoyed bouncing on buttons to
 * "step over" and "step into" things, when one just wants to see the whole flow at once.
 */
public final class SequenceHandlerEmitter<A>
    implements SequenceHandler<A>
{
    public SequenceHandlerEmitter(AtomBridge<A> bridge)
    {
        this.bridge = bridge;
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix,
            String value, DtdAttributeKind type)
        throws GenXDMException
    {
        System.out.println("attribute("+namespaceURI+", "+localName+", "+prefix+", "+value+", "+type +")");
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        System.out.println("comment("+value+")");
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        System.out.println("endDocument()");
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        System.out.println("endElement()");
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        System.out.println("namespace("+prefix+", "+namespaceURI+")");
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        System.out.println("processingInstruction("+target+", "+data+")");
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        System.out.println("startDocument("+documentURI+", "+docTypeDecl+")");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        System.out.println("startElement("+namespaceURI+", "+localName+", "+prefix+")");
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        System.out.println("text("+data+")");
    }

    @Override
    public void close()
        throws IOException
    {
        System.out.println("close()");
    }

    @Override
    public void flush()
        throws IOException
    {
        System.out.println("flush()");
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, List<? extends A> data, QName type)
        throws GenXDMException
    {
        System.out.println("Tattribute("+namespaceURI+", "+localName+", "+prefix+", "+bridge.getC14NString(data)+", "+type.toString()+")");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix, QName type)
        throws GenXDMException
    {
        System.out.println("TstartElement("+namespaceURI+", "+localName+", "+prefix+", "+type.toString()+")");
    }

    @Override
    public void text(List<? extends A> data)
        throws GenXDMException
    {
        System.out.println("Ttext("+bridge.getC14NString(data)+")");
    }

    private final AtomBridge<A> bridge;
}
