/*
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
package org.genxdm.bridgetest.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.bridgetest.utilities.Events;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;
import org.junit.Test;

public abstract class MutableModelBase<N>
    extends TestBase<N>
{

    @Test
    public void factory()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        NodeFactory<N> factory = newProcessingContext().getMutableContext().getModel().getFactory(doc);
        assertNotNull(factory);
    }
    
    @Test
    public void children()
    {
        ProcessingContext<N> context = newProcessingContext();
        MutableModel<N> mutant = context.getMutableContext().getModel();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        NodeFactory<N> factory = mutant.getFactory(doc);
        Model<N> model = context.getModel();
        N element = model.getFirstChildElement(doc);
        
        N first = model.getFirstChild(element);
        
        N text = factory.createText("new text");
        
        N comment = factory.createComment("new comment");
        N pi = factory.createProcessingInstruction("new", "pi");
        
        List<N> children = new ArrayList<N>(2);
        children.add(comment);
        children.add(pi);
        
        assertEquals(NodeKind.COMMENT, model.getNodeKind(first));
        mutant.prependChild(element, text);
        assertEquals(NodeKind.TEXT, model.getNodeKind(model.getFirstChild(element)));
        mutant.prependChildren(element, children);
        assertEquals(NodeKind.COMMENT, model.getNodeKind(model.getFirstChild(element)));
        
        Iterable<N> sids = mutant.deleteChildren(element);
        assertNotNull(sids);
        int i = 0;
        for (@SuppressWarnings("unused")N n : sids) { i++; }
        assertEquals(6, i);
        
        doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        element = model.getFirstChildElement(doc);
        
        N last = model.getLastChild(element);
        
        text = factory.createText("new text");
        
        comment = factory.createComment("new comment");
        pi = factory.createProcessingInstruction("new", "pi");
        
        children.clear();
        children.add(comment);
        children.add(pi);
        
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, model.getNodeKind(last));
        mutant.appendChild(element, text);
        assertEquals(NodeKind.TEXT, model.getNodeKind(model.getLastChild(element)));
        mutant.appendChildren(element, children);
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, model.getNodeKind(model.getLastChild(element)));
    }
    
    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        MutableModel<N> model = context.getMutableContext().getModel();
        NodeFactory<N> factory = model.getFactory(doc);
        
        N solo = factory.createAttribute("", "solo", "", "x");
        
        N a1 = factory.createAttribute("", "a1", "", "1");
        N a2 = factory.createAttribute("", "a2", "", "2");
        List<N> attlist = new ArrayList<N>(2);
        attlist.add(a1);
        attlist.add(a2);
        
        doc = model.getFirstChildElement(doc);
        model.insertAttribute(doc, solo);
        
        int i = 0;
        Iterable<N> atts = model.getAttributeAxis(doc, false);
        for (@SuppressWarnings("unused")N a : atts) { i++; }
        assertEquals(2, i);
        
        model.insertAttributes(doc, attlist);
        i = 0;
        atts = model.getAttributeAxis(doc, false);
        for (@SuppressWarnings("unused") N a : atts) { i++; }
        assertEquals(4, i);
    }
    
    @Test
    public void namespace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        MutableModel<N> model = context. getMutableContext().getModel();
        
        // note: not having a namespace axis isn't an excuse
        // namespace *support* is required, it's just that namespaces
        // are allowed to be metadata, not nodes
        
        List<String> nnames = new ArrayList<String>();
        N e = model.getFirstChildElement(doc);
        for (String s : model.getNamespaceNames(e, false))
        {
            nnames.add(s);
        }
        model.insertNamespace(e, "new", "http://localhost/");
        List<String> pnames = new ArrayList<String>();
        for (String s : model.getNamespaceNames(e, false))
        {
            pnames.add(s);
        }
        assertTrue(pnames.size() > nnames.size());
        for (String s : nnames)
        {
            if (!pnames.contains(s))
                throw new IllegalStateException("lost a namespace somewhere:" + s);
        }
        assertTrue(pnames.contains("new"));
    }
    
    @Test
    public void siblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        MutableModel<N> model = context.getMutableContext().getModel();
        NodeFactory<N> factory = model.getFactory(doc);
        
        N e1 = factory.createElement("", "e1", "");
        N e2 = factory.createElement("", "e2", "");
        N e3 = factory.createElement("", "e3", "");
        
        // insert element before pi
        N mark = model.getLastChild(model.getFirstChild(doc));
        N check = model.getPreviousSibling(mark);
        model.insertBefore(mark, e3);
        assertEquals(mark, model.getNextSibling(e3));
        assertEquals(check, model.getPreviousSibling(e3));
        
        // insert element before text
        mark = check;
        check = model.getPreviousSibling(mark);
        model.insertBefore(mark, e2);
        assertEquals(mark, model.getNextSibling(e2));
        assertEquals(check, model.getPreviousSibling(e2));
        
        // insert element before comment
        mark = check;
        check = model.getPreviousSibling(mark); // null!
        assertNull(check);
        model.insertBefore(mark, e1);
        assertEquals(mark, model.getNextSibling(e1));
        assertEquals(check, model.getPreviousSibling(e1)); // this should work, though.
        
        model.delete(e1);
        model.delete(e2);

        // insert element before element
        check = model.getPreviousSibling(e3);
        model.insertBefore(e3, e2);
        assertEquals(e3, model.getNextSibling(e2));
        assertEquals(check, model.getPreviousSibling(e2));
        
        // TODO: we should also insert text, comment, pi, before element text comment pi
        // tedious.  this is the short version.  the tricky one of those is text,
        // which has the potential (in some apis, at least) to collapse nodes when
        // inserted as a sibling to a text node.
        
        model.delete(e2); // again, yes, 'cause we put it back in
        model.delete(e3);
        
        // now do it again, the other way round.
        mark = model.getFirstChild(model.getFirstChild(doc));
        check = model.getNextSibling(mark);
        model.insertAfter(mark, e1);
        assertEquals(mark, model.getPreviousSibling(e1));
        assertEquals(check, model.getNextSibling(e1));
        
        mark = check;
        check = model.getNextSibling(mark);
        model.insertAfter(mark, e2);
        assertEquals(mark, model.getPreviousSibling(e2));
        assertEquals(check, model.getNextSibling(e2));
        
        mark = check;
        check = model.getNextSibling(mark); // null
        assertNull(check);
        model.insertAfter(mark, e3);
        assertEquals(mark, model.getPreviousSibling(e3));
        assertEquals(check, model.getNextSibling(e3));
        
        model.delete(e3);
        model.delete(e2);
        
        check = model.getNextSibling(e1);
        model.insertAfter(e1, e2);
        assertEquals(e1, model.getPreviousSibling(e2));
        assertEquals(check, model.getNextSibling(e2));
        
        // TODO: this is some basic sequence position and order checking.
        // we're not being as complete as we were above, and we could
        // afford to be, so add more here, when time permits.
        model.delete(e2);
        List<N> sibs = new ArrayList<N>(2);
        sibs.add(e2);
        sibs.add(e3);
        // check is currently pointing at the comment.
        // e1 is the first child.
        assertEquals(e1, model.getPreviousSibling(check));
        model.insertBefore(check, sibs);
        assertEquals(e2, model.getNextSibling(e1));
        assertEquals(e1, model.getPreviousSibling(e2));
        assertEquals(e3, model.getPreviousSibling(check));
        assertEquals(check, model.getNextSibling(e3));
        
        // belt and suspenders: after removing from the tree, let's
        // put 'em back in a different place.
        model.delete(e2);
        model.delete(e3);
        mark = model.getNextSibling(check);
        model.insertAfter(check, sibs);
        assertEquals(e2, model.getNextSibling(check));
        assertEquals(check, model.getPreviousSibling(e2));
        assertEquals(e3, model.getPreviousSibling(mark));
        assertEquals(mark, model.getNextSibling(e3));

        // TODO: we should test putting things in as siblings of the
        // document element, too, but there's a world of potential hurt,
        // there.  some apis won't permit text nodes (even ignorable whitespace,
        // per the specification) as children of document.  an actual *document*
        // node shouldn't let you put a second element there, but the xdm document
        // in fact represents an xml entity, more accurately (but not even
        // that, really; it's just the notional wrapper around a sequence,
        // if you want to get down to cases like bare attributes).
        
        // DOM had a bug wherein inserting a collection of items before the document element
        // could fail.
        N docElem = model.getFirstChildElement(doc);
        List<N> misc = new ArrayList<N>();
        misc.add(factory.createComment("Something here."));
        //misc.add(factory.createText("\n"));
        model.insertBefore(docElem, misc);
    }

    @Test
    public void delete()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        MutableModel<N> model = context.getMutableContext().getModel();
        
        N docElem = model.getFirstChildElement(doc);
        N target = model.getFirstChild(docElem);
        N marker = model.getNextSibling(target);
        N result = model.delete(target); // delete comment
        assertEquals(target, result);
        target = marker;
        marker = model.getNextSibling(target);
        result = model.delete(target); // delete text
        assertEquals(target, result);
        target = marker;
        marker = model.getParent(target);
        result = model.delete(target); // delete pi
        assertEquals(target, result);
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            // TODO: namespace?  only if namespace axis is not supported.
            // otherwise, what does it mean to delete a namespace?
            // also, there's no standard way to get a namespace as a node,
            // except by iterating over the namespace axis.
        }
        target = model.getAttribute(marker, "", "att");
        result = model.delete(target); // delete att
        assertEquals(target, result);
        // at this point, we could move the marker back to document, but why?
        result = model.delete(marker); // delete element
        assertEquals(marker, result);
        
        // Had a bug in AXIOM where the document was still pointing at the child even after delete.
        assertNull(model.getFirstChildElement(doc));
        // can't delete the document
    }
    
    @Test
    public void replace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        MutableModel<N> model = context.getMutableContext().getModel();
        NodeFactory<N> factory = model.getFactory(doc);
        
        // replace value: attribute, text, comment, pi.
        // note that there's no guarantee that a text or comment will
        // retain identity.
        doc = model.getFirstChildElement(doc);
        N target = model.getAttribute(doc, "", "att");
        String val = model.replaceValue(target, "none");
        assertEquals("value", val);
        assertEquals("none", model.getStringValue(target));
        assertEquals("none", model.getAttributeStringValue(doc, "", "att"));
        
        target = model.getFirstChild(doc); // comment
        val = model.replaceValue(target, "no comment");
        assertEquals("comment", val);
        assertEquals("no comment", model.getStringValue(model.getFirstChild(doc)));
        // don't test the value of target; it may not have changed.
        
        target = model.getNextSibling(model.getFirstChild(doc)); // text
        val = model.replaceValue(target, "no text");
        assertEquals("text", val);
        assertEquals("no text", model.getStringValue(model.getNextSibling(model.getFirstChild(doc))));
        // don't test the value of target; it may not have changed.
        
        target = model.getLastChild(doc); // pi
        val = model.replaceValue(target, "no data");
        assertEquals("data", val);
        assertEquals("no data", model.getStringValue(model.getLastChild(doc)));
        assertEquals("no data", model.getStringValue(target));
        
        // TODO : test breaking things (expect an error):
        // replacing an attribute with an element, or text with an attribute,
        // for instance.
        
        // reset the document; it got confused up there
        doc = model.getFirstChildElement(createSimpleAllKindsDocument(context.newFragmentBuilder()));
        factory = model.getFactory(doc);
        // replace node: attribute, child-node (text, element, comment, pi)
        // replacement nodes
        N att = factory.createAttribute("", "new", "", "none");
        N elem = factory.createElement("", "elem", "");
        N text = factory.createText("lorem ipsum");
        
        // attribute test
        target = model.getAttribute(doc, "", "att");
        N old = model.replace(target, att);
        assertEquals(target, old);
        assertNotNull(model.getAttribute(doc, "", "new"));
        assertEquals("none", model.getAttributeStringValue(doc, "", "new"));
        assertEquals(doc, model.getParent(att));
        
        // replace comment with element (like uncommenting?)
        target = model.getFirstChild(doc);
        old = model.replace(target, elem);
        assertEquals(target, old);
        assertEquals(elem, model.getFirstChild(doc));
        assertEquals(doc, model.getParent(elem));

        // replace text with text (which is probably common enough)
        target = model.getNextSibling(model.getFirstChild(doc));
        old = model.replace(target, text);
        assertEquals(target, old);
        assertEquals(text, model.getNextSibling(model.getFirstChild(doc)));
        assertEquals(doc, model.getParent(text));
        
        // use the removed text node to replace the new element node (common enough, i think)
        // note that we have now created a tree with two neighboring text nodes, which is bad
        // xdm.  not sure where that's supposed to get fixed.
        text = old;
        target = model.getFirstChild(doc);
        old = model.replace(target, text);
        assertEquals(target, old);
        assertEquals(text, model.getFirstChild(doc));
        assertEquals(doc, model.getParent(text));
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            // TODO: is it *really* meaningful to do this?
            // it's a good way to produce ill-formed documents
            // we also have the problem of creating a namespace node,
            // which is going to require using a fragment builder.
        }
    }
    
    @Test
    public void copy()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        assertNotNull(builder);
        
        // first, use the events matcher to copy a doc as it's constructed.
        Events<N> matcher = new Events<N>(builder);
        if (!context.isSupported(Feature.DOCUMENT_URI))
            matcher.ignoreDocumentURI();
        if (!context.isSupported(Feature.NAMESPACE_AXIS))
            matcher.ignoreExtraNamespaceDeclarations();
        matcher.record();
        N doc = createSimpleAllKindsDocument(matcher);
        
        MutableModel<N> model = context.getMutableContext().getModel();
        
        // now, use the mutable copy (which is pretty equivalent to stream, effectively)
        N doc2 = model.copyNode(doc, true);
        
        // now verify that the copy matches the original
        matcher.match();
        model.stream(doc2, true, matcher);
        
        N mark = model.copyNode(doc, false);
        // can't assert equals.  they're not "equal," even though they're
        // copies of each other.
        if (context.isSupported(Feature.DOCUMENT_URI))
            assertEquals(model.getDocumentURI(doc), model.getDocumentURI(mark)); // all we can really do.
        
        mark = model.getFirstChild(doc);
        N copy = model.copyNode(mark, false);
        assertEquals(model.getNamespaceURI(mark), model.getNamespaceURI(copy));
        assertEquals(model.getLocalName(mark), model.getLocalName(copy));
        assertEquals(model.getPrefix(mark), model.getPrefix(copy)); // necessarily true?
        
        mark = model.getAttribute(mark, "", "att");
        copy = model.copyNode(mark, false);
        assertEquals(model.getNamespaceURI(mark), model.getNamespaceURI(copy));
        assertEquals(model.getLocalName(mark), model.getLocalName(copy));
        assertEquals(model.getPrefix(mark), model.getPrefix(copy)); // necessarily true?
        assertEquals(model.getStringValue(mark), model.getStringValue(copy));
        
        if (context.isSupported(Feature.NAMESPACE_AXIS))
        {
            // TODO: namespace [ns = ns] ???
            // maybe we should use namespace bindings for this?
            // and just ensure that prefixes we want are mapped?
            // in which case, we don't care about the axis.
        }
        
        mark = model.getFirstChild(model.getFirstChild(doc)); // comment
        copy = model.copyNode(mark, false);
        assertEquals(model.getStringValue(mark), model.getStringValue(copy));
        
        mark = model.getNextSibling(mark); // text
        copy = model.copyNode(mark, false);
        assertEquals(model.getStringValue(mark), model.getStringValue(copy));
        
        mark = model.getNextSibling(mark);
        copy = model.copyNode(mark, false);
        assertEquals(model.getLocalName(mark), model.getLocalName(copy));
        assertEquals(model.getStringValue(mark), model.getStringValue(copy));
    }
}
