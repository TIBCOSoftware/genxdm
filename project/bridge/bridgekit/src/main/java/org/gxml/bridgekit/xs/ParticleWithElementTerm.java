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
package org.gxml.bridgekit.xs;

import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.constraints.SmElementUse;
import org.genxdm.xs.constraints.SmValueConstraint;

public final class ParticleWithElementTerm<A> extends ParticleImpl<A> implements SmElementUse<A>
{
	private SmValueConstraint<A> m_valueConstraint = null;

	public ParticleWithElementTerm(final int minOccurs, final int maxOccurs, final SmElement<A> element)
	{
		super(minOccurs, maxOccurs, false, element);
	}

	public ParticleWithElementTerm(final int minOccurs, final SmElement<A> element)
	{
		super(minOccurs, -1, true, element);
	}

	public SmValueConstraint<A> getValueConstraint()
	{
		return m_valueConstraint;
	}

	public void setValueConstraint(final SmValueConstraint<A> valueConstraint)
	{
		assertNotLocked();
		this.m_valueConstraint = valueConstraint;
	}

	public SmElement<A> getTerm()
	{
		// We know this is safe by construction.
		return (SmElement<A>)m_term;
	}

	public SmValueConstraint<A> getEffectiveValueConstraint()
	{
		if (null != m_valueConstraint)
		{
			return m_valueConstraint;
		}
		else
		{
			return getTerm().getValueConstraint();
		}
	}
}
