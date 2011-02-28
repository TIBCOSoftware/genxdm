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
package org.genxdm.typed;

import org.genxdm.Model;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.nodes.TypedNodeInformer;
import org.genxdm.typed.io.SequenceHandler;

/**
 * Aspect of the data model interface providing read-only access to the XQuery Data Model tree.
 * 
 * @param <N>
 *            The node handle
 * @param <A>
 *            The atom handle
 */
public interface TypedModel<N, A> 
    extends Model<N>, TypedNodeInformer<N, A>
{
	/**
	 * Support for streaming a node to a {@link SequenceHandler}.
	 * 
	 * @param node
	 *            The node to be streamed.
	 * @param copyNamespaces
	 *            Determines whether namespaces nodes are streamed.
	 * @param copyTypeAnnotations
	 *            Determines whether type annotations are streamed.
	 * @param handler
	 *            The handler for events generated.
	 */
	void stream(N node, boolean copyNamespaces, boolean copyTypeAnnotations, SequenceHandler<A> handler) throws GxmlException;
}
