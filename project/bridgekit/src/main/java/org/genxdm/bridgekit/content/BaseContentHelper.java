package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BranchCopier;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

// simplified api for generating untyped xml trees.
public class BaseContentHelper
    extends AbstractContentHelper
    implements BranchCopier, ContentHandlerSource
{
    public BaseContentHelper(ContentHandler handler)
    {
        this.handler = PreCondition.assertNotNull(handler, "content handler");
    }
    
    @Override
    public ContentHandler getContentHandler()
    {
        return handler;
    }

    @Override
    public void start()
    {
        if (depth >= 0)
            throw new GenXDMException("Illegal start-document invocation: nesting depth >= 0 ("+depth+")");
        handler.startDocument(null, null);
        depth++;
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        if ( (name == null) || name.trim().isEmpty() )
            throw new IllegalArgumentException("Illegal start-complex invocation: unnamed element");
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
        depth++;
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
        depth--;
    }

    @Override
    public void end()
    {
        while (depth > 0)
            endComplex();
        handler.endDocument();
        depth--;
    }

    @Override
    public void reset()
    {
        nsStack.reset();
        depth = -1;
        try
        {
            handler.flush();
            handler.close();
        }
        catch (IOException ioe)
        {
            throw new GenXDMException("Exception in reset of BaseContentHelper, while flushing attached handler", ioe);
        }
    }
    
    @Override
    protected void text(String ns, String name, String value)
    {
        // namespace and name are there for error messages.
        handler.text(value);
    }
    
    @Override
    public void copyTreeAt(ContentGenerator generator)
    {
        PreCondition.assertNotNull(generator);
        PreCondition.assertTrue(generator.isElement(), "ContentGenerator must be positioned on an element");
        // TODO: make sure that we're positioned inside an element
        // TODO: should we be handling namespace fixups?
        generator.write(handler);
    }

    private final ContentHandler handler;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    
    private int depth = -1;
    
    private static final String NIT = "";
}
