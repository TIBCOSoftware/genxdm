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
package org.genxdm.processor.w3c.xs.xmlrep;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLAttribute;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLAttributeGroup;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLElement;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLModelGroup;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLType;

public final class XMLScope
{
    private final boolean m_isGlobal;
    private final XMLType m_type;
    private final XMLAttribute m_attribute;
    private final XMLElement m_element;
    private final XMLModelGroup m_group;
    private final XMLAttributeGroup m_attributeGroup;

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

    public XMLScope(final XMLType type)
    {
        m_isGlobal = false;
        m_type = PreCondition.assertArgumentNotNull(type);
        m_attribute = null;
        m_element = null;
        m_group = null;
        m_attributeGroup = null;
    }

    public XMLScope(final XMLAttribute attribute)
    {
        m_isGlobal = false;
        m_type = null;
        m_attribute = PreCondition.assertArgumentNotNull(attribute);
        m_element = null;
        m_group = null;
        m_attributeGroup = null;
    }

    public XMLScope(final XMLElement element)
    {
        m_isGlobal = false;
        m_type = null;
        m_attribute = null;
        m_element = PreCondition.assertArgumentNotNull(element);
        m_group = null;
        m_attributeGroup = null;
    }

    public XMLScope(final XMLModelGroup group)
    {
        m_isGlobal = false;
        m_type = null;
        m_attribute = null;
        m_element = null;
        m_group = PreCondition.assertArgumentNotNull(group);
        m_attributeGroup = null;
    }

    public XMLScope(final XMLAttributeGroup attributeGroup)
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

    public XMLType getType()
    {
        return m_type;
    }

    public XMLAttribute getAttribute()
    {
        return m_attribute;
    }

    public XMLElement getElement()
    {
        return m_element;
    }

    public XMLModelGroup getModelGroup()
    {
        return m_group;
    }

    public XMLAttributeGroup getAttributeGroup()
    {
        return m_attributeGroup;
    }
}
