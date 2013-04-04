/*
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
package org.genxdm.exceptions;

/**
 * Indicates what action to take when numeric casts result in overflow or underflow errors.
 * 
 * <p/>If target is float or double: overflow: FOAR0002 | INF or -INF | largest non-infinite float or double underflow: 
 * FOAR0002 | 0.0E0 | +/- 2**Emin or a denormalized value, where Emin is the smallest possible xs:float or xs:double exponent.</p>
 * 
 * <p>See <a href="http://www.w3.org/TR/xpath-functions/#op.numeric"> functions and operators specification</a> for more details.</p>
 */
public enum SpillagePolicy
{
    /**
     * Raise an error for casts which result in underflows or overflows.
     */
    DO_THE_RIGHT_THING
    {
        public boolean checkCapacity()
        {
            return true;
        }

        public boolean raiseError()
        {
            return true;
        }
    },

    /**
     * For casts which result in underflows or overflows, return, respectively, 
     * the smallest or largest number that can be represented in the target type.
     */
    SWEEP_UNDER_THE_RUG
    {
        public boolean checkCapacity()
        {
            return true;
        }

        public boolean raiseError()
        {
            return false;
        }
    },

    /**
     * Do not explicitly check for underflows or overflows. Instead rely upon the 
     * behavior of the underlying JDK.
     */
    HOPE_FOR_THE_BEST
    {
        public boolean checkCapacity()
        {
            return false;
        }

        public boolean raiseError()
        {
            return false;
        }
    };

    public static SpillagePolicy lookup(final boolean checkCapacity, final boolean raiseError)
    {
        if (checkCapacity)
        {
            if (raiseError)
            {
                return SpillagePolicy.DO_THE_RIGHT_THING;
            }
            else
            {
                return SpillagePolicy.SWEEP_UNDER_THE_RUG;
            }
        }
        else
        {
            if (raiseError)
            {
                throw new AssertionError();
            }
            else
            {
                return SpillagePolicy.HOPE_FOR_THE_BEST;
            }
        }
    }

    /** Does this spillage policy check capacity?
     * 
     * @return <code>true</code> if policy is {@link #DO_THE_RIGHT_THING} or {@link #SWEEP_UNDER_THE_RUG}.
     */
    public abstract boolean checkCapacity();

    /** Does this spillage policy raise an error?
     * 
     * @return <code>true</code> if policy is {@link #DO_THE_RIGHT_THING}
     */
    public abstract boolean raiseError();
}
