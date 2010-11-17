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
package org.genxdm.base;

import org.genxdm.base.io.DocumentHandler;
import org.genxdm.base.io.DocumentHandlerFactory;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.base.mutable.MutableContext;
import org.genxdm.nodes.Bookmark;
import org.genxdm.typed.TypedContext;

/**
 * The processing context is a factory for new XML documents and adapters.
 * 
 * <p/>
 * 
 * The processing context may also represent an appropriate level for caching.
 * 
 * <p/>
 * 
 * End users will normally consume, rather than implement, this interface.
 */
public interface ProcessingContext<N> extends DocumentHandlerFactory<N> 
{
    /** Provide a wrapper around a node.
     * 
     * This abstraction is provided to allow applications and processors to contain
     * stateful references to nodes associated with a processing context (without
     * maintaining references to both N and ProcessingContext<... N ...>).
     * 
     * @param node the node for which a reference is desired.
     * @return a bookmark containing a reference to the supplied node.
     */
    Bookmark<N> bookmark(N node);
    
	/**
	 * Returns a {@link Model} for navigating an XDM model.
	 */
	Model<N> getModel();

    /**
     * Indicates whether this processing context supports mutation of trees in-memory.
     * 
     * @return a MutableContext associated with this ProcessingContext, or null
     * if no such context is available
     */
    <F> MutableContext<N, F> getMutableContext();
    
    /**
     * Indicates whether this processing context supports schema processing and awareness.
     * 
     * @return a TypedContext associated with this ProcessingContext, or null
     * if no such context is available
     */
    <A> TypedContext<N, A> getTypedContext();
    
    /**
     * Determines whether the item is a node (an instance of the bridge's
     * specialization of N).
     * 
     * @param item
     *            The candidate item.
     * @return <code>true</code> if the I is-a N, false otherwise.  If item
     * is <code>null</code>, returns <code>false</code>.
     */
    boolean isNode(Object item);

    /**
     * Determines whether the specified feature is supported.
     * 
     * @param feature
     *            The feature of interest.
     * @return <code>true</code> if the feature is supported, <code>false</code> otherwise.
     */
    boolean isSupported(String feature);
    
    /**
	 * Returns a new {@link Cursor} for navigating the XDM model.
	 * 
	 * @param node
	 *            The node over which the cursor is initially positioned. Cannot be <code>null</code>.
	 */
	Cursor<N> newCursor(N node);


    /**
	 * Returns a new {@link DocumentHandler} for constructing and writing data models.
	 */
	FragmentBuilder<N> newFragmentBuilder();

    /**
     * Applies the node() test to the item.
     * <p/>
     * If the item is a node, the node is returned. Otherwise, returns <code>null</code>.
     * </p>
     * 
     * @param item
     *            The candidate item.
     */
    N node(Object item);

    /**
     * Allocates an empty array of nodes.
     * 
     * @param size
     *            The size of the array of nodes.
     */
    N[] nodeArray(int size);

}
