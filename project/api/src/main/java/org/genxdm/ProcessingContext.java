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
package org.genxdm;

import org.genxdm.io.DocumentHandlerFactory;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableContext;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.SchemaComponentCache;

/**
 * The processing context is a factory for new XML documents and adapters.
 * 
 * <p>The processing context may also represent an appropriate level for caching.</p>
 * 
 * <p>End users will normally consume, rather than implement, this interface.</p>
 */
public interface ProcessingContext<N> extends DocumentHandlerFactory<N> 
{
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
    MutableContext<N> getMutableContext();
    
    /** Return the prefix preferred by the specified namespace.
     * 
     * @param namespace the namespace to query; if null, the method returns null
     * @return the prefix that the specified namespace has registered as its
     *         preference, or null if no preference is registered
     */
    String getRegisteredPrefix(String namespace);
    
    /**
     * Get a TypedContext associated with this ProcessingContext which
     * is also associated with the supplied SchemaComponentCache.
     * 
     * A ProcessingContext only has one instance of a TypedContext associated
     * with any SchemaComponentCache argument, <em>including <code>null</code>.</em>
     * Callers that do not care about the cache of schema components (except
     * that it has what it needs to) should always supply null.  Callers who
     * are sharing a single cache of components among multiple bridges must handle
     * the SchemaComponentCache management themselves (call with null the first time,
     * get the newly-instantiated SchemaComponentCache, and then supply that cache to
     * other TypedContexts).
     * 
     * @param cache the preferred SchemaComponentCache with which this TypedContext is
     * to be associated.  The first time that a null is supplied, the TypedContext 
     * will initialize a new SchemaComponentCache, and that TypedContext will be returned 
     * for subsequent calls with a null argument. 
     * 
     * @return a TypedContext associated with this ProcessingContext and the
     * supplied SchemaComponentCache, or null if no such context is available.
     * Multiple calls with the same argument must return the same TypedContext. 
     * If the ProcessingContext does not support schema-aware
     * processing, null will be returned.
     */
    <A> TypedContext<N, A> getTypedContext(SchemaComponentCache cache);
    
    /**
     * Get a temporary typed context, because the schema component cache is a
     * temporary object.
     * 
     * This method is mostly intended for processors using various techniques
     * that enhance an existing TypedContext's SchemaComponentCache, without
     * adding permanent content ("chaining" or "linking" of component caches).
     * In some cases, these constructions are known to be needed for a single
     * use only, and keeping them around to meet the "same argument-same return"
     * constraint of {@link getTypedContext(SchemaComponentCache)} produces
     * a memory leak.
     * 
     * @param cache the supplied SchemaComponentCache; see {@link getTypedContext(SchemaComponentCache)}.
     * @returna TypedContext associated with this ProcessingContext and the
     * supplied SchemaComponentCache, but repeated calls with the same SCC will
     * not return the same TypedContext.
     */
    <A> TypedContext<N, A> getTempTypedContext(SchemaComponentCache cache);
    
    /**
     * Determines whether the item is a node (an instance of the bridge's
     * specialization of N).
     * 
     * @param item
     *            The candidate item.
     * @return <code>true</code> if the item is-a N, false otherwise.  If item
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
     * List supported features.
     * 
     * @return an iterable over the list of features supported by this bridge.
     */
//    Iterable<String> getSupportedFeatures();
    
    /**
     * Returns a new {@link Cursor} for navigating the XQuery Data Model.
     * 
     * @param node
     *            The node over which the cursor is initially positioned. Cannot be <code>null</code>.
     */
    Cursor newCursor(N node);


    /**
     * Returns a new {@link FragmentBuilder} for constructing data models.
     * 
     * By default, this should be the same result as newFragmentBuilder(true);
     */
    FragmentBuilder<N> newFragmentBuilder();
    
    /**
     * Returns a new {@link FragmentBuilder} for constructing data models.
     *
     * @param fixupNamespaces if true, generate prefix:namespace bindings as needed.
     *        if false, the tree will not be modified, and may be namespace ill-formed.
     */
    FragmentBuilder<N> newFragmentBuilder(boolean fixupNamespaces);

    /**
     * Applies the node() test to the item.
     * 
     * <p>If the item is a node, the node is returned. Otherwise, returns <code>null</code>.</p>
     * 
     * @param item
     *            The candidate item.
     */
    N node(Object item);

    /**
     * Allocates an empty array of nodes.
     * 
     * @param size
     *            The size of the array of nodes.  May not be negative.
     */
    N[] nodeArray(int size);

}
