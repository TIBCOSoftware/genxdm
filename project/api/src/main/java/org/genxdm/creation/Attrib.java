package org.genxdm.creation;


/** A simple interface with the necessary bits for representing an
 * attribute, during creation.
 *
 */
public interface Attrib
{
    /** Return the namespace associated with this attribute.
     * 
     * @return the URI of the namespace (which may be, and usually is, the
     * empty string), never null
     */
    String getNamespace();
    /** Return the name of this attribute.
     * 
     * @return the name of the attribute, never null, never empty string or whitespace
     */
    String getName();
    /** Return the value of the attribute.
     * 
     * @return the value of the attribute, never null (but may be empty string)
     */
    String getValue();
}
