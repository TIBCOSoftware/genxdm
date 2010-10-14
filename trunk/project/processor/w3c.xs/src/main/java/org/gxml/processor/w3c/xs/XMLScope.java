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
package org.gxml.processor.w3c.xs;

import org.genxdm.exceptions.PreCondition;

final class XMLScope<A>
{
	private final boolean m_isGlobal;
	private final XMLType<A> m_type;
	private final XMLAttribute<A> m_attribute;
	private final XMLElement<A> m_element;
	private final XMLModelGroup<A> m_group;
	private final XMLAttributeGroup<A> m_attributeGroup;

	// public static final XMLScope<?, ?> Global = new XMLScope<Object, Object>();

	public XMLScope()
	{
		m_isGlobal = true;
		m_type = null;
		m_attribute = null;
		m_element = null;
		m_group = null;
		m_attributeGroup = null;
	}

	public XMLScope(final XMLType<A> type)
	{
		m_isGlobal = false;
		m_type = PreCondition.assertArgumentNotNull(type);
		m_attribute = null;
		m_element = null;
		m_group = null;
		m_attributeGroup = null;
	}

	public XMLScope(final XMLAttribute<A> attribute)
	{
		m_isGlobal = false;
		m_type = null;
		m_attribute = PreCondition.assertArgumentNotNull(attribute);
		m_element = null;
		m_group = null;
		m_attributeGroup = null;
	}

	public XMLScope(final XMLElement<A> element)
	{
		m_isGlobal = false;
		m_type = null;
		m_attribute = null;
		m_element = PreCondition.assertArgumentNotNull(element);
		m_group = null;
		m_attributeGroup = null;
	}

	public XMLScope(final XMLModelGroup<A> group)
	{
		m_isGlobal = false;
		m_type = null;
		m_attribute = null;
		m_element = null;
		m_group = PreCondition.assertArgumentNotNull(group);
		m_attributeGroup = null;
	}

	public XMLScope(final XMLAttributeGroup<A> attributeGroup)
	{
		m_isGlobal = false;
		m_type = null;
		m_attribute = null;
		m_element = null;
		m_group = null;
		m_attributeGroup = PreCondition.assertArgumentNotNull(attributeGroup);
	}

	public boolean isGlobal()
	{
		return m_isGlobal;
	}

	public boolean isLocal()
	{
		return !m_isGlobal;
	}

	public XMLType<A> getType()
	{
		return m_type;
	}

	public XMLAttribute<A> getAttribute()
	{
		return m_attribute;
	}

	public XMLElement<A> getElement()
	{
		return m_element;
	}

	public XMLModelGroup<A> getModelGroup()
	{
		return m_group;
	}

	public XMLAttributeGroup<A> getAttributeGroup()
	{
		return m_attributeGroup;
	}
}
