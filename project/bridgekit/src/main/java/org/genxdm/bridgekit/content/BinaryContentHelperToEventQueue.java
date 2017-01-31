package org.genxdm.bridgekit.content;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.creation.EventKind;
import org.genxdm.creation.TypedContentEvent;
import org.genxdm.creation.TypedEventQueue;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;

public class BinaryContentHelperToEventQueue<A>
    extends AbstractContentHelper
    implements BinaryContentHelper, TypedEventQueue<A>
{
    public BinaryContentHelperToEventQueue(AtomBridge<A> atoms, ComponentProvider components, Map<String, String> bindings) 
    { 
        bridge = PreCondition.assertNotNull(atoms);
        provider = PreCondition.assertNotNull(components);
        if (bindings == null)
            context = new HashMap<String, String>();
        else
            context = bindings;
    }

    public List<TypedContentEvent<A>> getQueue()
    {
        return queue;
        // TODO: keep track of whether we're unbalanced. If we are,
        // throw an exception.
        // maybe use the depth tracker?
    }
    
    @Override
    public void start()
    {
        queue.add(new TypedContentEventImpl<A>((URI)null, null));
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        // TODO: handle the whole element
        // TODO Auto-generated method stub

    }

    @Override
    public void binaryElement(String ns, String name, byte [] data)
    {
        binaryExElement(ns, name, null, null, data);
    }
    
    public void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data)
    {
        startComplex(ns, name, bindings, attributes);
        
        // TODO handle binary content
        endComplex();
    }

    @Override
    public void comment(String text)
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.COMMENT, text));
    }

    @Override
    public void pi(String target, String data)
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.PROCESSING_INSTRUCTION, target, data));
    }

    @Override
    public void endComplex()
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.END_ELEMENT));
    }

    @Override
    public void end()
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.END_DOCUMENT));
    }

    @Override
    public void reset()
    {
        queue.clear();
        // TODO: clear the namespace stuff, too
        // that means we need to have a way to reset context, for reuse!
    }
    
    protected void text(String ns, String name, String value)
    {
        // TODO: queue.add(text)
    }
    
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    private final Map<String, String> context;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("qns");

    private List<TypedContentEvent<A>> queue = new ArrayList<TypedContentEvent<A>>();
}
