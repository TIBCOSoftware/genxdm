package org.genxdm.creation;

/** Extension of simple Attrib interface solely for attributes of binary
 * types (typically base64Binary)
 *
 */
public interface BinaryAttrib
    extends Attrib
{
    /** Return the value of the attribute.
     * 
     * Note: users that have mixed Attrib and BinaryAttrib implementations in
     * an implementation should take care to <em>not</em> call getValue() on
     * a BinaryAttrib. Results are officially undefined: an implementation can
     * return an empty string (but not null, per the contract of getValue()) even
     * if getData() returns a massive byte array, <em>or</em> the implementation
     * may decide to encode the data appropriately (as base64Binary or hexBinary
     * represented in 16-bit UTF-16 characters), which is grotesquely inefficient
     * even leaving aside the fact that the original byte array will further inflate
     * the memory impact.
     * 
     * @return the value of the attribute, never null (but may be an array of zero
     * length)
     */
    byte [] getData();
}
