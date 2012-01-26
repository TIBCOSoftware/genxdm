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

import java.io.Closeable;
import java.io.Flushable;
import java.net.URI;

import org.genxdm.exceptions.GenXDMException;

/**
 * Receive notification of the logical content of the data model of an XML tree.
 * 
 * <p>
 * This interface is the main interface that a streaming XML application implements if it needs to be informed of the
 * data model content of a document, or a portion thereof.
 * </p>
 * <p>Although this interface looks similar to the SAX streaming interface, it differs in important
 * contractual ways. This interface supports the notion of building a sequence of nodes, rather than
 * just a "document", or a "fragment" of a document represented by a single element. As such
 * an implementation of the interface has different expectations than does the expected streaming
 * semantics implied by the "SAX" interfaces.
 * </p>
 * 
 * <p>Implementations may assume the following constraints are satisfied by the caller, unless
 * the implementation specifically chooses to relax a given constraint. For example, a derived
 * interface, or implementation may choose to allow automatic namespace fixups (see {@link FragmentBuilder}).
 * Constraints include:
 * </p>
 * 
 * <ul>
 *   <li>{@link #startDocument} and {@link #endDocument} will come in matched pairs, with
 *   startDocument coming first.</li>
 *   <li>{@link #startDocument} may only be called before any other method has been called, or after a preceding
 *   {@link #endDocument} call.</li>
 *   <li>All passed URI values, even though indicated as "String", are valid URIs.</li>
 *   <li>All local names and prefixes are valid local names (that is, they match the XML namespaces specification
 *   for <a href="http://www.w3.org/TR/xml-names/#NT-NCName">NCName</a>).</li>
 *   <li>All values passed to the {@link #attribute} {@link #text}, and {@link #comment} methods are valid
 *   XML text, and do not include illegal character values of 0, 1, 2, etc.). Note that this text
 *   can include sequences that themselves look like XML -
 *   for example, strings like "&lt;element/&gt;" or "&lt;![CDATA[". An implementation of this interface must therefore
 *   be careful to escape content when actually serialized to an XML file.</li>
 *   <li>{@link #startElement} and {@link #endElement} will come in matched pairs,
 *   with startElement coming first.</li>
 *   <li>The {@link #attribute} and {@link #namespace} methods may not be called between a startDocument and
 *   endDocument call unless they happen after a call to startElement that has not yet been matched by endElement.</li>
 *   <li>Every used namespace/prefix pair will be declared via the {@link #namespace} method. When this interface
 *   is used to create a sequence of nodes, each entry in the sequence must properly declare its namespace/prefix
 *   pairs.</li> 
 *   <li>When the {@link #attribute} method is called with DtdAttributeKind of type "ID", that the values given
 *   for IDs are at least unique within the given scope.</li>
 *   </li>The {@link #attribute} and {@link #namespace} methods may be called outside of all start/endDocument pairs,
 *   or immediately after a {@link #startElement} call, with no intervening calls to {@link #processingInstruction}, {@link #comment},
 *   {@link #text}.</li>
 *   <li>{@link #text} will never be called twice in a row, without some other intervening call.</li>
 * 
 * </ul>
 * 
 * <p>Some additional notes:</p>
 * 
 * <ul>
 *  <li>In the XDM, a document node <a href="http://www.w3.org/TR/xpath-datamodel/#constraints-document">can have</a>
 *  multiple child elements, and also contain text nodes.</li>
 *  <li>The {@link FragmentBuilder} interface specifically relaxes the constraint around declaring namespaces, so
 *  that it can satisfy the need to create a well formed XDM.</li>
 * </ul>
 * 
 * 
 * @see FragmentBuilder
 */
public interface ContentHandler
    extends Closeable, Flushable
{
    /**
     * Receive notification of an attribute with a dm:string-value.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name. Cannot be <code>null</code>.
     * @param localName
     *            The local-name part of the attribute name. Cannot be <code>null</code>.
     * @param prefix The prefix hint; may not be null but may be the empty string.
     * @param value
     *            The dm:string-value property of the attribute.
     * @param type
     *            The type of the attribute; if null, then type is "NONE", not "UNKNOWN".
     */
    void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type) throws GenXDMException;

    /**
     * Receive notification of a comment information item.
     * 
     * @param value
     *            The content of the comment. Cannot be <code>null</code>.
     */
    void comment(String value) throws GenXDMException;

    /**
     * Receive notification of the end of a document.
     */
    void endDocument() throws GenXDMException;

    /**
     * Receive notification of the end of an element.
     */
    void endElement() throws GenXDMException;

    /**
     * Receive notification of an namespace in the style of a lexical attribute. <br/>
     * Note that the timing of the namespace event is immediately after a start element.
     * 
     * An attempt to bind a reserved namespace prefix (xml, xmlns) incorrectly
     * <em>must</em> throw a GenXDMException.
     * 
     * @param prefix
     *            The name of the namespace node. Cannot be <code>null</code>.
     * @param namespaceURI
     *            The string value of the namespace node. Cannot be <code>null</code>.
     */
    void namespace(String prefix, String namespaceURI) throws GenXDMException;

    /**
     * Receive notification of a processing instruction.
     * 
     * @param target
     *            The processing instruction target. Cannot be <code>null</code> .
     * @param data
     *            The processing instruction data, or null if none was supplied. The data does not include any
     *            whitespace separating it from the target.
     */
    void processingInstruction(String target, String data) throws GenXDMException;

    /**
     * Receive notification of the beginning of a document.
     * <p>
     * <p/>
     * A callee will invoke this method only once, before any other methods in this interface.
     * 
     * @param documentURI
     *            The dm:document-uri.
     */
    void startDocument(URI documentURI, String docTypeDecl) throws GenXDMException;

    /**
     * Receive notification of the beginning of an element.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the element name. Cannot be <code>null</code>.
     * @param localName
     *            The local-name part of the element name. Cannot be <code>null</code>.
     * @param prefix
     *            The prefix-hint part of the element name.
     */
    void startElement(String namespaceURI, String localName, String prefix) throws GenXDMException;

    /**
     * Receive notification of character data.
     * 
     * This method may be called repeatedly, but implementations must insure
     * that two adjacent text nodes are never created.  That is, implementations
     * must accumulate the data from multiple adjacent calls of this method, and
     * create a single text node.
     * 
     * @param data
     *            The data associated with the text node.
     */
    void text(String data) throws GenXDMException;
}
