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

final class IteratorChildAxisElementsByName<N> implements Iterator<N>
{
	private N m_pending;
	private final String m_namespaceURI;
	private final String m_localName;
	private final Model<N> m_model;

	public IteratorChildAxisElementsByName(final N origin, final String namespaceURI, final String localName, final Model<N> model)
	{
		this.m_model = model;
		this.m_namespaceURI = namespaceURI;
		this.m_localName = localName;
		this.m_pending = getNextElementByName(model.getFirstChild(origin), namespaceURI, localName, model);
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
			m_pending = getNextElementByName(m_model.getNextSibling(m_pending), m_namespaceURI, m_localName, m_model);
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

	private static <N> N getNextElementByName(final N initial, final String namespaceURI, final String localName, final Model<N> model)
	{
		N candidate = initial;
		while (null != candidate)
		{
			if (model.isElement(candidate) && model.matches(candidate, namespaceURI, localName))
			{
				return candidate;
			}
			else
			{
				candidate = model.getNextSibling(candidate);
			}
		}
		return null;
	}
}
