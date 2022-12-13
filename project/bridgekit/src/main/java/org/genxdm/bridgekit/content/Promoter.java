package org.genxdm.bridgekit.content;

import org.genxdm.io.ContentHandler;

/** The Promoter interface exists in order to extend the ContentHandler interface to
 * handle binary content without the UTF-16 binary encoding penalty.
 *
 * Placed here where it is currently used and restricted to package access. If it
 * turns out to be a useful innovation DO NOT make it public here; move it to
 * org.genxdm.io in the api module, and make it public there, refactoring the
 * local implementations here to use the relocated version.
 *
 */
interface Promoter
    extends ContentHandler
{
    /** Extend ContentHandler to allow untyped attributes with binary content
     *
     * @param namespace the namespace, allows null (automatically changed to empty string)
     * @param name the local name; may not be null
     * @param prefix the prefix hint, which may change if the target bindings differ from
     * expectations
     * @param data the actual binary content, which may be a zero-length array, but may not be null
     */
    void binaryAttribute(final String namespace, final String name, final String prefix, final byte [] data);
    
    /** Extend ContentHandler to allow untyped text nodes with binary content
     *
     * @param data teh actual binary content, which may be a zero-length array, but may not be null
     */
    void binaryText(final byte [] data);
}
