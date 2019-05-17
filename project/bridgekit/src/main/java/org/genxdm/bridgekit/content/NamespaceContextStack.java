package org.genxdm.bridgekit.content;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.genxdm.creation.Attrib;

public class NamespaceContextStack
{
    public NamespaceContextStack(String stem)
    {
        if ((stem == null) || stem.trim().isEmpty())
            prefixPrefix = PREFIX_PREFIX;
        else
            prefixPrefix = stem;
    }
    
    public void push(Map<String, String> bindings)
    {
        NSContext bound = new NSContext(bindingStack.peek(), bindings);
        bindingStack.push(bound);
    }
    
    public void pop()
    {
        bindingStack.pop();
    }

    public String getPrefix(String namespace, Map<String, String> bindings)
    {
        // namespace is guaranteed non-null.
        if (bindings != null)
        {
            // if we have a set of bindings, then check if this namespace is already bound
            for (Map.Entry<String, String> entry : bindings.entrySet())
            {
                if (namespace.equals(entry.getValue()))
                    return entry.getKey();
            }
        }
        // either bindings was null, OR it doesn't contain our namespace
        // look up the stack.
        NSContext context = bindingStack.peek();
        if (context != null)
        {
            String answer = context.getPrefixForNamespace(namespace);
            if ( (answer != null) && // got a non-null answer
                    ((bindings == null) || !bindings.keySet().contains(answer)) ) // which is not overridden locally
                return answer; // return it
        }
        return null; // no bindings, or not local, and either not already defined or locally overridden
    }
    
    public String getAttributePrefix(String namespace, Map<String, String> bindings)
    {
        // namespace is guaranteed non-null.
        // if it's the global namespace, it's always the global prefix
        if (namespace.trim().isEmpty())
            return NIT;
        return getPrefix(namespace, bindings);
    }
    
    public Map<String, String> checkAttributePrefix(Attrib attribute, Map<String, String> bindings)
    {
        // the goal of this is to populate the (supplied-as-argument) map with
        // namespaces, if this attribute has a foreign namespace that's
        // not already bound. if the map is null, and we need it, we create it.
        // most of the time it's a local attribute, so resolve that *quick*,
        // and only look further if the namespace is non-empty (non-default/global).
        String namespace = attribute.getNamespace();
        if (!namespace.isEmpty()) // only care if not global namespace
        {
            String bound = getAttributePrefix(namespace, bindings); // use the stack and map
            if (bound == null) // nothing. get a new prefix
            {
                bound = newPrefix();
                if (bindings == null) // no bindings map, make one
                    bindings = new HashMap<String, String>();
                bindings.put(bound, namespace); // add it
            }
        }
        return bindings;
    }
    
    public String getNamespace(final String prefix)
    {
        if (prefix != null)
        {
            NSContext context = bindingStack.peek();
            if (context != null)
                return context.getNamespaceForPrefix(prefix); 
        }
        return null;
    }
    
    private class NSContext
    {
        NSContext(NSContext dad, Map<String, String> bound)
        { parent = dad; bindings = bound; }
        String getPrefixForNamespace(String namespace)
        {
            if (bindings != null)
            {
                for (Map.Entry<String, String> entry : bindings.entrySet())
                {
                    if (namespace.equals(entry.getValue()))
                        return entry.getKey();
                }
            }
            if (parent != null)
            {
                String answer = parent.getPrefixForNamespace(namespace);
                if ((bindings == null) || !bindings.keySet().contains(answer))
                    return answer;
            }
            return null;
        }
        String getNamespaceForPrefix(String prefix)
        {
            String result = null;
            if (bindings != null)
            {
                result = bindings.get(prefix);
                if (result != null)
                    return result;
            }
            if (parent != null)
                result = parent.getNamespaceForPrefix(prefix);
            return result;
        }
        private final NSContext parent;
        private final Map<String, String> bindings;
    }
    
    public String newPrefix()
    {
        return prefixPrefix+(nscounter++);
    }
    
    public void reset()
    {
        bindingStack.clear();
        nscounter = 0;
    }
    
    private final Deque<NSContext> bindingStack = new ArrayDeque<NSContext>();
    private final String prefixPrefix;
    private int nscounter = 0;
    
    private static final String NIT = "";
    private static final String PREFIX_PREFIX = "cns";
}
