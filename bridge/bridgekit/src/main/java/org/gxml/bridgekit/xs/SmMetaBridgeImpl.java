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
package org.gxml.bridgekit.xs;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.SmMetaBridge;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.components.SmWildcard;
import org.genxdm.xs.components.SmWildcardUse;
import org.genxdm.xs.constraints.SmAttributeUse;
import org.genxdm.xs.constraints.SmElementUse;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmModelGroupUse;
import org.genxdm.xs.constraints.SmNamespaceConstraint;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmAtomicUrType;
import org.genxdm.xs.types.SmAttributeNodeType;
import org.genxdm.xs.types.SmCommentNodeType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmDocumentNodeType;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmEmptyType;
import org.genxdm.xs.types.SmNamespaceNodeType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmNoneType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmProcessingInstructionNodeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmTextNodeType;
import org.genxdm.xs.types.SmType;

final class SmMetaBridgeImpl<A> implements SmMetaBridge<A>
{
	private final SmAtomicUrType<A> ANY_ATOMIC_TYPE;
	private final SmComplexUrType<A> ANY_COMPLEX_TYPE;
	private final SmSimpleUrType<A> ANY_SIMPLE_TYPE;

	// private final SmDocumentNodeType<A> DOCUMENT;
	// private final SmElementNodeType<A> ELEMENT;
	private final SmCommentNodeType<A> COMMENT;

	private final ConcurrentHashMap<SmType<A>, ArrayList<SmAttributeUse<A>>> m_attributeUses = new ConcurrentHashMap<SmType<A>, ArrayList<SmAttributeUse<A>>>();

	private final SmCacheImpl<A> m_cache;
	private final NameSource m_nameBridge;

	private final SmProcessingInstructionNodeType<A> PROCESSING_INSTRUCTION;
	private final SmTextNodeType<A> TEXT;
    private static final String ESCAPE = "\u001B";
	private final QName WILDNAME = new QName(ESCAPE, ESCAPE);

	public SmMetaBridgeImpl(final AtomBridge<A> atomBridge)
	{
		m_cache = new SmCacheImpl<A>(PreCondition.assertArgumentNotNull(atomBridge, "atomBridge"));
		m_nameBridge = atomBridge.getNameBridge();

		ANY_COMPLEX_TYPE = m_cache.getComplexUrType();
		ANY_SIMPLE_TYPE = m_cache.getSimpleUrType();
		ANY_ATOMIC_TYPE = m_cache.getAtomicUrType();
		// ELEMENT = new ElementNodeType<A>(WILDNAME, null, false, m_cache);
		COMMENT = new CommentNodeType<A>(m_cache);
		PROCESSING_INSTRUCTION = new ProcessingInstructionNodeType<A>(null, m_cache);
		TEXT = new TextNodeType<A>(m_cache);
	}

	private void assertNotLocked()
	{
		PreCondition.assertFalse(m_cache.isLocked());
	}

	public SmSequenceType<A> atomSet(final SmSequenceType<A> type)
	{
		if (type instanceof SmSimpleType<?>)
		{
			return (SmSimpleType<A>) type;
		}
		else
		{
			return zeroOrMore(getTypeDefinition(SmNativeType.ANY_ATOMIC_TYPE));
			// throw new AssertionError("TODO: atomSet(" + type.getClass() + ")");
		}
	}

	@SuppressWarnings("unchecked")
	public SmSequenceType<A> attributeAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
		case CHOICE:
		{
			final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>) prime;
			return multiply(choice(attributeAxis(choiceType.getLHS()), attributeAxis(choiceType.getRHS())), type.quantifier());
		}
		case ELEMENT:
		{
			return attributeWild(getTypeDefinition(SmNativeType.UNTYPED_ATOMIC));
		}
		case SCHEMA_ELEMENT:
		{
			final SmElement<A> elementDecl = (SmElement<A>) prime;
			final SmType<A> smType = elementDecl.getType();
			if (smType instanceof SmComplexType)
			{
				final SmComplexType<A> complexType = (SmComplexType<A>) smType;
				return attributeAxisFromComplexType(complexType, elementDecl);
			}
			else if (smType instanceof SmSimpleType)
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
			final SmComplexType<A> complexType = (SmComplexType<A>) prime;
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

	private SmSequenceType<A> attributeAxisFromComplexType(final SmComplexType<A> complexType, final SmElement<A> parentAxis)
	{
		final ArrayList<SmAttributeUse<A>> attributeUses = ensureAttributeUses(complexType);
		SmSequenceType<A> result = null;
		for (final SmAttributeUse<A> attributeUse : attributeUses)
		{
			final SmSequenceType<A> attributeType = attributeUseType(attributeUse, parentAxis);
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

	public SmAttributeNodeType<A> attributeType(final QName name, final SmSequenceType<A> type)
	{
		if (null != name)
		{
			return new AttributeNodeType<A>(name, type, m_cache);
		}
		else
		{
			return attributeWild(type);
		}
	}

	private SmSequenceType<A> attributeUseType(final SmAttributeUse<A> attributeUse, final SmElement<A> parentAxis)
	{
		final SmAttribute<A> attribute = attributeUse.getAttribute();
		if (null != parentAxis)
		{
			return multiply(new AttributeDeclWithParentAxisType<A>(attribute, parentAxis), attributeUse.isRequired() ? SmQuantifier.EXACTLY_ONE : SmQuantifier.OPTIONAL);
		}
		else
		{
			return multiply(attribute, attributeUse.isRequired() ? SmQuantifier.EXACTLY_ONE : SmQuantifier.OPTIONAL);
		}
	}

	public SmAttributeNodeType<A> attributeWild(final SmSequenceType<A> type)
	{
		return new AttributeNodeType<A>(WILDNAME, type, m_cache);
	}

	@SuppressWarnings("unchecked")
	public SmSequenceType<A> childAxis(final SmSequenceType<A> focus)
	{
		final SmPrimeType<A> prime = focus.prime();
		switch (prime.getKind())
		{
		case CHOICE:
		{
			final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>) prime;
			return multiply(choice(childAxis(choiceType.getLHS()), childAxis(choiceType.getRHS())), focus.quantifier());
		}
		case DOCUMENT:
		{
			final SmDocumentNodeType<A> documentNodeType = (SmDocumentNodeType<A>) prime;
			final SmSequenceType<A> contentType = documentNodeType.getContentType();
			if (null != contentType)
			{
				return contentType;
			}
			else
			{
				final SmElementNodeType<A> elementType = elementWild(null, true);
				final SmTextNodeType<A> textType = textType();
				final SmCommentNodeType<A> commentType = commentType();
				final SmProcessingInstructionNodeType<A> processingInstructionType = processingInstructionType(null);

				return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), focus.quantifier());
			}
		}
		case ELEMENT:
		{
			final SmElementNodeType<A> element = (SmElementNodeType<A>) prime;
			final SmSequenceType<A> dataType = element.getType();
			if (subtype(dataType, zeroOrMore(nodeType())))
			{
				return dataType;
			}
			else
			{
				final SmPrimeType<A> elementType = elementWild(null, true);
				final SmTextNodeType<A> textType = textType();
				final SmCommentNodeType<A> commentType = commentType();
				final SmProcessingInstructionNodeType<A> processingInstructionType = processingInstructionType(null);

				return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), focus.quantifier());
			}
		}
		case SCHEMA_ELEMENT:
		{
			final SmElement<A> elementDecl = (SmElement<A>) prime;
			final SmType<A> type = elementDecl.getType();
			if (type instanceof SmComplexType)
			{
				final SmComplexType<A> complexType = (SmComplexType<A>) type;
				return childAxisFromComplexType(complexType, elementDecl);
			}
			else if (type instanceof SmSimpleType)
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
			final SmComplexType<A> complexType = (SmComplexType<A>) prime;
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
	private SmSequenceType<A> childAxisFromComplexType(final SmComplexType<A> complexType, final SmElement<A> parentDecl)
	{
		final SmContentType<A> contentType = complexType.getContentType();
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

	public SmSequenceType<A> choice(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		return ZChoiceType.choice(lhs, rhs);
	}

	public SmCommentNodeType<A> commentType()
	{
		return COMMENT;
	}

	public SmSequenceType<A> concat(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		return ZConcatType.concat(lhs, rhs);
	}

	public void declareAttribute(final SmAttribute<A> attribute)
	{
		assertNotLocked();
		m_cache.declareAttribute(attribute);
	}

	public void declareElement(final SmElement<A> element)
	{
		assertNotLocked();
		m_cache.declareElement(element);
	}

	public void declareNotation(final SmNotation<A> notation)
	{
		assertNotLocked();
		m_cache.declareNotation(notation);
	}

	public void defineAttributeGroup(final SmAttributeGroup<A> attributeGroup)
	{
		assertNotLocked();
		m_cache.defineAttributeGroup(attributeGroup);
	}

	public void defineComplexType(final SmComplexType<A> complexType)
	{
		assertNotLocked();
		m_cache.defineComplexType(complexType);
	}

	public void defineIdentityConstraint(final SmIdentityConstraint<A> identityConstraint)
	{
		assertNotLocked();
		m_cache.defineIdentityConstraint(identityConstraint);
	}

	public void defineModelGroup(final SmModelGroup<A> modelGroup)
	{
		assertNotLocked();
		m_cache.defineModelGroup(modelGroup);
	}

	public void defineSimpleType(final SmSimpleType<A> simpleType)
	{
		assertNotLocked();
		m_cache.defineSimpleType(simpleType);
	}

	public SmDocumentNodeType<A> documentType(final SmSequenceType<A> contentType)
	{
		return m_cache.documentType(contentType);
	}

	public SmElementNodeType<A> elementType(final QName name, final SmSequenceType<A> type, final boolean nillable)
	{
		if (null != name)
		{
			return new ElementNodeType<A>(name, type, nillable, m_cache);
		}
		else
		{
			return elementWild(type, nillable);
		}
	}

	private SmSequenceType<A> elementUseType(final SmElementUse<A> elementUse, final SmElement<A> parentDecl)
	{
		final int minOccurs = elementUse.getMinOccurs();
		final int maxOccurs = elementUse.getMaxOccurs();
		final SmElement<A> elementDecl = elementUse.getTerm();
		if (null != parentDecl)
		{
			return multiply(new ElementDeclWithParentAxisType<A>(elementDecl, parentDecl), SmQuantifier.approximate(minOccurs, maxOccurs));
		}
		else
		{
			return multiply(elementDecl, SmQuantifier.approximate(minOccurs, maxOccurs));
		}
	}

	public SmElementNodeType<A> elementWild(final SmSequenceType<A> type, final boolean nillable)
	{
		return new ElementNodeType<A>(WILDNAME, type, nillable, m_cache);
	}

	public SmEmptyType<A> emptyType()
	{
		return m_cache.empty();
	}

	private ArrayList<SmAttributeUse<A>> ensureAttributeUses(final SmComplexType<A> complexType)
	{
		final ArrayList<SmAttributeUse<A>> cachedAttributeUses = m_attributeUses.get(complexType);
		if (null != cachedAttributeUses)
		{
			return cachedAttributeUses;
		}
		else
		{
			final ArrayList<SmAttributeUse<A>> attributeUses = new ArrayList<SmAttributeUse<A>>();
			for (final SmAttributeUse<A> attributeUse : complexType.getAttributeUses().values())
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

	public SmAtomicType<A> getAtomicType(final QName name)
	{
		return m_cache.getAtomicType(name);
	}

	public SmAtomicType<A> getAtomicType(final SmNativeType name)
	{
		return m_cache.getAtomicType(name);
	}

	public SmAtomicUrType<A> getAtomicUrType()
	{
		return ANY_ATOMIC_TYPE;
	}

	public SmAttribute<A> getAttributeDeclaration(final QName attributeName)
	{
		return m_cache.getAttributeDeclaration(attributeName);
	}

	public SmAttributeGroup<A> getAttributeGroup(final QName name)
	{
		return m_cache.getAttributeGroup(name);
	}

	public Iterable<SmAttributeGroup<A>> getAttributeGroups()
	{
		return m_cache.getAttributeGroups();
	}

	public Iterable<SmAttribute<A>> getAttributes()
	{
		return m_cache.getAttributes();
	}

	public SmComplexType<A> getComplexType(final QName name)
	{
		return m_cache.getComplexType(name);
	}

	public Iterable<SmComplexType<A>> getComplexTypes()
	{
		return m_cache.getComplexTypes();
	}

	public SmComplexUrType<A> getComplexUrType()
	{
		return ANY_COMPLEX_TYPE;
	}

	public SmElement<A> getElementDeclaration(final QName elementName)
	{
		return m_cache.getElementDeclaration(elementName);
	}

	public Iterable<SmElement<A>> getElements()
	{
		return m_cache.getElements();
	}

	public SmIdentityConstraint<A> getIdentityConstraint(final QName name)
	{
		return m_cache.getIdentityConstraint(name);
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		return m_cache.getIdentityConstraints();
	}

	public SmModelGroup<A> getModelGroup(final QName name)
	{
		return m_cache.getModelGroup(name);
	}

	public Iterable<SmModelGroup<A>> getModelGroups()
	{
		return m_cache.getModelGroups();
	}

	public QName getName(final SmSequenceType<A> type)
	{
		if (type instanceof SmType<?>)
		{
			final SmType<A> itemType = (SmType<A>) type;
			return itemType.getName();
		}
		else if (type instanceof SmAttribute<?>)
		{
			final SmAttribute<A> attType = (SmAttribute<A>) type;
			return attType.getName();
		}
		else if (type instanceof SmAttributeNodeType<?>)
		{
			final SmAttributeNodeType<A> attributeNodeType = (SmAttributeNodeType<A>) type;
			return attributeNodeType.getName();
		}
		else if (type instanceof SmElementNodeType<?>)
		{
			final SmElementNodeType<A> elementNodeType = (SmElementNodeType<A>) type;
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

	public SmNativeType getNearestBuiltInType(final SmSequenceType<A> arg)
	{
		if (arg instanceof SmSimpleType<?>)
		{
			final SmSimpleType<A> atomicType = (SmSimpleType<A>) arg;
			final QName name = atomicType.getName();
			return m_nameBridge.nativeType(name);
		}
		else
		{
			return null;
		}
	}

	public SmNotation<A> getNotationDeclaration(final QName name)
	{
		return m_cache.getNotationDeclaration(name);
	}

	public Iterable<SmNotation<A>> getNotations()
	{
		return m_cache.getNotations();
	}

	public SmSimpleType<A> getSimpleType(final QName name)
	{
		return m_cache.getSimpleType(name);
	}

	public SmSimpleType<A> getSimpleType(final SmNativeType name)
	{
		return m_cache.getSimpleType(name);
	}

	public Iterable<SmSimpleType<A>> getSimpleTypes()
	{
		return m_cache.getSimpleTypes();
	}

	public SmSimpleUrType<A> getSimpleUrType()
	{
		return ANY_SIMPLE_TYPE;
	}

	public SmType<A> getTypeDefinition(final QName name)
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

	public SmType<A> getTypeDefinition(final SmNativeType nativeType)
	{
		return m_cache.getTypeDefinition(nativeType);
	}

	public SmSequenceType<A> handle(SmSequenceType<A> sequenceType)
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

	public SmSequenceType<A> interleave(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		return ZInterleaveType.interleave(lhs, rhs);
	}

	public boolean isLocked()
	{
		return m_cache.isLocked();
	}

	public boolean isNative(final SmSequenceType<A> arg)
	{
		if (arg instanceof SmPrimeType<?>)
		{
			return arg.prime().isNative();
		}
		else
		{
			return false;
		}
	}

	public boolean isNone(final SmSequenceType<A> type)
	{
		return (type instanceof SmNoneType<?>);
	}

	public SmPrimeType<A> itemType()
	{
		return m_cache.item();
	}

	public void lock()
	{
		m_cache.lock();
	}

	private SmSequenceType<A> modelGroupUseType(final SmModelGroupUse<A> modelGroupUse, final SmElement<A> parentDecl)
	{
		final int minOccurs = modelGroupUse.getMinOccurs();
		final int maxOccurs = modelGroupUse.getMaxOccurs();
		final SmModelGroup<A> modelGroup = modelGroupUse.getTerm();
		final SmModelGroup.SmCompositor compositor = modelGroup.getCompositor();

		SmSequenceType<A> contentModel = null;
		for (final SmParticle<A> particle : modelGroup.getParticles())
		{
			final SmSequenceType<A> type = particle(particle, parentDecl);
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
			return multiply(contentModel, SmQuantifier.approximate(minOccurs, maxOccurs));
		}
		else
		{
			return emptyType();
		}
	}

	public SmSequenceType<A> multiply(final SmSequenceType<A> argument, final SmQuantifier multiplier)
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

	public SmNamespaceNodeType<A> namespaceType()
	{
		return m_cache.namespace();
	}

	public SmPrimeType<A> nodeType()
	{
		return m_cache.node();
	}

	public SmNoneType<A> noneType()
	{
		return new NoneType<A>();
	}

	public SmNoneType<A> noneType(final QName errorCode)
	{
		return new NoneType<A>(errorCode);
	}

	public SmSequenceType<A> oneOrMore(final SmSequenceType<A> type)
	{
		return multiply(type, SmQuantifier.ONE_OR_MORE);
	}

	public SmSequenceType<A> optional(final SmSequenceType<A> type)
	{
		if (null != type)
		{
			return multiply(type, SmQuantifier.OPTIONAL);
		}
		else
		{
			return null;
		}
	}

	private SmSequenceType<A> particle(final SmParticle<A> particle, final SmElement<A> parentDecl)
	{
		if (particle instanceof SmElementUse<?>)
		{
			return elementUseType((SmElementUse<A>) particle, parentDecl);
		}
		else if (particle instanceof SmModelGroupUse<?>)
		{
			return modelGroupUseType((SmModelGroupUse<A>) particle, parentDecl);
		}
		else if (particle instanceof SmWildcardUse<?>)
		{
			return wildcardUseType((SmWildcardUse<A>) particle, parentDecl);
		}
		else
		{
			// There shouldn't be anything else beside element, model group and wildcard.
			throw new AssertionError(particle);
		}
	}

	public SmProcessingInstructionNodeType<A> processingInstructionType(final String name)
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

	public void register(final SmComponentBag<A> components)
	{
		assertNotLocked();
		m_cache.register(components);
	}

	public boolean sameAs(final SmSequenceType<A> one, final SmSequenceType<A> two)
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");
		return subtype(one, two) && subtype(two, one);
	}

	public SmAttribute<A> schemaAttribute(final QName attributeName)
	{
		return m_cache.getAttributeDeclaration(attributeName);
	}

	public SmElement<A> schemaElement(final QName elementName)
	{
		return m_cache.getElementDeclaration(elementName);
	}

	public SmSequenceType<A> schemaType(final QName typeName)
	{
		return m_cache.getTypeDefinition(typeName);
	}

	public boolean subtype(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		return SmSupportImpl.subtype(lhs, rhs);
	}

	public SmTextNodeType<A> textType()
	{
		return TEXT;
	}

	@SuppressWarnings("unchecked")
	public SmSequenceType<A>[] typeArray(final int size)
	{
		return new SmSequenceType[size];
	}

	private SmSequenceType<A> wildcardUseType(final SmWildcardUse<A> wildcardUse, final SmElement<A> parentDecl)
	{
		final int minOccurs = wildcardUse.getMinOccurs();
		final int maxOccurs = wildcardUse.getMaxOccurs();
		final SmWildcard<A> term = wildcardUse.getTerm();
		// final ProcessContentsMode processContents = term.getProcessContents();
		final SmNamespaceConstraint namespaceConstraint = term.getNamespaceConstraint();
		switch (namespaceConstraint.getMode())
		{
		case Any:
		{
			return multiply(new ElementNodeWithParentAxisType<A>(elementWild(null, true), parentDecl), SmQuantifier.approximate(minOccurs, maxOccurs));
		}
		case Include:
		{
			SmSequenceType<A> type = null;
			for (final String namespace : namespaceConstraint.getNamespaces())
			{
				final ElementNodeWithParentAxisType<A> append = 
				    new ElementNodeWithParentAxisType<A>(new ElementNodeType<A>(new QName(namespace, null), null, true, m_cache), parentDecl);
				if (null != type)
				{
					type = choice(type, append);
				}
				else
				{
					type = append;
				}
			}
			return multiply(type, SmQuantifier.approximate(minOccurs, maxOccurs));
		}
		case Exclude:
		{
			// TODO: How do we define a regular expression type that excludes certain namespaces?
			// TODO: We don't even have the concept of AND.
			return multiply(new ElementNodeWithParentAxisType<A>(new ElementNodeType<A>(WILDNAME, null, true, m_cache), parentDecl), SmQuantifier.approximate(minOccurs,
					maxOccurs));
		}
		default:
		{
			throw new AssertionError();
		}
		}
	}

	public SmSequenceType<A> zeroOrMore(final SmSequenceType<A> type)
	{
		return multiply(type, SmQuantifier.ZERO_OR_MORE);
	}
}
