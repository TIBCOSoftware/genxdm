/*
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
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceType;

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
    void accept(SequenceType<A> type, MetaVisitor<A> visitor);

    /**
     * Computes the type resulting from the application of the ancestor axis.
     */
    SequenceType<A> ancestorAxis(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the ancestor-or-self axis.
     */
    SequenceType<A> ancestorOrSelfAxis(SequenceType<A> type);

    /**
     * Returns a type denoting fn:data(arg).
     */
    SequenceType<A> atomSet(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the attribute axis.
     */
    SequenceType<A> attributeAxis(SequenceType<A> type);

    /**
     * Returns a type denoting an attribute of the specified name and type.
     * 
     * @param name
     *            The name of the attribute. A value of <code>null</code> returns an attribute with a wildcard name.
     * @param type
     *            The type of the attribute. May be <code>null</code> for xs:anySimpleType.
     */
    SequenceType<A> attributeType(QName name, SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the child axis.
     */
    SequenceType<A> childAxis(SequenceType<A> type);

    /**
     * Returns a type denoting a choice of two types.
     */
    SequenceType<A> choice(SequenceType<A> one, SequenceType<A> two);

    /**
     * Applies the comment() test to the argument.
     */
    SequenceType<A> commentTest(SequenceType<A> arg);

    /**
     * Returns a type denoting a single comment node.
     */
    SequenceType<A> commentType();

    /**
     * Returns a type denoting the combined sequence of two types.
     */
    SequenceType<A> concat(SequenceType<A> lhs, SequenceType<A> rhs);

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
    SequenceType<A> contentType(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the descendant axis.
     */
    SequenceType<A> descendantAxis(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the descendant-or-self axis.
     */
    SequenceType<A> descendantOrSelfAxis(SequenceType<A> type);

    /**
     * Returns a type denoting a single document node with the specified content.
     */
    SequenceType<A> documentType(SequenceType<A> contentType);

    /**
     * Applies an element() test to the argument.
     */
    SequenceType<A> elementTest(SequenceType<A> arg);

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
    SequenceType<A> elementType(QName name, SequenceType<A> type, boolean nillable);

    /**
     * Returns a type denoting "empty-sequence()".
     */
    SequenceType<A> emptyType();

    /**
     * Computes the type resulting from the application of the following axis.
     */
    SequenceType<A> followingAxis(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the following-sibling axis.
     */
    SequenceType<A> followingSiblingAxis(SequenceType<A> type);

    /**
     * Returns the {@link AtomBridge} implementation.
     */
    AtomBridge<A> getAtomBridge();

    /**
     * Returns the Left-Hand-Side type in a binary type.
     */
    SequenceType<A> getBinaryLHS(SequenceType<A> type);

    /**
     * Returns the Right-Hand-Side type in a binary type.
     */
    SequenceType<A> getBinaryRHS(SequenceType<A> type);

    /**
     * Returns the name of the specified type definition.
     * 
     * @param type
     *            The type definition.
     */
    QName getName(SequenceType<A> type);

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
    SequenceType<A> getType(NativeType nativeType);

    /**
     * Returns a type definition with the specified name.
     * 
     * @param typeName
     *            The name of the type definition.
     * @return The type definition. May be <code>null</code> if the type does not exist.
     */
    SequenceType<A> getType(QName typeName);

    /**
     * Converts the specified type into an opaque handle.
     */
    SequenceType<A> handle(SequenceType<A> sequenceType);

    /**
     * Returns a type denoting the interleave of two types.
     */
    SequenceType<A> interleave(SequenceType<A> one, SequenceType<A> two);

    /**
     * Determines whether the specified type is an attribute node type.
     */
    boolean isAttributeNodeType(SequenceType<A> type);

    /**
     * Determines whether the specified type is a union of two types.
     */
    boolean isChoice(SequenceType<A> type);

    /**
     * Determines whether the specified type is a comment node type.
     */
    boolean isCommentNodeType(SequenceType<A> type);

    /**
     * Determines whether the specified type is a document node type.
     */
    boolean isDocumentNodeType(SequenceType<A> type);

    /**
     * Determines whether the specified type is an element node type.
     */
    boolean isElementNodeType(SequenceType<A> type);

    /**
     * Determines whether the specified type is the <code>empty</code> type.
     */
    boolean isEmpty(SequenceType<A> type);

    /**
     * Determines whether the specified type is a namespace node type.
     */
    boolean isNamespaceNodeType(SequenceType<A> type);

    /**
     * Determines whether the specified type is the <code>none</code> type.
     */
    boolean isNone(SequenceType<A> type);

    /**
     * Returns the error code for a type which must be <code>none</code>.
     * 
     * @param noneType
     *            The none type.
     * @return The error code. May be <code>null</code>.
     */
    QName getErrorCode(SequenceType<A> noneType);

    /**
     * Determines whether the specified type is a processing-instruction node type.
     */
    boolean isProcessingInstructionNodeType(SequenceType<A> type);

    /**
     * Determines whether the specified type is a text node type.
     */
    boolean isTextNodeType(SequenceType<A> type);

    /**
     * Returns a type denoting a single item.
     */
    SequenceType<A> itemType();

    /**
     * Multiplies the cardinality of the specified type by a specified cardinality.
     */
    SequenceType<A> multiply(SequenceType<A> argument, Quantifier multiplier);

    /**
     * Computes the type resulting from the application of the namespace axis.
     */
    SequenceType<A> namespaceAxis(SequenceType<A> type);

    /**
     * Applies the namespace() test to the argument.
     */
    SequenceType<A> namespaceTest(SequenceType<A> arg);

    /**
     * Returns a type denoting a single namespace node.
     */
    SequenceType<A> namespaceType();

    /**
     * Applies the node() test to the argument.
     */
    SequenceType<A> nodeTest(SequenceType<A> arg);

    /**
     * Returns a type denoting a single node.
     */
    SequenceType<A> nodeType();

    /**
     * Returns a type denoting an error.
     */
    SequenceType<A> noneType();

    /**
     * Returns a type denoting an error with an error code.
     * 
     * @param errorCode
     *            The error code. May be <code>null</code>.
     */
    SequenceType<A> noneType(QName errorCode);

    /**
     * Multiply the cardinality of the specified type by optional (+).
     */
    SequenceType<A> oneOrMore(SequenceType<A> type);

    /**
     * Multiply the cardinality of the specified type by optional (?).
     */
    SequenceType<A> optional(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the parent axis.
     */
    SequenceType<A> parentAxis(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the preceding axis.
     */
    SequenceType<A> precedingAxis(SequenceType<A> type);

    /**
     * Computes the type resulting from the application of the preceding-sibling axis.
     */
    SequenceType<A> precedingSiblingAxis(SequenceType<A> type);

    /**
     * Extracts all item types from the argument type and combines them into a choice.
     */
    SequenceType<A> prime(SequenceType<A> type);

    /**
     * Applies the processing-instruction(name) test to the argument.
     */
    SequenceType<A> processingInstructionTest(SequenceType<A> arg, String name);

    /**
     * Returns a type denoting a single processing-instruction node.
     */
    SequenceType<A> processingInstructionType(String name);

    /**
     * Approximates the possible number of items in the argument type.
     */
    Quantifier quantifier(SequenceType<A> type);

    boolean sameAs(SequenceType<A> one, SequenceType<A> two);

    /**
     * Returns a type denoting "schema-attribute(attributeName)".
     */
    SequenceType<A> schemaAttribute(QName attributeName);

    /**
     * Returns a type denoting "schema-element(elementName)".
     */
    SequenceType<A> schemaElement(QName elementDeclaration);

    /**
     * Returns a type denoting "schema-type(typeName)".
     */
    SequenceType<A> schemaType(QName typeName);

    /**
     * Computes the type resulting from the application of the self axis.
     */
    SequenceType<A> selfAxis(SequenceType<A> type);

    /**
     * Determines whether the actual type is a sub-type of the expected type.
     * 
     * @param lhs
     * @param rhs
     */
    boolean subtype(SequenceType<A> lhs, SequenceType<A> rhs);

    /**
     * Applies the text() test to the argument.
     */
    SequenceType<A> textTest(SequenceType<A> arg);

    /**
     * Returns a type denoting a single text node.
     */
    SequenceType<A> textType();

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
    String toString(SequenceType<A> type, NamespaceResolver mappings, String defaultElementAndTypeNamespace) throws GxmlException;

    /**
     * Allocates an empty array of types.
     * 
     * @param size
     *            The size of the array of types.
     */
    SequenceType<A>[] typeArray(int size);

    /**
     * Multiply the cardinality of the specified type by optional (*).
     */
    SequenceType<A> zeroOrMore(SequenceType<A> type);
}
