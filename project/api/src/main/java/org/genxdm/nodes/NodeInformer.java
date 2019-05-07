/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.names.NamespaceBinding;

/**
 * A stateless abstraction for investigating the properties of a &lt;N>ode, which
 * is supplied as the argument to each method. 
 */
public interface NodeInformer<N>
{
    /**
     * Returns the set of attribute names for the node.
     * 
     * <p>This method does not inherit attribute names in the reserved XML namespace.</p>
     * 
     * @param node
     *            The node for which the attribute names are required. May not be null.
     * @param orderCanonical
     *            Determines whether the names will be returned in canonical order (lexicographically by namespace
     *            URI,local name).
     * @return an iterable of attribute names as QNames, if called on an element
     * node (may be empty), or null if called on a non-element node.
     */
    Iterable<QName> getAttributeNames(N node, boolean orderCanonical);
    
    /**
     * Returns the dm:string-value of the attribute node with the specified expanded-QName.
     * 
     * <p>This is equivalent to retrieving the attribute node and then its string value.</p>
     * 
     * @param parent
     *            The node that is the parent of the attribute node. May not be null.
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     * @return the string value of the designated attribute, if it exists;
     * null if it does not. Note that null values for namespace or local name match nothing,
     * so return null.
     */
    String getAttributeStringValue(N parent, String namespaceURI, String localName);

    /**
     * Returns the base URI of the supplied context node, per the XML:Base
     * specification.
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-base-uri">
     * dm:base-uri</a> accessor in the XDM.  Defined
     * for all node types except namespace.</p>
     * 
     * @param node The node for which the base uri is required; may not be null.
     * @return the absolute value of the base-uri property, if it is available,
     * or null if it is not.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-base-uri">XDM dm:base-uri accessor</a>
     */
    URI getBaseURI(N node);

    /**
     * Returns the absolute URI of the resource from which the Document Node was
     * constructed.
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-document-uri">
     * dm:document-uri</a> accessor in the XDM.</p>
     * 
     * @param node The node for which the document uri is requested; may not be null.
     * @return the absolute URI of the resource from which the Document Node was
     *         constructed, if the absolute URI is available; f there is no URI
     *         available, or if it cannot be made absolute when the Document
     *         Node is constructed, or if it is used on a node other than a
     *         Document Node, returns null
     *         
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-document-uri">XDM dm:document-uri accessor</a>
     */
    URI getDocumentURI(N node);
    
    /**
     * Returns an IndexPair, identifying the parent index and own index of
     * every element and document node in the tree. See IndexPair for additional
     * information.
     * If ProcessingContext.isSupported(Feature.IN_TREE_INDEX) is true, this
     * must never return null; if false, it must always return null. This provides
     * support for highly efficient sorting of nodes within a document. Generally
     * of more interest to processors that have to operate in-order than to
     * general node processing.
     *
     * @param node The node for which the index is requested; may not be null.
     * @return an IndexPair representing the index of the supplied node's parent document or
     *         element (for all node kinds) and the index of the supplied node
     *         (only for document and element node kinds; all others have -1).
     *         Return value is strictly conditioned by feature support: either
     *         always null or never null.
     */
    NodeIndex getIndex(N node);

    /**
     * Returns the local-name property of the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name">
     * dm:node-name</a>.
     * 
     * <p>TEXT, COMMENT, and DOCUMENT nodes return <code>null</code>; they have no name.</p>
     * 
     * <p>Other node types should never return <code>null</code>.  Note that in the
     * case of namespace nodes, the <code>dm:node-name</code> accessor indicates that
     * it returns an empty sequence in the case of an "empty" prefix (as in
     * <code>xmlns=...</code>).  This API, however, dictates that an empty string
     * will be returned in that particular case.</p>
     *  
     * @param node
     *            The node for which the node local-name is required. May not be null.
     * 
     * @return the name of the node.
     *            
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name">XDM dm:node-name accessor</a>
     */
    String getLocalName(N node);

    /**
     * Returns the namespace bindings associated with the node as a set or prefix/URI pairs.
     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-bindings">
     * dm:namespace-bindings</a> accessor.
     * 
     * <p>Only includes prefix mappings which are explicit and local to the node.</p>
     * 
     * @param node
     *            The node under consideration. May not be null.
     * 
     * @return an iterable of {@link NamespaceBinding}s for namespaces declared
     * in this element node (which may be empty); null for non-element nodes
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-bindings">XDM dm:namespace-bindings accessor</a>
     */
    Iterable<NamespaceBinding> getNamespaceBindings(N node);

    /** Only reports on namespace declarations for the target node,
     * not namespaces in scope for that node.
     * 
     * @param node the target node on which the namespace is declared. May not be null.
     * @param prefix the prefix (namespace name) for which the URI is desired.
     * 
     * @return the namespace URI declared for this prefix, or null if no such prefix
     * mapping is declared on this node.
     */
    String getNamespaceForPrefix(N node, String prefix);

    /**
     * Returns the set of namespace names (prefixes) for a given node.
     * 
     * <p>This refers to the prefix mappings which are explicit and local to the node.</p>
     * 
     * @param node The node for which the list of prefixes is desired. May not be null.
     * @param orderCanonical
     *            Determines whether the names will be returned in canonical order (lexicographically by local name).
     * @return an iterable of namespace prefixes declared on this element node (which
     * may be empty; if called for a non-element node, returns null.
     */
    Iterable<String> getNamespaceNames(N node, boolean orderCanonical);

    /**
     * Returns the namespace-uri part of the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name">
     * dm:node-name</a>.
     * 
     * <p>DOCUMENT, COMMENT, and TEXT nodes return <code>null</code>; they have no name.</p>
     * 
     * <p>Other node types should never return <code>null</code>.</p>
     * @param node
     *            The node for which the node namespace-uri is required. May not be null.
     * 
     * @return the namespace-uri associated with this node.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name">XDM dm:node-name accessor</a>
     */
    String getNamespaceURI(N node);

    /**
     * Return an object which obeys the contract for equals() and hashCode();
     * it may also identify the node via object identity (==), but this is not
     * guaranteed.  Implementations often return the node itself, but some bridges
     * are not able to do so.  The object returned from this method is guaranteed
     * to obey the equals()/hashCode() contract even when the node the object identifies
     * does not.
     * 
     * <p>Conforms to the contract specified in section 2.3 of the XDM specification
     * for node identity.  Nodes in an instance are equal to themselves and to no
     * other node; they are never equal across instances.</p>
     * 
     * @param node the node for which an ID object is required. May not be null.
     * 
     * @return a bridge-defined object which obeys the constraints specified.
     */
    Object getNodeId(N node);

    /**
     * Returns the node-kind of the node as an enumeration in {@link NodeKind}.
     * 
     * <p>Applies to all node kinds and never returns <code>null</code>. <br/>
     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-kind">
     * dm:node-kind</a> accessor in the XDM.</p>
     * 
     * @param node
     *            The node for which the node-kind is required. May not be null.
     *            
     * @return a {@link NodeKind}; if node is null, returns null.
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-kind">XDM dm:node-kind accessor</a>
     */
    NodeKind getNodeKind(N node);

    /**
     * Returns the prefix part of the dm:node-name.
     * 
     * <p>DOCUMENT, COMMENT, and TEXT nodes return <code>null</code>; they have no name.</p>
     * 
     * <p>Other node types should never return <code>null</code>.</p>
     * <p>This is just a hint because it usually contains the prefix of the original document. The prefix will not be
     * updated to reflect in scope namespaces.</p>
     * @param node
     *            The node for which the node prefix hint is required. May not be null.
     * @return the prefix associated with the namespace of this node, if there
     * is one.
     */
    String getPrefix(N node);

    /**
     * Returns the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-string-value">
     * dm:string-value</a> property of the node. Applies to all node kinds.
     * 
     * @param node
     *            The node for which the dm:string-value is required. May not be null.
     * 
     * @return the string value of this node.
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-string-value">XDM dm:string-value accessor</a>
     */
    String getStringValue(N node);

    /**
     * Determines whether there are nodes on the attribute axis for this node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is an element node and it does contain attributes,
     * false otherwise.
     */
    boolean hasAttributes(N node);

    /**
     * Determines whether there are nodes on the child axis for this node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if the node is a document or element node which contains
     * one or more child nodes; false otherwise.
     */
    boolean hasChildren(N node);

    /**
     * Determines whether there are prefix-to-namespace mappings for this node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is an element node that contains namespace declarations;
     * false otherwise.
     */
    boolean hasNamespaces(N node);

    /**
     * Determines whether the node has a following sibling.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is a child node which has a (parent and a) following
     * sibling; false otherwise.
     */
    boolean hasNextSibling(N node);

    /**
     * Determines whether there are nodes on the parent axis for this node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is a child node which has a parent; false otherwise.
     */
    boolean hasParent(N node);

    /**
     * Determines whether the node has a preceding sibling.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is a child node which has a (parent and a) previous
     * sibling; false otherwise.
     */
    boolean hasPreviousSibling(N node);
    
    /**
     * Determines whether the specified node is an attribute node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is an attribute node; false otherwise.
     */
    boolean isAttribute(N node);

    /**
     * Determines whether the specified node is an element node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is an element node; false otherwise.
     */
    boolean isElement(N node);

    /**
     * <p>Determine whether the node is an ID node. Corresponds to the
     * <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-id">
     * dm:is-id</a> accessor.  Valid for element and attribute nodes.
     * </p>
     * 
     * @param node the node under consideration. May not be null.
     * @return true if the node is an attribute named xml:id, if it has a PSVI
     * type derived from xs:ID, or if it is an attribute with a DTD-defined type of ID,
     * otherwise false.  Elements that contain attributes that return true also
     * return true.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-id">XDM dm:is-id accessor</a>
     */
    boolean isId(N node);
    
    /**
     * Determine whether the node contains one or more IDREFs.
     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-idrefs">
     * dm:is-idrefs</a> accessor.  Valid for element and attribute nodes.
     * 
     * @param node the node under consideration. May not be null.
     * @return true if the node is an element or attribute with at least one atomic value
     * derived from xs:IDREF or xs:IDREFS, or if it is an attribute with a DTD-defined
     * type of IDREF or IDREFS.
     * 
     * @see <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-idrefs">XDM dm:is-idrefs accessor</a>
     */
    boolean isIdRefs(N node);

    /**
     * Determines whether the specified node is a namespace node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is a namespace node; false otherwise.
     */
    boolean isNamespace(N node);

    /**
     * Determines whether the specified node is a text node.
     * 
     * @param node
     *            The node under consideration. May not be null.
     * @return true if this is a text node; otherwise false.
     */
    boolean isText(N node);

    /**
     * Determines whether the specified node matches the arguments.
     * 
     * @param node
     *            The node to test. May not be null.
     * @param nodeKind
     *            The node kind to match; if null, match regardless of node kind
     * @param namespaceURI
     *            The namespace-uri to match; if null, ignore namespace matching
     * @param localName
     *            The local-name to match; if null, ignore local name matching
     * 
     * @return true if the node matches the arguments specified.
     */
    boolean matches(N node, NodeKind nodeKind, String namespaceURI, String localName);

    /**
     * Determines whether the specified node matches in name.
     * 
     * <p>Equivalent to {link matches(N, NodeKind, String, String)} with a null
     * second argument.</p>
     * 
     * @param node
     *            The node being tested. May not be null.
     * @param namespaceURI
     *            The namespace-uri to match; if null, ignore namespace matching
     * @param localName
     *            The local-name to match; if null, ignore local name matching
     * 
     * @return true if the node matches the arguments specified, ignoring nulls
     */
    boolean matches(N node, String namespaceURI, String localName);
}
