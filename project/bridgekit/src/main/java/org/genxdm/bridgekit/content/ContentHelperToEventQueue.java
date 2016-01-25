package org.genxdm.bridgekit.content;

import java.util.List;
import java.util.Map;

import org.genxdm.typed.types.AtomBridge;

public class ContentHelperToEventQueue
    implements ContentHelper
{
    public ContentHelperToEventQueue() { isTyped = false; }
    public <A> ContentHelperToEventQueue(AtomBridge<A> bridge) { isTyped = true; }

    public List<ContentEvent> getQueue()
    {
        // TODO Auto-scribbled method stub
        return null;
    }
    
    public <A> List<TypedContentEvent<A>> getTypedQueue()
    {
        // TODO more scribbly
        return null;
    }
    
    @Override
    public void start()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void simpleElement(String ns, String name, String value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Attrib newAttribute(String name, String value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Attrib newAttribute(String ns, String name, String value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void comment(String text)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void pi(String target, String data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void endComplex()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void end()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub

    }

    private final boolean isTyped;
}
