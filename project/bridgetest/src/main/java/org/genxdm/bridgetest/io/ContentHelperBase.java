package org.genxdm.bridgetest.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.genxdm.Cursor;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.content.Attrib;
import org.genxdm.bridgekit.content.BaseContentHelper;
import org.genxdm.bridgekit.content.ContentHelper;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.junit.Test;

public abstract class ContentHelperBase<N>
    extends TestBase<N>
{

    @Test
    public void constructionViaHelper()
    {
        // verify we can construct things well-formed
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        
        assertNotNull(builder); // never null
        assertNull(builder.getNode()); // should be null if list is empty
        assertNotNull(builder.getNodes()); // list is never null
        assertTrue(builder.getNodes().isEmpty());

        // make a very simple document (not a sequence of docs; we don't care about that)
//        builder.startDocument(null, null);
//        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
//        builder.endElement();
//        builder.endDocument();
        ContentHelper helper = new BaseContentHelper(builder);
        // simple doc start
        helper.start();
        // empty root element, in default namespace
        helper.simplexElement(null, "element", null, null, null);
        // end
        helper.end();

        List<N> nodes = builder.getNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        
        // reset, do it with the four-argument start()
        builder.reset();
        helper.reset();
        assertTrue(builder.getNodes().isEmpty());
        helper.start(null, "element", null, null);
        helper.endComplex(); // don't yet support unwind
        helper.end();
        nodes = builder.getNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 1);
        
        builder.reset();
        nodes = builder.getNodes();
        assertTrue(nodes.isEmpty());
//        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
//        builder.endElement();
//        builder.startElement(XMLConstants.NULL_NS_URI, "element", XMLConstants.DEFAULT_NS_PREFIX);
//        builder.endElement();
        // make a sequence of elements (this should be legal with the helper)
        helper.startComplex(null, "element", null, null);
        helper.endComplex();
        helper.simplexElement(null, "element", null, null, null);
        helper.simpleElement(null, "element", null);

        nodes = builder.getNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
        assertEquals(nodes.size(), 3);

        helper.reset();
        builder.reset();
        nodes = builder.getNodes();
        assertTrue(nodes.isEmpty());
        // this time, let's make a more complex document.
        // then we're going to navigate it and verify that it's working.
        // our target is in a comment below.
        
        // do it twice with two prefixes:
        String[] prefixes = { "", OPT_PREFIX };
        for (String pfx : prefixes)
        {
            Map<String, String> nsMap = new HashMap<String, String>();
            nsMap.put(pfx, XMLNS);
            Set<Attrib> atts = new HashSet<Attrib>();
            atts.add(helper.newAttribute(NS_ATT, NS));
            atts.add(helper.newAttribute(VERSION_ATT, VERSION));
            
            // use the simple form (we'll do this twice)
            //<root xmlns="http://www.example.org/namespaces/xyzzy/1.0"
            //      ns="http://www.example.org/namespaces/xyzzy/1.0/simple"
            //      version="1.0.0">
            if (pfx.equals(""))
            {
                helper.start();
                helper.startComplex(XMLNS, ROOT, nsMap, atts);
            }
            else
                helper.start(XMLNS, ROOT, nsMap, atts);
            atts.clear(); // safe: we've already processed them all. :-)
            
            //<plugh name="digit" start="0" end="9" />
            atts.add(helper.newAttribute(NAME_ATT, DIGIT));
            atts.add(helper.newAttribute(START_ATT, CH0));
            atts.add(helper.newAttribute(END_ATT, CH9));
            helper.simplexElement(XMLNS, ELEM_NAME, null, atts, null);
            atts.clear();
            
            //<plugh name="ascii-alpha">
            //  <plugh start="A" end="Z" />
            //  <plugh start="a" end="z" />
            //</plugh>
            atts.add(helper.newAttribute(NAME_ATT, ASCII));
            helper.startComplex(XMLNS, ELEM_NAME, null, atts);
            atts.clear();
            atts.add(helper.newAttribute(START_ATT, CHA));
            atts.add(helper.newAttribute(END_ATT, CHZ));
            helper.simplexElement(XMLNS, ELEM_NAME, null, atts, null);
            atts.clear();
            atts.add(helper.newAttribute(START_ATT, CHa));
            atts.add(helper.newAttribute(END_ATT, CHz));
            helper.simplexElement(XMLNS, ELEM_NAME, null, atts, null);
            atts.clear();
            helper.endComplex(); // plugh@name=ascii
            
            //<plugh name="ascii-alphanum">
            //  <plugh ref="ascii-alpha" />
            //  <plugh ref="digit" />
            //</plugh>
            atts.add(helper.newAttribute(NAME_ATT, ASCII_NUM));
            helper.startComplex(XMLNS, ELEM_NAME, null, atts);
            atts.clear();
            atts.add(helper.newAttribute(REF_ATT, ASCII));
            helper.simplexElement(XMLNS, ELEM_NAME, null, atts, null);
            atts.clear();
            atts.add(helper.newAttribute(REF_ATT, DIGIT));
            helper.simplexElement(XMLNS, ELEM_NAME, null, atts, null);
            atts.clear();
            helper.endComplex(); // plugh@name=ascii_num
            
            //<simple>just text here</simple>
            helper.simpleElement(XMLNS, SIMPLE_NAME, SIMPLE);
    
            //</root>
            helper.endComplex(); // root
            helper.end();
            checkTree(builder, context, pfx);
            
            builder.reset();
            helper.reset();
        }
    }
    
    private void checkTree(FragmentBuilder<N> builder, ProcessingContext<N> context, String prefix)
    {
        N doc = builder.getNode();
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        
        // check the root element
        cursor.moveToFirstChildElement();
        assertEquals(ROOT, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(NS, cursor.getAttributeStringValue("", NS_ATT));
        assertEquals(VERSION, cursor.getAttributeStringValue("", VERSION_ATT));
        
        // check plugh@name=digit
        cursor.moveToFirstChildElement();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(DIGIT, cursor.getAttributeStringValue("", NAME_ATT));
        assertEquals(CH0, cursor.getAttributeStringValue("", START_ATT));
        assertEquals(CH9, cursor.getAttributeStringValue("", END_ATT));
        
        // check plugh@name=ascii and children
        cursor.moveToNextSibling();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(ASCII, cursor.getAttributeStringValue("", NAME_ATT));
        cursor.moveToFirstChildElement();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(CHA, cursor.getAttributeStringValue("", START_ATT));
        assertEquals(CHZ, cursor.getAttributeStringValue("", END_ATT));
        cursor.moveToNextSibling();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(CHa, cursor.getAttributeStringValue("", START_ATT));
        assertEquals(CHz, cursor.getAttributeStringValue("", END_ATT));
        cursor.moveToParent();
        
        // check plugh@name=ascii_num and children
        cursor.moveToNextSibling();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(ASCII_NUM, cursor.getAttributeStringValue("", NAME_ATT));
        cursor.moveToFirstChildElement();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(ASCII, cursor.getAttributeStringValue("", REF_ATT));
        cursor.moveToNextSibling();
        assertEquals(ELEM_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(DIGIT, cursor.getAttributeStringValue("", REF_ATT));
        cursor.moveToParent();
        
        // check simple and its text
        cursor.moveToNextSibling();
        assertEquals(SIMPLE_NAME, cursor.getLocalName());
        assertEquals(XMLNS, cursor.getNamespaceURI());
        assertEquals(prefix, cursor.getPrefix());
        assertEquals(SIMPLE, cursor.getStringValue());
        
        cursor.moveToParent(); // root
        assertEquals(ROOT, cursor.getLocalName());
    }

    private static final String ROOT = "root";
    private static final String XMLNS = "http://www.example.org/namespaces/xyzzy/1.0";
    private static final String OPT_PREFIX = "tns";
    private static final String NS = "http://www.example.org/namespaces/xyzzy/1.0/simple";
    private static final String VERSION = "1.0.0";
    
    private static final String ELEM_NAME = "plugh";
    private static final String SIMPLE_NAME = "strange";
    
    private static final String NS_ATT = "ns";
    private static final String VERSION_ATT = "version";
    private static final String NAME_ATT = "name";
    private static final String START_ATT = "start";
    private static final String END_ATT = "end";
    private static final String REF_ATT = "ref";
    
    private static final String DIGIT = "digit";
    private static final String ASCII = "ascii-alpha";
    private static final String ASCII_NUM = "ascii-aphanum";
    private static final String CH0 = "0";
    private static final String CH9 = "9";
    private static final String CHA = "A";
    private static final String CHZ = "Z";
    private static final String CHa = "a";
    private static final String CHz = "z";
    private static final String SIMPLE = "just text here";
    
/*
<root xmlns="http://www.example.org/namespaces/xyzzy/1.0"
       ns="http://www.example.org/namespaces/xyzzy/1.0/simple"
       version="1.0.0">
  <plugh name="digit" start="0" end="9" />
  
  <plugh name="ascii-alpha">
    <plugh start="A" end="Z" />
    <plugh start="a" end="z" />
  </plugh>
  
  <plugh name="ascii-alphanum">
    <plugh ref="ascii-alpha" />
    <plugh ref="digit" />
  </plugh>
  
  <simple>just text here</simple>
  
</root>
*/
}
