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
package org.genxdm.nodes;

import java.net.URI;

import org.genxdm.NodeKind;
import org.genxdm.base.io.Reader;
import org.genxdm.names.NamespaceBinding;

public interface Informer
    extends Reader
{
    /**
     * Returns the base URI of the current node, per the XML:Base
     * specification.
     * <br />Corresponds to the dm:base-uri accessor in the XDM.  Defined
     * for all node types except namespace.
     * 
     * @return the absolute value of the base-uri property, if it is available,
     * or null if it is not.
     */
    URI getBaseURI();
    
    /**
     * Returns the absolute URI of the resource from which the Document Node was
     * constructed. <br/>
     * Corresponds to the dm:document-uri accessor in the XDM.
     * 
     * @return the absolute URI of the resource from which the Document Node was
     *         constructed, if the absolute URI is available; if there is no URI
     *         available, or if it cannot be made absolute when the Document
     *         Node is constructed, or if it is used on a node other than a
     *         Document Node, returns null
     */
    URI getDocumentURI();

    /**
     * Returns the namespace bindings associated with the node as a set or prefix/URI pairs.
     * 
     * Only includes prefix mappings which are explicit and local to the node.
     * 
     */
    Iterable<NamespaceBinding> getNamespaceBindings();
    
    /**
     * Return an object which obeys the contract for equals() and hashCode();
     * it may also identify the node via object identity (==), but this is not
     * guaranteed.  Implementations often return the node itself, but some bridges
     * are not able to do so.  The object returned from this method is guaranteed
     * to obey the equals()/hashCode() contract even when the node the object identifies
     * does not.
     */
    Object getNodeId();

    /**
     * Returns the dm:node-kind property of the XDM as an enumeration in {@link NodeKind}.
     */
    NodeKind getNodeKind();

    /**
     * @return <code>true</code> if the node has children, otherwise <code>false</code>.
     */
    boolean hasChildren();
    
    /**
     * @return <code>true</code> if the node has a following sibling, otherwise <code>false</code>.
     */
    boolean hasNextSibling();

    /**
     * @return <code>true</code> if the node has a parent, otherwise <code>false</code>.
     */
    boolean hasParent();

    /**
     * @return <code>true</code> if the node has a preceding sibling, otherwise <code>false</code>.
     */
    boolean hasPreviousSibling();

    /**
     * Determines whether the current node is an attribute node.
     * 
     */
    boolean isAttribute();

    /**
     * Determines whether the current node is an element node.
     * 
     */
    boolean isElement();
    
    /**
     * Determine whether the current node is an ID node.
     * Corresponds to the dm:is-id accessor.  Valid for element and attribute nodes.
     * 
     * @return true if the node is an attribute named xml:id, if it has a PSVI
     * type derived from xs:ID, or if it is an attribute with a DTD-defined type of ID,
     * otherwise false.
     */
    boolean isId();
    
    /**
     * Determine whether the current node contains one or more IDREFs.
     * Corresponds to the dm:is-idrefs accessor.  Valid for element and attribute nodes.
     * 
     * @return true if the node is an element or attribute with at least one atomic value
     * derived from xs:IDREF or xs:IDREFS, or if it is an attribute with a DTD-defined
     * type of IDREF or IDREFS.
     */
    boolean isIdRefs();

    /**
     * Determines whether the current node is a namespace node.
     * 
     */
    boolean isNamespace();
    
    /**
     * Determines whether the current node is a text node.
     * 
     */
    boolean isText();

    /**
     * Determines whether the current node matches the arguments.
     * 
     * @param nodeKind
     *            The node kind to match.
     * @param namespaceURI
     *            The namespace-uri to match.
     * @param localName
     *            The local-name to match.
     * @return <code>true</code> if the cursor matches the specified arguments.
     */
    boolean matches(NodeKind nodeKind, String namespaceURI, String localName);

    /**
     * Determines whether the current node matches in name.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the name.
     * @param localName
     *            The local-name part of the name.
     * @return <code>true</code> if the node matches the arguments specified, otherwise <code>false</code>.
     */
    boolean matches(String namespaceURI, String localName);
}
