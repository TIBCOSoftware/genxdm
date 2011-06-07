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
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

final class XmlSchema implements ComponentBag
{
	private final HashMap<QName, AttributeDefinition> m_attributes = new HashMap<QName, AttributeDefinition>();

	private final HashMap<QName, SimpleType> m_simpleTypes = new HashMap<QName, SimpleType>();
	final AttributeDefinition XML_BASE;

	final AttributeDefinition XML_LANG;

	/**
	 * Constructs the W3C XML Schema native types and atributes.
	 */
	public XmlSchema(final SchemaCacheImpl cache)
	{
	    // these are in blocks so that we can call register using a standard pattern.
		{
			final QName name = new QName(XMLConstants.XML_NS_URI, "base", XMLConstants.XML_NS_PREFIX);
			final SimpleType type = cache.getAtomicType(NativeType.ANY_URI);
			XML_BASE = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
			register(XML_BASE);
		}
		{
			final QName name = new QName(XMLConstants.XML_NS_URI, "lang", XMLConstants.XML_NS_PREFIX);
			final SimpleType type = cache.getAtomicType(NativeType.LANGUAGE);
			XML_LANG = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
			register(XML_LANG);
		}
	}

	public SimpleType getAtomicType(final QName name)
	{
		final SimpleType simpleType = getSimpleType(name);
		if (simpleType.isAtomicType())
		{
			return simpleType;
		}
		else
		{
			return null;
		}
	}

	public AttributeDefinition getAttribute(final QName name)
	{
		return m_attributes.get(name);
	}

	public AttributeGroupDefinition getAttributeGroup(final QName name)
	{
		return null;
	}

	public Iterable<AttributeGroupDefinition> getAttributeGroups()
	{
		return Collections.emptyList();
	}

	public Iterable<AttributeDefinition> getAttributes()
	{
		return m_attributes.values();
	}

	public ComplexType getComplexType(final QName name)
	{
		return null;
	}

	public Iterable<ComplexType> getComplexTypes()
	{
		return Collections.emptyList();
	}

	public ElementDefinition getElement(final QName name)
	{
		return null;
	}

	public Iterable<ElementDefinition> getElements()
	{
		return Collections.emptyList();
	}

	public IdentityConstraint getIdentityConstraint(final QName name)
	{
		return null;
	}

	public Iterable<IdentityConstraint> getIdentityConstraints()
	{
		return Collections.emptyList();
	}

	public ModelGroup getModelGroup(final QName name)
	{
		return null;
	}

	public Iterable<ModelGroup> getModelGroups()
	{
		return Collections.emptyList();
	}

	public NotationDefinition getNotation(final QName name)
	{
		return null;
	}

	public Iterable<NotationDefinition> getNotations()
	{
		return Collections.emptyList();
	}

	public SimpleType getSimpleType(final QName name)
	{
		return m_simpleTypes.get(name);
	}

	public Iterable<SimpleType> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public Type getType(final QName name)
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

	private void register(final AttributeDefinition attribute)
	{
		m_attributes.put(attribute.getName(), attribute);
	}
}
