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

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.complex.AttributeDeclWithParentAxisType;
import org.genxdm.bridgekit.xs.complex.AttributeNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.CommentNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ElementDeclWithParentAxisType;
import org.genxdm.bridgekit.xs.complex.ElementNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ElementNodeWithParentAxisType;
import org.genxdm.bridgekit.xs.complex.NoneTypeImpl;
import org.genxdm.bridgekit.xs.complex.ProcessingInstructionNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.TextNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ZChoiceType;
import org.genxdm.bridgekit.xs.complex.ZConcatType;
import org.genxdm.bridgekit.xs.complex.ZInterleaveType;
import org.genxdm.bridgekit.xs.complex.ZMultiplyType;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.SchemaTypeBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.ElementUse;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.ModelGroupUse;
import org.genxdm.xs.constraints.NamespaceConstraint;
import org.genxdm.xs.constraints.WildcardUse;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.CommentNodeType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.TextNodeType;
import org.genxdm.xs.types.Type;

final class SchemaTypeBridgeImpl implements SchemaTypeBridge
{
	private final AtomicUrType ANY_ATOMIC_TYPE;
	private final ComplexUrType ANY_COMPLEX_TYPE;
	private final SimpleUrType ANY_SIMPLE_TYPE;

	// private final DocumentNodeType DOCUMENT;
	// private final ElementNodeType ELEMENT;
	private final CommentNodeType COMMENT;

	private final ConcurrentHashMap<Type, ArrayList<AttributeUse>> m_attributeUses = new ConcurrentHashMap<Type, ArrayList<AttributeUse>>();

	private final SchemaCacheImpl m_cache;
	private final NameSource m_nameBridge = NameSource.SINGLETON;

	private final ProcessingInstructionNodeType PROCESSING_INSTRUCTION;
	private final TextNodeType TEXT;
    private static final String ESCAPE = "\u001B";
	private final QName WILDNAME = new QName(ESCAPE, ESCAPE);

	public SchemaTypeBridgeImpl()
	{
		m_cache = new SchemaCacheImpl();

		ANY_COMPLEX_TYPE = m_cache.getComplexUrType();
		ANY_SIMPLE_TYPE = m_cache.getSimpleUrType();
		ANY_ATOMIC_TYPE = m_cache.getAtomicUrType();
		// ELEMENT = new ElementNodeTypeImpl(WILDNAME, null, false, m_cache);
		COMMENT = new CommentNodeTypeImpl(m_cache);
		PROCESSING_INSTRUCTION = new ProcessingInstructionNodeTypeImpl(null, m_cache);
		TEXT = new TextNodeTypeImpl(m_cache);
	}

	private void assertNotLocked()
	{
		PreCondition.assertFalse(m_cache.isLocked());
	}

	public SequenceType atomSet(final SequenceType type)
	{
		if (type instanceof SimpleType)
		{
			return (SimpleType) type;
		}
		else
		{
			return zeroOrMore(getTypeDefinition(NativeType.ANY_ATOMIC_TYPE));
			// throw new AssertionError("TODO: atomSet(" + type.getClass() + ")");
		}
	}

	public SequenceType attributeAxis(final SequenceType type)
	{
		final PrimeType prime = type.prime();
		switch (prime.getKind())
		{
		case CHOICE:
		{
			final PrimeChoiceType choiceType = (PrimeChoiceType) prime;
			return multiply(choice(attributeAxis(choiceType.getLHS()), attributeAxis(choiceType.getRHS())), type.quantifier());
		}
		case ELEMENT:
		{
			return attributeWild(getTypeDefinition(NativeType.UNTYPED_ATOMIC));
		}
		case SCHEMA_ELEMENT:
		{
			final ElementDefinition elementDecl = (ElementDefinition) prime;
			final Type smType = elementDecl.getType();
			if (smType instanceof ComplexType)
			{
				final ComplexType complexType = (ComplexType) smType;
				return attributeAxisFromComplexType(complexType, elementDecl);
			}
			else if (smType instanceof SimpleType)
			{
				return emptyType();
			}
			else
			{
				// The type must be either a simple or a complex type.
				throw new AssertionError();
			}
		}
		case COMPLEX:
		{
			final ComplexType complexType = (ComplexType) prime;
			return attributeAxisFromComplexType(complexType, null);
		}
		case NONE:
		{
			return noneType();
		}
		default:
		{
			return emptyType();
		}
		}
	}

	private SequenceType attributeAxisFromComplexType(final ComplexType complexType, final ElementDefinition parentAxis)
	{
		final ArrayList<AttributeUse> attributeUses = ensureAttributeUses(complexType);
		SequenceType result = null;
		for (final AttributeUse attributeUse : attributeUses)
		{
			final SequenceType attributeType = attributeUseType(attributeUse, parentAxis);
			if (result == null)
			{
				result = attributeType;
			}
			else
			{
				result = interleave(result, attributeType);
			}
		}
		return result == null ? emptyType() : result;
	}

	public AttributeNodeType attributeType(final QName name, final SequenceType type)
	{
		if (null != name)
		{
			return new AttributeNodeTypeImpl(name, type, m_cache);
		}
		else
		{
			return attributeWild(type);
		}
	}

	private SequenceType attributeUseType(final AttributeUse attributeUse, final ElementDefinition parentAxis)
	{
		final AttributeDefinition attribute = attributeUse.getAttribute();
		if (null != parentAxis)
		{
			return multiply(new AttributeDeclWithParentAxisType(attribute, parentAxis), attributeUse.isRequired() ? KeeneQuantifier.EXACTLY_ONE : KeeneQuantifier.OPTIONAL);
		}
		else
		{
			return multiply(attribute, attributeUse.isRequired() ? KeeneQuantifier.EXACTLY_ONE : KeeneQuantifier.OPTIONAL);
		}
	}

	public AttributeNodeType attributeWild(final SequenceType type)
	{
		return new AttributeNodeTypeImpl(WILDNAME, type, m_cache);
	}

	public SequenceType childAxis(final SequenceType focus)
	{
		final PrimeType prime = focus.prime();
		switch (prime.getKind())
		{
		case CHOICE:
		{
			final PrimeChoiceType choiceType = (PrimeChoiceType) prime;
			return multiply(choice(childAxis(choiceType.getLHS()), childAxis(choiceType.getRHS())), focus.quantifier());
		}
		case DOCUMENT:
		{
			final DocumentNodeType documentNodeType = (DocumentNodeType) prime;
			final SequenceType contentType = documentNodeType.getContentType();
			if (null != contentType)
			{
				return contentType;
			}
			else
			{
				final ElementNodeType elementType = elementWild(null, true);
				final TextNodeType textType = textType();
				final CommentNodeType commentType = commentType();
				final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

				return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), focus.quantifier());
			}
		}
		case ELEMENT:
		{
			final ElementNodeType element = (ElementNodeType) prime;
			final SequenceType dataType = element.getType();
			if (subtype(dataType, zeroOrMore(nodeType())))
			{
				return dataType;
			}
			else
			{
				final PrimeType elementType = elementWild(null, true);
				final TextNodeType textType = textType();
				final CommentNodeType commentType = commentType();
				final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

				return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), focus.quantifier());
			}
		}
		case SCHEMA_ELEMENT:
		{
			final ElementDefinition elementDecl = (ElementDefinition) prime;
			final Type type = elementDecl.getType();
			if (type instanceof ComplexType)
			{
				final ComplexType complexType = (ComplexType) type;
				return childAxisFromComplexType(complexType, elementDecl);
			}
			else if (type instanceof SimpleType)
			{
				return emptyType();
			}
			else
			{
				// The type must be either a simple or a complex type.
				throw new AssertionError();
			}
		}
		case COMPLEX:
		{
			// TODO: This appears to be unreachable...
			final ComplexType complexType = (ComplexType) prime;
			return childAxisFromComplexType(complexType, null);
		}
		case NONE:
		{
			return noneType();
		}
		default:
		{
			return emptyType();
		}
		}
	}

	// TODO: The suggestion here is that we can factor out this function, maybe embed in the
	// complex type so that it can be cached.
	private SequenceType childAxisFromComplexType(final ComplexType complexType, final ElementDefinition parentDecl)
	{
		final ContentType contentType = complexType.getContentType();
		if (contentType.isMixed() || contentType.isElementOnly())
		{
			return modelGroupUseType(contentType.getContentModel(), parentDecl);
		}
		else
		{
			// TODO:
			throw new AssertionError();
		}
	}

	public SequenceType choice(final SequenceType lhs, final SequenceType rhs)
	{
		return ZChoiceType.choice(lhs, rhs);
	}

	public CommentNodeType commentType()
	{
		return COMMENT;
	}

	public SequenceType concat(final SequenceType lhs, final SequenceType rhs)
	{
		return ZConcatType.concat(lhs, rhs);
	}

	public void declareAttribute(final AttributeDefinition attribute)
	{
		assertNotLocked();
		m_cache.declareAttribute(attribute);
	}

	public void declareElement(final ElementDefinition element)
	{
		assertNotLocked();
		m_cache.declareElement(element);
	}

	public void declareNotation(final NotationDefinition notation)
	{
		assertNotLocked();
		m_cache.declareNotation(notation);
	}

	public void defineAttributeGroup(final AttributeGroupDefinition attributeGroup)
	{
		assertNotLocked();
		m_cache.defineAttributeGroup(attributeGroup);
	}

	public void defineComplexType(final ComplexType complexType)
	{
		assertNotLocked();
		m_cache.defineComplexType(complexType);
	}

	public void defineIdentityConstraint(final IdentityConstraint identityConstraint)
	{
		assertNotLocked();
		m_cache.defineIdentityConstraint(identityConstraint);
	}

	public void defineModelGroup(final ModelGroup modelGroup)
	{
		assertNotLocked();
		m_cache.defineModelGroup(modelGroup);
	}

	public void defineSimpleType(final SimpleType simpleType)
	{
		assertNotLocked();
		m_cache.defineSimpleType(simpleType);
	}

	public DocumentNodeType documentType(final SequenceType contentType)
	{
		return m_cache.documentType(contentType);
	}

	public ElementNodeType elementType(final QName name, final SequenceType type, final boolean nillable)
	{
		if (null != name)
		{
			return new ElementNodeTypeImpl(name, type, nillable, m_cache);
		}
		else
		{
			return elementWild(type, nillable);
		}
	}

	private SequenceType elementUseType(final ElementUse elementUse, final ElementDefinition parentDecl)
	{
		final int minOccurs = elementUse.getMinOccurs();
		final int maxOccurs = elementUse.getMaxOccurs();
		final ElementDefinition elementDecl = elementUse.getTerm();
		if (null != parentDecl)
		{
			return multiply(new ElementDeclWithParentAxisType(elementDecl, parentDecl), KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
		else
		{
			return multiply(elementDecl, KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
	}

	public ElementNodeType elementWild(final SequenceType type, final boolean nillable)
	{
		return new ElementNodeTypeImpl(WILDNAME, type, nillable, m_cache);
	}

	public EmptyType emptyType()
	{
		return m_cache.empty();
	}

	private ArrayList<AttributeUse> ensureAttributeUses(final ComplexType complexType)
	{
		final ArrayList<AttributeUse> cachedAttributeUses = m_attributeUses.get(complexType);
		if (null != cachedAttributeUses)
		{
			return cachedAttributeUses;
		}
		else
		{
			final ArrayList<AttributeUse> attributeUses = new ArrayList<AttributeUse>();
			for (final AttributeUse attributeUse : complexType.getAttributeUses().values())
			{
				attributeUses.add(attributeUse);
			}
			m_attributeUses.put(complexType, attributeUses);
			return attributeUses;
		}
	}

	public QName generateUniqueName()
	{
		return m_cache.generateUniqueName();
	}

	public AtomicType getAtomicType(final QName name)
	{
		return m_cache.getAtomicType(name);
	}

	public AtomicType getAtomicType(final NativeType name)
	{
		return m_cache.getAtomicType(name);
	}

	public AtomicUrType getAtomicUrType()
	{
		return ANY_ATOMIC_TYPE;
	}

	public AttributeDefinition getAttributeDeclaration(final QName attributeName)
	{
		return m_cache.getAttributeDeclaration(attributeName);
	}

	public AttributeGroupDefinition getAttributeGroup(final QName name)
	{
		return m_cache.getAttributeGroup(name);
	}

	public Iterable<AttributeGroupDefinition> getAttributeGroups()
	{
		return m_cache.getAttributeGroups();
	}

	public Iterable<AttributeDefinition> getAttributes()
	{
		return m_cache.getAttributes();
	}

	public ComplexType getComplexType(final QName name)
	{
		return m_cache.getComplexType(name);
	}

	public Iterable<ComplexType> getComplexTypes()
	{
		return m_cache.getComplexTypes();
	}

	public ComplexUrType getComplexUrType()
	{
		return ANY_COMPLEX_TYPE;
	}

	public ElementDefinition getElementDeclaration(final QName elementName)
	{
		return m_cache.getElementDeclaration(elementName);
	}

	public Iterable<ElementDefinition> getElements()
	{
		return m_cache.getElements();
	}

	public IdentityConstraint getIdentityConstraint(final QName name)
	{
		return m_cache.getIdentityConstraint(name);
	}

	public Iterable<IdentityConstraint> getIdentityConstraints()
	{
		return m_cache.getIdentityConstraints();
	}

	public ModelGroup getModelGroup(final QName name)
	{
		return m_cache.getModelGroup(name);
	}

	public Iterable<ModelGroup> getModelGroups()
	{
		return m_cache.getModelGroups();
	}

	public QName getName(final SequenceType type)
	{
		if (type instanceof Type)
		{
			final Type itemType = (Type) type;
			return itemType.getName();
		}
		else if (type instanceof AttributeDefinition)
		{
			final AttributeDefinition attType = (AttributeDefinition) type;
			return attType.getName();
		}
		else if (type instanceof AttributeNodeType)
		{
			final AttributeNodeType attributeNodeType = (AttributeNodeType) type;
			return attributeNodeType.getName();
		}
		else if (type instanceof ElementNodeType)
		{
			final ElementNodeType elementNodeType = (ElementNodeType) type;
			return elementNodeType.getName();
		}
		else
		{
			throw new AssertionError("getName(" + type.getClass() + ")");
		}
	}

	public NameSource getNameBridge()
	{
		return m_cache.getNameBridge();
	}

	public Iterable<String> getNamespaces()
	{
		return m_cache.getNamespaces();
	}

	public NativeType getNearestBuiltInType(final SequenceType arg)
	{
		if (arg instanceof SimpleType)
		{
			final SimpleType atomicType = (SimpleType) arg;
			final QName name = atomicType.getName();
			return m_nameBridge.nativeType(name);
		}
		else
		{
			return null;
		}
	}

	public NotationDefinition getNotationDeclaration(final QName name)
	{
		return m_cache.getNotationDeclaration(name);
	}

	public Iterable<NotationDefinition> getNotations()
	{
		return m_cache.getNotations();
	}

	public SimpleType getSimpleType(final QName name)
	{
		return m_cache.getSimpleType(name);
	}

	public SimpleType getSimpleType(final NativeType name)
	{
		return m_cache.getSimpleType(name);
	}

	public Iterable<SimpleType> getSimpleTypes()
	{
		return m_cache.getSimpleTypes();
	}

	public SimpleUrType getSimpleUrType()
	{
		return ANY_SIMPLE_TYPE;
	}

	public Type getTypeDefinition(final QName name)
	{
		if (null != name)
		{
			if (m_cache.hasComplexType(name))
			{
				return m_cache.getComplexType(name);
			}
			else if (m_cache.hasSimpleType(name))
			{
				return m_cache.getSimpleType(name);
			}
			else if (ANY_COMPLEX_TYPE.getName().equals(name))
			{
				return ANY_COMPLEX_TYPE;
			}
			else if (ANY_SIMPLE_TYPE.getName().equals(name))
			{
				return ANY_SIMPLE_TYPE;
			}
			else if (ANY_ATOMIC_TYPE.getName().equals(name))
			{
				return ANY_ATOMIC_TYPE;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	public Type getTypeDefinition(final NativeType nativeType)
	{
		return m_cache.getTypeDefinition(nativeType);
	}

	public SequenceType handle(SequenceType sequenceType)
	{
		return sequenceType;
	}

	public boolean hasAttribute(final QName name)
	{
		return m_cache.hasAttribute(name);
	}

	public boolean hasAttributeGroup(final QName name)
	{
		return m_cache.hasAttributeGroup(name);
	}

	public boolean hasComplexType(final QName name)
	{
		return m_cache.hasComplexType(name);
	}

	public boolean hasElement(final QName name)
	{
		return m_cache.hasElement(name);
	}

	public boolean hasIdentityConstraint(final QName name)
	{
		return m_cache.hasIdentityConstraint(name);
	}

	public boolean hasModelGroup(final QName name)
	{
		return m_cache.hasModelGroup(name);
	}

	public boolean hasNotation(final QName name)
	{
		return m_cache.hasNotation(name);
	}

	public boolean hasSimpleType(final QName name)
	{
		return m_cache.hasSimpleType(name);
	}

	public boolean hasType(final QName name)
	{
		return m_cache.hasType(name);
	}

	public SequenceType interleave(final SequenceType lhs, final SequenceType rhs)
	{
		return ZInterleaveType.interleave(lhs, rhs);
	}

	public boolean isLocked()
	{
		return m_cache.isLocked();
	}

	public boolean isNative(final SequenceType arg)
	{
		if (arg instanceof PrimeType)
		{
			return arg.prime().isNative();
		}
		else
		{
			return false;
		}
	}

	public boolean isNone(final SequenceType type)
	{
		return (type instanceof NoneType);
	}

	public PrimeType itemType()
	{
		return m_cache.item();
	}

	public void lock()
	{
		m_cache.lock();
	}

	private SequenceType modelGroupUseType(final ModelGroupUse modelGroupUse, final ElementDefinition parentDecl)
	{
		final int minOccurs = modelGroupUse.getMinOccurs();
		final int maxOccurs = modelGroupUse.getMaxOccurs();
		final ModelGroup modelGroup = modelGroupUse.getTerm();
		final ModelGroup.SmCompositor compositor = modelGroup.getCompositor();

		SequenceType contentModel = null;
		for (final SchemaParticle particle : modelGroup.getParticles())
		{
			final SequenceType type = particle(particle, parentDecl);
			if (null != contentModel)
			{
				switch (compositor)
				{
				case Sequence:
				{
					contentModel = concat(contentModel, type);
				}
					break;
				case Choice:
				{
					contentModel = choice(contentModel, type);
				}
					break;
				case All:
				{
					contentModel = interleave(contentModel, type);
				}
					break;
				default:
				{
					// Unexpected compositor.
					throw new AssertionError(compositor);
				}
				}
			}
			else
			{
				contentModel = type;
			}
		}
		if (null != contentModel)
		{
			return multiply(contentModel, KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
		else
		{
			return emptyType();
		}
	}

	public SequenceType multiply(final SequenceType argument, final KeeneQuantifier multiplier)
	{
		PreCondition.assertArgumentNotNull(argument, "argument");
		if (null != argument)
		{
			if (sameAs(argument, emptyType()))
			{
				return argument;
			}
			else if (sameAs(argument, noneType()))
			{
				return argument;
			}
			else if (multiplier.isExactlyOne())
			{
				return argument;
			}
			else
			{
				return ZMultiplyType.multiply(argument, multiplier);
			}
		}
		else
		{
			// TODO: We need to assert that the type is not null. This is a patch.
			return null;
		}
	}

	public NamespaceNodeType namespaceType()
	{
		return m_cache.namespace();
	}

	public PrimeType nodeType()
	{
		return m_cache.node();
	}

	public NoneType noneType()
	{
		return new NoneTypeImpl();
	}

	public NoneType noneType(final QName errorCode)
	{
		return new NoneTypeImpl(errorCode);
	}

	public SequenceType oneOrMore(final SequenceType type)
	{
		return multiply(type, KeeneQuantifier.ONE_OR_MORE);
	}

	public SequenceType optional(final SequenceType type)
	{
		if (null != type)
		{
			return multiply(type, KeeneQuantifier.OPTIONAL);
		}
		else
		{
			return null;
		}
	}

	private SequenceType particle(final SchemaParticle particle, final ElementDefinition parentDecl)
	{
		if (particle instanceof ElementUse)
		{
			return elementUseType((ElementUse) particle, parentDecl);
		}
		else if (particle instanceof ModelGroupUse)
		{
			return modelGroupUseType((ModelGroupUse) particle, parentDecl);
		}
		else if (particle instanceof WildcardUse)
		{
			return wildcardUseType((WildcardUse) particle, parentDecl);
		}
		else
		{
			// There shouldn't be anything else beside element, model group and wildcard.
			throw new AssertionError(particle);
		}
	}

	public ProcessingInstructionNodeType processingInstructionType(final String name)
	{
		if (null != name)
		{
			return m_cache.processingInstruction(name);
		}
		else
		{
			return PROCESSING_INSTRUCTION;
		}
	}

	public void register(final ComponentBag components)
	{
		assertNotLocked();
		m_cache.register(components);
	}

	public boolean sameAs(final SequenceType one, final SequenceType two)
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");
		return subtype(one, two) && subtype(two, one);
	}

	public AttributeDefinition schemaAttribute(final QName attributeName)
	{
		return m_cache.getAttributeDeclaration(attributeName);
	}

	public ElementDefinition schemaElement(final QName elementName)
	{
		return m_cache.getElementDeclaration(elementName);
	}

	public SequenceType schemaType(final QName typeName)
	{
		return m_cache.getTypeDefinition(typeName);
	}

	public boolean subtype(final SequenceType lhs, final SequenceType rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		return SchemaSupport.subtype(lhs, rhs);
	}

	public TextNodeType textType()
	{
		return TEXT;
	}

	public SequenceType[] typeArray(final int size)
	{
		return new SequenceType[size];
	}

	private SequenceType wildcardUseType(final WildcardUse wildcardUse, final ElementDefinition parentDecl)
	{
		final int minOccurs = wildcardUse.getMinOccurs();
		final int maxOccurs = wildcardUse.getMaxOccurs();
		final SchemaWildcard term = wildcardUse.getTerm();
		// final ProcessContentsMode processContents = term.getProcessContents();
		final NamespaceConstraint namespaceConstraint = term.getNamespaceConstraint();
		switch (namespaceConstraint.getMode())
		{
		case Any:
		{
			return multiply(new ElementNodeWithParentAxisType(elementWild(null, true), parentDecl), KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
		case Include:
		{
			SequenceType type = null;
			for (final String namespace : namespaceConstraint.getNamespaces())
			{
				final ElementNodeWithParentAxisType append = 
				    new ElementNodeWithParentAxisType(new ElementNodeTypeImpl(new QName(namespace, null), null, true, m_cache), parentDecl);
				if (null != type)
				{
					type = choice(type, append);
				}
				else
				{
					type = append;
				}
			}
			return multiply(type, KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
		case Exclude:
		{
			// TODO: How do we define a regular expression type that excludes certain namespaces?
			// TODO: We don't even have the concept of AND.
			return multiply(new ElementNodeWithParentAxisType(new ElementNodeTypeImpl(WILDNAME, null, true, m_cache), parentDecl), KeeneQuantifier.approximate(minOccurs,
					maxOccurs));
		}
		default:
		{
			throw new AssertionError();
		}
		}
	}

	public SequenceType zeroOrMore(final SequenceType type)
	{
		return multiply(type, KeeneQuantifier.ZERO_OR_MORE);
	}
}
