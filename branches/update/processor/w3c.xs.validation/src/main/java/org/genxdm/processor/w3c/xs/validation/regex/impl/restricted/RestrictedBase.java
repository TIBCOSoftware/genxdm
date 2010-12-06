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


import java.util.ArrayList;
import java.util.List;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExMachine;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPattern;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPatternInput;


public abstract class RestrictedBase<E, T> implements RegExPattern<E, T>
{
    public boolean matches(final RegExPatternInput<E, T> input, final List<E> followers)
    {
        final RegExMachine<E, T> stepper = createRegExMachine(followers);
        final List<E> matchTerms = new ArrayList<E>(23);
        while (input.hasNext())
        {
            matchTerms.clear();
            final T token = input.peek();
            if (stepper.step(token, matchTerms))
            {
                input.matchedPeek(matchTerms);    // tell input who matched
                input.next();    // consume the term and continue
            }
            else
            {
                return false;
            }
        }
        return stepper.step(null, null);
    }
}
