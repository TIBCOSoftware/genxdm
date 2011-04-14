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

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateAttributeException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateAttributeGroupException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateElementException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateIdentityConstraintException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateModelGroupException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateNotationException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateTypeException;
import org.genxdm.processor.w3c.xs.exception.SmUndeclaredReferenceException;
import org.genxdm.xs.constraints.IdentityConstraintKind;
import org.genxdm.xs.exceptions.SchemaException;


/**
 * Information collected as we incrementally parse a schema document. <br/>
 * This class is used to do the "bookkeeping" associated with parsing in a streaming fashion and with managing forward
 * references.
 */
final class XMLSchemaCache<A>
{
	final Map<QName, XMLElement<A>> m_elements = new HashMap<QName, XMLElement<A>>();
	final Map<QName, SrcFrozenLocation> m_elementsUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final Map<QName, XMLAttribute<A>> m_attributes = new HashMap<QName, XMLAttribute<A>>();
	final Map<QName, SrcFrozenLocation> m_attributesUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final Map<QName, XMLType<A>> m_globalTypes = new HashMap<QName, XMLType<A>>();
	final Map<QName, SrcFrozenLocation> m_typesUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final Map<QName, XMLModelGroup<A>> m_modelGroups = new HashMap<QName, XMLModelGroup<A>>();
	final Map<QName, SrcFrozenLocation> m_modelGroupsUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final Map<QName, XMLAttributeGroup<A>> m_attributeGroups = new HashMap<QName, XMLAttributeGroup<A>>();
	final Map<QName, SrcFrozenLocation> m_attributeGroupsUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final Map<QName, XMLIdentityConstraint<A>> m_constraints = new HashMap<QName, XMLIdentityConstraint<A>>();
	final Map<QName, SrcFrozenLocation> m_constraintsUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final Map<QName, XMLNotation<A>> m_notations = new HashMap<QName, XMLNotation<A>>();
	final Map<QName, SrcFrozenLocation> m_notationsUnresolved = new HashMap<QName, SrcFrozenLocation>();

	final HashSet<URI> m_seenNamespaces = new HashSet<URI>();
	final HashSet<URI> m_seenSystemIds = new HashSet<URI>();

	private final XMLScope<A> GLOBAL = new XMLScope<A>();
	private final XMLTypeRef<A> ANY_SIMPLE_TYPE;
	private final XMLTypeRef<A> ANY_TYPE;

	public XMLSchemaCache(final NameSource nameBridge)
	{
		ANY_SIMPLE_TYPE = new XMLTypeRef<A>(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType"));
		ANY_TYPE = new XMLTypeRef<A>(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType"));
	}

	public Iterable<XMLType<A>> getGlobalTypes()
	{
		return m_globalTypes.values();
	}

	public Iterable<XMLElement<A>> getElements()
	{
		return m_elements.values();
	}

	public Iterable<XMLAttribute<A>> getAttributes()
	{
		return m_attributes.values();
	}

	public Iterable<String> getNamespaces()
	{
		// Not sure how to handle this.
		return Collections.emptyList();
	}

	/**
	 * Expand temporary variables used to hold syntactic constructs.
	 */
	public void checkReferences() throws SchemaException
	{
		if (!m_elementsUnresolved.isEmpty())
		{
			final QName name = m_elementsUnresolved.keySet().iterator().next();
			final SrcFrozenLocation location = m_elementsUnresolved.get(name);
			throw new SmUndeclaredReferenceException(name, location);
		}

		if (!m_attributesUnresolved.isEmpty())
		{
			final QName name = m_attributesUnresolved.keySet().iterator().next();
			final SrcFrozenLocation location = m_attributesUnresolved.get(name);
			throw new SmUndeclaredReferenceException(name, location);
		}

		if (!m_typesUnresolved.isEmpty())
		{
			final QName name = m_typesUnresolved.keySet().iterator().next();
			final SrcFrozenLocation location = m_typesUnresolved.get(name);
			throw new SmUndeclaredReferenceException(name, location);
		}

		if (!m_modelGroupsUnresolved.isEmpty())
		{
			final QName name = m_modelGroupsUnresolved.keySet().iterator().next();
			final SrcFrozenLocation location = m_modelGroupsUnresolved.get(name);
			throw new SmUndeclaredReferenceException(name, location);
		}

		if (!m_attributeGroupsUnresolved.isEmpty())
		{
			final QName name = m_attributeGroupsUnresolved.keySet().iterator().next();
			final SrcFrozenLocation location = m_attributeGroupsUnresolved.get(name);
			throw new SmUndeclaredReferenceException(name, location);
		}

		if (!m_constraintsUnresolved.isEmpty())
		{
			final QName name = m_constraintsUnresolved.keySet().iterator().next();
			final SrcFrozenLocation location = m_constraintsUnresolved.get(name);
			throw new SmUndeclaredReferenceException(name, location);
		}
	}

	public void computeSubstitutionGroups()
	{
		for (final QName name : m_elements.keySet())
		{
			final XMLElement<A> element = m_elements.get(name);
			if (null != element.substitutionGroup && element.typeRef.isComplexUrType())
			{
				element.typeRef = element.substitutionGroup.typeRef;
			}
		}
	}

	public XMLAttribute<A> registerAttribute(final QName name, final SrcFrozenLocation location) throws SmDuplicateAttributeException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_attributes.containsKey(name))
		{
			final XMLAttribute<A> attribute = m_attributes.get(name);
			if (m_attributesUnresolved.containsKey(name))
			{
				m_attributesUnresolved.remove(name);
				attribute.setLocation(location);
			}
			else
			{
				throw new SmDuplicateAttributeException(name, location);
			}
			return attribute;
		}
		else
		{
			final XMLAttribute<A> attribute = new XMLAttribute<A>(name, GLOBAL, ANY_SIMPLE_TYPE, location);
			m_attributes.put(name, attribute);
			return attribute;
		}
	}

	public XMLAttribute<A> dereferenceAttribute(final QName name, final Location reference)
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_attributes.containsKey(name))
		{
			return m_attributes.get(name);
		}
		else
		{
			final XMLAttribute<A> attribute = new XMLAttribute<A>(name, GLOBAL, ANY_SIMPLE_TYPE);
			m_attributesUnresolved.put(name, new SrcFrozenLocation(reference));
			m_attributes.put(name, attribute);
			return attribute;
		}
	}

	/**
	 * Guarantee that a global attribute group declaration exists with the specified name. <br/>
	 * Forward references are handled by creating place-holders that get filled in later.
	 */
	public XMLAttributeGroup<A> registerAttributeGroup(final QName name, final SrcFrozenLocation location) throws SmDuplicateAttributeGroupException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_attributeGroups.containsKey(name))
		{
			final XMLAttributeGroup<A> attributeGroup = m_attributeGroups.get(name);
			if (m_attributeGroupsUnresolved.containsKey(name))
			{
				m_attributeGroupsUnresolved.remove(name);
				attributeGroup.setLocation(location);
			}
			else
			{
				throw new SmDuplicateAttributeGroupException(name, location);
			}
			return attributeGroup;
		}
		else
		{
			final XMLAttributeGroup<A> attributeGroup = new XMLAttributeGroup<A>(name, GLOBAL, location);
			m_attributeGroups.put(name, attributeGroup);
			return attributeGroup;
		}
	}

	public XMLAttributeGroup<A> dereferenceAttributeGroup(final QName name, final Location reference, final boolean mustExist) throws SchemaException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_attributeGroups.containsKey(name))
		{
			return m_attributeGroups.get(name);
		}
		else
		{
			if (mustExist)
			{
				throw new SmUndeclaredReferenceException(name, new SrcFrozenLocation(reference));
			}
			else
			{
				final XMLAttributeGroup<A> attributeGroup = new XMLAttributeGroup<A>(name, GLOBAL);
				m_attributeGroupsUnresolved.put(name, new SrcFrozenLocation(reference));
				m_attributeGroups.put(name, attributeGroup);
				return attributeGroup;
			}
		}
	}

	/**
	 * Guarantee that a global element declaration exists with the specified name. <br/>
	 * Forward references are handled by creating placeholders that get filled in later.
	 */
	public XMLIdentityConstraint<A> registerIdentityConstraint(final IdentityConstraintKind kind, final QName name, final SrcFrozenLocation location) throws SmDuplicateIdentityConstraintException
	{
		PreCondition.assertArgumentNotNull(name);

		final XMLIdentityConstraint<A> constraint;
		if (m_constraints.containsKey(name))
		{
			constraint = m_constraints.get(name);
			if (m_constraintsUnresolved.containsKey(name))
			{
				m_constraintsUnresolved.remove(name);
				constraint.setLocation(location);
			}
			else
			{
				throw new SmDuplicateIdentityConstraintException(name, location);
			}
		}
		else
		{
			constraint = new XMLIdentityConstraint<A>(name, GLOBAL, location);
			m_constraints.put(name, constraint);
		}
		constraint.category = kind;
		return constraint;
	}

	/**
	 * Dereferences the identity constraint if it has already been seen or creates a placeholder identity constraint
	 * marked as unresolved.
	 */
	public XMLIdentityConstraint<A> dereferenceIdentityConstraint(final QName name, final Location reference)
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_constraints.containsKey(name))
		{
			return m_constraints.get(name);
		}
		else
		{
			final XMLIdentityConstraint<A> constraint = new XMLIdentityConstraint<A>(name, GLOBAL);
			m_constraints.put(name, constraint);
			m_constraintsUnresolved.put(name, new SrcFrozenLocation(reference));
			return constraint;
		}
	}

	public XMLType<A> registerType(final QName name, final SrcFrozenLocation location) throws SmDuplicateTypeException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_globalTypes.containsKey(name))
		{
			final XMLType<A> type = m_globalTypes.get(name);
			if (m_typesUnresolved.containsKey(name))
			{
				m_typesUnresolved.remove(name);
				type.setLocation(location);
			}
			else
			{
				// Duplicate type in the context of the cache.
				throw new SmDuplicateTypeException(name, location);
			}
			return type;
		}
		else
		{
			final XMLType<A> type = new XMLType<A>(name, GLOBAL, location);
			m_globalTypes.put(name, type);
			return type;
		}
	}

	public XMLType<A> registerAnonymousType(final XMLScope<A> scope, final SrcFrozenLocation location)
	{
		return new XMLType<A>(null, scope, location);
	}

	public XMLType<A> dereferenceType(final QName name, final Location reference, final boolean mustExist) throws SchemaException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_globalTypes.containsKey(name))
		{
			return m_globalTypes.get(name);
		}
		else
		{
			if (mustExist)
			{
				throw new SmUndeclaredReferenceException(name, new SrcFrozenLocation(reference));
			}
			else
			{
				final XMLType<A> type = new XMLType<A>(name, GLOBAL);
				m_typesUnresolved.put(name, new SrcFrozenLocation(reference));
				m_globalTypes.put(name, type);
				return type;
			}
		}
	}

	public XMLModelGroup<A> registerModelGroup(final QName name, final SrcFrozenLocation location) throws SmDuplicateModelGroupException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_modelGroups.containsKey(name))
		{
			final XMLModelGroup<A> modelGroup = m_modelGroups.get(name);
			if (m_modelGroupsUnresolved.containsKey(name))
			{
				m_modelGroupsUnresolved.remove(name);
				modelGroup.setLocation(location);
			}
			else
			{
				throw new SmDuplicateModelGroupException(name, location);
			}
			return modelGroup;
		}
		else
		{
			final XMLModelGroup<A> modelGroup = new XMLModelGroup<A>(name, GLOBAL, location);
			m_modelGroups.put(name, modelGroup);
			return modelGroup;
		}
	}

	public XMLModelGroup<A> dereferenceModelGroup(final QName name, final Location reference, final boolean mustExist) throws SchemaException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_modelGroups.containsKey(name))
		{
			return m_modelGroups.get(name);
		}
		else
		{
			if (mustExist)
			{
				throw new SmUndeclaredReferenceException(name, new SrcFrozenLocation(reference));
			}
			else
			{
				final XMLModelGroup<A> modelGroup = new XMLModelGroup<A>(name, GLOBAL);
				m_modelGroups.put(name, modelGroup);
				m_modelGroupsUnresolved.put(name, new SrcFrozenLocation(reference));
				return modelGroup;
			}
		}
	}

	public XMLNotation<A> registerNotation(final QName name, final SrcFrozenLocation location) throws SmDuplicateNotationException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_notations.containsKey(name))
		{
			final XMLNotation<A> notation = m_notations.get(name);
			if (m_notationsUnresolved.containsKey(name))
			{
				m_notationsUnresolved.remove(name);
				notation.setLocation(location);
				return notation;
			}
			else
			{
				throw new SmDuplicateNotationException(name, location);
			}
		}
		else
		{
			final XMLNotation<A> notation = new XMLNotation<A>(name, GLOBAL, location);
			m_notations.put(name, notation);
			return notation;
		}
	}

	public XMLElement<A> registerElement(final QName name, final SrcFrozenLocation location) throws SmDuplicateElementException
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_elements.containsKey(name))
		{
			final XMLElement<A> element = m_elements.get(name);
			if (m_elementsUnresolved.containsKey(name))
			{
				m_elementsUnresolved.remove(name);
				element.setLocation(location);
			}
			else
			{
				throw new SmDuplicateElementException(name, location);
			}
			return element;
		}
		else
		{
			final XMLElement<A> element = new XMLElement<A>(name, GLOBAL, ANY_TYPE, location);
			m_elements.put(name, element);
			return element;
		}
	}

	/**
	 * Dereferences a global element or creates a global placeholder element marked as unresolved.
	 */
	public XMLElement<A> dereferenceElement(final QName name, final Location reference)
	{
		PreCondition.assertArgumentNotNull(name);

		if (m_elements.containsKey(name))
		{
			return m_elements.get(name);
		}
		else
		{
			final XMLElement<A> element = new XMLElement<A>(name, GLOBAL, ANY_TYPE);
			m_elements.put(name, element);
			m_elementsUnresolved.put(name, new SrcFrozenLocation(reference));
			return element;
		}
	}
}
