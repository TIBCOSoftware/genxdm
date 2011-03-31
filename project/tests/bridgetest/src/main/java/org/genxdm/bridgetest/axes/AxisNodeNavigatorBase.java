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

import javax.xml.XMLConstants;

import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
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
            
            // namespace nodes have an empty namespace axis
            n = getNamespaceNode(model, e, "gue");
            namespaces = model.getNamespaceAxis(n, false);
            assertNotNull(namespaces);
            iterableToList(namespaces, domains);
            assertEquals(0, domains.size());
            
            namespaces = model.getNamespaceAxis(n, true);
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
        
        // problem: if we start counting descendants, chances are excellent
        // that we are going to have an extremely brittle test, which will
        // break the first time we tweak the document to add or modify something.
        ArrayList<N> spawn = new ArrayList<N>();
        ArrayList<N> spawnMe = new ArrayList<N>();
        Iterable<N> descendants = model.getDescendantAxis(doc);
        Iterable<N> descendantsAndSelf = model.getDescendantOrSelfAxis(doc);
        assertNotNull(descendants);
        assertNotNull(descendantsAndSelf);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(spawnMe.size(), spawn.size() + 1);
        
        N el = model.getFirstChild(doc); // doc element
        descendantsAndSelf = model.getDescendantOrSelfAxis(el);
        assertNotNull(descendantsAndSelf);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(spawn.size(), spawnMe.size());
        descendants = model.getDescendantAxis(el);
        assertNotNull(descendants);
        iterableToList(descendants, spawn);
        assertEquals(spawnMe.size(), spawn.size() + 1);
        
        // the document element has seven child elements, eight text children.
        
        N n = model.getAttribute(el, "", "name"); // attributes have no descendants
        descendantsAndSelf = model.getDescendantOrSelfAxis(n);
        descendants = model.getDescendantAxis(n);
        assertNotNull(descendantsAndSelf);
        assertNotNull(descendants);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(0, spawn.size());
        assertEquals(spawnMe.size(), spawn.size() + 1);

        n = model.getFirstChild(el); // text node
        descendantsAndSelf = model.getDescendantOrSelfAxis(n);
        descendants = model.getDescendantAxis(n);
        assertNotNull(descendantsAndSelf);
        assertNotNull(descendants);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(0, spawn.size());
        assertEquals(spawnMe.size(), spawn.size() + 1);
        
        n = model.getFirstChildElementByName(model.getFirstChildElementByName(el, "", "path"), "", "pathelement");
        // empty element
        descendantsAndSelf = model.getDescendantOrSelfAxis(n);
        descendants = model.getDescendantAxis(n);
        assertNotNull(descendantsAndSelf);
        assertNotNull(descendants);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(0, spawn.size());
        assertEquals(spawnMe.size(), spawn.size() + 1);
        
        n = model.getFirstChildElement(model.getFirstChildElementByName(el, "http://www.genxdm.org/nonsense", "nstest"));
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            N ns = getNamespaceNode(model, n, "grue");
            descendants = model.getDescendantAxis(ns);
            descendantsAndSelf = model.getDescendantOrSelfAxis(ns);
            assertNotNull(descendants);
            assertNotNull(descendantsAndSelf);
            iterableToList(descendants, spawn);
            iterableToList(descendantsAndSelf, spawnMe);
            assertEquals(0, spawn.size());
            assertEquals(spawnMe.size(), spawn.size() + 1);
        }

        n = model.getNextSiblingElement(model.getFirstChildElement(n)); // has a text child and an attribute
        descendants = model.getDescendantAxis(n);
        descendantsAndSelf = model.getDescendantOrSelfAxis(n);
        assertNotNull(descendants);
        assertNotNull(descendantsAndSelf);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(1, spawn.size());
        assertEquals(spawnMe.size(), spawn.size() + 1);
        
        // already checked text node, so double over to the comment
        n = model.getNextSibling(model.getNextSibling(n));
        descendants = model.getDescendantAxis(n);
        descendantsAndSelf = model.getDescendantOrSelfAxis(n);
        assertNotNull(descendants);
        assertNotNull(descendantsAndSelf);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(0, spawn.size());
        assertEquals(spawnMe.size(), spawn.size() + 1);
        
        // pi, same like comment
        n = model.getNextSibling(model.getNextSibling(n));
        descendants = model.getDescendantAxis(n);
        descendantsAndSelf = model.getDescendantOrSelfAxis(n);
        assertNotNull(descendants);
        assertNotNull(descendantsAndSelf);
        iterableToList(descendants, spawn);
        iterableToList(descendantsAndSelf, spawnMe);
        assertEquals(0, spawn.size());
        assertEquals(spawnMe.size(), spawn.size() + 1);
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
        Iterable<N> children = model.getChildAxis(doc);
        // there's only one child, the document element
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(1, spawn.size());
        
        N de = spawn.get(0);
        children = model.getChildAxis(de);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(15, spawn.size());

        N child = model.getFirstChildElement(model.getFirstChildElement(de)); // empty element
        children = model.getChildAxis(child);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(0, spawn.size());
        
        child = model.getAttribute(de, "", "name"); // attributes have no children
        children = model.getChildAxis(child);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(0, spawn.size());
        
        child = model.getFirstChild(de); // a text element (which has no children)
        children = model.getChildAxis(child);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(0, spawn.size());

        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            child = getNamespaceNode(model, model.getFirstChildElementByName(de, "http://www.genxdm.org/nonsense", "nstest"), "gue");
            children = model.getChildAxis(child);
            assertNotNull(children);
            iterableToList(children, spawn);
            assertEquals(0, spawn.size());
        }

        child = model.getFirstChildElement(model.getFirstChildElementByName(de, "http://www.genxdm.org/nonsense", "nstest"));
        // this is the zork element, which has two elements, a comment, a pi, and five text nodes
        children = model.getChildAxis(child);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(9, spawn.size());

        child = model.getPreviousSibling(model.getLastChild(child)); // pi
        children = model.getChildAxis(child);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(0, spawn.size());
        
        child = model.getPreviousSibling(model.getPreviousSibling(child)); // comment
        children = model.getChildAxis(child);
        assertNotNull(children);
        iterableToList(children, spawn);
        assertEquals(0, spawn.size());
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
        
        // little brothers and sisters are pests (and that's what following siblings are)
        ArrayList<N> pests = new ArrayList<N>();
        Iterable<N> fSibs = model.getFollowingSiblingAxis(doc);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(0, pests.size()); // always empty for doc
        
        N de = model.getFirstChildElement(doc); // project element; use to find an attribute with 'sibs' and a namespace ditto.
        // for attribute and namespace, the axis is always empty.
        N a = model.getAttribute(de, XMLConstants.NULL_NS_URI, "name");
        assertNotNull(a);
        fSibs = model.getFollowingSiblingAxis(a);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(0, pests.size());
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            N ns = getNamespaceNode(model, model.getFirstChildElementByName(de, "http://www.genxdm.org/nonsense", "nstest"), "gue");
            assertNotNull(ns);
            fSibs = model.getFollowingSiblingAxis(ns);
            assertNotNull(fSibs);
            iterableToList(fSibs, pests);
            assertEquals(0, pests.size());
        }
        
        // interesting case: the project element has no siblings
        fSibs = model.getFollowingSiblingAxis(de);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(0, pests.size());
        
        N first = model.getFirstChild(de); // text
        N last = model.getLastChild(de); // text
        
        fSibs = model.getFollowingSiblingAxis(first);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(14, pests.size());

        fSibs = model.getFollowingSiblingAxis(last);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(0, pests.size());
        
        first = model.getNextSiblingElement(first); // element
        last = model.getPreviousSibling(model.getLastChild(model.getFirstChildElement(model.getPreviousSibling(last)))); // pi
        
        fSibs = model.getFollowingSiblingAxis(first);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(13, pests.size());
        
        fSibs = model.getFollowingSiblingAxis(last);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(1, pests.size());
        
        last = model.getPreviousSibling(model.getPreviousSibling(last)); // comment
        fSibs = model.getFollowingSiblingAxis(last);
        assertNotNull(fSibs);
        iterableToList(fSibs, pests);
        assertEquals(3, pests.size());
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
        
        // big brothers and sisters are bullies (see above)
        ArrayList<N> bullies = new ArrayList<N>();
        Iterable<N> pSibs = model.getPrecedingSiblingAxis(doc);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(0, bullies.size()); // always empty for doc
        
        N de = model.getFirstChildElement(doc); // project element; use to find an attribute with 'sibs' and a namespace ditto.
        // for attribute and namespace, the axis is always empty.
        N a = model.getAttribute(de, XMLConstants.NULL_NS_URI, "name");
        assertNotNull(a);
        pSibs = model.getPrecedingSiblingAxis(a);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(0, bullies.size());
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            N ns = getNamespaceNode(model, model.getFirstChildElementByName(de, "http://www.genxdm.org/nonsense", "nstest"), "gue");
            assertNotNull(ns);
            pSibs = model.getPrecedingSiblingAxis(ns);
            assertNotNull(pSibs);
            iterableToList(pSibs, bullies);
            assertEquals(0, bullies.size());
        }

        // interesting case: the project element has no siblings
        pSibs = model.getPrecedingSiblingAxis(de);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(0, bullies.size());
        
        N first = model.getFirstChild(de); //text
        N last = model.getLastChild(de); // text
        
        pSibs = model.getPrecedingSiblingAxis(first);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(0, bullies.size());

        pSibs = model.getPrecedingSiblingAxis(last);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(14, bullies.size());

        first = model.getNextSiblingElement(first); // element
        last = model.getPreviousSibling(model.getLastChild(model.getFirstChildElement(model.getPreviousSibling(last)))); // pi
        
        pSibs = model.getPrecedingSiblingAxis(first);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(1, bullies.size());
        
        pSibs = model.getPrecedingSiblingAxis(last);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(7, bullies.size());
        
        last = model.getPreviousSibling(model.getPreviousSibling(last)); // comment
        pSibs = model.getPrecedingSiblingAxis(last);
        assertNotNull(pSibs);
        iterableToList(pSibs, bullies);
        assertEquals(5, bullies.size());
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
        // A note, for those of you following along at home.
        // Though it seems 'natural' for "following" and "preceding"
        // to partition a tree into two pieces around a single node,
        // that's not what happens.  There is a partition, but it involves
        // not following + preceding + self, but following + preceding + ancestor-or-self.
        // This is *most* counterintuitive for 'preceding'--one doe not
        // expect 'following' to include any ancestors, and following *does*
        // include descendants.  It would seem reasonable for 'preceding',
        // in parallel, to include ancestors--but it doesn't.
        // both following and preceding are apt to produce non-well-formed
        // fragments, as a consequence of these definitions.
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
