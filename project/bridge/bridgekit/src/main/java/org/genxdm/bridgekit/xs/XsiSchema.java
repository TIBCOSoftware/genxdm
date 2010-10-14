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

import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;

final class XsiSchema<A> implements SmComponentBag<A>
{
	private final HashMap<QName, SmAttribute<A>> m_attributes = new HashMap<QName, SmAttribute<A>>();

	private final HashMap<QName, SmSimpleType<A>> m_simpleTypes = new HashMap<QName, SmSimpleType<A>>();
	final SmAttribute<A> XSI_NIL;

	final SmAttribute<A> XSI_NO_NAMESPACE_SCHEMA_LOCATION;
	final SmAttribute<A> XSI_SCHEMA_LOCATION;
	final SmAttribute<A> XSI_TYPE;

	/**
	 * Constructs the W3C XML Schema native types and atributes.
	 */
	public XsiSchema(final SmCacheImpl<A> cache)
	{
		final SmSimpleType<A> ANY_URI = cache.getAtomicType(SmNativeType.ANY_URI);

		{
			final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
			final SmSimpleType<A> type = cache.getAtomicType(SmNativeType.QNAME);
			XSI_TYPE = new AttributeDeclTypeImpl<A>(name, SmScopeExtent.Global, type);
			register(XSI_TYPE);
		}

		{
			final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
			final SmSimpleType<A> type = cache.getAtomicType(SmNativeType.BOOLEAN);
			XSI_NIL = new AttributeDeclTypeImpl<A>(name, SmScopeExtent.Global, type);
			register(XSI_NIL);
		}

		{
			final SmSimpleType<A> LIST_OF_ANY_URI = new ListTypeImpl<A>(cache.generateUniqueName(), true, SmScopeExtent.Local, ANY_URI, cache.getSimpleUrType(), null, cache.getAtomBridge());
			register(LIST_OF_ANY_URI);
			final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
			XSI_SCHEMA_LOCATION = new AttributeDeclTypeImpl<A>(name, SmScopeExtent.Global, LIST_OF_ANY_URI);
			register(XSI_SCHEMA_LOCATION);
		}

		{
			final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "noNamespaceSchemaLocation");
			final SmSimpleType<A> type = cache.getSimpleType(cache.getNameBridge().nativeType(SmNativeType.ANY_URI));
			XSI_NO_NAMESPACE_SCHEMA_LOCATION = new AttributeDeclTypeImpl<A>(name, SmScopeExtent.Global, type);
			register(XSI_NO_NAMESPACE_SCHEMA_LOCATION);
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

	public SmAttribute<A> getAttribute(final QName name)
	{
		return m_attributes.get(name);
	}

	public SmAttributeGroup<A> getAttributeGroup(final QName name)
	{
		return null;
	}

	public Iterable<SmAttributeGroup<A>> getAttributeGroups()
	{
		return Collections.emptyList();
	}

	public Iterable<SmAttribute<A>> getAttributes()
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

	public SmElement<A> getElement(final QName name)
	{
		return null;
	}

	public Iterable<SmElement<A>> getElements()
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

	public SmModelGroup<A> getModelGroup(final QName name)
	{
		return null;
	}

	public Iterable<SmModelGroup<A>> getModelGroups()
	{
		return Collections.emptyList();
	}

	public SmNotation<A> getNotation(final QName name)
	{
		return null;
	}

	public Iterable<SmNotation<A>> getNotations()
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

	private void register(final SmAttribute<A> attribute)
	{
		m_attributes.put(attribute.getName(), attribute);
	}

	private void register(final SmSimpleType<A> simpleType)
	{
		m_simpleTypes.put(simpleType.getName(), simpleType);
	}
}
