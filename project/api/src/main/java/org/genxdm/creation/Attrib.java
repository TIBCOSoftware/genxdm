package org.genxdm.creation;

import org.genxdm.exceptions.PreCondition;

/** A simple container class with the necessary bits for representing an
 * attribute, during creation.
 * 
 * This abstraction contains nothing more than what would be found in an attribute
 * in a document gotten off the wire or from a random file containing xml. The
 * attribute has a name and a value; if it has a prefix then that should resolve
 * to a namespace by resolution of the prefix-to-namespace bindings. There is
 * no type information stored! This is intentional, because just as the usual
 * attribute's (absence of) namespace actually means "scoped to its parent element",
 * the type of the attribute is also generally associated with the element which
 * the attribute decorates.
 *
 */
public final class Attrib
{
    /** Usual constructor; most attributes are scoped to their containing element
     * and thus have no namespace (or prefix).
     * 
     * @param name the local name; must be a valid XML NCName.
     * @param value the value of the attribute; if null will be treated as an empty string.
     */
    public Attrib(String name, String value)
    {
        this("", name, value);
    }
    
    /** Less common constructor; elements that are imported into foreign namespaces
     * must be associated with namespaces themselves.
     * 
     * @param namespace the uri of the namespace; if null or empty after trimming
     * whitespace, this constructor behaves identically to the two-argument constructor.
     * @param name the local name; must be a valid XML NCName.
     * @param value the value of the attribute; if null will be treated as an empty string.
     */
    public Attrib(String namespace, String name, String value)
    {
       ns = PreCondition.assertNotNull(namespace, "namespace");
       n = PreCondition.assertNotNull(name, "name");
       v = value;
    }
    
    /** Return the namespace associated with this attribute.
     * 
     * @return the URI of the namespace (which may be, and usually is, the
     * empty string), never null
     */
    public String getNamespace() { return ns; }
    /** Return the name of this attribute.
     * 
     * @return the name of the attribute, never null, never empty string or whitespace
     */
    public String getName() { return n; }
    /** Return the value of the attribute.
     * 
     * @return the value of the attribute, never null (but may be empty string)
     */
    public String getValue() { return v; }
    
    private final String ns;
    private final String n;
    private final String v;
}
