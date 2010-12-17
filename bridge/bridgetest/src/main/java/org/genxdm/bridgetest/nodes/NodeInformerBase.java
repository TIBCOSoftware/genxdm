package org.genxdm.bridgetest.nodes;

import java.net.URI;

import javax.xml.namespace.QName;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.names.NamespaceBinding;

public abstract class NodeInformerBase<N>
    extends TestBase<N>
{

    @Test
    public void nodeKinds()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // we're going to assume that navigation works; that's tested
        // in nodenavigator.
        assertTrue(model.getNodeKind(doc) == NodeKind.DOCUMENT);
        
        N docElement = model.getFirstChildElement(doc);
        assertTrue(model.getNodeKind(docElement) == NodeKind.ELEMENT);
        assertTrue(model.isElement(docElement));
        
        // is*, getNodeKind()
    }
    
    public void nodeIdentity()
    {
    }
    
    public void idsAndRefs()
    {
    }
    
    public void attributes()
    {
    }
    
    public void namespaces()
    {
    }
    
    public void relationships()
    {
        // has this, has that
    }
    
    public void names()
    {
    }
    
    public void values()
    {
    }
    
    public void uris()
    {
    }
    
    public void matching()
    {
    }
    
//    /**
//     * Returns the set of attribute names for the node.
//     * 
//     * <p>
//     * This method does not inherit attribute names in the reserved XML namespace.
//     * </p>
//     * 
//     * @param node
//     *            The node for which the attribute names are required.
//     * @param orderCanonical
//     *            Determines whether the names will be returned in canonical order (lexicographically by namespace
//     *            URI,local name).
//     */
//    Iterable<QName> getAttributeNames(N node, boolean orderCanonical);
//    
//    /**
//     * Returns the dm:string-value of the attribute node with the specified expanded-QName.
//     * <p>
//     * This is equivalent to retrieving the attribute node and then its string value.
//     * </p>
//     * 
//     * @param parent
//     *            The node that is the parent of the attribute node.
//     * @param namespaceURI
//     *            The namespace-uri part of the attribute name.
//     * @param localName
//     *            The local-name part of the attribute name.
//     */
//    String getAttributeStringValue(N parent, String namespaceURI, String localName);
//
//    /**
//     * Returns the base URI of the supplied context node, per the XML:Base
//     * specification.
//     * <br />Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-base-uri">
//     * dm:base-uri</a> accessor in the XDM.  Defined
//     * for all node types except namespace.
//     * 
//     * @return the absolute value of the base-uri property, if it is available,
//     * or null if it is not.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-base-uri
//     */
//    URI getBaseURI(N node);
//
//    /**
//     * Returns the absolute URI of the resource from which the Document Node was
//     * constructed. <br/>
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-document-uri">
//     * dm:document-uri</a> accessor in the XDM.
//     * 
//     * @return the absolute URI of the resource from which the Document Node was
//     *         constructed, if the absolute URI is available; f there is no URI
//     *         available, or if it cannot be made absolute when the Document
//     *         Node is constructed, or if it is used on a node other than a
//     *         Document Node, returns null
//     *         
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-document-uri
//     */
//    URI getDocumentURI(N node);
//
//    /**
//     * Returns the local-name property of the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name">
//     * dm:node-name</a>.
//     * 
//     * <br/>
//     * 
//     * <p>
//     * TEXT, COMMENT, and DOCUMENT nodes return <code>null</code>; they have no name.
//     * </p>
//     * 
//     * <p>Other node types should never return <code>null</code>.  Note that in the
//     * case of namespace nodes, the <code>dm:node-name</code> accessor indicates that
//     * it returns an empty sequence in the case of an "empty" prefix (as in
//     * <code>xmlns=...</code>).  This API, however, dictates that an empty string
//     * will be returned in that particular case.</p>
//     *  
//     * @param node
//     *            The node for which the node local-name is required.
//     *            
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name
//     */
//    String getLocalName(N node);
//
//    /**
//     * Returns the namespace bindings associated with the node as a set or prefix/URI pairs.
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-bindings">
//     * dm:namespace-bindings</a> accessor.
//     * 
//     * Only includes prefix mappings which are explicit and local to the node.
//     * 
//     * @param node
//     *            The node under consideration.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-namespace-bindings
//     */
//    Iterable<NamespaceBinding> getNamespaceBindings(N node);
//
//    /** Only reports on namespace declarations for the target node,
//     * not namespaces in scope for that node.
//     * 
//     * @param node the target node on which the namespace is declared.
//     * @param prefix the prefix (namespace name) for which the URI is desired.
//     * 
//     * @return the namespace URI declared for this prefix, or null if no such prefix
//     * mapping is declared on this node.
//     */
//    String getNamespaceForPrefix(N node, String prefix);
//
//    /**
//     * Returns the set of namespace names (prefixes) for a given node.
//     * 
//     * <br/>
//     * 
//     * This refers to the prefix mappings which are explicit and local to the node.
//     * 
//     * @param orderCanonical
//     *            Determines whether the names will be returned in canonical order (lexicographically by local name).
//     */
//    Iterable<String> getNamespaceNames(N node, boolean orderCanonical);
//
//    /**
//     * Returns the namespace-uri part of the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name">
//     * dm:node-name</a>.
//     * 
//     * <br/>
//     * 
//     * DOCUMENT, COMMENT, and TEXT nodes return <code>null</code>; they have no name.
//     * 
//     * Other node types should never return <code>null</code>.
//     * @param node
//     *            The node for which the node namespace-uri is required.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-name
//     */
//    String getNamespaceURI(N node);
//
//    /**
//     * Return an object which obeys the contract for equals() and hashCode();
//     * it may also identify the node via object identity (==), but this is not
//     * guaranteed.  Implementations often return the node itself, but some bridges
//     * are not able to do so.  The object returned from this method is guaranteed
//     * to obey the equals()/hashCode() contract even when the node the object identifies
//     * does not.
//     * 
//     * @param node the node for which an ID object is required.
//     */
//    Object getNodeId(N node);
//
//    /**
//     * Returns the node-kind of the node as an enumeration in {@link NodeKind}.
//     * 
//     * Applies to all node kinds and never returns <code>null</code>. <br/>
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-kind">
//     * dm:node-kind</a> accessor in the XDM.
//     * 
//     * @param node
//     *            The node for which the node-kind is required.
//     *            
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-node-kind
//     */
//    NodeKind getNodeKind(N node);
//
//    /**
//     * Returns the prefix part of the dm:node-name.
//     * 
//     * <br/>
//     * 
//     * DOCUMENT, COMMENT, and TEXT nodes return <code>null</code>; they have no name.
//     * 
//     * Other node types should never return <code>null</code>.
//     * This is just a hint because it usually contains the prefix of the original document. The prefix will not be
//     * updated to reflect in scope namespaces.
//     * @param node
//     *            The node for which the node prefix hint is required.
//     */
//    String getPrefix(N node);
//
//    /**
//     * Returns the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-string-value">
//     * dm:string-value</a> property of the node. Applies to all node kinds.
//     * 
//     * @param node
//     *            The node for which the dm:string-value is required.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-string-value
//     */
//    String getStringValue(N node);
//
//    /**
//     * Determines whether there are nodes on the attribute axis for this node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean hasAttributes(N node);
//
//    /**
//     * Determines whether there are nodes on the child axis for this node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean hasChildren(N node);
//
//    /**
//     * Determines whether there are prefix-to-namespace mappings for this node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean hasNamespaces(N node);
//
//    /**
//     * Determines whether the node has a following sibling.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean hasNextSibling(N node);
//
//    /**
//     * Determines whether there are nodes on the parent axis for this node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean hasParent(N node);
//
//    /**
//     * Determines whether the node has a preceding sibling.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean hasPreviousSibling(N node);
//    
//    /**
//     * Determines whether the specified node is an attribute node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean isAttribute(N node);
//
//    /**
//     * Determines whether the specified node is an element node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean isElement(N node);
//
//    /**
//     * <p>Determine whether the node is an ID node. Corresponds to the
//     * <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-id">
//     * dm:is-id</a> accessor.  Valid for element and attribute nodes.
//     * </p>
//     * 
//     * @param node the node under consideration
//     * @return true if the node is an attribute named xml:id, if it has a PSVI
//     * type derived from xs:ID, or if it is an attribute with a DTD-defined type of ID,
//     * otherwise false.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-id
//     */
//    boolean isId(N node);
//    
//    /**
//     * Determine whether the node contains one or more IDREFs.
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-idrefs">
//     * dm:is-idrefs</a> accessor.  Valid for element and attribute nodes.
//     * 
//     * @param node the node under consideration
//     * @return true if the node is an element or attribute with at least one atomic value
//     * derived from xs:IDREF or xs:IDREFS, or if it is an attribute with a DTD-defined
//     * type of IDREF or IDREFS.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-is-idrefs
//     */
//    boolean isIdRefs(N node);
//
//    /**
//     * Determines whether the specified node is a namespace node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean isNamespace(N node);
//
//    /**
//     * Determines whether the specified node is a text node.
//     * 
//     * @param node
//     *            The node under consideration.
//     */
//    boolean isText(N node);
//
//    /**
//     * Deterimines whether the specified node matches the arguments.
//     * 
//     * @param node
//     *            The XML node.
//     * @param nodeKind
//     *            The node kind to match.
//     * @param namespaceURI
//     *            The namespace-uri to match.
//     * @param localName
//     *            The local-name to match.
//     */
//    boolean matches(N node, NodeKind nodeKind, String namespaceURI, String localName);
//
//    /**
//     * Determines whether the specified node matches in name.
//     * 
//     * @param node
//     *            The node being tested.
//     * @param namespaceURI
//     *            The namespace-uri part of the name.
//     * @param localName
//     *            The local-name part of the name.
//     * @return <code>true</code> if the node matches the arguments specified, otherwise <code>false</code>.
//     */
//    boolean matches(N node, String namespaceURI, String localName);
}
