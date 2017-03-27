package org.genxdm.creation;

import java.util.Map;

/** Primary creation interface: a developer-oriented, in-order XML generation tool.
 * 
 * An implementation of ContentHelper will be instantiated with some form of
 * bridge-specific (tree model specific) tools to enable creation of XML documents
 * in-order. In the current (known) implementation, there are also tools for
 * queueing contenthelper "events" and replaying them into the helper at the
 * appropriate point, which provides a means of creating multiple subtrees at
 * once (by storing the creation events for other subtrees until one reaches
 * the point in the construction of the main tree that those deferred queues
 * should exist, and then flushing the queue into the tree through the helper).
 * 
 * In general, it is expected that the initialized implementation will be supplied
 * a tool that exposes the NodeSource interface; developers should retain a reference
 * to the supplied NodeSource, and call its getNode() method when finished with
 * construction.
 * 
 */
public interface ContentHelper
{

    /** Start creation of a tree at the document node, simple form.
     * 
     * start() (in either the no-argument or four-argument variant) must
     * only be called once, and can only be the first call for a new
     * ContentHelper (exception: attributes can be created first).
     * 
     * Only the document node is started. Must be paired with end().
     */
    void start(); // start just the document, no document element
    
    /** Start the creation of a tree at the document node, supplying the
     * document element (root element) at the same time.
     * 
     * start() (in either the no-argument or four-argument variant) must
     * only be called once, and can only be the first call for a new
     * ContentHelper (exception: attributes can be created first).
     * 
     * Both the document node and its (sole permitted) element child (the
     * 'document element' or 'root element' or sometimes misleadingly 'root node')
     * will be created.
     * 
     * This is actually a portmanteau of the start() and startComplex() methods,
     * provided for convenience. There is no similar portmanteau for either the
     * simplified startComplex() (lacking namespace and attribute arguments) or
     * for startSimplex(); to do either of those, invoke start() followed by the
     * desired startComplex(String, String) or startSimplex() methods.
     * 
     * Must be paired with end(). Note that the implementation is expected to
     * track invocations of endComplex(), so that the developer is <em>not</em>
     * required to pair this method with endComplex();end();. end() must recognize
     * that the document element is not completed, and internally supply the
     * extra call (developers who want to call endComplex() regardless of this
     * assurance are guaranteed that doing so will <em>not</em> cause an error).
     * 
     * Parameters are thus identical to those required for the four-argument version
     * of startComplex().
     * 
     * @param ns the URI of the namespace for the document element (not the prefix);
     * if null, will be treated as the empty string (global/default namespace)
     * @param name the local name of the document element; if null or empty, and
     * no other arguments are supplied (ns, bindings, and attributes are also all
     * null), the implementation must treat this as equivalent to the start() method
     * with no arguments. Otherwise, if any other argument is non-null and name is
     * null or empty (after trim()), an IllegalArgumentException will be thrown.
     * @param bindings bindings of namespaces to prefixes; it should include all
     * of the preferred prefixes for all of the namespaces used in this scope, if
     * possible. Implementations <em>may</em> (or may <em>not</em>) perform
     * namespace fixup; developers are advised to manage bindings themselves. If
     * null, no bindings are established. If internally inconsistent, the underlying
     * tree model creator will presumably throw an exception.
     * @param attributes a Set&lt;Attrib> or List&lt;Attrib> of the simplified form of
     * attributes (q.v.), if any are on this element. If null (or empty), ignored.
     * If inconsistent (two attributes with the same name and different values,
     * for instance), then the underlying creation tool will throw an exception.
     */
    void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes);
    
    /** Start a complex element with the given name, namespace, and optional namespace
     * bindings and attributes.
     * 
     * A complex element is one that contains child elements, comments, or
     * processing instructions, so far as the ContentHelper interface is
     * concerned. Must be paired with a corresponding endComplex() method call,
     * presumably after the invocation of the methods creating its child
     * content.
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
     */
    void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes);
    
    /** Simple form of complex element creation.
     * 
     * Many complex elements have neither namespace declarations or attributes;
     * this is a convenience method, which must be identical in effect, in the
     * implementation, to invoking the four-argument method with the final two
     * arguments passed as null.
     * 
     * @param ns the URI of the namespace for this element (not the prefix);
     * if null, will be treated as the empty string (global/default namespace)
     * @param name the local name of this element; may not be null or empty
     * after trimming whitespace (must be a valid element name). If it is not
     * a valid element name, an exception will be thrown, either IllegalArgumentException
     * or whatever the underlying tree model creation tool throws.
     */
    void startComplex(String ns, String name);
    
    /** Create an element of simple type (or an empty element), with its
     * value, opening and closing it in a single call.
     * 
     * This could create a node that looks like this:
     * &lt;element>value&lt;/element> or this: &lt;element />, for example
     * 
     * This is probably the most common type of simple element; there is also a
     * more complex method, with five arguments. Implementations must guarantee
     * that the behavior of this method is identical to the behavior of the
     * five argument method with arguments three and four passed as null.
     * 
     * @param ns the URI of the namespace for this element (not the prefix);
     * if null, will be treated as the empty string (global/default namespace)
     * @param name the local name of this element; may not be null or empty
     * after trimming whitespace (must be a valid element name). If it is not
     * a valid element name, an exception will be thrown, either IllegalArgumentException
     * or whatever the underlying tree model creation tool throws.
     * @param value the value of the child text node, as a string. If null, will
     * be treated as an empty text node or empty element.
     */
    void simpleElement(String ns, String name, String value);
    
    /** Create an element of simple type (or an empty element), with local namespace
     * declarations, attributes, or both.
     * 
     * For example: &lt;nsxyz:element xmlns:nsxyz="http://xyz/">value&lt;/nsxyz:element>
     * or &lt;element a1="2" a2="true" a3="whiskey" />
     * 
     * Probably less common than the three-argument simpleElement() form.
     * Callers must <em>not</em> pair this with endComplex(); implementations
     * are certain to fail in an ugly fashion if they do. The call opens and
     * closes the element with all of its content.
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
     * @param value the value of the child text node, as a string. If null, will
     * be treated as an empty text node or empty element.
     */
    void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value);
    
    /** Simple form of attribute creation, most commonly used: the name and the text
     * value are both supplied.
     * 
     * The created attribute will be in the global/default namespace (no prefix).
     * This is true for the overwhelming majority of attributes encountered in the wild.
     * 
     * @param name the name of the attribute, which must be unique in the scope of
     * the element it is to decorate.
     * @param value the value of the attribute, as text.
     * @return a newly-created Attrib with the name and value supplied 
     */
    Attrib newAttribute(String name, String value);
    
    /** More complex form of attribute creation, in which a namespace (not the
     * prefix!) is supplied as well.
     * 
     * @param ns if null or the empty string after trimming whitespace, this is
     * the same as the two-argument invocation. Otherwise, the namespace in which
     * this attribute is declared (the attribute will be prefixed with whatever
     * prefix is bound to this namespace in scope).
     * @param name the name of the attribute, which must be unique in the scope of
     * the element it is to decorate.
     * @param value the value of the attribute, as text.
     * @return a newly-created Attrib with the namespace, name, and value supplied 
     */
    Attrib newAttribute(String ns, String name, String value);
    
    /** Create a comment.
     * 
     * Must happen between a startComplex() and endComplex() call (possibly mixed
     * in with other simpleElement() or simplexElement() or startComplex()+endComplex()
     * or pi() calls representing siblings).
     * 
     * @param text the comment text; if null, an empty comment node will be created.
     */
    void comment(String text);
    
    /** Create a processing instruction.
     * 
     * Must happen between a startComplex() and endComplex() call (possibly mixed
     * in with other simpleElement() or simplexElement() or startComplex()+endComplex()
     * or comment() calls representing siblings).
     * 
     * @param target the content of the processing instruction up to the first
     *        whitespace, commonly called the target and used to determine which
     *        processor cares about it and how the rest of its content is interpreted.
     *        Must not be null.
     * @param data the processing instruction content after the first whitespace; 
     *        if null, equivalent to empty string.
     */
    void pi(String target, String data);

    /** Every startComplex() invocation must be matched by an endComplex() invocation.
     * 
     * However, it is an error to invoke endComplex() for simpleElement() or
     * simplexElement(). Pair endComplex() with startComplex() (and optionally
     * with the four-argument version of start(), but it's better to trust the
     * implementation to close the document element in end() if it was opened in
     * start(String, String, Map&lt;String, String>, Iterable&lt;Attrib>)).
     */
    void endComplex();

    /** End tree construction.
     * 
     * Just as there can only be one start(), there can only be one end(). If a
     * fragment is being built (no invocation of start(), but startComplex() as
     * the initial call, for example), this method <em>must not</em> be called.
     * It must only be paired with a start() (zero argument or four argument) method.
     */
    void end();
    
    /** Discard any state, reinitialize the tree generation tool, and get ready
     * start over.
     * 
     * If a partial tree has been created, it will be discarded.
     * 
     * Not all tree generation tools support resets; those that do not will
     * cause an exception to be thrown.
     */
    void reset();
}
