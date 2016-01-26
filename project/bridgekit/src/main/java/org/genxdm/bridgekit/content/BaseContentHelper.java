package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

// simplified api for generating untyped xml trees.
public class BaseContentHelper
    implements ContentHelper
{
    public BaseContentHelper(ContentHandler handler)
    {
        this.handler = PreCondition.assertNotNull(handler, "content handler");
    }

    @Override
    public void start()
    {
        handler.startDocument(null, null);
    }

    @Override
    public void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        start();
        if (name != null)
            startComplex(ns, name, bindings, attributes);
        //else what? just drop it?
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        if ( (name == null) || name.trim().isEmpty() )
            throw new IllegalArgumentException("Element with null or empty name");
        if (ns == null)
            ns = NIT;
        if (nsStack.getPrefix(ns, bindings) == null)
        {
            // add a binding for the namespace, and initialize bindings.
            if (bindings == null)
                bindings = new HashMap<String, String>();
            if (ns.isEmpty())
                bindings.put(NIT, NIT);
            else
                bindings.put(nsStack.newPrefix(), ns);
        }
        if (attributes != null)
            for (Attrib att : attributes)
            {
                if (!att.getNamespace().isEmpty())
                    bindings = nsStack.checkAttributePrefix(att, bindings);
            }
        // by the time we get here, the local namespace context should be consistent.
        // in most cases, we shouldn't have had to do anything to achieve that.
        handler.startElement(ns, name, nsStack.getPrefix(ns, bindings));
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                handler.namespace(binding.getKey(), binding.getValue());
            }
        if (attributes != null)
            for (Attrib attribute : attributes)
            {
                if (attribute.getNamespace().isEmpty())
                    handler.attribute(NIT, attribute.getName(), NIT, attribute.getValue(), DtdAttributeKind.CDATA);
                else
                    handler.attribute(attribute.getNamespace(), attribute.getName(), nsStack.getAttributePrefix(attribute.getNamespace(), bindings), attribute.getValue(), DtdAttributeKind.CDATA);
            }
        nsStack.push(bindings);
    }

    @Override
    public void simpleElement(String ns, String name, String value)
    {
        simplexElement(ns, name, null, null, value);
    }

    @Override
    public void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value)
    {
        startComplex(ns, name, bindings, attributes);
        handler.text(value);
        endComplex();
    }

    @Override
    public Attrib newAttribute(String name, String value)
    {
        return new Attrib(name, value);
    }

    @Override
    public Attrib newAttribute(String ns, String name, String value)
    {
        return new Attrib(ns, name, value);
    }

    @Override
    public void comment(String text)
    {
        handler.comment(text);
    }

    @Override
    public void pi(String target, String data)
    {
        handler.processingInstruction(target, data);
    }

    @Override
    public void endComplex()
    {
        handler.endElement();
        nsStack.pop();
    }

    @Override
    public void end()
    {
        // TODO : implement user-friendly finishing?
        handler.endDocument();
    }

    @Override
    public void reset()
    {
        // TODO : prolly mostly stack management?
        nsStack.reset();
        try
        {
            handler.flush();
            handler.close();
        }
        catch (IOException ioe)
        {
            // TODO : something that isn't this
            throw new GenXDMException(ioe);
        }
    }
    
    private final ContentHandler handler;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    
    private static final String NIT = "";
}
