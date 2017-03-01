package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.creation.ContentEvent;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

// this is a package-access class that's used by the two binarycontent implementation
// classes (TypedContentHelper and BinaryContentHelperToEventQueue). What it does
// is to accumulate events so that it can invoke startComplex(), text(), and endComplex(),
// with attributes and namespaces as specified. the helper then either accumulates typed
// events or it fires events into the sequence handler after annotating with type names
// and compiling the types.
// it turns out to be pretty simple.
final class HandlerHelper<A>
    implements ContentHandler
{
    HandlerHelper(BinaryContentHelper helper)
    {
        this.helper = PreCondition.assertNotNull(helper, "helper");
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        //if (currentOpenTag != null)
        attributes.add(helper.newAttribute(namespaceURI, localName, value));
        //else
        //    throw new GenXDMException("No start tag for attribute");
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        openTag(); // flush if an element is open
        helper.comment(value);
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        // do nothing. we do not support document nodes in this context
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        openTag(); // flush if an empty element is open
        helper.endComplex(); // and end it
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        // if (currentOpenTag != null) // or else it's an error, damn it.
        namespaces.put(prefix, namespaceURI);
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        openTag(); // flush if necessary
        helper.pi(target, data);
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        // do nothing. we don't support document nodes in this context.
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        openTag(); // flush if parent still open
        currentOpenTag = new ContentEventImpl(namespaceURI, localName, prefix);
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        String ns = null, name = null;
        if (currentOpenTag != null) {
            ns = currentOpenTag.getNamespace();
            name = currentOpenTag.getName();
        }
        openTag();
        impl().text(ns, name, data);
    }

    @Override
    public void close()
        throws IOException
    {
        // do nothing; helper doesn't use it
    }

    @Override
    public void flush()
        throws IOException
    {
        // do nothing; helper doesn't use it
    }
    
    private void openTag()
    {
        if (currentOpenTag != null)
        {
            // don't care about state of namespaces and attributes.
            helper.startComplex(currentOpenTag.getNamespace(), currentOpenTag.getName(), 
                                namespaces.isEmpty() ? null : namespaces, 
                                attributes.isEmpty() ? null : attributes);
            currentOpenTag = null;
            attributes.clear();
            namespaces.clear();
        }
    }
    
    private AbstractContentHelper impl()
    {
        if (helper instanceof AbstractContentHelper)
            return (AbstractContentHelper)helper;
        return null;
    }
    
    private List<Attrib> attributes = new ArrayList<Attrib>();
    private Map<String, String> namespaces = new HashMap<String, String>();
    private ContentEvent currentOpenTag;

    private final BinaryContentHelper helper;
}
