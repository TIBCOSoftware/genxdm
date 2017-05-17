package org.genxdm.typed.io;

import org.genxdm.exceptions.GenXDMException;

/** Designed as a tool for XML-based component model APIs.
 * 
 * By extending the TypedRenderable interface in a component model that is parsed from
 * XML, the component model undertakes to represent itself as a stream of XML
 * events, driving a SequenceHandler (which may generate a tree or write to a
 * file or a socket or run a converter or whatever).
 *
 * Although directly modeled on the untyped Renderable interface, the two
 * interfaces are not directly related by inheritance. This permits implementors
 * to force type-awareness for component models in which this is desired.
 * 
 */
public interface TypedRenderable<A>
{
    void renderTyped(SequenceHandler<A> handler) 
        throws GenXDMException;
}
