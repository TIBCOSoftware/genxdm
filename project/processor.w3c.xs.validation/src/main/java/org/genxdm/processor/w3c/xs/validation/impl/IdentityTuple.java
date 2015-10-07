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
package org.genxdm.processor.w3c.xs.validation.impl;

import java.util.ArrayList;

import org.genxdm.exceptions.PreCondition;

/**
 * A tuple is an ordered list of keys values.
 */
final class IdentityTuple
{
    private final ArrayList<IdentityKey> m_keys;

    public IdentityTuple(final ArrayList<IdentityKey> keys)
    {
        // This invariant may not hold up to the test of time.
        this.m_keys = PreCondition.assertArgumentNotNull(keys, "keys");
    }

    public ArrayList<IdentityKey> getKeys()
    {
        return m_keys;
    }

    @Override
    public int hashCode()
    {
        // deityonastick,thisisnotevenwrong
//        return m_keys.size();
        // thank you josh bloch for the correct impl:
        final int prime = 31;
        int result = 1;
        for (IdentityKey key : m_keys)
            result = prime * result + key.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof IdentityTuple)
            return equalKeys(m_keys, ((IdentityTuple)obj).m_keys);
        return false;
    }
    
    private boolean equalKeys(final ArrayList<IdentityKey> expect, final ArrayList<IdentityKey> actual)
    {
        if (expect.size() != actual.size())
            return false;
        for (int i = 0; i < expect.size(); i++)
        {
            if (!expect.get(i).equals(actual.get(i)))
                return false;
        }
        return true; // if we get here, everything matched.

        // old version.
//        final Iterator<IdentityKey> lhs = expect.iterator();
//        final Iterator<IdentityKey> rhs = actual.iterator();
//        while (lhs.hasNext())
//        {
//            final IdentityKey lhsAtom = lhs.next();
//            if (rhs.hasNext())
//            {
//                final IdentityKey rhsAtom = rhs.next();
//                if (!lhsAtom.equals(rhsAtom))
//                {
//                    return false;
//                }
//            }
//            else
//            {
//                return false;
//            }
//        }
//        return !rhs.hasNext();
    }
}
