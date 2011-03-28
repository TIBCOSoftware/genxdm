package org.genxdm.bridgetest.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        NodeFactory<N> factory = context.getMutableContext().getNodeFactory();
        MutableModel<N> mutant = context.getMutableContext().getModel();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
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
        for (N n : sids) { i++; }
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
        NodeFactory<N> factory = context.getMutableContext().getNodeFactory();
        MutableModel<N> model = context.getMutableContext().getModel();
        
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
        for (N a : atts) { i++; }
        assertEquals(2, i);
        
        model.insertAttributes(doc, attlist);
        i = 0;
        atts = model.getAttributeAxis(doc, false);
        for (N a : atts) { i++; }
        assertEquals(4, i);
    }
    
    @Test
    public void namespace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        
        // note: not having a namespace axis isn't an excuse
        // namespace *support* is required, it's just that namespaces
        // are allowed to be metadata, not nodes
        
        //TODO
    }
    
    @Test
    public void siblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        NodeFactory<N> factory = context.getMutableContext().getNodeFactory();
        MutableModel<N> model = context.getMutableContext().getModel();
        
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
        
        
        // TODO: now, sequences
        
        // TODO: we should test putting things in as siblings of the
        // document element, too, but there's a world of potential hurt,
        // there.  some apis won't permit text nodes (even ignorable whitespace,
        // per the specification) as children of document.  an actual *document*
        // node shouldn't let you put a second element there, but the xdm document
        // in fact represents an xml entity, more accurately (but not even
        // that, really; it's just the notional wrapper around a sequence,
        // if you want to get down to cases like bare attributes).
    }

    @Test
    public void delete()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        MutableModel<N> model = context.getMutableContext().getModel();
        
        N target = model.getFirstChild(model.getFirstChildElement(doc));
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
        // TODO: namespace?
        target = model.getAttribute(marker, "", "att");
        result = model.delete(target); // delete att
        assertEquals(target, result);
        // at this point, we could move the marker back to document, but why?
        result = model.delete(marker); // delete element
        assertEquals(marker, result);
        // can't delete the document
    }
    
    @Test
    public void replace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        // replace, replaceValue
        
        //TODO
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
    
///**
// * Inserts a namespace binding into the namespace axis of an element.
// * This is an immediate-effect API equivalent to the delayed-effect
// * API specified in the XQuery Update Facility, propagateNamespace.
// * 
// * @param element
// *            The element that will hold the namespace binding.  Must be
// *            an element node.  May not be null.
// * @param prefix
// *            The prefix (local-name part of the dm:name) of the namespace node.
// *            May not be null, but may be the empty string (default prefix).
// * @param uri
// *            The dm:string-value of the namespace node (the namespace URI).
// *            May not be null, but may be the empty string (default/global namespace,
// *            or namespace un-definition for Namespaces in XML 1.1).
// **/
//N insertNamespace(final N element, final String prefix, final String uri);
//
///**
// * Replaces a node.  Corresponds to XQuery Update Facility replaceNode
// * (except that it's a single node replacement, not a sequence).
// *
// * @param target
// *            The old node to be replaced.  May be any node type except
// *            document; must not be null.
// * @param content
// *            The new node that will replace the old node.  When the target
// *            node is an attribute, or namespace, this node must
// *            be of the same type.  When the target is element, text,
// *            comment, or processing instruction, this node must be one
// *            of those four kinds (not a document, attribute, or namespace).
// *            May not be null (use delete).
// * 
// * @return The node that was removed; if null, no action was taken.
// */
//N replace(final N target, final N content); //replace
//
///**
// * Replaces the value of a node with a new value.  Corresponds to
// * XQuery Update Facility replaceValue.
// *
// * @param target The node for which the value is to be replaced.  Must
// *               be a text, attribute, comment, or processing instruction
// *               node.  May not be null.
// * @param value The replacement value.  If null, the empty string will
// *              be used instead.
// * @return the original textual value of the node. If null, then no
// *         replacement was possible.
// **/
//String replaceValue(final N target, final String value);
}
