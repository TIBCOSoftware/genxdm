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
package org.gxml.bridgetest.typed;

import javax.xml.namespace.QName;

import org.genxdm.base.ProcessingContext;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceResolver;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSequenceType;
import org.gxml.bridgetest.GxTestBase;

public abstract class MetaBridgeTestBase<N, A> 
    extends GxTestBase<N>
{
    private static final String ESCAPE = "\u001B";

	public void testSerialization()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();
		final QName foo = new QName("foo");
		final QName starFoo = new QName(ESCAPE, "foo");
		final QName fooStar = new QName("http://www.foo.com", ESCAPE);
		final QName star = new QName(ESCAPE, ESCAPE);
		final SmSequenceType<A> stringType = metaBridge.getType(SmNativeType.STRING);

		assertToString("empty-sequence()", metaBridge.emptyType(), pcx);

		assertToString("document-node()", metaBridge.documentType(null), pcx);
		assertToString("document-node(element(foo))", metaBridge.documentType(metaBridge.elementType(foo, null, false)), pcx);
		assertToString("document-node(schema-element(foo))", metaBridge.documentType(metaBridge.schemaElement(foo)), pcx);

		assertToString("element("+ESCAPE+")", metaBridge.elementType(null, null, false), pcx);
		assertToString("element(foo)", metaBridge.elementType(foo, null, false), pcx);
		assertToString("element("+ESCAPE+":foo)", metaBridge.elementType(starFoo, null, false), pcx);
		assertToString("element(f:"+ESCAPE+")", metaBridge.elementType(fooStar, null, false), pcx);
		assertToString("element("+ESCAPE+")", metaBridge.elementType(star, null, false), pcx);
		assertToString("element("+ESCAPE+", xs:string)", metaBridge.elementType(star, stringType, false), pcx);

		assertToString("element("+ESCAPE+")", metaBridge.elementType(null, null, true), pcx);
		assertToString("element(foo)", metaBridge.elementType(foo, null, true), pcx);
		assertToString("element("+ESCAPE+":foo)", metaBridge.elementType(starFoo, null, true), pcx);
		assertToString("element(f:"+ESCAPE+")", metaBridge.elementType(fooStar, null, true), pcx);
		assertToString("element("+ESCAPE+")", metaBridge.elementType(star, null, true), pcx);
		assertToString("element("+ESCAPE+", xs:string, ?)", metaBridge.elementType(star, stringType, true), pcx);

		assertToString("attribute("+ESCAPE+")", metaBridge.attributeType(null, null), pcx);
		assertToString("attribute(foo)", metaBridge.attributeType(foo, null), pcx);
		assertToString("attribute("+ESCAPE+":foo)", metaBridge.attributeType(starFoo, null), pcx);
		assertToString("attribute(f:"+ESCAPE+")", metaBridge.attributeType(fooStar, null), pcx);
		assertToString("attribute("+ESCAPE+")", metaBridge.attributeType(star, null), pcx);
		assertToString("attribute("+ESCAPE+", xs:string)", metaBridge.attributeType(star, stringType), pcx);

		assertToString("schema-element(foo)", metaBridge.schemaElement(foo), pcx);
		assertToString("schema-attribute(foo)", metaBridge.schemaAttribute(foo), pcx);
		assertToString("processing-instruction()", metaBridge.processingInstructionType(null), pcx);
		assertToString("processing-instruction('x')", metaBridge.processingInstructionType("x"), pcx);
		assertToString("comment()", metaBridge.commentType(), pcx);
		assertToString("text()", metaBridge.textType(), pcx);
		assertToString("node()", metaBridge.nodeType(), pcx);
		assertToString("item()", metaBridge.itemType(), pcx);
		assertToString("xs:string", stringType, pcx);

		assertToString("item()?", metaBridge.optional(metaBridge.itemType()), pcx);
		assertToString("item()*", metaBridge.zeroOrMore(metaBridge.itemType()), pcx);
		assertToString("item()+", metaBridge.oneOrMore(metaBridge.itemType()), pcx);
		assertToString("item()?", metaBridge.multiply(metaBridge.itemType(), Quantifier.OPTIONAL), pcx);
		assertToString("item()*", metaBridge.multiply(metaBridge.itemType(), Quantifier.ZERO_OR_MORE), pcx);
		assertToString("item()+", metaBridge.multiply(metaBridge.itemType(), Quantifier.ONE_OR_MORE), pcx);
		assertToString("item()", metaBridge.multiply(metaBridge.itemType(), Quantifier.EXACTLY_ONE), pcx);
		assertToString("empty-sequence()", metaBridge.multiply(metaBridge.itemType(), Quantifier.EMPTY), pcx);
		assertToString("none", metaBridge.multiply(metaBridge.itemType(), Quantifier.NONE), pcx);

		final SmSequenceType<A> documentType = metaBridge.documentType(null);
		final SmSequenceType<A> elementType = metaBridge.elementType(null, null, false);

		// final SmSequenceType<A> ed = metaBridge.choice(elementType, documentType);

		assertToString("element(*)", elementType, pcx);
		assertToString("element(*)", metaBridge.choice(elementType, elementType), pcx);
		assertToString("element(*) | document-node()", metaBridge.choice(elementType, documentType), pcx);
	}

	public void assertToString(final String expression, final SmSequenceType<A> type, final TypedContext<N, A> pcx)
	{
		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final NamespaceResolver inScopeNamespaces = null;

		final String actual = metaBridge.toString(type, inScopeNamespaces, "");

		assertEquals("assertToString(expression=" + expression + ")", expression, actual);
	}

	public void testProlog()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		assertNotNull(metaBridge);
	}

	/**
	 * None is used for when nothing is returned, which means an error was raised.
	 */
	public void testNone()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final SmSequenceType<A> error = metaBridge.noneType();
		assertNotNull(error);
		assertNotNull(metaBridge.prime(error));
		assertEquals(Quantifier.EXACTLY_ONE, metaBridge.quantifier(error));

		assertFalse(metaBridge.quantifier(error).isEmpty());
	}

	public void testEmpty()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final SmSequenceType<A> empty = metaBridge.emptyType();
		assertNotNull(empty);
		assertNotNull(metaBridge.prime(empty));
		assertEquals(Quantifier.OPTIONAL, metaBridge.quantifier(empty));

		assertTrue(metaBridge.quantifier(empty).isEmpty());
	}

	public void testSubtypeForAtoms()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final SmSequenceType<A> anyAtomicType = metaBridge.getType(SmNativeType.ANY_ATOMIC_TYPE);
		assertNotNull(anyAtomicType);
		final SmSequenceType<A> doubleType = metaBridge.getType(SmNativeType.DOUBLE);
		assertNotNull(doubleType);
		final SmSequenceType<A> floatType = metaBridge.getType(SmNativeType.FLOAT);
		assertNotNull(floatType);

		assertTrue(metaBridge.subtype(anyAtomicType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, doubleType));
		assertFalse(metaBridge.subtype(anyAtomicType, floatType));
		assertTrue(metaBridge.subtype(doubleType, anyAtomicType));
		assertTrue(metaBridge.subtype(doubleType, doubleType));
		assertFalse(metaBridge.subtype(doubleType, floatType));
		assertTrue(metaBridge.subtype(floatType, anyAtomicType));
		// TODO: Issue here is promotion.
		// assertTrue(metaBridge.subtype(floatType, doubleType));
		assertTrue(metaBridge.subtype(floatType, floatType));
	}

	public void testSubtypeForNodes()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final SmSequenceType<A> attributeType = metaBridge.attributeType(null, null);
		final SmSequenceType<A> commentType = metaBridge.commentType();
		final SmSequenceType<A> documentType = metaBridge.documentType(null);
		final SmSequenceType<A> elementType = metaBridge.elementType(null, null, true);
		final SmSequenceType<A> namespaceType = metaBridge.namespaceType();
		final SmSequenceType<A> textType = metaBridge.textType();
		final SmSequenceType<A> piType = metaBridge.processingInstructionType(null);

		assertSubtypePositive(attributeType, attributeType, metaBridge);
		assertSubtypeNegative(attributeType, commentType, metaBridge);
		assertSubtypeNegative(attributeType, documentType, metaBridge);
		assertSubtypeNegative(attributeType, elementType, metaBridge);
		assertSubtypeNegative(attributeType, namespaceType, metaBridge);
		assertSubtypeNegative(attributeType, piType, metaBridge);
		assertSubtypeNegative(attributeType, textType, metaBridge);

		assertSubtypeNegative(commentType, attributeType, metaBridge);
		assertSubtypePositive(commentType, commentType, metaBridge);
		assertSubtypeNegative(commentType, documentType, metaBridge);
		assertSubtypeNegative(commentType, elementType, metaBridge);
		assertSubtypeNegative(commentType, namespaceType, metaBridge);
		assertSubtypeNegative(commentType, piType, metaBridge);
		assertSubtypeNegative(commentType, textType, metaBridge);

		assertSubtypeNegative(documentType, attributeType, metaBridge);
		assertSubtypeNegative(documentType, commentType, metaBridge);
		assertSubtypePositive(documentType, documentType, metaBridge);
		assertSubtypeNegative(documentType, elementType, metaBridge);
		assertSubtypeNegative(documentType, namespaceType, metaBridge);
		assertSubtypeNegative(documentType, piType, metaBridge);
		assertSubtypeNegative(documentType, textType, metaBridge);

		assertSubtypePositive(documentType, metaBridge.choice(documentType, attributeType), metaBridge);
		assertSubtypePositive(documentType, metaBridge.choice(documentType, commentType), metaBridge);
		assertSubtypePositive(documentType, metaBridge.choice(documentType, documentType), metaBridge);
		assertSubtypePositive(documentType, metaBridge.choice(documentType, elementType), metaBridge);
		assertSubtypePositive(documentType, metaBridge.choice(documentType, namespaceType), metaBridge);
		assertSubtypePositive(documentType, metaBridge.choice(documentType, piType), metaBridge);
		assertSubtypePositive(documentType, metaBridge.choice(documentType, textType), metaBridge);

		assertSubtypeNegative(elementType, attributeType, metaBridge);
		assertSubtypeNegative(elementType, commentType, metaBridge);
		assertSubtypeNegative(elementType, documentType, metaBridge);
		assertSubtypePositive(elementType, elementType, metaBridge);
		assertSubtypeNegative(elementType, namespaceType, metaBridge);
		assertSubtypeNegative(elementType, piType, metaBridge);
		assertSubtypeNegative(elementType, textType, metaBridge);

		assertSubtypeNegative(namespaceType, attributeType, metaBridge);
		assertSubtypeNegative(namespaceType, commentType, metaBridge);
		assertSubtypeNegative(namespaceType, documentType, metaBridge);
		assertSubtypeNegative(namespaceType, elementType, metaBridge);
		assertSubtypePositive(namespaceType, namespaceType, metaBridge);
		assertSubtypeNegative(namespaceType, piType, metaBridge);
		assertSubtypeNegative(namespaceType, textType, metaBridge);

		assertSubtypeNegative(piType, attributeType, metaBridge);
		assertSubtypeNegative(piType, commentType, metaBridge);
		assertSubtypeNegative(piType, documentType, metaBridge);
		assertSubtypeNegative(piType, elementType, metaBridge);
		assertSubtypeNegative(piType, namespaceType, metaBridge);
		assertSubtypePositive(piType, piType, metaBridge);
		assertSubtypeNegative(piType, textType, metaBridge);

		assertSubtypeNegative(textType, attributeType, metaBridge);
		assertSubtypeNegative(textType, commentType, metaBridge);
		assertSubtypeNegative(textType, documentType, metaBridge);
		assertSubtypeNegative(textType, elementType, metaBridge);
		assertSubtypeNegative(textType, namespaceType, metaBridge);
		assertSubtypeNegative(textType, piType, metaBridge);
		assertSubtypePositive(textType, textType, metaBridge);

		assertSubtypePositive(commentType, metaBridge.choice(commentType, commentType), metaBridge);
		assertSubtypePositive(commentType, metaBridge.choice(textType, commentType), metaBridge);
		assertSubtypePositive(commentType, metaBridge.choice(commentType, textType), metaBridge);
		assertSubtypeNegative(commentType, metaBridge.choice(textType, textType), metaBridge);

		assertSubtypePositive(metaBridge.choice(commentType, commentType), commentType, metaBridge);
		assertSubtypeNegative(metaBridge.choice(commentType, textType), commentType, metaBridge);
		assertSubtypeNegative(metaBridge.choice(textType, commentType), commentType, metaBridge);
		assertSubtypeNegative(metaBridge.choice(textType, textType), commentType, metaBridge);

		assertSubtypeNegative(textType, metaBridge.choice(commentType, commentType), metaBridge);
		assertSubtypePositive(textType, metaBridge.choice(textType, commentType), metaBridge);
		assertSubtypePositive(textType, metaBridge.choice(commentType, textType), metaBridge);
		assertSubtypePositive(textType, metaBridge.choice(textType, textType), metaBridge);

		final SmSequenceType<A> nodeType = metaBridge.nodeType();

		assertSubtypePositive(attributeType, nodeType, metaBridge);
		assertSubtypePositive(commentType, nodeType, metaBridge);
		assertSubtypePositive(documentType, nodeType, metaBridge);
		assertSubtypePositive(elementType, nodeType, metaBridge);
		assertSubtypePositive(namespaceType, nodeType, metaBridge);
		assertSubtypePositive(piType, nodeType, metaBridge);
		assertSubtypePositive(textType, nodeType, metaBridge);

		assertSubtypeNegative(nodeType, attributeType, metaBridge);
		assertSubtypeNegative(nodeType, commentType, metaBridge);
		assertSubtypeNegative(nodeType, documentType, metaBridge);
		assertSubtypeNegative(nodeType, elementType, metaBridge);
		assertSubtypeNegative(nodeType, namespaceType, metaBridge);
		assertSubtypeNegative(nodeType, piType, metaBridge);
		assertSubtypeNegative(nodeType, textType, metaBridge);

		assertSubtypePositive(nodeType, nodeType, metaBridge);

		final SmSequenceType<A> itemType = metaBridge.itemType();

		assertSubtypePositive(attributeType, itemType, metaBridge);
		assertSubtypePositive(commentType, itemType, metaBridge);
		assertSubtypePositive(documentType, itemType, metaBridge);
		assertSubtypePositive(elementType, itemType, metaBridge);
		assertSubtypePositive(namespaceType, itemType, metaBridge);
		assertSubtypePositive(piType, itemType, metaBridge);
		assertSubtypePositive(textType, itemType, metaBridge);

		assertSubtypeNegative(itemType, attributeType, metaBridge);
		assertSubtypeNegative(itemType, commentType, metaBridge);
		assertSubtypeNegative(itemType, documentType, metaBridge);
		assertSubtypeNegative(itemType, elementType, metaBridge);
		assertSubtypeNegative(itemType, namespaceType, metaBridge);
		assertSubtypeNegative(itemType, piType, metaBridge);
		assertSubtypeNegative(itemType, textType, metaBridge);

		assertSubtypePositive(itemType, itemType, metaBridge);

		assertSubtypePositive(nodeType, itemType, metaBridge);
		assertSubtypeNegative(itemType, nodeType, metaBridge);
	}

	public void testProcessingInstructionSubtype()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final SmSequenceType<A> piType = metaBridge.processingInstructionType(null);
		final SmSequenceType<A> piTypeA = metaBridge.processingInstructionType("a");
		final SmSequenceType<A> piTypeB = metaBridge.processingInstructionType("b");

		assertSubtypePositive(piType, piType, metaBridge);
		assertSubtypeNegative(piType, piTypeA, metaBridge);
		assertSubtypeNegative(piType, piTypeB, metaBridge);
		assertSubtypePositive(piTypeA, piType, metaBridge);
		assertSubtypePositive(piTypeA, piTypeA, metaBridge);
		assertSubtypeNegative(piTypeA, piTypeB, metaBridge);
		assertSubtypePositive(piTypeB, piType, metaBridge);
		assertSubtypeNegative(piTypeB, piTypeA, metaBridge);
		assertSubtypePositive(piTypeB, piTypeB, metaBridge);
	}

	public void testQuantifier()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		assertNotNull(metaBridge);

		doQuantifier(metaBridge.attributeType(null, null), metaBridge);
		doQuantifier(metaBridge.commentType(), metaBridge);
		doQuantifier(metaBridge.documentType(null), metaBridge);
		doQuantifier(metaBridge.elementType(null, null, false), metaBridge);
		doQuantifier(metaBridge.namespaceType(), metaBridge);
		doQuantifier(metaBridge.processingInstructionType(null), metaBridge);
		doQuantifier(metaBridge.textType(), metaBridge);
	}

	public void doQuantifier(final SmSequenceType<A> nodeEins, final MetaBridge<A> metaBridge)
	{
		final SmSequenceType<A> nodeNone = metaBridge.multiply(nodeEins, Quantifier.NONE);
		final SmSequenceType<A> nodeZero = metaBridge.multiply(nodeEins, Quantifier.EMPTY);
		final SmSequenceType<A> nodeQmrk = metaBridge.optional(nodeEins);
		final SmSequenceType<A> nodePlus = metaBridge.oneOrMore(nodeEins);
		final SmSequenceType<A> nodeStar = metaBridge.zeroOrMore(nodeEins);

		assertEquals(Quantifier.NONE, metaBridge.quantifier(nodeNone));
		assertEquals(Quantifier.EXACTLY_ONE, metaBridge.quantifier(nodeEins));
		assertEquals(Quantifier.EMPTY, metaBridge.quantifier(nodeZero));
		assertEquals(Quantifier.OPTIONAL, metaBridge.quantifier(nodeQmrk));
		assertEquals(Quantifier.ONE_OR_MORE, metaBridge.quantifier(nodePlus));
		assertEquals(Quantifier.ZERO_OR_MORE, metaBridge.quantifier(nodeStar));

		assertEquals(Quantifier.OPTIONAL, metaBridge.quantifier(metaBridge.multiply(nodeEins, Quantifier.OPTIONAL)));
		assertEquals(Quantifier.ZERO_OR_MORE, metaBridge.quantifier(metaBridge.multiply(nodeEins, Quantifier.ZERO_OR_MORE)));

		assertEquals(Quantifier.EXACTLY_ONE, metaBridge.quantifier(metaBridge.multiply(nodeEins, Quantifier.EXACTLY_ONE)));
		assertEquals(Quantifier.ONE_OR_MORE, metaBridge.quantifier(metaBridge.multiply(nodeEins, Quantifier.ONE_OR_MORE)));

		assertEquals(Quantifier.NONE, metaBridge.quantifier(metaBridge.multiply(nodeEins, Quantifier.NONE)));
	}

	public void testBuiltInAtomsExist()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		assertNotNull(metaBridge);

		// Make sure they are all present and correct.
		for (final SmNativeType builtInType : SmNativeType.values())
		{
			switch (builtInType)
			{
				case ANY_TYPE:
				case ANY_SIMPLE_TYPE:
				case UNTYPED:
				case IDREFS:
				case NMTOKENS:
				case ENTITIES:
				{
					// Ignore
				}
				break;
				default:
				{
					final SmSequenceType<A> atomicType = metaBridge.getType(builtInType);
					assertNotNull(builtInType.name(), atomicType);
				}
			}
		}

		final SmSequenceType<A> stringType = metaBridge.getType(SmNativeType.STRING);
		// final SmSequenceType<A> doubleType = metaBridge.getType(BuiltInType.DOUBLE);
		// final SmSequenceType<A> floatType = metaBridge.getType(BuiltInType.FLOAT);
		// final SmSequenceType<A> decimalType = metaBridge.getType(BuiltInType.DECIMAL);
		final SmSequenceType<A> integerType = metaBridge.getType(SmNativeType.INTEGER);
		final SmSequenceType<A> dateType = metaBridge.getType(SmNativeType.DATE);
		final SmSequenceType<A> choiceType = metaBridge.choice(integerType, dateType);

		assertTrue(metaBridge.subtype(stringType, stringType));
		assertFalse(metaBridge.subtype(choiceType, integerType));

		assertTrue(metaBridge.subtype(integerType, integerType));
		assertTrue(metaBridge.subtype(integerType, choiceType));
		assertFalse(metaBridge.subtype(choiceType, integerType));

		assertTrue(metaBridge.subtype(dateType, dateType));
		assertTrue(metaBridge.subtype(dateType, choiceType));
		assertFalse(metaBridge.subtype(choiceType, dateType));
	}

	public void testSubtypeForAtomsAndNodes()
	{
        final ProcessingContext<N> ctx = newProcessingContext();
        final TypedContext<N, A> pcx = ctx.getTypedContext();

		final MetaBridge<A> metaBridge = pcx.getMetaBridge();

		final SmSequenceType<A> attributeType = metaBridge.attributeType(null, null);
		final SmSequenceType<A> commentType = metaBridge.commentType();
		final SmSequenceType<A> documentType = metaBridge.documentType(null);
		final SmSequenceType<A> elementType = metaBridge.elementType(null, null, true);
		final SmSequenceType<A> namespaceType = metaBridge.namespaceType();
		final SmSequenceType<A> textType = metaBridge.textType();
		final SmSequenceType<A> piType = metaBridge.processingInstructionType(null);

		final SmSequenceType<A> stringType = metaBridge.getType(SmNativeType.STRING);

		final SmSequenceType<A> nodeType = metaBridge.nodeType();

		final SmSequenceType<A> itemType = metaBridge.itemType();

		assertFalse(metaBridge.subtype(stringType, attributeType));
		assertFalse(metaBridge.subtype(attributeType, stringType));
		assertFalse(metaBridge.subtype(stringType, commentType));
		assertFalse(metaBridge.subtype(commentType, stringType));
		assertFalse(metaBridge.subtype(stringType, documentType));
		assertFalse(metaBridge.subtype(documentType, stringType));
		assertFalse(metaBridge.subtype(stringType, elementType));
		assertFalse(metaBridge.subtype(elementType, stringType));
		assertFalse(metaBridge.subtype(stringType, namespaceType));
		assertFalse(metaBridge.subtype(namespaceType, stringType));
		assertFalse(metaBridge.subtype(stringType, textType));
		assertFalse(metaBridge.subtype(textType, stringType));
		assertFalse(metaBridge.subtype(stringType, piType));
		assertFalse(metaBridge.subtype(piType, stringType));

		assertFalse(metaBridge.subtype(stringType, nodeType));
		assertFalse(metaBridge.subtype(nodeType, stringType));

		assertTrue(metaBridge.subtype(stringType, itemType));
		assertFalse(metaBridge.subtype(itemType, stringType));

		final SmSequenceType<A> anyAtomicType = metaBridge.getType(SmNativeType.ANY_ATOMIC_TYPE);

		assertFalse(metaBridge.subtype(anyAtomicType, attributeType));
		assertFalse(metaBridge.subtype(attributeType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, commentType));
		assertFalse(metaBridge.subtype(commentType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, documentType));
		assertFalse(metaBridge.subtype(documentType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, elementType));
		assertFalse(metaBridge.subtype(elementType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, namespaceType));
		assertFalse(metaBridge.subtype(namespaceType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, textType));
		assertFalse(metaBridge.subtype(textType, anyAtomicType));
		assertFalse(metaBridge.subtype(anyAtomicType, piType));
		assertFalse(metaBridge.subtype(piType, anyAtomicType));

		assertFalse(metaBridge.subtype(anyAtomicType, nodeType));
		assertFalse(metaBridge.subtype(nodeType, anyAtomicType));

		assertTrue(metaBridge.subtype(anyAtomicType, itemType));
		assertFalse(metaBridge.subtype(itemType, anyAtomicType));
	}

	/**
	 * Asserts that the lhs type is a subtype of the rhs type. <br/>
	 * Using indirection here in case the API signature changes.
	 */
	private void assertSubtypePositive(final SmSequenceType<A> lhsType, final SmSequenceType<A> rhsType, final MetaBridge<A> metaBridge)
	{
		final NameSource nameBridge = metaBridge.getNameBridge();
		final NamespaceResolver mappings = null;
		final String lhsString = metaBridge.toString(lhsType, mappings, nameBridge.empty());
		final String rhsString = metaBridge.toString(rhsType, mappings, nameBridge.empty());
		if (!metaBridge.subtype(lhsType, rhsType))
		{
			System.out.println("lhs=" + lhsString);
			System.out.println("rhs=" + rhsString);
		}
		assertTrue(metaBridge.subtype(lhsType, rhsType));
	}

	/**
	 * Asserts that the lhs type is not a subtype of the rhs type. <br/>
	 * Using indirection here in case the API signature changes.
	 */
	private void assertSubtypeNegative(final SmSequenceType<A> lhsType, final SmSequenceType<A> rhsType, final MetaBridge<A> metaBridge)
	{
		assertFalse(metaBridge.subtype(lhsType, rhsType));
	}
}
