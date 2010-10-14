/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.base.io;

import java.io.Closeable;
import java.io.Flushable;
import java.net.URI;

import org.genxdm.DtdAttributeKind;
import org.genxdm.exceptions.GxmlException;

/**
 * Receive notification of the logical content of an XML tree. In this context, logical content applies principally to
 * the information available in the data model.
 * 
 * <p/>
 * This interface is the main interface that a streaming XML application implements if it needs to be informed of the
 * data model content of a document.
 * <p>
 */
public interface ContentHandler
    extends Closeable, Flushable
{
    /**
     * Receive notification of an attribute with a dm:typed-value.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name. Cannot be <code>null</code>.
     * @param localName
     *            The local-name part of the attribute name. Cannot be <code>null</code>.
     * @param value
     *            The dm:string-value property of the attribute.
     * @param type
     *            The type of the attribute; if null, then type is "NONE", not "UNKNOWN".
     */
    void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type) throws GxmlException;

    /**
     * Receive notification of a comment information item.
     * 
     * @param value
     *            The content of the comment. Cannot be <code>null</code>.
     */
    void comment(String value) throws GxmlException;

    /**
     * Receive notification of the end of a document.
     */
    void endDocument() throws GxmlException;

    /**
     * Receive notification of the end of an element.
     */
    void endElement() throws GxmlException;

    /**
     * Receive notification of an namespace in the style of a lexical attribute. <br/>
     * Note that the timing of the namespace event is immediately after a start element.
     * 
     * @param prefix
     *            The name of the namespace node. Cannot be <code>null</code>.
     * @param namespaceURI
     *            The string value of the namespace node. Cannot be <code>null</code>.
     */
    void namespace(String prefix, String namespaceURI) throws GxmlException;

    /**
     * Receive notification of a processing instruction.
     * 
     * @param target
     *            The processing instruction target. Cannot be <code>null</code> .
     * @param data
     *            The processing instruction data, or null if none was supplied. The data does not include any
     *            whitespace separating it from the target.
     */
    void processingInstruction(String target, String data) throws GxmlException;

    /**
     * Receive notification of the beginning of a document.
     * <p>
     * <p/>
     * A callee will invoke this method only once, before any other methods in this interface.
     * 
     * @param documentURI
     *            The dm:document-uri.
     */
    void startDocument(URI documentURI, String docTypeDecl) throws GxmlException;

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
    void startElement(String namespaceURI, String localName, String prefix) throws GxmlException;

    /**
     * Receive notification of character data.
     * <p>
     * 
     * @param data
     *            The data associated with the text node.
     */
    void text(String data) throws GxmlException;
}
