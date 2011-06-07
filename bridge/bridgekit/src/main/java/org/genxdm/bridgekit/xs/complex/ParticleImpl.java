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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.ParticleTerm;

abstract class ParticleImpl extends LockableImpl implements SchemaParticle
{
	private static void checkOccurs(final int minOccurs, final int maxOccurs, final boolean isMaxOccursUnbounded)
	{
		PreCondition.assertTrue(minOccurs >= 0, "minOccurs must be non-negative");
	}

	private final int m_maxOccurs;
	private final int m_minOccurs;
	private final boolean isMaxOccursUnbounded;

	protected final ParticleTerm m_term;

	public ParticleImpl(final int minOccurs, final int maxOccurs, final boolean isMaxOccursUnbounded, final ParticleTerm term)
	{
		checkOccurs(minOccurs, maxOccurs, isMaxOccursUnbounded);
		m_minOccurs = minOccurs;
		m_maxOccurs = maxOccurs;
		this.isMaxOccursUnbounded = isMaxOccursUnbounded;
		m_term = PreCondition.assertArgumentNotNull(term, "term");
	}

	public final int getMaxOccurs()
	{
		PreCondition.assertFalse(isMaxOccursUnbounded(), "isMaxOccursUnbounded()");
		return m_maxOccurs;
	}

	public final int getMinOccurs()
	{
		return m_minOccurs;
	}

	public boolean isMaxOccursUnbounded()
	{
		return isMaxOccursUnbounded;
	}
}
