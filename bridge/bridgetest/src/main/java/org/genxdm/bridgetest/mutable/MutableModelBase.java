package org.genxdm.bridgetest.mutable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.bridgetest.utilities.Events;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableContext;
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
        for (N n : sids)
        {
            i++;
        }
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
        
        //TODO
    }
    
    @Test
    public void namespace()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        
        //TODO
    }
    
    @Test
    public void siblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        // insertBefore, insertAfter
        
        //TODO
    }

    @Test
    public void delete()
    {
        ProcessingContext<N> context = newProcessingContext();
        N doc = createSimpleAllKindsDocument(context.newFragmentBuilder());
        
        //TODO
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
        
        // TODO
        // for shallow copy, just verify each node type ('cept namespace?)
        // document (has uri)
        // element "doc"
        // attribute att=value
        // [namespace? ns=ns
        // comment "comment"
        // text "text"
        // pi "target" "data"
    }
    
///**
// * Removes a node from the tree.  Corresponds to XQuery Update Facility
// * delete.
// *
// * @param target
// *            The node to be removed.  If null, no action will be taken.
// *            If the node has no parent, no action will be taken.
// * 
// * @return The node that has been removed; if null, then no node was removed.
// **/
//N delete(final N target);
//
///**
// * Inserts the specified node as the following sibling of the supplied
// * target node.
// * Corresponds to XQuery Update Facility insertAfter.  This version
// * is a convenience method that takes a single node rather than requiring
// * construction of a list to hold a single-element sequence.
// * 
// * @param target
// *            The node which will become the preceding sibling of the new
// *            node.  Must be a text, element, comment, or processing
// *            instruction node.  May not be null.
// * @param content
// *            The node to be added.  Must be a text, element,
// *            comment, or processing instruction node.  May not be null.
// **/
//void insertAfter(final N target, final N content);
//
///**
// * Inserts the specified node sequence, in order, as the following siblings 
// * of the supplied target node.
// * Corresponds to XQuery Update Facility insertAfter.
// * 
// * @param target
// *            The node which will become the preceding sibling of the last
// *            node in the supplied sequence.  Must be a text, element, 
// *            comment, or processing instruction node.  May not be null.
// * @param content
// *            The sequence to be added.  May not be null,
// *            but may be an empty sequence (in which case nothing happens).
// *            Contents must conform to the contract for insertAfter.
// **/
//void insertAfter(final N target, final Iterable<N> content);
//
///**
// * Inserts an attribute node into the attribute axis of an element.
// * Corresponds to XQuery Update Facility insertAttributes.  This is a
// * convenience method, permitting a single node rather than requiring
// * construction of a single-element sequence container.
// * 
// * @param element
// *            The element that will hold the attribute.  May not be null.
// *            Must be an element node.
// * @param attribute
// *            The attribute to be inserted.  May not be null.  Must be
// *            an attribute node.
// **/
//void insertAttribute(final N element, final N attribute);
//
///**
// * Inserts an attribute node into the attribute axis of an element.
// * Corresponds to XQuery Update Facility insertAttributes.
// * 
// * @param element
// *            The element that will hold the attributes.  May not be null.
// *            Must be an element node.
// * @param attributes
// *            The list of attributes to be inserted.  May not be null.  
// *            May be empty.  Contents must be attribute nodes.
// **/
//void insertAttributes(final N element, final Iterable<N> attributes);
//
///**
// * Inserts the specified node as the preceding sibling of the supplied
// * target node.
// * Corresponds to XQuery Update Facility insertBefore.  This version
// * is a convenience method that takes a single node rather than requiring
// * construction of a list to hold a single-element sequence.
// * 
// * @param target
// *            The node which will become the following sibling of the new
// *            node.  Must be a text, element, comment, or processing
// *            instruction node.  May not be null.
// * @param content
// *            The node to be added.  Must be a text, element,
// *            comment, or processing instruction node.  May not be null.
// **/
//void insertBefore(final N target, final N content);
//
///**
// * Inserts the specified node sequence, in order, as the preceding siblings 
// * of the supplied target node.
// * Corresponds to XQuery Update Facility inserBefore.
// * 
// * @param target
// *            The node which will become the following sibling of the last
// *            node in the supplied sequence.  Must be a text, element, 
// *            comment, or processing instruction node.  May not be null.
// * @param content
// *            The sequence to be added.  May not be null,
// *            but may be an empty sequence (in which case nothing happens).
// *            Contents must conform to the contract for insertBefore.
// **/
//void insertBefore(final N target, final Iterable<N> content);
//
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
