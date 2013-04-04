/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.typed.io;

import org.genxdm.io.DocumentHandler;
import org.genxdm.typed.Validator;

/** A handler for creating typed documents.
 * 
 * This subinterface changes the semantics of the parse methods,
 * changing almost nothing else.  A TypedDocumentHandler is created with a
 * supplied Validator, which is then made available from the
 * TypedDocumentHandler (use it to check errors, for instance).
 * 
 * @param <N> The node abstraction
 * @param <A> The atom abstraction
 */
public interface TypedDocumentHandler<N, A>
    extends DocumentHandler<N>
{
    Validator<A> getValidator();
}
