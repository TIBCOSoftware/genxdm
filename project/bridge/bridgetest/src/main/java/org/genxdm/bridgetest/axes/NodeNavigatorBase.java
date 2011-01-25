package org.genxdm.bridgetest.axes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.bridgetest.TestBase;
import org.junit.Test;

public abstract class NodeNavigatorBase<N>
    extends TestBase<N>
{

    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        // complex test document is an ant build script (mostly).
        // ant is attribute-heavy.  we're gonna navigate to one
        // of the few elements that are attribute-free in the
        // second part of the test, to verify that we can't
        // get an attribute no matter what we do.
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        N el = model.getFirstChildElement(doc); // project element
        assertNotNull(el);
        // project element has two attributes: name and default
        N at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "name");
        assertNotNull(at);
        at = model.getAttribute(el, "'", "name");
        assertNull(at); // no such attribute exists
        at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "default");
        assertNotNull(at);
        N nn = model.getAttribute(at, XMLConstants.NULL_NS_URI, "default");
        assertNull(nn); // the attribute should not return itself.
        
        el = model.getLastChild(el); // a text node?
        assertEquals(NodeKind.TEXT, model.getNodeKind(el));
        at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "name");
        assertNull(at); // text nodes have no attributes.
        el = model.getPreviousSibling(el); // nstest node
        assertNotNull(el);
        at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "name");
        assertNull(at); // no such attribute
        at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "xmlns");
        assertNull(at); // the xmlns 'attribute' is not an attribute!
        
        // TODO: maybe test other node types (document, comment, pi, even ns)
        // to verify that they will not return an attribute when asked?
        // but ... you can't prove a negative, you know. so we should test
        // common error cases; other ones ... not so much.
    }
    
    @Test
    public void ids()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        N lc = model.getLastChild(model.getFirstChildElement(doc));
        assertNotNull(lc); // text node inside closing tag of doc element
        
        N el = model.getElementById(doc, "project.class.path");
        assertNotNull(el);
        N at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "id");
        assertNotNull(at);
        assertEquals("project.class.path", model.getStringValue(at));
        
        el = model.getElementById(lc, "project.output");
        assertNotNull(el);
        at = model.getAttribute(el, XMLConstants.NULL_NS_URI, "id");
        assertNotNull(at);
        assertEquals("project.output", model.getStringValue(at));
        
        el = model.getElementById(doc, "this.id.does.not.exist");
        assertNull(el);
    }
    
    @Test
    public void children()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // getfirstchild, getfirstchildelement, getfirstchildelementbyname
        // laschild
    }
    
    @Test
    public void siblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // getNextSibling, getNextSiblingElement, getNextSiblingElementByName
        // previousSibling
    }
    
    @Test
    public void ancestors()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // document node
        N p = model.getParent(doc);
        assertNull(p); // doc has no parent.
        p = model.getRoot(doc);
        assertNotNull(p);
        assertEquals(doc, p);
        
        // element node
        N el = model.getFirstChildElement(model.getFirstChildElement(doc));
        assertNotNull(el);
        p = model.getParent(el);
        assertNotNull(p);
        p = model.getRoot(el);
        assertNotNull(p);
        assertEquals(doc, p);
        
        // attribute node
        N n = model.getAttribute(el, XMLConstants.NULL_NS_URI, "id");
        p = model.getParent(n);
        assertNotNull(p);
        assertEquals(el, p);
        p = model.getRoot(n);
        assertNotNull(p);
        assertEquals(doc, p);
        
        // text node
        n = model.getFirstChild(el);
        assertNotNull(n);
        assertEquals(NodeKind.TEXT, model.getNodeKind(n));
        p = model.getParent(n);
        assertNotNull(p);
        assertEquals(el, p);
        p = model.getRoot(n);
        assertNotNull(p);
        assertEquals(doc, p);
        
        // TODO: namespace, comment, and processing instruction nodes.
    }
    
//    /**
//     * Returns the first child node of the node provided.
//     * 
//     * @param node
//     *            The node for which the first child node is required.
//     */
//    N getFirstChild(N origin);
//
//    /**
//     * Returns the first element along the child axis.
//     * 
//     * @param node
//     *            The parent node that owns the child axis.
//     */
//    N getFirstChildElement(N node);
//
//    /**
//     * Returns the first child element along the child axis whose name matches the arguments supplied.
//     * 
//     * @param node
//     *            The parent node that owns the child axis.
//     * @param namespaceURI
//     *            The namespace-uri to be matched.
//     * @param localName
//     *            The local-name to be matched.
//     */
//    N getFirstChildElementByName(N node, String namespaceURI, String localName);
//
//    /**
//     * Returns the last child node of the node provided.
//     * 
//     * @param node
//     *            The node for which the last child node is required.
//     */
//    N getLastChild(N node);
//
//    /**
//     * Returns the next sibling node of the node provided.
//     * 
//     * @param node
//     *            The node for which the next sibling node is required.
//     */
//    N getNextSibling(N node);
//
//    /**
//     * Returns the next element along the child axis.
//     * 
//     * @param node
//     *            The node for which the next sibling node is required.
//     */
//    N getNextSiblingElement(N node);
//
//    /**
//     * Returns the next element along the following-sibling axis whose name matches the arguments supplied.
//     * 
//     * @param node
//     *            The node for which the next sibling node is required.
//     * @param namespaceURI
//     *            The namespace-uri to be matched.
//     * @param localName
//     *            The local-name to be matched.
//     */
//    N getNextSiblingElementByName(N node, String namespaceURI, String localName);
//
//    /**
//     * Returns the previous sibling node of the node provided.
//     * 
//     * @param node
//     *            The node for which the previous sibling node is required.
//     */
//    N getPreviousSibling(N node);
//
}
