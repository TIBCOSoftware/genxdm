package org.genxdm.bridgekit.content;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentHelperToEventQueue
    extends AbstractContentHelper
{
    public ContentHelperToEventQueue(Map<String, String> bindings)
    {
        if (bindings == null)
            context = new HashMap<String, String>();
        else 
            context = bindings;
    }

    public List<ContentEvent> getQueue()
    {
        return queue;
        // throw new GenXDMException("Unbalanced queue! Missing 'end' event for 'start' event");
    }
    
    @Override
    public void start()
    {
        queue.add(new ContentEvent((URI)null, null));
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        // big deal is all in here; queue a start element:
        // TODO: do it right.
        String prefix = null;
        queue.add(new ContentEvent(ns, name, prefix));
        // TODO: queue the namespaces
        // TODO: queue the attributes
    }

    @Override
    public void comment(String text)
    {
        queue.add(new ContentEvent(EventKind.COMMENT, text));
    }

    @Override
    public void pi(String target, String data)
    {
        queue.add(new ContentEvent(EventKind.PROCESSING_INSTRUCTION, target, data));
    }

    @Override
    public void endComplex()
    {
        queue.add(new ContentEvent(EventKind.END_ELEMENT));
    }

    @Override
    public void end()
    {
        queue.add(new ContentEvent(EventKind.END_DOCUMENT));
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
        queue.add(new ContentEvent(EventKind.TEXT, value));
    }

    private final List<ContentEvent> queue = new ArrayList<ContentEvent>();
    private final Map<String, String> context; // namespace context at start
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
}
