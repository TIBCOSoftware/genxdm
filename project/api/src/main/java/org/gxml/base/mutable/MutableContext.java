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
package org.gxml.base.mutable;

import org.gxml.base.ProcessingContext;

public interface MutableContext<N>
{
    /** Provide the node factory associated with this context.
     * 
     * A bridge need not support mutability, and consequently need not support this
     * method.  If {@link isMutable()} returns <code>false</code>, this method may
     * return null.
     * 
     * @return a {@link NodeFactory} for creating new nodes, or null if mutability is not supported.
     */
    NodeFactory<N> getNodeFactory();

    /**
     * Returns a {@link MutableModel} for navigating and modifying an XDM model.
     */
    MutableModel<N> getModel();
    
    /**
     * Returns the associated processing context.
     */
    ProcessingContext<N> getProcessingContext();
    
    /**
     * Returns a new {@link MutableCursor} for navigating the XDM model.
     * 
     * @param node
     *            The node over which the cursor is initially positioned. Cannot be <code>null</code>.
     */
    MutableCursor<N> newCursor(N node);

}
