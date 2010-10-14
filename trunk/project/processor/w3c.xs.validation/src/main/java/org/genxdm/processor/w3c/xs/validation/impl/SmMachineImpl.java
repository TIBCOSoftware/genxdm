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

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExMachine;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmParticleTerm;
import org.genxdm.xs.components.SmWildcard;



final class SmMachineImpl<A> implements SmContentFiniteStateMachine<A>
{
	private SmElement<A> m_element;
	private final LinkedList<ValidationExpr<A, SmParticleTerm<A>>> m_matchers = new LinkedList<ValidationExpr<A, SmParticleTerm<A>>>();
	private final RegExMachine<ValidationExpr<A, SmParticleTerm<A>>, QName> m_regexm;
	private SmWildcard<A> m_wildcard;

	public SmMachineImpl(final RegExMachine<ValidationExpr<A, SmParticleTerm<A>>, QName> regexm)
	{
		m_regexm = PreCondition.assertArgumentNotNull(regexm, "regexm");
	}

	public boolean end()
	{
		return m_regexm.step(null, null);
	}

	public SmElement<A> getElement()
	{
		return m_element;
	}

	private SmParticleTerm<A> getParticleTerm()
	{
		final int size = m_matchers.size();
		if (size > 0)
		{
			final ValidationExpr<A, SmParticleTerm<A>> expr = m_matchers.get(0);
			return expr.getParticleTerm();
		}
		else
		{
			return null;
		}
	}

	public SmWildcard<A> getWildcard()
	{
		return m_wildcard;
	}

	public boolean isElementMatch()
	{
		return (null != m_element);
	}

	public boolean isWildcardMatch()
	{
		return (null != m_wildcard);
	}

	public boolean step(final QName name)
	{
		m_matchers.clear();
		final boolean stepped = m_regexm.step(name, m_matchers);
		if (stepped)
		{
			final SmParticleTerm<A> term = getParticleTerm();
			if (term instanceof SmElement<?>)
			{
				m_element = (SmElement<A>)term;
				m_wildcard = null;
			}
			else if (term instanceof SmWildcard<?>)
			{
				m_element = null;
				m_wildcard = (SmWildcard<A>)term;
			}
			else
			{
				throw new AssertionError(term);
			}
		}
		return stepped;
	}

	@Override
	public final String toString()
	{
		if (isElementMatch())
		{
			return m_element.getName().toString();
		}
		else if (isWildcardMatch())
		{
			return m_wildcard.getProcessContents().name();
		}
		else
		{
			return "";
		}
	}
}
