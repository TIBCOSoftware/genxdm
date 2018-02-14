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

import javax.xml.namespace.QName;

import org.genxdm.ProcessingContext;
import org.genxdm.io.ContentGenerator;
import org.genxdm.names.NamespaceRegistry;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.TypedDocumentHandlerFactory;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.SchemaComponentCache;

/**
 * A context for schema-related processing and state.
 * 
 * TypedContext extends Schema so that it can be initialized with the schemas
 * necessary for validating target instances.  It also extends
 * TypedDocumentHandlerFactory in order to permit validation on parse.
 * 
 * @param <N>
 *            The node handle.
 * @param <A>
 *            The atom handle.
 */
public interface TypedContext<N, A> 
    extends TypedDocumentHandlerFactory<N, A>, NamespaceRegistry
{
    /**
     * Returns the bridge used for atom interaction.  Atoms are typed values.
     * 
     * @return the AtomBridge associated with this typed context; never null.
     */
    AtomBridge<A> getAtomBridge();

    /**
     * Returns the schema cache for registering new schema components and for discovering 
     * existing schema components.
     * @return the schema cache associated with this context
     */
    SchemaComponentCache getSchema();
    
    /**
     * Returns the bridge used for type interaction.  Types means both
     * type names and the org.genxdm.xs abstractions used to model types.
     * 
     * @return the TypesBridge associated with this typed context; never null.
     */
    TypesBridge getTypesBridge();

    /**
     * Returns the {@link TypedModel} for navigating the document model.
     * 
     * @return the TypedModel associated with this typed context; never null.
     */
    TypedModel<N, A> getModel();
    
    /**
     * Returns the associated ProcessingContext.  The ProcessingContext
     * is the 'parent' of this typed context.
     * 
     * @return the ProcessingContext from which this typed context was originally
     * obtained; never null.
     */
    ProcessingContext<N> getProcessingContext();
    
    /**
     * Returns a cursor for navigating the data model.
     * 
     * @param node the context node over which the cursor is initially
     * positioned.  If null, an exception will be thrown.
     * 
     * @return a new TypedCursor positioned over the supplied node.
     */
    TypedCursor<N, A> newCursor(N node);
    
    /** Create a new SequenceBuilder for constructing type-annotated trees
     * in memory, with typed values.
     * 
     * By default, identical in behavior to newSequenceBuilder(true);
     *
     * @return a new SequenceBuilder capable of generating typed and type-annotated
     * trees compatible with this typed context, never null.
     */
    SequenceBuilder<N, A> newSequenceBuilder();
    
    /** Create a new SequenceBuilder for constructing type-annotated trees
     * in memory, with typed values.
     * 
     * @param fixupNamespaces if true, add prefix to namespace bindings as needed,
     *        and if possible. If false, make no such changes, and potentially generate
     *        a namespace ill-formed tree.
     * @return a new SequenceBuilder capable of generating typed and type-annotated
     * trees compatible with this typed context, never null.
     */
    SequenceBuilder<N, A> newSequenceBuilder(boolean fixupNamespaces);
    
    /** Validate or revalidate a tree in memory.
     * 
     * @param source the starting point for validation; must not be null.
     *        If the initialType argument is non-null, then this node <em>must</em>
     *        be an element (of the type specified by the type-name).
     * @param validator the (partially-initialized) validation handler to be
     *        used; must not be null.
     * @param initialType the QName of the type of the element node passed as the 'source'
     *        parameter. This argument should <em>usually</em> be null; supplying
     *        an initial type argument tells the validator to ignore the name
     *        of the supplied element, and to validate it based solely on its
     *        content, without looking for an element declaration.
     * @return a node representing the equivalent to the supplied source node,
     * as the base of a validated (type-annotated and type-valued) tree; never
     * null.  If validation has failed, the tree may not be typed.  The returned
     * node <em>may</em>, but is not required to be the same node supplied as an
     * argument, suitably modified with type annotations and typed values, if
     * the bridge supports this form of mutation; otherwise, the bridge may
     * create and return a tree comparable to the supplied tree, with added
     * type annotations and typed values. When in-tree validation is supported,
     * this <em>should</em> be the supplied tree, upgraded and annotated.
     */
    N validate(N source, ValidationHandler<A> validator, QName initialType);
    
    /** Validate or revalidate a tree in memory. This form of validation can be
     * used to convert a foreign tree (typed or untyped) to this bridge's
     * tree model.
     * 
     * @param source the starting point for validation; must not be null. Not
     *        required to be the Cursor associated with this bridge. <em>Should</em> be
     *        positioned at the document, if possible, unless the initialType
     *        argument is non-null, in which case it <em>must</em> be positioned
     *        on the element for which initialType indicates the type name to
     *        be used during validation.
     * @param validator the (partially-initialized) validation handler to be
     *        used; must not be null.
     * @param initialType the QName of the type of the element node that the 'source'
     *        parameter is positioned on. This argument should <em>usually</em> be null; supplying
     *        an initial type argument tells the validator to ignore the name
     *        of the initial element, and to validate it based solely on its
     *        content, without looking for an element declaration.
     * @return a node representing the equivalent to the supplied source cursor,
     * as the base of a validated (type-annotated and type-valued) tree; never
     * null. If validation has failed, the tree may not be typed. Will <em>not</em>
     * be the same tree (object) supplied as an argument. 
     */
    N validate(ContentGenerator source, ValidationHandler<A> validator, QName initialType);
}
