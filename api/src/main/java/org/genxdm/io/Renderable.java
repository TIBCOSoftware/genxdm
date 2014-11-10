package org.genxdm.io;

import org.genxdm.exceptions.GenXDMException;

/** Designed as a tool for XML-based component model APIs.
 * 
 * By extending the Renderable interface in a component model that is parsed from
 * XML, the component model undertakes to represent itself as a stream of XML
 * events, driving a ContentHandler (which may generate a tree or write to a
 * file or a socket or run a converter or whatever).
 * 
 */
public interface Renderable
{
    void render(ContentHandler handler) 
        throws GenXDMException;
}
