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
package org.genxdm.mutable;

import org.genxdm.ProcessingContext;

public interface MutableContext<N>
{
    /** Provide the node factory associated with this context.
     * 
     * Implementation note: for bridge implementations over tree models
     * that require nodes to be created in the context of an existing document
     * (e.g. DOM), it is far, <em>far</em> safer to use MutableModel.getFactory(N)
     * or MutableCursor.getFactory().  Otherwise, while the nodes added to the
     * tree will be "identical", they will not have object identity.  Also,
     * this is more efficient, as well.
     * 
     * @return a {@link NodeFactory} for creating new nodes, never null.
     */
    NodeFactory<N> getNodeFactory();

    /**
     * Returns a {@link MutableModel} for navigating and modifying an XDM, never null.
     */
    MutableModel<N> getModel();
    
    /**
     * Returns the associated processing context (which supplied this mutable
     * context), never null.
     */
    ProcessingContext<N> getProcessingContext();
    
    /**
     * Returns a new {@link MutableCursor} for navigating the XDM.
     * 
     * @param node
     *            The node over which the cursor is initially positioned. Cannot be <code>null</code>.
     * @return a new mutable cursor positioned at the supplied node, never null.
     */
    MutableCursor<N> newCursor(N node);

}
