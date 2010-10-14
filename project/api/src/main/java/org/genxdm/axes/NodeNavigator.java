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
package org.genxdm.axes;

public interface NodeNavigator<N> 
{

    /**
     * Returns the attribute node with the specified expanded-QName.
     * 
     * @param node
     *            The node that is the parent of the attribute node.
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     */
    N getAttribute(N node, String namespaceURI, String localName);
    
    /**
     * Return the element that has the specified ID
     * 
     * @return the element node (in the same document as the context node)
     * that has the specified ID, or <code>null</code> if no such element exists.
     */
    N getElementById(N context, String id);

    /**
     * Returns the first child node of the node provided.
     * 
     * @param node
     *            The node for which the first child node is required.
     */
    N getFirstChild(N origin);

    /**
     * Returns the first element along the child axis.
     * 
     * @param node
     *            The parent node that owns the child axis.
     */
    N getFirstChildElement(N node);

    /**
     * Returns the first child element along the child axis whose name matches the arguments supplied.
     * 
     * @param node
     *            The parent node that owns the child axis.
     * @param namespaceURI
     *            The namespace-uri to be matched.
     * @param localName
     *            The local-name to be matched.
     */
    N getFirstChildElementByName(N node, String namespaceURI, String localName);

    /**
     * Returns the last child node of the node provided.
     * 
     * @param node
     *            The node for which the last child node is required.
     */
    N getLastChild(N node);

    /**
     * Returns the next sibling node of the node provided.
     * 
     * @param node
     *            The node for which the next sibling node is required.
     */
    N getNextSibling(N node);

    /**
     * Returns the next element along the child axis.
     * 
     * @param node
     *            The node for which the next sibling node is required.
     */
    N getNextSiblingElement(N node);

    /**
     * Returns the next element along the following-sibling axis whose name matches the arguments supplied.
     * 
     * @param node
     *            The node for which the next sibling node is required.
     * @param namespaceURI
     *            The namespace-uri to be matched.
     * @param localName
     *            The local-name to be matched.
     */
    N getNextSiblingElementByName(N node, String namespaceURI, String localName);

    /**
     * Returns the parent node of the node provided. <br/>
     * May return <code>null</code> for top-most or orphaned nodes. <br/>
     * Corresponds to the dm:parent accessor in the XDM.
     * 
     * @param node
     *            The node for which the parent is required.
     */
    N getParent(N origin);

    /**
     * Returns the previous sibling node of the node provided.
     * 
     * @param node
     *            The node for which the previous sibling node is required.
     */
    N getPreviousSibling(N node);

    /**
     * Returns the identity of the top-most node along the ancestor-or-self axis from this node.
     * 
     * @param node
     *            The node from which to begin the search for the top-most node.
     */
    N getRoot(N node);

}
