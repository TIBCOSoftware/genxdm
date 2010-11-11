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

import javax.xml.namespace.QName;

public interface Reader 
{
    /**
     * Returns the set of attribute names for the current node.
     * 
     * @param orderCanonical
     *            Determines whether the names will be returned in canonical order (lexicographically by namespace URI,
     *            local name).
     */
    Iterable<QName> getAttributeNames(boolean orderCanonical);

    /**
     * Returns the dm:typed-value of the attribute with the specified dm:name.
     * 
     * Returns <code>null</code> if the attribute does not exist.
     * 
     * @param namespaceURI
     *            The dm:namespace-uri of the attribute. Cannot be <code>null</code>.
     * @param localName
     *            The dm:local-name of the attribute. Cannot be <code>null</code>.
     */
    String getAttributeStringValue(String namespaceURI, String localName);

    /**
     * Returns the line number, or <code>-1</code> if none is available.
     */
    int getLineNumber();

    /**
     * Returns the local-name property of the dm:node-name.
     * 
     * <br/>
     * 
     * TEXT, COMMENT, and DOCUMENT nodes return <code>null</code>; they have no name.
     * 
     * Other node types should never return <code>null</code>.
     */
    String getLocalName();

    /** Only reports on namespace declarations for the current position,
     * not namespaces in scope.
     * 
     * @param prefix the prefix (namespace name) for which the URI is desired.
     * @return the namespace URI declared for this prefix, or null if no such prefix
     * mapping is declared in this event scope.
     */
    String getNamespaceForPrefix(String prefix);

    /**
     * Returns the set of namespace names (prefixes) for the current node.
     * 
     * Only considers prefix mappings which are explicit and local to the node.
     * 
     * @param orderCanonical
     *            Determines whether the names will be returned in canonical order (lexicographically by local name).
     */
    Iterable<String> getNamespaceNames(boolean orderCanonical);

    /**
     * Returns the namespace-uri part of the dm:node-name.
     * 
     * <br/>
     * 
     * DOCUMENT, COMMENT, and TEXT nodes return <code>null</code>; they have no name.
     * 
     * Other node types should never return <code>null</code>.
     */
    String getNamespaceURI();

    /**
     * Returns the prefix part of the dm:node-name.
     * 
     * <br/>
     * 
     * DOCUMENT, COMMENT, and TEXT nodes return <code>null</code>; they have no name.
     * 
     * Other node types should never return <code>null</code>.
     * This is just a hint because it usually contains the prefix of the original document. The prefix will not be
     * updated to reflect in scope namespaces.
     */
    String getPrefix();

    /**
     * @return the value of the parse event as a string.
     */
    String getStringValue();

    /**
     * @return <code>true</code> if the node has attributes, otherwise <code>false</code>.
     */
    boolean hasAttributes();

    /**
     * @return <code>true</code> if the node has prefix-to-namespace mappings, otherwise <code>false</code>.
     */
    boolean hasNamespaces();

}
