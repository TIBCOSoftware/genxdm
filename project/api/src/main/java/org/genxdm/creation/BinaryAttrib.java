package org.genxdm.creation;

/** Extension of simple Attrib interface solely for attributes of binary
 * types (typically base64Binary)
 *
 */
public interface BinaryAttrib
    extends Attrib
{
    byte [] getData();
}
