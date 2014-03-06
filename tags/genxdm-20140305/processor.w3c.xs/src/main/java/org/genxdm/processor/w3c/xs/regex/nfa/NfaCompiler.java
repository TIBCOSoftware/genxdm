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
package org.genxdm.processor.w3c.xs.regex.nfa;

import org.genxdm.processor.w3c.xs.regex.api.RegExBridge;

final class NfaCompiler
{
    /**
     * Private variable which prevents very large maxOccurs values
     * from causing OutOfMemory errors.  If a maxOccurs value
     * is greater than or equal to this constant, we will treat
     * it in the same manner as we treat maxOccurs = 'unbounded'.
     */
    private static final int MAXIMUM_MAX_OCCURS = 256;
    
    /**
     * Compiles the regular expression pattern term into a NFA.
     *
     * @param term term head of the term term tree. We allow null which represents the lambda set.
     * @return initial state of the created state machine
     */
    public static <E,T> NfaMatchState<E> compileNFA(final E term, final RegExBridge<E, T> bridge)
    {
        final NfaMatchState<E> start = new NfaMatchState<E>(/*unconditional*/null);

        final NfaMatchState<E> end = compile(term, bridge, start);
        if (end.getTerm() != null || end.isAll())
        {
            // need non-matching end-state, so add one
            final NfaMatchState<E> t = new NfaMatchState<E>(null);
            end.addNext(t);
        }
        return start;
    }

    /**
     * Compiles a term, handling min/max values.
     *
     * @param term  the head of pattern term tree to compile. We allow null representing the lambda set.
     * @param start the start state for the compiled states
     * @return the end state of the compiled states
     */
    private static <E,T> NfaMatchState<E> compile(final E term, final RegExBridge<E, T> bridge, NfaMatchState<E> start)
    {
        if (null != term)
        {
            int min = bridge.minOccurs(term);
            int max = bridge.maxOccurs(term);
            final E prime = bridge.prime(term);

            while (min > 0)
            {
                // start with seq of min repeats
                start = compileOne(prime, bridge, start);
                min -= 1;
                if (max != RegExBridge.UNBOUNDED)    // don't touch infinity
                {
                    max -= 1;
                }
            }

            if (max >= MAXIMUM_MAX_OCCURS)
            {
                // loop
                final NfaMatchState<E> looper = new NfaMatchState<E>(null);
                start.addNext(looper);
                compileOne(prime, bridge, looper).addNext(looper);

                // Looper cannot be used as the end state because it has following states,
                // so we create a distinct end state.
                final NfaMatchState<E> end = new NfaMatchState<E>(null);
                looper.addNext(end);
                return end;
            }
            else if (max > 0)
            {
                // 0 to max number of occurrences
                NfaMatchState<E> join = new NfaMatchState<E>(null);
                
                NfaMatchState<E> originalStart = start;
                while (max > 0)
                {
                    start = compileOne(prime, bridge, start);
                    start.addNext(join);
                    max -= 1;
                }
                originalStart.addNext(join);                
                return join;
            }
            else
            {
                return start;
            }
        }
        else
        {
            // Lambda
            final NfaMatchState<E> end = new NfaMatchState<E>(null);
            start.addNext(end);
            return end;
        }
    }

    /**
     * Compiles one occurrence of the term (ignoring min/max).
     *
     * @param term  the head of pattern term tree to compile
     * @param start the start state for the compiled states
     * @return the end state of the compiled states
     */
    private static <E,T> NfaMatchState<E> compileOne(final E term, final RegExBridge<E, T> bridge, final NfaMatchState<E> start)
    {
        if (bridge.isSequence(term))
        {
            // must match all of the subterms in sequence
            NfaMatchState<E> end = start;
            for (final E subTerm : bridge.getSubTerms(term))
            {
                end = compile(subTerm, bridge, end);
            }
            return end;
        }
        else if (bridge.isChoice(term))
        {
            // must match any one of the subterms
            final NfaMatchState<E> join = new NfaMatchState<E>(null);

            for (final E subTerm : bridge.getSubTerms(term))
            {
                compile(subTerm, bridge, start).addNext(join);
            }
            return join;
        }
        else if (bridge.isInterleave(term))
        {
            // must match all of the subterms in any order
            final NfaMatchAllState<E> allStateNfa = new NfaMatchAllState<E>();
            allStateNfa.addAllTerms(term, bridge);
            start.addNext(allStateNfa);
            return allStateNfa;
        }
        else
        {
            // leaf term
            final NfaMatchState<E> t = new NfaMatchState<E>(term);
            start.addNext(t);
            return t;
        }
    }
}
