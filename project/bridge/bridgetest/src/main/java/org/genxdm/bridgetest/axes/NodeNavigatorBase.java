package org.genxdm.bridgetest.axes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        
        // Maybe test other node types (document, comment, pi, even ns)
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
        
        doc = model.getFirstChildElement(doc);
        assertNotNull(doc); // move to document element.
        
        N c1 = model.getFirstChild(doc); // a text node.
        N c2 = model.getFirstChildElement(doc); // the path node
        N c3 = model.getFirstChildElementByName(doc, XMLConstants.NULL_NS_URI, "target"); // the first target node
        assertNotNull(c1);
        assertNotNull(c2);
        assertNotNull(c3);
        assertTrue(c1 != c2);
        assertTrue(c1 != c3);
        assertTrue(c2 != c3);
        assertEquals(NodeKind.TEXT, model.getNodeKind(c1));
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(c2));
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(c3));
        
        c2 = model.getFirstChild(c1);
        assertNull(c2);
        
        c1 = model.getFirstChild(c3);
        c2 = model.getLastChild(c3);
        assertNotNull(c1);
        assertNotNull(c2);
        assertTrue(c1 != c2);
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
        
        N parent = model.getFirstChildElementByName(model.getFirstChildElement(doc), "http://www.genxdm.org/nonsense", "nstest");
        assertNotNull(parent);
        parent = model.getFirstChildElement(parent);
        N fc = model.getFirstChild(parent);
        N fce = model.getFirstChildElement(parent);
        N fcebn = model.getFirstChildElementByName(parent, "http://great.underground.empire/adventure", "magicword");
        assertNotNull(fc);
        assertNotNull(fce);
        assertNotNull(fcebn);
        assertEquals(NodeKind.TEXT, model.getNodeKind(fc));
        
        N walker = model.getNextSibling(fc);
        assertNotNull(walker);
        assertEquals(fce, walker);
        
        walker = model.getNextSiblingElement(fc);
        assertNotNull(walker);
        assertEquals(fce, walker);
        
        walker = model.getNextSiblingElementByName(fc, "http://great.underground.empire/adventure", "magicword");
        assertNotNull(walker);
        assertEquals(fcebn, walker);
        
        walker = model.getNextSibling(walker);
        assertNotNull(walker);
        walker = model.getPreviousSibling(walker);
        assertNotNull(walker);
        assertEquals(fcebn, walker);
        
        walker = model.getNextSibling(walker);
        assertNotNull(walker);
        assertEquals(NodeKind.TEXT, model.getNodeKind(walker));
        walker = model.getNextSibling(walker);
        assertNotNull(walker);
        assertEquals(NodeKind.COMMENT, model.getNodeKind(walker));
        walker = model.getNextSibling(walker);
        assertNotNull(walker);
        assertEquals(NodeKind.TEXT, model.getNodeKind(walker));
        walker = model.getNextSibling(walker);
        assertNotNull(walker);
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, model.getNodeKind(walker));
        
        fce = model.getAttribute(model.getFirstChildElement(doc), XMLConstants.NULL_NS_URI, "name");
        assertNotNull(fce);
        walker = model.getNextSibling(fce);
        assertNull(walker);
        walker = model.getPreviousSibling(fce);
        assertNull(walker);

        // TODO: axiom doesn't like this at all.  i don't know if it's
        // a problem with the test, or with axiom, and if it's a problem
        // with axiom, whether it's a problem with the bridge or the underlying
        // tree model.  in any event, if we ask for the namespace bound to ""
        // here, we *should* get something different; we don't get anything,
        // because we're getting a generated prefix (via axis) instead.
        // So, make a better namespaces test, somewhere.
//        fce = getNamespaceNode(model, parent, XMLConstants.DEFAULT_NS_PREFIX);
        // this, on the other hand, works.
        fce = getNamespaceNode(model, parent, "grue");
        assertNotNull(fce);
        walker = model.getNextSibling(fce);
        assertNull(walker);
        walker = model.getPreviousSibling(fce);
        assertNull(walker);
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
        
        el = model.getFirstChildElementByName(model.getFirstChildElement(doc), "http://www.genxdm.org/nonsense", "nstest");
        assertNotNull(el);
        el = model.getFirstChildElement(el);
        assertNotNull(el);
        n = getNamespaceNode(model, el, "grue");
        assertNotNull(n);
        p = model.getParent(n);
        assertNotNull(p);
        assertEquals(el, p);
        p = model.getRoot(n);
        assertNotNull(p);
        assertEquals(doc, p);
        
        n = model.getFirstChildElementByName(el, "http://great.underground.empire/adventure", "magicword");
        assertNotNull(n);
        n = model.getNextSibling(model.getNextSibling(n)); // comment node
        assertNotNull(n);
        assertEquals(NodeKind.COMMENT, model.getNodeKind(n));
        p = model.getParent(n);
        assertNotNull(p);
        assertEquals(el, p);
        p = model.getRoot(n);
        assertNotNull(p);
        assertEquals(doc, p);
        
        n = model.getNextSibling(model.getNextSibling(n)); // pi
        assertNotNull(n);
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, model.getNodeKind(n));
        p = model.getParent(n);
        assertNotNull(p);
        assertEquals(el, p);
        p = model.getRoot(n);
        assertNotNull(p);
        assertEquals(doc, p);
    }
    
}
