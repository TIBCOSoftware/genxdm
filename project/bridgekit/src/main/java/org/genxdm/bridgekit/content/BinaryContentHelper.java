package org.genxdm.bridgekit.content;

import java.util.Map;

public interface BinaryContentHelper
    extends ContentHelper
{
    void binaryElement(String ns, String name, byte [] data);
    
    void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data);
    
    // we could add:
    // ComponentProvider getComponentProvider();
    // AtomBridge<A> getAtomBridge();
    // these would be useful, except that we'd have to add the <A> parameter
    // for the atom bridge. the point would be to avoid having to pass multiple
    // abstractions.
    // and on the other hand ... it implies things that i'm not sure i'm comfortable with
    // I *really* don't want to hand someone the sequence handler, though.
    // that's kind of asking for problems; they can feed it garbage and there's
    // no recovery from that.
}
