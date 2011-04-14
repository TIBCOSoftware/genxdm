/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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

import org.genxdm.Model;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.PreCondition;


final class IteratorPrecedingAxis<N> implements Iterator<N>
{
	private N m_next;
	private N m_exclude;
	private final Model<N> navigator;

	public IteratorPrecedingAxis(final N origin, final Model<N> navigator)
	{
		this.navigator = PreCondition.assertArgumentNotNull(navigator);
		m_next = prime(origin);
	}

	public boolean hasNext()
	{
		return (null != m_next);
	}

	public N next() throws NoSuchElementException
	{
		if (m_next != null)
		{
			final N last = m_next;

			m_next = next(m_next);

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

	private N prime(final N origin)
	{
		if (navigator.isAttribute(origin) || navigator.isNamespace(origin))
		{
			final N parent = navigator.getParent(origin);
			if (null != parent)
			{
				m_exclude = navigator.getParent(parent);
				return next(parent);
			}
			else
			{
				return null;
			}
		}
		else
		{
			m_exclude = navigator.getParent(origin);
			return next(origin);
		}
	}

	private N next(final N current)
	{
		N candidate = navigator.getPreviousSibling(current);
		if (null != candidate)
		{
			// Return the deepest and last node starting from this one.
			N result = candidate;
			candidate = navigator.getLastChild(result);
			while (null != candidate)
			{
				result = candidate;
				candidate = navigator.getLastChild(result);
			}
			return result;
		}
		else
		{
			candidate = navigator.getParent(current);
			if (candidate != null)
			{
				if (!Ordering.isSameNode(candidate, m_exclude, navigator))
				{
					return candidate;
				}
				else
				{
					m_exclude = navigator.getParent(m_exclude);
					return next(candidate);
				}
			}
			else
			{
				// terminates.
				return null;
			}
		}
	}
}
