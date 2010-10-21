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

import java.util.ArrayList;

import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.resolve.LocationInSchema;

final class IdentityDanglingReference<A>
{
	private final SmIdentityConstraint<A> m_constraint;
	private final ArrayList<IdentityKey<A>> m_keys;
	private final LocationInSchema m_location;

	public IdentityDanglingReference(final SmIdentityConstraint<A> constraint, final ArrayList<IdentityKey<A>> keys, final LocationInSchema location)
	{
		m_constraint = PreCondition.assertArgumentNotNull(constraint, "constraint");
		m_keys = PreCondition.assertArgumentNotNull(keys, "keys");
		m_location = location;
	}

	public LocationInSchema getLocationContext()
	{
		return m_location;
	}

	public SmIdentityConstraint<A> getConstraint()
	{
		return m_constraint;
	}

	public ArrayList<IdentityKey<A>> getKeys()
	{
		return m_keys;
	}
}
