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
package org.gxml.xs.types;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.gxml.exceptions.PreCondition;

/**
 * Used internally by {@link SmNativeType} to return ancestors.
 */
final class UberTypeArgumentOrSelfIterator implements Iterator<SmNativeType>
{
	private SmNativeType m_pending;
	private final boolean m_promotions;

	public UberTypeArgumentOrSelfIterator(final SmNativeType origin, final boolean promotions)
	{
		m_pending = PreCondition.assertArgumentNotNull(origin, "origin");
		m_promotions = promotions;
	}

	public boolean hasNext()
	{
		return (null != m_pending);
	}

	public SmNativeType next()
	{
		if (null != m_pending)
		{
			final SmNativeType next = m_pending;

			m_pending = computePending(m_pending);

			return next;
		}
		else
		{
			throw new NoSuchElementException();
		}
	}

	private SmNativeType computePending(final SmNativeType type)
	{
		if (m_promotions)
		{
			return type.getPromotion();
		}
		else
		{
			return type.getParent();
		}
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
