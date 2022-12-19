package org.genxdm.bridgekit.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryAttrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.creation.EventKind;
import org.genxdm.creation.TypedContentEvent;
import org.genxdm.creation.TypedEventQueue;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceGenerator;

// this has a metric f..kton of copypasta from typedcontenthelper,
// with some very minor tweaks. think about how to refactor cleanly.
// same is true for the untyped contenthelpertoeventqueue vis-a-vis basecontenthelper
public class BinaryContentHelperToEventQueue<A>
    extends AbstractContentHelper
    implements BinaryContentHelper, TypedEventQueue<A>
{
    public BinaryContentHelperToEventQueue(Map<String, String> bindings) 
    { 
        if (bindings == null)
            bindings = new HashMap<String, String>();
        nsStack.push(bindings);
        queue = new ArrayList<TypedContentEvent>();
    }

    @Override
    public List<TypedContentEvent> getQueue()
    {
        // this is now deprecated.
        // disable it.
        return null;
//        if (depth != 0)
//            throw new GenXDMException("Unbalanced queue! Missing 'end' event for 'start' event");
//        return queue;
    }
    
    @Override
    public SequenceGenerator<A> getTypedGenerator()
    {
        // alternate form:
        //if (depth != 0) // two options: throw an exception (best way) or call endComplex() until it's balanced
        //    throw new GenXDMException("Unbalanced queue! Missing 'endComplex' event for 'startComplex' event!");
        while (depth != 0) // this is easier, but supports sloppy coding
            endComplex();
        return new TypedQueueGenerator<A>(queue);
    }
    
    @Override
    public void start()
    {
        // TODO: this should *probably* throw an exception, because how stupid is this?
        // it's an event queue. we shouldn't have a document in it.
        // *alternately*, just discard the event. do that for now.
        //queue.add(new TypedContentEventImpl((URI)null, null));
    }
    
    @Override
    public void startComplex(final String namespace, final String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        if ( (name == null) || name.trim().isEmpty() )
            throw new IllegalArgumentException("Illegal start-complex invocation: unnamed element");
        String ns = namespace;
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
        // conditional for loop has weird indents
        if (attributes != null)
            for (Attrib att : attributes)
            {
                if (!att.getNamespace().isEmpty())
                    bindings = nsStack.checkAttributePrefix(att, bindings);
            }
        // by the time we get here, the local namespace context should be consistent.
        // in most cases, we shouldn't have had to do anything to achieve that.
        queue.add(new TypedContentEventImpl(ns, name, nsStack.getPrefix(ns, bindings))); // startElement

        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                queue.add(new TypedContentEventImpl(EventKind.NAMESPACE, binding.getKey(), binding.getValue()));
            }

// note: the xsi:type override isn't handled in this impl. is it necessary?        
        if (attributes != null)
            for (Attrib attribute : attributes)
            {
                final boolean isBinary = (attribute instanceof BinaryAttrib);
                if (attribute.getNamespace().isEmpty())
                {
                    if (isBinary)
                        queue.add(new TypedContentEventImpl(NIT, attribute.getName(), NIT, ((BinaryAttrib)attribute).getData()));
                    else
                        queue.add(new TypedContentEventImpl(NIT, attribute.getName(), NIT, attribute.getValue()));
                }
                else
                {
                    if (isBinary)
                        queue.add(new TypedContentEventImpl(attribute.getNamespace(), attribute.getName(), nsStack.getAttributePrefix(attribute.getNamespace(), bindings), ((BinaryAttrib)attribute).getData()));
                    else
                        queue.add(new TypedContentEventImpl(attribute.getNamespace(), attribute.getName(), nsStack.getAttributePrefix(attribute.getNamespace(), bindings), attribute.getValue()));
                }
            }
        
        // no else: there are no attributes present, though some might be defaulted or missing
        nsStack.push(bindings);
        depth++;
    }

    @Override
    public void binaryElement(String ns, String name, byte [] data)
    {
        binaryExElement(ns, name, null, null, data);
    }
    
    @Override
    public void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data)
    {
        if (data == null)
            throw new GenXDMException("Illegal content in invocation of binary-element for element {"+ns+"}"+name+": missing data");
        startComplex(ns, name, bindings, attributes);
        // that handles element, namespaces, attributes. we don't have a separate text method here. just this:
        queue.add(new TypedContentEventImpl(data));
        endComplex();
    }

    @Override
    public void comment(String text)
    {
        queue.add(new TypedContentEventImpl(EventKind.COMMENT, text));
    }

    @Override
    public void pi(String target, String data)
    {
        queue.add(new TypedContentEventImpl(EventKind.PROCESSING_INSTRUCTION, target, data));
    }

    @Override
    public void endComplex()
    {
        queue.add(new TypedContentEventImpl(EventKind.END_ELEMENT));
        nsStack.pop();
        depth--;
    }

    @Override
    public void end()
    {
        //TODO: see above, for start(). we should *not* have documents inside
        // an event queue, so we either throw an exception or we ignore it.
        // ignore for now.
        //queue.add(new TypedContentEventImpl(EventKind.END_DOCUMENT));
    }

    @Override
    public void reset()
    {
        nsStack.reset();
        depth = -1;
        queue = new ArrayList<TypedContentEvent>();
    }
    // the next two are not ideal, but neither is returning attribs for
    // newattribute() in abstracthelper, for our case.
    @Override
    public BinaryAttrib newBinaryAttribute(String name, byte [] data)
    {
        return new BinaryAttr(name, data);
    }
    
    @Override
    public BinaryAttrib newBinaryAttribute(String ns, String name, byte[] data)
    {
        return new BinaryAttr(ns, name, data);
    }
    
    @Override
    protected void text(String ns, String name, String value)
    {
        // now handle the content of the text node. it *may* be empty or null,
        // in which case, bypass all of this and don't even supply a text node.
        // this differs from base contenthelper, because trying to distinguish
        // between empty string and empty sequence is a #$%^& pain.
        // note that this doesn't handle the TEXT_BINARY event, which is in binaryEx
        if ( (value != null) && !value.trim().isEmpty() )
            queue.add(new TypedContentEventImpl(EventKind.TEXT, value));
    }
    
    private List<TypedContentEvent> queue;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("qns");
    
    private int depth = 0;
    
    private static final String NIT = "";

}
