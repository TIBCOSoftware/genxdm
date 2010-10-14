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

import org.genxdm.base.Model;
import org.genxdm.exceptions.PreCondition;


final class IteratorDescendantOrSelfAxis<N> implements Iterator<N>
{
	private N m_pending;
	private final Model<N> m_navigator;

	// Take advantage of the fact that the tree implementation has a fast parent
	// pointer to avoid a stack. We keep track of where we started so as to
	// terminate the axis before going on to the origin's following siblings.
	private final N m_origin;

	public IteratorDescendantOrSelfAxis(final N origin, final Model<N> navigator)
	{
		this.m_navigator = PreCondition.assertArgumentNotNull(navigator);
		this.m_origin = PreCondition.assertArgumentNotNull(origin);
		this.m_pending = origin;
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
			m_pending = IteratorDescendantAxis.jump(m_pending, m_origin, m_navigator);
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
}
