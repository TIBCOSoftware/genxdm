package org.genxdm.bridgekit.names;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.genxdm.names.NamespaceRegistrar;

public class DefaultNamespaceRegistrar
    implements NamespaceRegistrar
{
    public DefaultNamespaceRegistrar(Map<String, String> prefixes)
    {
        if (prefixes != null)
            preferredPrefixes = prefixes;
        else
            preferredPrefixes = new HashMap<String, String>();
    }

    @Override
    public Map<String, String> getNamespaceRegistry()
    {
        return Collections.unmodifiableMap(preferredPrefixes);
    }

    @Override
    public String getRegisteredPrefix(String namespace)
    {
        return preferredPrefixes.get(namespace);
    }

    @Override
    public void registerNamespace(String namespace, String prefix)
    {
        // null is meaningless; empty string can't have a prefix bound
        if ( (namespace != null) && !namespace.equals("") )
        {
            // similarly, we aren't going to put a preference for the default
            // prefix into this registry; that's not what this tool is for
            if ( (prefix != null) && !prefix.equals("") )
                preferredPrefixes.put(namespace, prefix);
            // note that we do not do any error checking, like
            // checking whether some damfool wants to bind every namespace
            // to the same worthless prefix. again, that's not the purpose
            // of the tool; doing that is stupid, but just defeats the
            // utility of the tool, and we fall back to previous patterns
        }
    }

    @Override
    public void registerNamespaces(Map<String, String> nsToPrefixMap)
    {
        if ( (nsToPrefixMap != null) && !nsToPrefixMap.isEmpty() )
            preferredPrefixes.putAll(nsToPrefixMap);
    }

    private final Map<String, String> preferredPrefixes;
}
