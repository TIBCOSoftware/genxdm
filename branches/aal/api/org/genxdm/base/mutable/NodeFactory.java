/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.base.mutable;

import java.net.URI;

// Development note:
// This NodeFactory deliberately reflects the pattern of the ContentHandler
// interface.  Where ContentHandler has methods with N returns, we return
// the node.  That's the only difference.  The goal is consistency.
// In terms of implementation, a bridge could either have a node factory
// (which it used inside ContentHandler), or a content handler (used in
// NodeFactory, with getNode()).  The same implementation should work for
// both.
public interface NodeFactory<N>
{
    /**
     * Create an attribute with a value and a DTD type.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name. Cannot be <code>null</code> (but may be an empty string).
     * @param localName
     *            The local-name part of the attribute name. Cannot be <code>null</code> or the empty string.
     * @param value
     *            The dm:string-value property of the attribute. May be null or an empty string.
     * @param type
     *            The type of the attribute; if null, then type is "NONE", not "UNKNOWN".
     */
    N createAttribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type) throws GxmlException;

    /**
     * Create a comment information item.
     * 
     * @param value
     *            The content of the comment. Cannot be <code>null</code>, but may be an empty string.
     */
    N createComment(String value) throws GxmlException;

    /**
     * Create a document.
     * <p>
     * <p/>
     * A callee will invoke this method only once, before any other methods in this interface.
     * 
     * @param documentURI
     *            The dm:document-uri. May be null or the empty string.
     * @param docTypeDecl
     *            The document type declaration or internal subset, as a string. May be null; should not be an empty string.
     */
    N createDocument(URI documentURI, String docTypeDecl) throws GxmlException;

    /**
     * Create an element.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the element name. Cannot be <code>null</code> but may be an empty string.
     * @param localName
     *            The local-name part of the element name. Cannot be <code>null</code> or the empty string.
     * @param prefix
     *            The prefix-hint part of the element name. May not be null, but may be the empty string.
     */
    N createElement(String namespaceURI, String localName, String prefix) throws GxmlException;

    /**
     * Create a namespace in the style of a lexical attribute. <br/>
     * 
     * @param prefix
     *            The name of the namespace node. Cannot be <code>null</code> but may be the empty string.
     * @param namespaceURI
     *            The string value of the namespace node. Cannot be <code>null</code>, but may be the empty string.
     */
    N createNamespace(String prefix, String namespaceURI) throws GxmlException;

    /**
     * Create a processing instruction.
     * 
     * @param target
     *            The processing instruction target. Cannot be <code>null</code> or the empty string.
     * @param data
     *            The processing instruction data, or null if none was supplied. The data does not include any
     *            whitespace separating it from the target.
     */
    N createProcessingInstruction(String target, String data) throws GxmlException;

    /**
     * Create character data.
     * <p>
     * 
     * @param data
     *            The data associated with the text node. May be null or the empty string.
     */
    N createText(String data) throws GxmlException;
}
