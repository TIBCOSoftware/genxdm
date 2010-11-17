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
	 * Removes a node from the child axis of the parent node.
	 * @param target
	 *            The child to be removed.
	 * 
	 * @return The child that has been removed.
	 */
	N delete(final N target);
	
	N deleteChildren(final N target);

	/**
	 * Replaces a node in the child axis of a parent node.
	 * @param target
	 *            The old node to be replaced.
	 * @param content
	 *            The new node that will replace the old node.
	 * 
	 * @return The old node that was removed.
	 */
	N replace(final N target, final N content); //replace
	
	N replaceValue(final N target, final String value);

	/**
	 * Sets an attribute node into the attribute axis of an element.
	 * 
	 * @param element
	 *            The element that will hold the attribute.
	 * @param attribute
	 *            The attribute to be inserted.
	 * @return TODO
	 */
	N insertAttribute(final N element, final N attribute);
	
	N insertAttributes(final N element, final Iterable<N> attributes);

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
	N propagateNamespace(final N element, final String prefixString, final String uriSymbol);
	
	// implementing this looks to me like a *really* bad idea.
	// i'd rather discuss it than implement it.
	//N rename(N node, String namespace, String name);
}
