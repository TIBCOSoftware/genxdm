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

import java.util.List;


/**
 * A concrete class that allows our general simple type values to participate as keys in a {@link java.util.Map} by
 * providing equals and hashCode. This is really taking the high road as far as the specification is concerned, and the
 * W3C XML Schema specification really pre-dates xs:atomicType, so it might have chosen key values to be atomic if that
 * option had been available. What database uses non-atomic keys? <br/>
 * Anyway, to hell with the object creation, if performance sucks we can simplify and replace this class by an atom.
 * We'll assume that identity-constraints are being used for non-database purposes as well so that simple type values
 * are a possibility.
 */
final class IdentityKey<A>
{
	private final List<? extends A> m_value;

	public IdentityKey(final List<? extends A> value)
	{
		// This invariant may not hold up to the test of time.
		this.m_value = PreCondition.assertArgumentNotNull(value, "value");
	}

	public Iterable<? extends A> getTypedValue()
	{
		return m_value;
	}

	@Override
	public int hashCode()
	{
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof IdentityKey)
		{
			final IdentityKey<A> other = (IdentityKey<A>)obj;
			return ValidationSupport.equalValues(m_value, other.m_value);
		}
		else
		{
			return false;
		}
	}
}
