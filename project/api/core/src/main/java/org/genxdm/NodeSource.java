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
package org.genxdm;

import java.util.List;

/** Defines methods used to retrieve nodes in memory.
 *
 * @param <N> The node abstraction
 */

public interface NodeSource<N>
{
    /**
     * Returns a sequence of <N>odes, expressed as a list of <N>odes (some
     * of which may have complex content).
     * 
     * @return the sequence of nodes available from this source, never null (the
     * list may be empty).
     */
    List<N> getNodes();
    
    /** Return the document node, or the first node in the list.
     * 
     * This is really a shortcut for getNodes().get(0), but with error checking.
     * 
     * @return a single node, either the single node available, or the first in the sequence;
     *  may return null if no nodes are available; may <em>not</em> return a node which is
     *  not the first in the list of available nodes.
     */
    N getNode();

}
