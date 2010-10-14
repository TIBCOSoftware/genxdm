/**
 * Copyright (c) 2010 TIBCO Software Inc.
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

import java.util.List;

/** Defines methods used to retrieve nodes in memory.
 *
 * @param <N> The node abstraction
 */

public interface NodeSource<N>
{
    /**
     * This is a shortcut method for getting the tree and then converting it to a node.
     * 
     * @return The nodes built by this builder.
     */
    List<N> getNodes();
    
    /** Return the document node, or the first node in the list.
     * 
     * @return a single node, either the single node constructed, or the first in the list;
     *  may return null if no nodes are available.
     */
    N getNode();

}
