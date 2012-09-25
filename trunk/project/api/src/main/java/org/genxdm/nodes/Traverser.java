/*
 * Copyright (c) 2012 TIBCO Software Inc.
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

import org.genxdm.Precursor;

/**
 * A key lynchpin of a "cursor" oriented API comes from this interface, which follows
 * a particular sequence of "nodes" to visit, but which nodes visited is controlled
 * by that which instantiates the interface, rather than that which uses it.
 * 
 * <p>The "Traverser" has three possible states - "ready", "at a location", and "finished".
 * The {@link TraversingInformer} methods work only when the Traverser is in the
 * "at a location" state. 
 * </p>
 * <ul>
 * <li>Just started - the initial state - must call "moveToNext()" to transition to the "at a location"
 * or "finished" state.</li>
 * <li>At a location - whenever "moveToNext" returns "true", the Traverser is at a location, and the
 * methods that introspect what is at that location can be used.</li>
 * <li>Finished - after "moveToNext" returns "false", the Traverser no longer points at a valid location.</li>
 * </ul>
 * </p>
 * 
 * <p>The Traverser operates in contrast to the {@link Precursor}. A Precursor can
 * be moved in arbitrary directions to a new location in the underlying "tree" of data.
 * In contrast, the Traverser proceeds to the next entry it its intended set.
 * The Traverser works for returning any sort of sequence of locations, and
 * can be computed on demand.
 * </p>
 * 
 * <p>By design, the Traverser interface cannot be reset to restart the traversal
 * of the underlying nodes. This allows for efficient processing when intermediate
 * results do not need to be stored. Should a client need to store the intermediate
 * results, that can be accomplished by looping over the locations of the Traverser,
 * and invoking {@link TraversingInformer#newPrecursor()}</p>
 * 
 * @see Precursor
 */
public interface Traverser extends TraversingInformer {

    /**
     * Move to the next location, if any.
     * 
     * <p>Once this method returns "false", the Traverser is in the "finished" state,
     * and will not return any further results.
     * </p>
     * 
     * @return false if there is no new location to move to.
     */
    boolean moveToNext();
    
    boolean isFinished();
}
