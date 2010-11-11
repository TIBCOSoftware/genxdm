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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
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

final class SchemaTypeBridgeImpl<A> implements SchemaTypeBridge<A>
{
	private final AtomicUrType<A> ANY_ATOMIC_TYPE;
	private final ComplexUrType<A> ANY_COMPLEX_TYPE;
	private final SimpleUrType<A> ANY_SIMPLE_TYPE;

	// private final DocumentNodeType<A> DOCUMENT;
	// private final ElementNodeType<A> ELEMENT;
	private final CommentNodeType<A> COMMENT;

	private final ConcurrentHashMap<Type<A>, ArrayList<AttributeUse<A>>> m_attributeUses = new ConcurrentHashMap<Type<A>, ArrayList<AttributeUse<A>>>();

	private final SchemaCacheImpl<A> m_cache;
	private final NameSource m_nameBridge;

	private final ProcessingInstructionNodeType<A> PROCESSING_INSTRUCTION;
	private final TextNodeType<A> TEXT;
    private static final String ESCAPE = "\u001B";
	private final QName WILDNAME = new QName(ESCAPE, ESCAPE);

	public SchemaTypeBridgeImpl(final AtomBridge<A> atomBridge)
	{
		m_cache = new SchemaCacheImpl<A>(PreCondition.assertArgumentNotNull(atomBridge, "atomBridge"));
		m_nameBridge = atomBridge.getNameBridge();

		ANY_COMPLEX_TYPE = m_cache.getComplexUrType();
		ANY_SIMPLE_TYPE = m_cache.getSimpleUrType();
		ANY_ATOMIC_TYPE = m_cache.getAtomicUrType();
		// ELEMENT = new ElementNodeTypeImpl<A>(WILDNAME, null, false, m_cache);
		COMMENT = new CommentNodeTypeImpl<A>(m_cache);
		PROCESSING_INSTRUCTION = new ProcessingInstructionNodeTypeImpl<A>(null, m_cache);
		TEXT = new TextNodeTypeImpl<A>(m_cache);
	}

	private void assertNotLocked()
	{
		PreCondition.assertFalse(m_cache.isLocked());
	}

	public SequenceType<A> atomSet(final SequenceType<A> type)
	{
		if (type instanceof SimpleType<?>)
		{
			return (SimpleType<A>) type;
		}
		else
		{
			return zeroOrMore(getTypeDefinition(NativeType.ANY_ATOMIC_TYPE));
			// throw new AssertionError("TODO: atomSet(" + type.getClass() + ")");
		}
	}

	@SuppressWarnings("unchecked")
	public SequenceType<A> attributeAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
		case CHOICE:
		{
			final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>) prime;
			return multiply(choice(attributeAxis(choiceType.getLHS()), attributeAxis(choiceType.getRHS())), type.quantifier());
		}
		case ELEMENT:
		{
			return attributeWild(getTypeDefinition(NativeType.UNTYPED_ATOMIC));
		}
		case SCHEMA_ELEMENT:
		{
			final ElementDefinition<A> elementDecl = (ElementDefinition<A>) prime;
			final Type<A> smType = elementDecl.getType();
			if (smType instanceof ComplexType)
			{
				final ComplexType<A> complexType = (ComplexType<A>) smType;
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
			final ComplexType<A> complexType = (ComplexType<A>) prime;
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

	private SequenceType<A> attributeAxisFromComplexType(final ComplexType<A> complexType, final ElementDefinition<A> parentAxis)
	{
		final ArrayList<AttributeUse<A>> attributeUses = ensureAttributeUses(complexType);
		SequenceType<A> result = null;
		for (final AttributeUse<A> attributeUse : attributeUses)
		{
			final SequenceType<A> attributeType = attributeUseType(attributeUse, parentAxis);
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

	public AttributeNodeType<A> attributeType(final QName name, final SequenceType<A> type)
	{
		if (null != name)
		{
			return new AttributeNodeTypeImpl<A>(name, type, m_cache);
		}
		else
		{
			return attributeWild(type);
		}
	}

	private SequenceType<A> attributeUseType(final AttributeUse<A> attributeUse, final ElementDefinition<A> parentAxis)
	{
		final AttributeDefinition<A> attribute = attributeUse.getAttribute();
		if (null != parentAxis)
		{
			return multiply(new AttributeDeclWithParentAxisType<A>(attribute, parentAxis), attributeUse.isRequired() ? KeeneQuantifier.EXACTLY_ONE : KeeneQuantifier.OPTIONAL);
		}
		else
		{
			return multiply(attribute, attributeUse.isRequired() ? KeeneQuantifier.EXACTLY_ONE : KeeneQuantifier.OPTIONAL);
		}
	}

	public AttributeNodeType<A> attributeWild(final SequenceType<A> type)
	{
		return new AttributeNodeTypeImpl<A>(WILDNAME, type, m_cache);
	}

	@SuppressWarnings("unchecked")
	public SequenceType<A> childAxis(final SequenceType<A> focus)
	{
		final PrimeType<A> prime = focus.prime();
		switch (prime.getKind())
		{
		case CHOICE:
		{
			final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>) prime;
			return multiply(choice(childAxis(choiceType.getLHS()), childAxis(choiceType.getRHS())), focus.quantifier());
		}
		case DOCUMENT:
		{
			final DocumentNodeType<A> documentNodeType = (DocumentNodeType<A>) prime;
			final SequenceType<A> contentType = documentNodeType.getContentType();
			if (null != contentType)
			{
				return contentType;
			}
			else
			{
				final ElementNodeType<A> elementType = elementWild(null, true);
				final TextNodeType<A> textType = textType();
				final CommentNodeType<A> commentType = commentType();
				final ProcessingInstructionNodeType<A> processingInstructionType = processingInstructionType(null);

				return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), focus.quantifier());
			}
		}
		case ELEMENT:
		{
			final ElementNodeType<A> element = (ElementNodeType<A>) prime;
			final SequenceType<A> dataType = element.getType();
			if (subtype(dataType, zeroOrMore(nodeType())))
			{
				return dataType;
			}
			else
			{
				final PrimeType<A> elementType = elementWild(null, true);
				final TextNodeType<A> textType = textType();
				final CommentNodeType<A> commentType = commentType();
				final ProcessingInstructionNodeType<A> processingInstructionType = processingInstructionType(null);

				return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), focus.quantifier());
			}
		}
		case SCHEMA_ELEMENT:
		{
			final ElementDefinition<A> elementDecl = (ElementDefinition<A>) prime;
			final Type<A> type = elementDecl.getType();
			if (type instanceof ComplexType)
			{
				final ComplexType<A> complexType = (ComplexType<A>) type;
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
			final ComplexType<A> complexType = (ComplexType<A>) prime;
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
	private SequenceType<A> childAxisFromComplexType(final ComplexType<A> complexType, final ElementDefinition<A> parentDecl)
	{
		final ContentType<A> contentType = complexType.getContentType();
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

	public SequenceType<A> choice(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		return ZChoiceType.choice(lhs, rhs);
	}

	public CommentNodeType<A> commentType()
	{
		return COMMENT;
	}

	public SequenceType<A> concat(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		return ZConcatType.concat(lhs, rhs);
	}

	public void declareAttribute(final AttributeDefinition<A> attribute)
	{
		assertNotLocked();
		m_cache.declareAttribute(attribute);
	}

	public void declareElement(final ElementDefinition<A> element)
	{
		assertNotLocked();
		m_cache.declareElement(element);
	}

	public void declareNotation(final NotationDefinition<A> notation)
	{
		assertNotLocked();
		m_cache.declareNotation(notation);
	}

	public void defineAttributeGroup(final AttributeGroupDefinition<A> attributeGroup)
	{
		assertNotLocked();
		m_cache.defineAttributeGroup(attributeGroup);
	}

	public void defineComplexType(final ComplexType<A> complexType)
	{
		assertNotLocked();
		m_cache.defineComplexType(complexType);
	}

	public void defineIdentityConstraint(final IdentityConstraint<A> identityConstraint)
	{
		assertNotLocked();
		m_cache.defineIdentityConstraint(identityConstraint);
	}

	public void defineModelGroup(final ModelGroup<A> modelGroup)
	{
		assertNotLocked();
		m_cache.defineModelGroup(modelGroup);
	}

	public void defineSimpleType(final SimpleType<A> simpleType)
	{
		assertNotLocked();
		m_cache.defineSimpleType(simpleType);
	}

	public DocumentNodeType<A> documentType(final SequenceType<A> contentType)
	{
		return m_cache.documentType(contentType);
	}

	public ElementNodeType<A> elementType(final QName name, final SequenceType<A> type, final boolean nillable)
	{
		if (null != name)
		{
			return new ElementNodeTypeImpl<A>(name, type, nillable, m_cache);
		}
		else
		{
			return elementWild(type, nillable);
		}
	}

	private SequenceType<A> elementUseType(final ElementUse<A> elementUse, final ElementDefinition<A> parentDecl)
	{
		final int minOccurs = elementUse.getMinOccurs();
		final int maxOccurs = elementUse.getMaxOccurs();
		final ElementDefinition<A> elementDecl = elementUse.getTerm();
		if (null != parentDecl)
		{
			return multiply(new ElementDeclWithParentAxisType<A>(elementDecl, parentDecl), KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
		else
		{
			return multiply(elementDecl, KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
	}

	public ElementNodeType<A> elementWild(final SequenceType<A> type, final boolean nillable)
	{
		return new ElementNodeTypeImpl<A>(WILDNAME, type, nillable, m_cache);
	}

	public EmptyType<A> emptyType()
	{
		return m_cache.empty();
	}

	private ArrayList<AttributeUse<A>> ensureAttributeUses(final ComplexType<A> complexType)
	{
		final ArrayList<AttributeUse<A>> cachedAttributeUses = m_attributeUses.get(complexType);
		if (null != cachedAttributeUses)
		{
			return cachedAttributeUses;
		}
		else
		{
			final ArrayList<AttributeUse<A>> attributeUses = new ArrayList<AttributeUse<A>>();
			for (final AttributeUse<A> attributeUse : complexType.getAttributeUses().values())
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

	public AtomBridge<A> getAtomBridge()
	{
		return m_cache.getAtomBridge();
	}

	public AtomicType<A> getAtomicType(final QName name)
	{
		return m_cache.getAtomicType(name);
	}

	public AtomicType<A> getAtomicType(final NativeType name)
	{
		return m_cache.getAtomicType(name);
	}

	public AtomicUrType<A> getAtomicUrType()
	{
		return ANY_ATOMIC_TYPE;
	}

	public AttributeDefinition<A> getAttributeDeclaration(final QName attributeName)
	{
		return m_cache.getAttributeDeclaration(attributeName);
	}

	public AttributeGroupDefinition<A> getAttributeGroup(final QName name)
	{
		return m_cache.getAttributeGroup(name);
	}

	public Iterable<AttributeGroupDefinition<A>> getAttributeGroups()
	{
		return m_cache.getAttributeGroups();
	}

	public Iterable<AttributeDefinition<A>> getAttributes()
	{
		return m_cache.getAttributes();
	}

	public ComplexType<A> getComplexType(final QName name)
	{
		return m_cache.getComplexType(name);
	}

	public Iterable<ComplexType<A>> getComplexTypes()
	{
		return m_cache.getComplexTypes();
	}

	public ComplexUrType<A> getComplexUrType()
	{
		return ANY_COMPLEX_TYPE;
	}

	public ElementDefinition<A> getElementDeclaration(final QName elementName)
	{
		return m_cache.getElementDeclaration(elementName);
	}

	public Iterable<ElementDefinition<A>> getElements()
	{
		return m_cache.getElements();
	}

	public IdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return m_cache.getIdentityConstraint(name);
	}

	public Iterable<IdentityConstraint<A>> getIdentityConstraints()
	{
		return m_cache.getIdentityConstraints();
	}

	public ModelGroup<A> getModelGroup(final QName name)
	{
		return m_cache.getModelGroup(name);
	}

	public Iterable<ModelGroup<A>> getModelGroups()
	{
		return m_cache.getModelGroups();
	}

	public QName getName(final SequenceType<A> type)
	{
		if (type instanceof Type<?>)
		{
			final Type<A> itemType = (Type<A>) type;
			return itemType.getName();
		}
		else if (type instanceof AttributeDefinition<?>)
		{
			final AttributeDefinition<A> attType = (AttributeDefinition<A>) type;
			return attType.getName();
		}
		else if (type instanceof AttributeNodeType<?>)
		{
			final AttributeNodeType<A> attributeNodeType = (AttributeNodeType<A>) type;
			return attributeNodeType.getName();
		}
		else if (type instanceof ElementNodeType<?>)
		{
			final ElementNodeType<A> elementNodeType = (ElementNodeType<A>) type;
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

	public NativeType getNearestBuiltInType(final SequenceType<A> arg)
	{
		if (arg instanceof SimpleType<?>)
		{
			final SimpleType<A> atomicType = (SimpleType<A>) arg;
			final QName name = atomicType.getName();
			return m_nameBridge.nativeType(name);
		}
		else
		{
			return null;
		}
	}

	public NotationDefinition<A> getNotationDeclaration(final QName name)
	{
		return m_cache.getNotationDeclaration(name);
	}

	public Iterable<NotationDefinition<A>> getNotations()
	{
		return m_cache.getNotations();
	}

	public SimpleType<A> getSimpleType(final QName name)
	{
		return m_cache.getSimpleType(name);
	}

	public SimpleType<A> getSimpleType(final NativeType name)
	{
		return m_cache.getSimpleType(name);
	}

	public Iterable<SimpleType<A>> getSimpleTypes()
	{
		return m_cache.getSimpleTypes();
	}

	public SimpleUrType<A> getSimpleUrType()
	{
		return ANY_SIMPLE_TYPE;
	}

	public Type<A> getTypeDefinition(final QName name)
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

	public Type<A> getTypeDefinition(final NativeType nativeType)
	{
		return m_cache.getTypeDefinition(nativeType);
	}

	public SequenceType<A> handle(SequenceType<A> sequenceType)
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

	public SequenceType<A> interleave(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		return ZInterleaveType.interleave(lhs, rhs);
	}

	public boolean isLocked()
	{
		return m_cache.isLocked();
	}

	public boolean isNative(final SequenceType<A> arg)
	{
		if (arg instanceof PrimeType<?>)
		{
			return arg.prime().isNative();
		}
		else
		{
			return false;
		}
	}

	public boolean isNone(final SequenceType<A> type)
	{
		return (type instanceof NoneType<?>);
	}

	public PrimeType<A> itemType()
	{
		return m_cache.item();
	}

	public void lock()
	{
		m_cache.lock();
	}

	private SequenceType<A> modelGroupUseType(final ModelGroupUse<A> modelGroupUse, final ElementDefinition<A> parentDecl)
	{
		final int minOccurs = modelGroupUse.getMinOccurs();
		final int maxOccurs = modelGroupUse.getMaxOccurs();
		final ModelGroup<A> modelGroup = modelGroupUse.getTerm();
		final ModelGroup.SmCompositor compositor = modelGroup.getCompositor();

		SequenceType<A> contentModel = null;
		for (final SchemaParticle<A> particle : modelGroup.getParticles())
		{
			final SequenceType<A> type = particle(particle, parentDecl);
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

	public SequenceType<A> multiply(final SequenceType<A> argument, final KeeneQuantifier multiplier)
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

	public NamespaceNodeType<A> namespaceType()
	{
		return m_cache.namespace();
	}

	public PrimeType<A> nodeType()
	{
		return m_cache.node();
	}

	public NoneType<A> noneType()
	{
		return new NoneTypeImpl<A>();
	}

	public NoneType<A> noneType(final QName errorCode)
	{
		return new NoneTypeImpl<A>(errorCode);
	}

	public SequenceType<A> oneOrMore(final SequenceType<A> type)
	{
		return multiply(type, KeeneQuantifier.ONE_OR_MORE);
	}

	public SequenceType<A> optional(final SequenceType<A> type)
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

	private SequenceType<A> particle(final SchemaParticle<A> particle, final ElementDefinition<A> parentDecl)
	{
		if (particle instanceof ElementUse<?>)
		{
			return elementUseType((ElementUse<A>) particle, parentDecl);
		}
		else if (particle instanceof ModelGroupUse<?>)
		{
			return modelGroupUseType((ModelGroupUse<A>) particle, parentDecl);
		}
		else if (particle instanceof WildcardUse<?>)
		{
			return wildcardUseType((WildcardUse<A>) particle, parentDecl);
		}
		else
		{
			// There shouldn't be anything else beside element, model group and wildcard.
			throw new AssertionError(particle);
		}
	}

	public ProcessingInstructionNodeType<A> processingInstructionType(final String name)
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

	public void register(final ComponentBag<A> components)
	{
		assertNotLocked();
		m_cache.register(components);
	}

	public boolean sameAs(final SequenceType<A> one, final SequenceType<A> two)
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");
		return subtype(one, two) && subtype(two, one);
	}

	public AttributeDefinition<A> schemaAttribute(final QName attributeName)
	{
		return m_cache.getAttributeDeclaration(attributeName);
	}

	public ElementDefinition<A> schemaElement(final QName elementName)
	{
		return m_cache.getElementDeclaration(elementName);
	}

	public SequenceType<A> schemaType(final QName typeName)
	{
		return m_cache.getTypeDefinition(typeName);
	}

	public boolean subtype(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		return SchemaSupport.subtype(lhs, rhs);
	}

	public TextNodeType<A> textType()
	{
		return TEXT;
	}

	@SuppressWarnings("unchecked")
	public SequenceType<A>[] typeArray(final int size)
	{
		return new SequenceType[size];
	}

	private SequenceType<A> wildcardUseType(final WildcardUse<A> wildcardUse, final ElementDefinition<A> parentDecl)
	{
		final int minOccurs = wildcardUse.getMinOccurs();
		final int maxOccurs = wildcardUse.getMaxOccurs();
		final SchemaWildcard<A> term = wildcardUse.getTerm();
		// final ProcessContentsMode processContents = term.getProcessContents();
		final NamespaceConstraint namespaceConstraint = term.getNamespaceConstraint();
		switch (namespaceConstraint.getMode())
		{
		case Any:
		{
			return multiply(new ElementNodeWithParentAxisType<A>(elementWild(null, true), parentDecl), KeeneQuantifier.approximate(minOccurs, maxOccurs));
		}
		case Include:
		{
			SequenceType<A> type = null;
			for (final String namespace : namespaceConstraint.getNamespaces())
			{
				final ElementNodeWithParentAxisType<A> append = 
				    new ElementNodeWithParentAxisType<A>(new ElementNodeTypeImpl<A>(new QName(namespace, null), null, true, m_cache), parentDecl);
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
			return multiply(new ElementNodeWithParentAxisType<A>(new ElementNodeTypeImpl<A>(WILDNAME, null, true, m_cache), parentDecl), KeeneQuantifier.approximate(minOccurs,
					maxOccurs));
		}
		default:
		{
			throw new AssertionError();
		}
		}
	}

	public SequenceType<A> zeroOrMore(final SequenceType<A> type)
	{
		return multiply(type, KeeneQuantifier.ZERO_OR_MORE);
	}
}
