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
package org.genxdm.base.mutable;

import org.genxdm.base.Model;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.typed.TypedModel;

public interface MutableModel<N> extends Model<N>
{
	/**
	 * Appends the specified child to the end of the child axis of the specified parent.
	 * 
	 * @param parent
	 *            The parent to which the child should be added.
	 * @param newChild
	 *            The child to be added to the parent.
	 * @return The child added to the parent.
	 */
	N appendChild(final N parent, final N content);
	
	N appendChildren(final N parent, final Iterable<N> content);
	
	N prependChild(final N parent, final N content);
	
	N prependChildren(final N parent, final Iterable<N> content);

	/**
	 * Inserts a new child node before a specified reference node in the child axis of a parent node.
	 * <p>
	 * Insertion is not expected and not required to result in a normalized tree.
	 * </p>
	 * @param target
	 *            The reference child before which the new node will be added. If no reference child is specified then
	 *            the new child is appended to the children of the parent node.
	 * @param content
	 *            The new child to be added to the parent.
	 * 
	 * @return The node that was inserted.
	 */
	N insertBefore(final N target, final N content);
	
	N insertBefore(final N target, final Iterable<N> content);
	
	N insertAfter(final N target, final N content);

	N insertAfter(final N target, final Iterable<N> content);

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
	void removeAttribute(final N element, final String namespaceURI, final String localName); //delete

	/**
	 * Removes a node from the child axis of the parent node.
	 * 
	 * @param parent
	 *            The parent containing the child to be removed.
	 * @param oldChild
	 *            The child to be removed.
	 * @return The child that has been removed.
	 */
	N removeChild(final N parent, final N oldChild); //delete

	/**
	 * Removes a namespace from an element by specifying the prefix (dm:local-name) of the namespace.
	 * 
	 * @param element
	 *            The element that contains the namespace node.
	 * @param prefix
	 *            The prefix (local-name part of dm:name) of the namespace node.
	 */
	void removeNamespace(final N element, final String prefix); //delete

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
	N replaceChild(final N parent, final N newChild, final N oldChild); //replace

	/**
	 * Sets an attribute node into the attribute axis of an element.
	 * 
	 * @param element
	 *            The element that will hold the attribute.
	 * @param attribute
	 *            The attribute to be inserted.
	 */
	void setAttribute(final N element, final N attribute); //insertattributes

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
	N setNamespace(final N element, final String prefixString, final String uriSymbol); // propagatenamespace
}
