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
package org.genxdm.bridgekit.xs.complex;

import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.ElementUse;
import org.genxdm.xs.constraints.ValueConstraint;

public final class ParticleWithElementTerm extends ParticleImpl implements ElementUse
{
	private ValueConstraint m_valueConstraint = null;

	public ParticleWithElementTerm(final int minOccurs, final int maxOccurs, final ElementDefinition element)
	{
		super(minOccurs, maxOccurs, false, element);
	}

	public ParticleWithElementTerm(final int minOccurs, final ElementDefinition element)
	{
		super(minOccurs, -1, true, element);
	}

	public ValueConstraint getValueConstraint()
	{
		return m_valueConstraint;
	}

	public void setValueConstraint(final ValueConstraint valueConstraint)
	{
		assertNotLocked();
		this.m_valueConstraint = valueConstraint;
	}

	public ElementDefinition getTerm()
	{
		// We know this is safe by construction.
		return (ElementDefinition)m_term;
	}

	public ValueConstraint getEffectiveValueConstraint()
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
