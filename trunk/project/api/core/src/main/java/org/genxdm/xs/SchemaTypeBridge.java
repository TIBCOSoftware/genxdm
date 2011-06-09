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
package org.genxdm.xs;

import javax.xml.namespace.QName;

import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.CommentNodeType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.TextNodeType;

/* TODO: turn this into javadoc, if you can figure out wtf this
 * interface is.  it clearly adds something. 
 * 
 * Methods from the extended interface allow registry and
 * interrogation of component kinds.
 * 
 * The bridge also defines methods that allow the creation, on the
 * fly, of types from other types, such as setting cardinality,
 * allowing unions or choices.  NodeType support appears here only,
 * apparently (but it isn't clear what the relationship is between
 * ElementNodeType and ElementDefinition, for example).
 * 
 */
public interface SchemaTypeBridge extends Schema
{
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
     * Returns a type denoting a single comment node.
     */
    CommentNodeType commentType();

    /**
     * Returns a type denoting the combined sequence of two types.
     */
    SequenceType concat(SequenceType one, SequenceType two);

    /**
     * Returns a type denoting a single document node with the specified content.
     */
    DocumentNodeType documentType(SequenceType contextType);

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
     * Returns the name of the specified type definition.
     * 
     * @param type
     *            The type definition.
     */
    QName getName(SequenceType type);

    /**
     * Returns a type denoting the interleave of two types.
     */
    SequenceType interleave(SequenceType one, SequenceType two);

    /**
     * Returns a type denoting a single item.
     */
    PrimeType itemType();

    /**
     * Multiplies the cardinality of the specified type by a specified cardinality.
     */
    SequenceType multiply(SequenceType argument, Quantifier multiplier);

    /**
     * Returns a type denoting a single namespace node.
     */
    NamespaceNodeType namespaceType();

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
     * Returns a type denoting a single processing-instruction node.
     */
    ProcessingInstructionNodeType processingInstructionType(String name);

    boolean sameAs(SequenceType one, SequenceType two);

    /**
     * Determines whether the actual type is a sub-type of the expected type.
     * 
     * @param lhs
     * @param rhs
     */
    boolean subtype(SequenceType lhs, SequenceType rhs);

    /**
     * Returns a type denoting a single text node.
     */
    TextNodeType textType();

    SequenceType zeroOrMore(SequenceType type);
}
