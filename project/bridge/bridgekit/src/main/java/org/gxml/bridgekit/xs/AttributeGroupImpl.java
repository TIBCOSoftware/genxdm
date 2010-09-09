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

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.components.SmAttributeGroup;
import org.gxml.xs.components.SmWildcard;
import org.gxml.xs.constraints.SmAttributeUse;
import org.gxml.xs.enums.SmScopeExtent;

public final class AttributeGroupImpl<A> extends NamedComponentImpl<A> implements SmAttributeGroup<A>
{
	private final Iterable<SmAttributeUse<A>> m_attributeUses;
	private final boolean m_hasAttributeUses;
	private final SmWildcard<A> m_wildcard;

	public AttributeGroupImpl(final QName name, final SmScopeExtent scope, final Iterable<SmAttributeUse<A>> attributeUses, final SmWildcard<A> wildcard)
	{
		super(name, false, scope);
		if (null != attributeUses)
		{
			if (attributeUses.iterator().hasNext())
			{
				m_hasAttributeUses = true;
				m_attributeUses = attributeUses;
			}
			else
			{
				m_hasAttributeUses = false;
				m_attributeUses = null;
			}
		}
		else
		{
			m_hasAttributeUses = false;
			m_attributeUses = null;
		}
		m_wildcard = wildcard;
	}

	public Iterable<SmAttributeUse<A>> getAttributeUses()
	{
		PreCondition.assertTrue(m_hasAttributeUses, "hasAttributeUses() -> false");
		return m_attributeUses;
	}

	public SmWildcard<A> getWildcard()
	{
		return m_wildcard;
	}

	public boolean hasAttributeUses()
	{
		return m_hasAttributeUses;
	}
}
