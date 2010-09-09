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
package org.gxml.typed.io;

import org.gxml.base.NodeSource;

/**
 * An instance of this interface assembles the events into an XML tree representation.
 * 
 * The tree built by this interface is expected to contain type annotations, and atoms
 * in place of character data (or in addition to).  The model produced, in other words,
 * is schema aware.
 */
public interface SequenceBuilder<N, A>
    extends SequenceHandler<A>, NodeSource<N>
{
    /**
     * Resets the builder by clearing the list of nodes that have been constructed in earlier executions.
     */
    void reset();


}
