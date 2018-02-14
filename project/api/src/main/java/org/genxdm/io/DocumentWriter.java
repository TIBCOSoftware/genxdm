package org.genxdm.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.genxdm.exceptions.XdmMarshalException;

public interface DocumentWriter<N>
{
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
   * @param config the serialization parameters for this output
   */
   void write(final OutputStream byteStream, final N source, final SerializationParams config) throws IOException, XdmMarshalException;

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
   
   /** Write XML, as characters, to a Writer, unformatted.
    * 
    * The output makes no attempt to format or pretty-print the output, but guarantees
    * well-formedness (for core XML and for namespaces), and tries to be compact (that
    * is, no newlines or spaces will be introduced).
    * 
    * @param characterStream the target Writer; may not be null
    * @param source the starting node from which to traverse (usually a document node); may not be null
    * @param config the serialization parameters for this output
    */
   void write(final Writer characterStream, final N source, final SerializationParams config) throws IOException, XdmMarshalException;

}
