package org.genxdm.processor.w3c.xs;

import javax.xml.namespace.QName;

public interface LoadOptions
{
    static final String W3C_XS_PROCESSOR_NS = "http://xs.w3c.processor.genxdm.org/options";
    
    static final String PROCESS_REPEATED_NAMESPACES = "processRepeatedNamespaces";
    
    static final String LAST_IN_WINS = "lastInWins";
    
    static final QName OPTION_REPEAT_NS = new QName(W3C_XS_PROCESSOR_NS, PROCESS_REPEATED_NAMESPACES);
    
    static final QName OPTION_LAST_IN_WINS = new QName(W3C_XS_PROCESSOR_NS, LAST_IN_WINS);
    
    // in this parser, boolean options that have the string value "true" or "yes"
    // are set true; boolean options with *any other value* are considered false. 
    static final String TRUE = "true";
    static final String YES = "yes";
}
