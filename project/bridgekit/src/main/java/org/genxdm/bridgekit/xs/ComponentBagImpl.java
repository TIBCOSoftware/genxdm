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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

/**
 * A bag for storing of {@link org.genxdm.xs.components.SchemaComponent}(s).
 */
public final class ComponentBagImpl implements ComponentBag
{
    private final Map<QName, AttributeGroupDefinition> m_attributeGroups = new HashMap<QName, AttributeGroupDefinition>();

    private final Map<QName, AttributeDefinition> m_attributes = new HashMap<QName, AttributeDefinition>();
    private final Map<QName, ComplexType> m_complexTypes = new HashMap<QName, ComplexType>();
    private final Map<QName, ElementDefinition> m_elements = new HashMap<QName, ElementDefinition>();
    private final Map<QName, IdentityConstraint> m_identityConstraints = new HashMap<QName, IdentityConstraint>();
    private final Map<QName, ModelGroup> m_modelGroups = new HashMap<QName, ModelGroup>();
    private final Map<QName, NotationDefinition> m_notations = new HashMap<QName, NotationDefinition>();
    private final Map<QName, SimpleType> m_simpleTypes = new HashMap<QName, SimpleType>();

    public void add(final ElementDefinition element)
    {
        PreCondition.assertArgumentNotNull(element, "element");
        PreCondition.assertTrue(element.getScopeExtent() == ScopeExtent.Global, "{scope} of element " + element.getName() + " must be global");
        PreCondition.assertFalse(m_elements.containsKey(element.getName()), "element " + element.getName() + " must only be added once");
        m_elements.put(element.getName(), element);
    }

    public void add(final AttributeDefinition attribute)
    {
        PreCondition.assertArgumentNotNull(attribute, "attribute");
        PreCondition.assertTrue(attribute.getScopeExtent() == ScopeExtent.Global, "{scope} of attribute " + attribute.getName() + " must be global");
        PreCondition.assertFalse(m_attributes.containsKey(attribute.getName()), "attribute " + attribute.getName() + " must only be added once");
        m_attributes.put(attribute.getName(), attribute);
    }

    public void add(final AttributeGroupDefinition attributeGroup)
    {
        PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
        PreCondition.assertTrue(attributeGroup.getScopeExtent() == ScopeExtent.Global, "{scope} of attribute group " + attributeGroup.getName() + " must be global");
        PreCondition.assertFalse(m_attributeGroups.containsKey(attributeGroup.getName()), "attribute group " + attributeGroup.getName() + " must only be added once");
        m_attributeGroups.put(attributeGroup.getName(), attributeGroup);
    }

    public void add(final ComplexType complexType)
    {
        PreCondition.assertArgumentNotNull(complexType, "complexType");
        PreCondition.assertFalse(m_complexTypes.containsKey(complexType.getName()), "complexType " + complexType.getName() + " must only be added once");
        PreCondition.assertFalse(m_simpleTypes.containsKey(complexType.getName()), "complexType " + complexType.getName() + " has the same name as a simpleType");
        m_complexTypes.put(complexType.getName(), complexType);
    }

    public void add(final IdentityConstraint identityConstraint)
    {
        PreCondition.assertArgumentNotNull(identityConstraint, "identity-constraint");
        PreCondition.assertTrue(identityConstraint.getScopeExtent() == ScopeExtent.Global, "{scope} of identity-constraint " + identityConstraint.getName() + " must be global");
        PreCondition.assertFalse(m_identityConstraints.containsKey(identityConstraint.getName()), "identity-constraint " + identityConstraint.getName() + " must only be added once");
        m_identityConstraints.put(identityConstraint.getName(), identityConstraint);
    }

    public void add(final ModelGroup group)
    {
        PreCondition.assertArgumentNotNull(group, "group");
        PreCondition.assertTrue(group.getScopeExtent() == ScopeExtent.Global, "{scope} of group " + group.getName() + " must be global");
        PreCondition.assertFalse(m_modelGroups.containsKey(group.getName()), "group " + group.getName() + " must only be added once");
        m_modelGroups.put(group.getName(), group);
    }

    public void add(final NotationDefinition notation)
    {
        PreCondition.assertArgumentNotNull(notation, "notation");
        PreCondition.assertTrue(notation.getScopeExtent() == ScopeExtent.Global, "{scope} of notation " + notation.getName() + " must be global");
        PreCondition.assertFalse(m_notations.containsKey(notation.getName()), "notation " + notation.getName() + " must only be added once");
        m_notations.put(notation.getName(), notation);
    }

    public void add(final SimpleType simpleType)
    {
        PreCondition.assertArgumentNotNull(simpleType, "simpleType");
        PreCondition.assertFalse(m_simpleTypes.containsKey(simpleType.getName()), "simpleType " + simpleType.getName() + " must only be added once");
        PreCondition.assertFalse(m_complexTypes.containsKey(simpleType.getName()), "simpleType " + simpleType.getName() + " has the same name as a complexType");
        m_simpleTypes.put(simpleType.getName(), simpleType);
    }

    public ComplexType dereferenceComplexType(final QName name, final ComponentProvider existing)
    {
        PreCondition.assertArgumentNotNull(name);

        final ComplexType type = existing.getComplexType(name);
        if (null != type)
        {
            return type;
        }
        else
        {
            if (m_complexTypes.containsKey(name))
            {
                return m_complexTypes.get(name);
            }
            else
            {
                throw new RuntimeException(name.toString());
            }
        }
    }

    public SimpleType dereferenceSimpleType(final QName name, final ComponentProvider existing)
    {
        PreCondition.assertArgumentNotNull(name);

        final SimpleType type = existing.getSimpleType(name);
        if (null != type)
        {
            return type;
        }
        else
        {
            if (m_simpleTypes.containsKey(name))
            {
                return m_simpleTypes.get(name);
            }
            else
            {
                throw new RuntimeException(name.toString());
            }
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
        return m_attributeGroups.get(name);
    }

    public Iterable<AttributeGroupDefinition> getAttributeGroups()
    {
        return m_attributeGroups.values();
    }

    public Iterable<AttributeDefinition> getAttributes()
    {
        return m_attributes.values();
    }

    public ComplexType getComplexType(final QName name)
    {
        return m_complexTypes.get(name);
    }

    public Iterable<ComplexType> getComplexTypes()
    {
        return m_complexTypes.values();
    }

    public ElementDefinition getElement(final QName name)
    {
        return m_elements.get(name);
    }

    public Iterable<ElementDefinition> getElements()
    {
        return m_elements.values();
    }

    public IdentityConstraint getIdentityConstraint(final QName name)
    {
        return m_identityConstraints.get(name);
    }

    public Iterable<IdentityConstraint> getIdentityConstraints()
    {
        return m_identityConstraints.values();
    }

    public ModelGroup getModelGroup(final QName name)
    {
        return m_modelGroups.get(name);
    }

    public Iterable<ModelGroup> getModelGroups()
    {
        return m_modelGroups.values();
    }

    public NotationDefinition getNotation(final QName name)
    {
        return m_notations.get(name);
    }

    public Iterable<NotationDefinition> getNotations()
    {
        return m_notations.values();
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
        if (hasSimpleType(name))
        {
            return getSimpleType(name);
        }
        else if (hasComplexType(name))
        {
            return getComplexType(name);
        }
        else
        {
            return null;
        }
    }

    public boolean hasAttribute(final QName name)
    {
        return m_attributes.containsKey(name);
    }

    public boolean hasAttributeGroup(final QName name)
    {
        return m_attributeGroups.containsKey(name);
    }

    public boolean hasComplexType(final QName name)
    {
        return m_complexTypes.containsKey(name);
    }

    public boolean hasElement(final QName name)
    {
        return m_elements.containsKey(name);
    }

    public boolean hasIdentityConstraint(final QName name)
    {
        return m_identityConstraints.containsKey(name);
    }

    public boolean hasModelGroup(final QName name)
    {
        return m_modelGroups.containsKey(name);
    }

    public boolean hasNotation(final QName name)
    {
        return m_notations.containsKey(name);
    }

    public boolean hasSimpleType(final QName name)
    {
        return m_simpleTypes.containsKey(name);
    }

    public boolean hasType(final QName name)
    {
        return hasSimpleType(name) || hasComplexType(name);
    }

    public boolean isComplexType(final QName name, final ComponentProvider existing)
    {
        PreCondition.assertArgumentNotNull(name);

        final ComplexType type = existing.getComplexType(name);
        if (null != type)
        {
            return true;
        }
        else
        {
            return m_complexTypes.containsKey(name);
        }
    }

    public boolean isSimpleType(final QName name, final ComponentProvider existing)
    {
        PreCondition.assertArgumentNotNull(name);

        final SimpleType type = existing.getSimpleType(name);
        if (null != type)
        {
            return true;
        }
        else
        {
            return m_simpleTypes.containsKey(name);
        }
    }
}
