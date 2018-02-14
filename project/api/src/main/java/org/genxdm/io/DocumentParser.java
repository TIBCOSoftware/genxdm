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
     * This method typically delegates to parse(InputSource, systemId).
     * 
     * @param byteStream the input; may not be null
     * @param systemId the URI of the document, if available; may be null
     * @return a document node resulting from the parse
     */
    N parse(final InputStream byteStream, final String systemId) throws IOException, XdmMarshalException;
    
    /** Parse a reader (characters) as a document node.
     * 
     * This method typically delegates to parse(InputSource, systemId).
     * 
     * @param characterStream the input; may not be null
     * @param systemId the URI of the document, if available; may be null
     * @return a document node resulting from the parse
     */
    N parse(final Reader characterStream, final String systemId) throws IOException, XdmMarshalException;
    
    /** Parse a SAX InputSource to a document node.
     * 
     * The contract of this method is that the SAX InputSource will be read
     * completely, feeding a document builder associated with the corresponding
     * bridge.
     * 
     * @param source the input; may not be null
     * @param systemId the URI of the document, if available; may be null
     * @return a document node resulting from the parse.
     * 
     */
    N parse(final InputSource source, final String systemId) throws IOException, XdmMarshalException;

}
