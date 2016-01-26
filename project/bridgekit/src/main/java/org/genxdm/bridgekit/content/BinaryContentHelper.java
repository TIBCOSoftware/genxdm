package org.genxdm.bridgekit.content;

import java.util.Map;

public interface BinaryContentHelper
    extends ContentHelper
{
    void binaryElement(String ns, String name, byte [] data);
    
    void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data);
    
    // we could add:
    // SequenceHandler getSequenceHandler();
    // ComponentProvider getComponentProvider();
    // AtomBridge<A> getAtomBridge();
    // I *really* don't want to hand someone the sequence handler, though.
    // that's kind of asking for problems; they can feed it garbage and there's
    // no recovery from that.
}
