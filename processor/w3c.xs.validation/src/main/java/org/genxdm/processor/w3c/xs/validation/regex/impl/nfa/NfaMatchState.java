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
package org.genxdm.processor.w3c.xs.validation.regex.impl.nfa;

/**
 * MatchState is the implementation of a state in the
 * state machine.  The state machine is nothing but a collection
 * of states, and is completely defined by the initial state.
 * <p/>
 * Each state contains a list of possible next states and a
 * term that must be matched to exit the state.
 */
class NfaMatchState<E> extends Vertex<E, NfaMatchState<E>>
{
    NfaMatchState(final E term)
    {
        super(term);
    }

    boolean isAll()
    {
        return false;
    }
}
