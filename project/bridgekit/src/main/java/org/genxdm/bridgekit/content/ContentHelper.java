package org.genxdm.bridgekit.content;

import java.util.Map;

public interface ContentHelper
{

    void start();
    
    void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes);
    
    void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes);
    
    void simpleElement(String ns, String name, String value);
    
    void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value);
    
    Attrib newAttribute(String name, String value);
    
    Attrib newAttribute(String ns, String name, String value);
    
    void comment(String text);
    
    void pi(String target, String data);
    
    void endComplex();
    
    void end();
    
    void reset();
}
