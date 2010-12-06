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
package org.genxdm.bridgekit.axes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.genxdm.base.Model;
import org.genxdm.exceptions.PreCondition;


final class IteratorPrecedingSiblingAxis<N> implements Iterator<N>
{
	private N m_pending;
	private final Model<N> m_navigator;

	public IteratorPrecedingSiblingAxis(final N origin, final Model<N> navigator)
	{
		m_navigator = PreCondition.assertArgumentNotNull(navigator);
		m_pending = prime(origin, navigator);
	}

	public boolean hasNext()
	{
		return (null != m_pending);
	}

	public N next() throws NoSuchElementException
	{
		if (m_pending != null)
		{
			final N last = m_pending;
			m_pending = m_navigator.getPreviousSibling(m_pending);
			return last;
		}
		else
		{
			// The iteration has no more elements.
			throw new NoSuchElementException();
		}
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	public static <N> N prime(final N origin, final Model<N> navigator)
	{
		return navigator.getPreviousSibling(origin);
	}
}
