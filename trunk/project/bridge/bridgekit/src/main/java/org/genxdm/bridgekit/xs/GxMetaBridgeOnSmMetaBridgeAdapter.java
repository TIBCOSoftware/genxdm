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
import org.genxdm.xs.types.SmAttributeNodeType;
import org.genxdm.xs.types.SmChoiceType;
import org.genxdm.xs.types.SmCommentNodeType;
import org.genxdm.xs.types.SmDocumentNodeType;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmEmptyType;
import org.genxdm.xs.types.SmMultiplyType;
import org.genxdm.xs.types.SmNamespaceNodeType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmNoneType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmProcessingInstructionNodeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmTextNodeType;
import org.genxdm.xs.types.SmType;

public final class GxMetaBridgeOnSmMetaBridgeAdapter<A> implements MetaBridge<A>
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

	public GxMetaBridgeOnSmMetaBridgeAdapter(final SchemaTypeBridge<A> metaBridge, final AtomBridge<A> atomBridge)
	{
		this.metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
	}

	public void accept(final SmSequenceType<A> type, final MetaVisitor<A> visitor)
	{
		if (type instanceof SmSimpleType<?>)
		{
			final SmSimpleType<A> simpleType = (SmSimpleType<A>)type;
			if (simpleType.isAtomicType())
			{
				visitor.atomicType(simpleType, simpleType.getName(), simpleType.getBaseType());
			}
			else
			{
				throw new AssertionError("TODO");
			}
		}
		else if (type instanceof SmAttributeNodeType<?>)
		{
			final SmAttributeNodeType<A> attribute = (SmAttributeNodeType<A>)type;
			visitor.attributeType(attribute, attribute.getName(), attribute.getType());
		}
		else if (type instanceof SmChoiceType<?>)
		{
			final SmChoiceType<A> choice = (SmChoiceType<A>)type;
			visitor.choiceType(choice, choice.getLHS(), choice.getRHS());
		}
		else if (type instanceof SmCommentNodeType<?>)
		{
			final SmCommentNodeType<A> comment = (SmCommentNodeType<A>)type;
			visitor.textType(comment);
		}
		else if (type instanceof SmDocumentNodeType<?>)
		{
			final SmDocumentNodeType<A> document = (SmDocumentNodeType<A>)type;
			visitor.documentType(document, document.getContentType());
		}
		else if (type instanceof SmElementNodeType<?>)
		{
			final SmElementNodeType<A> element = (SmElementNodeType<A>)type;
			visitor.elementType(element, element.getName(), element.getType(), element.isNillable());
		}
		else if (type instanceof SmEmptyType<?>)
		{
			final SmEmptyType<A> emptyType = (SmEmptyType<A>)type;
			visitor.emptyType(emptyType);
		}
		else if (type instanceof SmMultiplyType<?>)
		{
			final SmMultiplyType<A> multiply = (SmMultiplyType<A>)type;
			visitor.multiplyType(multiply, multiply.getArgument(), convert(multiply.getMultiplier()));
		}
		else if (type instanceof SmNamespaceNodeType<?>)
		{
			final SmNamespaceNodeType<A> namespace = (SmNamespaceNodeType<A>)type;
			visitor.namespaceType(namespace);
		}
		else if (type instanceof SmNoneType<?>)
		{
			final SmNoneType<A> errorType = (SmNoneType<A>)type;
			visitor.noneType(errorType);
		}
		else if (type instanceof SmProcessingInstructionNodeType<?>)
		{
			final SmProcessingInstructionNodeType<A> pi = (SmProcessingInstructionNodeType<A>)type;
			visitor.processingInstructionType(pi, pi.getName());
		}
		else if (type instanceof SmTextNodeType<?>)
		{
			final SmTextNodeType<A> text = (SmTextNodeType<A>)type;
			visitor.textType(text);
		}
		else
		{
			throw new UnsupportedOperationException("accept(" + type.getClass().getName() + ")");
		}
	}

	public SmSequenceType<A> ancestorAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)prime;
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

	public SmSequenceType<A> ancestorOrSelfAxis(final SmSequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SmSequenceType<A> atomSet(final SmSequenceType<A> type)
	{
		return metaBridge.atomSet(type);
	}

	public SmSequenceType<A> attributeAxis(final SmSequenceType<A> type)
	{
		return metaBridge.attributeAxis(type);
	}

	public SmSequenceType<A> attributeType(final QName name, final SmSequenceType<A> type)
	{
		return metaBridge.attributeType(name, type);
	}

	private SmSequenceType<A> branchChildAxis()
	{
		final SmElementNodeType<A> elementType = metaBridge.elementWild(null, true);
		final SmTextNodeType<A> textType = metaBridge.textType();
		final SmCommentNodeType<A> commentType = metaBridge.commentType();
		final SmProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

		return zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType))));
	}

	public SmSequenceType<A> childAxis(final SmSequenceType<A> type)
	{
		return metaBridge.childAxis(type);
	}

	public SmSequenceType<A> choice(final SmSequenceType<A> one, final SmSequenceType<A> two)
	{
		return metaBridge.choice(one, two);
	}

	public SmSequenceType<A> commentTest(final SmSequenceType<A> arg)
	{
		final SmPrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)primeType;

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

	public SmCommentNodeType<A> commentType()
	{
		return metaBridge.commentType();
	}

	public SmSequenceType<A> concat(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		return metaBridge.concat(lhs, rhs);
	}

	public SmSequenceType<A> contentType(final SmSequenceType<A> type)
	{
		if (type instanceof SmDocumentNodeType<?>)
		{
			return ((SmDocumentNodeType<A>)type).getContentType();
		}
		else
		{
			throw new AssertionError(type);
		}
	}

	public SmSequenceType<A> descendantAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(descendantAxis(choiceType.getLHS()), descendantAxis(choiceType.getRHS())), type.quantifier());
			}
			case DOCUMENT:
			case ELEMENT:
			{
				final SmElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final SmTextNodeType<A> textType = metaBridge.textType();
				final SmCommentNodeType<A> commentType = metaBridge.commentType();
				final SmProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

				return metaBridge.multiply(zeroOrMore(choice(elementType, choice(textType, choice(commentType, processingInstructionType)))), type.quantifier());
			}
			case SCHEMA_ELEMENT:
			{
				final SmElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final SmTextNodeType<A> textType = metaBridge.textType();
				final SmCommentNodeType<A> commentType = metaBridge.commentType();
				final SmProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

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

	public SmSequenceType<A> descendantOrSelfAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(descendantOrSelfAxis(choiceType.getLHS()), descendantOrSelfAxis(choiceType.getRHS())), type.quantifier());
			}
			case ELEMENT:
			case SCHEMA_ELEMENT:
			case DOCUMENT:
			{
				final SmSequenceType<A> kids = branchChildAxis();
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

	public SmDocumentNodeType<A> documentType(final SmSequenceType<A> contentType)
	{
		return metaBridge.documentType(contentType);
	}

	public SmSequenceType<A> elementTest(final SmSequenceType<A> arg)
	{
		final SmPrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)primeType;

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

	public SmSequenceType<A> elementType(final QName name, final SmSequenceType<A> type, final boolean nillable)
	{
		return metaBridge.elementType(name, type, nillable);
	}

	public SmEmptyType<A> emptyType()
	{
		return metaBridge.emptyType();
	}

	public SmSequenceType<A> followingAxis(final SmSequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SmSequenceType<A> followingSiblingAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(followingSiblingAxis(choiceType.getLHS()), followingSiblingAxis(choiceType.getRHS())), type.quantifier());
			}
			case ATTRIBUTE:
			{
				return metaBridge.multiply(zeroOrMore(attributeType(null, metaBridge.getTypeDefinition(SmNativeType.UNTYPED_ATOMIC))), type.quantifier());
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
				final SmElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final SmTextNodeType<A> textType = metaBridge.textType();
				final SmCommentNodeType<A> commentType = metaBridge.commentType();
				final SmProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

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

	public SmSequenceType<A> getBinaryLHS(final SmSequenceType<A> type)
	{
		if (type instanceof SmChoiceType<?>)
		{
			final SmChoiceType<A> choice = (SmChoiceType<A>)type;
			return choice.getLHS();
		}
		else
		{
			throw new AssertionError("TODO: getBinaryLHS(" + type.getClass() + ")");
		}
	}

	public SmSequenceType<A> getBinaryRHS(final SmSequenceType<A> type)
	{
		if (type instanceof SmChoiceType<?>)
		{
			final SmChoiceType<A> choice = (SmChoiceType<A>)type;
			return choice.getRHS();
		}
		else
		{
			throw new AssertionError("TODO: getBinaryRHS(" + type.getClass() + ")");
		}
	}

	public QName getErrorCode(final SmSequenceType<A> noneType)
	{
		if (noneType instanceof SmNoneType<?>)
		{
			final SmNoneType<A> error = (SmNoneType<A>)noneType;
			return error.getErrorCode();
		}
		else
		{
			PreCondition.assertArgumentNotNull(noneType, "noneType");
			PreCondition.assertTrue(isNone(noneType), "isNone(noneType)");
			throw new AssertionError();
		}
	}

	public QName getName(final SmSequenceType<A> type)
	{
		return metaBridge.getName(type);
	}

	public NameSource getNameBridge()
	{
		return atomBridge.getNameBridge();
	}

	public SmType<A> getType(final QName typeName)
	{
		return metaBridge.getTypeDefinition(typeName);
	}

	public SmType<A> getType(final SmNativeType nativeType)
	{
		return metaBridge.getTypeDefinition(nativeType);
	}

	public SmSequenceType<A> handle(final SmSequenceType<A> sequenceType)
	{
		return sequenceType;
	}

	public SmSequenceType<A> interleave(final SmSequenceType<A> one, final SmSequenceType<A> two)
	{
		return metaBridge.interleave(one, two);
	}

	public boolean isAttributeNodeType(final SmSequenceType<A> type)
	{
		return type instanceof SmAttributeNodeType<?>;
	}

	public boolean isChoice(final SmSequenceType<A> type)
	{
		return (type instanceof SmChoiceType<?>);
	}

	public boolean isCommentNodeType(final SmSequenceType<A> type)
	{
		return type instanceof SmCommentNodeType<?>;
	}

	public boolean isDocumentNodeType(final SmSequenceType<A> type)
	{
		return type instanceof SmDocumentNodeType<?>;
	}

	public boolean isElementNodeType(final SmSequenceType<A> type)
	{
		return (type instanceof SmElementNodeType<?>);
	}

	public boolean isEmpty(final SmSequenceType<A> type)
	{
		return type instanceof SmEmptyType<?>;
	}

	public boolean isNamespaceNodeType(final SmSequenceType<A> type)
	{
		return type instanceof SmNamespaceNodeType<?>;
	}

	public boolean isNone(final SmSequenceType<A> type)
	{
		return (type instanceof SmNoneType<?>);
	}

	public boolean isProcessingInstructionNodeType(final SmSequenceType<A> type)
	{
		return type instanceof SmProcessingInstructionNodeType<?>;
	}

	public boolean isTextNodeType(final SmSequenceType<A> type)
	{
		return type instanceof SmTextNodeType<?>;
	}

	public SmSequenceType<A> itemType()
	{
		return metaBridge.itemType();
	}

	public SmSequenceType<A> multiply(final SmSequenceType<A> argument, final Quantifier multiplier)
	{
		return metaBridge.multiply(argument, convert(multiplier));
	}

	public SmSequenceType<A> namespaceAxis(final SmSequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SmSequenceType<A> namespaceTest(final SmSequenceType<A> arg)
	{
		final SmPrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)primeType;

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

	public SmNamespaceNodeType<A> namespaceType()
	{
		return metaBridge.namespaceType();
	}

	public SmSequenceType<A> nodeTest(final SmSequenceType<A> arg)
	{
		final SmPrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)primeType;

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

	public SmSequenceType<A> nodeType()
	{
		return metaBridge.nodeType();
	}

	public SmSequenceType<A> noneType()
	{
		return metaBridge.noneType();
	}

	public SmSequenceType<A> noneType(final QName errorCode)
	{
		return metaBridge.noneType(errorCode);
	}

	public SmSequenceType<A> oneOrMore(final SmSequenceType<A> type)
	{
		return metaBridge.oneOrMore(type);
	}

	public SmSequenceType<A> optional(final SmSequenceType<A> type)
	{
		return metaBridge.optional(type);
	}

	public SmSequenceType<A> parentAxis(final SmSequenceType<A> contextType)
	{
		return optional(nodeType());
	}

	public SmSequenceType<A> precedingAxis(final SmSequenceType<A> contextType)
	{
		return zeroOrMore(nodeType());
	}

	public SmSequenceType<A> precedingSiblingAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)prime;
				return metaBridge.multiply(choice(precedingSiblingAxis(choiceType.getLHS()), precedingSiblingAxis(choiceType.getRHS())), type.quantifier());
			}
			case ATTRIBUTE:
			{
				return metaBridge.multiply(zeroOrMore(attributeType(null, metaBridge.getTypeDefinition(SmNativeType.UNTYPED_ATOMIC))), type.quantifier());
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
				final SmElementNodeType<A> elementType = metaBridge.elementWild(null, true);
				final SmTextNodeType<A> textType = metaBridge.textType();
				final SmCommentNodeType<A> commentType = metaBridge.commentType();
				final SmProcessingInstructionNodeType<A> processingInstructionType = metaBridge.processingInstructionType(null);

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

	public SmPrimeType<A> prime(final SmSequenceType<A> type)
	{
		return type.prime();
	}

	public SmSequenceType<A> processingInstructionTest(final SmSequenceType<A> arg, final String name)
	{
		final SmPrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)primeType;
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

	public SmProcessingInstructionNodeType<A> processingInstructionType(final String name)
	{
		return metaBridge.processingInstructionType(name);
	}

	public Quantifier quantifier(final SmSequenceType<A> type)
	{
		return convert(type.quantifier());
	}

	public boolean sameAs(final SmSequenceType<A> one, final SmSequenceType<A> two)
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");
		return metaBridge.sameAs(one, two);
	}

	public SmSequenceType<A> schemaAttribute(final QName attributeName)
	{
		return metaBridge.getAttributeDeclaration(attributeName);
	}

	public SmSequenceType<A> schemaElement(final QName elementName)
	{
		return metaBridge.getElementDeclaration(elementName);
	}

	public SmSequenceType<A> schemaType(final QName typeName)
	{
		return metaBridge.getTypeDefinition(typeName);
	}

	public SmSequenceType<A> selfAxis(final SmSequenceType<A> type)
	{
		final SmPrimeType<A> prime = type.prime();
		switch (prime.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)prime;
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

	public boolean subtype(final SmSequenceType<A> lhs, final SmSequenceType<A> rhs)
	{
		return metaBridge.subtype(lhs, rhs);
	}

	public SmSequenceType<A> textTest(final SmSequenceType<A> arg)
	{
		final SmPrimeType<A> primeType = arg.prime();
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)primeType;
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

	public SmTextNodeType<A> textType()
	{
		return metaBridge.textType();
	}

	public String toString(final SmSequenceType<A> type, final NamespaceResolver mappings, final String defaultElementAndTypeNamespace) throws GxmlException
	{
		return type.toString();
	}

	@SuppressWarnings("unchecked")
	public SmSequenceType<A>[] typeArray(final int size)
	{
		return new SmSequenceType[size];
	}

	public SmSequenceType<A> zeroOrMore(final SmSequenceType<A> type)
	{
		return metaBridge.zeroOrMore(type);
	}
}
