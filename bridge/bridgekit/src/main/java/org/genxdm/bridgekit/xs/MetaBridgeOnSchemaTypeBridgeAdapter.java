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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.typed.types.MetaVisitor;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.SchemaTypeBridge;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.ChoiceType;
import org.genxdm.xs.types.CommentNodeType;
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

public final class MetaBridgeOnSchemaTypeBridgeAdapter<A> implements MetaBridge<A>
{
	private static KeeneQuantifier convert(final Quantifier gq)
	{
		switch (gq)
		{
			case NONE:
			{
				return KeeneQuantifier.NONE;
			}
			case EMPTY:
			{
				return KeeneQuantifier.EMPTY;
			}
			case EXACTLY_ONE:
			{
				return KeeneQuantifier.EXACTLY_ONE;
			}
			case OPTIONAL:
			{
				return KeeneQuantifier.OPTIONAL;
			}
			case ONE_OR_MORE:
			{
				return KeeneQuantifier.ONE_OR_MORE;
			}
			case ZERO_OR_MORE:
			{
				return KeeneQuantifier.ZERO_OR_MORE;
			}
			default:
			{
				throw new AssertionError(gq);
			}
		}
	}

	private static Quantifier convert(final KeeneQuantifier sq)
	{
		switch (sq)
		{
			case NONE:
			{
				return Quantifier.NONE;
			}
			case EMPTY:
			{
				return Quantifier.EMPTY;
			}
			case EXACTLY_ONE:
			{
				return Quantifier.EXACTLY_ONE;
			}
			case OPTIONAL:
			{
				return Quantifier.OPTIONAL;
			}
			case ONE_OR_MORE:
			{
				return Quantifier.ONE_OR_MORE;
			}
			case ZERO_OR_MORE:
			{
				return Quantifier.ZERO_OR_MORE;
			}
			default:
			{
				throw new AssertionError(sq);
			}
		}
	}

	private final AtomBridge<A> atomBridge;

	private final SchemaTypeBridge<A> metaBridge;

	public MetaBridgeOnSchemaTypeBridgeAdapter(final SchemaTypeBridge<A> metaBridge, final AtomBridge<A> atomBridge)
	{
		this.metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
	}

	public void accept(final SequenceType<A> type, final MetaVisitor<A> visitor)
	{
		if (type instanceof SimpleType<?>)
		{
			final SimpleType<A> simpleType = (SimpleType<A>)type;
			if (simpleType.isAtomicType())
			{
				visitor.atomicType(simpleType, simpleType.getName(), simpleType.getBaseType());
			}
			else
			{
				throw new AssertionError("TODO");
			}
		}
		else if (type instanceof AttributeNodeType<?>)
		{
			final AttributeNodeType<A> attribute = (AttributeNodeType<A>)type;
			visitor.attributeType(attribute, attribute.getName(), attribute.getType());
		}
		else if (type instanceof ChoiceType<?>)
		{
			final ChoiceType<A> choice = (ChoiceType<A>)type;
			visitor.choiceType(choice, choice.getLHS(), choice.getRHS());
		}
		else if (type instanceof CommentNodeType<?>)
		{
			final CommentNodeType<A> comment = (CommentNodeType<A>)type;
			visitor.textType(comment);
		}
		else if (type instanceof DocumentNodeType<?>)
		{
			final DocumentNodeType<A> document = (DocumentNodeType<A>)type;
			visitor.documentType(document, document.getContentType());
		}
		else if (type instanceof ElementNodeType<?>)
		{
			final ElementNodeType<A> element = (ElementNodeType<A>)type;
			visitor.elementType(element, element.getName(), element.getType(), element.isNillable());
		}
		else if (type instanceof EmptyType<?>)
		{
			final EmptyType<A> emptyType = (EmptyType<A>)type;
			visitor.emptyType(emptyType);
		}
		else if (type instanceof MultiplyType<?>)
		{
			final MultiplyType<A> multiply = (MultiplyType<A>)type;
			visitor.multiplyType(multiply, multiply.getArgument(), convert(multiply.getMultiplier()));
		}
		else if (type instanceof NamespaceNodeType<?>)
		{
			final NamespaceNodeType<A> namespace = (NamespaceNodeType<A>)type;
			visitor.namespaceType(namespace);
		}
		else if (type instanceof NoneType<?>)
		{
			final NoneType<A> errorType = (NoneType<A>)type;
			visitor.noneType(errorType);
		}
		else if (type instanceof ProcessingInstructionNodeType<?>)
		{
			final ProcessingInstructionNodeType<A> pi = (ProcessingInstructionNodeType<A>)type;
			visitor.processingInstructionType(pi, pi.getName());
		}
		else if (type instanceof TextNodeType<?>)
		{
			final TextNodeType<A> text = (TextNodeType<A>)type;
			visitor.textType(text);
		}
		else
		{
			throw new UnsupportedOperationException("accept(" + type.getClass().getName() + ")");
		}
	}

	public SequenceType<A> ancestorAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(ancestorAxis(choiceType.getLHS()), ancestorAxis(choiceType.getRHS())), type.quantifier());
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
				return metaBridge.multiply(metaBridge.choice(metaBridge.documentType(null), metaBridge.zeroOrMore(metaBridge.elementType(null, null, true))), type.quantifier());
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

	public SequenceType<A> ancestorOrSelfAxis(final SequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SequenceType<A> atomSet(final SequenceType<A> type)
	{
		return metaBridge.atomSet(type);
	}

	public SequenceType<A> attributeAxis(final SequenceType<A> type)
	{
		return metaBridge.attributeAxis(type);
	}

	public SequenceType<A> attributeType(final QName name, final SequenceType<A> type)
	{
		return metaBridge.attributeType(name, type);
	}

	private SequenceType<A> branchChildAxis()
	{
		final ElementNodeType<A> elementType = metaBridge.elementWild(null, true);
		final TextNodeType<A> textType = metaBridge.textType();
		final CommentNodeType<A> commentType = metaBridge.commentType();
		final ProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

		return zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType))));
	}

	public SequenceType<A> childAxis(final SequenceType<A> type)
	{
		return metaBridge.childAxis(type);
	}

	public SequenceType<A> choice(final SequenceType<A> one, final SequenceType<A> two)
	{
		return metaBridge.choice(one, two);
	}

	public SequenceType<A> commentTest(final SequenceType<A> arg)
	{
		final PrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)primeType;

				return metaBridge.multiply(choice(commentTest(choiceType.getLHS()), commentTest(choiceType.getRHS())), arg.quantifier());
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

	public CommentNodeType<A> commentType()
	{
		return metaBridge.commentType();
	}

	public SequenceType<A> concat(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		return metaBridge.concat(lhs, rhs);
	}

	public SequenceType<A> contentType(final SequenceType<A> type)
	{
		if (type instanceof DocumentNodeType<?>)
		{
			return ((DocumentNodeType<A>)type).getContentType();
		}
		else
		{
			throw new AssertionError(type);
		}
	}

	public SequenceType<A> descendantAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(descendantAxis(choiceType.getLHS()), descendantAxis(choiceType.getRHS())), type.quantifier());
			}
			case DOCUMENT:
			case ELEMENT:
			{
				final ElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final TextNodeType<A> textType = metaBridge.textType();
				final CommentNodeType<A> commentType = metaBridge.commentType();
				final ProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

				return metaBridge.multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
			}
			case SCHEMA_ELEMENT:
			{
				final ElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final TextNodeType<A> textType = metaBridge.textType();
				final CommentNodeType<A> commentType = metaBridge.commentType();
				final ProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

				return metaBridge.multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

	public SequenceType<A> descendantOrSelfAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(descendantOrSelfAxis(choiceType.getLHS()), descendantOrSelfAxis(choiceType.getRHS())), type.quantifier());
			}
			case ELEMENT:
			case SCHEMA_ELEMENT:
			case DOCUMENT:
			{
				final SequenceType<A> kids = branchChildAxis();
				return metaBridge.multiply(choice(kids, prime), type.quantifier());
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

	public DocumentNodeType<A> documentType(final SequenceType<A> contentType)
	{
		return metaBridge.documentType(contentType);
	}

	public SequenceType<A> elementTest(final SequenceType<A> arg)
	{
		final PrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)primeType;

				return metaBridge.multiply(choice(elementTest(choiceType.getLHS()), elementTest(choiceType.getRHS())), arg.quantifier());
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

	public SequenceType<A> elementType(final QName name, final SequenceType<A> type, final boolean nillable)
	{
		return metaBridge.elementType(name, type, nillable);
	}

	public EmptyType<A> emptyType()
	{
		return metaBridge.emptyType();
	}

	public SequenceType<A> followingAxis(final SequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SequenceType<A> followingSiblingAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(followingSiblingAxis(choiceType.getLHS()), followingSiblingAxis(choiceType.getRHS())), type.quantifier());
			}
			case ATTRIBUTE:
			{
				return metaBridge.multiply(zeroOrMore(attributeType(null, metaBridge.getTypeDefinition(NativeType.UNTYPED_ATOMIC))), type.quantifier());
			}
			case SCHEMA_ATTRIBUTE:
			{
				// TODO: This should come from the complex type, if it exists (otherwise empty).
				return metaBridge.multiply(zeroOrMore(attributeType(null, null)), type.quantifier());
			}
			case NAMESPACE:
			{
				return metaBridge.multiply(metaBridge.zeroOrMore(metaBridge.namespaceType()), type.quantifier());
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
				final ElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final TextNodeType<A> textType = metaBridge.textType();
				final CommentNodeType<A> commentType = metaBridge.commentType();
				final ProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

				return metaBridge.multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

	public AtomBridge<A> getAtomBridge()
	{
		return atomBridge;
	}

	public SequenceType<A> getBinaryLHS(final SequenceType<A> type)
	{
		if (type instanceof ChoiceType<?>)
		{
			final ChoiceType<A> choice = (ChoiceType<A>)type;
			return choice.getLHS();
		}
		else
		{
			throw new AssertionError("TODO: getBinaryLHS(" + type.getClass() + ")");
		}
	}

	public SequenceType<A> getBinaryRHS(final SequenceType<A> type)
	{
		if (type instanceof ChoiceType<?>)
		{
			final ChoiceType<A> choice = (ChoiceType<A>)type;
			return choice.getRHS();
		}
		else
		{
			throw new AssertionError("TODO: getBinaryRHS(" + type.getClass() + ")");
		}
	}

	public QName getErrorCode(final SequenceType<A> noneType)
	{
		if (noneType instanceof NoneType<?>)
		{
			final NoneType<A> error = (NoneType<A>)noneType;
			return error.getErrorCode();
		}
		else
		{
			PreCondition.assertArgumentNotNull(noneType, "noneType");
			PreCondition.assertTrue(isNone(noneType), "isNone(noneType)");
			throw new AssertionError();
		}
	}

	public QName getName(final SequenceType<A> type)
	{
		return metaBridge.getName(type);
	}

	public NameSource getNameBridge()
	{
		return atomBridge.getNameBridge();
	}

	public Type<A> getType(final QName typeName)
	{
		return metaBridge.getTypeDefinition(typeName);
	}

	public Type<A> getType(final NativeType nativeType)
	{
		return metaBridge.getTypeDefinition(nativeType);
	}

	public SequenceType<A> handle(final SequenceType<A> sequenceType)
	{
		return sequenceType;
	}

	public SequenceType<A> interleave(final SequenceType<A> one, final SequenceType<A> two)
	{
		return metaBridge.interleave(one, two);
	}

	public boolean isAttributeNodeType(final SequenceType<A> type)
	{
		return type instanceof AttributeNodeType<?>;
	}

	public boolean isChoice(final SequenceType<A> type)
	{
		return (type instanceof ChoiceType<?>);
	}

	public boolean isCommentNodeType(final SequenceType<A> type)
	{
		return type instanceof CommentNodeType<?>;
	}

	public boolean isDocumentNodeType(final SequenceType<A> type)
	{
		return type instanceof DocumentNodeType<?>;
	}

	public boolean isElementNodeType(final SequenceType<A> type)
	{
		return (type instanceof ElementNodeType<?>);
	}

	public boolean isEmpty(final SequenceType<A> type)
	{
		return type instanceof EmptyType<?>;
	}

	public boolean isNamespaceNodeType(final SequenceType<A> type)
	{
		return type instanceof NamespaceNodeType<?>;
	}

	public boolean isNone(final SequenceType<A> type)
	{
		return (type instanceof NoneType<?>);
	}

	public boolean isProcessingInstructionNodeType(final SequenceType<A> type)
	{
		return type instanceof ProcessingInstructionNodeType<?>;
	}

	public boolean isTextNodeType(final SequenceType<A> type)
	{
		return type instanceof TextNodeType<?>;
	}

	public SequenceType<A> itemType()
	{
		return metaBridge.itemType();
	}

	public SequenceType<A> multiply(final SequenceType<A> argument, final Quantifier multiplier)
	{
		return metaBridge.multiply(argument, convert(multiplier));
	}

	public SequenceType<A> namespaceAxis(final SequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SequenceType<A> namespaceTest(final SequenceType<A> arg)
	{
		final PrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)primeType;

				return metaBridge.multiply(choice(namespaceTest(choiceType.getLHS()), namespaceTest(choiceType.getRHS())), arg.quantifier());
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

	public NamespaceNodeType<A> namespaceType()
	{
		return metaBridge.namespaceType();
	}

	public SequenceType<A> nodeTest(final SequenceType<A> arg)
	{
		final PrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)primeType;

				return metaBridge.multiply(choice(nodeTest(choiceType.getLHS()), nodeTest(choiceType.getRHS())), arg.quantifier());
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

	public SequenceType<A> nodeType()
	{
		return metaBridge.nodeType();
	}

	public SequenceType<A> noneType()
	{
		return metaBridge.noneType();
	}

	public SequenceType<A> noneType(final QName errorCode)
	{
		return metaBridge.noneType(errorCode);
	}

	public SequenceType<A> oneOrMore(final SequenceType<A> type)
	{
		return metaBridge.oneOrMore(type);
	}

	public SequenceType<A> optional(final SequenceType<A> type)
	{
		return metaBridge.optional(type);
	}

	public SequenceType<A> parentAxis(final SequenceType<A> contextType)
	{
		return optional(nodeType());
	}

	public SequenceType<A> precedingAxis(final SequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SequenceType<A> precedingSiblingAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(precedingSiblingAxis(choiceType.getLHS()), precedingSiblingAxis(choiceType.getRHS())), type.quantifier());
			}
			case ATTRIBUTE:
			{
				return metaBridge.multiply(zeroOrMore(attributeType(null, metaBridge.getTypeDefinition(NativeType.UNTYPED_ATOMIC))), type.quantifier());
			}
			case SCHEMA_ATTRIBUTE:
			{
				// TODO: This should come from the complex type, if it exists (otherwise empty).
				return metaBridge.multiply(zeroOrMore(attributeType(null, null)), type.quantifier());
			}
			case NAMESPACE:
			{
				return metaBridge.multiply(zeroOrMore(metaBridge.namespaceType()), type.quantifier());
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
				final ElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final TextNodeType<A> textType = metaBridge.textType();
				final CommentNodeType<A> commentType = metaBridge.commentType();
				final ProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

				return metaBridge.multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
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

	public PrimeType<A> prime(final SequenceType<A> type)
	{
		return type.prime();
	}

	public SequenceType<A> processingInstructionTest(final SequenceType<A> arg, final String name)
	{
		final PrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)primeType;
				return metaBridge.multiply(choice(processingInstructionTest(choiceType.getLHS(), name), processingInstructionTest(choiceType.getRHS(), name)), arg.quantifier());
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

	public ProcessingInstructionNodeType<A> processingInstructionType(final String name)
	{
		return metaBridge.processingInstructionType(name);
	}

	public Quantifier quantifier(final SequenceType<A> type)
	{
		return convert(type.quantifier());
	}

	public boolean sameAs(final SequenceType<A> one, final SequenceType<A> two)
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");
		return metaBridge.sameAs(one, two);
	}

	public SequenceType<A> schemaAttribute(final QName attributeName)
	{
		return metaBridge.getAttributeDeclaration(attributeName);
	}

	public SequenceType<A> schemaElement(final QName elementName)
	{
		return metaBridge.getElementDeclaration(elementName);
	}

	public SequenceType<A> schemaType(final QName typeName)
	{
		return metaBridge.getTypeDefinition(typeName);
	}

	public SequenceType<A> selfAxis(final SequenceType<A> type)
	{
		final PrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(selfAxis(choiceType.getLHS()), selfAxis(choiceType.getRHS())), type.quantifier());
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

	public boolean subtype(final SequenceType<A> lhs, final SequenceType<A> rhs)
	{
		return metaBridge.subtype(lhs, rhs);
	}

	public SequenceType<A> textTest(final SequenceType<A> arg)
	{
		final PrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)primeType;
				return metaBridge.multiply(choice(textTest(choiceType.getLHS()), textTest(choiceType.getRHS())), arg.quantifier());
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

	public TextNodeType<A> textType()
	{
		return metaBridge.textType();
	}

	public String toString(final SequenceType<A> type, final NamespaceResolver mappings, final String defaultElementAndTypeNamespace) throws GxmlException
	{
		return type.toString();
	}

	@SuppressWarnings("unchecked")
	public SequenceType<A>[] typeArray(final int size)
	{
		return new SequenceType[size];
	}

	public SequenceType<A> zeroOrMore(final SequenceType<A> type)
	{
		return metaBridge.zeroOrMore(type);
	}
}
