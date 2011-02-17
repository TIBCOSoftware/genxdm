/**
 * Copyright (c) 2011 TIBCO Software Inc.
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
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size());
        
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(doc, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(0, attributes.size());
        }
        
        // (some) element nodes do have attributes.
        N de = model.getFirstChildElement(doc);
        assertNotNull(de);
        // the document element has three attributes, one of which is xml:lang
        atts = model.getAttributeAxis(de, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(3, attributes.size());

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(de, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(3, attributes.size());
        }
        
        N n = model.getFirstChildElement(de); // path element; 1 attribute
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(1, attributes.size());
        
        if (doInheritedAttributeTests)
        {
            // there are *two* attributes if we inherit.
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(2, attributes.size());
        }
        
        n = attributes.get(0); // first attribute
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size()); // attributes ain't got attributes.

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(0, attributes.size()); // not even inherited ones.
        }

        // nstest element has no attributes, but does inherit the same as before.
        n = model.getPreviousSibling(model.getLastChild(de)); // nstest element
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size());

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(1, attributes.size());
        }
        
        N ns = getNamespaceNode(model, n, "gue");
        assertNotNull(ns);
        atts = model.getAttributeAxis(ns, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size()); // namespaces don't got attributes, neither

        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(ns, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(0, attributes.size()); // nope, not even inherited ones
        }
        
        n = model.getLastChild(model.getFirstChildElement(n)); // text node: no atts
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size());
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(0, attributes.size());
        }
        
        n = model.getPreviousSibling(n); // pi; no atts
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size());
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
            assertEquals(0, attributes.size());
        }

        n = model.getPreviousSibling(model.getPreviousSibling(n)); // comment; no atts
        assertNotNull(n);
        atts = model.getAttributeAxis(n, false);
        assertNotNull(atts);
        iterableToList(atts, attributes);
        assertEquals(0, attributes.size());
        if (doInheritedAttributeTests)
        {
            atts = model.getAttributeAxis(n, true);
            assertNotNull(atts);
            iterableToList(atts, attributes);
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

            ArrayList<N> domains = new ArrayList<N>();
            
            Iterable<N> namespaces = model.getNamespaceAxis(doc, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(doc, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            N e = model.getFirstChildElement(doc);
            // an element node has a namespace axis; this one's empty,
            // except for the inherited namespace.
            
            namespaces = model.getNamespaceAxis(e, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(e, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(1, domains.size());
            
            N n = model.getAttribute(e, "", "name");
            namespaces = model.getNamespaceAxis(n, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(n, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            n = model.getLastChild(e);
            e = model.getPreviousSibling(n);
            namespaces = model.getNamespaceAxis(e, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
//for (N ns : namespaces) { System.out.println(model.getLocalName(ns)); System.out.println(model.getStringValue(ns));}
            assertEquals(2, domains.size());
            
            namespaces = model.getNamespaceAxis(e, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(3, domains.size());
            
            n = getNamespaceNode(model, e, "gue");
            namespaces = model.getNamespaceAxis(e, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(e, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            e = model.getFirstChildElement(e);
            // this one also has namespaces. 2+2
            namespaces = model.getNamespaceAxis(e, false);
            iterableToList(namespaces, domains);
            assertEquals(2, domains.size());
            
            namespaces = model.getNamespaceAxis(e, true);
            iterableToList(namespaces, domains);
            assertEquals(4, domains.size());
            
            // get the last child.  that's a text node.
            n = model.getLastChild(e);
            namespaces = model.getNamespaceAxis(n, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(n, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            // get the previous sibling. it's a processing instruction
            n = model.getPreviousSibling(n);
            namespaces = model.getNamespaceAxis(n, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(n, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            // get the previous sibling of the previous sibling. that's a comment
            n = model.getPreviousSibling(model.getPreviousSibling(n));
            namespaces = model.getNamespaceAxis(n, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(n, true);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            // get the firstchildelement.  0+4
            n = model.getFirstChildElement(e);
            namespaces = model.getNamespaceAxis(n, false);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(n, true);
            iterableToList(namespaces, domains);
            assertEquals(4, domains.size());
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
        
        ArrayList<N> olds = new ArrayList<N>();
        
        Iterable<N> ancestors = model.getAncestorAxis(doc);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(0, olds.size()); // documents have no ancestors.
        
        ancestors = model.getAncestorOrSelfAxis(doc);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(1, olds.size());
        
        N de = model.getFirstChildElement(doc);
        assertNotNull(de);
        
        ancestors = model.getAncestorAxis(de);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(1, olds.size());
        
        ancestors = model.getAncestorOrSelfAxis(de);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(2, olds.size());
        
        N n = model.getAttribute(de, "", "att");
        ancestors = model.getAncestorAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(2, olds.size());
        
        ancestors = model.getAncestorOrSelfAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(3, olds.size());
        
        n = getNamespaceNode(model, de, "ns");
        ancestors = model.getAncestorAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(2, olds.size());
        
        ancestors = model.getAncestorOrSelfAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(3, olds.size());
        
        n = model.getFirstChild(de); //comment
        ancestors = model.getAncestorAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(2, olds.size());
        
        ancestors = model.getAncestorOrSelfAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(3, olds.size());
        
        n = model.getNextSibling(n); // text
        ancestors = model.getAncestorAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(2, olds.size());
        
        ancestors = model.getAncestorOrSelfAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(3, olds.size());
        
        n = model.getNextSibling(n);
        ancestors = model.getAncestorAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(2, olds.size());
        
        ancestors = model.getAncestorOrSelfAxis(n);
        assertNotNull(ancestors);
        iterableToList(ancestors, olds);
        assertEquals(3, olds.size());
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
        
        ArrayList<N> spawn = new ArrayList<N>();
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

        ArrayList<N> spawn = new ArrayList<N>();
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

        ArrayList<N> spawn = new ArrayList<N>();
        ArrayList<N> namedSpawn = new ArrayList<N>();
        Iterable<N> childElements = model.getChildElements(doc);
        assertNotNull(childElements);
        iterableToList(childElements, spawn);
        assertEquals(1, spawn.size()); // a document always has one child element.
        
        childElements = model.getChildElementsByName(doc, "", ""); // match nothing
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(0, namedSpawn.size());
        
        childElements = model.getChildElementsByName(doc, null, null); // match anything, equivalent to getChildElements. verify that.
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(1, namedSpawn.size());
        // old fashioned, so we can compare the members of both lists
        for (int i = 0 ; i < spawn.size() ; i++)
        {
            assertEquals(model.getNodeId(spawn.get(i)), model.getNodeId(namedSpawn.get(i)));
        }
        
        N project = spawn.get(0);
        // project has seven child elements: path, fileset, target x 4, nstest
        childElements = model.getChildElements(project);
        assertNotNull(childElements);
        iterableToList(childElements, spawn);
        assertEquals(7, spawn.size());
        
        childElements = model.getChildElementsByName(project, null, null);
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(7, namedSpawn.size());
        for (int i = 0 ; i < spawn.size() ; i++)
        {
            assertEquals(model.getNodeId(spawn.get(i)), model.getNodeId(namedSpawn.get(i)));
        }

        childElements = model.getChildElementsByName(project, "", null); // six in this namespace
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(6, namedSpawn.size());
        
        // only nstest in this namespace
        childElements = model.getChildElementsByName(project, "http://www.genxdm.org/nonsense", null);
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(1, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, "no-match-possible", null);
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(0, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, "", "path");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(1, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, null, "path");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(1, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, "", "target");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(4, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, null, "target");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(4, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, "http://www.genxdm.org/nonsense", "nstest");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(1, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, null, "nstest");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(1, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, "no-match-possible", "no-match-possible");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(0, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, "", "no-match-possible");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(0, namedSpawn.size());
        
        childElements = model.getChildElementsByName(project, null, "no-match-possible");
        assertNotNull(childElements);
        iterableToList(childElements, namedSpawn);
        assertEquals(0, namedSpawn.size());
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
        
        ArrayList<N> pests = new ArrayList<N>();
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
        
        ArrayList<N> elders = new ArrayList<N>();
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
        
        ArrayList<N> sheep = new ArrayList<N>();
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
        
        ArrayList<N> cousins = new ArrayList<N>();
        // TODO
        // oh, how fucking weird.  *cannot* include ancestors, but includes
        // other descendants of the root of the tree that precede this node in doc order 
    }
    
    private void iterableToList(Iterable<N> iterable, ArrayList<N> list)
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
