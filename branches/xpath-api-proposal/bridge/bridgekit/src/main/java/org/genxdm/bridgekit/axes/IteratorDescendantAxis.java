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

import org.genxdm.Model;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.PreCondition;


public final class IteratorDescendantAxis<N> implements Iterator<N>
{
	private N m_pending;
	private final Model<N> m_navigator;

	// Take advantage of the fact that the tree implementation has a fast parent
	// pointer to avoid a stack. We keep track of where we started so as to
	// terminate the axis before going on to the origin's following siblings.
	private final N m_origin;

	public IteratorDescendantAxis(final N origin, final Model<N> navigator)
	{
		m_navigator = PreCondition.assertArgumentNotNull(navigator);
		m_origin = origin;
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
			m_pending = jump(m_pending, m_origin, m_navigator);
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
		return jump(origin, origin, navigator);
	}

	static <N> N jump(final N current, final N origin, final Model<N> navigator)
	{
		final N headChild = navigator.getFirstChild(current);
		if (null != headChild)
		{
			return headChild;
		}
		else
		{
			if ((current == origin) || Ordering.isSameNode(current, origin, navigator))
			{
				return null;
			}
			else
			{
				final N currentNextSibling = navigator.getNextSibling(current);

				if (null != currentNextSibling)
				{
					return currentNextSibling;
				}
				else
				{
					N moving = current;
					while (true)
					{
						moving = navigator.getParent(moving);
						if (null != moving)
						{
							if ((moving == origin) || Ordering.isSameNode(moving, origin, navigator))
							{
								// Terminate rather than doing the else clause.
								return null;
							}
							else
							{
								final N movingNextSibling = navigator.getNextSibling(moving);
								if (null != movingNextSibling)
								{
									return movingNextSibling;
								}
								else
								{
									// Go round again.
								}
							}
						}
						else
						{
							// This happens for a root node with no children.
							return null;
						}
					}
				}
			}
		}
	}
	
}
