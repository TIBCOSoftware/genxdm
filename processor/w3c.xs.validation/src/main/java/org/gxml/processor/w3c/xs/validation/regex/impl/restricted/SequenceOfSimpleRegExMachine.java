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
package org.gxml.processor.w3c.xs.validation.regex.impl.restricted;

import java.util.List;

import org.gxml.processor.w3c.xs.validation.regex.api.RegExBridge;
import org.gxml.processor.w3c.xs.validation.regex.api.RegExMachine;


final class SequenceOfSimpleRegExMachine<E, T> implements RegExMachine<E, T>
{
	private List<E> m_expected;
	private final RegExBridge<E, T> m_bridge;

	public SequenceOfSimpleRegExMachine(final List<E> subterms, final List<E> expectedFollowers, final RegExBridge<E, T> bridge)
	{
		m_subterms = subterms;
		m_index = 0;
		m_expected = expectedFollowers;
		m_bridge = PreCondition.assertArgumentNotNull(bridge);
	}

	public boolean step(final T token, final List<? super E> matchers)
	{
		if (m_index >= m_subterms.size())
		{
			return (token == null);
		}

		E next = m_subterms.get(m_index++);

		if (token != null)
		{
			if (m_bridge.matches(next, token))
			{
				matchers.add(next);
				return true;
			}
		}

		if (m_expected != null)
		{
			m_expected.add(next);
		}

		return false;
	}

	private List<E> m_subterms;
	private int m_index;
}
