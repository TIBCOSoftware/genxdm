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
package org.genxdm.bridgetest.axes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.XMLConstants;

import org.genxdm.Cursor;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.junit.Test;

/**
 * Base Test Class for Cursor unit tests for the Navigation methods. 
 *
 * @param <N>
 */
public abstract class NavigatorBase<N>
    extends TestBase<N>

{

    @Test
    public void attributes()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);

        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        boolean isMoved = cursor.moveToFirstChildElement(); // project element
        assertTrue(isMoved);
        
        // project element has two attributes: name and default
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "name");
        assertTrue(isMoved);
        
        isMoved = cursor.moveToAttribute("'", "name");
        assertFalse(isMoved); // no such attribute exists
        
        cursor.moveToParent();
        
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "default");
        assertTrue(isMoved);
        
        isMoved = cursor.moveToAttribute( XMLConstants.NULL_NS_URI, "default");
        assertFalse(isMoved); // the cursor should not move to itself.
        
        cursor.moveToParent();
        
        isMoved = cursor.moveToLastChild(); // a text node?
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());
        
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "name");
        assertFalse(isMoved); // text nodes have no attributes.
        
        isMoved = cursor.moveToPreviousSibling();// nstest node
        assertTrue(isMoved);
        
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "name");
        assertFalse(isMoved); // no such attribute
        
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "xmlns");
        assertFalse(isMoved);// the xmlns 'attribute' is not an attribute!

    }

    @Test
    public void ids()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        boolean isMoved = cursor.moveToLastChild();
        isMoved = cursor.moveToLastChild();
        assertTrue(isMoved);// text node inside closing tag of doc element
        
        isMoved = cursor.moveToElementById("project.class.path");
        assertTrue(isMoved);
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "id");
        assertTrue(isMoved);
        assertEquals("project.class.path", cursor.getStringValue());
        
        isMoved = cursor.moveToElementById("project.output");
        assertTrue(isMoved);
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "id");
        assertTrue(isMoved);
        assertEquals("project.output", cursor.getStringValue());
        
        cursor.moveToParent();
        
        isMoved = cursor.moveToElementById("this.id.does.not.exist");
        assertFalse(isMoved);
    }

    @Test
    public void children()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        assertNotNull(cursor);
        
        boolean isMoved = cursor.moveToFirstChildElement();
        assertTrue(isMoved); // move to document element.
        
        isMoved = cursor.moveToFirstChild(); // a text node.
        assertTrue(isMoved);
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());

        isMoved = cursor.moveToFirstChild();
        assertFalse(isMoved);
        
        cursor.moveToParent(); 
        
        isMoved = cursor.moveToFirstChildElement(); // the path node
        assertTrue(isMoved);
        assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
        
        cursor.moveToParent();
        
        isMoved = cursor.moveToFirstChildElementByName(XMLConstants.NULL_NS_URI, "target"); // the first target node
        assertTrue(isMoved);
        assertEquals(NodeKind.ELEMENT, cursor.getNodeKind());
       
        isMoved = cursor.moveToFirstChild();
        assertTrue(isMoved);

        cursor.moveToParent();
        
        isMoved = cursor.moveToFirstChildElementByName(XMLConstants.NULL_NS_URI, "pathelement"); // descendant, not child; verify single-stepping
        assertFalse(isMoved);
        
        isMoved = cursor.moveToLastChild();
        assertTrue(isMoved);
    }

    @Test
    public void siblings()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        
        Cursor cursor2 = context.newCursor(doc);
        
        assertNotNull(cursor);
        
        cursor.moveToFirstChildElement();
        cursor2.moveToFirstChildElement();
        
        boolean isMoved = cursor.moveToFirstChildElementByName("http://www.genxdm.org/nonsense", "nstest");
        cursor2.moveToFirstChildElementByName("http://www.genxdm.org/nonsense", "nstest");
        assertTrue(isMoved);
        
        isMoved = cursor.moveToFirstChildElement(); //gue:zork
        cursor2.moveToFirstChildElement();//gue:zork
        
        isMoved = cursor.moveToFirstChild();// Text Node
        assertTrue(isMoved);
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());
        
        cursor2.moveToFirstChild();// Text Node
        assertEquals( cursor.getNodeId(), cursor2.getNodeId()); 
               
        cursor.moveToNextSibling();// grue:light Node
        
        cursor2.moveToParent(); //gue:zork
        cursor2.moveToFirstChildElement();// grue:light Node
        
        assertEquals( cursor.getNodeId(), cursor2.getNodeId());
        
        cursor.moveToNextSiblingElement(); //magicword Node
                
        cursor2.moveToParent();
        cursor2.moveToFirstChildElementByName("http://great.underground.empire/adventure", "magicword"); //axis2ns3:magicword Node
        
        assertEquals( cursor.getNodeId(), cursor2.getNodeId());
        
        isMoved = cursor.moveToNextSibling();
        assertTrue(isMoved);
        cursor.moveToPreviousSibling();
        assertTrue(isMoved);
        assertEquals( cursor.getNodeId(), cursor2.getNodeId());
        
        isMoved = cursor.moveToNextSibling();
        assertTrue(isMoved);
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());
        
        isMoved = cursor.moveToNextSibling();
        assertTrue(isMoved);
        assertEquals(NodeKind.COMMENT, cursor.getNodeKind());

        isMoved = cursor.moveToNextSibling();
        assertTrue(isMoved);
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());

        isMoved = cursor.moveToNextSibling();
        assertTrue(isMoved);
        assertEquals(NodeKind.PROCESSING_INSTRUCTION, cursor.getNodeKind());

        cursor.moveToRoot();
        cursor.moveToFirstChildElement();
        isMoved = cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "name");
        assertTrue(isMoved);

        isMoved = cursor.moveToNextSibling();
        assertFalse(isMoved);
        isMoved = cursor.moveToPreviousSibling();
        assertFalse(isMoved);

        // TODO: removed moveTo, which killed moveToNamespace hack.
        // figure out a way to test this without that hack.
        //namespace
//        if (context.isSupported(Feature.NAMESPACE_AXIS))
//        {
//            N nstest = context.getModel().getChildElementsByName(context.getModel().getFirstChild(doc), "http://www.genxdm.org/nonsense", "nstest").iterator().next();
//            cursor.moveTo(getNamespaceNode(context.getModel(), nstest, "gue"));
//            assertEquals(NodeKind.NAMESPACE, cursor.getNodeKind());
//            assertFalse(cursor.hasNextSibling());
//            assertFalse(cursor.moveToNextSibling());
//            assertFalse(cursor.hasPreviousSibling());
//            assertFalse(cursor.moveToPreviousSibling());
//        }
    }

    @Test
    public void ancestors()
    {
        ProcessingContext<N> context = newProcessingContext();
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);
        
        assertNotNull(doc);
        Cursor cursor = context.newCursor(doc);
        Cursor cursor2 = context.newCursor(doc);
        assertNotNull(cursor);
        
        // document node
        boolean isMoved = cursor.moveToParent();
        assertFalse(isMoved); // doc has no parent.
        
        cursor.moveToRoot();
        assertEquals(cursor.getNodeId(), cursor2.getNodeId());
        
        // element node
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElement());
        
        cursor.moveToParent();
        cursor.moveToRoot();
        assertEquals(cursor.getNodeId(), cursor2.getNodeId());

        // element node
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElement());

        // element node
        assertTrue(cursor2.moveToFirstChildElement());
        assertTrue(cursor2.moveToFirstChildElement());

        // attribute node
        assertTrue(cursor.moveToAttribute(XMLConstants.NULL_NS_URI, "id"));
        assertTrue(cursor.moveToParent());
        
        assertEquals(cursor.getNodeId(), cursor2.getNodeId());
        
        assertTrue(cursor.moveToParent());
        assertTrue(cursor.moveToParent());
        
        cursor2.moveToRoot();

        assertEquals(cursor.getNodeId(), cursor2.getNodeId());

        
        // text node
        // element node
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChild());
        
        assertEquals(NodeKind.TEXT, cursor.getNodeKind());
        
        assertTrue(cursor.moveToParent());
        cursor.moveToRoot();
        assertEquals(cursor.getNodeId(), cursor2.getNodeId());

        // TODO: removed moveTo, so this doesn't work. find a different way.
        //namespace
//        if (context.isSupported(Feature.NAMESPACE_AXIS))
//        {
//            N nstest = context.getModel().getChildElementsByName(context.getModel().getFirstChild(doc), "http://www.genxdm.org/nonsense", "nstest").iterator().next();
//            cursor.moveTo(getNamespaceNode(context.getModel(), nstest, "gue"));
//            assertEquals(NodeKind.NAMESPACE, cursor.getNodeKind());
//            assertTrue(cursor.moveToParent());
//            cursor.moveToRoot();
//            assertTrue(cursor.getNodeId().equals(cursor2.getNodeId()));
//        }

        // element node
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElementByName("http://www.genxdm.org/nonsense", "nstest"));
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElementByName("http://great.underground.empire/adventure", "magicword"));
        assertTrue(cursor.moveToNextSibling());//Text Node
        assertTrue(cursor.moveToNextSibling()); // Comment Node
        assertEquals(NodeKind.COMMENT, cursor.getNodeKind());
        assertTrue(cursor.moveToParent());
        cursor.moveToRoot();
        assertEquals(cursor.getNodeId(), cursor2.getNodeId());

        // element node
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElementByName("http://www.genxdm.org/nonsense", "nstest"));
        assertTrue(cursor.moveToFirstChildElement());
        assertTrue(cursor.moveToFirstChildElementByName("http://great.underground.empire/adventure", "magicword"));
        assertTrue(cursor.moveToNextSibling());//Text Node
        assertTrue(cursor.moveToNextSibling()); // Comment Node
        assertTrue(cursor.moveToNextSibling());//Text Node
        assertTrue(cursor.moveToNextSibling()); // PI

        assertEquals(NodeKind.PROCESSING_INSTRUCTION, cursor.getNodeKind());
        assertTrue(cursor.moveToParent());
        cursor.moveToRoot();
        assertEquals(cursor.getNodeId(), cursor2.getNodeId());
    }

}
