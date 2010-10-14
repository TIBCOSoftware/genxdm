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
package org.genxdm.typed.types;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GxmlException;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceResolver;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSequenceType;

/**
 * A processing context parameterized by atom(s) and type(s).
 */
public interface MetaBridge<A>
{
	/**
	 * Implementation of the visitor pattern.
	 * 
	 * @param type
	 *            The type that accepts the visitor.
	 * @param visitor
	 *            The visitor.
	 */
	void accept(SmSequenceType<A> type, MetaVisitor<A> visitor);

	/**
	 * Computes the type resulting from the application of the ancestor axis.
	 */
	SmSequenceType<A> ancestorAxis(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the ancestor-or-self axis.
	 */
	SmSequenceType<A> ancestorOrSelfAxis(SmSequenceType<A> type);

	/**
	 * Returns a type denoting fn:data(arg).
	 */
	SmSequenceType<A> atomSet(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the attribute axis.
	 */
	SmSequenceType<A> attributeAxis(SmSequenceType<A> type);

	/**
	 * Returns a type denoting an attribute of the specified name and type.
	 * 
	 * @param name
	 *            The name of the attribute. A value of <code>null</code> returns an attribute with a wildcard name.
	 * @param type
	 *            The type of the attribute. May be <code>null</code> for xs:anySimpleType.
	 */
	SmSequenceType<A> attributeType(QName name, SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the child axis.
	 */
	SmSequenceType<A> childAxis(SmSequenceType<A> type);

	/**
	 * Returns a type denoting a choice of two types.
	 */
	SmSequenceType<A> choice(SmSequenceType<A> one, SmSequenceType<A> two);

	/**
	 * Applies the comment() test to the argument.
	 */
	SmSequenceType<A> commentTest(SmSequenceType<A> arg);

	/**
	 * Returns a type denoting a single comment node.
	 */
	SmSequenceType<A> commentType();

	/**
	 * Returns a type denoting the combined sequence of two types.
	 */
	SmSequenceType<A> concat(SmSequenceType<A> lhs, SmSequenceType<A> rhs);

	/**
	 * <table border="1">
	 * <tr>
	 * <th>type</th>
	 * <th>contentType(type)</th>
	 * </tr>
	 * <tr>
	 * <td>DocumentTest</td>
	 * <td>ElementTest | SchemaElementTest</td>
	 * </tr>
	 * </table>
	 */
	SmSequenceType<A> contentType(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the descendant axis.
	 */
	SmSequenceType<A> descendantAxis(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the descendant-or-self axis.
	 */
	SmSequenceType<A> descendantOrSelfAxis(SmSequenceType<A> type);

	/**
	 * Returns a type denoting a single document node with the specified content.
	 */
	SmSequenceType<A> documentType(SmSequenceType<A> contentType);

	/**
	 * Applies an element() test to the argument.
	 */
	SmSequenceType<A> elementTest(SmSequenceType<A> arg);

	/**
	 * Returns a type denoting an element of the specified name, type and nillability.
	 * 
	 * @param name
	 *            The name of the element. A value of <code>null</code> return an element node type with a wildcard
	 *            name.
	 * @param type
	 *            The type of the element. May be <code>null</code> for xs:untyped.
	 * @param nillable
	 *            The nillable flag.
	 */
	SmSequenceType<A> elementType(QName name, SmSequenceType<A> type, boolean nillable);

	/**
	 * Returns a type denoting "empty-sequence()".
	 */
	SmSequenceType<A> emptyType();

	/**
	 * Computes the type resulting from the application of the following axis.
	 */
	SmSequenceType<A> followingAxis(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the following-sibling axis.
	 */
	SmSequenceType<A> followingSiblingAxis(SmSequenceType<A> type);

	/**
	 * Returns the {@link AtomBridge} implementation.
	 */
	AtomBridge<A> getAtomBridge();

	/**
	 * Returns the Left-Hand-Side type in a binary type.
	 */
	SmSequenceType<A> getBinaryLHS(SmSequenceType<A> type);

	/**
	 * Returns the Right-Hand-Side type in a binary type.
	 */
	SmSequenceType<A> getBinaryRHS(SmSequenceType<A> type);

	/**
	 * Returns the name of the specified type definition.
	 * 
	 * @param type
	 *            The type definition.
	 */
	QName getName(SmSequenceType<A> type);

	/**
	 * Returns the {@link NameSource} implementation.
	 */
	NameSource getNameBridge();

	/**
	 * Returns the specified type definition given a built in type.
	 * 
	 * @param nativeType
	 *            The built in type definition.
	 */
	SmSequenceType<A> getType(SmNativeType nativeType);

	/**
	 * Returns a type definition with the specified name.
	 * 
	 * @param typeName
	 *            The name of the type definition.
	 * @return The type definition. May be <code>null</code> if the type does not exist.
	 */
	SmSequenceType<A> getType(QName typeName);

	/**
	 * Converts the specified type into an opaque handle.
	 */
	SmSequenceType<A> handle(SmSequenceType<A> sequenceType);

	/**
	 * Returns a type denoting the interleave of two types.
	 */
	SmSequenceType<A> interleave(SmSequenceType<A> one, SmSequenceType<A> two);

	/**
	 * Determines whether the specified type is an attribute node type.
	 */
	boolean isAttributeNodeType(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is a union of two types.
	 */
	boolean isChoice(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is a comment node type.
	 */
	boolean isCommentNodeType(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is a document node type.
	 */
	boolean isDocumentNodeType(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is an element node type.
	 */
	boolean isElementNodeType(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is the <code>empty</code> type.
	 */
	boolean isEmpty(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is a namespace node type.
	 */
	boolean isNamespaceNodeType(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is the <code>none</code> type.
	 */
	boolean isNone(SmSequenceType<A> type);

	/**
	 * Returns the error code for a type which must be <code>none</code>.
	 * 
	 * @param noneType
	 *            The none type.
	 * @return The error code. May be <code>null</code>.
	 */
	QName getErrorCode(SmSequenceType<A> noneType);

	/**
	 * Determines whether the specified type is a processing-instruction node type.
	 */
	boolean isProcessingInstructionNodeType(SmSequenceType<A> type);

	/**
	 * Determines whether the specified type is a text node type.
	 */
	boolean isTextNodeType(SmSequenceType<A> type);

	/**
	 * Returns a type denoting a single item.
	 */
	SmSequenceType<A> itemType();

	/**
	 * Multiplies the cardinality of the specified type by a specified cardinality.
	 */
	SmSequenceType<A> multiply(SmSequenceType<A> argument, Quantifier multiplier);

	/**
	 * Computes the type resulting from the application of the namespace axis.
	 */
	SmSequenceType<A> namespaceAxis(SmSequenceType<A> type);

	/**
	 * Applies the namespace() test to the argument.
	 */
	SmSequenceType<A> namespaceTest(SmSequenceType<A> arg);

	/**
	 * Returns a type denoting a single namespace node.
	 */
	SmSequenceType<A> namespaceType();

	/**
	 * Applies the node() test to the argument.
	 */
	SmSequenceType<A> nodeTest(SmSequenceType<A> arg);

	/**
	 * Returns a type denoting a single node.
	 */
	SmSequenceType<A> nodeType();

	/**
	 * Returns a type denoting an error.
	 */
	SmSequenceType<A> noneType();

	/**
	 * Returns a type denoting an error with an error code.
	 * 
	 * @param errorCode
	 *            The error code. May be <code>null</code>.
	 */
	SmSequenceType<A> noneType(QName errorCode);

	/**
	 * Multiply the cardinality of the specified type by optional (+).
	 */
	SmSequenceType<A> oneOrMore(SmSequenceType<A> type);

	/**
	 * Multiply the cardinality of the specified type by optional (?).
	 */
	SmSequenceType<A> optional(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the parent axis.
	 */
	SmSequenceType<A> parentAxis(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the preceding axis.
	 */
	SmSequenceType<A> precedingAxis(SmSequenceType<A> type);

	/**
	 * Computes the type resulting from the application of the preceding-sibling axis.
	 */
	SmSequenceType<A> precedingSiblingAxis(SmSequenceType<A> type);

	/**
	 * Extracts all item types from the argument type and combines them into a choice.
	 */
	SmSequenceType<A> prime(SmSequenceType<A> type);

	/**
	 * Applies the processing-instruction(name) test to the argument.
	 */
	SmSequenceType<A> processingInstructionTest(SmSequenceType<A> arg, String name);

	/**
	 * Returns a type denoting a single processing-instruction node.
	 */
	SmSequenceType<A> processingInstructionType(String name);

	/**
	 * Approximates the possible number of items in the argument type.
	 */
	Quantifier quantifier(SmSequenceType<A> type);

	boolean sameAs(SmSequenceType<A> one, SmSequenceType<A> two);

	/**
	 * Returns a type denoting "schema-attribute(attributeName)".
	 */
	SmSequenceType<A> schemaAttribute(QName attributeName);

	/**
	 * Returns a type denoting "schema-element(elementName)".
	 */
	SmSequenceType<A> schemaElement(QName elementDeclaration);

	/**
	 * Returns a type denoting "schema-type(typeName)".
	 */
	SmSequenceType<A> schemaType(QName typeName);

	/**
	 * Computes the type resulting from the application of the self axis.
	 */
	SmSequenceType<A> selfAxis(SmSequenceType<A> type);

	/**
	 * Determines whether the actual type is a sub-type of the expected type.
	 * 
	 * @param lhs
	 * @param rhs
	 */
	boolean subtype(SmSequenceType<A> lhs, SmSequenceType<A> rhs);

	/**
	 * Applies the text() test to the argument.
	 */
	SmSequenceType<A> textTest(SmSequenceType<A> arg);

	/**
	 * Returns a type denoting a single text node.
	 */
	SmSequenceType<A> textType();

	/**
	 * Computes the String representation of the specified type in SequenceType syntax.
	 * 
	 * @param type
	 *            The type to be serialized.
	 * @param mappings
	 *            A resolver from namespaces to prefixes.
	 * @param defaultElementAndTypeNamespace
	 *            The default namespace for element names.
	 */
	String toString(SmSequenceType<A> type, NamespaceResolver mappings, String defaultElementAndTypeNamespace) throws GxmlException;

	/**
	 * Allocates an empty array of types.
	 * 
	 * @param size
	 *            The size of the array of types.
	 */
	SmSequenceType<A>[] typeArray(int size);

	/**
	 * Multiply the cardinality of the specified type by optional (*).
	 */
	SmSequenceType<A> zeroOrMore(SmSequenceType<A> type);
}
