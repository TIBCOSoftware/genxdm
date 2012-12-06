package org.genxdm.xs.generator;

import javax.xml.namespace.QName;

import org.genxdm.xs.enums.ProcessContentsMode;

public interface GroupBuilder
{
    // content model is all | choice | sequence, but without occurrence constraints.
    // the SchemaBuilder selects the outermost container.
    // reference only
    void group(QName ref, int minOccurs, int maxOccurs);
    // for element, we're not going to support local definitions, except by reference to a type.
    void element(QName ref, int minOccurs, int maxOccurs);
    
    void element(String name, QName type, int minOccurs, int maxOccurs);

    // wildcard may appear in choice and sequence
    void any(ProcessContentsMode processContents, Iterable<String> namespaces, int minOccurs, int maxOccurs);

    void startAll(boolean optional);
    // only contains element*
    void endAll();
    
    void startChoice(int minOccurs, int maxOccurs);
    // element | group | choice | sequence | any
    void endChoice();

    void startSequence(int minOccurs, int maxOccurs);
    // element | group | choice | sequence | any
    void endSequence();
}
