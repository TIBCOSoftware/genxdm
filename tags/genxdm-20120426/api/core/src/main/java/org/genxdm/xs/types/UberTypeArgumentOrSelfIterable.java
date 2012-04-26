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
package org.genxdm.xs.types;

import java.util.Iterator;

import org.genxdm.exceptions.PreCondition;

/**
 * Used internally by {@link NativeType} to return ancestors.
 */
final class UberTypeArgumentOrSelfIterable implements Iterable<NativeType>
{
    private final NativeType m_origin;
    private final boolean m_promotions;

    public UberTypeArgumentOrSelfIterable(final NativeType origin, final boolean promotions)
    {
        m_origin = PreCondition.assertArgumentNotNull(origin, "origin");
        m_promotions = promotions;
    }

    public Iterator<NativeType> iterator()
    {
        return new UberTypeArgumentOrSelfIterator(m_origin, m_promotions);
    }
}
