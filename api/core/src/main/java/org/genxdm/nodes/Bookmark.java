/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.nodes;

import org.genxdm.Cursor;
import org.genxdm.Model;
import org.genxdm.NodeSource;

/** A stateful, positionally-immutable wrapper around a node.
 * 
 * The Bookmark abstraction is primarily intended to allow applications and processors
 * to retain state in a context surrounding a node, without requiring that a processing
 * context also be part of the state.  The bookmark is an {@link Informer}, which can
 * be queried for information about the contained node.
 * 
 * Bookmark guarantees immutability of position.  Implementations must <em>not</em>
 * permit a Bookmark's node reference to change.
 * 
 * The Bookmark can supply either a {@link Model} or {@link Cursor} (the latter initialized
 * to its own position) to permit further investigation.
 *
 * @param <N> the node handle
 */
public interface Bookmark<N>
    extends Informer, NodeSource<N>
{
    /** Allow navigation of the tree in which this bookmark is positioned.
     * 
     * @return a Cursor positioned at the contained node.
     */
    Cursor<N> newCursor();
    
    /** Supply a model which may be used to investigate the tree in which
     * this bookmark is positioned.
     * 
     * <p>In typical use, the Model will be queried starting from the result
     * of {@link #getNode()}</p>
     * 
     * @return a Model for the owning processing context.
     */
    Model<N> getModel();
    
    /** Permit storage of additional state.
     * 
     * @param key a unique identifier for the data; may not be null
     * @param data the additional state to be stored; if null, the key and any
     * previously stored data associated with it should be removed
     */
    void putState(String key, Object data);
    
    /** Retrieve previously-stored additional state.
     * 
     * @param key the unique identifier for the additional state; may not be null
     * @return the data associated with the supplied key; may return null
     */
    Object getState(String key);
}
