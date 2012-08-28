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

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.names.NamespaceResolver;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.CommentNodeType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.TextNodeType;

/**
 * A processing context parameterized by atom(s) and type(s).
 */
public interface TypesBridge
{
    // TODO: push down accept to SequenceType (removing the first param).
    // see impl in TypesBridgeImpl
    /**
     * Implementation of the visitor pattern.
     * 
     * @param type
     *            The type that accepts the visitor.
     * @param visitor
     *            The visitor.
     */
    void accept(SequenceType type, MetaVisitor visitor);

    /**
     * Computes the type resulting from the application of the ancestor axis.
     */
    SequenceType ancestorAxis(SequenceType type);

    /**
     * Computes the type resulting from the application of the ancestor-or-self axis.
     */
    SequenceType ancestorOrSelfAxis(SequenceType type);

    /**
     * Returns a type denoting fn:data(arg).
     */
    SequenceType atomSet(SequenceType type);

    /**
     * Computes the type resulting from the application of the attribute axis.
     */
    SequenceType attributeAxis(SequenceType contextType);

    /**
     * Returns a type denoting an attribute of the specified name and type.
     * 
     * @param name
     *            The name of the attribute. A value of <code>null</code> returns an attribute with a wildcard name.
     * @param type
     *            The type of the attribute. May be <code>null</code> for xs:anySimpleType.
     */
    AttributeNodeType attributeType(QName name, SequenceType type);

    AttributeNodeType attributeWild(SequenceType type);

    /**
     * Computes the type resulting from the application of the child axis.
     */
    SequenceType childAxis(SequenceType contextType);

    /**
     * Returns a type denoting a choice of two types.
     */
    SequenceType choice(SequenceType one, SequenceType two);

    /**
     * Applies the comment() test to the argument.
     */
    SequenceType commentTest(SequenceType arg);

    /**
     * Returns a type denoting a single comment node.
     */
    CommentNodeType commentType();

    /**
     * Returns a type denoting the combined sequence of two types.
     */
    SequenceType concat(SequenceType one, SequenceType two);

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
    SequenceType contentType(SequenceType type);

    /**
     * Computes the type resulting from the application of the descendant axis.
     */
    SequenceType descendantAxis(SequenceType type);

    /**
     * Computes the type resulting from the application of the descendant-or-self axis.
     */
    SequenceType descendantOrSelfAxis(SequenceType type);

    /**
     * Returns a type denoting a single document node with the specified content.
     */
    DocumentNodeType documentType(SequenceType contextType);

    /**
     * Applies an element() test to the argument.
     */
    SequenceType elementTest(SequenceType arg);

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
    ElementNodeType elementType(QName name, SequenceType type, boolean nillable);

    ElementNodeType elementWild(SequenceType type, boolean nillable);

    /**
     * Returns a type denoting "empty-sequence()".
     */
    EmptyType emptyType();

    /**
     * Computes the type resulting from the application of the following axis.
     */
    SequenceType followingAxis(SequenceType type);

    /**
     * Computes the type resulting from the application of the following-sibling axis.
     */
    SequenceType followingSiblingAxis(SequenceType type);

    /**
     * Returns the Left-Hand-Side type in a binary type.
     */
    SequenceType getBinaryLHS(SequenceType type);

    /**
     * Returns the Right-Hand-Side type in a binary type.
     */
    SequenceType getBinaryRHS(SequenceType type);

    /**
     * Returns the error code for a type which must be <code>none</code>.
     * 
     * @param noneType
     *            The none type.
     * @return The error code. May be <code>null</code>.
     */
    QName getErrorCode(SequenceType noneType);

    /**
     * Returns the name of the specified type definition.
     * 
     * @param type
     *            The type definition.
     */
    QName getName(SequenceType type);

    /**
     * Converts the specified type into an opaque handle.
     */
    SequenceType handle(SequenceType sequenceType);

    /**
     * Returns a type denoting the interleave of two types.
     */
    SequenceType interleave(SequenceType one, SequenceType two);

    /**
     * Determines whether the specified type is an attribute node type.
     */
    boolean isAttributeNodeType(SequenceType type);

    /**
     * Determines whether the specified type is a union of two types.
     */
    boolean isChoice(SequenceType type);

    /**
     * Determines whether the specified type is a comment node type.
     */
    boolean isCommentNodeType(SequenceType type);

    /**
     * Determines whether the specified type is a document node type.
     */
    boolean isDocumentNodeType(SequenceType type);

    /**
     * Determines whether the specified type is an element node type.
     */
    boolean isElementNodeType(SequenceType type);

    /**
     * Determines whether the specified type is the <code>empty</code> type.
     */
    boolean isEmpty(SequenceType type);

    /**
     * Determines whether the specified type is a namespace node type.
     */
    boolean isNamespaceNodeType(SequenceType type);

    /**
     * Determines whether the specified type is the <code>none</code> type.
     */
    boolean isNone(SequenceType type);

    /**
     * Determines whether the specified type is a processing-instruction node type.
     */
    boolean isProcessingInstructionNodeType(SequenceType type);

    /**
     * Determines whether the specified type is a text node type.
     */
    boolean isTextNodeType(SequenceType type);

    /**
     * Returns a type denoting a single item.
     */
    PrimeType itemType();

    /**
     * Multiplies the cardinality of the specified type by a specified cardinality.
     */
    SequenceType multiply(SequenceType argument, Quantifier multiplier);

    /**
     * Computes the type resulting from the application of the namespace axis.
     */
    SequenceType namespaceAxis(SequenceType type);

    /**
     * Returns a type denoting a single namespace node.
     */
    NamespaceNodeType namespaceType();

    /**
     * Applies the namespace() test to the argument.
     */
    SequenceType namespaceTest(SequenceType arg);

    /**
     * Applies the node() test to the argument.
     */
    SequenceType nodeTest(SequenceType arg);

    /**
     * Returns a type denoting a single node.
     */
    PrimeType nodeType();

    /**
     * Returns a type denoting an error.
     */
    NoneType noneType();

    /**
     * Returns a type denoting an error with an error code.
     * 
     * @param errorCode
     *            The error code. May be <code>null</code>.
     */
    NoneType noneType(QName errorCode);

    /**
     * Multiply the cardinality of the specified type by optional (+).
     */
    SequenceType oneOrMore(SequenceType type);

    /**
     * Multiply the cardinality of the specified type by optional (?).
     */
    SequenceType optional(SequenceType type);

    /**
     * Computes the type resulting from the application of the parent axis.
     */
    SequenceType parentAxis(SequenceType type);

    /**
     * Computes the type resulting from the application of the preceding axis.
     */
    SequenceType precedingAxis(SequenceType type);

    /**
     * Computes the type resulting from the application of the preceding-sibling axis.
     */
    SequenceType precedingSiblingAxis(SequenceType type);

    /**
     * Extracts all item types from the argument type and combines them into a choice.
     */
    SequenceType prime(SequenceType type);

    /**
     * Applies the processing-instruction(name) test to the argument.
     */
    SequenceType processingInstructionTest(SequenceType arg, String name);

    /**
     * Returns a type denoting a single processing-instruction node.
     */
    ProcessingInstructionNodeType processingInstructionType(String name);

    /**
     * Approximates the possible number of items in the argument type.
     */
    Quantifier quantifier(SequenceType type);

    // TODO: how is this different from "equals"?
    boolean sameAs(SequenceType one, SequenceType two);

    /**
     * Computes the type resulting from the application of the self axis.
     */
    SequenceType selfAxis(SequenceType type);

    /**
     * Determines whether the actual type is a sub-type of the expected type.
     * 
     * @param lhs
     * @param rhs
     */
    boolean subtype(SequenceType lhs, SequenceType rhs);

    /**
     * Applies the text() test to the argument.
     */
    SequenceType textTest(SequenceType arg);

    /**
     * Returns a type denoting a single text node.
     */
    TextNodeType textType();

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
    String toString(SequenceType type, NamespaceResolver mappings, String defaultElementAndTypeNamespace) throws GenXDMException;

    /**
     * Allocates an empty array of types.
     * 
     * @param size
     *            The size of the array of types.
     */
    SequenceType[] typeArray(int size);

    /**
     * Multiply the cardinality of the specified type by optional (*).
     */
    SequenceType zeroOrMore(SequenceType type);
}
