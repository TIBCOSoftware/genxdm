/*
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

import org.genxdm.bridgekit.xs.complex.AttributeDeclTypeImpl;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

final class XmlSchema implements ComponentBag
{
    private final HashMap<QName, AttributeDefinition> m_attributes = new HashMap<QName, AttributeDefinition>();

    private final HashMap<QName, SimpleType> m_simpleTypes = new HashMap<QName, SimpleType>();
    final AttributeDefinition XML_BASE;

    final AttributeDefinition XML_LANG;

    /**
     * Constructs the W3C XML Schema native types and atributes.
     */
    public XmlSchema()
    {
        // these are in blocks so that we can call register using a standard pattern.
        {
            final QName name = new QName(XMLConstants.XML_NS_URI, "base", XMLConstants.XML_NS_PREFIX);
            final SimpleType type = BuiltInSchema.SINGLETON.ANY_URI;
            XML_BASE = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
            register(XML_BASE);
        }
        {
            final QName name = new QName(XMLConstants.XML_NS_URI, "lang", XMLConstants.XML_NS_PREFIX);
            final SimpleType type = BuiltInSchema.SINGLETON.LANGUAGE;
            XML_LANG = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
            register(XML_LANG);
        }
    }

    public Iterable<AttributeGroupDefinition> getAttributeGroups()
    {
        return Collections.emptyList();
    }

    public Iterable<AttributeDefinition> getAttributes()
    {
        return m_attributes.values();
    }

    public Iterable<ComplexType> getComplexTypes()
    {
        return Collections.emptyList();
    }

    public Iterable<ElementDefinition> getElements()
    {
        return Collections.emptyList();
    }

    public Iterable<IdentityConstraint> getIdentityConstraints()
    {
        return Collections.emptyList();
    }

    public Iterable<ModelGroup> getModelGroups()
    {
        return Collections.emptyList();
    }

    public Iterable<NotationDefinition> getNotations()
    {
        return Collections.emptyList();
    }

    public Iterable<SimpleType> getSimpleTypes()
    {
        return m_simpleTypes.values();
    }

    private void register(final AttributeDefinition attribute)
    {
        m_attributes.put(attribute.getName(), attribute);
    }
}
