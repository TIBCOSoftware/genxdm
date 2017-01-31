package org.genxdm.creation;

import java.util.Map;

/** Extends ContentHelper by providing methods for setting binary content.
 *
 * This is not the difference between typed and untyped; it's mostly a way of
 * avoiding the cost (in memory and processing) of converting between a byte
 * array and a string (especially a base64 representation using UTF-16, which
 * is painfully enormous).
 * 
 * It can be used in a typed content helper, and should be, if content is
 * expected to contain large binary-content nodes <em>and</em> the tree model
 * supports typed values (rather than storing text values and converting).
 */
public interface BinaryContentHelper
    extends ContentHelper
{
    /** Simple form for creating an element with binary content, supplying the
     * value, and closing the element all at once, with no attributes or namespaces.
     * 
     * Implementations must guarantee that this method behaves identically to
     * the five-argument method of the same name, when arguments three and four
     * are passed as null.
     * 
     * @param ns the URI of the namespace for this element (not the prefix);
     * if null, will be treated as the empty string (global/default namespace)
     * @param name the local name of this element; may not be null or empty
     * after trimming whitespace (must be a valid element name). If it is not
     * a valid element name, an exception will be thrown, either IllegalArgumentException
     * or whatever the underlying tree model creation tool throws.
     * @param data the content of the element, as an array of bytes. if null,
     * an empty element will be created.
     */
    void binaryElement(String ns, String name, byte [] data);

    /** More complex form for creating an element with binary content, supplying
     * the value, and closing the element all at once, with optional attributes
     * and namespace declarations.
     * 
     * @param ns the URI of the namespace for this element (not the prefix);
     * if null, will be treated as the empty string (global/default namespace)
     * @param name the local name of this element; may not be null or empty
     * after trimming whitespace (must be a valid element name). If it is not
     * a valid element name, an exception will be thrown, either IllegalArgumentException
     * or whatever the underlying tree model creation tool throws.
     * @param bindings bindings of namespaces to prefixes; it should include all
     * of the preferred prefixes for all of the namespaces used in this scope, if
     * possible. Implementations <em>may</em> (or may <em>not</em>) perform
     * namespace fixup; developers are advised to manage bindings themselves. If
     * null, no localbindings are established. If internally inconsistent, the underlying
     * tree model creator will presumably throw an exception.
     * @param attributes a Set&lt;Attrib> or List&lt;Attrib> of the simplified form of
     * attributes (q.v.), if any are on this element. If null (or empty), ignored.
     * If inconsistent (two attributes with the same name and different values,
     * for instance), then the underlying creation tool will throw an exception.
     * @param data
     */
    void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data);
    
}
