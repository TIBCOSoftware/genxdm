/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.gxml.axes;

public interface Navigator<N>
{

    /**
     * Positions the cursor at the specified bookmark.
     * 
     * @param bookmark
     *            The bookmark to which the cursor should be positioned.
     */
    void moveTo(N bookmark);

    /**
     * Moves to the attribute node with the specified expanded-QName.
     * 
     * Assumes that the cursor is initially positioned on an element with the required attribute.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     */
    boolean moveToAttribute(String namespaceURI, String localName);
    
    /**
     * Reposition the cursor to the element which has an attribute of type
     * id that matches the specified id string.
     * 
     * @param id the target id
     * @return <code>true</code> if IDs are supported, the ID in question is
     * defined for the containing document, and the cursor can be successfully
     * repositioned to the element having that ID, <code>false</code> if the
     * operation causes no change in position.
     */
    boolean moveToElementById(String id);

    /**
     * Moves the cursor to the first node along the child axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToFirstChild();

    /**
     * Moves the cursor to the first element along the child axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToFirstChildElement();

    /**
     * Moves the cursor to the first element with the specified name along the child axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToFirstChildElementByName(String namespaceURI, String localName);

    /**
     * Moves the cursor to the last node along the child axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToLastChild();

    /**
     * Moves the cursor to the next node along the following-sibling axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToNextSibling();

    /**
     * Moves the cursor to the next element along the following-sibling axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToNextSiblingElement();

    /**
     * Moves the cursor to the next element along the following-sibling axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToNextSiblingElementByName(String namespaceURI, String localName);

    /**
     * Moves the cursor to the node along the parent axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToParent();

    /**
     * Moves the cursor to the next node along the preceding-sibling axis.
     * 
     * @return <code>true</code> if the cursor moved, otherwise <code>false</code>.
     */
    boolean moveToPreviousSibling();

    /**
     * Moves the cursor to the farthest node along the ancestor axis.
     */
    void moveToRoot();

}
