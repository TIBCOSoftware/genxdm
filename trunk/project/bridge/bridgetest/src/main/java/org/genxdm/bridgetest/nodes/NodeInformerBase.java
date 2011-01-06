package org.genxdm.bridgetest.nodes;

import javax.xml.namespace.QName;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.bridgetest.TestBase;

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
        
        N node = model.getAttribute(docElement, "", "att");
        assertNotNull(node);
        assertTrue(model.getNodeKind(node) == NodeKind.ATTRIBUTE);
        assertTrue(model.isAttribute(node));
  
        node = getNamespaceNode(model, docElement, "ns");
        assertNotNull(node);
        assertTrue(model.getNodeKind(node) == NodeKind.NAMESPACE);
        assertTrue(model.isNamespace(node));
        
        node = model.getFirstChild(docElement);
        assertNotNull(node); // it should be a comment
        assertTrue(model.getNodeKind(node) == NodeKind.COMMENT);
        
        node = model.getNextSibling(node);
        assertNotNull(node); // it should be text
        assertTrue(model.getNodeKind(node) == NodeKind.TEXT);
        assertTrue(model.isText(node));
        
        node = model.getNextSibling(node);
        assertNotNull(node); // it should be a pi
        assertTrue(model.getNodeKind(node) == NodeKind.PROCESSING_INSTRUCTION);
    }
    
    @Test
    public void nodeIdentity()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        //there's not a whole lot to do, here.
        // in fact, there's an interesting question: what the fuck is a
        // node identity?  say that we've created a document, above.
        // does "node id" refer to this instance of that document?  we
        // create it a lot; does it refer to any instance?  if no, then
        // does it refer to the node id returned by a different model,
        // or by a different cursor?
        // TODO: define the semantics of NodeId more clearly (in the API, not here;
        // here, we need to better test those semantics once they're defined).
        Object id = model.getNodeId(doc);
        N docElement = model.getFirstChildElement(doc);
        assertNotNull(docElement);
        assertEquals(model.getNodeId(model.getParent(docElement)), id);
        
        id = model.getNodeId(docElement);
        assertEquals(model.getNodeId(model.getFirstChildElement(doc)), id);
    }
    
    @Test
    public void idsAndRefs()
    {
        // TODO: create a document, in testbase.
        // we need to create a document that has ids and idrefs.
        // see testbase.
    }
    
    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // contract seems less than well-documented.
        // the rule is complex: on an element, getAttributeNames never
        // returns null, but may return an empty iterable.  on any other
        // node type, it always returns null.
        // getAttributeStringValue may return null.
        // it should not return anything for a "namespace attribute",
        // when a tree model provides such things.
        
        Iterable<QName> attributes = model.getAttributeNames(doc, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(doc, "", "xyzzy"));
        N node = model.getFirstChildElement(doc);
        assertNotNull(node);
        
        attributes = model.getAttributeNames(node, false);
        assertNotNull(attributes);
        // TODO: we really need a test that has an element with no attributes
        int count = 0;
        for (QName name : attributes)
        {
            assertNotNull(name.getLocalPart());
            assertNotNull(name.getNamespaceURI());
            assertNotNull(name.getPrefix());
            count++;
        }
        assertEquals(count, 1);
        assertEquals(model.getAttributeStringValue(node, "", "att"), "value");
        
        node = model.getFirstChild(node); // comment
        attributes = model.getAttributeNames(node, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(node, "", "att"));
        
        node = model.getNextSibling(node); // text
        attributes = model.getAttributeNames(node, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(node, "", "att"));
        
        node = model.getNextSibling(node); // pi
        attributes = model.getAttributeNames(node, false);
        assertNull(attributes);
        assertNull(model.getAttributeStringValue(node, "", "att"));
    }
    
    @Test
    public void namespaces()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        //TODO
        // namespace bindings, namespace for prefix, namespace names
    }
    
    @Test
    public void relationships()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        assertFalse(model.hasAttributes(doc));
        assertFalse(model.hasNamespaces(doc));
        assertTrue(model.hasChildren(doc));
        assertFalse(model.hasParent(doc));
        assertFalse(model.hasPreviousSibling(doc));
        assertFalse(model.hasNextSibling(doc));

        N elem = model.getFirstChildElement(doc);
        assertNotNull(elem);
        assertTrue(model.hasAttributes(elem));
        assertTrue(model.hasNamespaces(elem));
        assertTrue(model.hasChildren(elem));
        assertTrue(model.hasParent(elem));
        assertFalse(model.hasPreviousSibling(elem));
        assertFalse(model.hasNextSibling(elem));
        
        N node = model.getAttribute(elem, "", "att");
        assertNotNull(node);
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertFalse(model.hasPreviousSibling(node));
        assertFalse(model.hasNextSibling(node));
        
        node = getNamespaceNode(model, elem, "ns");
        assertNotNull(node);
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertFalse(model.hasPreviousSibling(node));
        assertFalse(model.hasNextSibling(node));
        
        node = model.getFirstChild(elem);
        assertNotNull(node); // comment node
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertFalse(model.hasPreviousSibling(node));
        assertTrue(model.hasNextSibling(node));
        
        node = model.getNextSibling(node);
        assertNotNull(node); // text
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertTrue(model.hasPreviousSibling(node));
        assertTrue(model.hasNextSibling(node));
        
        node = model.getNextSibling(node);
        assertNotNull(node); // pi
        assertFalse(model.hasAttributes(node));
        assertFalse(model.hasNamespaces(node));
        assertFalse(model.hasChildren(node));
        assertTrue(model.hasParent(node));
        assertTrue(model.hasPreviousSibling(node));
        assertFalse(model.hasNextSibling(node));
    }
    
    @Test
    public void names()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // namespace uri, local name, prefix
    }
    
    @Test
    public void values()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // string value
    }
    
    @Test
    public void uris()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // base and doc.
    }
    
    @Test
    public void matching()
    {
    }
    
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
