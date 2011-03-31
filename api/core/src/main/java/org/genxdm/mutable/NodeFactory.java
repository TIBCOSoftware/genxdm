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
package org.genxdm.mutable;

import java.net.URI;

public interface NodeFactory<N>
{
    /**
     * Creates a new attribute.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name; may not be null.
     * @param localName
     *            The local-name part of the attribute name; may not be null.
     * @param prefix
     *            The prefix part of the attribute name; may not be null.
     * @param value
     *            The value of the attribute as a {@link String} i.e. xs:untypedAtomic; may be null or empty.
     * @return The created attribute; never null.
     */
    N createAttribute(final String namespaceURI, final String localName, final String prefix, final String value);

    /**
     * Creates a new comment node.
     * 
     * @param data
     *            The string-value of the comment node; may be null.
     * @return The created comment node, never null.
     */
    N createComment(final String data);

    /**
     * Creates a new document node.

     * @param uri The URI corresponding to the 'system' identifier; may be null.
     * @param docTypeDecl A string containing the internal subset, if any; may be null.
     * @return a new document appropriate for this bridge; never null.
     */
    N createDocument(final URI uri, final String docTypeDecl);

    /**
     * Creates a new element node.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the name of the element node; may not be null.
     * @param localName
     *            The local-name part of the name of the element node; may not be null.
     * @param prefix
     *            The prefix part of the name of the element node; may not be null.
     * @return The created element node, never null.
     */
    N createElement(final String namespaceURI, final String localName, final String prefix);

    /**
     * Creates a new processing-instruction node.
     * 
     * @param target
     *            The target of the processing-instruction (dm:local-name); may not be null.
     * @param data
     *            The data of the processing-instruction (dm:string-value); may be null.
     * @return The created processing-instruction, never null.
     */
    N createProcessingInstruction(final String target, final String data);

    /**
     * Creates a new text node.
     * 
     * @param value
     *            The value of the text node as a {@link String} i.e. xs:untypedAtomic; may be null.
     * @return The created text node, never null.
     */
    N createText(final String value);
}
