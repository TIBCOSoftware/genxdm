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

    void startAllParticle(boolean optional);
    // only contains element*
    
    void startChoiceParticle(int minOccurs, int maxOccurs);
    // element | group | choice | sequence | any

    void startSequenceParticle(int minOccurs, int maxOccurs);
    // element | group | choice | sequence | any

    /** Signal the end of content for a given particle.
     *
     * For every startAllParticle, startChoiceParticle, startSequenceParticle in this
     * interface, endParticle must be invoked to indicate the end
     * of content for that particle. Note that there is only a single method to be used.
     */
    void endParticle();
}
