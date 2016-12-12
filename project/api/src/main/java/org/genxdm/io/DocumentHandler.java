/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.XMLConstants;

import org.genxdm.exceptions.XdmMarshalException;
import org.xml.sax.InputSource;

/** Provides an interface for reading and writing XML.
 * 
 * The DocumentHandler interface provides a means to supply XML (as
 * readers or input streams) to be built into a target
 * tree model; it also permits tree models to "serialize" onto parallel output
 * abstractions (writers and output streams).
 * 
 * These are conveniences; it is not difficult to hook a parser to a
 * fragment builder or a writer to a content handler to model.stream() if
 * alternative behavior is desired.
 * 
 * DocumentHandler is <em>not</em> thread-safe, either for parsing or for
 * writing. Internal state is not protected by synchronization, and will be
 * affected by calls from another thread that begin before calls from the
 * original thread end. Do not pass the DocumentHandler to different threads
 * unless the threads themselves enforce synchronization.
 *
 * @param <N> the Node handle.
 */

public interface DocumentHandler<N>
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

    /** Write XML, as bytes in a specified character encoding, to an output stream, unformatted.
     *
     * The output makes no attempt to format or pretty-print the output, but guarantees
     * well-formedness (for core XML and for namespaces), and tries to be compact (that
     * is, no newlines or spaces will be introduced).
     * 
     * @param byteStream the target output stream; may not be null
     * @param source the starting node from which to traverse (usually a document node); may not be null
     * @param encoding the encoding in which to write characters as bytes; if null, or an unsupported encoding
     *   for the JVM, "UTF-8" will be used.
     */
    void write(final OutputStream byteStream, final N source, String encoding) throws IOException, XdmMarshalException;
    
    /* * Write XML, as bytes in a specified character encoding, to an output stream, unformatted.
    *
    * The output makes no attempt to format or pretty-print the output, but guarantees
    * well-formedness (for core XML and for namespaces), and tries to be compact (that
    * is, no newlines or spaces will be introduced).
    * 
    * @param byteStream the target output stream; may not be null
    * @param source the starting node from which to traverse (usually a document node); may not be null
    * @param encoding the encoding in which to write characters as bytes; if null, or an unsupported encoding
    *   for the JVM, "UTF-8" will be used.
    * @param config the serialization parameters for this output
    */
//    void write(final OutputStream byteStream, final N source, String encoding, final SerializationParams config) throws IOException, XdmMarshalException;

    /** Write XML, as characters, to a Writer, unformatted.
     * 
     * The output makes no attempt to format or pretty-print the output, but guarantees
     * well-formedness (for core XML and for namespaces), and tries to be compact (that
     * is, no newlines or spaces will be introduced).
     * 
     * @param characterStream the target Writer; may not be null
     * @param source the starting node from which to traverse (usually a document node); may not be null
     */
    void write(final Writer characterStream, final N source) throws IOException, XdmMarshalException;
    
    /* * Write XML, as characters, to a Writer, unformatted.
     * 
     * The output makes no attempt to format or pretty-print the output, but guarantees
     * well-formedness (for core XML and for namespaces), and tries to be compact (that
     * is, no newlines or spaces will be introduced).
     * 
     * @param characterStream the target Writer; may not be null
     * @param source the starting node from which to traverse (usually a document node); may not be null
     * @param config the serialization parameters for this output
     */
//    void write(final Writer characterStream, final N source, final SerializationParams config) throws IOException, XdmMarshalException;

    /**
     * A method that corresponds to the {@link XMLConstants#FEATURE_SECURE_PROCESSING} flag that can be
     * passed to various XML processors.
     * 
     * <p>Note that what "secure" means is parser implementation dependent.</p>
     * 
     * @return <code>true</code> if the processing will be done securely.
     */
    boolean isSecurelyProcessing();
}
