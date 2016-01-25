package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.Map;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;

public class TypedContentHelper<A>
    implements ContentHelper
{
    public TypedContentHelper(SequenceHandler<A> output, ComponentProvider components, AtomBridge<A> atoms)
    {
        handler = PreCondition.assertNotNull(output, "sequence handler");
        provider = PreCondition.assertNotNull(components, "component provider");
        bridge = PreCondition.assertNotNull(atoms, "atom bridge");
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
        // TODO Auto-generated method stub
        
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
        // TODO: typed form
//        handler.text(value);
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

    private final SequenceHandler<A> handler;
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    
    private static final String NIT = "";
}
