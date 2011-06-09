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
import org.genxdm.names.NamespaceResolver;
import org.genxdm.xs.SchemaTypeBridge;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.TextNodeType;

/**
 * A processing context parameterized by atom(s) and type(s).
 */
public interface MetaBridge extends SchemaTypeBridge
{
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
     * Applies the comment() test to the argument.
     */
    SequenceType commentTest(SequenceType arg);

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
     * Applies an element() test to the argument.
     */
    SequenceType elementTest(SequenceType arg);

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
     * Returns the specified type definition given a built in type.
     * 
     * @param nativeType
     *            The built in type definition.
     */
    SequenceType getType(NativeType nativeType);

    /**
     * Returns a type definition with the specified name.
     * 
     * @param typeName
     *            The name of the type definition.
     * @return The type definition. May be <code>null</code> if the type does not exist.
     */
    SequenceType getType(QName typeName);

    /**
     * Converts the specified type into an opaque handle.
     */
    SequenceType handle(SequenceType sequenceType);

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
     * Returns the error code for a type which must be <code>none</code>.
     * 
     * @param noneType
     *            The none type.
     * @return The error code. May be <code>null</code>.
     */
    QName getErrorCode(SequenceType noneType);

    /**
     * Determines whether the specified type is a processing-instruction node type.
     */
    boolean isProcessingInstructionNodeType(SequenceType type);

    /**
     * Determines whether the specified type is a text node type.
     */
    boolean isTextNodeType(SequenceType type);

    /**
     * Computes the type resulting from the application of the namespace axis.
     */
    SequenceType namespaceAxis(SequenceType type);

    /**
     * Applies the namespace() test to the argument.
     */
    SequenceType namespaceTest(SequenceType arg);

    /**
     * Applies the node() test to the argument.
     */
    SequenceType nodeTest(SequenceType arg);

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
     * Approximates the possible number of items in the argument type.
     */
    Quantifier quantifier(SequenceType type);

    /**
     * Returns a type denoting "schema-attribute(attributeName)".
     */
    SequenceType schemaAttribute(QName attributeName);

    /**
     * Returns a type denoting "schema-element(elementName)".
     */
    SequenceType schemaElement(QName elementDeclaration);

    /**
     * Returns a type denoting "schema-type(typeName)".
     */
    SequenceType schemaType(QName typeName);

    /**
     * Computes the type resulting from the application of the self axis.
     */
    SequenceType selfAxis(SequenceType type);

    /**
     * Applies the text() test to the argument.
     */
    SequenceType textTest(SequenceType arg);

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
    String toString(SequenceType type, NamespaceResolver mappings, String defaultElementAndTypeNamespace) throws GxmlException;

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
