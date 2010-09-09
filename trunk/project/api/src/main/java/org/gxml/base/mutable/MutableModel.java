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
package org.gxml.base.mutable;

import org.gxml.base.Model;
import org.gxml.base.io.FragmentBuilder;
import org.gxml.typed.TypedModel;

/**
 * An extension to {@link GxModel} that supports mutation of the data model.
 *
 * <p>This interface enables modifications to existing XML tree models.  If you're
 * familiar with the underlying tree models, it may seem natural to continue with existing
 * patterns and use this form of interface for XML tree exploration.  Note, however
 * that the introduction of mutability does imply some sacrifices.  In particular:
 * </p>
 * <ul>
 *  <li>Tree models that are either thread-unsafe, or force the use of synchronization blocks.</li>
 *  <li>Inability to readily access non-XML data as if it were XML (JSON, CSV, binary XML)</li>
 *  <li>Additional object storage to track changes.</li>
 *  <li>Representations that are not all "in-memory" become far more difficult.</li>
 *  <li>Inconsistent states for the XML tree while the document is being manipulated - such
 *  as being invalid according to a schema.</li>
 * </ul>
 * 
 * <p>As a consequence of these limitations of a mutable model, users of the gXML API
 * should favor the {@link Model} and {@link TypedModel} interfaces
 * whenever possible.  Further note that the MutableModel is specifically <i>untyped</i>
 * with the design assumption that clients will perform whatever set of manipulations,
 * and then create an typed, immutable tree for further "typed" processing. 
 * </p>
 * 
 * <p><b>Note:</b> This interface does not handle the creation of new nodes in a tree.
 * Rather that can be done with a {@link NodeFactory}, or a {@link FragmentBuilder}</p>
 * 
 * <p>By design, this interface does not enable changes the value of an attribute, comment,
 * or text node.  Rather, to change the text of a text node, for example, create a
 * new text node with {@link NodeFactory#createText(Object, String)}, and then
 * replace the existing child with {@link #replaceChild(Object, Object, Object)}.
 * </p> 
 *
 * @see Model
 * @see TypedModel
 * @see FragmentBuilder
 * @see NodeFactory
 */
public interface MutableModel<N> extends Model<N>
{
	/**
	 * Adopts a node from an external document. The source node (and its subtree) is removed from the document it is in
	 * (if any), and its owner document is changed to the target document. The source node can then be inserted into the
	 * target document.
	 * 
	 * @param target
	 *            The target document.
	 * @param source
	 *            The source node.
	 */
	N adoptNode(final N target, final N source);

	/**
	 * Appends the specified child to the end of the child axis of the specified parent.
	 * 
	 * @param parent
	 *            The parent to which the child should be added.
	 * @param newChild
	 *            The child to be added to the parent.
	 * @return The child added to the parent.
	 */
	N appendChild(final N parent, final N newChild);

	/**
	 * Copies a node to a specified depth.
	 * <p>
	 * The resulting node has the same owner as the source node.
	 * </p>
	 * 
	 * @param source
	 *            The source for the copying operation.
	 * @param deep
	 *            Determines whether to include the children of the copied node.
	 * @return A copy of the source node.
	 */
	N cloneNode(final N source, final boolean deep);

	/**
	 * Returns the owner document node of the specified node.
	 * 
	 * @param node
	 *            The node for which the owner is required.
	 * @return The owner document, never <code>null</code>.
	 */
	N getOwner(final N node);

	/**
	 * Normalizes the specified node and all of its descendants.
	 * <p>
	 * Normalization coalesces adjacent text nodes into a single node and removes any text nodes that have
	 * dm:string-value equivalent to a zero-length string.
	 * </p>
	 * 
	 * @param node
	 *            The node to be normalized.
	 */
	void normalize(final N node);

	/**
	 * Creates a copy of a source node from an external document that can be inserted into the target document.
	 * 
	 * @param target
	 *            The document into which the source node will be imported.
	 * @param source
	 *            The node from another document to be imported.
	 * @param deep
	 *            indicates whether the children of the node should be imported.
	 * @return The new node that is imported into the target document.
	 */
	N importNode(final N target, final N source, final boolean deep);

	/**
	 * Inserts a new child node before a specified reference node in the child axis of a parent node.
	 * <p>
	 * Insertion is not expected and not required to result in a normalized tree.
	 * </p>
	 * 
	 * @param parent
	 *            The parent that will contain the new child node.
	 * @param newChild
	 *            The new child to be added to the parent.
	 * @param refChild
	 *            The reference child before which the new node will be added. If no reference child is specified then
	 *            the new child is appended to the children of the parent node.
	 * @return The node that was inserted.
	 */
	N insertBefore(final N parent, final N newChild, final N refChild);

	/**
	 * Removes an attribute from an element by specifying the name of the attribute.
	 * 
	 * @param element
	 *            The element that contains the attribute node.
	 * @param namespaceURI
	 *            The namespace-uri part of the attribute name.
	 * @param localName
	 *            The local-name part of the attribute name.
	 */
	void removeAttribute(final N element, final String namespaceURI, final String localName);

	/**
	 * Removes a node from the child axis of the parent node.
	 * 
	 * @param parent
	 *            The parent containing the child to be removed.
	 * @param oldChild
	 *            The child to be removed.
	 * @return The child that has been removed.
	 */
	N removeChild(final N parent, final N oldChild);

	/**
	 * Removes a namespace from an element by specifying the prefix (dm:local-name) of the namespace.
	 * 
	 * @param element
	 *            The element that contains the namespace node.
	 * @param prefix
	 *            The prefix (local-name part of dm:name) of the namespace node.
	 */
	void removeNamespace(final N element, final String prefix);

	/**
	 * Replaces a node in the child axis of a parent node.
	 * 
	 * @param parent
	 *            The parent containing the node to be replaced in its child axis.
	 * @param newChild
	 *            The new node that will replace the old node.
	 * @param oldChild
	 *            The old node to be replaced.
	 * @return The old node that was removed.
	 */
	N replaceChild(final N parent, final N newChild, final N oldChild);

	/**
	 * Sets an attribute node into the attribute axis of an element.
	 * 
	 * @param element
	 *            The element that will hold the attribute.
	 * @param attribute
	 *            The attribute to be inserted.
	 */
	void setAttribute(final N element, final N attribute);

	/**
	 * Sets an attribute into the attribute axis of an element.
	 * 
	 * @param element
	 *            The element that will hold the new attribute.
	 * @param namespaceURI
	 *            The namespace-uri part of the dm:name of the attribute.
	 * @param localName
	 *            The local-name part of the dm:name of the attribute.
	 * @param prefix
	 *            The prefix part of the dm:name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @return The new attribute node.
	 */
	N setAttribute(final N element, final String namespaceURI, final String localName, final String prefix, final String value);

	/**
	 * Sets a namespace node into the namespace axis of an element.
	 * 
	 * @param element
	 *            The element that will hold the namespace node.
	 * @param namespace
	 *            The namespace node to be added.
	 */
	void setNamespace(final N element, final N namespace);

	/**
	 * Sets a namespace binding into the namespace axis of an element.
	 * 
	 * @param element
	 *            The element that will hold the namespace binding.
	 * @param prefixString
	 *            The prefix (local-name part of the dm:name) of the namespace node as a <code>String</code>.
	 * @param uriSymbol
	 *            The dm:string-value of the namespace node as a symbol.
	 * @return TODO
	 */
	N setNamespace(final N element, final String prefixString, final String uriSymbol);
}
