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
package org.genxdm.typed;

import java.net.URI;

import org.genxdm.ProcessingContext;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.typed.variant.VariantBridge;
import org.genxdm.xs.Schema;

/**
 * A context for schema-related processing and state.
 * 
 * @param <N>
 *            The node handle.
 * @param <A>
 *            The atom handle.
 */
public interface TypedContext<N, A> 
    extends Schema<A>
{
    /**
     * Returns the bridge used for atom interaction.
     */
    AtomBridge<A> getAtomBridge();

    /**
     * Returns the bridge used for meta-data interaction.
     */
    MetaBridge<A> getMetaBridge();

    /**
     * Returns the {@link TypedModel} for navigating the document model. <br/>
     */
    TypedModel<N, A> getModel();
    
    /**
     * Returns the associated ProcessingContext
     */
    ProcessingContext<N> getProcessingContext();
    
    VariantBridge<N, A> getVariantBridge();

    /**
     * Determines whether the item is an atom.
     * <p/>
     * If the item is <code>null</code>, the return value is <code>false</code>.
     * </p>
     * 
     * @param item
     *            The candidate item.
     */
    boolean isAtom(Object item);
    /**
     * Applies a hypothetical atom() test to the item.
     * <p/>
     * If the item is an atom, the atom is returned. Otherwise, returns <code>null</code>.
     * </p>
     * 
     * @param item
     *            The candidate item.
     */
    A atom(Object item);

    /**
     * Determines whether this processing context is currently in a locked state.
     */
    boolean isLocked();

    /**
     * The processing context is in an unlocked state after it has been created. While in this state an implementation is not required to provide safe multi-threaded access. Locking the processing context guarantees that the processing context is safe for access by
     * different threads. In practice this will mean that further loading of schema resources is prohibited.
     */
    void lock();

    /**
     * Returns a cursor for navigating the tree part of the data model.
     */
    TypedCursor<N, A> newCursor(N node);
    
    SequenceBuilder<N, A> newSequenceBuilder();
    
    N validate(N source, Validator<N, A> validator, URI schemaNamespace);

}
