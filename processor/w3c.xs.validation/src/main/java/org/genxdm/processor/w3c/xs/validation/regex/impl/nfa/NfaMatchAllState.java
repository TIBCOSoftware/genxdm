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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExBridge;


final class NfaMatchAllState<E> extends NfaMatchState<E>
{
    private final List<E> required = new ArrayList<E>(23);    // List of leaf terms
    private final List<E> optional = new ArrayList<E>(23);    // List of leaf terms

    NfaMatchAllState()
    {
        super(null);
    }

    @Override
    boolean isAll()
    {
        return true;
    }

    /**
     * Adds the given term to the "all" state.  The term must
     * have a single leaf term with occurrence of {1,1} or {0,1}.
     */
    void addAllTerm(E t, final RegExBridge<E, ?> bridge)
    {
        boolean isOptional = false;
        while (t != null && (bridge.isSequence(t) || bridge.isChoice(t)))
        {
            if (bridge.minOccurs(t) == 0)
            {
                isOptional = true;
            }
            // use first (and presumably only) term
            final Iterator<? extends E> subTerms = bridge.getSubTerms(t).iterator();
            if (subTerms != null && subTerms.hasNext())
            {
                t = subTerms.next();
            }
            else
            {
                t = null;
            }
        }
        if (t != null)
        {
            if (bridge.minOccurs(t) == 0)
                isOptional = true;
            if (isOptional)
                optional.add(t);
            else
                required.add(t);
        }
    }

    /**
     * Looks recursively for 'all' group leaf terms, since
     * often an all group will be represented in two levels.
     */
    void addAllTerms(final E t, final RegExBridge<E, ?> bridge)
    {
        for (final E subTerm : bridge.getSubTerms(t))
        {
            if (bridge.isInterleave(subTerm))
            {
                addAllTerms(subTerm, bridge);
            }
            else
            {
                addAllTerm(subTerm, bridge);
            }
        }
    }

    List<E> getRequiredTerms()
    {
        return required;
    }

    List<E> getOptionalTerms()
    {
        return optional;
    }
}
