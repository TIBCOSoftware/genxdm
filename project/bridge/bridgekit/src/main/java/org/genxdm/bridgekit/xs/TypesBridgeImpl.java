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
import org.genxdm.bridgekit.xs.complex.DocumentNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ElementDeclWithParentAxisType;
import org.genxdm.bridgekit.xs.complex.ElementNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ElementNodeWithParentAxisType;
import org.genxdm.bridgekit.xs.complex.NamespaceNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.NoneTypeImpl;
import org.genxdm.bridgekit.xs.complex.ProcessingInstructionNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.TextNodeTypeImpl;
import org.genxdm.bridgekit.xs.complex.ZChoiceType;
import org.genxdm.bridgekit.xs.complex.ZConcatType;
import org.genxdm.bridgekit.xs.complex.ZEmptyType;
import org.genxdm.bridgekit.xs.complex.ZInterleaveType;
import org.genxdm.bridgekit.xs.complex.ZMultiplyType;
import org.genxdm.bridgekit.xs.complex.ZPrimeChoiceType;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NamespaceResolver;
import org.genxdm.typed.types.MetaVisitor;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
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
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.ChoiceType;
import org.genxdm.xs.types.CommentNodeType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.MultiplyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.TextNodeType;
import org.genxdm.xs.types.Type;

public final class TypesBridgeImpl implements TypesBridge
{
    public TypesBridgeImpl(SchemaCache cache)
    {
        m_cache = PreCondition.assertNotNull(cache, "cache");

        ANY_ATOMIC_TYPE = m_cache.getAtomicUrType();

        ELEMENT = new ElementNodeTypeImpl(WILDNAME, null, false, m_cache);
        NAMESPACE = new NamespaceNodeTypeImpl();
        ATTRIBUTE = new AttributeNodeTypeImpl(WILDNAME, null, m_cache);
        COMMENT = new CommentNodeTypeImpl();
        PROCESSING_INSTRUCTION = new ProcessingInstructionNodeTypeImpl(null);
        TEXT = new TextNodeTypeImpl();
        final SequenceType X = ZMultiplyType.zeroOrMore(ZPrimeChoiceType.choice(ELEMENT, ZPrimeChoiceType.choice(TEXT, ZPrimeChoiceType.choice(COMMENT, PROCESSING_INSTRUCTION))));
        DOCUMENT = new DocumentNodeTypeImpl(X, m_cache);

        ANY_KIND = ZPrimeChoiceType.choice(ELEMENT, ZPrimeChoiceType.choice(ATTRIBUTE, ZPrimeChoiceType.choice(TEXT, ZPrimeChoiceType.choice(DOCUMENT, ZPrimeChoiceType.choice(COMMENT, ZPrimeChoiceType.choice(NAMESPACE, PROCESSING_INSTRUCTION))))));
        ANY_ITEM = ZPrimeChoiceType.choice(ANY_KIND, ANY_ATOMIC_TYPE);
    }

    public void accept(final SequenceType type, final MetaVisitor visitor)
    {
        if (type instanceof SimpleType)
        {
            final SimpleType simpleType = (SimpleType)type;
            if (simpleType.isAtomicType())
            {
                visitor.atomicType(simpleType, simpleType.getName(), simpleType.getBaseType());
            }
            else
            {
                throw new AssertionError("TODO");
            }
        }
        else if (type instanceof AttributeNodeType)
        {
            final AttributeNodeType attribute = (AttributeNodeType)type;
            visitor.attributeType(attribute, attribute.getName(), attribute.getType());
        }
        else if (type instanceof ChoiceType)
        {
            final ChoiceType choice = (ChoiceType)type;
            visitor.choiceType(choice, choice.getLHS(), choice.getRHS());
        }
        else if (type instanceof CommentNodeType)
        {
            final CommentNodeType comment = (CommentNodeType)type;
            visitor.textType(comment);
        }
        else if (type instanceof DocumentNodeType)
        {
            final DocumentNodeType document = (DocumentNodeType)type;
            visitor.documentType(document, document.getContentType());
        }
        else if (type instanceof ElementNodeType)
        {
            final ElementNodeType element = (ElementNodeType)type;
            visitor.elementType(element, element.getName(), element.getType(), element.isNillable());
        }
        else if (type instanceof EmptyType)
        {
            final EmptyType emptyType = (EmptyType)type;
            visitor.emptyType(emptyType);
        }
        else if (type instanceof MultiplyType)
        {
            final MultiplyType multiply = (MultiplyType)type;
            visitor.multiplyType(multiply, multiply.getArgument(), multiply.getMultiplier());
        }
        else if (type instanceof NamespaceNodeType)
        {
            final NamespaceNodeType namespace = (NamespaceNodeType)type;
            visitor.namespaceType(namespace);
        }
        else if (type instanceof NoneType)
        {
            final NoneType errorType = (NoneType)type;
            visitor.noneType(errorType);
        }
        else if (type instanceof ProcessingInstructionNodeType)
        {
            final ProcessingInstructionNodeType pi = (ProcessingInstructionNodeType)type;
            visitor.processingInstructionType(pi, pi.getName());
        }
        else if (type instanceof TextNodeType)
        {
            final TextNodeType text = (TextNodeType)type;
            visitor.textType(text);
        }
        else
        {
            throw new UnsupportedOperationException("accept(" + type.getClass().getName() + ")");
        }
    }

    public SequenceType ancestorAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)prime;
                return multiply(choice(ancestorAxis(choiceType.getLHS()), ancestorAxis(choiceType.getRHS())), type.quantifier());
            }
            case DOCUMENT:
            {
                return emptyType();
            }
            case ELEMENT:
            case SCHEMA_ELEMENT:
            case ATTRIBUTE:
            case SCHEMA_ATTRIBUTE:
            case COMMENT:
            case NAMESPACE:
            case PROCESSING_INSTRUCTION:
            case TEXT:
            {
                return multiply(choice(documentType(null), zeroOrMore(elementType(null, null, true))), type.quantifier());
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

    public SequenceType ancestorOrSelfAxis(final SequenceType contextType)
    {
        return zeroOrMore(nodeType());
    }

    public SequenceType atomSet(final SequenceType type)
    {
        if (type instanceof SimpleType)
        {
            return (SimpleType) type;
        }
        else
        {
            return zeroOrMore(m_cache.getTypeDefinition(NativeType.ANY_ATOMIC_TYPE));
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
                return attributeWild(m_cache.getTypeDefinition(NativeType.UNTYPED_ATOMIC));
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

    public AttributeNodeType attributeType(final QName name, final SequenceType type)
    {
        if (name != null)
            return new AttributeNodeTypeImpl(name, type, m_cache);
        return attributeWild(type);
    }

    @Override
    public AttributeNodeType attributeWild(SequenceType type)
    {
        return new AttributeNodeTypeImpl(WILDNAME, type, m_cache);
    }

    public SequenceType childAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType) prime;
                return multiply(choice(childAxis(choiceType.getLHS()), childAxis(choiceType.getRHS())), type.quantifier());
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

                    return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

                    return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
                }
            }
            case SCHEMA_ELEMENT:
            {
                final ElementDefinition elementDecl = (ElementDefinition) prime;
                final Type t = elementDecl.getType();
                if (t instanceof ComplexType)
                {
                    final ComplexType complexType = (ComplexType) t;
                    return childAxisFromComplexType(complexType, elementDecl);
                }
                else if (t instanceof SimpleType)
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

    public SequenceType choice(final SequenceType one, final SequenceType two)
    {
        return ZChoiceType.choice(one, two);
    }

    public SequenceType commentTest(final SequenceType arg)
    {
        final PrimeType primeType = arg.prime();
        switch (primeType.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)primeType;

                return multiply(choice(commentTest(choiceType.getLHS()), commentTest(choiceType.getRHS())), arg.quantifier());
            }
            case COMMENT:
            {
                return arg;
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

    public CommentNodeType commentType()
    {
        return COMMENT;
    }

    public SequenceType concat(final SequenceType lhs, final SequenceType rhs)
    {
        return ZConcatType.concat(lhs, rhs);
    }

    public SequenceType contentType(final SequenceType type)
    {
        if (type instanceof DocumentNodeType)
        {
            return ((DocumentNodeType)type).getContentType();
        }
        else
        {
            throw new AssertionError(type);
        }
    }

    @Override
    public void declareAttribute(AttributeDefinition attribute)
    {
        assertNotLocked();
        m_cache.declareAttribute(attribute);
    }

    @Override
    public void declareElement(ElementDefinition element)
    {
        assertNotLocked();
        m_cache.declareElement(element);
    }

    @Override
    public void declareNotation(NotationDefinition notation)
    {
        assertNotLocked();
        m_cache.declareNotation(notation);
    }

    @Override
    public void defineAttributeGroup(AttributeGroupDefinition attributeGroup)
    {
        assertNotLocked();
        m_cache.defineAttributeGroup(attributeGroup);
    }

    @Override
    public void defineComplexType(ComplexType complexType)
    {
        assertNotLocked();
        m_cache.defineComplexType(complexType);
    }

    @Override
    public void defineIdentityConstraint(IdentityConstraint identityConstraint)
    {
        assertNotLocked();
        m_cache.defineIdentityConstraint(identityConstraint);
    }

    @Override
    public void defineModelGroup(ModelGroup modelGroup)
    {
        assertNotLocked();
        m_cache.defineModelGroup(modelGroup);
    }

    @Override
    public void defineSimpleType(SimpleType simpleType)
    {
        assertNotLocked();
        m_cache.defineSimpleType(simpleType);
    }

    public SequenceType descendantAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)prime;
                return multiply(choice(descendantAxis(choiceType.getLHS()), descendantAxis(choiceType.getRHS())), type.quantifier());
            }
            case DOCUMENT:
            case ELEMENT:
            {
                final ElementNodeType elementType = elementWild(null, true);
                final TextNodeType textType = textType();
                final CommentNodeType commentType = commentType();
                final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

                return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
            }
            case SCHEMA_ELEMENT:
            {
                final ElementNodeType elementType = elementWild(null, true);
                final TextNodeType textType = textType();
                final CommentNodeType commentType = commentType();
                final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

                return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

    public SequenceType descendantOrSelfAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)prime;
                return multiply(choice(descendantOrSelfAxis(choiceType.getLHS()), descendantOrSelfAxis(choiceType.getRHS())), type.quantifier());
            }
            case ELEMENT:
            case SCHEMA_ELEMENT:
            case DOCUMENT:
            {
                final SequenceType kids = branchChildAxis();
                return multiply(choice(kids, prime), type.quantifier());
            }
            case ATTRIBUTE:
            case SCHEMA_ATTRIBUTE:
            case COMMENT:
            case NAMESPACE:
            case PROCESSING_INSTRUCTION:
            case TEXT:
            {
                return type;
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

    public DocumentNodeType documentType(final SequenceType contentType)
    {
        if (contentType != null)
            return new DocumentNodeTypeImpl(contentType, m_cache);
        return DOCUMENT;
    }

    public SequenceType elementTest(final SequenceType arg)
    {
        final PrimeType primeType = arg.prime();
        switch (primeType.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)primeType;

                return multiply(choice(elementTest(choiceType.getLHS()), elementTest(choiceType.getRHS())), arg.quantifier());
            }
            case ELEMENT:
            case SCHEMA_ELEMENT:
            {
                return arg;
            }
            default:
            {
                return emptyType();
            }
        }
    }

    public ElementNodeType elementType(final QName name, final SequenceType type, final boolean nillable)
    {
        if (name != null)
            return new ElementNodeTypeImpl(name, type, nillable, m_cache);
        return elementWild(type, nillable);
    }

    @Override
    public ElementNodeType elementWild(SequenceType type, boolean nillable)
    {
        return new ElementNodeTypeImpl(WILDNAME, type, nillable, m_cache);
    }

    public EmptyType emptyType()
    {
        return EMPTY;
    }

    public SequenceType followingAxis(final SequenceType contextType)
    {
        return zeroOrMore(nodeType());
    }

    public SequenceType followingSiblingAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)prime;
                return multiply(choice(followingSiblingAxis(choiceType.getLHS()), followingSiblingAxis(choiceType.getRHS())), type.quantifier());
            }
            case ATTRIBUTE:
            {
                return multiply(zeroOrMore(attributeType(null, m_cache.getTypeDefinition(NativeType.UNTYPED_ATOMIC))), type.quantifier());
            }
            case SCHEMA_ATTRIBUTE:
            {
                // TODO: This should come from the complex type, if it exists (otherwise empty).
                return multiply(zeroOrMore(attributeType(null, null)), type.quantifier());
            }
            case NAMESPACE:
            {
                return multiply(zeroOrMore(namespaceType()), type.quantifier());
            }
            case DOCUMENT:
            {
                return emptyType();
            }
            case ELEMENT:
            case SCHEMA_ELEMENT:
            case COMMENT:
            case PROCESSING_INSTRUCTION:
            case TEXT:
            {
                final ElementNodeType elementType = elementWild(null, true);
                final TextNodeType textType = textType();
                final CommentNodeType commentType = commentType();
                final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

                return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

    public SequenceType getBinaryLHS(final SequenceType type)
    {
        if (type instanceof ChoiceType)
        {
            final ChoiceType choice = (ChoiceType)type;
            return choice.getLHS();
        }
        else
        {
            throw new AssertionError("TODO: getBinaryLHS(" + type.getClass() + ")");
        }
    }

    public SequenceType getBinaryRHS(final SequenceType type)
    {
        if (type instanceof ChoiceType)
        {
            final ChoiceType choice = (ChoiceType)type;
            return choice.getRHS();
        }
        else
        {
            throw new AssertionError("TODO: getBinaryRHS(" + type.getClass() + ")");
        }
    }

    public QName getErrorCode(final SequenceType noneType)
    {
        if (noneType instanceof NoneType)
        {
            final NoneType error = (NoneType)noneType;
            return error.getErrorCode();
        }
        else
        {
            PreCondition.assertArgumentNotNull(noneType, "noneType");
            PreCondition.assertTrue(isNone(noneType), "isNone(noneType)");
            throw new AssertionError();
        }
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

    public Type getType(final QName typeName)
    {
        return m_cache.getTypeDefinition(typeName);
    }

    public Type getType(final NativeType nativeType)
    {
        return m_cache.getTypeDefinition(nativeType);
    }

    public SequenceType handle(final SequenceType sequenceType)
    {
        return sequenceType;
    }

    public SequenceType interleave(final SequenceType one, final SequenceType two)
    {
        return ZInterleaveType.interleave(one, two);
    }

    public boolean isAttributeNodeType(final SequenceType type)
    {
        return type instanceof AttributeNodeType;
    }

    public boolean isChoice(final SequenceType type)
    {
        return (type instanceof ChoiceType);
    }

    public boolean isCommentNodeType(final SequenceType type)
    {
        return type instanceof CommentNodeType;
    }

    public boolean isDocumentNodeType(final SequenceType type)
    {
        return type instanceof DocumentNodeType;
    }

    public boolean isElementNodeType(final SequenceType type)
    {
        return (type instanceof ElementNodeType);
    }

    public boolean isEmpty(final SequenceType type)
    {
        return type instanceof EmptyType;
    }

    public boolean isNamespaceNodeType(final SequenceType type)
    {
        return type instanceof NamespaceNodeType;
    }

    public boolean isNone(final SequenceType type)
    {
        return (type instanceof NoneType);
    }

    public boolean isProcessingInstructionNodeType(final SequenceType type)
    {
        return type instanceof ProcessingInstructionNodeType;
    }

    public boolean isTextNodeType(final SequenceType type)
    {
        return type instanceof TextNodeType;
    }

    public PrimeType itemType()
    {
        return ANY_ITEM;
    }

    public SequenceType multiply(final SequenceType argument, final Quantifier multiplier)
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

    public SequenceType namespaceAxis(final SequenceType contextType)
    {
        return zeroOrMore(nodeType());
    }

    public SequenceType namespaceTest(final SequenceType arg)
    {
        final PrimeType primeType = arg.prime();
        switch (primeType.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)primeType;

                return multiply(choice(namespaceTest(choiceType.getLHS()), namespaceTest(choiceType.getRHS())), arg.quantifier());
            }
            case NAMESPACE:
            {
                return arg;
            }
            default:
            {
                return emptyType();
            }
        }
    }

    public NamespaceNodeType namespaceType()
    {
        return NAMESPACE;
    }

    public SequenceType nodeTest(final SequenceType arg)
    {
        final PrimeType primeType = arg.prime();
        switch (primeType.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)primeType;

                return multiply(choice(nodeTest(choiceType.getLHS()), nodeTest(choiceType.getRHS())), arg.quantifier());
            }
            case ATTRIBUTE:
            case SCHEMA_ATTRIBUTE:
            case COMMENT:
            case DOCUMENT:
            case ELEMENT:
            case SCHEMA_ELEMENT:
            case NAMESPACE:
            case PROCESSING_INSTRUCTION:
            case TEXT:
            case EMPTY:
            case ANY_ATOMIC_TYPE:
            case ATOM:
            {
                return arg;
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

    public PrimeType nodeType()
    {
        return ANY_KIND;
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
        return multiply(type, Quantifier.ONE_OR_MORE);
    }

    public SequenceType optional(final SequenceType type)
    {
        if (null != type)
        {
            return multiply(type, Quantifier.OPTIONAL);
        }
        else
        {
            return null;
        }
    }

    public SequenceType parentAxis(final SequenceType contextType)
    {
        return optional(nodeType());
    }

    public SequenceType precedingAxis(final SequenceType contextType)
    {
        return zeroOrMore(nodeType());
    }

    public SequenceType precedingSiblingAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)prime;
                return multiply(choice(precedingSiblingAxis(choiceType.getLHS()), precedingSiblingAxis(choiceType.getRHS())), type.quantifier());
            }
            case ATTRIBUTE:
            {
                return multiply(zeroOrMore(attributeType(null, m_cache.getTypeDefinition(NativeType.UNTYPED_ATOMIC))), type.quantifier());
            }
            case SCHEMA_ATTRIBUTE:
            {
                // TODO: This should come from the complex type, if it exists (otherwise empty).
                return multiply(zeroOrMore(attributeType(null, null)), type.quantifier());
            }
            case NAMESPACE:
            {
                return multiply(zeroOrMore(namespaceType()), type.quantifier());
            }
            case DOCUMENT:
            {
                return emptyType();
            }
            case ELEMENT:
            case SCHEMA_ELEMENT:
            case COMMENT:
            case PROCESSING_INSTRUCTION:
            case TEXT:
            {
                final ElementNodeType elementType = elementWild(null, true);
                final TextNodeType textType = textType();
                final CommentNodeType commentType = commentType();
                final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

                return multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

    public PrimeType prime(final SequenceType type)
    {
        return type.prime();
    }

    public SequenceType processingInstructionTest(final SequenceType arg, final String name)
    {
        final PrimeType primeType = arg.prime();
        switch (primeType.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)primeType;
                return multiply(choice(processingInstructionTest(choiceType.getLHS(), name), processingInstructionTest(choiceType.getRHS(), name)), arg.quantifier());
            }
            case PROCESSING_INSTRUCTION:
            {
                return arg;
            }
            default:
            {
                return emptyType();
            }
        }
    }

    public ProcessingInstructionNodeType processingInstructionType(final String name)
    {
        if (name != null)
            return new ProcessingInstructionNodeTypeImpl(name);
        return PROCESSING_INSTRUCTION;
    }

    public Quantifier quantifier(final SequenceType type)
    {
        return type.quantifier();
    }

    public boolean sameAs(final SequenceType one, final SequenceType two)
    {
        PreCondition.assertArgumentNotNull(one, "one");
        PreCondition.assertArgumentNotNull(two, "two");
        return subtype(one, two) && subtype(two, one);
    }

    public SequenceType schemaAttribute(final QName attributeName)
    {
        return m_cache.getAttributeDeclaration(attributeName);
    }

    public SequenceType schemaElement(final QName elementName)
    {
        return m_cache.getElementDeclaration(elementName);
    }

    public SequenceType schemaType(final QName typeName)
    {
        return m_cache.getTypeDefinition(typeName);
    }

    public SequenceType selfAxis(final SequenceType type)
    {
        final PrimeType prime = type.prime();
        switch (prime.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)prime;
                return multiply(choice(selfAxis(choiceType.getLHS()), selfAxis(choiceType.getRHS())), type.quantifier());
            }
            case EMPTY:
            case ATOM:
            case ANY_ATOMIC_TYPE:
            {
                return emptyType();
            }
            case NONE:
            {
                return noneType();
            }
            default:
            {
                return type;
            }
        }
    }

    public boolean subtype(final SequenceType lhs, final SequenceType rhs)
    {
        PreCondition.assertArgumentNotNull(lhs, "lhs");
        PreCondition.assertArgumentNotNull(rhs, "rhs");
        return SchemaSupport.subtype(lhs, rhs);
    }

    public SequenceType textTest(final SequenceType arg)
    {
        final PrimeType primeType = arg.prime();
        switch (primeType.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)primeType;
                return multiply(choice(textTest(choiceType.getLHS()), textTest(choiceType.getRHS())), arg.quantifier());
            }
            case TEXT:
            {
                return arg;
            }
            case NONE:
            {
                return arg;
            }
            default:
            {
                return emptyType();
            }
        }
    }

    public TextNodeType textType()
    {
        return TEXT;
    }

    public String toString(final SequenceType type, final NamespaceResolver mappings, final String defaultElementAndTypeNamespace) throws GxmlException
    {
        return type.toString();
    }

    public SequenceType[] typeArray(final int size)
    {
        return new SequenceType[size];
    }

    public SequenceType zeroOrMore(final SequenceType type)
    {
        return multiply(type, Quantifier.ZERO_OR_MORE);
    }

    @Override
    public ComponentProvider getComponentProvider()
    {
        return m_cache.getComponentProvider();
    }

    @Override
    public ComponentBag getComponents()
    {
        return m_cache.getComponents();
    }

    @Override
    public Iterable<String> getNamespaces()
    {
        return m_cache.getNamespaces();
    }

    @Override
    public void register(ComponentBag components)
    {
        assertNotLocked();
        m_cache.register(components);
    }

    private void assertNotLocked()
    {
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

    private SequenceType attributeUseType(final AttributeUse attributeUse, final ElementDefinition parentAxis)
    {
        final AttributeDefinition attribute = attributeUse.getAttribute();
        if (null != parentAxis)
        {
            return multiply(new AttributeDeclWithParentAxisType(attribute, parentAxis), attributeUse.isRequired() ? Quantifier.EXACTLY_ONE : Quantifier.OPTIONAL);
        }
        else
        {
            return multiply(attribute, attributeUse.isRequired() ? Quantifier.EXACTLY_ONE : Quantifier.OPTIONAL);
        }
    }

    private SequenceType branchChildAxis()
    {
        final ElementNodeType elementType = elementWild(null, true);
        final TextNodeType textType = textType();
        final CommentNodeType commentType = commentType();
        final ProcessingInstructionNodeType processingInstructionType = processingInstructionType(null);

        return zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType))));
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

    private SequenceType elementUseType(final ElementUse elementUse, final ElementDefinition parentDecl)
    {
        final int minOccurs = elementUse.getMinOccurs();
        final int maxOccurs = elementUse.getMaxOccurs();
        final ElementDefinition elementDecl = elementUse.getTerm();
        if (null != parentDecl)
        {
            return multiply(new ElementDeclWithParentAxisType(elementDecl, parentDecl), Quantifier.approximate(minOccurs, maxOccurs));
        }
        else
        {
            return multiply(elementDecl, Quantifier.approximate(minOccurs, maxOccurs));
        }
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
            return multiply(contentModel, Quantifier.approximate(minOccurs, maxOccurs));
        }
        else
        {
            return emptyType();
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
            return multiply(new ElementNodeWithParentAxisType(elementWild(null, true), parentDecl), Quantifier.approximate(minOccurs, maxOccurs));
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
            return multiply(type, Quantifier.approximate(minOccurs, maxOccurs));
        }
        case Exclude:
        {
            // TODO: How do we define a regular expression type that excludes certain namespaces?
            // TODO: We don't even have the concept of AND.
            return multiply(new ElementNodeWithParentAxisType(new ElementNodeTypeImpl(WILDNAME, null, true, m_cache), parentDecl), Quantifier.approximate(minOccurs,
                    maxOccurs));
        }
        default:
        {
            throw new AssertionError();
        }
        }
    }
    private final PrimeType ANY_KIND;
    private final PrimeType ANY_ITEM;
    private final AtomicUrType ANY_ATOMIC_TYPE;
    private final EmptyType EMPTY = new ZEmptyType();

    private final DocumentNodeType DOCUMENT;
    private final ElementNodeType ELEMENT;
    private final CommentNodeType COMMENT;
    private final ProcessingInstructionNodeType PROCESSING_INSTRUCTION;
    private final TextNodeType TEXT;

    private final AttributeNodeType ATTRIBUTE;
    private final NamespaceNodeType NAMESPACE;

    
    private final ConcurrentHashMap<Type, ArrayList<AttributeUse>> m_attributeUses = new ConcurrentHashMap<Type, ArrayList<AttributeUse>>();

    private final SchemaCache m_cache;

    private static final String ESCAPE = "\u001B";
    private final QName WILDNAME = new QName(ESCAPE, ESCAPE);
}
