package org.genxdm.names;

public interface RegisteredPrefixProvider
{
    /** Return the prefix preferred by the specified namespace.
     * 
     * @param namespace the namespace to query; if null, the method returns null
     * @return the prefix that the specified namespace has registered as its
     *         preference, or null if no preference is registered
     */
    String getRegisteredPrefix(String namespace);

}
