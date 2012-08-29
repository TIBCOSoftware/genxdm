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
import org.genxdm.bridgekit.xs.complex.ListTypeImpl;
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

final class XsiSchema implements ComponentBag
{
    private final HashMap<QName, AttributeDefinition> m_attributes = new HashMap<QName, AttributeDefinition>();

    private final HashMap<QName, SimpleType> m_simpleTypes = new HashMap<QName, SimpleType>();
    final AttributeDefinition XSI_NIL;

    final AttributeDefinition XSI_NO_NAMESPACE_SCHEMA_LOCATION;
    final AttributeDefinition XSI_SCHEMA_LOCATION;
    final AttributeDefinition XSI_TYPE;

    /**
     * Constructs the W3C XML Schema native types and atributes.
     */
    public XsiSchema()
    {
        final SimpleType ANY_URI = BuiltInSchema.SINGLETON.ANY_URI;

        {
            final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
            final SimpleType type = BuiltInSchema.SINGLETON.QNAME;
            XSI_TYPE = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
            register(XSI_TYPE);
        }

        {
            final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
            final SimpleType type = BuiltInSchema.SINGLETON.BOOLEAN;
            XSI_NIL = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
            register(XSI_NIL);
        }

        {
            final SimpleType LIST_OF_ANY_URI = new ListTypeImpl(new QName("http://genxdm.org/typed/local-types", "list-of-any-uri"), true, ScopeExtent.Local, ANY_URI, BuiltInSchema.SINGLETON.ANY_SIMPLE_TYPE, null);
            register(LIST_OF_ANY_URI);
            final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
            XSI_SCHEMA_LOCATION = new AttributeDeclTypeImpl(name, ScopeExtent.Global, LIST_OF_ANY_URI);
            register(XSI_SCHEMA_LOCATION);
        }

        {
            final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "noNamespaceSchemaLocation");
            final SimpleType type = BuiltInSchema.SINGLETON.ANY_URI;
            XSI_NO_NAMESPACE_SCHEMA_LOCATION = new AttributeDeclTypeImpl(name, ScopeExtent.Global, type);
            register(XSI_NO_NAMESPACE_SCHEMA_LOCATION);
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

    private void register(final SimpleType simpleType)
    {
        m_simpleTypes.put(simpleType.getName(), simpleType);
    }
}
