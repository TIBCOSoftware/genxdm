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
package org.gxml.typed;

import org.gxml.base.Cursor;
import org.gxml.nodes.TypeInformer;

/**
 * A cursor paradigm for navigating an XML tree.
 * <p>
 * A single cursor moves over a tree allowing the underlying tree to be accessed. using a cursor provides maximum opportunity for the underlying representation to avoid object creation.
 * </p>
 * 
 * @param <N>
 *            The node handle.
 * @param <A>
 *            The atom handle.
 */
public interface TypedCursor<N, A> 
    extends Cursor<N>, TypeInformer<A>
{
}
