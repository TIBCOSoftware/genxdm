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

import org.genxdm.bridgekit.xs.complex.AttributeNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.CommentNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ComplexUrTypeImpl;
import org.genxdm.bridgekit.xs.complex.DocumentNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ElementNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.NamespaceNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ProcessingInstructionNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.TextNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ZMultiplyType;
import org.genxdm.bridgekit.xs.complex.ZPrimeChoiceType;
import org.genxdm.bridgekit.xs.simple.AtomicUrTypeImpl;
import org.genxdm.bridgekit.xs.simple.SimpleUrTypeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.Schema;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ComponentKind;
import org.genxdm.xs.components.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaComponent;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.NodeType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

final class SchemaCacheImpl implements ComponentProvider, Schema
{
	final AtomicUrTypeImpl ANY_ATOMIC_TYPE;
	private final ComplexUrTypeImpl ANY_COMPLEX_TYPE;
	final PrimeType ANY_KIND;
	private final SimpleUrTypeImpl ANY_SIMPLE_TYPE;
	private final AttributeNodeType ATTRIBUTE;

	private final BuiltInSchema BUILT_IN;
	private final NodeType COMMENT;
	private final DocumentNodeType DOCUMENT;
	private final ElementNodeType ELEMENT;
	final ConcurrentHashMap<QName, AttributeGroupDefinition> m_attributeGroups = new ConcurrentHashMap<QName, AttributeGroupDefinition>();
	final ConcurrentHashMap<QName, AttributeDefinition> m_attributes = new ConcurrentHashMap<QName, AttributeDefinition>();

	final ConcurrentHashMap<QName, ComplexType> m_complexTypes = new ConcurrentHashMap<QName, ComplexType>();
	final ConcurrentHashMap<QName, ElementDefinition> m_elements = new ConcurrentHashMap<QName, ElementDefinition>();
	final ConcurrentHashMap<QName, IdentityConstraint> m_identityConstraints = new ConcurrentHashMap<QName, IdentityConstraint>();
	private boolean m_isLocked = false;
	final ConcurrentHashMap<QName, ModelGroup> m_modelGroups = new ConcurrentHashMap<QName, ModelGroup>();
	private int m_nextType = 0;
	final ConcurrentHashMap<QName, NotationDefinition> m_notations = new ConcurrentHashMap<QName, NotationDefinition>();
	private final ConcurrentHashMap<QName, SimpleType> m_simpleTypes = new ConcurrentHashMap<QName, SimpleType>();
	private final NameSource nameBridge = NameSource.SINGLETON;
	private final NamespaceNodeType NAMESPACE;
	/**
	 * The set of namespaces of all components. We build this during registration, which acts as the gateway.
	 */
	private final Set<String> namespaces = new HashSet<String>();
	private final ProcessingInstructionNodeType PROCESSING_INSTRUCTION;

	private final NodeType TEXT;
	private static final String ESCAPE = "\u001B";
	private final QName WILDNAME = new QName(ESCAPE, ESCAPE);

	public SchemaCacheImpl()
	{
		assertNotLocked();

		final String W3C_XML_SCHEMA_NS_URI = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		this.ANY_COMPLEX_TYPE = new ComplexUrTypeImpl(W3C_XML_SCHEMA_NS_URI, nameBridge);
		this.ANY_SIMPLE_TYPE = new SimpleUrTypeImpl(W3C_XML_SCHEMA_NS_URI, this);
		this.ANY_ATOMIC_TYPE = new AtomicUrTypeImpl(W3C_XML_SCHEMA_NS_URI, ANY_SIMPLE_TYPE);

		defineComplexType(ANY_COMPLEX_TYPE);
		defineSimpleType(ANY_SIMPLE_TYPE);
		defineSimpleType(ANY_ATOMIC_TYPE);

		BUILT_IN = new BuiltInSchema(W3C_XML_SCHEMA_NS_URI, this);
		register(BUILT_IN);
		register(new XmlSchema(this));
		register(new XsiSchema(this));

		ELEMENT = new ElementNodeTypeImpl(WILDNAME, null, false, this);
		NAMESPACE = new NamespaceNodeTypeImpl(this);
		ATTRIBUTE = new AttributeNodeTypeImpl(WILDNAME, null, this);
		COMMENT = new CommentNodeTypeImpl(this);
		PROCESSING_INSTRUCTION = new ProcessingInstructionNodeTypeImpl(null, this);
		TEXT = new TextNodeTypeImpl(this);
		final SequenceType X = ZMultiplyType.zeroOrMore(ZPrimeChoiceType.choice(ELEMENT, ZPrimeChoiceType.choice(TEXT, ZPrimeChoiceType.choice(COMMENT, PROCESSING_INSTRUCTION))));
		DOCUMENT = new DocumentNodeTypeImpl(X, this);
		ANY_KIND = ZPrimeChoiceType.choice(ELEMENT, ZPrimeChoiceType.choice(ATTRIBUTE, ZPrimeChoiceType.choice(TEXT, ZPrimeChoiceType.choice(DOCUMENT, ZPrimeChoiceType.choice(COMMENT, ZPrimeChoiceType.choice(NAMESPACE, PROCESSING_INSTRUCTION))))));
	}

	private void assertNotLocked()
	{
		PreCondition.assertFalse(m_isLocked, "isLocked -> true");
	}

	private QName checkComponent(final SchemaComponent component, final ComponentKind kind)
	{
		PreCondition.assertArgumentNotNull(component);
		if (!kind.canBeAnonymous)
		{
			PreCondition.assertFalse(component.isAnonymous());
		}
		return PreCondition.assertArgumentNotNull(component.getName());
	}

	public PrimeType comment()
	{
		return COMMENT;
	}

	public void declareAttribute(final AttributeDefinition attribute)
	{
		final QName name = checkComponent(attribute, ComponentKind.ATTRIBUTE);
		if (!m_attributes.containsKey(name))
		{
			m_attributes.put(name, attribute);
			recordNamespace(attribute);
		}
	}

	public void declareElement(final ElementDefinition element)
	{
		final QName name = checkComponent(element, ComponentKind.ELEMENT);
		if (!m_elements.containsKey(name))
		{
			m_elements.put(name, element);
			recordNamespace(element);
		}
	}

	public void declareNotation(final NotationDefinition notation)
	{
		final QName name = checkComponent(notation, ComponentKind.NOTATION);
		if (!m_notations.containsKey(name))
		{
			m_notations.put(name, notation);
			recordNamespace(notation);
		}
	}

	public void defineAttributeGroup(final AttributeGroupDefinition attributeGroup)
	{
		final QName name = checkComponent(attributeGroup, ComponentKind.ATTRIBUTE_GROUP);
		if (!m_attributeGroups.containsKey(name))
		{
			m_attributeGroups.put(name, attributeGroup);
			recordNamespace(attributeGroup);
		}
	}

	public void defineComplexType(final ComplexType complexType)
	{
		final QName name = checkComponent(complexType, ComponentKind.COMPLEX_TYPE);
		if (!m_complexTypes.containsKey(name))
		{
			m_complexTypes.put(name, complexType);
			recordNamespace(complexType);
		}
	}

	public void defineIdentityConstraint(final IdentityConstraint identityConstraint)
	{
		final QName name = checkComponent(identityConstraint, ComponentKind.IDENTITY_CONSTRAINT);
		if (!m_identityConstraints.containsKey(name))
		{
			m_identityConstraints.put(name, identityConstraint);
			recordNamespace(identityConstraint);
		}
	}

	public void defineModelGroup(final ModelGroup modelGroup)
	{
		final QName name = checkComponent(modelGroup, ComponentKind.MODEL_GROUP);
		if (!m_modelGroups.containsKey(name))
		{
			m_modelGroups.put(name, modelGroup);
			recordNamespace(modelGroup);
		}
	}

	public void defineSimpleType(final SimpleType simpleType)
	{
		final QName name = checkComponent(simpleType, ComponentKind.SIMPLE_TYPE);
		if (!m_simpleTypes.containsKey(name))
		{
			m_simpleTypes.put(name, simpleType);
			recordNamespace(simpleType);
		}
	}

	public DocumentNodeType documentType(final SequenceType contentType)
	{
		if (null != contentType)
		{
			return new DocumentNodeTypeImpl(contentType, this);
		}
		else
		{
			return DOCUMENT;
		}
	}

	// no longer accessible via interface.
	public ElementNodeType elementWild(final SequenceType type, final boolean nillable)
	{
		return new ElementNodeTypeImpl(WILDNAME, type, nillable, this);
	}

	public synchronized QName generateUniqueName()
	{
		assertNotLocked();
		return new QName("http://www.tibco.com/gXML-SA/local-types", "type-".concat(Integer.toString(m_nextType++)));
	}

	public AtomicType getAtomicType(final QName name)
	{
		final SimpleType simpleType = m_simpleTypes.get(name);
		if (simpleType.isAtomicType())
		{
			return (AtomicType)simpleType;
		}
		else
		{
			return null;
		}
	}

	public AtomicType getAtomicType(final NativeType nativeType)
	{
		final Type type = getTypeDefinition(nativeType);
		if (type.isAtomicType())
		{
			return (AtomicType)type;
		}
		else
		{
			return null;
		}
	}

	public AtomicUrType getAtomicUrType()
	{
		return ANY_ATOMIC_TYPE;
	}

	public AttributeDefinition getAttributeDeclaration(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_attributes.get(name);
	}

	public AttributeGroupDefinition getAttributeGroup(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
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
		PreCondition.assertArgumentNotNull(name, "name");
		return m_complexTypes.get(name);
	}

	public Iterable<ComplexType> getComplexTypes()
	{
		return m_complexTypes.values();
	}

	public ComplexUrType getComplexUrType()
	{
		return ANY_COMPLEX_TYPE;
	}

	public ElementDefinition getElementDeclaration(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_elements.get(name);
	}

	public Iterable<ElementDefinition> getElements()
	{
		return m_elements.values();
	}

	public IdentityConstraint getIdentityConstraint(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_identityConstraints.get(name);
	}

	public Iterable<IdentityConstraint> getIdentityConstraints()
	{
		return m_identityConstraints.values();
	}

	public ModelGroup getModelGroup(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_modelGroups.get(name);
	}

	public Iterable<ModelGroup> getModelGroups()
	{
		return m_modelGroups.values();
	}

	public Iterable<String> getNamespaces()
	{
		return namespaces;
	}

	public NotationDefinition getNotationDeclaration(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_notations.get(name);
	}

	public Iterable<NotationDefinition> getNotations()
	{
		return m_notations.values();
	}

	public SimpleType getSimpleType(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_simpleTypes.get(name);
	}

	public SimpleType getSimpleType(final NativeType nativeType)
	{
		final Type type = getTypeDefinition(nativeType);
		if (type instanceof SimpleType)
		{
			return (SimpleType)type;
		}
		else
		{
			return null;
		}
	}

	public Iterable<SimpleType> getSimpleTypes()
	{
		return m_simpleTypes.values();
	}

	public SimpleUrType getSimpleUrType()
	{
		return ANY_SIMPLE_TYPE;
	}

	public Type getTypeDefinition(final QName name)
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

	public Type getTypeDefinition(final NativeType nativeType)
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

	public void lock()
	{
		m_isLocked = true;
	}

	public NamespaceNodeType namespace()
	{
		return NAMESPACE;
	}

	public PrimeType node()
	{
		return ANY_KIND;
	}

	public ProcessingInstructionNodeType processingInstruction(final String name)
	{
		if (null == name)
		{
			return PROCESSING_INSTRUCTION;
		}
		else
		{
			return new ProcessingInstructionNodeTypeImpl(name, this);
		}
	}

	private void recordNamespace(final SchemaComponent component)
	{
		if (!component.isAnonymous())
		{
			namespaces.add(component.getTargetNamespace());
		}
	}

	public void register(final ComponentBag components)
	{
		assertNotLocked();
		if (null != components)
		{
			for (final SimpleType simpleType : components.getSimpleTypes())
			{
				defineSimpleType(simpleType);
			}
			for (final ComplexType complexType : components.getComplexTypes())
			{
				defineComplexType(complexType);
			}
			for (final AttributeDefinition attribute : components.getAttributes())
			{
				declareAttribute(attribute);
			}
			for (final ElementDefinition element : components.getElements())
			{
				declareElement(element);
			}
			for (final AttributeGroupDefinition attributeGroup : components.getAttributeGroups())
			{
				defineAttributeGroup(attributeGroup);
			}
			for (final IdentityConstraint identityConstraint : components.getIdentityConstraints())
			{
				defineIdentityConstraint(identityConstraint);
			}
			for (final ModelGroup modelGroup : components.getModelGroups())
			{
				defineModelGroup(modelGroup);
			}
			for (final NotationDefinition notation : components.getNotations())
			{
				declareNotation(notation);
			}
		}
	}

}
