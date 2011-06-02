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
package org.genxdm.bridgekit.xs;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.ValueConstraint;

public final class AttributeUseImpl<A> extends LockableImpl implements AttributeUse<A>
{
	private final AttributeDefinition<A> m_attribute;
	private final boolean m_isRequired;

	/**
	 * {value constraint} is mutable and default to <code>null</code>/
	 */
	private ValueConstraint<A> m_valueConstraint = null;

	public AttributeUseImpl(final boolean isRequired, final AttributeDefinition<A> attribute)
	{
		m_isRequired = isRequired;
		m_attribute = PreCondition.assertArgumentNotNull(attribute, "attribute");
	}

	public AttributeDefinition<A> getAttribute()
	{
		return m_attribute;
	}

	public ValueConstraint<A> getEffectiveValueConstraint()
	{
		if (null != m_valueConstraint)
		{
			return m_valueConstraint;
		}
		else
		{
			return m_attribute.getValueConstraint();
		}
	}

	public ValueConstraint<A> getValueConstraint()
	{
		return m_valueConstraint;
	}

	public boolean isRequired()
	{
		return m_isRequired;
	}

	public void setValueConstraint(final ValueConstraint<A> valueConstraint)
	{
		assertNotLocked();
		m_valueConstraint = valueConstraint;
	}
}
