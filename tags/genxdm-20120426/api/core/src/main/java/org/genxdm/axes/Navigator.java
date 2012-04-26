/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.axes;

/** A stateful abstraction that provides navigation from node to node in
 * a tree of connected nodes.
 * 
 * <p>Note that Navigator, by design, does not actually require a specialization
 * of the common &lt;N>ode parameter.  In theory, like the {@link org.genxdm.nodes.Informer} with
 * which it is commonly paired, it can be used over a tree which does not have
 * a "node" abstraction easily conformant to the design of GenXDM.</p>
 */
public interface Navigator
{

    /**
     * Moves to the attribute node with the specified expanded-QName.
     * 
     * <p>Assumes that the cursor is initially positioned on an element with the required attribute.</p>
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     * @return true if the move is successful, false if not (and position will be unchanged)
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
     * <p>This is an unconditional, untested move; the state is expected
     * to have changed upon completion. If the cursor is already at the
     * root, it will not change.</p>
     */
    void moveToRoot();

}
