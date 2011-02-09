package org.genxdm.bridgetest.axes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.genxdm.Feature;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.bridgetest.TestBase;
import org.junit.Test;

public abstract class AxisNodeNavigatorBase<N>
    extends TestBase<N>
{

    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        
        boolean doInheritedAttributeTests = context.isSupported(Feature.ATTRIBUTE_AXIS_INHERIT);
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        ArrayList<N> attributes = new ArrayList<N>(); 
        
        // document nodes have no attributes, inherited or otherwise
        Iterable<N> atts = model.getAttributeAxis(doc, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size());
        
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(doc, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(0, attributes.size());
        }
        
        // (some) element nodes do have attributes.
        N de = model.getFirstChildElement(doc);
        assertNotNull(de);
        // the document element has three attributes, one of which is xml:lang
        atts = model.getAttributeAxis(de, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(3, attributes.size());

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(de, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(3, attributes.size());
        }
        
        N n = model.getFirstChildElement(de); // path element; 1 attribute
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(1, attributes.size());
        
        if (doInheritedAttributeTests)
        {
            // there are *two* attributes if we inherit.
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(2, attributes.size());
        }
        
        n = attributes.get(0); // first attribute
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size()); // attributes ain't got attributes.

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(0, attributes.size()); // not even inherited ones.
        }

        // nstest element has no attributes, but does inherit the same as before.
        n = model.getPreviousSibling(model.getLastChild(de)); // nstest element
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size());

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(1, attributes.size());
        }
        
        N ns = getNamespaceNode(model, n, "gue");
        assertNotNull(ns);
        atts = model.getAttributeAxis(ns, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size()); // namespaces don't got attributes, neither

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(ns, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(0, attributes.size()); // nope, not even inherited ones
        }
        
        n = model.getLastChild(model.getFirstChildElement(n)); // text node: no atts
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size());
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(0, attributes.size());
        }
        
        n = model.getPreviousSibling(n); // pi; no atts
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size());
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(0, attributes.size());
        }

        n = model.getPreviousSibling(model.getPreviousSibling(n)); // comment; no atts
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(attributes, atts);
        assertEquals(0, attributes.size());
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(attributes, atts);
            assertEquals(0, attributes.size());
        }
    }
    
    @Test
    public void namespaces()
    {
        ProcessingContext<N> context = newProcessingContext();
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            FragmentBuilder<N> builder = context.newFragmentBuilder();
            N doc = createComplexTestDocument(builder);
            
            assertNotNull(doc);
            Model<N> model = context.getModel();
            assertNotNull(model);

            // TODO: doc, doc element (inherited only). check docelement att
            // nstest element and its children. namespaces, text, comment, pi
        }
    }
    
    @Test
    public void ancestors()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createSimpleAllKindsDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        
        // context is document: no ancestors; self
        // document element: one ancestor + self
        // docelement attribute: two ancestors + self
        // docelement namespace: two ancestors + self
        // comment: two ancestors + self
        // text: two ancestores + self
        // pi: two ancestors + self
        // TODO
    }
    
    @Test
    public void descendants()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // no attributes, no namespaces
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
        // no attributes, no namespaces
    }
    
    @Test
    public void childElements()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // no attributes, no namespaces
        // no comments, no text, no pis
    }
    
    @Test
    public void childElementsByName()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
    }
    
    @Test
    public void followingSiblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // no attributes, no namespaces
    }
    
    @Test
    public void precedingSiblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // no attributes, no namespaces
    }

    @Test
    public void following()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // note: no attributes, no namespaces, and no descendants.
        // all descendants of following siblings and of ancestor's following siblings
    }
    
    @Test
    public void preceding()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Model<N> model = context.getModel();
        assertNotNull(model);
        // TODO
        // oh, how fucking weird.  *cannot* include ancestors, but includes
        // other descendants of the root of the tree that precede this node in doc order 
    }
    
    private void iterableToList(ArrayList<N> list, Iterable<N> iterable)
    {
        list.clear();
        for (N n : iterable)
            list.add(n);
    }
    
//    /**
//     * Returns the nodes along the ancestor axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getAncestorAxis(N node);
//
//    /**
//     * Returns the nodes along the ancestor-or-self axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getAncestorOrSelfAxis(N node);
//
//    /**
//     * Returns the nodes along the child axis using the specified node as the origin.
//     * 
//     * <br/>
//     * 
//     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-children">
//     * dm:children</a> accessor in the XDM.
//     * 
//     * @param node
//     *            The origin node.
//     * 
//     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-children
//     */
//    Iterable<N> getChildAxis(N node);
//
//    /**
//     * Returns all the child element along the child axis.
//     * 
//     * @param node
//     *            The parent node that owns the child axis.
//     */
//    Iterable<N> getChildElements(N node);
//
//    /**
//     * Returns all the child element along the child axis whose names match the arguments supplied.
//     * 
//     * @param node
//     *            The parent node that owns the child axis.
//     * @param namespaceURI
//     *            The namespace-uri to be matched.
//     * @param localName
//     *            The local-name to be matched.
//     */
//    Iterable<N> getChildElementsByName(N node, String namespaceURI, String localName);
//
//    /**
//     * Returns the nodes along the descendant axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getDescendantAxis(N node);
//
//    /**
//     * Returns the nodes along the descendant-or-self axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getDescendantOrSelfAxis(N node);
//
//    /**
//     * Returns the nodes along the following axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getFollowingAxis(N node);
//
//    /**
//     * Returns the nodes along the following-sibling axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getFollowingSiblingAxis(N node);
//
//    /**
//     * Returns the nodes along the preceding axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getPrecedingAxis(N node);
//
//    /**
//     * Returns the nodes along the preceding-sibling axis using the specified node as the origin.
//     * 
//     * @param node
//     *            The origin node.
//     */
//    Iterable<N> getPrecedingSiblingAxis(N node);
}
