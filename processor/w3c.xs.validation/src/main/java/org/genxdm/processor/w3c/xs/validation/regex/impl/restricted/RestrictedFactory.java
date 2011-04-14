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
package org.genxdm.processor.w3c.xs.validation.regex.impl.restricted;


import java.util.LinkedList;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExBridge;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExFactory;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPattern;


/**
 * Pattern factory which catches easy but restricted cases and special-cases them.
 * <p/>
 * Current cases caught are {m, *} and {m, n} for a simple term.
 * <p/>
 * If the term being processed is not simple, reverts to the underlying factory for
 * full treatment.
 */
public final class RestrictedFactory<E, T> implements RegExFactory<E, T>
{
    private RegExFactory<E, T> m_underlying;

    public RestrictedFactory(final RegExFactory<E, T> underlying)
    {
        m_underlying = underlying;
    }

    public RegExPattern<E, T> newPattern(final E term, final RegExBridge<E, T> bridge)
    {
        if (term == null)
        {
            return new SequenceOfSimplePattern<E, T>(new LinkedList<E>(), bridge);
        }
        if (isSimple(term, bridge))
        {
            int minOccurs = bridge.minOccurs(term);
            int maxOccurs = bridge.maxOccurs(term);

            if (maxOccurs == RegExBridge.UNBOUNDED)
            {
                return new KleenePattern<E, T>(term, minOccurs, bridge);
            }
            else
            {
                return new FiniteRepetitionPattern<E, T>(term, minOccurs, maxOccurs, bridge);
            }
        }
        else if (bridge.isSequence(term) && bridge.minOccurs(term) == 1 && bridge.maxOccurs(term) == 1)
        {
            return sequencePattern(term, bridge);
        }
        else
        {
            return m_underlying.newPattern(term, bridge);
        }
    }

    private static <E,T> boolean isSimple(final E term, final RegExBridge<E, T> bridge)
    {
        return !(bridge.isSequence(term) || bridge.isChoice(term) || bridge.isInterleave(term));
    }

    private RegExPattern<E, T> sequencePattern(final E term, final RegExBridge<E, T> bridge)
    {
        final LinkedList<E> subtermList = new LinkedList<E>();
        for (final E subterm : bridge.getSubTerms(term))
        {
            if (!(isSimple(subterm, bridge) && bridge.minOccurs(subterm) == 1 && bridge.maxOccurs(subterm) == 1))
            {
                return m_underlying.newPattern(term, bridge);
            }
            subtermList.add(subterm);
        }
        return new SequenceOfSimplePattern<E, T>(subtermList, bridge);
    }
}
