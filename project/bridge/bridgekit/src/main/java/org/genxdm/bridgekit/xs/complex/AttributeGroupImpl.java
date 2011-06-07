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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.enums.ScopeExtent;

public final class AttributeGroupImpl extends NamedComponentImpl implements AttributeGroupDefinition
{
	private final Iterable<AttributeUse> m_attributeUses;
	private final boolean m_hasAttributeUses;
	private final SchemaWildcard m_wildcard;

	public AttributeGroupImpl(final QName name, final ScopeExtent scope, final Iterable<AttributeUse> attributeUses, final SchemaWildcard wildcard)
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

	public Iterable<AttributeUse> getAttributeUses()
	{
		PreCondition.assertTrue(m_hasAttributeUses, "hasAttributeUses() -> false");
		return m_attributeUses;
	}

	public SchemaWildcard getWildcard()
	{
		return m_wildcard;
	}

	public boolean hasAttributeUses()
	{
		return m_hasAttributeUses;
	}
}
