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

import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.WildcardUse;

public final class ParticleWithWildcardTerm extends ParticleImpl implements WildcardUse
{
	public ParticleWithWildcardTerm(final int minOccurs, final int maxOccurs, final SchemaWildcard wildcard)
	{
		super(minOccurs, maxOccurs, false, wildcard);
	}

	public ParticleWithWildcardTerm(final int minOccurs, final SchemaWildcard wildcard)
	{
		super(minOccurs, -1, true, wildcard);
	}

	public SchemaWildcard getTerm()
	{
		// We know this is safe by construction.
		return (SchemaWildcard)m_term;
	}
}
