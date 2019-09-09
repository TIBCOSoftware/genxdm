package org.genxdm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.genxdm.exceptions.XdmMarshalException;
import org.xml.sax.InputSource;

public interface DocumentParser<N>
{
    /** Parse an input stream (bytes) as a document node.
     * 
     * This method is typically implemented using the system encoding to
     * decode the bytes into characters.
     * 
     * @param byteStream the input; may not be null
     * @param systemId the URI of the document, if available; may be null
     * @return a document node resulting from the parse
     */
    N parse(final InputStream byteStream, final String systemId) throws IOException, XdmMarshalException;
    
    /** Parse a reader (characters) as a document node.
     * 
     * This method is typically implemented.
     * 
     * @param characterStream the input; may not be null
     * @param systemId the URI of the document, if available; may be null
     * @return a document node resulting from the parse
     */
    N parse(final Reader characterStream, final String systemId) throws IOException, XdmMarshalException;
    
    /** Parse a SAX InputSource to a document node.
     * 
     * This method exists mostly for compatibility and tradition; it is typically
     * implemented by using the non-null return from InputSource.getCharacterStream(),
     * and if that fails, trying InputSource.getByteStream(), and if that fails
     * but the handler has a Resolver, using the Resolver to find and parse the
     * document found in the (non-null) systemId argument.
     * 
     * @param source the input; may not be null
     * @param systemId the URI of the document, if available; may be null
     * @return a document node resulting from the parse.
     * 
     */
    N parse(final InputSource source, final String systemId) throws IOException, XdmMarshalException;

}
