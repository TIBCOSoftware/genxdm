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
package org.genxdm.processor.w3c.xs;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ComponentProvider;
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
final class SmComponentBagImpl<A> implements ComponentBag<A>
{
	private final Map<QName, AttributeGroupDefinition<A>> m_attributeGroups = new HashMap<QName, AttributeGroupDefinition<A>>();

	private final Map<QName, AttributeDefinition<A>> m_attributes = new HashMap<QName, AttributeDefinition<A>>();
	private final Map<QName, ComplexType<A>> m_complexTypes = new HashMap<QName, ComplexType<A>>();
	private final Map<QName, ElementDefinition<A>> m_elements = new HashMap<QName, ElementDefinition<A>>();
	private final Map<QName, IdentityConstraint<A>> m_identityConstraints = new HashMap<QName, IdentityConstraint<A>>();
	private final Map<QName, ModelGroup<A>> m_modelGroups = new HashMap<QName, ModelGroup<A>>();
	private final Map<QName, NotationDefinition<A>> m_notations = new HashMap<QName, NotationDefinition<A>>();
	private final Map<QName, SimpleType<A>> m_simpleTypes = new HashMap<QName, SimpleType<A>>();

	public void add(final ElementDefinition<A> element)
	{
		PreCondition.assertArgumentNotNull(element, "element");
		PreCondition.assertTrue(element.getScopeExtent() == ScopeExtent.Global, "{scope} of element must be global");
		PreCondition.assertFalse(m_elements.containsKey(element.getName()), "element must only be added once");
		m_elements.put(element.getName(), element);
	}

	public void add(final AttributeDefinition<A> attribute)
	{
		PreCondition.assertArgumentNotNull(attribute, "attribute");
		PreCondition.assertTrue(attribute.getScopeExtent() == ScopeExtent.Global, "{scope} of attribute must be global");
		PreCondition.assertFalse(m_attributes.containsKey(attribute.getName()), "attribute must only be added once");
		m_attributes.put(attribute.getName(), attribute);
	}

	public void add(final AttributeGroupDefinition<A> attributeGroup)
	{
		PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
		PreCondition.assertTrue(attributeGroup.getScopeExtent() == ScopeExtent.Global, "{scope} of attribute group must be global");
		PreCondition.assertFalse(m_attributeGroups.containsKey(attributeGroup.getName()), "attribute group must only be added once");
		m_attributeGroups.put(attributeGroup.getName(), attributeGroup);
	}

	public void add(final ComplexType<A> complexType)
	{
		PreCondition.assertArgumentNotNull(complexType, "complexType");
		PreCondition.assertFalse(m_complexTypes.containsKey(complexType.getName()));
		PreCondition.assertFalse(m_simpleTypes.containsKey(complexType.getName()));
		m_complexTypes.put(complexType.getName(), complexType);
	}

	public void add(final IdentityConstraint<A> identityConstraint)
	{
		PreCondition.assertArgumentNotNull(identityConstraint, "identity-constraint");
		PreCondition.assertTrue(identityConstraint.getScopeExtent() == ScopeExtent.Global, "{scope} of identity-constraint must be global");
		PreCondition.assertFalse(m_identityConstraints.containsKey(identityConstraint.getName()), "identity-constraint must only be added once");
		m_identityConstraints.put(identityConstraint.getName(), identityConstraint);
	}

	public void add(final ModelGroup<A> group)
	{
		PreCondition.assertArgumentNotNull(group, "group");
		PreCondition.assertTrue(group.getScopeExtent() == ScopeExtent.Global, "{scope} of group must be global");
		PreCondition.assertFalse(m_modelGroups.containsKey(group.getName()), "group must only be added once");
		m_modelGroups.put(group.getName(), group);
	}

	public void add(final NotationDefinition<A> notation)
	{
		PreCondition.assertArgumentNotNull(notation, "notation");
		PreCondition.assertTrue(notation.getScopeExtent() == ScopeExtent.Global, "{scope} of notation must be global");
		PreCondition.assertFalse(m_notations.containsKey(notation.getName()), "notation must only be added once");
		m_notations.put(notation.getName(), notation);
	}

	public void add(final SimpleType<A> simpleType)
	{
		PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		PreCondition.assertFalse(m_simpleTypes.containsKey(simpleType.getName()), "simpleType must only be added once");
		PreCondition.assertFalse(m_complexTypes.containsKey(simpleType.getName()), "complexType already exists with the same name");
		m_simpleTypes.put(simpleType.getName(), simpleType);
	}

	public ComplexType<A> dereferenceComplexType(final QName name, final ComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final ComplexType<A> type = existing.getComplexType(name);
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
				throw new AssertionError(name);
			}
		}
	}

	public SimpleType<A> dereferenceSimpleType(final QName name, final ComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final SimpleType<A> type = existing.getSimpleType(name);
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

	public SimpleType<A> getAtomicType(final QName name)
	{
		final SimpleType<A> simpleType = getSimpleType(name);
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
		return m_attributeGroups.get(name);
	}

	public Iterable<AttributeGroupDefinition<A>> getAttributeGroups()
	{
		return m_attributeGroups.values();
	}

	public Iterable<AttributeDefinition<A>> getAttributes()
	{
		return m_attributes.values();
	}

	public ComplexType<A> getComplexType(final QName name)
	{
		return m_complexTypes.get(name);
	}

	public Iterable<ComplexType<A>> getComplexTypes()
	{
		return m_complexTypes.values();
	}

	public ElementDefinition<A> getElement(final QName name)
	{
		return m_elements.get(name);
	}

	public Iterable<ElementDefinition<A>> getElements()
	{
		return m_elements.values();
	}

	public IdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return m_identityConstraints.get(name);
	}

	public Iterable<IdentityConstraint<A>> getIdentityConstraints()
	{
		return m_identityConstraints.values();
	}

	public ModelGroup<A> getModelGroup(final QName name)
	{
		return m_modelGroups.get(name);
	}

	public Iterable<ModelGroup<A>> getModelGroups()
	{
		return m_modelGroups.values();
	}

	public NotationDefinition<A> getNotation(final QName name)
	{
		return m_notations.get(name);
	}

	public Iterable<NotationDefinition<A>> getNotations()
	{
		return m_notations.values();
	}

	public SimpleType<A> getSimpleType(final QName name)
	{
		return m_simpleTypes.get(name);
	}

	public Iterable<SimpleType<A>> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public Type<A> getType(final QName name)
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

	public boolean isComplexType(final QName name, final ComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final ComplexType<A> type = existing.getComplexType(name);
		if (null != type)
		{
			return true;
		}
		else
		{
			return m_complexTypes.containsKey(name);
		}
	}

	public boolean isSimpleType(final QName name, final ComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final SimpleType<A> type = existing.getSimpleType(name);
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
