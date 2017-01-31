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
 */
public interface ContentHelper
{

    void start(); // start just the document, no document element
    
    void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes);
    
    void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes);
    
    void startComplex(String ns, String name); // for use only when there are no new bindings or attributes, like simplified start()
    
    void simpleElement(String ns, String name, String value);
    
    // more complex simple-type element which may have namespaces, attributes, or both.
    void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value);
    
    Attrib newAttribute(String name, String value);
    
    Attrib newAttribute(String ns, String name, String value);
    
    void comment(String text);
    
    void pi(String target, String data);
    
    // call once and only once for each startComplex() (either variant)
    void endComplex();
    
    // once and only once for each start() (either variant; impl determines if doc element needs closed)
    void end();
    
    void reset();
}
