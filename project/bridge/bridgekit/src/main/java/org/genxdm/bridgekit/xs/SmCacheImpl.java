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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.Schema;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.SchemaComponent;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ComponentKind;
import org.genxdm.xs.components.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmAtomicUrType;
import org.genxdm.xs.types.SmAttributeNodeType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmDocumentNodeType;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmEmptyType;
import org.genxdm.xs.types.SmNamespaceNodeType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmNodeType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmProcessingInstructionNodeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmType;

final class SmCacheImpl<A> implements SmCache<A>, ComponentProvider<A>, Schema<A>
{
	private final AtomicUrTypeImpl<A> ANY_ATOMIC_TYPE;
	private final ComplexUrTypeImpl<A> ANY_COMPLEX_TYPE;
	private final SmPrimeType<A> ANY_ITEM;
	private final SmPrimeType<A> ANY_KIND;
	private final SimpleUrTypeImpl<A> ANY_SIMPLE_TYPE;
	private final AtomBridge<A> atomBridge;
	private final SmAttributeNodeType<A> ATTRIBUTE;

	private final BuiltInSchema<A> BUILT_IN;
	private final SmNodeType<A> COMMENT;
	private final SmDocumentNodeType<A> DOCUMENT;
	private final SmElementNodeType<A> ELEMENT;
	private final SmEmptyType<A> EMPTY;
	final ConcurrentHashMap<QName, AttributeGroupDefinition<A>> m_attributeGroups = new ConcurrentHashMap<QName, AttributeGroupDefinition<A>>();
	final ConcurrentHashMap<QName, AttributeDefinition<A>> m_attributes = new ConcurrentHashMap<QName, AttributeDefinition<A>>();

	final ConcurrentHashMap<QName, SmComplexType<A>> m_complexTypes = new ConcurrentHashMap<QName, SmComplexType<A>>();
	final ConcurrentHashMap<QName, ElementDefinition<A>> m_elements = new ConcurrentHashMap<QName, ElementDefinition<A>>();
	final ConcurrentHashMap<QName, SmIdentityConstraint<A>> m_identityConstraints = new ConcurrentHashMap<QName, SmIdentityConstraint<A>>();
	private boolean m_isLocked = false;
	final ConcurrentHashMap<QName, ModelGroup<A>> m_modelGroups = new ConcurrentHashMap<QName, ModelGroup<A>>();
	private int m_nextType = 0;
	final ConcurrentHashMap<QName, NotationDefinition<A>> m_notations = new ConcurrentHashMap<QName, NotationDefinition<A>>();
	private final ConcurrentHashMap<QName, SmSimpleType<A>> m_simpleTypes = new ConcurrentHashMap<QName, SmSimpleType<A>>();
	private final NameSource nameBridge;
	private final SmNamespaceNodeType<A> NAMESPACE;
	/**
	 * The set of namespaces of all components. We build this during registration, which acts as the gateway.
	 */
	private final Set<String> namespaces = new HashSet<String>();
	private final SmProcessingInstructionNodeType<A> PROCESSING_INSTRUCTION;

	private final SmNodeType<A> TEXT;
	private static final String ESCAPE = "\u001B";
	private final QName WILDNAME = new QName(ESCAPE, ESCAPE);

	public SmCacheImpl(final AtomBridge<A> atomBridge)
	{
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		this.nameBridge = atomBridge.getNameBridge();

		assertNotLocked();

		final String W3C_XML_SCHEMA_NS_URI = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		this.ANY_COMPLEX_TYPE = new ComplexUrTypeImpl<A>(W3C_XML_SCHEMA_NS_URI, nameBridge);
		this.ANY_SIMPLE_TYPE = new SimpleUrTypeImpl<A>(W3C_XML_SCHEMA_NS_URI, atomBridge, this);
		this.ANY_ATOMIC_TYPE = new AtomicUrTypeImpl<A>(W3C_XML_SCHEMA_NS_URI, ANY_SIMPLE_TYPE, atomBridge);

		defineComplexType(ANY_COMPLEX_TYPE);
		defineSimpleType(ANY_SIMPLE_TYPE);
		defineSimpleType(ANY_ATOMIC_TYPE);

		BUILT_IN = new BuiltInSchema<A>(W3C_XML_SCHEMA_NS_URI, this);
		register(BUILT_IN);
		register(new XmlSchema<A>(this));
		register(new XsiSchema<A>(this));

		ELEMENT = new ElementNodeType<A>(WILDNAME, null, false, this);
		NAMESPACE = new NamespaceNodeType<A>(this);
		ATTRIBUTE = new AttributeNodeType<A>(WILDNAME, null, this);
		COMMENT = new CommentNodeType<A>(this);
		PROCESSING_INSTRUCTION = new ProcessingInstructionNodeType<A>(null, this);
		TEXT = new TextNodeType<A>(this);
		final SmSequenceType<A> X = ZMultiplyType.zeroOrMore(ZPrimeChoiceType.choice(ELEMENT, ZPrimeChoiceType.choice(TEXT, ZPrimeChoiceType.choice(COMMENT, PROCESSING_INSTRUCTION))));
		DOCUMENT = new DocumentNodeType<A>(X, this);
		ANY_KIND = ZPrimeChoiceType.choice(ELEMENT, ZPrimeChoiceType.choice(ATTRIBUTE, ZPrimeChoiceType.choice(TEXT, ZPrimeChoiceType.choice(DOCUMENT, ZPrimeChoiceType.choice(COMMENT, ZPrimeChoiceType.choice(NAMESPACE, PROCESSING_INSTRUCTION))))));
		ANY_ITEM = ZPrimeChoiceType.choice(ANY_KIND, ANY_ATOMIC_TYPE);
		EMPTY = new ZEmptyType<A>();
	}

	private void assertNotLocked()
	{
		PreCondition.assertFalse(m_isLocked, "isLocked -> true");
	}

	private QName checkComponent(final SchemaComponent<A> component, final ComponentKind kind)
	{
		PreCondition.assertArgumentNotNull(component);
		if (!kind.canBeAnonymous)
		{
			PreCondition.assertFalse(component.isAnonymous());
		}
		return PreCondition.assertArgumentNotNull(component.getName());
	}

	public SmPrimeType<A> comment()
	{
		return COMMENT;
	}

	public void declareAttribute(final AttributeDefinition<A> attribute)
	{
		final QName name = checkComponent(attribute, ComponentKind.ATTRIBUTE);
		if (!m_attributes.containsKey(name))
		{
			m_attributes.put(name, attribute);
			recordNamespace(attribute);
		}
	}

	public void declareElement(final ElementDefinition<A> element)
	{
		final QName name = checkComponent(element, ComponentKind.ELEMENT);
		if (!m_elements.containsKey(name))
		{
			m_elements.put(name, element);
			recordNamespace(element);
		}
	}

	public void declareNotation(final NotationDefinition<A> notation)
	{
		final QName name = checkComponent(notation, ComponentKind.NOTATION);
		if (!m_notations.containsKey(name))
		{
			m_notations.put(name, notation);
			recordNamespace(notation);
		}
	}

	public void defineAttributeGroup(final AttributeGroupDefinition<A> attributeGroup)
	{
		final QName name = checkComponent(attributeGroup, ComponentKind.ATTRIBUTE_GROUP);
		if (!m_attributeGroups.containsKey(name))
		{
			m_attributeGroups.put(name, attributeGroup);
			recordNamespace(attributeGroup);
		}
	}

	public void defineComplexType(final SmComplexType<A> complexType)
	{
		final QName name = checkComponent(complexType, ComponentKind.COMPLEX_TYPE);
		if (!m_complexTypes.containsKey(name))
		{
			m_complexTypes.put(name, complexType);
			recordNamespace(complexType);
		}
	}

	public void defineIdentityConstraint(final SmIdentityConstraint<A> identityConstraint)
	{
		final QName name = checkComponent(identityConstraint, ComponentKind.IDENTITY_CONSTRAINT);
		if (!m_identityConstraints.containsKey(name))
		{
			m_identityConstraints.put(name, identityConstraint);
			recordNamespace(identityConstraint);
		}
	}

	public void defineModelGroup(final ModelGroup<A> modelGroup)
	{
		final QName name = checkComponent(modelGroup, ComponentKind.MODEL_GROUP);
		if (!m_modelGroups.containsKey(name))
		{
			m_modelGroups.put(name, modelGroup);
			recordNamespace(modelGroup);
		}
	}

	public void defineSimpleType(final SmSimpleType<A> simpleType)
	{
		final QName name = checkComponent(simpleType, ComponentKind.SIMPLE_TYPE);
		if (!m_simpleTypes.containsKey(name))
		{
			m_simpleTypes.put(name, simpleType);
			recordNamespace(simpleType);
		}
	}

	public SmDocumentNodeType<A> documentType(final SmSequenceType<A> contentType)
	{
		if (null != contentType)
		{
			return new DocumentNodeType<A>(contentType, this);
		}
		else
		{
			return DOCUMENT;
		}
	}

	public SmElementNodeType<A> elementWild(final SmSequenceType<A> type, final boolean nillable)
	{
		return new ElementNodeType<A>(WILDNAME, type, nillable, this);
	}

	public SmEmptyType<A> empty()
	{
		return EMPTY;
	}

	public synchronized QName generateUniqueName()
	{
		assertNotLocked();
		return new QName("http://www.tibco.com/gXML-SA/local-types", "type-".concat(Integer.toString(m_nextType++)));
	}

	public AtomBridge<A> getAtomBridge()
	{
		return atomBridge;
	}

	public SmAtomicType<A> getAtomicType(final QName name)
	{
		final SmSimpleType<A> simpleType = m_simpleTypes.get(name);
		if (simpleType.isAtomicType())
		{
			return (SmAtomicType<A>)simpleType;
		}
		else
		{
			return null;
		}
	}

	public SmAtomicType<A> getAtomicType(final SmNativeType nativeType)
	{
		final SmType<A> type = getTypeDefinition(nativeType);
		if (type.isAtomicType())
		{
			return (SmAtomicType<A>)type;
		}
		else
		{
			return null;
		}
	}

	public SmAtomicUrType<A> getAtomicUrType()
	{
		return ANY_ATOMIC_TYPE;
	}

	public AttributeDefinition<A> getAttributeDeclaration(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_attributes.get(name);
	}

	public AttributeGroupDefinition<A> getAttributeGroup(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
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

	public SmComplexType<A> getComplexType(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_complexTypes.get(name);
	}

	public Iterable<SmComplexType<A>> getComplexTypes()
	{
		return m_complexTypes.values();
	}

	public SmComplexUrType<A> getComplexUrType()
	{
		return ANY_COMPLEX_TYPE;
	}

	public ElementDefinition<A> getElementDeclaration(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_elements.get(name);
	}

	public Iterable<ElementDefinition<A>> getElements()
	{
		return m_elements.values();
	}

	public SmIdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_identityConstraints.get(name);
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return m_identityConstraints.values();
	}

	public ModelGroup<A> getModelGroup(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_modelGroups.get(name);
	}

	public Iterable<ModelGroup<A>> getModelGroups()
	{
		return m_modelGroups.values();
	}

	public NameSource getNameBridge()
	{
		return nameBridge;
	}

	public Iterable<String> getNamespaces()
	{
		return namespaces;
	}

	public NotationDefinition<A> getNotationDeclaration(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_notations.get(name);
	}

	public Iterable<NotationDefinition<A>> getNotations()
	{
		return m_notations.values();
	}

	public SmSimpleType<A> getSimpleType(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_simpleTypes.get(name);
	}

	public SmSimpleType<A> getSimpleType(final SmNativeType nativeType)
	{
		final SmType<A> type = getTypeDefinition(nativeType);
		if (type instanceof SmSimpleType<?>)
		{
			return (SmSimpleType<A>)type;
		}
		else
		{
			return null;
		}
	}

	public Iterable<SmSimpleType<A>> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public SmSimpleUrType<A> getSimpleUrType()
	{
		return ANY_SIMPLE_TYPE;
	}

	public SmType<A> getTypeDefinition(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		if (m_complexTypes.containsKey(name))
		{
			return m_complexTypes.get(name);
		}
		else if (m_simpleTypes.containsKey(name))
		{
			return m_simpleTypes.get(name);
		}
		else if (name.equals(ANY_ATOMIC_TYPE.getName()))
		{
			return ANY_ATOMIC_TYPE;
		}
		else if (name.equals(ANY_SIMPLE_TYPE.getName()))
		{
			return ANY_SIMPLE_TYPE;
		}
		else if (name.equals(ANY_COMPLEX_TYPE.getName()))
		{
			return ANY_COMPLEX_TYPE;
		}
		else
		{
			return null;
		}
	}

	public SmType<A> getTypeDefinition(final SmNativeType nativeType)
	{
		switch (nativeType)
		{
			case BOOLEAN:
			{
				return BUILT_IN.BOOLEAN;
			}
			case STRING:
			{
				return BUILT_IN.STRING;
			}
			case DOUBLE:
			{
				return BUILT_IN.DOUBLE;
			}
			case FLOAT:
			{
				return BUILT_IN.FLOAT;
			}
			case DECIMAL:
			{
				return BUILT_IN.DECIMAL;
			}
			case INTEGER:
			{
				return BUILT_IN.INTEGER;
			}
			case LONG:
			{
				return BUILT_IN.LONG;
			}
			case INT:
			{
				return BUILT_IN.INT;
			}
			case SHORT:
			{
				return BUILT_IN.SHORT;
			}
			case BYTE:
			{
				return BUILT_IN.BYTE;
			}
			case NON_POSITIVE_INTEGER:
			{
				return BUILT_IN.NON_POSITIVE_INTEGER;
			}
			case NEGATIVE_INTEGER:
			{
				return BUILT_IN.NEGATIVE_INTEGER;
			}
			case NON_NEGATIVE_INTEGER:
			{
				return BUILT_IN.NON_NEGATIVE_INTEGER;
			}
			case POSITIVE_INTEGER:
			{
				return BUILT_IN.POSITIVE_INTEGER;
			}
			case UNSIGNED_LONG:
			{
				return BUILT_IN.UNSIGNED_LONG;
			}
			case UNSIGNED_INT:
			{
				return BUILT_IN.UNSIGNED_INT;
			}
			case UNSIGNED_SHORT:
			{
				return BUILT_IN.UNSIGNED_SHORT;
			}
			case UNSIGNED_BYTE:
			{
				return BUILT_IN.UNSIGNED_BYTE;
			}
			case UNTYPED_ATOMIC:
			{
				return BUILT_IN.UNTYPED_ATOMIC;
			}
			case UNTYPED:
			{
				return BUILT_IN.UNTYPED;
			}
			case DATE:
			{
				return BUILT_IN.DATE;
			}
			case DATETIME:
			{
				return BUILT_IN.DATETIME;
			}
			case TIME:
			{
				return BUILT_IN.TIME;
			}
			case GYEARMONTH:
			{
				return BUILT_IN.GYEARMONTH;
			}
			case GYEAR:
			{
				return BUILT_IN.GYEAR;
			}
			case GMONTHDAY:
			{
				return BUILT_IN.GMONTHDAY;
			}
			case GDAY:
			{
				return BUILT_IN.GDAY;
			}
			case GMONTH:
			{
				return BUILT_IN.GMONTH;
			}
			case DURATION_DAYTIME:
			{
				return BUILT_IN.DURATION_DAYTIME;
			}
			case DURATION_YEARMONTH:
			{
				return BUILT_IN.DURATION_YEARMONTH;
			}
			case DURATION:
			{
				return BUILT_IN.DURATION;
			}
			case ANY_URI:
			{
				return BUILT_IN.ANY_URI;
			}
			case BASE64_BINARY:
			{
				return BUILT_IN.BASE64_BINARY;
			}
			case HEX_BINARY:
			{
				return BUILT_IN.HEX_BINARY;
			}
			case LANGUAGE:
			{
				return BUILT_IN.LANGUAGE;
			}
			case QNAME:
			{
				return BUILT_IN.QNAME;
			}
			case ANY_TYPE:
			{
				return ANY_COMPLEX_TYPE;
			}
			case ANY_SIMPLE_TYPE:
			{
				return ANY_SIMPLE_TYPE;
			}
			case ANY_ATOMIC_TYPE:
			{
				return ANY_ATOMIC_TYPE;
			}
			case NORMALIZED_STRING:
			{
				return BUILT_IN.NORMALIZED_STRING;
			}
			case TOKEN:
			{
				return BUILT_IN.TOKEN;
			}
			case NMTOKEN:
			{
				return BUILT_IN.NMTOKEN;
			}
			case NAME:
			{
				return BUILT_IN.NAME;
			}
			case NCNAME:
			{
				return BUILT_IN.NCNAME;
			}
			case ID:
			{
				return BUILT_IN.ID;
			}
			case IDREF:
			{
				return BUILT_IN.IDREF;
			}
			case ENTITY:
			{
				return BUILT_IN.ENTITY;
			}
			case NOTATION:
			{
				return BUILT_IN.NOTATION;
			}
			case IDREFS:
			{
				return BUILT_IN.IDREFS;
			}
			case NMTOKENS:
			{
				return BUILT_IN.NMTOKENS;
			}
			case ENTITIES:
			{
				return BUILT_IN.ENTITIES;
			}
			default:
			{
				throw new AssertionError(nativeType);
			}
		}
	}

	public boolean hasAttribute(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_attributes.containsKey(name);
	}

	public boolean hasAttributeGroup(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_attributeGroups.containsKey(name);
	}

	public boolean hasComplexType(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_complexTypes.containsKey(name);
	}

	public boolean hasElement(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_elements.containsKey(name);
	}

	public boolean hasIdentityConstraint(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_identityConstraints.containsKey(name);
	}

	public boolean hasModelGroup(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_modelGroups.containsKey(name);
	}

	public boolean hasNotation(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_notations.containsKey(name);
	}

	public boolean hasSimpleType(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_simpleTypes.containsKey(name);
	}

	public boolean hasType(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		if (m_complexTypes.containsKey(name))
		{
			return true;
		}
		else if (m_simpleTypes.containsKey(name))
		{
			return true;
		}
		return false;
	}

	public boolean isLocked()
	{
		return m_isLocked;
	}

	public SmPrimeType<A> item()
	{
		return ANY_ITEM;
	}

	public SmSequenceType<A> itemSet()
	{
		return ZMultiplyType.zeroOrMore(item());
	}

	public void lock()
	{
		m_isLocked = true;
	}

	public SmNamespaceNodeType<A> namespace()
	{
		return NAMESPACE;
	}

	public SmPrimeType<A> node()
	{
		return ANY_KIND;
	}

	public SmProcessingInstructionNodeType<A> processingInstruction(final String name)
	{
		if (null == name)
		{
			return PROCESSING_INSTRUCTION;
		}
		else
		{
			return new ProcessingInstructionNodeType<A>(name, this);
		}
	}

	private void recordNamespace(final SchemaComponent<A> component)
	{
		if (!component.isAnonymous())
		{
			namespaces.add(component.getTargetNamespace());
		}
	}

	public void register(final ComponentBag<A> components)
	{
		assertNotLocked();
		if (null != components)
		{
			for (final SmSimpleType<A> simpleType : components.getSimpleTypes())
			{
				defineSimpleType(simpleType);
			}
			for (final SmComplexType<A> complexType : components.getComplexTypes())
			{
				defineComplexType(complexType);
			}
			for (final AttributeDefinition<A> attribute : components.getAttributes())
			{
				declareAttribute(attribute);
			}
			for (final ElementDefinition<A> element : components.getElements())
			{
				declareElement(element);
			}
			for (final AttributeGroupDefinition<A> attributeGroup : components.getAttributeGroups())
			{
				defineAttributeGroup(attributeGroup);
			}
			for (final SmIdentityConstraint<A> identityConstraint : components.getIdentityConstraints())
			{
				defineIdentityConstraint(identityConstraint);
			}
			for (final ModelGroup<A> modelGroup : components.getModelGroups())
			{
				defineModelGroup(modelGroup);
			}
			for (final NotationDefinition<A> notation : components.getNotations())
			{
				declareNotation(notation);
			}
		}
	}

	public SmPrimeType<A> text()
	{
		return TEXT;
	}
}
