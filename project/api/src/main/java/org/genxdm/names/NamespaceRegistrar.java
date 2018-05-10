package org.genxdm.names;

import java.util.Map;

public interface NamespaceRegistrar
    extends RegisteredPrefixProvider
{
    /** Get the contents of the registry
     * 
     * Note that the registry <em>must</em> guarantee that changes to this
     * map will not change its own content (that is, the registry must return
     * either a copy or an immutable wrapper around its own internal map).
     * Callers should check whether the return value is immutable.
     * 
     * @return a map of namespace to prefix representing the internal content
     * of this namespace registry; may be empty but never null
     */
    Map<String, String> getNamespaceRegistry();
    
    /** Register a preferred prefix for one of the namespaces in our SchemaComponentCache
     * 
     * It is not guaranteed that this prefix will always be used, and it is perfectly
     * possible to register multiple namespaces with the same prefix.
     * 
     * It is not an error to register a namespace not in the cache, but that's the assumption.
     * 
     * @param prefix the preferred prefix; if null removes any previously-defined preference
     * @param namespace the namespace for which the preference is registered; may not be null
     *        (but may be the empty string)
     */
    void registerNamespace(String namespace, String prefix);

    /** Register multiple namespace-to-prefix preference mappings at once.
     * 
     * This is the usual way of communicating between namespace registries.
     * 
     * @param nsToPrefixMap a map (which may not be null) of namespaces and
     *        their preferred prefixes; if null, nothing happens
     */
    void registerNamespaces(Map<String, String> nsToPrefixMap);
}
