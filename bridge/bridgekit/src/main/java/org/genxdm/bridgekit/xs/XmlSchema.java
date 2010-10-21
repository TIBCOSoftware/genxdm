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

import java.util.Collections;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;

final class XmlSchema<A> implements ComponentBag<A>
{
	private final HashMap<QName, AttributeDefinition<A>> m_attributes = new HashMap<QName, AttributeDefinition<A>>();

	private final HashMap<QName, SmSimpleType<A>> m_simpleTypes = new HashMap<QName, SmSimpleType<A>>();
	final AttributeDefinition<A> XML_BASE;

	final AttributeDefinition<A> XML_LANG;

	/**
	 * Constructs the W3C XML Schema native types and atributes.
	 */
	public XmlSchema(final SmCacheImpl<A> cache)
	{
	    // these are in blocks so that we can call register using a standard pattern.
		{
			final QName name = new QName(XMLConstants.XML_NS_URI, "base", XMLConstants.XML_NS_PREFIX);
			final SmSimpleType<A> type = cache.getAtomicType(SmNativeType.ANY_URI);
			XML_BASE = new AttributeDeclTypeImpl<A>(name, ScopeExtent.Global, type);
			register(XML_BASE);
		}
		{
			final QName name = new QName(XMLConstants.XML_NS_URI, "lang", XMLConstants.XML_NS_PREFIX);
			final SmSimpleType<A> type = cache.getAtomicType(SmNativeType.LANGUAGE);
			XML_LANG = new AttributeDeclTypeImpl<A>(name, ScopeExtent.Global, type);
			register(XML_LANG);
		}
	}

	public SmSimpleType<A> getAtomicType(final QName name)
	{
		final SmSimpleType<A> simpleType = getSimpleType(name);
		if (simpleType.isAtomicType())
		{
			return simpleType;
		}
		else
		{
			return null;
		}
	}

	public AttributeDefinition<A> getAttribute(final QName name)
	{
		return m_attributes.get(name);
	}

	public AttributeGroupDefinition<A> getAttributeGroup(final QName name)
	{
		return null;
	}

	public Iterable<AttributeGroupDefinition<A>> getAttributeGroups()
	{
		return Collections.emptyList();
	}

	public Iterable<AttributeDefinition<A>> getAttributes()
	{
		return m_attributes.values();
	}

	public SmComplexType<A> getComplexType(final QName name)
	{
		return null;
	}

	public Iterable<SmComplexType<A>> getComplexTypes()
	{
		return Collections.emptyList();
	}

	public ElementDefinition<A> getElement(final QName name)
	{
		return null;
	}

	public Iterable<ElementDefinition<A>> getElements()
	{
		return Collections.emptyList();
	}

	public SmIdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return null;
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return Collections.emptyList();
	}

	public ModelGroup<A> getModelGroup(final QName name)
	{
		return null;
	}

	public Iterable<ModelGroup<A>> getModelGroups()
	{
		return Collections.emptyList();
	}

	public NotationDefinition<A> getNotation(final QName name)
	{
		return null;
	}

	public Iterable<NotationDefinition<A>> getNotations()
	{
		return Collections.emptyList();
	}

	public SmSimpleType<A> getSimpleType(final QName name)
	{
		return m_simpleTypes.get(name);
	}

	public Iterable<SmSimpleType<A>> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public SmType<A> getType(final QName name)
	{
		return getSimpleType(name);
	}

	public boolean hasAttribute(final QName name)
	{
		return m_attributes.containsKey(name);
	}

	public boolean hasAttributeGroup(final QName name)
	{
		return false;
	}

	public boolean hasComplexType(final QName name)
	{
		return false;
	}

	public boolean hasElement(final QName name)
	{
		return false;
	}

	public boolean hasIdentityConstraint(final QName name)
	{
		return false;
	}

	public boolean hasModelGroup(final QName name)
	{
		return false;
	}

	public boolean hasNotation(final QName name)
	{
		return false;
	}

	public boolean hasSimpleType(final QName name)
	{
		return m_simpleTypes.containsKey(name);
	}

	public boolean hasType(final QName name)
	{
		return hasSimpleType(name);
	}

	private void register(final AttributeDefinition<A> attribute)
	{
		m_attributes.put(attribute.getName(), attribute);
	}
}
