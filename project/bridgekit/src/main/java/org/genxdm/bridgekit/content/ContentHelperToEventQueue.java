package org.genxdm.bridgekit.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.ContentEvent;
import org.genxdm.creation.EventKind;
import org.genxdm.creation.EventQueue;
import org.genxdm.exceptions.GenXDMException;

public class ContentHelperToEventQueue
    extends AbstractContentHelper
    implements EventQueue
{
    public ContentHelperToEventQueue(Map<String, String> bindings)
    {
        if (bindings == null)
            bindings = new HashMap<String, String>();
        nsStack.push(bindings);
    }

    @Override
    public List<ContentEvent> getQueue()
    {
        if (depth != 0)
            throw new GenXDMException("Unbalanced queue! Missing 'end' event for 'start' event");
        return queue;
    }
    
    @Override
    public void start()
    {
        // TODO: this should *probably* throw an exception, because how stupid is this?
        // it's an event queue. we shouldn't have a document in it.
        // *alternately*, just discard the event. do that for now.
        //queue.add(new ContentEventImpl((URI)null, null));
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
        queue.add(new ContentEventImpl(ns, name, nsStack.getPrefix(ns, bindings)));
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                queue.add(new ContentEventImpl(EventKind.NAMESPACE, binding.getKey(), binding.getValue()));
            }
        if (attributes != null)
            for (Attrib attribute : attributes)
            {
                if (attribute.getNamespace().isEmpty())
                    queue.add(new ContentEventImpl(NIT, attribute.getName(), NIT, attribute.getValue()));
                else
                    queue.add(new ContentEventImpl(attribute.getNamespace(), attribute.getName(), nsStack.getAttributePrefix(attribute.getNamespace(), bindings), attribute.getValue()));
            }
        nsStack.push(bindings);
        depth++;
    }

    @Override
    public void comment(String text)
    {
        queue.add(new ContentEventImpl(EventKind.COMMENT, text));
    }

    @Override
    public void pi(String target, String data)
    {
        queue.add(new ContentEventImpl(EventKind.PROCESSING_INSTRUCTION, target, data));
    }

    @Override
    public void endComplex()
    {
        queue.add(new ContentEventImpl(EventKind.END_ELEMENT));
        nsStack.pop();
        depth--;
    }

    @Override
    public void end()
    {
        //TODO: see above, for start(). we should *not* have documents inside
        // an event queue, so we either throw an exception or we ignore it.
        // ignore for now.
        //queue.add(new ContentEventImpl(EventKind.END_DOCUMENT));
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
        queue.add(new ContentEventImpl(EventKind.TEXT, value));
    }

    private final List<ContentEvent> queue = new ArrayList<ContentEvent>();
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    
    private int depth = 0;
    
    private static final String NIT = "";
}
