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

import org.genxdm.xs.types.SequenceType;

/**
 * Visitor pattern for types.
 * 
 */
public interface MetaVisitor
{
    void atomicType(SequenceType node, QName name, SequenceType baseType);

    void atomicUrType(SequenceType node);

    void attributeType(SequenceType node, QName name, SequenceType type);

    void choiceType(SequenceType node, SequenceType lhs, SequenceType rhs);

    void commentType(SequenceType node);

    void complexType(SequenceType node, QName name, SequenceType baseType);

    void complexUrType(SequenceType node);

    void concatType(SequenceType node, SequenceType lhs, SequenceType rhs);

    void documentType(SequenceType node, SequenceType contentType);

    void elementType(SequenceType node, QName name, SequenceType type, boolean nillable);

    void emptyType(SequenceType node);

    void interleaveType(SequenceType node, SequenceType lhs, SequenceType rhs);

    void multiplyType(SequenceType node, SequenceType argument, Quantifier multiplier);

    void namespaceType(SequenceType node);

    void noneType(SequenceType node);

    void processingInstructionType(SequenceType node, String name);

    void schemaAttributeType(SequenceType node, QName name);

    void schemaElementType(SequenceType type, QName name);

    void simpleUrType(SequenceType node);

    void textType(SequenceType node);
}
