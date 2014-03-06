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
package org.genxdm.processor.xpath.v10.iterators;

import org.genxdm.nodes.Traverser;
import org.genxdm.xpath.v10.ExprException;

public interface CloneableTraverser extends Traverser
{
    // TODO - is it useful to override the built-in notion of "clone"?
    Object clone();

    // Bind variable references to the values, so that
    // the iterator is protected from mutations in the
    // ExprContext.
    void bind() throws ExprException;
}
