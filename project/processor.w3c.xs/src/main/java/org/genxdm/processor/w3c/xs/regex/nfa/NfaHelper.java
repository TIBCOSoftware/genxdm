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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

final class NfaHelper
{
    /**
     * updates the followers using the states remaining in this turn.
     *
     * @param followers a List of PatternTerm.
     */
    public static <E> void updateFollowers(ArrayDeque<NfaMatchState<E>> work, List<E> followers)
    {
        if (followers != null)
        {
            while (work.size() != 0)
            {
                NfaMatchState<E> s = work.remove();
                if (s == NfaStepper.getMarker()) 
                {
                    break;
                }
                addFollowers(s, followers, new ArrayList<NfaMatchState<E>>(23));
            }
        }
    }

    /**
     * add all leaf terms that could directly follow the given state.
     *
     * @param followers a List of PatternTerm.
     */
    public static <E> void addFollowers(NfaMatchState<E> curState, List<E> followers, List<NfaMatchState<E>> visited)
    {
        if (curState.getTerm() == null)
        {
            // avoid infinite loop in case of (a?)*
            if (visited.indexOf(curState) == -1)
            {
                visited.add(curState);
                List<NfaMatchState<E>> matchStates = curState.nextStates();
                int count = matchStates.size();
                for (int index = 0; index < count; index++)
                {
                    addFollowers(matchStates.get(index), followers, visited);
                }
            }
        }
        else if (followers.indexOf(curState.getTerm()) == -1 && curState.getTerm() != null)
        {
            followers.add(curState.getTerm());
        }
    }
}
