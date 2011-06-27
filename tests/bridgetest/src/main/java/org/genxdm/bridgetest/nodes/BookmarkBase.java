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
package org.genxdm.bridgetest.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.genxdm.Cursor;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.nodes.Bookmark;
import org.junit.Test;

/**
 * 
 *
 * @param <N>
 */
public abstract class BookmarkBase<N>
    extends TestBase<N>
{

    @Test
    public void testBookmarkCreation()
    {
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);        
        assertNotNull(doc);
        
        Model<N> model  = context.getModel();
        assertNotNull(doc);
        
        //Get Node by Node Navigation and obtain a bookmark
        N node1 = model.getFirstChildElement(doc);
        node1 = model.getFirstChildElement(node1);
        node1 = model.getNextSiblingElement(node1); // Navigate to the fileset element
        assertNotNull(node1);        
        
        Bookmark<N> bookmark1 = context.bookmark(node1);
        assertNotNull(bookmark1);        
        assertEquals(bookmark1.getNodeId(), model.getNodeId(node1));//To test Bookmark is created on the same node 
        
        List<N> nodes = bookmark1.getNodes();
        assertEquals( nodes.size(), 1); // Bookmark returns only one Node.
        
        //Get Node by Id and obtain a bookmark
        N node2 = model.getElementById(doc, "project.output"); // Navigate to the fileset element
        assertNotNull(node2);   

        Bookmark<N> bookmark2 = context.bookmark(node2);
        assertNotNull(bookmark2); 
        assertEquals(bookmark2.getNodeId(), model.getNodeId(node2)); //To test Bookmark is created on the same node
        
        //Move the cursor to the Node and obtain a bookmark
        Cursor<N> cursor = context.newCursor(doc);
        cursor.moveToFirstChildElement();
        cursor.moveToFirstChildElement();
        cursor.moveToNextSiblingElement(); // Navigate to the fileset element
        
        Bookmark<N> bookmark3 = cursor.bookmark();
        assertNotNull(bookmark2); 
        assertEquals(bookmark3.getNodeId(), cursor.getNodeId());//To test Bookmark is created on the same node
        Object nodeIdBefore = bookmark3.getNodeId();
        
        assertTrue(cursor.moveToNextSibling());        
        
        // Test that moving cursor doest not change the bookmark
        assertEquals(nodeIdBefore, bookmark3.getNodeId());
        assertFalse( bookmark3.getNodeId().equals(cursor.getNodeId()));  
        
        // Test that cursor is on the same 
        assertEquals(NodeKind.ELEMENT, bookmark1.getNodeKind());
        assertEquals(NodeKind.ELEMENT, bookmark2.getNodeKind());
        assertEquals(NodeKind.ELEMENT, bookmark3.getNodeKind());
        
        //Test different bookmarks created on same node have same Node Id. 
        assertEquals(bookmark1.getNodeId(), bookmark2.getNodeId());
        assertEquals(bookmark2.getNodeId(), bookmark3.getNodeId());
        assertEquals(bookmark3.getNodeId(), bookmark1.getNodeId());
        
        // Test that Bookmark maintains the Model  
        Model<N> modelFromBookmark = bookmark1.getModel();
        assertNotNull(modelFromBookmark); 
        assertEquals(bookmark1.getNodeId(), model.getNodeId( bookmark1.getNode()));
        
        // Test that Bookmark creates a new Cursor starting at the same Node.
        Cursor<N> cursor2 = bookmark1.newCursor();
        assertNotNull(cursor2);
        assertEquals(bookmark1.getNodeId(), cursor2.getNodeId());
        
    }

    
    
    @Test
    public void testBookmarkState()
    {
        ProcessingContext<N> context = newProcessingContext();
        
        FragmentBuilder<N> builder = context.newFragmentBuilder();
        N doc = createComplexTestDocument(builder);        
        assertNotNull(doc);
        
        Model<N> model  = context.getModel();
        assertNotNull(doc);
        
        //Get Node by Node Navigation and obtain a bookmark
        N node1 = model.getFirstChildElement(doc);
        node1 = model.getFirstChildElement(node1);
        node1 = model.getNextSiblingElement(node1);
        assertNotNull(node1);
        
        Bookmark<N> bookmark1 = context.bookmark(node1);
        assertNotNull(bookmark1); // Navigate to the fileset element
       
        //Test that there is no state in bookmark
        assertNull(bookmark1.getState("key1"));        
        //Add State to Bookmark
        bookmark1.putState("Key1", "Value1");
        bookmark1.putState("Key2", "Value2");
        
        //Test bookmark state
        assertEquals("Value1", bookmark1.getState("Key1"));
        assertEquals("Value2", bookmark1.getState("Key2"));
        
        //Get Node by Id and obtain a bookmark
        N node2 = model.getElementById(doc, "project.output"); // Navigate to the fileset element
        assertNotNull(node2);        
        Bookmark<N> bookmark2 = context.bookmark(node2);
        assertNotNull(bookmark2); 

        //Add State to Bookmark
        bookmark2.putState("Key1", "Value3");       
        //Test bookmark state
        assertEquals("Value3", bookmark2.getState("Key1"));
        
        //Move the cursor to the Node and obtain a bookmark
        Cursor<N> cursor = context.newCursor(doc);
        cursor.moveToFirstChildElement();
        cursor.moveToFirstChildElement();
        cursor.moveToNextSiblingElement(); // Navigate to the fileset element
        
        Bookmark<N> bookmark3 = cursor.bookmark();
        assertNotNull(bookmark2); 
        //Add State to Bookmark
        bookmark3.putState("Key1", "Value4");       
        //Test bookmark state
        assertEquals("Value4", bookmark3.getState("Key1"));
        
        //Test that different bookmark on different node have different state
        assertFalse(bookmark1.getState("Key1").equals( bookmark2.getState("Key1")));
        assertFalse(bookmark2.getState("Key1").equals( bookmark3.getState("Key1")));
        assertFalse(bookmark3.getState("Key1").equals( bookmark1.getState("Key1")));
    }
    
}
