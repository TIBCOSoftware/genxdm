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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.components.SmAttribute;
import org.gxml.xs.components.SmAttributeGroup;
import org.gxml.xs.components.SmComponentBag;
import org.gxml.xs.components.SmComponentProvider;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.components.SmModelGroup;
import org.gxml.xs.components.SmNotation;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmType;

/**
 * A bag for storing of {@link org.gxml.xs.components.SmComponent}(s).
 */
final class SmComponentBagImpl<A> implements SmComponentBag<A>
{
	private final Map<QName, SmAttributeGroup<A>> m_attributeGroups = new HashMap<QName, SmAttributeGroup<A>>();

	private final Map<QName, SmAttribute<A>> m_attributes = new HashMap<QName, SmAttribute<A>>();
	private final Map<QName, SmComplexType<A>> m_complexTypes = new HashMap<QName, SmComplexType<A>>();
	private final Map<QName, SmElement<A>> m_elements = new HashMap<QName, SmElement<A>>();
	private final Map<QName, SmIdentityConstraint<A>> m_identityConstraints = new HashMap<QName, SmIdentityConstraint<A>>();
	private final Map<QName, SmModelGroup<A>> m_modelGroups = new HashMap<QName, SmModelGroup<A>>();
	private final Map<QName, SmNotation<A>> m_notations = new HashMap<QName, SmNotation<A>>();
	private final Map<QName, SmSimpleType<A>> m_simpleTypes = new HashMap<QName, SmSimpleType<A>>();

	public void add(final SmElement<A> element)
	{
		PreCondition.assertArgumentNotNull(element, "element");
		PreCondition.assertTrue(element.getScopeExtent() == SmScopeExtent.Global, "{scope} of element must be global");
		PreCondition.assertFalse(m_elements.containsKey(element.getName()), "element must only be added once");
		m_elements.put(element.getName(), element);
	}

	public void add(final SmAttribute<A> attribute)
	{
		PreCondition.assertArgumentNotNull(attribute, "attribute");
		PreCondition.assertTrue(attribute.getScopeExtent() == SmScopeExtent.Global, "{scope} of attribute must be global");
		PreCondition.assertFalse(m_attributes.containsKey(attribute.getName()), "attribute must only be added once");
		m_attributes.put(attribute.getName(), attribute);
	}

	public void add(final SmAttributeGroup<A> attributeGroup)
	{
		PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
		PreCondition.assertTrue(attributeGroup.getScopeExtent() == SmScopeExtent.Global, "{scope} of attribute group must be global");
		PreCondition.assertFalse(m_attributeGroups.containsKey(attributeGroup.getName()), "attribute group must only be added once");
		m_attributeGroups.put(attributeGroup.getName(), attributeGroup);
	}

	public void add(final SmComplexType<A> complexType)
	{
		PreCondition.assertArgumentNotNull(complexType, "complexType");
		PreCondition.assertFalse(m_complexTypes.containsKey(complexType.getName()));
		PreCondition.assertFalse(m_simpleTypes.containsKey(complexType.getName()));
		m_complexTypes.put(complexType.getName(), complexType);
	}

	public void add(final SmIdentityConstraint<A> identityConstraint)
	{
		PreCondition.assertArgumentNotNull(identityConstraint, "identity-constraint");
		PreCondition.assertTrue(identityConstraint.getScopeExtent() == SmScopeExtent.Global, "{scope} of identity-constraint must be global");
		PreCondition.assertFalse(m_identityConstraints.containsKey(identityConstraint.getName()), "identity-constraint must only be added once");
		m_identityConstraints.put(identityConstraint.getName(), identityConstraint);
	}

	public void add(final SmModelGroup<A> group)
	{
		PreCondition.assertArgumentNotNull(group, "group");
		PreCondition.assertTrue(group.getScopeExtent() == SmScopeExtent.Global, "{scope} of group must be global");
		PreCondition.assertFalse(m_modelGroups.containsKey(group.getName()), "group must only be added once");
		m_modelGroups.put(group.getName(), group);
	}

	public void add(final SmNotation<A> notation)
	{
		PreCondition.assertArgumentNotNull(notation, "notation");
		PreCondition.assertTrue(notation.getScopeExtent() == SmScopeExtent.Global, "{scope} of notation must be global");
		PreCondition.assertFalse(m_notations.containsKey(notation.getName()), "notation must only be added once");
		m_notations.put(notation.getName(), notation);
	}

	public void add(final SmSimpleType<A> simpleType)
	{
		PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		PreCondition.assertFalse(m_simpleTypes.containsKey(simpleType.getName()), "simpleType must only be added once");
		PreCondition.assertFalse(m_complexTypes.containsKey(simpleType.getName()), "complexType already exists with the same name");
		m_simpleTypes.put(simpleType.getName(), simpleType);
	}

	public SmComplexType<A> dereferenceComplexType(final QName name, final SmComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final SmComplexType<A> type = existing.getComplexType(name);
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

	public SmSimpleType<A> dereferenceSimpleType(final QName name, final SmComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final SmSimpleType<A> type = existing.getSimpleType(name);
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
		return m_attributeGroups.get(name);
	}

	public Iterable<SmAttributeGroup<A>> getAttributeGroups()
	{
		return m_attributeGroups.values();
	}

	public Iterable<SmAttribute<A>> getAttributes()
	{
		return m_attributes.values();
	}

	public SmComplexType<A> getComplexType(final QName name)
	{
		return m_complexTypes.get(name);
	}

	public Iterable<SmComplexType<A>> getComplexTypes()
	{
		return m_complexTypes.values();
	}

	public SmElement<A> getElement(final QName name)
	{
		return m_elements.get(name);
	}

	public Iterable<SmElement<A>> getElements()
	{
		return m_elements.values();
	}

	public SmIdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return m_identityConstraints.get(name);
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return m_identityConstraints.values();
	}

	public SmModelGroup<A> getModelGroup(final QName name)
	{
		return m_modelGroups.get(name);
	}

	public Iterable<SmModelGroup<A>> getModelGroups()
	{
		return m_modelGroups.values();
	}

	public SmNotation<A> getNotation(final QName name)
	{
		return m_notations.get(name);
	}

	public Iterable<SmNotation<A>> getNotations()
	{
		return m_notations.values();
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

	public boolean isComplexType(final QName name, final SmComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final SmComplexType<A> type = existing.getComplexType(name);
		if (null != type)
		{
			return true;
		}
		else
		{
			return m_complexTypes.containsKey(name);
		}
	}

	public boolean isSimpleType(final QName name, final SmComponentProvider<A> existing)
	{
		PreCondition.assertArgumentNotNull(name);

		final SmSimpleType<A> type = existing.getSimpleType(name);
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
