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
package org.gxml.bridgekit.axes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.gxml.base.Model;
import org.gxml.exceptions.PreCondition;

final class IteratorChildAxis<N> implements Iterator<N>
{
	private N m_pending;
	private final Model<N> m_navigator;

	public IteratorChildAxis(final N origin, final Model<N> navigator)
	{
		this.m_navigator = PreCondition.assertArgumentNotNull(navigator);
		this.m_pending = navigator.getFirstChild(origin);
	}

	public boolean hasNext()
	{
		return (null != m_pending);
	}

	public N next()
	{
		if (m_pending != null)
		{
			final N last = m_pending;
			m_pending = m_navigator.getNextSibling(m_pending);
			return last;
		}
		else
		{
			throw new NoSuchElementException();
		}
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
